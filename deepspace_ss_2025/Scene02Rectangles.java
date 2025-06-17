import processing.core.PApplet;
import processing.core.PConstants;
import TUIO.*;
import java.util.ArrayList;

public class Scene02Rectangles extends AbstractScene {
    private TuioClient tracker;
    private ArrayList<SceneRect> rects;
    private int animStage = 0;
    private boolean isExtended = false;
    private boolean isFollow = false;
    private boolean isClosing = false;

    private static final int NUM_RECTS = 7;
    private static final float ANIM_DURATION_FRMS = 120f;
    private static final float DEFORM_DURATION_FRMS = 60f;

    private final float wallTargetW;
    private final float wallTargetH;
    private final float baselineY;

    public Scene02Rectangles(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        this.wallTargetW = p.width / 10f;
        this.wallTargetH = (p.height / 10f) * 6f;
        this.baselineY = p.height;
        initRectangles();
    }

    private void initRectangles() {
        rects = new ArrayList<>();
        float gap = p.width / (NUM_RECTS + 1f);
        for (int i = 0; i < NUM_RECTS; i++) {
            rects.add(new SceneRect(gap * (i + 1), baselineY, wallTargetW, wallTargetH));
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

        // update animations
        for (SceneRect r : rects) {
            r.animateIn();
            r.animateDeform();
            if (isFollow) r.updateFollow(tracker.getTuioCursorList(), p.width, p.height);
        }
        p.noStroke(); p.fill(255);
        for (SceneRect r : rects) r.draw();
    }

    @Override
    public void drawFloor() {
        if (!isExtended) return;
        p.background(0);
        // update animations
        for (SceneRect r : rects) {
            r.animateIn();
            r.animateDeform();
            if (isFollow) r.updateFollow(tracker.getTuioCursorList(), p.width, p.height);
        }
        p.pushMatrix();
        p.translate(0, p.height);
        p.scale(1, -1);
        p.noStroke(); p.fill(255);
        for (SceneRect r : rects) r.draw();
        p.popMatrix();
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        char k = Character.toLowerCase(key);
        switch (k) {
            case 'a':
                triggerNextAnimStage();
                break;
            case 't':
                isExtended = true;
                for (SceneRect r : rects) r.startIn();
                break;
            case 'd':
                // assign new targets
                rects.get(0).setTarget(wallTargetW * 0.5f, wallTargetH * 1.0f);
                rects.get(1).setTarget(wallTargetW * 1.5f, wallTargetH * 0.2f);
                rects.get(2).setTarget(wallTargetW * 0.8f, wallTargetH * 0.6f);
                rects.get(3).setTarget(wallTargetW * 0.6f, wallTargetH * 1.5f);
                rects.get(4).setTarget(wallTargetW * 1.2f, wallTargetH * 0.7f);
                rects.get(5).setTarget(wallTargetW * 0.7f, wallTargetH * 1.2f);
                rects.get(6).setTarget(wallTargetW * 1.0f, wallTargetH * 0.9f);
                for (SceneRect r : rects) r.startDeform();
                break;
            case 'f':
                isFollow = true;
                break;
            case 'c':
                for (SceneRect r : rects) r.close();
                break;
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
        boolean animInDone = false;
        int animStartFrame = -1;

        // deformation
        float startW, startH;
        boolean isDeforming = false;
        int deformStartFrame = -1;

        // follow
        int assignedCursorId = -1;

        // closing
        boolean closing = false;
        int closeFrame = 0;

        SceneRect(float x, float baseY, float targetW, float targetH) {
            this.x = x;
            this.baseY = baseY;
            this.targetW = targetW;
            this.targetH = targetH;
        }

        void setTarget(float tw, float th) {
            this.targetW = tw;
            this.targetH = th;
        }

        void startIn() {
            if (animInDone) return;
            animStartFrame = p.frameCount;
        }

        void animateIn() {
            if (animStartFrame < 0) return;
            int t = p.frameCount - animStartFrame;
            float prog = PApplet.constrain(t / ANIM_DURATION_FRMS, 0, 1);
            float eased = PApplet.sin(prog * PConstants.HALF_PI);
            w = PApplet.lerp(0, targetW, eased);
            h = PApplet.lerp(0, targetH, eased);
            if (prog >= 1) animInDone = true;
        }

        void startDeform() {
            this.startW = this.w;
            this.startH = this.h;
            this.deformStartFrame = p.frameCount;
            this.isDeforming = true;
        }

        void animateDeform() {
            if (!isDeforming) return;
            int t = p.frameCount - deformStartFrame;
            float prog = PApplet.constrain(t / DEFORM_DURATION_FRMS, 0, 1);
            float eased = PApplet.sin(prog * PConstants.HALF_PI);
            w = PApplet.lerp(startW, targetW, eased);
            h = PApplet.lerp(startH, targetH, eased);
            if (prog >= 1) isDeforming = false;
        }

        void close() {
            closing = true;
            closeFrame = p.frameCount;
        }

        void animateClose() {
            int t = p.frameCount - closeFrame;
            float prog = PApplet.constrain(t / ANIM_DURATION_FRMS, 0, 1);
            float eased = 1 - PApplet.sin(prog * PConstants.HALF_PI);
            w = PApplet.lerp(targetW, 0, eased);
            h = PApplet.lerp(targetH, 0, eased);
        }

        /**
         * Assigns this rectangle to the first cursor that touches it, then follows that cursor.
         */
        void updateFollow(ArrayList<TuioCursor> cursors, int sw, int sh) {
            // if already assigned, follow that cursor
            if (assignedCursorId >= 0) {
                for (TuioCursor c : cursors) {
                    if (c.getCursorID() == assignedCursorId) {
                        x = c.getScreenX(sw);
                        baseY = c.getScreenY(sh) + h/2;
                        return;
                    }
                }
                assignedCursorId = -1;
            }
            // assign first cursor that touches
            for (TuioCursor c : cursors) {
                float cx = c.getScreenX(sw);
                float cy = c.getScreenY(sh);
                float left = x - w/2;
                float right = x + w/2;
                float top = baseY - h;
                float bottom = baseY;
                if (cx >= left && cx <= right && cy >= top && cy <= bottom) {
                    assignedCursorId = c.getCursorID();
                    return;
                }
            }
        }

        void draw() {
            p.rectMode(PConstants.CORNER);
            p.rect(x - w/2, baseY - h, w, h);
        }
    }
}
