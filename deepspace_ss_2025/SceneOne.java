import processing.core.PApplet;
import processing.core.PConstants;

public class SceneOne extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    public SceneOne(PApplet p) {
        super(p);
        this.timeElapsed = 0;
        this.animationTime = (int) (frameRate() * 60 * 3);
    }

    @Override
    public void drawWall() {
        display();
    }

    @Override
    public void drawFloor() {
        display();
    }

    private void drawGradientRect(float x, float y, float w, float h, int c1, int c2, int c3) {
        p.loadPixels();
        float time = p.frameCount * 0.01f; // For animated grain
        for (int i = (int)x; i < x + w; i++) {
            for (int j = (int)y; j < y + h; j++) {
                float t = PApplet.map(i, x, x + w, 0, 1); // Horizontal gradient

                int gradCol;
                if (t < 0.5f) {
                    // First half: interpolate c1 to c2
                    float t2 = t / 0.5f;
                    gradCol = p.lerpColor(c1, c2, t2);
                } else {
                    // Second half: interpolate c2 to c3
                    float t2 = (t - 0.5f) / 0.5f;
                    gradCol = p.lerpColor(c2, c3, t2);
                }

                // Add animated noise-based brightness
                float n = p.noise(i * 0.005f, j * 0.005f, time);
                float grain = PApplet.map(n, 0, 1, -100, 100);

                int r = PApplet.constrain((int)(p.red(gradCol) + grain), 0, 255);
                int g = PApplet.constrain((int)(p.green(gradCol) + grain), 0, 255);
                int b = PApplet.constrain((int)(p.blue(gradCol) + grain), 0, 255);

                int noisyCol = p.color(r, g, b);

                if (i >= 0 && i < p.width && j >= 0 && j < p.height) {
                    p.pixels[j * p.width + i] = noisyCol;
                }
            }
        }
        p.updatePixels();
    }

    private void display() {
        background(30);
        noStroke();

        float rectW = PApplet.lerp(this.width(), 0, animationProgress());
        float rectH = this.height();

        float x = (this.width() - rectW) / 2f;
        float y = 0;

        // nice orange blue gradient
        // int c1 = p.color(225, 86, 0);
        // int c2 = p.color(0, 219, 224);

        int c1 = p.color(225, 86, 0);
        int c2 = p.color(168, 224, 191);
        int c3 = p.color(255, 86, 0); // Example third color

        drawGradientRect(x, y, rectW, rectH, c1, c2, c3);


        this.update();
    }

    private float easeOutSeventh(float t) {
        return 1 - (float)Math.pow(1 - t, 7);
    }


    private float animationProgress() {
        float linearProgress = timeElapsed / (float) animationTime;
        return easeOutSeventh(linearProgress);
    }

    private void update() {
        this.timeElapsed++;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

}
