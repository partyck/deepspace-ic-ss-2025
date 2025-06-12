import processing.core.PApplet;

/**
 * CONTROLS : 
 *    speed of the stripes
 *        - speedUp [up arrow]
 *        - speedDown [down arrow]
 *    direction of the stripes
 *        - change direction [d]
 *    Rotation trigger an animation of 90deg rotation of whole scene (should be extended over floor and wall like one big canvas)
 *        - 90deg rotate [r] 
 *    stripes thickness irregular with Noise
 */

public class Scene07_DifferentSpeeds extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    // stripe settings
    private final int   stripeThicknessTop      = 20;
    private final int   stripeThicknessBottom   = 40;
    private float speedTop                      = 1.0f;
    private final float speedBottom             = 0f;
    private final float overlap                 = 20f;
    private float noiseOffset                   = 0; 
    private final float SPEED_CHANGE_AMOUNT     = 0.5f;

    public Scene07_DifferentSpeeds(PApplet p) {
        super(p);
        this.timeElapsed   = 0;
        this.animationTime = 100;
        System.out.println("Scene initialized with speed: " + speedTop);
        p.registerMethod("keyEvent", this);  // Register for key events
    }

    public void keyEvent(processing.event.KeyEvent event) {
        if (event.getAction() == processing.event.KeyEvent.PRESS) {
            System.out.println("Key pressed: " + event.getKeyCode());
            if (event.getKeyCode() == 38) { // UP arrow
                speedTop = Math.min(speedTop + SPEED_CHANGE_AMOUNT, 5.0f);
                System.out.println("Speed increased to: " + speedTop);
            } else if (event.getKeyCode() == 40) { // DOWN arrow
                speedTop = Math.max(speedTop - SPEED_CHANGE_AMOUNT, 0.0f);
                System.out.println("Speed decreased to: " + speedTop);
            }
        }
    }

    @Override
    public void drawWall() {
        background(0);
        noStroke();
        timeElapsed = (timeElapsed + 1) % animationTime;
        noiseOffset += 0.01;

        // Draw the black background and stripes first
        p.pushMatrix();
        p.noStroke();
        p.fill(0);
        p.rect(0, 0, p.width, p.height/2f);
        drawStripes(0, p.height - p.height/4f, p.width, p.height/4f, stripeThicknessTop, speedTop);
        p.popMatrix();

        // Draw text last so it appears on top
        p.fill(255);  // Set text color to white
        p.textAlign(p.LEFT, p.TOP);  // Align text to top-left
        p.textSize(24);
        String text = "Speed: " + String.format("%.1f", speedTop);
        p.text(text, 20, 20);  // Position text in top-left corner
        
        p.textSize(16);
        p.text("Press UP/DOWN arrows to change speed", 20, 50);
    }

    @Override
    public void drawFloor() {
        background(0);
        noStroke();
        
        timeElapsed = (timeElapsed + 1) % animationTime;
        noiseOffset += 0.01; // Increment noise offset for smooth variation
        
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
     * Draws vertical white stripes across a region with irregular widths.
     *
     * @param x         left corner x
     * @param y         top corner y
     * @param w         region width
     * @param h         region height
     * @param thickness base stripe width
     * @param speed     pixels/frame (0 → static)
     */
    private void drawStripes(float x, float y, float w, float h, int thickness, float speed) {
        float offset = (timeElapsed * speed) % (thickness * 2);
        p.fill(255);
        p.noStroke();
        
        float currentX = -offset;
        while (currentX < w) {
            // Use Perlin noise to create more dramatic variation in stripe width
            float noiseValue = p.noise(currentX * 0.005f, noiseOffset); // Even lower frequency for more dramatic changes
            
            // Create more extreme variations with more black space
            float stripeWidth;
            if (noiseValue < 0.15f) {
                // Ultra thin lines (1-2 pixels)
                stripeWidth = thickness * 0.03f;
            } else if (noiseValue < 0.3f) {
                // Very thin lines
                stripeWidth = thickness * 0.1f;
            } else if (noiseValue < 0.5f) {
                // Medium lines
                stripeWidth = thickness * 0.3f;
            } else if (noiseValue < 0.7f) {
                // Normal stripes
                stripeWidth = thickness * (0.5f + (noiseValue - 0.5f) * 2f);
            } else if (noiseValue < 0.85f) {
                // Wide blocks
                stripeWidth = thickness * (2f + (noiseValue - 0.7f) * 4f);
            } else {
                // Ultra wide blocks
                stripeWidth = thickness * (6f + (noiseValue - 0.85f) * 8f);
            }
            
            p.rect(x + currentX, y, stripeWidth, h);
            
            // Random width for black space using a different noise value
            float gapNoise = p.noise((currentX + 1000) * 0.002f, noiseOffset + 0.5f); // Offset to get different pattern
            float gapWidth;
            if (gapNoise < 0.2f) {
                // Very narrow gaps
                gapWidth = thickness * 0.5f;
            } else if (gapNoise < 0.4f) {
                // Narrow gaps
                gapWidth = thickness * 1.0f;
            } else if (gapNoise < 0.6f) {
                // Medium gaps
                gapWidth = thickness * 2.0f;
            } else if (gapNoise < 0.8f) {
                // Wide gaps
                gapWidth = thickness * 3.0f;
            } else {
                // Very wide gaps
                gapWidth = thickness * 4.0f;
            }
            
            // Add random multiplication to gap width
            float randomMultiplier = 0.2f + p.random(0.5f);
            gapWidth *= randomMultiplier;
            
            currentX += stripeWidth + gapWidth;
        }
    }
}
