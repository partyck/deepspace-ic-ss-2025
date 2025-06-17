import processing.core.PApplet;
import processing.core.PConstants;
import TUIO.*;
import java.util.ArrayList;

// key 'a': triggerNextAnimStage();               
// key 't': isExtended = true;                    
// key 'd': for (SceneRect r : rects) r.deform(); 
// key 'f': isFollow   = true;                    
// key 'c': for (SceneRect r : rects) r.close();  

public class Scene02Rectangles extends AbstractScene {
    private TuioClient tracker;
    private ArrayList<SceneRect> rects;

    // Animation stage control
    private int animStage = 0;

    // Other states
    private boolean isExtended   = false;
    private boolean isDeformed   = false;
    private boolean isFollow     = false;
    private boolean isClosing    = false;

    // Parameters
    private static final int   NUM_RECTS         = 7;
    private static final float ANIM_DURATION_FRMS = 120f;

    // Dimensions & baseline for wall
    private final float wallTargetW;
    private final float wallTargetH;
    private final float baselineY;

    public Scene02Rectangles(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker      = tracker;
        this.wallTargetW  = p.width  / 10f;
        this.wallTargetH  = (p.height / 10f) * 6f;
        this.baselineY    = p.height;          // bottom of the window
        initRectangles();
    }

    private void initRectangles() {
        rects = new ArrayList<>();
        float gap = p.width / (NUM_RECTS + 1f);
        for (int i = 0; i < NUM_RECTS; i++) {
            float x = gap * (i + 1);
            rects.add(new SceneRect(x, baselineY, wallTargetW, wallTargetH));
        }
    }

    @Override
    public void drawWall() {
        p.background(0);
        p.fill(255);
        p.textSize(24);
        String title = "SCENE RECTANGLES";
        float tw = p.textWidth(title);
        p.text(title, p.width - tw - 20, 40);

        // Animate "in" for any rect started
        for (SceneRect r : rects) r.animateIn();

        // Draw wall rects at bottom of window
        p.noStroke(); p.fill(255);
        for (SceneRect r : rects) r.draw();
    }

    @Override
public void drawFloor() {
    // clear
    p.background(0);
    if (!isExtended) return;

    // ensure "in" animation runs here too, mirroring wall state
    for (SceneRect r : rects) r.animateIn();

    // Mirror the wall rectangles onto the floor by flipping vertically
    p.pushMatrix();
    // Move origin to bottom of window, then invert Y-axis
    p.translate(0, p.height);
    p.scale(1, -1);

    p.noStroke();
    p.fill(255);
    for (SceneRect r : rects) {
        // you could also r.animateClose(), r.deform(), r.followCursor(...) here as needed
        r.draw();
    }

    p.popMatrix();
}

    @Override
    public void keyPressed(char key, int keyCode) {
        switch (Character.toLowerCase(key)) {
            case 'a': triggerNextAnimStage();               break;
            case 't': isExtended = true;                    break;
            case 'd': for (SceneRect r : rects) r.deform(); break;
            case 'f': isFollow   = true;                    break;
            case 'c': for (SceneRect r : rects) r.close();  break;
        }
    }

    private void triggerNextAnimStage() {
        int center = NUM_RECTS / 2;
        switch (animStage) {
            case 0:
                rects.get(center).startIn();
                break;
            case 1:
                rects.get(center - 1).startIn();
                rects.get(center + 1).startIn();
                break;
            case 2:
                rects.get(center - 2).startIn();
                rects.get(center + 2).startIn();
                break;
            case 3:
                rects.get(center - 3).startIn();
                rects.get(center + 3).startIn();
                break;
        }
        animStage++;
    }

    private class SceneRect {
        float x, baseY;
        float w = 0, h = 0;
        float targetW, targetH;
        boolean animInDone    = false;
        int animStartFrame    = -1;

        // Closing
        boolean closing       = false;
        int closeFrame        = 0;

        SceneRect(float x, float baseY, float targetW, float targetH) {
            this.x       = x;
            this.baseY   = baseY;
            this.targetW = targetW;
            this.targetH = targetH;
        }

        void startIn() {
            if (animInDone) return;
            animStartFrame = p.frameCount;
        }

        void animateIn() {
            if (animInDone || animStartFrame < 0) return;
            int t = p.frameCount - animStartFrame;
            float prog = PApplet.constrain(t / ANIM_DURATION_FRMS, 0, 1);
            float eased = PApplet.sin(prog * PConstants.HALF_PI);
            w = PApplet.lerp(0, targetW, eased);
            h = p.lerp(0, targetH, eased);
            if (prog >= 1) animInDone = true;
        }

        void deform() {
            targetW *= p.random(0.7f, 1.3f);
            targetH *= p.random(0.7f, 1.3f);
        }

        void close() {
            closing = true;
            closeFrame = p.frameCount;
        }

        void animateClose() {
            int t = p.frameCount - closeFrame;
            float prog = p.constrain(t / ANIM_DURATION_FRMS, 0, 1);
            float eased = 1 - p.sin(prog * PConstants.HALF_PI);
            w = p.lerp(targetW, 0, eased);
            h = p.lerp(targetH, 0, eased);
        }

        void draw() {
            p.rectMode(PConstants.CORNER);
            p.rect(x - w/2, baseY - h, w, h);
        }
    }
}
