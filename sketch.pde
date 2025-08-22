import ddf.minim.*;

// Minim object for sound
Minim minim;
AudioPlayer backgroundSound;
AudioPlayer[] tooltipSounds = new AudioPlayer[5]; // Array for tooltip sounds

PImage backgroundImage;
PImage[] lightbulbImages = new PImage[5]; // Array to store images for each lightbulb
boolean[] clickedStatus = new boolean[5]; // Track clicked lightbulbs
int activeLightbulb = -1; // Track which lightbulb is clicked (-1 means none)

PVector[] lightbulbPositions = new PVector[5];

String[] tooltips = {
  "Plant Diversity:\nInclude a variety of flowering plants, grasses, and herbs to provide\nyear-round forage for pollinators like monarch butterflies and bumblebees.",
  "Natural Pest Control:\nEncourage the presence of ladybugs and praying mantises to naturally\nmanage pests like aphids and other insects, reducing reliance on\nchemical pesticides and fostering a healthier, more balanced ecosystem.",
  "Shade-tolerant Species:\nPlace clover in shaded areas\nbeneath or near solar panels.",
  "Full-sun Plants:\nPlace wildflowers and milkweed in\nsunnier areas.",
  "Gentle Mowing:\nUse rotational grazing or flail mowers to manage plant growth\nwhile protecting pollinator habitats."
};

int lastClickTime = 0; // Store the time of the last lightbulb selection
boolean restartScheduled = false; // Track if restart has been scheduled

void setup() {
  size(1080, 800);
  
  // Initialize Minim
  minim = new Minim(this);
  backgroundSound = minim.loadFile("birds.mp3");
  backgroundSound.loop();
  
  // Reduce the volume of the background sound
  backgroundSound.setGain(-20); // Adjust volume here (lower values = quieter sound)

  // Load tooltip sounds
  tooltipSounds[0] = minim.loadFile("flapping.mp3");
  tooltipSounds[1] = minim.loadFile("bug-eating-plants.mp3");
  tooltipSounds[2] = minim.loadFile("rustling-plant.mp3");
  tooltipSounds[3] = minim.loadFile("bumblebees.mp3");
  tooltipSounds[4] = minim.loadFile("sheep.mp3");

  tooltipSounds[0].setGain(-10); // Make flapping.mp3 quieter by reducing gain
  tooltipSounds[3].setGain(-5); // Make bumblebees.mp3 quieter by reducing gain
  tooltipSounds[4].setGain(-15); // Make sheep.mp3 quieter by reducing gain

  // Load background image
  backgroundImage = loadImage("initial-scene.jpg");

  // Load images for each lightbulb selection
  lightbulbImages[0] = loadImage("plant-diversity.png");
  lightbulbImages[1] = loadImage("pest-control.png");
  lightbulbImages[2] = loadImage("clover.png");
  lightbulbImages[3] = loadImage("flowers.png");
  lightbulbImages[4] = loadImage("grazing.png");

  // Define lightbulb positions
  lightbulbPositions[0] = new PVector(128, 223);
  lightbulbPositions[1] = new PVector(297, 501);
  lightbulbPositions[2] = new PVector(391, 399);
  lightbulbPositions[3] = new PVector(841, 539);
  lightbulbPositions[4] = new PVector(1030, 405);
}

void draw() {
  image(backgroundImage, 0, 0, width, height);

  // Calculate pulsating glow effect
  float glowSize = 40 + 10 * sin(frameCount * 0.1); // Glow oscillates

  // Enable additive blending for a consistent glow color
  blendMode(ADD);
  fill(255, 255, 150, 80); // Light yellow transparent glow
  
  for (int i = 0; i < lightbulbPositions.length; i++) {
    if (!clickedStatus[i]) { // Only show glow if lightbulb has NOT been clicked
      ellipse(lightbulbPositions[i].x, lightbulbPositions[i].y, glowSize, glowSize);
    }
  }

  // Reset to normal blend mode before drawing the actual lightbulbs
  blendMode(BLEND);

  for (int i = 0; i < lightbulbPositions.length; i++) {
    PVector pos = lightbulbPositions[i];
    float distance = dist(mouseX, mouseY, pos.x, pos.y);

    // Draw lightbulb with hover effect
    fill(distance < 20 ? color(255, 255, 0, 150) : color(255, 255, 255, 80));
    ellipse(pos.x, pos.y, 20, 20);
  }

  // Ensure pest-control is always on top of clover
  for (int i = 0; i < clickedStatus.length; i++) {
    if (clickedStatus[i]) {
      // First, draw the clover image
      if (i == 2) {
        image(lightbulbImages[2], 0, 0);
      }
      
      // Then, draw pest-control, making sure it is always on top of clover
      if (i == 1) {
        image(lightbulbImages[1], 0, 0);
      }

      // Draw other images as needed
      if (i != 2 && i != 1) { 
        image(lightbulbImages[i], 0, 0);
      }
    }
  }

  // Display tooltip if a lightbulb is selected, ensuring it appears on top
  if (activeLightbulb != -1) {
    displayTooltip(tooltips[activeLightbulb], lightbulbPositions[activeLightbulb]);
  }

  // Restart sketch if 15 seconds have passed since last click
  if (restartScheduled && millis() - lastClickTime >= 15000) {
    restartSketch();
  }
}

