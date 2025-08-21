// === Global Variables ===
int currentStitch = 0;
PImage bg;
int legendItemPadding = 80;

boolean pullingLoop = false;
float loopPullProgress = 0;
float sparklePulse = 0;
float glowPulse = 0;

int horizontalPadding = 80;
int verticalPadding = 5;
int cellSpacing = 25;

int topStitchPadding = 55;
int bottomStitchPadding = 15;

int totalStitches = 0;
int[][] stitchGrid;

int gridWidth;
int gridHeight;

color[] habitatColors = {
  color(110, 130, 50),
  color(255, 225, 50),
  color(96, 91, 51),
  color(170, 170, 140),
  color(255, 80, 60),
  color(216, 208, 176)
};

String[] habitatTypes = {
  "Pollinator",
  "Native Grasses",
  "Pollinator, Native Grasses",
  "Naturalized, Pollinator",
  "Pollinator, Native Grasses, Naturalized",
  "No Data"
};

int[] displayedCounts = new int[habitatTypes.length];
int legendHeight = 210;
int crochetSize = 62;

// === Bee Animation Variables ===
float beeX, beeY;
ArrayList<PVector> beeTrail = new ArrayList<PVector>();

// === App State Variables ===
String appState = "start"; // start, running, reset
int finishedTime = -1;
boolean isResetting = false;
float resetAlpha = 255;

// === Start Button ===
int buttonX, buttonY, buttonW = 180, buttonH = 60;

// === Setup ===
void setup() {
  distributeHabitats();
  totalStitches = gridWidth * gridHeight;

  int canvasWidth = gridWidth * cellSpacing + horizontalPadding * 2;
  int canvasHeight = verticalPadding * 2 + topStitchPadding + bottomStitchPadding + gridHeight * cellSpacing + legendHeight;
  surface.setSize(canvasWidth, canvasHeight);

  buttonW = buttonH = 120;
  buttonX = width / 2 - buttonW / 2;
  buttonY = height / 2 + 80;

  beeX = width / 2;
  beeY = height - legendHeight + 20;

  bg = loadImage("scene-with-title.png");
  frameRate(10);
  cursor(ARROW);
}

void draw() {
  background(248, 243, 232);

  switch (appState) {
    case "start":
      drawStartScreen();
      return;
    case "reset":
      playResetAnimation();
      return;
  }

  resetDisplayedCounts();
  drawCrochetChains(255);
  drawLegend();

  float flutterY = sin(frameCount * 0.2) * 3;
  beeX = width / 2 + 90;
  beeY = height - legendHeight + 150 + 35 + flutterY;
  drawBee(beeX, beeY);

  currentStitch++;
  if (currentStitch >= totalStitches && finishedTime == -1) {
    finishedTime = millis();
  }

  if (finishedTime > 0 && millis() - finishedTime > 10000) {
    appState = "reset";
    startReset();
  }
}


// === Start Reset Animation ===
void startReset() {
  isResetting = true;
  beeX = width + 40;
  resetAlpha = 255;
  beeTrail.clear();
}


// === Reset Bee Animation ===
void playResetAnimation() {
  background(240);

  resetAlpha -= 5;
  resetAlpha = max(0, resetAlpha);
  drawCrochetChains(resetAlpha);
  image(bg, 0, 0, width, height);

  float wave = sin(frameCount * 0.15) * 30;
  beeX -= 4;
  beeY = height / 1.7 + wave;
  
  // Draw the bee normally without any glow
  drawBee(beeX, beeY);

  beeTrail.add(new PVector(beeX, beeY));
  if (beeTrail.size() > 300) {
    beeTrail.remove(0);
  }
  
  // Apply a glowing effect to the bee trail
  stroke(255, 255, 0, 150);  // Yellow color with some transparency for the glow effect
  strokeWeight(4);  // Make the trail slightly thicker for the glow
  for (int i = 0; i < beeTrail.size(); i += 3) {
    PVector p = beeTrail.get(i);
    point(p.x, p.y);
  }

  if (beeX < -40) {
    appState = "start";
  }
}


