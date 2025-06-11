import controlP5.*;

ControlP5 cp5;

int spacing = 75;
int cols, rows;
float[][] offsets;

float influenceRadius = 400;
float maxPush = 300;
float baseNoiseAmount = 80;
float lerpAmount = 0.01;
float alphaFade = 1;

// curtain
boolean foldCurtain = false;
float curtainProgress = 0;
float curtainSpeed = 0.003;
float targetCurtainProgress = 0;

// mirroring
boolean cloneMirrored = false;

// noise frequency control
float noiseScale = 0.05; // Lower = larger shapes, higher = finer detail

boolean waveMode = false;


//Keyboard control key ----------------------
void keyPressed() {
  if (key == 'w' || key == 'W') {
    waveMode = !waveMode;
  }

  if (key == 'f' || key == 'F') {
    foldCurtain = !foldCurtain;
    targetCurtainProgress = foldCurtain ? 1 : 0;
  }

  if (key == 'c' || key == 'C') {
    cloneMirrored = !cloneMirrored;
  }

  //baseNoiseAmount
  if (keyCode == UP) {
    baseNoiseAmount += 5;
  } else if (keyCode == DOWN) {
    baseNoiseAmount = max(0, baseNoiseAmount - 5);
  }

  //influenceRadius
  if (keyCode == RIGHT) {
    influenceRadius += 20;
  } else if (keyCode == LEFT) {
    influenceRadius = max(0, influenceRadius - 20);
  }

  //noise frequency
  if (key == 'z' || key == 'Z') {
    noiseScale = max(0.001, noiseScale - 0.005);  // decrease frequency (more stretched)
  } else if (key == 'x' || key == 'X') {
    noiseScale += 0.005; // increase frequency (more detailed)
  }
}
//SETUP
void setup() {
  fullScreen(P2D);
  noiseDetail(4);
  smooth(4);
  updateGrid();
}

void updateGrid() {
  cols = floor(width / spacing) + 4;
  rows = floor(height / spacing) + 4;

  offsets = new float[cols][rows];
  for (int x = 0; x < cols; x++) {
    for (int y = 0; y < rows; y++) {
      offsets[x][y] = 0;
    }
  }
}
//DRAW
void draw() 
{
  fill(0, alphaFade);
  rect(0, 0, width, height);

  stroke(255);
  float zoff = frameCount * 0.01;
  curtainProgress = lerp(curtainProgress, targetCurtainProgress, curtainSpeed);

  for (int x = 0; x < cols; x++) 
  {
    if (foldCurtain) {
      curtainProgress += curtainSpeed;
  }

    ArrayList<PVector> shapePoints = new ArrayList<PVector>();
    beginShape();

    for (int y = 0; y < rows; y++) 
    {
      float foldOffset = 0;
      if (foldCurtain) 
      {
        float foldFactor = constrain((float)x / cols, 0, 1);
        float wave = sin((y + frameCount * 0.3f) * 0.2f + x * 0.05f) * 20;
        foldOffset = curtainProgress * 200 * foldFactor + wave * foldFactor;
      }

      float xpos = x * spacing;
      float ypos = y * spacing;

      float n = noise(x * noiseScale, y * noiseScale, zoff);
      float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);

      float dx = xpos - mouseX;
      float dy = ypos - mouseY;
      float d = dist(xpos, ypos, mouseX, mouseY);
      float influence = 0;

      if (d < influenceRadius) 
      {
        float strength = 1 - (d / influenceRadius);
        strength *= strength;
        float direction = dx > 0 ? 1 : -1;
        float direction2 = direction * sin(n);

        if (waveMode) 
        {
          float wave = (2 * abs(2 * (n * 3 - floor(n * 3 + 0.1))) - 1);
          direction2 += wave * 0.75;
        }

        influence = direction2 * strength * maxPush;
      }

      float target = baseWave + influence;
      offsets[x][y] = lerp(offsets[x][y], target, lerpAmount);

      float px = xpos + offsets[x][y] + foldOffset;
      float py = ypos;

      curveVertex(px, py);
      shapePoints.add(new PVector(px, py));
    }
    endShape();

    if (cloneMirrored) 
    {
      beginShape();
      for (PVector p : shapePoints) 
      {
        float mirroredX = width - p.x;
        curveVertex(mirroredX, p.y);
      }
      endShape();
    }
  }
}
