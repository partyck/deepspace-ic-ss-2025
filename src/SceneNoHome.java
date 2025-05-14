import processing.core.PApplet;
import processing.core.PConstants;

public class SceneNoHome implements Scene {
    private final int w, fullH;
    private float animProgress = 0;

    public SceneNoHome(int w, int hHalf) {
        this.w = w;
        this.fullH = hHalf * 2;
    }

    @Override
    public void enter() { animProgress = 0; }

    @Override
    public void exit() { /* cleanup if needed: reset settings for changing scene */ }

    @Override
    public void update() {
        animProgress += 0.005f;
        if(animProgress > 1) animProgress = 0;
    }

    @Override
    public void render(PApplet p, float yOff, float alpha) {
        p.pushStyle();
        p.tint(255, alpha * 255);     // fade in/out
        p.pushMatrix();
        p.translate(0, yOff);

        // — start double-height drawing here —
        p.background(30);
        p.noStroke();
        p.fill(200);

        // calculate width shrinking from full w → 0, anchored center
        float rectW = PApplet.lerp(w, 0, animProgress);
        float rectH = fullH;          // full height

        // draw centered rectangle
        p.rectMode(PConstants.CENTER);
        p.rect(w/2f, fullH/2f, rectW, rectH);

        // reset to default (just in case other scenes rely on it)
        p.rectMode(PConstants.CORNER);
        // — end drawing —

        p.popMatrix();
        p.popStyle();
    }

    @Override
    public int getFullHeight() {
        return fullH;
    }
}
