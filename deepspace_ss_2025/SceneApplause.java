import processing.core.PApplet;
import processing.core.PConstants;

public class SceneApplause extends AbstractScene {
    int[] colors = {
        p.color(0, 0, 0),          
        p.color(77,141,143),        
        p.color(255, 238, 195),     // Pale white-yellow 0.5f
        p.color(77,141,143),        // turquoise 0.75f
        p.color(0, 0, 0)   
    };

    float[] stops = {0.0f, 0.25f, 0.5f, 0.75f, 1.0f};

    public SceneApplause(PApplet p) {
        super(p);
    }
    
    public void drawSeamlessGradient(PApplet p, float x, float y, float w, float h, int[] colors, float[] stops, float globalYOffset, float totalHeight) {
        p.loadPixels();
        int startX = Math.max((int)x, 0);
        int endX = Math.min((int)(x + w), p.width);
        int startY = Math.max((int)y, 0);
        int endY = Math.min((int)(y + h), p.height);

        for (int px = startX; px < endX; px++) {
            for (int py = startY; py < endY; py++) {
                float t = PApplet.map(py + globalYOffset, 0, totalHeight, 0, 1);
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

        float rectW = this.width();
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;

        float totalHeight = p.height * 2f;
        float offsetY = 0; // ← set this explicitly for wall

        drawSeamlessGradient(p, x, y , rectW, rectH, colors, stops, offsetY, totalHeight);
    }

    @Override
    public void drawFloor() {
        p.background(30);  // Clear background

        float rectW = this.width();
        float rectH = this.height();
        float x = (this.width() - rectW) / 2f;
        float y = 0;

        float totalHeight = p.height * 2f;
        float offsetY = p.height; // ← shift for mirrored floor

        drawSeamlessGradient(p, x, y, rectW, rectH, colors, stops, offsetY, totalHeight);
    }
}

