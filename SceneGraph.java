/*  ***************************************************
  * I declare that the code below this point is of my own writing. 
  * Simon Drake
  * spdrake2@sheffield.ac.uk
*/ 

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

// Class used to initialise the models, lights and implement the scene graph. 
public class SceneGraph{

    private SGNode sceneRoot;  
    private float r1Start;
    private float r2Start;
    private float r3Start;
    private Model floor, oak, baseHat, shinySteel, letter, fountain, baseGlbe, pen, window, wall, shinySteelSphr, ivorySphere, topHat;
    private Light light1, light2, light3, light4;
    private TransformNode tlight, transEdge, transCenterHead, transR1, transR2, transR3, transLamp, transPen, transBGlobe, movePaper, transPW;

    public SceneGraph(GL3 gl, Camera camera){

        int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/carpet.jpg");
        int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/oak.jpg");
        int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/steel.jpg");
        int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/letter.jpg");
        int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/water.jpg");
        int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
        int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/brown.jpg");
        int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/ivory.jpg");
        int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/garden.jpg");
        int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/garden_sunset.jpg");
        int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/garden_night.jpg");


        // Overhead light
        light1 = new Light(gl);
        light1.setCamera(camera);

        // Wall light
        light2 = new Light(gl);
        light2.setPosition(-6.0f, 5.0f, 6.0f);
        light2.setCamera(camera);

        // Lamp light
        light3 = new Light(gl);
        light3.setCamera(camera);

        // Light used simply for its position, doesn't need colour. 
        light4 = new Light(gl);
        light4.setCamera(camera);
        light4.getMaterial().setSpecular(0.0f, 0.0f, 0.0f);
        light4.getMaterial().setDiffuse(0.0f, 0.0f, 0.0f);

        // TwoTriangle models
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "vs.txt", "fs.txt");
        Material material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 1.0f);
        Mat4 modelMatrix = new Mat4(1);
        floor = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId0);
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
        letter = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId3);
        material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.4f, 0.4f), 32.0f);
        baseHat = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId1);
        window = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId8, textureId9, textureId10);
        
        // Sphere models
        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        pen = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh);
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 52.0f);
        shinySteel = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId2);
        ivorySphere = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId7);

        // Seperate shader for updating tex coords
        shader = new Shader(gl, "vs_water.txt", "fs_water.txt");
        fountain = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId4);

        // Cube models
        shader = new Shader(gl, "vs.txt", "fs.txt");
        mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.4f, 0.4f), 32.0f);
        baseGlbe = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId5);
        oak = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId1); 
        wall = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId6);
        shinySteelSphr = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId2);
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 52.0f);
        topHat = new Model(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId2);

        // Static Transformation nodes that need not be modified or accessed
        TransformNode trans1 = new TransformNode("translate(-7.375f,0.0f,4.125f)", Mat4Transform.translate(-7.375f,0.0f,4.125f));
        TransformNode trans2 = new TransformNode("translate(-7.375f,0.0f,-4.125f)", Mat4Transform.translate(-7.375f,0.0f,-4.125f));
        TransformNode trans3 = new TransformNode("translate(-4.125f,0.0f,-4.125f)", Mat4Transform.translate(-4.125f,0.0f,-4.125f));
        TransformNode trans4 = new TransformNode("translate(-4.125f,0.0f, 4.125f)", Mat4Transform.translate(-4.125f,0.0f, 4.125f));
        TransformNode transTT = new TransformNode("translate(-5.875f, 1.8f, 0.0f)", Mat4Transform.translate(-5.875f, 1.8f, 0.0f));
        TransformNode transLW = new TransformNode("translate(-7.75f, 0.0f, -5f)", Mat4Transform.translate(-7.75f, 0.0f, -5f));
        TransformNode transMBW = new TransformNode("translate(-7.75f, 0.0f, 0.0f)", Mat4Transform.translate(-7.75f, 0.0f, 0.0f));
        TransformNode transMTW = new TransformNode("translate(-7.75f, 5.0f, 0.0f)", Mat4Transform.translate(-7.75f, 5.0f, 0.0f));
        TransformNode transRW = new TransformNode("translate(-7.75f, 0.0f, 5f)", Mat4Transform.translate(-7.75f, 0.0f, 5f));
        TransformNode transPenT = new TransformNode("translate(-0.135f, 0.02f, 0.0f)", Mat4Transform.translate(-0.135f, 0.02f, 0.0f));
        TransformNode transGlobe = new TransformNode("translate(0.0f, 0.5f, 0.0f)", Mat4Transform.translate(0.0f, 0.25f, 0.0f));
        TransformNode transLampTB = new TransformNode("translate(0.0f, 0.05f, 0.0f)", Mat4Transform.translate(0.0f, 0.05f, 0.0f)); 
        TransformNode transLampTTS = new TransformNode("translate(0.0f, 0.7f, 0.0f)", Mat4Transform.translate(0.0f, 0.7f, 0.0f)); 
        TransformNode transLampTLS = new TransformNode("translate(0.0f, 0.7f, 0.0f)", Mat4Transform.translate(0.0f, 0.7f, 0.0f));
        TransformNode transLampTC = new TransformNode("translate(0.0f, 0.08f, 0.0f)", Mat4Transform.translate(0.0f, 0.08f, 0.0f)); 
        TransformNode transLampTH = new TransformNode("translate(0.0f, 0.165f, 0.0f)", Mat4Transform.translate(0.0f, 0.165f, 0.0f)); 
        TransformNode translampb = new TransformNode("translate(0.0f, -0.5f, 0.0f)", Mat4Transform.translate(0.0f, -0.5f, 0.0f));
        TransformNode trlight1 = new TransformNode("scale(0.2f,0.2f,0.2f),translate(0.0f, 10.0f, 0.0f)", Mat4.multiply(Mat4Transform.scale(0.2f,0.2f,0.2f), Mat4Transform.translate(0.0f, 10.0f, 0.0f)));
        TransformNode makeWndw = new TransformNode("scale(2.5f,1.0f,4.0f)", Mat4Transform.scale(4.0f,1.0f,2.5f));
        TransformNode makePaper = new TransformNode("scale(0.45f,1.0f,0.7f); translate(0.0f,0.01f,0.0f)", Mat4Transform.scale(0.45f,1.0f,0.7f));
        TransformNode tfloor = new TransformNode("scale(16,1f,16)", Mat4Transform.scale(16,1f,16));


        Mat4 m = Mat4Transform.scale(0.25f,1.8f,0.25f);
        Mat4 u = Mat4Transform.translate(0.0f,0.5f,0.0f);
        m = Mat4.multiply(m, u);
        TransformNode makeLeg1 = new TransformNode("scale(0.25f,1.8f,0.25f); translate(0.0f,0.5f,0.0f)", m);
        TransformNode makeLeg2 = new TransformNode("scale(0.25f,1.8f,0.25f); translate(0.0f,0.5f,0.0f)", m);
        TransformNode makeLeg3 = new TransformNode("scale(0.25f,1.8f,0.25f); translate(0.0f,0.5f,0.0f)", m);
        TransformNode makeLeg4 = new TransformNode("scale(0.25f,1.8f,0.25f); translate(0.0f,0.5f,0.0f)", m);
        TransformNode tlight3 = new TransformNode("scale(0.05f,0.05f,0.05f); translate(0.0f,0.5f,0.0f)", Mat4.multiply(Mat4Transform.scale(0.05f,0.05f,0.05f), u)); 
        TransformNode tlightC = new TransformNode("scale(0.01f,0.01f,0.01f); translate(0.0f,0.5f,0.0f)", Mat4.multiply(Mat4Transform.scale(0.01f,0.01f,0.01f), u)); 
        m = Mat4.multiply(Mat4Transform.scale(3.75f,0.2f,8.5f), u);
        TransformNode makeTT = new TransformNode("scale(3.75f,0.2f,8.5f; translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.5f,6.0f,6.0f), u);
        TransformNode makeLW = new TransformNode("scale(0.5f,6.0f,6.0f); translate(0.0f,0.5f,0.0f)", m);
        TransformNode makeRW = new TransformNode("scale(0.5f,6.0f,6.0f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.5f,2.5f,4.0f), u);
        TransformNode makeMBW = new TransformNode("scale(0.5f,2.5f,4.0f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.5f,1.0f,4.0f), u);
        TransformNode makeMTW = new TransformNode("scale(0.5f,1.0f,4.0f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.25f,0.25f,0.25f), u);
        TransformNode makePW = new TransformNode("scale(0.25f,0.25f,0.25f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.25f,0.04f,0.03f), u);
        TransformNode makePenS = new TransformNode("scale(0.25f,0.04f,0.03f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.03f,0.01f,0.01f), u);
        TransformNode makePenT = new TransformNode("scale(0.03f,0.01f,0.01f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.25f,0.25f,0.25f), u);
        TransformNode makebglobe = new TransformNode("scale(0.25f,0.25f,0.25f); translate(0.0f,0.5f,0.0f)", m);
        TransformNode makeglobe = new TransformNode("translate(0.0f,0.5f,0.0f)", u);
        m = Mat4.multiply(Mat4Transform.scale(0.3f,0.05f,0.4f), u);
        TransformNode makebase = new TransformNode("scale(0.3f,0.05f,0.4f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.1f,0.7f,0.1f), u);
        TransformNode makeLampLS = new TransformNode("scale(0.1f,0.7f,0.1f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.08f,0.08f,0.08f), u);
        TransformNode makeLampC = new TransformNode("scale(0.08f,0.08f,0.08f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.1f,0.7f,0.1f), u);
        TransformNode makeLampTS = new TransformNode("scale(0.1f,0.7f,0.1f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.25f,0.16f,0.4f), u);
        TransformNode makeLHead = new TransformNode("scale(0.25f,0.16f,0.4f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.15f,0.1f,0.2f), u);
        TransformNode makeCHat = new TransformNode("scale(0.15f,0.1f,0.2f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.45f,1.0f,0.45f), u);
        TransformNode makeBHat = new TransformNode("scale(0.45f,1.0f,0.45f); translate(0.0f,0.5f,0.0f)", m);
        m = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), u);
        TransformNode tlight2 = new TransformNode("scale(0.3f,0.3f,0.3f); translate(-6.0f, 5.5f, 6.0f)", Mat4.multiply(Mat4Transform.translate(-6.0f, 5.0f, 6.0f), m)); 
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), Mat4Transform.rotateAroundY(90));
        TransformNode placeWall = new TransformNode("scale(16,1f,16)", Mat4.multiply(Mat4Transform.translate(-7.5f, 3.75f, 0f), m));

        // Transformation nodes that are subject to change 
        transLamp = new TransformNode("translate(-5.5f, 2.0f, 0.0f)", Mat4Transform.translate(-5.5f, 2.0f, 0.0f)); 
        transBGlobe = new TransformNode("translate(-6.5f, 2.0f, 3.5f)", Mat4Transform.translate(-6.5f, 2.0f, 3.5f));
        transPW = new TransformNode("translate(-4.4f, 2.0f, -3.5f)", Mat4Transform.translate(0.0f, 0.0f, 0.0f));
        transPen = new TransformNode("translate(-4.4f, 2.0f, 3.5f)", Mat4Transform.translate(-4.4f, 2.0f, 3.5f));  
        transR1 = new TransformNode("transR1 changes", Mat4Transform.rotateAroundX(r1Start)); 
        transR2 = new TransformNode("transR2 changes", Mat4Transform.rotateAroundX(r2Start)); 
        transR3 = new TransformNode("transR3 changes", Mat4Transform.rotateAroundY(r3Start));
        transEdge = new TransformNode("transEdge changes", Mat4Transform.translate(0.0f, 0.075f, 0.225f)); 
        transCenterHead = new TransformNode("transCenterHead changes", Mat4Transform.translate(0.0f, 0.1f,-1000.0f)); 
        movePaper = new TransformNode("scale(0.45f,1.0f,0.7f); translate(0.0f,0.01f,0.0f)", Mat4Transform.translate(-4.4f, 2.01f, -3.5f));
        tlight = new TransformNode("translate(0.0f, 5.0f, 0.0f)", Mat4Transform.translate(0.0f, 5.0f, 0.0f));
        m = Mat4.multiply( Mat4Transform.scale(0.3f,0.3f,0.3f), u); 

    
        sceneRoot = new NameNode("scene");
        NameNode leg1 = new NameNode("leg1");
        NameNode leg2 = new NameNode("leg2");
        NameNode leg3 = new NameNode("leg3");
        NameNode leg4 = new NameNode("leg4");
        NameNode tableTop = new NameNode("tableTop");
        NameNode leftW = new NameNode("leftW");
        NameNode rightW = new NameNode("rightW");
        NameNode middleBW = new NameNode("middleBW");
        NameNode middleTW = new NameNode("middleTW");
        NameNode paperW = new NameNode("paperW");
        NameNode penS = new NameNode("Pen Stem");
        NameNode penT = new NameNode("Pen Tip");
        NameNode globe = new NameNode("Globe");
        NameNode bglobe = new NameNode("BaseGlobe");
        NameNode lamp = new NameNode("Lamp");
        NameNode lampLS = new NameNode("LampLB");
        NameNode lampCore = new NameNode("LampCore");
        NameNode lampTS = new NameNode("Lamp Top Step");
        NameNode lampHead = new NameNode("Lamp Head");
        NameNode base = new NameNode("floor");
        NameNode light_1 = new NameNode("light1");
        NameNode light_2 = new NameNode("light2");

        ModelNode cube0Node = new ModelNode("Cube(0)", oak);
        ModelNode cube1Node = new ModelNode("Cube(1)", oak);
        ModelNode cube2Node = new ModelNode("Cube(2)", oak);
        ModelNode cube3Node = new ModelNode("Cube(3)", oak);
        ModelNode cube4Node = new ModelNode("Cube(4)", oak);
        ModelNode cube5Node = new ModelNode("Cube(5)", wall);
        ModelNode cube6Node = new ModelNode("Cube(6)", wall);
        ModelNode cube7Node = new ModelNode("Cube(7)", wall);
        ModelNode cube8Node = new ModelNode("Cube(8)", wall);
        ModelNode cube9Node = new ModelNode("Cube(9)", baseGlbe);
        ModelNode cube10Node = new ModelNode("Cube(10)", shinySteelSphr);
        ModelNode cube11Node = new ModelNode("Cube(11)", shinySteelSphr);
        ModelNode cube12Node = new ModelNode("Cube(12)", topHat);
        ModelNode sphere0Node = new ModelNode("Sphere(0)", shinySteel);
        ModelNode sphere1Node = new ModelNode("Sphere(1)", pen);
        ModelNode sphere2Node = new ModelNode("Sphere(2)", shinySteel);
        ModelNode sphere3Node = new ModelNode("Sphere(3)", fountain);
        ModelNode sphere4Node = new ModelNode("Sphere(4)", ivorySphere);
        ModelNode sphere5Node = new ModelNode("Sphere(5)", shinySteel);
        ModelNode sphere6Node = new ModelNode("Sphere(6)", ivorySphere);
        ModelNode floor0Node = new ModelNode("Floor(0)", baseHat);
        ModelNode floor1Node = new ModelNode("Floor(1)", letter);
        ModelNode floor2Node = new ModelNode("Floor(2)", floor);
        ModelNode floor3Node = new ModelNode("Floor(2)", window);
        LightNode light0Node = new LightNode("Light(0)", light1);
        LightNode light1Node = new LightNode("Light(1)", light2);
        LightNode light2Node = new LightNode("Light(3)", light3);
        LightNode light3Node = new LightNode("Light(4)", light4);

        // Floor
        sceneRoot.addChild(base);
            base.addChild(tfloor);
                tfloor.addChild(floor2Node);

        // Global lights
        sceneRoot.addChild(tlight);
            tlight.addChild(light_1);
                light_1.addChild(trlight1);
                    trlight1.addChild(light0Node);
        sceneRoot.addChild(tlight2); 
            tlight2.addChild(light_2);
                light_2.addChild(light1Node);

        // Table
        sceneRoot.addChild(trans1);
            trans1.addChild(leg1);
                leg1.addChild(makeLeg1);
                    makeLeg1.addChild(cube0Node);
        sceneRoot.addChild(trans2);
            trans2.addChild(leg2);
                leg2.addChild(makeLeg2);
                    makeLeg2.addChild(cube1Node);
        sceneRoot.addChild(trans3);
            trans3.addChild(leg3);
                leg3.addChild(makeLeg3);
                    makeLeg3.addChild(cube2Node);
        sceneRoot.addChild(trans4);
            trans4.addChild(leg4);
                leg4.addChild(makeLeg4);
                    makeLeg4.addChild(cube3Node);
        sceneRoot.addChild(transTT);
            transTT.addChild(tableTop);
                tableTop.addChild(makeTT);
                    makeTT.addChild(cube4Node);

        //Wall and window
        sceneRoot.addChild(placeWall);
            placeWall.addChild(makeWndw);
                makeWndw.addChild(floor3Node);
        sceneRoot.addChild(transLW);
            transLW.addChild(leftW);
                leftW.addChild(makeLW);
                makeLW.addChild(cube5Node);
        sceneRoot.addChild(transMBW);
            transMBW.addChild(middleBW);
                middleBW.addChild(makeMBW);
                    makeMBW.addChild(cube6Node);
        sceneRoot.addChild(transRW);
            transRW.addChild(rightW);
                rightW.addChild(makeRW);
                    makeRW.addChild(cube7Node);
        sceneRoot.addChild(transMTW);
            transMTW.addChild(middleTW);
                middleTW.addChild(makeMTW);
                    makeMTW.addChild(cube8Node);

        // Table objects
        sceneRoot.addChild(movePaper);
            movePaper.addChild(makePaper);
                makePaper.addChild(floor1Node);
        movePaper.addChild(transPW);
            transPW.addChild(paperW);
                paperW.addChild(makePW);
                    makePW.addChild(sphere0Node);
        sceneRoot.addChild(transPen);
            transPen.addChild(penS);
                penS.addChild(makePenS);
                    makePenS.addChild(sphere1Node);
            transPen.addChild(transPenT);
                transPenT.addChild(penT);
                    penT.addChild(makePenT);
                        makePenT.addChild(sphere2Node);
        sceneRoot.addChild(transBGlobe);
            transBGlobe.addChild(bglobe);
                bglobe.addChild(makebglobe);
                    makebglobe.addChild(cube9Node);
            transBGlobe.addChild(transGlobe);
                transGlobe.addChild(globe);
                    globe.addChild(makeglobe);
                        makeglobe.addChild(sphere3Node);

        // Lamp
        sceneRoot.addChild(transLamp);
            transLamp.addChild(lamp);
                lamp.addChild(makebase);
                    makebase.addChild(cube10Node);
            transLamp.addChild(transLampTB);
                transLampTB.addChild(transR1);
                    transR1.addChild(lampLS);
                        lampLS.addChild(makeLampLS);
                            makeLampLS.addChild(sphere4Node);
                transLampTB.addChild(transR1);
                    transR1.addChild(transLampTLS);
                        transLampTLS.addChild(lampCore);
                            lampCore.addChild(makeLampC);
                                makeLampC.addChild(sphere5Node);
                        transLampTLS.addChild(transLampTC);
                            transLampTC.addChild(transR2);
                                transR2.addChild(lampTS);
                                    lampTS.addChild(makeLampTS);
                                        makeLampTS.addChild(sphere6Node);
                            transLampTC.addChild(transR2);
                                transR2.addChild(transLampTTS);
                                    transLampTTS.addChild(transR3);
                                        transR3.addChild(lampHead);
                                            lampHead.addChild(makeLHead);
                                                makeLHead.addChild(cube11Node);
                                    transLampTTS.addChild(transR3);
                                        transR3.addChild(transEdge);
                                            transEdge.addChild(tlight3);
                                                tlight3.addChild(light2Node);
                                    transLampTTS.addChild(transR3);
                                        transR3.addChild(transCenterHead);
                                            transCenterHead.addChild(tlightC);
                                                tlightC.addChild(light3Node);
                                        transR3.addChild(transLampTH);
                                            transLampTH.addChild(makeCHat);
                                                makeCHat.addChild(cube12Node);
                                            transLampTH.addChild(translampb);
                                                translampb.addChild(makeBHat);
                                                    makeBHat.addChild(floor0Node);
    }

    // Get methods for tranformation nodes that need updating
    public TransformNode getTlight(){
        return tlight;
    }

    public TransformNode getTransEdge(){
        return transEdge;
    }

    public TransformNode getTransCenterHead(){
        return transCenterHead;
    }

    public TransformNode getTransR1(){
        return transR1;
    }

    public TransformNode getTransR2(){
        return transR2;
    }

    public TransformNode getTransR3(){
        return transR3;
    }

    public TransformNode getTransLamp(){
        return transLamp;
    }

    public TransformNode getTransPen(){
        return transPen;
    }

    public TransformNode getTransBGlobe(){
        return transBGlobe;
    }

    public TransformNode getMovePaper(){
        return movePaper;
    }

    public TransformNode getTransPW(){
        return transPW;
    }

    public SGNode getSceneRoot(){
        return sceneRoot;
    }
    public Light getLight1(){
        return light1;
    }
      
    public Light getLight2(){
        return light2;
    }

    public Light getLight3(){
        return light3;
    }

    public Light getLight4(){
        return light4;
    }

    // Clean up memory
    public void dispose(GL3 gl){
        oak.dispose(gl);
        topHat.dispose(gl);
        shinySteel.dispose(gl);
        shinySteelSphr.dispose(gl);
        wall.dispose(gl);
        baseGlbe.dispose(gl);
        baseHat.dispose(gl);
        fountain.dispose(gl);
        ivorySphere.dispose(gl);
        pen.dispose(gl);
        window.dispose(gl);
        letter.dispose(gl);
        floor.dispose(gl);
    }
}