// === Start Screen ===
void drawStartScreen() {
  background(248, 243, 232);
  image(bg, 0, 0, width, height);

  drawStartButton();
}

void drawStartButton() {
  boolean isHovering = mouseX > buttonX && mouseX < buttonX + buttonW &&
                       mouseY > buttonY && mouseY < buttonY + buttonH;

if (isHovering) {
  sparklePulse += 0.05;
  glowPulse += 0.07;
  cursor(HAND);
} else {
  cursor(ARROW);
}


  pushMatrix();
  translate(buttonX + buttonW / 2, buttonY + buttonH / 2);
  
  // === Soft Pulsating Glow (Only on hover) ===
if (isHovering) {
  float glowSize = 90 + sin(glowPulse) * 6; // pulsates between ~84–96
  for (int i = 3; i >= 1; i--) {
    float alpha = 25 * i;
    float size = glowSize * (1 + i * 0.1);  // layered blur
    noStroke();
    fill(255, 255, 150, alpha); // soft golden glow
    ellipse(0, 0, size, size);
  }
}


  // === Hook hover animation parameters ===
  float hookAngle = radians(-25);
  float hookShiftX = 5;
  float hookShiftY = -10;
  float hookScale = 1.8;
  float textSizeVal = 46;
  int sparkleCount = 2;

  if (isHovering) {
    hookAngle += radians(10);
    hookShiftX -= 2;
    hookShiftY -= 2;
    hookScale = 2.2;
    textSizeVal = 46;
    sparkleCount = 5;
  }

  // === Soft Glow Behind ===
  noStroke();
  fill(255, 255, 150, 60); // soft yellow glow
  ellipse(0, 0, 140, 140);

  // === Crochet Hook ===
  pushMatrix();
  translate(hookShiftX, hookShiftY);
  rotate(hookAngle);
  scale(hookScale);
  drawCrochetHook(-10, 0);
  popMatrix();

  // === Start Text on top ===
  textAlign(CENTER, CENTER);
  textSize(textSizeVal);
  fill(255, 225, 50);
  text("Start", 0, 0);

  // === Sparkles ===
  for (int i = 0; i < sparkleCount; i++) {
    float angle = TWO_PI / sparkleCount * i + sparklePulse;
    float radius = 20 + sin(frameCount * 0.1 + i) * 2;
    float sx = cos(angle) * radius;
    float sy = sin(angle) * radius;
    drawSparkle(sx, sy, random(0.8, 1.3));
  }

  popMatrix();
}

void drawCrochetHook(float x, float y) {
  pushMatrix();
  translate(x, y);

  stroke(80);
  fill(160);
  strokeWeight(2);
  beginShape();
  vertex(0, 0);
  bezierVertex(20, -2, 40, 5, 60, 10);
  bezierVertex(70, 15, 65, 20, 60, 22);
  bezierVertex(50, 24, 45, 18, 40, 12);
  vertex(15, 8);
  endShape();

  stroke(255, 70);
  strokeWeight(1);
  line(10, 2, 45, 10);

  popMatrix();
}

void drawSparkle(float x, float y, float scaleFactor) {
  pushMatrix();
  translate(x, y);
  scale(scaleFactor);

  stroke(255, 230, 180, 200);
  strokeWeight(1.2);
  for (int i = 0; i < 8; i++) {
    float angle = TWO_PI / 8 * i;
    float len = 6;
    line(0, 0, cos(angle) * len, sin(angle) * len);
  }

  noStroke();
  fill(255, 240, 200, 150);
  ellipse(0, 0, 4, 4);
  popMatrix();
}

// === Mouse Click ===
void mousePressed() {
  if (appState.equals("start")) {
    if (mouseX > buttonX && mouseX < buttonX + buttonW &&
        mouseY > buttonY && mouseY < buttonY + buttonH) {
      pullingLoop = true;
      loopPullProgress = 0;
      startSketch();
    }
  }
}

