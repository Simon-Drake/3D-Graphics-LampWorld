import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Anilamp_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(3f,6.5f,4f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   * 
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light1.dispose(gl);
    light2.dispose(gl);
    light3.dispose(gl);
    light4.dispose(gl);
  }

    // ***************************************************
  /* TIME
   */ 
  
  public double startTime;

  public double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

/*  ***************************************************
  * I declare that the code below this point is of my own writing. 
  * Simon Drake
  * simon.p.drake93@gmail.com
*/ 

  // ***************************************************
  /* INTERACTION
    These methods are those which are directly related to the swing interface. 
  */
  
  // Turns on or off the relevant light. 
  // Uses helper method below.
  public void switchLight(int i){
    switch(i){
      case 1:
        if(lightOne){
          turnOffLight(light1);
          lightOne = false;
        }
        else{
          turnOnLight(light1);
          lightOne = true;
        }
        break;
      case 2:
        if(lightTwo){
          turnOffLight(light2);
          lightTwo = false;
        }
        else{
          turnOnLight(light2);
          lightTwo = true;
        }
        break;
      case 3:
        if(lightThree){
          turnOffLight(light3);
          lightThree = false;
        }
        else{
          turnOnLight(light3);
          lightThree = true;
        }
        break;
      }
    }

  // Implements Random Pose
  // Determines target rotations within certain restrictions and how much the lamp will have to move. 
  public void pose(){
    tarAnglLmp = new Vec4(20 + (float)Math.random()*-70, (float)Math.random()*110, 25.0f - (float)Math.random()*50, 90.0f - (float)Math.random()*180);
    diff = new Vec4(tarAnglLmp.x - AnglLmp.x, tarAnglLmp.y - AnglLmp.y, tarAnglLmp.z - AnglLmp.z, tarAnglLmp.w - AnglLmp.w); 
    eTime = getSeconds();
    lampState = 5;
  }

  // Resets the lamp to its neutral state
  // Neutral state is not a fixed position, it involves very small changes in rotation angles
  public void reset(){

    // If already in neutral position do nothing otherwise reset. 
    // To determine target position: uses an offset to find out where the lamp will be in the time it takes to change position.
    // The offset is an approximation but the error is small enough to not be noticeable and for the animation to be fluid. 
    if(lampState == 0){
      System.out.println("Already in neutral position.");
    }
    else{
      eTime = getSeconds();
      Vec3 v = new Vec3(getAngles(1.0));
      tarAnglLmp = new Vec4(v.x, v.y, v.z, -v.z);
      diff = new Vec4(v.x - AnglLmp.x, v.y - AnglLmp.y, v.z - AnglLmp.z, -v.z - AnglLmp.w);
      lampState = 4;
    }
  }

  // Begins the jump animation. 
  // Determined target position randomly and distance between points calculated. 
  // If the distance is large the lamp will bend more then if the distance is small. 
  public void jump(){
    eTime = getSeconds();
    tarPosTbleLmp = new Vec3(-4.4f - (float)Math.random()*2.35f, 2.0f, 3.0f - (float)Math.random()*6f);
    tableDiff = new Vec2((float)tarPosTbleLmp.x - posTbleLmp.x, (float)tarPosTbleLmp.z - posTbleLmp.z);

    float dist = (float)Math.sqrt(Math.pow((tarPosTbleLmp.z - posTbleLmp.z),2) + Math.pow((tarPosTbleLmp.x - posTbleLmp.x),2));
    if(dist > 2.0f){
      tarAnglLmp = new Vec4(-70.0f, 140.0f, -80.0f, 0.0f);
    }
    else{
      tarAnglLmp = new Vec4(-50.0f, 120.0f, -60.0f, 0.0f);  
    }

    diff = new Vec4(tarAnglLmp.x - AnglLmp.x, tarAnglLmp.y - AnglLmp.y, tarAnglLmp.z - AnglLmp.z, -tarAnglLmp.w - AnglLmp.w);
    lampState = 1;
  }
   
  // ***************************************************
  /* THE SCENE
   */
   
  private SceneGraph sg;
  private Camera camera;
  private Mat4 perspective;
  private Light light1, light2, light3, light4;
  private Boolean lightOne = true, lightTwo = true, lightThree = true, reaction = false;

  private SGNode sceneRoot;  
  private TransformNode tlight, transEdge, transCenterHead, transR1, transR2, transR3, transLamp, transPen, transBGlobe, movePaper, transPW;

  private int lampState = 0;
  private float tarYGlbeRot, tarYPapRot, tarYPapWRot, tarYPenRot, yGlbeRot = 0.0f, yPapRot = 0.0f, yPapWRot = 0.0f, yPenRot = 0.0f;
  private double eTime; 
  private Vec4 tarAnglLmp, diff, AnglLmp, diffYRotDec;
  private Vec3 tarPosTbleLmp, posTbleLmp = new Vec3(-5.5f, 2.0f, 0.0f); 
  private Vec3 tarPosTblePen, posTblePen = new Vec3(-4.4f, 2.0f, 3.5f); 
  private Vec3 tarPosTblePapW, posTblePapW = new Vec3(0.0f, 0.0f, 0.0f); 
  private Vec3 tarPosTblePap, posTblePap = new Vec3(-4.4f, 2.01f, -3.5f); 
  private Vec3 tarPosTbleGlbe, posTbleGlbe = new Vec3(-6.5f, 2.0f, 3.5f); 
  private Vec2 tableDiff, difCurTarGlbe, difCurTarPapW, difCurTarPen, difCurTarPap;


  // Initialise the scene graph, models and other objects and variables.
  // Implemented in SceneGraph.java
  private void initialise(GL3 gl) {
    
    sg = new SceneGraph(gl, camera);
    light1 = sg.getLight1();
    light2 = sg.getLight2();
    light3 = sg.getLight3();
    light4 = sg.getLight4();
    tlight = sg.getTlight();
    transEdge = sg.getTransEdge();
    transCenterHead = sg.getTransCenterHead();
    transR1 = sg.getTransR1();
    transR2 = sg.getTransR2();
    transR3 = sg.getTransR3();
    transLamp = sg.getTransLamp();
    transPen = sg.getTransPen();
    transBGlobe = sg.getTransBGlobe();
    movePaper = sg.getMovePaper();
    transPW = sg.getTransPW();
    sceneRoot = sg.getSceneRoot();

    // Print off scene graph?
    //sceneRoot.print(0, false);
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    update();
    sceneRoot.draw(gl);
  }

  // Changes the state of the scene for each frame. 
  private void update() {

    // Changes one of the world lights with each frame
    // Rotates in circles above the scene
    Vec3 lpos = new Vec3(getLightPosition());
    tlight.setTransform(Mat4Transform.translate(lpos));
    light1.setPosition(lpos);

    // Updates light positions
    updateLightPosition(light3, transEdge.children.get(0).getwt().toString());
    updateLightPosition(light4, transCenterHead.children.get(0).getwt().toString());
    
    // Depending on the state of the scene (largely decided by the state of the lamp); do something.
    // States are explained in readme.txt
    switch(lampState){

      // Neutral state, small changes in rotations. Makes the lamp look alive.
      case 0:
        Vec3 v = new Vec3(getAngles(0.0));
        transR1.setTransform(Mat4Transform.rotateAroundX(v.x));
        transR2.setTransform(Mat4Transform.rotateAroundX(v.y));
        Mat4 r3 = Mat4.multiply(Mat4Transform.rotateAroundX(v.z), Mat4Transform.rotateAroundY(-v.z));
        transR3.setTransform(r3);
        AnglLmp = new Vec4(v.x, v.y, v.z, -v.z);
        break;
      case 1:
      case 4:
      case 5: 
        transition(1.0f);
        break;
      case 2: 
        tarAnglLmp = new Vec4(0.0f, 0.0f, 0.0f, 0.0f);
        diff = new Vec4(tarAnglLmp.x - AnglLmp.x, tarAnglLmp.y - AnglLmp.y, tarAnglLmp.z - AnglLmp.z, -tarAnglLmp.w - AnglLmp.w);
        transition(2.0f);
        break;
      case 3:
        transitionJump();
    }

    //Update scene graph. 
    sceneRoot.update(); 
  }

  // A change between two positions and is implemented here
  // Takes a parameter to determine how fast the transition is
  private void transition(float w){

    float t = (float)(getSeconds() - eTime);
    transR1.setTransform(Mat4Transform.rotateAroundX(AnglLmp.x + diff.x*t*w));   
    transR2.setTransform(Mat4Transform.rotateAroundX(AnglLmp.y + diff.y*t*w));
    transR3.setTransform(Mat4.multiply(Mat4Transform.rotateAroundX(AnglLmp.z + diff.z*t*w), Mat4Transform.rotateAroundY(AnglLmp.w + diff.w*t*w)));

    // Depending on the state of the lamp, move to the next state.
    if(t > 1.0/w){
      switch (lampState){
        case 5: 
          lampState = 6;
          AnglLmp = tarAnglLmp; 
          break;
        case 4: 
          lampState = 0;
          break;
        case 1: 
          AnglLmp = tarAnglLmp;
          eTime = getSeconds();
          lampState = 2;
          break;
        case 2:
          AnglLmp = tarAnglLmp;
          eTime = getSeconds();
          lampState = 3;
          break;
      }
    }
    
    // When a jump lands update the scene accordingly. 
    if(reaction){
      reactionJump();
    }
  }

  // Implements updates for when the lamp is in the air. 
  private void transitionJump(){

    float t = (float)(getSeconds() - eTime);
    transLamp.setTransform(Mat4Transform.translate(posTbleLmp.x+tableDiff.x*t, posTbleLmp.y + (float)Math.sin(t*Math.PI), posTbleLmp.z + tableDiff.y*t));

    // Once the jump has landed update accordingly. 
    if(t > 1.0){ 

      // Updates transformation for scene graph. 
      transLamp.setTransform(Mat4Transform.translate(tarPosTbleLmp));
      posTbleLmp = tarPosTbleLmp;

      // Defines the parameters needed to reset the lamp. 
      Vec3 st = new Vec3(getAngles(1.0));
      tarAnglLmp = new Vec4(st.x, st.y, st.z, -st.z);
      diff = new Vec4(st.x - AnglLmp.x, st.y - AnglLmp.y, st.z - AnglLmp.z, -st.z - AnglLmp.w);
      lampState = 4;

      // Defines the parameters needed for each objects on the table. 
      reaction = true;
      eTime = getSeconds();
      tarPosTblePapW = new Vec3( 0.0f - 0.2f*(float)Math.random(), 0.0f, 0.0f - 0.2f*(float)Math.random());
      tarPosTblePap = new Vec3( -4.6f - 0.2f*(float)Math.random(), 2.01f, -3.7f - 0.2f*(float)Math.random());
      tarPosTblePen = new Vec3( -4.6f - 0.4f*(float)Math.random(), 2.0f, 3.7f - 0.4f*(float)Math.random());
      tarPosTbleGlbe = new Vec3( -6.7f - 0.4f*(float)Math.random(), 2.0f, 3.7f - 0.4f*(float)Math.random());

      difCurTarPen = new Vec2(tarPosTblePen.x - posTblePen.x, tarPosTblePen.z - posTblePen.z);
      difCurTarPap = new Vec2(tarPosTblePap.x - posTblePap.x, tarPosTblePap.z - posTblePap.z);
      difCurTarPapW = new Vec2(tarPosTblePapW.x - posTblePapW.x, tarPosTblePapW.z - posTblePapW.z);
      difCurTarGlbe = new Vec2(tarPosTbleGlbe.x - posTbleGlbe.x, tarPosTbleGlbe.z - posTbleGlbe.z);

      tarYGlbeRot = yGlbeRot + 10.0f - 20*(float)Math.random();
      tarYPapRot = yPapRot + 10.0f - 20*(float)Math.random();
      tarYPapWRot = yPapWRot + 10.0f - 20*(float)Math.random();
      tarYPenRot = yPenRot + 10.0f - 20*(float)Math.random();

      diffYRotDec = new Vec4(tarYGlbeRot - yGlbeRot, tarYPenRot - yPenRot, tarYPapRot - yPapRot, tarYPapWRot - yPapWRot);
    }
  }

  // Defines the transitions for the effects of the jump on the objects on the table. 
  private void reactionJump(){

    float t = (float)(getSeconds() - eTime);

    transBGlobe.setTransform(Mat4.multiply(Mat4Transform.translate(posTbleGlbe.x + difCurTarGlbe.x*t, posTbleGlbe.y + (float)Math.sin(t*Math.PI)/6, posTbleGlbe.z + difCurTarGlbe.y*t), Mat4Transform.rotateAroundY(yGlbeRot + diffYRotDec.x*t))); 
    transPen.setTransform(Mat4.multiply(Mat4Transform.translate(posTblePen.x + difCurTarPen.x*t, posTblePen.y + (float)Math.sin(t*Math.PI)/6, posTblePen.z + difCurTarPen.y*t), Mat4Transform.rotateAroundY(yPenRot + diffYRotDec.y*t)));
    transPW.setTransform(Mat4.multiply(Mat4Transform.translate(posTblePapW.x + difCurTarPapW.x*t, posTblePapW.y + (float)Math.sin(t*Math.PI)/6, posTblePapW.z + difCurTarPapW.y*t), Mat4Transform.rotateAroundY(yPapWRot + diffYRotDec.w*t)));
    movePaper.setTransform(Mat4.multiply(Mat4Transform.translate(posTblePap.x + difCurTarPap.x*t, posTblePap.y + (float)Math.sin(t*Math.PI)/8, posTblePap.z + difCurTarPap.y*t), Mat4Transform.rotateAroundY(yPapRot + diffYRotDec.z*t)));

    if(t > 1.0){ 
      posTbleGlbe = tarPosTbleGlbe;
      yGlbeRot = tarYGlbeRot;
      transBGlobe.setTransform(Mat4.multiply(Mat4Transform.translate(posTbleGlbe), Mat4Transform.rotateAroundY(tarYGlbeRot)));
      posTblePen = tarPosTblePen;
      yPenRot = tarYPenRot;
      transPen.setTransform(Mat4.multiply(Mat4Transform.translate(posTblePen), Mat4Transform.rotateAroundY(tarYPenRot)));
      posTblePapW = tarPosTblePapW;
      yPapWRot = tarYPapWRot;
      transPW.setTransform(Mat4.multiply(Mat4Transform.translate(posTblePapW), Mat4Transform.rotateAroundY(tarYPapWRot)));
      posTblePap = tarPosTblePap;
      yPapRot = tarYPapRot;
      movePaper.setTransform(Mat4.multiply(Mat4Transform.translate(posTblePap), Mat4Transform.rotateAroundY(tarYPenRot)));
      reaction = false;
    }
  }

  // Updates the position of light1. 
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 7.0f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
  }

  // Gets the angles for the rotations of the lamp. 
  // Implements an offset to get angles where the lamp will be after a certain amount of time has elapsed. 
  private Vec3 getAngles(double offset){
    double elapsedTime = getSeconds() - startTime + offset;
    float x = -30.0f + -10.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 75.0f + 10.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float z =  -20 + 10.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
  }

  // Turns off light. 
  public void turnOffLight(Light l){
    l.getMaterial().setSpecular(0.0f, 0.0f, 0.0f);
    l.getMaterial().setDiffuse(0.0f, 0.0f, 0.0f);
  }

  // Turns on light. 
  public void turnOnLight(Light l){
    l.getMaterial().setSpecular(0.8f, 0.8f, 0.8f);
    l.getMaterial().setDiffuse(0.8f, 0.8f, 0.8f);
  }

  // Updates the light positions. 
  private void updateLightPosition(Light l, String s){
    String[] pos = s.replace("}", "").replace("{", "").split(",");
    Float x = Float.valueOf(pos[3]);
    Float y = Float.valueOf(pos[7]);
    Float z = Float.valueOf(pos[11]);
    l.setPosition(x,y,z);
  }
}