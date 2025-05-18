import processing.core.*;

public class Scene01Intro extends AbstractScene{
    float[][] offsets;
    int cols, rows;

    int spacing = 20;
    float influenceRadius = 400;
    float maxPush = 300;
    float baseNoiseAmount = 80;
    float lerpAmount = 0.05f;
    float alphaFade = 2.0f;
    int xPerson;

    public Scene01Intro(PApplet p) {
        super(p);
        p.noiseDetail(4);
        xPerson = width() / 2;

        PFont font = createFont("Arial", 16);

        int sliderWidth = 250;
        int sliderHeight = 30;
        int x = 30;
        int y = 30;
        int gap = 50;
        
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
    public void drawWall() {
        p.fill(0, alphaFade);
        p.noStroke();
        rect(0, 0, width(), height());

        stroke(255);
        noFill();

        float zoff = p.frameCount * 0.01f;

        for (int x = 0; x < cols; x++) {
            beginShape();
            float xpos = x * spacing;
            p.curveVertex(xpos, 0);
            for (int y = 0; y < rows; y++) {
                float ypos = y * spacing;

                float n = p.noise(x * 0.05f, y * 0.05f, zoff);
                float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);

                float dx = xpos - xPerson;
                float dy = ypos - p.mouseY;
                float d = dist(xpos, ypos, xPerson, p.mouseY);
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

    @Override
    public void drawFloor() {
        background(0);
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/1/fader1":
                spacing = floor(map(value, 0, 1, 5, 100));
                updateGrid();
                System.out.println("spacing: "+spacing);
                break;
            case "/1/fader2":
                influenceRadius = map(value, 0, 1, 50, 1000);
                System.out.println("influenceRadius: "+influenceRadius);
                break;
            case "/1/fader3":
                maxPush = map(value, 0, 1, 50, 600);
                System.out.println("maxPush: "+maxPush);
                break;
            case "/1/fader4":
                baseNoiseAmount = map(value, 0, 1, 0, 150);
                System.out.println("baseNoiseAmount: "+baseNoiseAmount);
                break;
            case "/1/fader5":
                lerpAmount = map(value, 0, 1, 0.001f, 0.2f);
                System.out.println("lerpAmount: "+lerpAmount);
                break;
            case "/1/fader6":
                alphaFade = map(value, 0, 1, 0, 50);
                System.out.println("alphaFade: "+alphaFade);
                break;
            case "/Scene01Intro/fader8":
                xPerson = floor(map(value, 0, 1, 0, width()));
                System.out.println("xPerson: "+xPerson);
                break;
            default:
                // code block
        }
    }
}
