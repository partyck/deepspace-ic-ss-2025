import processing.core.PApplet;
import processing.core.PConstants;

public class SceneOne extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;
    // Example: 0.0 = top, 0.5 = horizon, 1.0 = bottom
    int[] colorsWall = {
        p.color(14,22,23),          // Deep navy (top) 0.0f
        p.color(77,141,143),        // turquoise light 0.25f
        p.color(255, 238, 195),     // light (horizon) 0.45f
        p.color(135, 160, 180),     // Deep navy (horizon) 0.5f
    };
    int[] colorsFloor = {
        p.color(135, 160, 180),     // Deep navy (horizon) 0.5f
        p.color(255, 238, 195),     // light (horizon) 0.55f
        p.color(77,141,143),        // turquoise light 0.75f
        p.color(14,22,23)           // Deep navy (bottom) 1.0f
    };

    // float[] stopsW = {0.0f, 0.15f, 0.45f, 0.5f};
    // float[] stopsF = {1f, 0.55f, 0.85f, 1.0f};
    float[] stopsW = {0.0f, 0.3f, 0.9f, 1.2f};
    float[] stopsF = {-0.2f, 0.1f, 0.7f, 1.0f};

    public SceneOne(PApplet p) {
        super(p);
        this.timeElapsed = 0;
        this.animationTime = (int) (frameRate() * 60 * 1);
    }
    
    public void drawSeamlessGradient(PApplet p, float x, float y, float w, float h, int[] colors, float[] stops) {
        p.loadPixels();
        int startX = Math.max((int)x, 0);
        int endX = Math.min((int)(x + w), p.width);
        int startY = Math.max((int)y, 0);
        int endY = Math.min((int)(y + h), p.height);

        for (int px = startX; px < endX; px++) {
            for (int py = startY; py < endY; py++) {
                float t = PApplet.map(py, 0, height(), 0, 1);
                t = t * t * (3 - 2 * t); // Smoothstep
                int gradColor = getColorAtPosition(p, colors, stops, t);
                p.pixels[py * p.width + px] = gradColor;
            }
        }
        p.updatePixels();
    }

    public int getColorAtPosition(PApplet p, int[] colors, float[] stops, float t) {
        for (int i = 0; i < stops.length - 1; i++) {
            if (t >= stops[i] && t <= stops[i + 1]) {
                float localT = PApplet.map(t, stops[i], stops[i + 1], 0, 1);
                return p.lerpColor(colors[i], colors[i + 1], localT);
            }
        }
        return colors[colors.length - 1];
    }

    @Override
    public void drawWall() {
        p.background(30);  // Clear background

        float rectW = PApplet.lerp(this.width(), 0, animationProgress());
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;

        drawSeamlessGradient(p, x, y, rectW, rectH, colorsWall, stopsW);

        update();
    }

    @Override
    public void drawFloor() {
        p.background(30);  // Clear background

        float rectW = PApplet.lerp(this.width(), 0, animationProgress());
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;

        // rect(x, y, rectW, rectH);

        drawSeamlessGradient(p, x, y, rectW, rectH, colorsFloor, stopsF);

        update();
        System.out.println(animationTime +" elapsed: "+this.timeElapsed + "; "+ (float) (timeElapsed / (float) animationTime) + " rect w: " + rectW);
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
        // if (this.timeElapsed >= this.animationTime) ;
    }
}