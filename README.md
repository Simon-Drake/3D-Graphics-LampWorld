This program builds a 3D scene with animated lamp starting with primitive shapes such as cubes and spheres defined as a 3D mesh. These shapes are transformed and rotated to build up a scene using a "Scene Graph". The lighting is calculated using shaders, I have implemented ambient, diffuse and specular lighting. The lamp can jump and pose and these animations are initiated using buttons. 

To run the progrem unzip jogl2.zip file where you would like the class files and executables then add the paths. For a mac or linux:

{
export PATH=$PATH:/path_to_folder/jogl2/lib 
export CLASSPATH=.:$CLASSPATH:./gmaths/*
export CLASSPATH=.:$CLASSPATH:/path_to_folder/jogl2/jar/*
}

Compile and run **Anilamp.java**