// Function to display tooltip dynamically
void displayTooltip(String text, PVector pos) {
  String[] lines = split(text, '\n');
  float tooltipWidth = textWidth(text) + 20;
  float tooltipHeight = lines.length * 20 + 20;

  float tooltipX = pos.x + 10;
  if (tooltipX + tooltipWidth > width) tooltipX = pos.x - tooltipWidth - 10;

  float tooltipY = pos.y - 25;
  if (tooltipY - tooltipHeight < 0) tooltipY = pos.y + 25;

  // Ensure tooltips do not go off-screen
  if (tooltipX < 0) tooltipX = 10;
  if (tooltipY + tooltipHeight > height) tooltipY = height - tooltipHeight - 10;

  fill(255, 255, 255, 200);
  noStroke();
  rect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 10);

  fill(0);
  textSize(16);
  textAlign(LEFT, TOP);

  float lineHeight = 20;
  for (int i = 0; i < lines.length; i++) {
    text(lines[i], tooltipX + 10, tooltipY + 10 + i * lineHeight);
  }
}

void mousePressed() {
  boolean clickedOnLightbulb = false;
  
  for (int i = 0; i < lightbulbPositions.length; i++) {
    if (dist(mouseX, mouseY, lightbulbPositions[i].x, lightbulbPositions[i].y) < 20) {
      activeLightbulb = i; // Set the clicked lightbulb as active
      clickedStatus[i] = true; // Mark it as clicked (stops the pulsating glow)
      clickedOnLightbulb = true;

      // Start or continue playing the tooltip sound for the clicked lightbulb
      if (tooltipSounds[i] != null) {
        // Rewind and loop the sound each time a lightbulb is selected
        tooltipSounds[i].rewind();
        tooltipSounds[i].loop();  // Loop the sound
      } else {
        println("Error: Sound for lightbulb " + i + " is not loaded.");
      }

      // Schedule a restart if all lightbulbs have been clicked
      if (allLightbulbsClicked()) {
        lastClickTime = millis();
        restartScheduled = true;
      }

      break;
    }
  }
  
  // If the user clicks anywhere else, reset the active lightbulb but leave sounds playing
  if (!clickedOnLightbulb) {
    activeLightbulb = -1;
  }
}

boolean allLightbulbsClicked() {
  for (boolean status : clickedStatus) {
    if (!status) return false;
  }
  return true;
}

void restartSketch() {
  // Reset clicked status and active lightbulb
  for (int i = 0; i < clickedStatus.length; i++) {
    clickedStatus[i] = false;
  }
  activeLightbulb = -1;
  restartScheduled = false;
  lastClickTime = 0;

  // Stop all sounds when restarting
  for (int i = 0; i < tooltipSounds.length; i++) {
    if (tooltipSounds[i] != null) {
      tooltipSounds[i].close();
    }
  }

  // Reload the tooltip sounds so they are ready to play again
  tooltipSounds[0] = minim.loadFile("flapping.mp3");
  tooltipSounds[1] = minim.loadFile("bug-eating-plants.mp3");
  tooltipSounds[2] = minim.loadFile("rustling-plant.mp3");
  tooltipSounds[3] = minim.loadFile("bumblebees.mp3");
  tooltipSounds[4] = minim.loadFile("sheep.mp3");

  // Adjust the volume again after reloading
  tooltipSounds[0].setGain(-10); // Make flapping.mp3 quieter
  tooltipSounds[3].setGain(-5); // Make bumblebees.mp3 quieter
  tooltipSounds[4].setGain(-15); // Make sheep.mp3 quieter
}

void stop() {
  backgroundSound.close();
  minim.stop();
  super.stop();
}
