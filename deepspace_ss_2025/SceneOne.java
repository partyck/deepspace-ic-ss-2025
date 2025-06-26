import processing.core.PApplet;
import processing.core.PConstants;

public class SceneOne extends AbstractScene {
    private int timeElapsed;
    private int timeSteps;
    private final int animationTime;
    // Example: 0.0 = top, 0.5 = horizon, 1.0 = bottom
    int[] colors = {
        color(14,22,23),          // Deep navy (top) 0.0f
        color(77,141,143),        // turquoise light 0.25f
        color(255, 238, 195),     // light (horizon) 0.45f
        color(135, 160, 180),     // Deep navy (horizon) 0.5f
        color(255, 238, 195),     // light (horizon) 0.55f
        color(77,141,143),        // turquoise light 0.75f
        color(14,22,23)           // Deep navy (bottom) 1.0f
    };

    float[] stops = {0.0f, 0.15f, 0.45f, 0.5f, 0.55f, 0.85f, 1.0f};

    public SceneOne(PApplet p) {
        super(p);
        timeElapsed = 0;
        timeSteps = 1;
        animationTime = (int) (frameRate() * 60 * 3);
    }
    
    public void drawSeamlessGradient(PApplet p, float x, float y, float w, float h, int[] colors, float[] stops, float globalYOffset, float totalHeight) {
        loadPixels();
        int startX = Math.max((int)x, 0);
        int endX = Math.min((int)(x + w), width());
        int startY = Math.max((int)y, 0);
        int endY = Math.min((int)(y + h), height());

        for (int px = startX; px < endX; px++) {
            for (int py = startY; py < endY; py++) {
                float t = PApplet.map(py + globalYOffset, 0, totalHeight, 0, 1);
                t = t * t * (3 - 2 * t); // Smoothstep
                int gradColor = getColorAtPosition(p, colors, stops, t);
                p.pixels[py * width() + px] = gradColor;
            }
        }
        updatePixels();
    }

    public int getColorAtPosition(PApplet p, int[] colors, float[] stops, float t) {
        for (int i = 0; i < stops.length - 1; i++) {
            if (t >= stops[i] && t <= stops[i + 1]) {
                float localT = PApplet.map(t, stops[i], stops[i + 1], 0, 1);
                return lerpColor(colors[i], colors[i + 1], localT);
            }
        }
        return colors[colors.length - 1];
    }

    @Override
    public void drawWall() {
        background(30);  // Clear background

        float rectW = PApplet.lerp(width(), 0, animationProgress());
        float rectH = height();
        float x = (width() - rectW) / 2f;
        float y = 0;

        float totalHeight = height() * 2f;
        float offsetY = 0; // ← set this explicitly for wall

        drawSeamlessGradient(p, x, y, rectW, rectH, colors, stops, offsetY, totalHeight);

        update();
    }

    @Override
    public void drawFloor() {
        background(30);  // Clear background

        float rectW = PApplet.lerp(width(), 0, animationProgress());
        float rectH = height();
        float x = (width() - rectW) / 2f;
        float y = 0;

        float totalHeight = height() * 2f;
        float offsetY = height(); // ← shift for mirrored floor

        drawSeamlessGradient(p, x, y, rectW, rectH, colors, stops, offsetY, totalHeight);

        update();
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/closing/fader42":
                timeSteps = (int) (50 * value);
                System.out.println("    timeSteps: "+timeSteps);
                break;
        
        }
    }

    private float easeOutSeventh(float t) {
        return 1 - (float)Math.pow(1 - t, 7);
    }

    private float animationProgress() {
        float linearProgress = timeElapsed / (float) animationTime;
        return easeOutSeventh(linearProgress);
    }

    private void update() {
        timeElapsed += timeSteps;
        if (timeElapsed >= animationTime) timeElapsed = 0;
    }
}