// === Start Sketch ===
void startSketch() {
  appState = "running";
  currentStitch = 0;
  finishedTime = -1;
  isResetting = false;
  resetAlpha = 255;
  beeTrail.clear();
  pullingLoop = false; // reset
  distributeHabitats();
  cursor(ARROW);  // reset to arrow at startup
}

// === Stitch Counts ===
void resetDisplayedCounts() {
  for (int i = 0; i < displayedCounts.length; i++) {
    displayedCounts[i] = 0;
  }

  int count = 0;
  for (int j = 0; j < gridHeight; j++) {
    for (int i = 0; i < gridWidth; i++) {
      if (count >= currentStitch) return;
      int habitatIndex = stitchGrid[i][j];
      displayedCounts[habitatIndex]++;
      count++;
    }
  }
}

// === Crochet Grid ===
void distributeHabitats() {
  int[][] rows = {
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0, 0, 5, 5, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 2, 1, 1, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 4, 1, 2, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 2, 1, 1, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 1, 1, 2, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 1, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 1, 2, 3, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
  };

  gridHeight = rows.length;
  gridWidth = rows[0].length;
  stitchGrid = new int[gridWidth][gridHeight];

  for (int y = 0; y < gridHeight; y++) {
    for (int x = 0; x < gridWidth; x++) {
      stitchGrid[x][y] = rows[y][x];
    }
  }
}

// === Crochet Chains with Alpha ===
void drawCrochetChains(float alphaVal) {
  noFill();
  strokeWeight(13);
  int count = 0;

  for (int j = 0; j < gridHeight; j++) {
    int y = verticalPadding + topStitchPadding + j * cellSpacing;

    for (int i = 0; i < gridWidth; i++) {
      if (count >= totalStitches || count >= currentStitch || y >= height - legendHeight - bottomStitchPadding) return;

      int drawIndex = (j % 2 == 0) ? i : (gridWidth - 1 - i);
      int availableWidth = width - 2 * horizontalPadding;
      int gridPixelWidth = (gridWidth - 1) * cellSpacing;
      int startX = horizontalPadding + (availableWidth - gridPixelWidth) / 2;
      int x = startX + drawIndex * cellSpacing;

      int habitatIndex = stitchGrid[i][j];
      color stitchColor = habitatColors[habitatIndex];

      float loopSize = crochetSize * 1.1;
      float xOffset = (drawIndex % 2 == 0) ? 0 : loopSize * 0.2;
      float yOffset = 0; // remove vertical shift entirely

      // Create a unique seed for this stitch
      int uniqueSeed = i * 1000 + j;
      drawCrochetStitch(x + xOffset, y + yOffset, loopSize, stitchColor, alphaVal, uniqueSeed);

      count++;
    }
  }
}

