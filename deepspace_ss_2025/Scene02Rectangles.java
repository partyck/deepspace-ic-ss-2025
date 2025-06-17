import processing.core.PApplet;
import processing.core.PConstants;
import TUIO.*;
import java.util.ArrayList;

public class Scene02Rectangles extends AbstractScene {
    private TuioClient tracker;
    private ArrayList<SceneRect> rects;

    // Animation stage control
    private int animStage = 0;
    private static final int NUM_STAGES = 4;

    // Other states
    private boolean isExtended   = false;
    private boolean isDeformed   = false;
    private boolean isFollow     = false;
    private boolean isClosing    = false;

    // Parameters
    private static final int   NUM_RECTS       = 7;
    private static final float ANIM_DELAY_BASE = 15f;

    // Dimensions
    private final float wallTargetW;
    private final float wallTargetH;

    public Scene02Rectangles(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        this.wallTargetW = p.width  / 10f;
        this.wallTargetH = (p.height / 10f) * 6f;
        initRectangles();
    }

    private void initRectangles() {
        rects = new ArrayList<>();
        float gap = p.width / (NUM_RECTS + 1f);
        for (int i = 0; i < NUM_RECTS; i++) {
            float x = gap * (i + 1);
            float y = p.height - wallTargetH; // bottom-of-wall origin: translated in drawWall
            rects.add(new SceneRect(x, y, wallTargetW, wallTargetH, i));
        }
    }

    /**
     * Always update any rectangles flagged to animate in
     */
    private void animateInSequence() {
        for (SceneRect r : rects) r.animateIn();
    }

    private void extendToFloor() {
        isExtended = true;
    }

    private void deformRectangles() {
        isDeformed = true;
        for (SceneRect r : rects) r.deform();
    }

    private void enableFollow() {
        isFollow = true;
    }

    private void closeRectangles() {
        isClosing = true;
        for (SceneRect r : rects) r.close();
    }

    @Override
    public void drawWall() {
        p.background(0);
        p.fill(255);
        p.textSize(24);
        String title = "SCENE RECTANGLES";
        float tw = p.textWidth(title);
        p.text(title, p.width - tw - 20, 40);

        // always update animations
        animateInSequence();

        p.pushMatrix();
        // origin (0,0) at bottom of wall
        // p.translate(0, p.height);
        displayRects();
        p.popMatrix();
    }

    @Override
    public void drawFloor() {
        // background(0);
        if (!isExtended) return;
        displayRects();
    }

    private void displayRects() {
        p.noStroke();
        p.fill(255);
        ArrayList<TuioCursor> cursors = tracker.getTuioCursorList();

        for (SceneRect r : rects) {
            if (isFollow) r.followCursor(cursors, p.width, p.height);
            if (isClosing) r.animateClose();
            r.draw();
        }
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        switch (Character.toLowerCase(key)) {
            case 'a': triggerNextAnimStage(); break;
            case 't': extendToFloor();       break;
            case 'd': deformRectangles();    break;
            case 'f': enableFollow();        break;
            case 'c': closeRectangles();     break;
        }
    }

    /**
     * On each 'a' press, animate the next group: center, 1-away, 2-away, 3-away
     */
    private void triggerNextAnimStage() {
        int center = NUM_RECTS / 2;
        switch (animStage) {
            case 0:
                rects.get(center).shouldAnimateIn = true;
                break;
            case 1:
                rects.get(center - 1).shouldAnimateIn = true;
                rects.get(center + 1).shouldAnimateIn = true;
                break;
            case 2:
                rects.get(center - 2).shouldAnimateIn = true;
                rects.get(center + 2).shouldAnimateIn = true;
                break;
            case 3:
                rects.get(center - 3).shouldAnimateIn = true;
                rects.get(center + 3).shouldAnimateIn = true;
                break;
            default:
                return;
        }
        animStage++;
    }

    private class SceneRect {
        float x, y;
        float w, h;
        float targetW, targetH;
        int index;
        int frameOffset;

        // NEW: only animate in when flagged
        boolean shouldAnimateIn = false;
        boolean animInDone      = false;

        // Closing
        boolean closing    = false;
        int closeFrame     = 0;

        SceneRect(float x, float y, float targetW, float targetH, int idx) {
            this.x = x;
            this.y = y;
            this.targetW = targetW;
            this.targetH = targetH;
            this.w = 0;
            this.h = 0;
            this.index = idx;
            int center = NUM_RECTS / 2;
            this.frameOffset = Math.abs(idx - center);
        }

        void animateIn() {
            if (!shouldAnimateIn || animInDone) return;
            int delay = (int)(frameOffset * ANIM_DELAY_BASE);
            int t = p.frameCount - delay;
            if (t < 0) return;
            float prog = p.constrain(t / 60f, 0, 1);
            w = p.lerp(0, targetW, prog);
            h = p.lerp(0, targetH, prog);
            if (prog >= 1) animInDone = true;
        }

        void deform() {
            targetW *= p.random(0.7f, 1.3f);
            targetH *= p.random(0.7f, 1.3f);
        }

        void followCursor(ArrayList<TuioCursor> cursors, int sw, int sh) {
            for (TuioCursor c : cursors) {
                float cx = c.getScreenX(sw);
                float cy = c.getScreenY(sh);
                if (cx >= x - w/2 && cx <= x + w/2 &&
                    cy >= y - h/2 && cy <= y + h/2) {
                    x = cx;
                    y = cy;
                    break;
                }
            }
        }

        void close() {
            closing = true;
            closeFrame = p.frameCount;
        }

        void animateClose() {
            int t = p.frameCount - closeFrame;
            float prog = p.constrain(t / 60f, 0, 1);
            w = p.lerp(targetW, 0, prog);
            h = p.lerp(targetH, 0, prog);
        }

        void draw() {
            // p.rectMode(PConstants.CENTER, 0);
            p.rect(x, y, w, h);
        }
    }
}
