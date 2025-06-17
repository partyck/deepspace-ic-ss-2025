import processing.core.*;
import TUIO.*;
import java.util.ArrayList;

public class Scene01Intro extends AbstractScene{
    float[][] offsets;
    int cols, rows;

    int spacing = 75;
    float influenceRadius = 400;
    float maxPush = 300;
    float baseNoiseAmount = 80;
    float lerpAmount = 0.01f;
    float alphaFade = 1.0f;
    int personHeight = 50;
    int floorHeightInteraction = 800;
    //NEW variable
    // curtain
    boolean foldCurtain = false;
    float curtainProgress = 0;
    float curtainSpeed = 0.003f;
    float targetCurtainProgress = 0;
    
    float circleSize;
    
    
    float incDecNoiseAmount = 0.01f;
    float incDecInfluenceAmount = 30;
    float incDecBaseNoiseAmount = 10;

    // mirroring
    boolean cloneMirrored = false;

    // noise frequency control
    float noiseScale = 0.05f; // Lower = larger shapes, higher = finer detail

    boolean waveMode = false;

    TuioClient tracker;

    public Scene01Intro(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        noiseDetail(4);
        // p.smooth(4);
        updateGrid();

        PFont font = createFont("Arial", 16);

        int sliderWidth = 250;
        int sliderHeight = 30;
        int x = 30;
        int y = 30;
        int gap = 50;
        floorHeightInteraction = height();
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

     // Draw
    @Override
    public void drawWall() {
        p.fill(0, alphaFade);
        p.noStroke();
        rect(0, 0, width(), height());

        stroke(255);
        noFill();
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();

        float zoff = p.frameCount * 0.01f;
         //---------------------------------------
         curtainProgress = lerp(curtainProgress, targetCurtainProgress, curtainSpeed);
         //---------------------------------------

        for (int x = 0; x < cols; x++) {
            if (foldCurtain) {
                curtainProgress += curtainSpeed;
            }

            ArrayList<PVector> shapePoints = new ArrayList<PVector>();
            beginShape();
            float xpos = x * spacing;
            p.curveVertex(xpos, 0);

            for (int y = 0; y < rows; y++) {
                float foldOffset = 0;
                if (foldCurtain) {
                    float foldFactor = constrain((float)x / cols, 0, 1);
                    float wave = sin((y + frameCount() * 0.3f) * 0.2f + x * 0.05f) * 20;
                    foldOffset = curtainProgress * 200 * foldFactor + wave * foldFactor;
                }

                float ypos = y * spacing;

                float n = noise(x * noiseScale, y * noiseScale, zoff);
                float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);
                //float influence = 0;

                //for(TuioCursor cursor: tuioCursorList) {
                //    if (cursor.getScreenY(height()) < floorHeightInteraction) {
                //        int px = cursor.getScreenX(this.width());
                //        int py = cursor.getScreenY(this.height());
                //        float strengthEffect = map(py,0,height(),1f,0.1f);

                //        float effectiveX = xpos + foldOffset;
                //        float dx = effectiveX - px;
                //        float d = dist(effectiveX, ypos, px, height() - personHeight);
                        


                //        //if (d < influenceRadius) {
                //            float strength = 1 - (d / influenceRadius) ;
                //            strength *= strength;
                //            float direction = dx > 0 ? 1 : -1;
                //            float direction2 = direction * sin(n);

                //            if (waveMode) {
                //                float wave = (2.0f * abs(2 * (n * 3.0f - floor(n * 3.0f + 0.1f))) - 1);
                //                direction2 += wave * 0.75;
                //            }

                //            influence = direction2 * strength * maxPush * strengthEffect;
                //        //}
                //    }
                //}
                
                float totalInfluence = 0;

for (TuioCursor cursor : tuioCursorList) {
    int px = cursor.getScreenX(this.width());
    int py = cursor.getScreenY(this.height());

    float strengthEffect = map(py, 0, height(), 1f, 0.1f);

    float effectiveX = xpos + foldOffset;
    float dx = effectiveX - px;
    float d = dist(effectiveX, ypos, px, height() - personHeight);

    float strength = 1 - (d / influenceRadius);
    if (strength > 0) {
        strength *= strength;
        float direction = dx > 0 ? 1 : -1;
        float direction2 = direction * sin(n);

        if (waveMode) {
            float wave = (2.0f * abs(2 * (n * 3.0f - floor(n * 3.0f + 0.1f))) - 1);
            direction2 += wave * 0.75;
        }

        totalInfluence += direction2 * strength * maxPush * strengthEffect;
    }
}

                
                

                float target = baseWave + totalInfluence;
                offsets[x][y] = lerp(offsets[x][y], target, lerpAmount);

                float px = xpos + offsets[x][y] + foldOffset;
                float py = ypos;

                curveVertex(px, py);
                shapePoints.add(new PVector(px, py));
            }
            endShape();
            if (cloneMirrored) {
                beginShape();
                for (PVector p : shapePoints) {
                    float mirroredX = width() - p.x;
                    curveVertex(mirroredX, p.y); 
                }
                endShape();
            }
        }
        System.out.println(frameRate());
    }

    @Override
    //public void drawFloor() {
    //    background(0);
    //    fill(255);
    //    noStroke();
    //    rect(0, 0, width(), floorHeightInteraction);
    //}
//@Override
public void drawFloor() {
    background(0);
    
   
    fill(255);
    noStroke();
    //rect(0, 0, width(), floorHeightInteraction);

    
    ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();
    fill(255); // White color
    noStroke();
    for (TuioCursor cursor : tuioCursorList) {
        float x = cursor.getScreenX(width());
        float y = cursor.getScreenY(height());
        float circleSize = map(y,0,height(),100,10);
        ellipse(x, y, circleSize, circleSize);  // Bigger circles (80x80)
    }
}


    public void keyPressed(char key, int keyCode) {
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
        if (key == PConstants.UP) {
            baseNoiseAmount += incDecBaseNoiseAmount;
        }
        
        if (keyCode == PConstants.DOWN) {
            baseNoiseAmount = Math.max(0, baseNoiseAmount - incDecBaseNoiseAmount);
        } 

      //influenceRadius
        if (keyCode == PConstants.RIGHT) {
            influenceRadius += incDecInfluenceAmount;
        }
        if (keyCode == PConstants.LEFT) {
            influenceRadius = Math.max(0, influenceRadius - incDecInfluenceAmount);
        } 

        //noise frequency
        if (p.key == 'z' || p.key == 'Z') {
            noiseScale = (float) Math.max(0.001, noiseScale - incDecNoiseAmount);  // decrease frequency (more stretched)
        } else if (p.key == 'x' || p.key == 'X') {
            noiseScale += incDecNoiseAmount; // increase frequency 
        }
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
                influenceRadius = map(value, 0, 1, 50, 500);
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