void drawCrochetStitch(float x, float y, float size, color baseColor, float alphaVal, int seed) {
  float loopWidth = size * 1.5;
  color stitchColor = naturalShade(baseColor, seed);

  randomSeed(seed);

  // === DROP SHADOW ===
  noStroke();
  fill(0, alphaVal * 0.05);
  ellipse(x + 3, y + 4, loopWidth * 0.95, size * 1.2);  // slightly offset

  // === BACK GRADIENT LAYER / 3D Yarn Shine ===
  for (int i = 3; i >= 1; i--) {
    float step = map(i, 1, 3, 0.1, 0.3);
    stroke(lerpColor(stitchColor, color(255), step), alphaVal * 0.08);
    strokeWeight(i);
    noFill();
    bezier(x, y,
           x - loopWidth / 2, y - size * 0.9,
           x + loopWidth / 2, y + size * 0.9,
           x, y);
  }

  // === INTERLOCKING CURVED SHAPE using beginShape() ===
  stroke(stitchColor, alphaVal);
  strokeWeight(9);
  noFill();
  beginShape();
  for (float t = 0; t <= 1; t += 0.2) {
    float cx = x + sin(TWO_PI * t) * loopWidth * 0.4;
    float cy = y + cos(TWO_PI * t) * size * 0.6;
    curveVertex(cx, cy);
  }
  endShape(CLOSE);

  // === TWISTED YARN SPIRAL ===
  stroke(lerpColor(stitchColor, color(0), 0.2), alphaVal * 0.15);
  strokeWeight(1);
  noFill();
  for (float a = 0; a < TWO_PI; a += PI / 6) {
    float rad = size * 0.4;
    float tx1 = x + cos(a) * rad;
    float ty1 = y + sin(a) * rad * 0.7;
    float tx2 = x + cos(a + 0.3) * rad * 0.9;
    float ty2 = y + sin(a + 0.3) * rad * 0.7;
    line(tx1, ty1, tx2, ty2);
  }

  // === INNER SPIRAL FIBERS ===
  strokeWeight(0.5);
  for (float i = 0; i < 5; i++) {
    float a = random(0, TWO_PI);
    float r1 = size * random(0.2, 0.4);
    float r2 = r1 + 4;
    float x1 = x + cos(a) * r1;
    float y1 = y + sin(a) * r1 * 0.8;
    float x2 = x + cos(a + 0.4) * r2;
    float y2 = y + sin(a + 0.4) * r2 * 0.8;
    stroke(fiberBrown((int)(seed + i)), alphaVal * 0.3);
    line(x1, y1, x2, y2);
  }

  // === TOP OVERLAP HOOK / INTERLOCK ===
  stroke(lerpColor(stitchColor, color(255), 0.3), alphaVal * 0.7);
  strokeWeight(4);
  arc(x, y - size * 0.4, loopWidth * 0.4, size * 0.3, PI, TWO_PI);

  // === CONTRAST RIM (Dark or light depending on brightness) ===
  if (brightness(stitchColor) < 85) {
    stroke(255, alphaVal * 0.4);
  } else {
    stroke(50, alphaVal * 0.3);
  }
  strokeWeight(1.5);
  noFill();
  arc(x, y, loopWidth * 1.1, size * 1.1, -PI / 2.5, PI / 2.5);
}


// — Adds randomized dyed/weathered tone —
color naturalShade(color base, int seed) {
  randomSeed(seed + 99);
  float t = random(0.05, 0.18);
  color earthy = lerpColor(color(93, 82, 48), color(170, 170, 140), random(0.3, 0.7));
  return lerpColor(base, earthy, t);
}

// — Fibrous yarn texture (brown threads) —
void drawFiberTexture(float x, float y, float size, float loopWidth, float alphaVal, int seed) {
  randomSeed(seed + 100);
  strokeWeight(0.3);
  for (int i = 0; i < 4; i++) {
    float t = random(0.1, 0.9);
    float angle = random(-PI / 5, PI / 5);
    float cx = x + cos(angle) * t * loopWidth * 0.5;
    float cy = y + sin(angle) * t * size;
    float offset = size * 0.12;

    color fibColor = fiberBrown(seed + i);
    stroke(fibColor, alphaVal * 0.14);
    bezier(cx - offset, cy - offset,
           cx, cy + random(-2, 2),
           cx, cy + random(-2, 2),
           cx + offset, cy + offset);
  }
}

// — Thin horizontal fibers (woven effect) —
void drawCrossFibers(float x, float y, float size, float loopWidth, float alphaVal, int seed) {
  randomSeed(seed + 300);
  strokeWeight(0.2);
  
  int crossCount = 2 + int(size * 0.04); // more crosses for larger size
  float maxSpread = size * 0.25;

  for (int i = 0; i < crossCount; i++) {
    float shift = random(-maxSpread, maxSpread); // vertical spread
    float fiberLength = loopWidth * random(0.6, 1.0); // horizontal size varies with loopWidth

    float cx1 = x - fiberLength / 2;
    float cx2 = x + fiberLength / 2;
    float cy = y + shift;

    color fiber = fiberBrown(seed + i + 50);
    stroke(fiber, alphaVal * 0.12);
    line(cx1, cy, cx2, cy);
  }
}

