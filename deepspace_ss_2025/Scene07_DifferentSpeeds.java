import processing.core.PApplet;

/**
 * TODO : 
 *    speed of the stripes should be controllable
 *    playing with colors?
 *    stripes thickness schould be indivual adressable and changing
 */

public class Scene07_DifferentSpeeds extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    // stripe settings
    private final int   stripeThicknessTop    = 20;
    private final int   stripeThicknessBottom = 40;
    private final float speedTop              = 1.2f;
    private final float speedBottom           = 0f;
    private final float overlap               = 20f;

    public Scene07_DifferentSpeeds(PApplet p) {
        super(p);
        this.timeElapsed   = 0;
        this.animationTime = 200;
    }

    @Override
    public void drawWall() {
        background(0);
        noStroke();
        timeElapsed = (timeElapsed + 1) % animationTime;

        p.pushMatrix();
        p.noStroke();
        p.fill(0);
        p.rect(0, 0, p.width, p.height/2f);
        drawStripes(0, p.height - p.height/4f, p.width, p.height/4f, stripeThicknessTop, speedTop);
        p.popMatrix();
    }

    @Override
    public void drawFloor() {
        background(0);
        noStroke();
        
        timeElapsed = (timeElapsed + 1) % animationTime;
        p.pushMatrix();
        p.translate(0, p.height/2f - 50);

        p.noStroke();
        p.fill(0);
        p.rect(0, 0, p.width, p.height/2f);

        drawStripes(0, 0, p.width, p.height/2f, stripeThicknessBottom, -speedTop);    
        drawStripes(0, - p.height/2f, p.width, p.height/2f, stripeThicknessTop, speedTop);
        drawStripes(0, -overlap, p.width, overlap, stripeThicknessTop, speedTop);

        p.popMatrix();
    }

    /**
     * Draws vertical white stripes across a region.
     *
     * @param x         left corner x
     * @param y         top corner y
     * @param w         region width
     * @param h         region height
     * @param thickness stripe width
     * @param speed     pixels/frame (0 → static)
     */
    private void drawStripes(float x, float y, float w, float h, int thickness, float speed) {
        float offset = (timeElapsed * speed) % (thickness * 2);
        p.fill(255);
        p.noStroke();
        for (float sx = -offset; sx < w; sx += thickness * 2) {
            p.rect(x + sx, y, thickness, h);
        }
    }
}
