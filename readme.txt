COM3503 3D Computer Graphics Assignment
Simon Drake
spdrake2@sheffield.ac.uk

____________________________________________________________

Jump: 
- If the button "jump" is pushed then the lamp will jump at a random position on the table (not on top of the other objects),
  if the jump is a big one relative to the possible distances then the lamp will bend more. 
- Once the jump lands the objects on the table experience their own small jumps with stochastic properties.
____________________________________________________________

Pose:
- If the button "random pose" is pressed the lamp will pose within certain angular restrictions. 
  The lamp can animate between poses (doesn't have to return to its neutral state).
  The lamp will hold its pose until told to reset or jump. 
____________________________________________________________

LampStates:
- LampState 0: Neutral, the lamp bobs up and down with small angular changes this gives the impression that it is alive. 
- LampState 1: Starts a jump by bending to its bend angles.
- LampState 2: Extends to a fully straight position.
- LampState 3: Moves through the air.
- LampState 4: It resets to its neutral set.    
- LampState 5: Poses.
- LampState 6: Frozen during a pose.  

The lamp can go from a pose to a jump directly without having to reset. The only thing it will not cope well with is if
the pose/jump button is hit twice in rapid succession not giving it enough time to finish. As long as this is avoided
all animations are smooth. 
____________________________________________________________

SceneGraph: 
- LightNode: The scene graph was extended to implement LightNodes in LightNode.java so that the lamp light would move
             as required. 
- A separate class SceneGraph.java is used to implement the initial scene graph, models and lights. 
____________________________________________________________

Lights: 
- 2 general world lights: one in a fixed position one moving. 
- 1 spotlight
- 1 position light: I needed two points in space which would give me the orientation of the spotlight, 
                    to do this I implemented another light in the scene graph so its position was always
                    where I needed it to be, the position of this light and the lamp light gave me the 
                    orientation for the spotlight.
- All lights can be turns on and off independently using the buttons. 
____________________________________________________________

Shaders: 
- Mainly one vertex and fragment shader used, except for an object on the table where I wanted to update the texture coords. 
____________________________________________________________

Texture: 
- Everything is texture mapped except from the pen stem which is just a shiny red colour. 
- One object on the table dynamically changes its texture as to give the impression of flowing water. 
- The window alternates between 3 images, one for night, one for day and one which acts as sunset and sunrise.  
  The images do not display the same scene as I could not find any, so unfortunately it is not very realistic. This was more to 
  demonstrate that I could do it. 
____________________________________________________________
EXECUTE

Compile Anilamp.java and run it. 
As long as the class paths and executable paths are added as instructed in the tutorial. It should run without a problem
____________________________________________________________