// — Brown color generator with per-stitch variation —
color fiberBrown(int seed) {
  randomSeed(seed);
  return color(
    85 + random(-10, 10),   // R
    60 + random(-8, 6),     // G
    40 + random(-5, 4)      // B
  );
}


// === Bee Drawing ===
void drawBee(float x, float y) {
  fill(255, 225, 50);
  ellipse(x, y, 40, 35);

  fill(96, 91, 51);
  ellipse(x - 10, y - 5, 10, 10);
  ellipse(x + 2, y - 5, 10, 10);
  rect(x + 9, y - 9, 3, 19);
  rect(x + 14, y - 7, 3, 15);

  fill(255, 80, 60);
  rect(x - 5, y + 5, 5, 4);

  fill(216, 208, 176);
  float wingOffset = sin(frameCount * 0.2) * 2;
  ellipse(x - 10, y - 19 + wingOffset, 20, 10);
  ellipse(x + 10, y - 19 + wingOffset, 20, 10);
}

void drawVerticalGradient(float x, float y, float w, float h, color c1, color c2) {
  noFill();
  for (int i = 0; i < h; i++) {
    float inter = map(i, 0, h, 0, 1);
    stroke(lerpColor(c1, c2, inter));
    line(x, y + i, x + w, y + i);
  }
}


// === Legend ===
void drawLegend() {
  // === Background Gradient ===
  noStroke();
  drawVerticalGradient(0, height - legendHeight, width, legendHeight, 
    color(255, 250, 240), color(230, 230, 197)); 

  // === Stitched Border (Hand-Sewn Frame) ===
  drawStitchedBorder(0, height - legendHeight, width, legendHeight, 10);

  // === Text and Elements ===
  textAlign(CENTER, CENTER);
  textSize(20);
  fill(0);
  text("Habitat Types", width / 2, height - legendHeight + 22);

  int spacing = (width - 2 * horizontalPadding - (legendItemPadding * (habitatTypes.length - 1))) / habitatTypes.length;

  for (int i = 0; i < habitatTypes.length; i++) {
    int x = horizontalPadding + i * (spacing + legendItemPadding) + spacing / 2;
    int y = height - legendHeight + 55;

    fill(habitatColors[i]);
    noStroke();
    ellipse(x, y, 17, 17);

    fill(0);
    textSize(12);
    String[] words = split(habitatTypes[i], " ");
    float textY = y + 20;
    for (String word : words) {
      text(word.trim(), x, textY);
      textY += 15;
    }

    textSize(18);
    text(displayedCounts[i], x, textY + 5);
  }

  stroke(180);
  strokeWeight(1);
  int ruleY = height - legendHeight + 155;

  int totalY = ruleY + 25;
  int totalXCenter = width / 2;
  int sum = 0;
  for (int val : displayedCounts) sum += val;

  fill(0);
  textSize(24);
  textAlign(CENTER, CENTER);
  text(sum, totalXCenter - 69, totalY);
  textSize(20);
  text("Habitat Sites", totalXCenter + 9, totalY);
}

void drawStitchedBorder(float x, float y, float w, float h, float stitchSpacing) {
  stroke(100, 70);  // soft dark brown stitches
  strokeWeight(1);

  // Top
  for (float i = x; i < x + w; i += stitchSpacing) {
    line(i, y, i + stitchSpacing / 2, y);
  }
  // Bottom
  for (float i = x; i < x + w; i += stitchSpacing) {
    line(i, y + h, i + stitchSpacing / 2, y + h);
  }
  // Left
  for (float i = y; i < y + h; i += stitchSpacing) {
    line(x, i, x, i + stitchSpacing / 2);
  }
  // Right
  for (float i = y; i < y + h; i += stitchSpacing) {
    line(x + w, i, x + w, i + stitchSpacing / 2);
  }
}