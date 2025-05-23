import processing.core.PApplet;

/**
 * TODO : 
 *    add tracking interactivity to enhance the boxSize (with TUIO tracking the dancer can make their rooms bigger)
 *    control the position of the box
 *    NICETOHAVE: add camera, film one room and project this view as an additional square on the wall
 *    Forms morphing to other forms --> Scene06_Daria
 */
 
public class Scene05_Sophie extends AbstractScene {
    private static final int ORANGE        = 0xFFFF6600;
    private static final int BLUE          = 0xFF3366FF;
    private static final int ORANGE_BG     = 0xFFCC5500; // a bit darker
    private static final int BLUE_BG       = 0xFF254CB0; // a bit deeper

    private int   timeElapsed;
    private final int animationTime;

    public Scene05_Sophie(PApplet p) {
        super(p);
        this.timeElapsed   = 0;
        this.animationTime = 200;
    }

    @Override
    public void drawWall() {
        p.background(0);
        p.noStroke();

        drawSmallRect(p.width * 0.1f, p.height - 40, 80, 40, true, false);
        drawMediumSquare(p.width * 0.3f, p.height - 100, 100, 100, false, false);
        drawVerticalBar(p.width * 0.55f, p.height - 180, 40, 180, true, false);
        drawVerticalBar(p.width * 0.75f, p.height - 240, 40, 240, false, false);
    }

    @Override
    public void drawFloor() {
        p.background(0);
        p.noStroke();

        // mirror wall shapes on the floor:
        drawSmallRect(   p.width * 0.1f, 0,              80,  40, true, true);
        drawMediumSquare(p.width * 0.3f, 0,             100,  70, false, true);
        drawVerticalBar( p.width * 0.55f, 0,             40, 240, true, true);
        drawVerticalBar( p.width * 0.75f, 0,             40,  40, false, true);

        // extra two shapes below
        float baseY = p.height * 0.75f;
        drawIrregularPoly(p.width * 0.2f, p.height - 140);
        drawTiltedRect(   p.width * 0.7f, p.height - 200, 150, 80, PApplet.radians(-35));
    }

    // 1) small rectangle with two-tone fill
    private void drawSmallRect(float x, float y, int w, int h, boolean isOrange, boolean isFloor) {
        p.fill(isOrange ? ORANGE_BG : BLUE_BG);
        p.rect(x, y, w, h);
        p.fill(isOrange ? ORANGE : BLUE);
        p.rect(x + w*0.15f, isFloor? y: p.height-(h*0.7f), w*0.7f, h*0.7f);
    }

    // 2) medium square with concentric two-tone
    private void drawMediumSquare(float x, float y, int w, int h, boolean isOrange, boolean isFloor) {
        p.fill(isOrange ? ORANGE_BG : BLUE_BG);
        p.rect(x, y, w, h);
        p.fill(isOrange ? ORANGE : BLUE);
        float inset = w * 0.2f;
        p.rect(x + inset, y + inset, w - 2*inset, h - 2*inset);
        p.fill(isOrange ? ORANGE_BG : BLUE_BG);
        float inset2 = w * 0.4f;
        p.rect(x + inset2, y + inset2, w - 2*inset2, h - 2*inset2);
    }

    // 3&4) vertical bar with two-tone stripes
    private void drawVerticalBar(float x, float y, float w, float h, boolean isOrange, boolean isFloor) {
        p.fill(isOrange ? ORANGE_BG : BLUE_BG);
        p.rect(x, y, w, h);
        // stripes in primary color
        p.fill(isOrange ? ORANGE : BLUE);
        float stripeH = h / 8f;
        for (float yy = y; yy < y + h; yy += stripeH * 2) {
            p.rect(x, yy, w, stripeH);
        }
    }

    // 5) irregular polygon in white
    private void drawIrregularPoly(float x, float y) {
        p.fill(255);
        p.beginShape();
        p.vertex(x,       y);
        p.vertex(x + 60,  y - 20);
        p.vertex(x + 100, y + 80);
        p.vertex(x + 40,  y + 120);
        p.vertex(x - 20,  y + 40);
        p.endShape(PApplet.CLOSE);
    }

    // 6) tilted rectangle in white
    private void drawTiltedRect(float x, float y, float w, float h, float angle) {
        p.pushMatrix();
        p.translate(x, y);
        p.rotate(angle);
        p.fill(255);
        p.rect(0, 0, w, h);
        p.popMatrix();
    }
}
