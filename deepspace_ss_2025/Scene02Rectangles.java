import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import TUIO.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scene02Rectangles extends AbstractScene {
    private TuioClient tracker;
    private ArrayList<SceneRect> rects;
    private int animStage = 0;
    private boolean isExtended = false;
    private boolean isFollow = false;
    private boolean isClosing = false;
    private boolean showTrace = false;
    private boolean hideExceptSecond = false;

    private static final int NUM_RECTS = 7;
    private static final float ANIM_DURATION_FRMS = 120f;
    // faster deformation (~10 seconds at 60 FPS)
    private static final float DEFORM_DURATION_FRMS = 600f;

    private final float wallTargetW;
    private final float wallTargetH;
    private final float baselineY;

    // trace history per cursor ID
    private Map<Integer, ArrayList<PVector>> traces;
    private static final int MAX_TRACE = 200;

    public Scene02Rectangles(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        this.wallTargetW = p.width / 10f;
        this.wallTargetH = (p.height / 10f) * 6f;
        this.baselineY = p.height;
        initRectangles();
        traces = new HashMap<>();
    }

    private void initRectangles() {
        rects = new ArrayList<>();
        float gap = p.width / (NUM_RECTS + 1f);
        
        // Soft, atmospheric color pairs
        int[][] colorPairs = {
            {p.color(255, 160, 172), p.color(235, 109, 23), p.color(235, 109, 23)}, // pink orange
            {p.color(100, 150, 255), p.color(12, 39, 183), p.color(12, 39, 183)}, // Cool blue 100, 150, 255
            {p.color(183, 211, 172), p.color(18, 181, 163), p.color(18, 181, 163)}, // türkis
            {p.color(255, 180, 200), p.color(100, 150, 255), p.color(100, 150, 255)}, // blue 
            {p.color(200, 180, 255), p.color(97, 116, 150), p.color(97, 116, 150)}, // Lavender
            {p.color(200, 255, 200), p.color(235, 109, 23), p.color(235, 109, 23)}, // Golden
            {p.color(180, 220, 200), p.color(170, 111, 111), p.color(170, 111, 111)}  // Mint
        };
        
        for (int i = 0; i < NUM_RECTS; i++) {
            int[] colors = colorPairs[i % colorPairs.length];
            rects.add(new SceneRect(gap * (i + 1), baselineY, wallTargetW, wallTargetH, 
                                colors[0], colors[1], colors[2]));
        }
    }

    @Override
    public void drawWall() {
        p.background(0);
        for (int i = 0; i < rects.size(); i++) {
            SceneRect r = rects.get(i);
            r.animateIn();
            r.animateDeform();
            if (isFollow && i != 1) r.updateFollow(tracker.getTuioCursorList(), p.width, p.height);
        }
        p.noStroke(); p.fill(255);
        if (hideExceptSecond) {
            rects.get(1).draw(); // Only draw the second rectangle
        } else {
            for (SceneRect r : rects) r.draw();
        }

        if (showTrace) drawTraces(tracker.getTuioCursorList(), true);
    }

    @Override
    public void drawFloor() {
        p.background(0);
        if (!isExtended) return;

        for (int i = 0; i < rects.size(); i++) {
            SceneRect r = rects.get(i);
            r.animateIn();
            r.animateDeform();
            if (isFollow && i != 1) r.updateFollow(tracker.getTuioCursorList(), p.width, p.height);
        }

        if (hideExceptSecond) {
            rects.get(1).drawForFloor();
        } else {
            for (SceneRect r : rects) r.drawForFloor();
        }

        if (showTrace) drawTraces(tracker.getTuioCursorList(), false);
    }

    private void drawTraces(ArrayList<TuioCursor> cursors, boolean mirrored) {
        // update history
        for (TuioCursor c : cursors) {
            int id = c.getCursorID();
            float cx = c.getScreenX(p.width);
            float cy = c.getScreenY(p.height);
            ArrayList<PVector> list = traces.getOrDefault(id, new ArrayList<>());
            if (mirrored) cy = p.height - cy;
            list.add(new PVector(cx, cy));
            if (list.size() > MAX_TRACE) list.remove(0);
            traces.put(id, list);
        }
        // draw pixelated traces
        for (ArrayList<PVector> list : traces.values()) {
            for (PVector v : list) {
                boolean insideAny = false;
                for (SceneRect r : rects) {
                    if (r.contains(v.x, mirrored ? p.height-v.y : v.y)) { insideAny = true; break; }
                }
                p.fill(insideAny ? 0 : 255);
                p.noStroke();
                p.rect(v.x-3, v.y-3, 6, 6);
            }
        }
    }

    @Override
    public void oscEvent(String path, float value) {
        switch (path) {
            case "/rect/push22": triggerNextAnimStage(); break;
            case "/rect/push24": isExtended = true; for (SceneRect r : rects) r.startIn(); break;
            case "/rect/push26":
                // assign new targets
                rects.get(0).setTarget(wallTargetW * 0.4f, wallTargetH * 1.0f);
                rects.get(1).setTarget(wallTargetW * 1.5f, wallTargetH * 0.2f);
                rects.get(2).setTarget(wallTargetW * 0.8f, wallTargetH * 0.6f);
                rects.get(3).setTarget(wallTargetW * 0.6f, wallTargetH * 1.5f);
                rects.get(4).setTarget(wallTargetW * 1.2f, wallTargetH * 0.7f);
                rects.get(5).setTarget(wallTargetW * 0.7f, wallTargetH * 1.2f);
                rects.get(6).setTarget(wallTargetW * 1.0f, wallTargetH * 0.9f);
                for (SceneRect r : rects) r.startDeform();
                break;
            case "/rect/push32": isFollow = true; break;
            // case "/rect/c": for (SceneRect r : rects) r.close(); break;
            case "/rect/toggle7": 
                showTrace = value == 1;
                if (!showTrace) traces.clear();
                break;
            case "/rect/push38": hideExceptSecond = true; break;
        }
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        char k = Character.toLowerCase(key);
        switch (k) {
            case 'a': triggerNextAnimStage(); break;
            case 't': isExtended = true; for (SceneRect r : rects) r.startIn(); break;
            case 'd':
                // assign new targets
                rects.get(0).setTarget(wallTargetW * 0.4f, wallTargetH * 1.0f);
                rects.get(1).setTarget(wallTargetW * 1.5f, wallTargetH * 0.2f);
                rects.get(2).setTarget(wallTargetW * 0.8f, wallTargetH * 0.6f);
                rects.get(3).setTarget(wallTargetW * 0.6f, wallTargetH * 1.5f);
                rects.get(4).setTarget(wallTargetW * 1.2f, wallTargetH * 0.7f);
                rects.get(5).setTarget(wallTargetW * 0.7f, wallTargetH * 1.2f);
                rects.get(6).setTarget(wallTargetW * 1.0f, wallTargetH * 0.9f);
                for (SceneRect r : rects) r.startDeform();
                break;
            case 'f': isFollow = true; break;
            case 'c': for (SceneRect r : rects) r.close(); break;
            case 'p': showTrace = !showTrace; if (!showTrace) traces.clear(); break;
            case 'h': hideExceptSecond = true; break;
        }
    }

    private void triggerNextAnimStage() {
        int center = NUM_RECTS / 2;
        switch (animStage) {
            case 0: rects.get(center).startIn(); break;
            case 1: rects.get(center-1).startIn(); rects.get(center+1).startIn(); break;
            case 2: rects.get(center-2).startIn(); rects.get(center+2).startIn(); break;
            case 3: rects.get(center-3).startIn(); rects.get(center+3).startIn(); break;
        }
        animStage++;
    }

    private class SceneRect {
        float x, baseY;
        float w = 0, h = 0;
        float targetW, targetH;
        boolean animInDone = false;
        int animStartFrame = -1;
        float startW, startH;
        boolean isDeforming = false;
        int deformStartFrame = -1;
        int assignedCursorId = -1;
        boolean closing = false;
        int closeFrame = 0;
        private int color1, color2, color3;
        private float noiseIntensity = 8f;
        private float noiseScale = 0.012f;

        SceneRect(float x, float baseY, float targetW, float targetH, int c1, int c2, int c3) {
            this.x = x; this.baseY = baseY;
            this.targetW = targetW; this.targetH = targetH;
            this.color1 = c1; this.color2 = c2; this.color3 = c3;
        }

        void setTarget(float tw, float th) { targetW = tw; targetH = th; }
        void startIn() { if (!animInDone) animStartFrame = p.frameCount; }
        void animateIn() {
            if (animStartFrame<0) return;
            float prog = PApplet.constrain((p.frameCount-animStartFrame)/ANIM_DURATION_FRMS,0,1);
            float eased = PApplet.sin(prog*PConstants.HALF_PI);
            w = PApplet.lerp(0,targetW,eased);
            h = PApplet.lerp(0,targetH,eased);
            if (prog>=1) animInDone=true;
        }
        void startDeform() { startW=w; startH=h; deformStartFrame=p.frameCount; isDeforming=true; }
        void animateDeform() {
            if (!isDeforming) return;
            float prog = PApplet.constrain((p.frameCount-deformStartFrame)/DEFORM_DURATION_FRMS,0,1);
            float eased = PApplet.sin(prog*PConstants.HALF_PI);
            w = PApplet.lerp(startW,targetW,eased);
            h = PApplet.lerp(startH,targetH,eased);
            if (prog>=1) isDeforming=false;
        }
        void close() { closing=true; closeFrame=p.frameCount; }
        void animateClose() {
            float prog = PApplet.constrain((p.frameCount-closeFrame)/ANIM_DURATION_FRMS,0,1);
            float eased = 1-PApplet.sin(prog*PConstants.HALF_PI);
            w = PApplet.lerp(targetW,0,eased);
            h = PApplet.lerp(targetH,0,eased);
        }
        boolean contains(float px,float py) {
            float left = x-w/2, right=x+w/2;
            float top = baseY-h, bottom=baseY;
            return px>=left&&px<=right&&py>=top&&py<=bottom;
        }
        void updateFollow(ArrayList<TuioCursor> cursors,int sw,int sh) {
            if (assignedCursorId>=0) {
                for (TuioCursor c: cursors) {
                    if (c.getCursorID()==assignedCursorId) {
                        x=c.getScreenX(sw);
                        baseY=c.getScreenY(sh)+h/2;
                        return;
                    }
                }
                assignedCursorId=-1;
            }
            for (TuioCursor c: cursors) {
                float cx=c.getScreenX(sw), cy=c.getScreenY(sh);
                if (contains(cx,cy)) { assignedCursorId=c.getCursorID(); return; }
            }
        }

        void drawSoftGradient() {
            if (w <= 0 || h <= 0) return;

            p.loadPixels();
            float time = p.frameCount * 0.002f;
            float x0 = x - w/2;
            float y0 = baseY - h;

            for (int py = (int)y0; py < baseY; py++) {
                for (int px = (int)x0; px < x0 + w; px++) {
                    if (px < 0 || px >= p.width || py < 0 || py >= p.height) continue;
                    float t = PApplet.map(py, y0, y0 + h, 0, 1);
                    t = smoothstep(t);
                    int gradCol;
                    float split = 0.99f;
                    if (t < split) {
                        float tt = t / split;
                        gradCol = p.lerpColor(color1, color2, tt);
                    } else {
                        float tt = (t - split) / (1.0f - split);
                        gradCol = p.lerpColor(color2, color3, tt);
                    }
                    float n = p.noise(px * noiseScale, py * noiseScale, time);
                    float grain = PApplet.map(n, 0, 1, -noiseIntensity, noiseIntensity);
                    int r = PApplet.constrain((int)(p.red(gradCol) + grain), 0, 255);
                    int g = PApplet.constrain((int)(p.green(gradCol) + grain), 0, 255);
                    int b = PApplet.constrain((int)(p.blue(gradCol) + grain), 0, 255);
                    p.pixels[py * p.width + px] = p.color(r, g, b);
                }
            }
            p.updatePixels();
        }

        private float smoothstep(float t) {
            return t * t * (3 - 2 * t);
        }

        void addSoftGlow() {
            p.fill(p.red(color1), p.green(color1), p.blue(color1), 30);
            p.noStroke();
            p.rectMode(PConstants.CORNER);
            float glowSize = 8f;
            p.rect(x - w/2 - glowSize, baseY - h - glowSize, w + glowSize*2, h + glowSize*2);
        }

        void drawForFloor() {
            if (closing) animateClose();
            if (w <= 0 || h <= 0) return;
            float x0 = x - w/2;
            float y0 = 0;

            p.loadPixels();
            float time = p.frameCount * 0.002f;
            for (int py = (int)y0; py < h; py++) {
                for (int px = (int)x0; px < x0 + w; px++) {
                    if (px < 0 || px >= p.width || py < 0 || py >= p.height) continue;
                    float t = PApplet.map(py, y0, y0 + h, 1, 0);
                    t = smoothstep(t);
                    int gradCol;
                    float split = 0.99f; // 70% for first gradient, 30% for second
                    if (t < split) {
                        float tt = t / split;
                        gradCol = p.lerpColor(color1, color2, tt);
                    } else {
                        float tt = (t - split) / (1.0f - split);
                        gradCol = p.lerpColor(color2, color3, tt);
                    }
                    float n = p.noise(px * noiseScale, (y0 + h - (py - y0)) * noiseScale, time);
                    float grain = PApplet.map(n, 0, 1, -noiseIntensity, noiseIntensity);
                    int r = PApplet.constrain((int)(p.red(gradCol) + grain), 0, 255);
                    int g = PApplet.constrain((int)(p.green(gradCol) + grain), 0, 255);
                    int b = PApplet.constrain((int)(p.blue(gradCol) + grain), 0, 255);
                    p.pixels[py * p.width + px] = p.color(r, g, b);
                }
            }
            p.updatePixels();

            float glowSize = 8f;
            p.fill(p.red(color1), p.green(color1), p.blue(color1), 30);
            p.noStroke();
            p.rectMode(PConstants.CORNER);
            p.rect(x0 - glowSize, y0 - glowSize, w + glowSize*2, h + glowSize*2);
        }

        void draw() {
            if (closing) animateClose();
            drawSoftGradient();
            addSoftGlow();
        }
    }
}
