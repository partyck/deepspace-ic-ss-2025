import processing.core.PApplet;

public class Scene01_Intro_v1 extends AbstractScene {
    private int   timeElapsed;
    private final int animationTime;

    // — gradient & layer styling —
    private final int layers   = 12;
    private       int startCol, endCol;
    private       float animOff = 0f;
    private       float speed   = 0.02f;

    public Scene01_Intro_v1(PApplet p) {
        super(p);
        this.timeElapsed   = 0;
        this.animationTime = 200;
        this.startCol      = color(255, 150, 10);
        this.endCol        = color(180,   2, 65);
    }

    // TOP HALF (wall)
    @Override
    public void drawWall() {
        float centerX = this.width() * 0.5f;   // always middle of screen
        float centerY = this.height();        // bottom-edge of top window
        display(centerX, centerY);
    }

    // BOTTOM HALF (floor)
    @Override
    public void drawFloor() {
        float centerX = this.width() * 0.5f;   // same middle
        float centerY = 0;                    // top-edge of bottom window
        display(centerX, centerY);
    }

    private void display(float centerX, float centerY) {
        // clear
        background(30);
        noStroke();

        // advance the little wiggle
        animOff += speed;

        // build a “height” that spans both windows
        float totalH   = height() * 2f;
        float progress = timeElapsed / (float)animationTime;
        float rectH    = lerp(totalH, 0, progress);
        float halfH    = rectH * 0.5f;

        // draw your concentric layers around that big center
        for (int i = 0; i < layers; i++) {
            float t = map(i, 0, layers - 1, 0, 1);
            fill( lerpColor(startCol, endCol, t) );

            // Y-margins shrink from halfH down
            float yM = map(i + sin(animOff + i * 0.2f), 0, layers, 0, halfH);
            // X-margins just center in each window’s width
            float xM = map(i + cos(animOff + i * 0.2f), 0, layers, 0, width() * 0.5f);

            // carve out the slice for this window:
            rect(
                centerX - width() * 0.5f + xM,    // left
                centerY - halfH + yM,            // top
                width()  - 2 * xM,               // w
                rectH    - 2 * yM                // h
            );
        }

        // step & wrap
        timeElapsed++;
        if (timeElapsed >= animationTime) timeElapsed = 0;
    }
}
