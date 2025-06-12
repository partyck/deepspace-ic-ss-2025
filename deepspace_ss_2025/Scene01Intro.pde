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
    int personHeight = 10;
    int floorHeightInteraction = 10;
    
    TuioClient tracker;

    public Scene01Intro(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        noiseDetail(4);

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
        cols = floor(width() / (float) spacing) + 4;
        rows = floor(height() / (float) spacing) + 4;

        offsets = new float[cols][rows];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                offsets[x][y] = 0;
            }
        }
    }

    @Override
    public void drawWall() {
        fill(0, alphaFade);
        noStroke();
        rect(0, 0, width(), height());

        stroke(255);
        noFill();

        float zoff = frameCount * 0.01f;

        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();

        for (int x = 0; x < cols; x++) {
            beginShape();
            float xpos = x * spacing;
            curveVertex(xpos, 0);
            for (int y = 0; y < rows; y++) {
                float ypos = y * spacing;

                float n = noise(x * 0.05f, y * 0.05f, zoff);
                float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);
                
                float influence = 0;
                for(TuioCursor cursor: tuioCursorList) {
                    if (cursor.getScreenY(this.height()) < floorHeightInteraction) {
                        int pX = cursor.getScreenX(this.width());
                        float dx = xpos - pX;
                        float d = dist(xpos, ypos, pX, height() - personHeight);

                        if (d < influenceRadius) {
                            float strength = 1 - (d / influenceRadius);
                            strength *= strength;
                            float direction = dx > 0 ? 1 : -1;
                            float direction2 = direction * sin(n);
                            influence = direction2 * strength * maxPush;
                        }
                    }
                }


                float target = baseWave + influence;
                offsets[x][y] = lerp(offsets[x][y], target, lerpAmount);

                curveVertex(xpos + offsets[x][y], ypos);
            }
            endShape();
        }
    }

    @Override
    public void drawFloor() {
        background(0);
        fill(255);
        noStroke();
        rect(0, 0, width(), floorHeightInteraction);
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/1/fader1":
                spacing = floor(map(value, 0, 1, 5, 100));
                updateGrid();
                System.out.println("    spacing: "+spacing);
                break;
            case "/1/fader2":
                influenceRadius = map(value, 0, 1, 50, 1000);
                System.out.println("    influenceRadius: "+influenceRadius);
                break;
            case "/1/fader3":
                maxPush = map(value, 0, 1, 50, 600);
                System.out.println("    maxPush: "+maxPush);
                break;
            case "/1/fader4":
                baseNoiseAmount = map(value, 0, 1, 0, 150);
                System.out.println("    baseNoiseAmount: "+baseNoiseAmount);
                break;
            case "/1/fader5":
                lerpAmount = map(value, 0, 1, 0.001f, 0.2f);
                System.out.println("    lerpAmount: "+lerpAmount);
                break;
            case "/1/fader6":
                alphaFade = map(value, 0, 1, 0, 50);
                System.out.println("    alphaFade: "+alphaFade);
                break;
            case "/1/fader7":
                personHeight = floor(map(value, 0, 1, 0, 500));
                System.out.println("    personHeight: "+personHeight);
                break;
            case "/1/fader8":
                floorHeightInteraction = floor(map(value, 0, 1, 0, 500));
                System.out.println("    floorHeightInteraction: "+floorHeightInteraction);
                break;
            default:
        }
    }
}
