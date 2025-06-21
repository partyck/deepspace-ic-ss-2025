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

    private void drawSmoothGradient(float x, float y, float w, float h, int c1, int c2, boolean reversed) {
        p.loadPixels();
        
        for (int px = (int)x; px < x + w; px++) {
            for (int py = (int)y; py < y + h; py++) {
                if (px < 0 || px >= p.width || py < 0 || py >= p.height) continue;
                
                // Vertical gradient calculation
                float t = PApplet.map(py, y, y + h, 0, 1);
                if (reversed) t = 1 - t; // Flip the interpolation
                t = smoothstep(t);
                
                int gradColor = p.lerpColor(c1, c2, t);
                p.pixels[py * p.width + px] = gradColor;
            }
        }
        p.updatePixels();
    }

    // Smooth interpolation function
    private float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }


    @Override
    public void drawWall() {
        display(true); // Reversed gradient for wall
    }

    @Override
    public void drawFloor() {
        display(false); // Normal gradient for floor
    }

    private void display(boolean reversed) {
        background(30);
        noStroke();

        float rectW = lerp(this.width(), 0, animationProgress());
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;

        // Define base colors
        int coolColor = p.color(174, 198, 207); //p.color(139, 157, 195);   // Cool blue-gray
        int warmColor =  p.color(255, 183, 147); //p.color(244, 209, 174);   // Warm peach

        drawSmoothGradient(x, y, rectW, rectH, warmColor, coolColor, reversed);

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
