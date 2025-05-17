


import controlP5.*;
import processing.core.PFont;

import static processing.core.PConstants.P2D;


public class hanifTest extends AbstractScene{

    ControlP5 cp5;

    int spacing = 20;
    int cols, rows;
    float[][] offsets;

    float influenceRadius = 400;
    float maxPush = 300;
    float baseNoiseAmount = 80;
    float lerpAmount = 0.05f;
    float alphaFade = 2.0f;


        public hanifTest(Performance p) {
            super(p);
//            p.fullScreen(P2D);
            p.noiseDetail(4);

            cp5 = new ControlP5(p);
            PFont font = createFont("Arial", 16);
            cp5.setFont(font);

            int sliderWidth = 250;
            int sliderHeight = 30;
            int x = 30;
            int y = 30;
            int gap = 50;

            cp5.addSlider("spacing")
                    .setPosition(x, y)
                    .setSize(sliderWidth, sliderHeight)
                    .setRange(5, 100)
                    .setValue(spacing)
                    .getCaptionLabel().setFont(font).setPaddingX(10);

            cp5.addSlider("influenceRadius")
                    .setPosition(x, y += gap)
                    .setSize(sliderWidth, sliderHeight)
                    .setRange(50, 1000)
                    .setValue(influenceRadius)
                    .getCaptionLabel().setFont(font).setPaddingX(10);

            cp5.addSlider("maxPush")
                    .setPosition(x, y += gap)
                    .setSize(sliderWidth, sliderHeight)
                    .setRange(50, 600)
                    .setValue(maxPush)
                    .getCaptionLabel().setFont(font).setPaddingX(10);

            cp5.addSlider("baseNoiseAmount")
                    .setPosition(x, y += gap)
                    .setSize(sliderWidth, sliderHeight)
                    .setRange(0, 150)
                    .setValue(baseNoiseAmount)
                    .getCaptionLabel().setFont(font).setPaddingX(10);

            cp5.addSlider("lerpAmount")
                    .setPosition(x, y += gap)
                    .setSize(sliderWidth, sliderHeight)
                    .setRange(0.001f, 0.2f)
                    .setValue(lerpAmount)
                    .getCaptionLabel().setFont(font).setPaddingX(10);

            cp5.addSlider("alphaFade")
                    .setPosition(x, y += gap)
                    .setSize(sliderWidth, sliderHeight)
                    .setRange(0, 50)
                    .setValue(alphaFade)
                    .getCaptionLabel().setFont(font).setPaddingX(10);

            updateGrid();

            stroke(255);
            noFill();
        }



    void updateGrid() {
        cols = p.floor(width() / (float) spacing) + 4;
        rows = p.floor(height() / (float) spacing) + 4;

        offsets = new float[cols][rows];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                offsets[x][y] = 0;
            }
        }
    }

        @Override
        public void draw() {

            p.fill(0, alphaFade);
            p.noStroke();
            rect(0, 0, width(), height());

            stroke(255);
            noFill();

            float zoff = p.frameCount * 0.01f;

            for (int x = 0; x < cols; x++) {
                beginShape();
                for (int y = 0; y < rows; y++) {
                    float xpos = x * spacing;
                    float ypos = y * spacing;

                    float n = p.noise(x * 0.05f, y * 0.05f, zoff);
                    float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);

                    float dx = xpos - p.mouseX;
                    float dy = ypos - p.mouseY;
                    float d = dist(xpos, ypos, p.mouseX, p.mouseY);
                    float influence = 0;

                    if (d < influenceRadius) {
                        float strength = 1 - (d / influenceRadius);
                        strength *= strength;
                        float direction = dx > 0 ? 1 : -1;
                        float direction2 = direction * p.sin(n);
                        influence = direction2 * strength * maxPush;
                    }

                    float target = baseWave + influence;
                    offsets[x][y] = lerp(offsets[x][y], target, lerpAmount);

                    p.curveVertex(xpos + offsets[x][y], ypos);
                }
                endShape();
            }

        }


}
