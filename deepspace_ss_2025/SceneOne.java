import processing.core.PApplet;
import processing.core.PConstants;

public class SceneOne extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;
    // Example: 0.0 = top, 0.5 = horizon, 1.0 = bottom
    int[] colors = {
        p.color(10, 20, 60),    // Deep navy (top)
        p.color(135, 160, 180),   // Dark blue
        p.color(235, 109, 23), // Light blue
        p.color(255, 200, 120), // Orange (horizon)
        p.color(235, 109, 23), // Light blue (reflection)
        p.color(135, 160, 180),   // Dark blue (bottom)
        p.color(10, 20, 60)     // Deep navy (bottom)
    };

            // p.color(135, 160, 180), // Light blue

    float[] stops = {0.0f, 0.25f, 0.45f, 0.5f, 0.55f, 0.75f, 1.0f};

    public SceneOne(PApplet p) {
        super(p);
        this.timeElapsed = 0;
        this.animationTime = (int) (frameRate() * 60 * 3);
    }
    
    private int getColorAtPosition(int[] colors, float[] stops, float t) {
        for (int i = 0; i < stops.length - 1; i++) {
            if (t >= stops[i] && t <= stops[i + 1]) {
                float localT = PApplet.map(t, stops[i], stops[i + 1], 0, 1);
                return p.lerpColor(colors[i], colors[i + 1], localT);
            }
        }
        return colors[colors.length - 1];
    }

    private float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }

    private void drawSeamlessGradient(float x, float y, float w, float h, int[] colors, float[] stops, float globalYOffset, float totalHeight) {
        p.loadPixels();
        int startX = Math.max((int)x, 0);
        int endX = Math.min((int)(x + w), p.width);
        int startY = Math.max((int)y, 0);
        int endY = Math.min((int)(y + h), p.height);

        for (int px = startX; px < endX; px++) {
            for (int py = startY; py < endY; py++) {
                float t = PApplet.map(py + globalYOffset, 0, totalHeight, 0, 1);
                t = smoothstep(t);
                int gradColor = getColorAtPosition(colors, stops, t);
                p.pixels[py * p.width + px] = gradColor;
            }
        }
        p.updatePixels();
    }

    @Override
    public void drawWall() {
        p.background(30);  // Clear background
        
        // RESTORED: Calculate animated dimensions
        float rectW = p.lerp(this.width(), 0, animationProgress());
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;
        
        // RESTORED: Use animated dimensions
        float totalHeight = p.height * 2f;
        drawSeamlessGradient(x, y, rectW, rectH, colors, stops, 0, totalHeight);
        
        update();  // ADDED: Call update to advance animation
    }

    @Override
    public void drawFloor() {
        p.background(30);  // Clear background
        
        // RESTORED: Calculate animated dimensions
        float rectW = p.lerp(this.width(), 0, animationProgress());
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;
        
        // RESTORED: Use animated dimensions
        float totalHeight = p.height * 2f;
        drawSeamlessGradient(x, y, rectW, rectH, colors, stops, p.height, totalHeight);
        
        update();  // ADDED: Call update to advance animation
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
