import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Model {
  
  private Mesh mesh;
  private int[] textureId1, textureId2, textureId3; 
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light1, light2, light3, light4; 
  public double startTime;  

  public Model(GL3 gl, Camera camera, Light light1, Light light2, Light light3, Light light4, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1,  int[] textureId2, int[] textureId3){
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light1 = light1;
    this.light2 = light2;
    this.light3 = light3;
    this.light4 = light4;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
    this.textureId3 = textureId3;
    startTime = getSeconds();
  }
  
  public Model(GL3 gl, Camera camera, Light light1, Light light2, Light light3, Light light4, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, null, null, null);
  }

  public Model(GL3 gl, Camera camera, Light light1, Light light2, Light light3, Light light4, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light1, light2, light3, light4, shader, material, modelMatrix, mesh, textureId1, null, null);
  }


  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    // Used to update the texture of the fountain on the table. Updates the tex coords periodically. 
    double elapsedTime = getSeconds() - startTime;
    double t = elapsedTime*0.1; 
    float offsetX = (float)(t - Math.floor(t));
    float offsetY = (float)(t - Math.floor(t));
    shader.setFloat(gl, "offset", offsetX, offsetY);


    shader.setVec3(gl, "light1.position", light1.getPosition());
    shader.setVec3(gl, "light1.ambient", light1.getMaterial().getAmbient());
    shader.setVec3(gl, "light1.diffuse", light1.getMaterial().getDiffuse());
    shader.setVec3(gl, "light1.specular", light1.getMaterial().getSpecular());
        
    shader.setVec3(gl, "light2.position", light2.getPosition());
    shader.setVec3(gl, "light2.ambient", light2.getMaterial().getAmbient());
    shader.setVec3(gl, "light2.diffuse", light2.getMaterial().getDiffuse());
    shader.setVec3(gl, "light2.specular", light2.getMaterial().getSpecular());

    shader.setVec3(gl, "light3.position", light3.getPosition());
    shader.setVec3(gl, "light3.ambient", light3.getMaterial().getAmbient());
    shader.setVec3(gl, "light3.diffuse", light3.getMaterial().getDiffuse());
    shader.setVec3(gl, "light3.specular", light3.getMaterial().getSpecular());

    shader.setVec3(gl, "light4.position", light4.getPosition());
    shader.setVec3(gl, "light4.ambient", light4.getMaterial().getAmbient());
    shader.setVec3(gl, "light4.diffuse", light4.getMaterial().getDiffuse());
    shader.setVec3(gl, "light4.specular", light4.getMaterial().getSpecular());

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess()); 

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0); 
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }

    // For the window alternate between night and day
    double v = Math.abs(Math.sin(elapsedTime*0.1));
    if (textureId2!=null && textureId3!=null) {
      if(v > 0.65){ 
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId3[0]);
      }
      else if(v < 0.30){
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
      }
      else{
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
      
      }
    }

    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
    if (textureId3!=null) gl.glDeleteBuffers(1, textureId3, 0);
  }
}