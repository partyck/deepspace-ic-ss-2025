import processing.core.PApplet;

public class Scene01_Intro extends AbstractScene {
    private int   timeElapsed;
    private final int animationTime;

    // — gradient & layer styling —
    private final int layers    = 12;
    private       int startCol, endCol;
    private       float animOff = 0f;
    private       float speed   = 0.02f;

    public Scene01_Intro(PApplet p) {
        super(p);
        this.timeElapsed   = 0;
        this.animationTime = 200;
        this.startCol      = color(255, 150, 10);
        this.endCol        = color(180,   2, 65);
    }

    // TOP WINDOW: show the **top half** of our tall tunnel
    @Override
    public void drawWall() {
        float centerX = p.width  * 0.5f;   // horizontal center
        float centerY = p.height;         // bottom edge of top window
        display(centerX, centerY);
    }

    // BOTTOM WINDOW: show the **bottom half**
    @Override
    public void drawFloor() {
        float centerX = p.width  * 0.5f;   // same horizontal center
        float centerY = 0;                // top edge of bottom window
        display(centerX, centerY);
    }

    private void display(float centerX, float centerY) {
        p.background(30);
        p.noStroke();
        animOff += speed;

        // — combined canvas is twice as tall as one window —
        float totalH   = p.height * 2f;
        float progress = timeElapsed / (float)animationTime;
        // shrink **width** from full-window to zero, pulled into the shared center
        float rectW    = p.lerp(p.width, 0, progress);
        float halfW    = rectW * 0.5f;

        for (int i = 0; i < layers; i++) {
            float t = p.map(i, 0, layers - 1, 0, 1);
            p.fill(p.lerpColor(startCol, endCol, t));

            // how far in from each side…
            float xM = p.map(i + p.sin(animOff + i * 0.2f), 0, layers, 0, halfW);
            // and how much vertical “bleed” into the tall canvas
            float yM = p.map(i + p.cos(animOff + i * 0.2f), 0, layers, 0, totalH * 0.5f);

            // carve out just this layer’s slice for whichever window we’re in
            float x = centerX - halfW + xM;
            float y = centerY - totalH * 0.5f + yM - 50;
            float w = rectW - 2 * xM;
            float h = totalH - 2 * yM;

            p.rect(x, y, w, h);
        }

        // loop that animation
        timeElapsed++;
        if (timeElapsed >= animationTime) timeElapsed = 0;
    }
}
