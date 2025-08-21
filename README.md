# Power-in-Stitches
Power in Stitches is an interactive, abstract data visualization that explores agrivoltaics—the integration of solar energy generation with agricultural systems—through a creative, dynamic lens. Drawing from the visual language of crochet and inspired by the minimalist abstractions of suprematism as well as the perceptual play of Op Art, it uses a grid of "stitches" to represent the frequencies and diversity of agrivoltaic habitats, sourced from the InSPIRE Agrivoltaics Map.

Merging environmental data with creative coding, the piece highlights how agrivoltaics fosters biodiversity. A bee, the icon for Habitat under the InSPIRE Agrivoltaics Map’s "Agrivoltaics Activities" category, is rendered. As users interact with the visualization, they experience a shifting, crochet-like pattern that offers a deeper understanding of how agrivoltaic activities benefit the environment and agricultural productivity.

The sketch is structured around a series of global variables that define the key animation elements, such as the bee’s movement, visual components like habitat-specific colors, and the grid dimensions for the crochet pattern. These variables synchronize the layout, interactivity, and data display throughout the project, ensuring that each visual and functional component aligns with the project's narrative.

In the setup() function, the canvas is initialized, and the start button is positioned with a pulsating effect to indicate its interactive nature. The initial states of the bee, background image, and crochet grid are set up, laying the groundwork for the subsequent animation. This step effectively prepares the environment to engage the user.

The draw() loop serves as the heart of the animation, updating the visuals based on the app's state (start, running, or reset). It animates the bee’s flight path across the canvas, draws a glowing crochet chain grid that expands as the animation progresses, and dynamically updates the habitat data being visualized.

The start button is designed to pulsate and glow in response to mouse interactions. When clicked, it triggers the transition from the “start” to “running” state, initiating the animation of crochet stitches. 

A core visual element is the crochet grid, representing different habitat types within the Habitat agrivoltaic activity. Each habitat type is color-coded and dynamically transformed into crochet stitches that are drawn in real time, creating a growing pattern that corresponds to live data. This grid not only serves as a visual focal point but also acts as a data-driven model of habitat distribution.

At the bottom of the canvas, a stitched border displays at the top of a legend table that provides real-time updates on the number of stitches (habitat sites) drawn for each habitat type. 

For the crochet stitches, a combination of beginShape() and curveVertex() is used to craft smooth, organic shapes that resemble yarn loops. These shapes are enhanced with soft drop shadows for depth, layered Bezier curves to simulate the natural twist of yarn, and sine-wave spirals that evoke the plied structure of thread. The naturalShade() function introduces subtle color variations, mimicking the hand-dyed quality of yarn, while contrast strokes and highlights emphasize the structural integrity of each stitch.

Additional fibrous textures are drawn with drawFiberTexture() and drawCrossFibers(), simulating yarn tension and surface fuzz. Thread-line irregularities are added to give each crochet loop a handmade feel, emphasizing the organic nature of the process.

Finally, 10 seconds after the animation completes and all stitches are drawn, a reset animation occurs. The bee exits the scene, and the app returns to its starting state, preparing for another cycle. This reset function ensures that the user can experience the animation from the beginning.
