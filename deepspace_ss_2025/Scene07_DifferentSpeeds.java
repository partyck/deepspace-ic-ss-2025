import processing.core.*;
import TUIO.*;
import java.util.ArrayList;

public class Scene07_DifferentSpeeds extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    // Shared animation state
    private static float speedTop = 1.0f;
    private static float noiseOffset = 0f;
    private static float rotationAngle = 0f;
    private static float targetRotationAngle = 0f;
    private static boolean isKeyRegistered = false;

    // Constants
    private final int stripeThicknessTop = 20;
    private final int stripeThicknessBottom = 40;
    private final float overlap = 20f;
    private final float SPEED_CHANGE_AMOUNT = 0.5f;
    private final float ROTATION_SPEED = 0.05f; // how fast the rotation animates

    public Scene07_DifferentSpeeds(PApplet p) {
        super(p);
        this.timeElapsed = 0;
        this.animationTime = 100;

        if (!isKeyRegistered) {
            p.registerMethod("keyEvent", this);
            isKeyRegistered = true;
        }
    }

    public void keyEvent(processing.event.KeyEvent event) {
        if (event.getAction() == processing.event.KeyEvent.PRESS) {
            if (event.getKeyCode() == 38) { // UP arrow
                speedTop = Math.min(speedTop + SPEED_CHANGE_AMOUNT, 5.0f);
            } else if (event.getKeyCode() == 40) { // DOWN arrow
                speedTop = Math.max(speedTop - SPEED_CHANGE_AMOUNT, 0.0f);
            } else if (event.getKey() == 'r' || event.getKey() == 'R') {
                // Toggle between 0 and HALF_PI (90 degrees)
                if (targetRotationAngle == 0) {
                    targetRotationAngle = PConstants.HALF_PI;
                } else {
                    targetRotationAngle = 0;
                }
            }
        }
    }

    private void updateAnimationState() {
        timeElapsed = (timeElapsed + 1) % animationTime;

        if (speedTop > 0.0f) {
            noiseOffset += 0.005 * speedTop;
        }

        // Smooth rotation
        float angleDiff = targetRotationAngle - rotationAngle;
        if (Math.abs(angleDiff) > 0.001f) {
            rotationAngle += angleDiff * ROTATION_SPEED;
        }
    }

    @Override
    public void drawWall() {
        updateAnimationState();

        p.pushMatrix();
        p.translate(p.width / 2f, p.height / 2f);
        p.rotate(rotationAngle);
        p.translate(-p.width / 2f, -p.height / 2f);
        drawScene(true);
        p.popMatrix();
    }

    @Override
    public void drawFloor() {
        updateAnimationState();

        p.pushMatrix();
        p.translate(p.width / 2f, p.height / 2f);
        p.rotate(rotationAngle);
        p.translate(-p.width / 2f, -p.height / 2f);
        drawScene(false);
        p.popMatrix();
    }

    private void drawScene(boolean isWall) {
        p.background(0);
        p.noStroke();

        if (isWall) {
            // Wall: upper portion
            p.fill(0);
            p.rect(0, 0, p.width, p.height / 2f);
            drawStripes(0, p.height - p.height / 4f, p.width, p.height / 4f, stripeThicknessTop, speedTop);
        } else {
            // Floor: lower portion
            p.translate(0, p.height / 2f - 50);
            p.fill(0);
            p.rect(0, 0, p.width, p.height / 2f);
            drawStripes(0, 0, p.width, p.height / 2f, stripeThicknessBottom, -speedTop);
            drawStripes(0, -p.height / 2f, p.width, p.height / 2f, stripeThicknessTop, speedTop);
            drawStripes(0, -overlap, p.width, overlap, stripeThicknessTop, speedTop);
        }
    }

    private void drawStripes(float x, float y, float w, float h, int thickness, float speed) {
        float baseStripeWidth = thickness;
        float totalWidth = w + baseStripeWidth * 2;
        float offset = (timeElapsed * speed) % baseStripeWidth;

        for (float currentX = -offset; currentX < totalWidth; currentX += baseStripeWidth * 2) {
            float stripeWidth = baseStripeWidth;

            // Subtle widening effect only at high speeds
            if (speedTop > 4.5f) {
                float noiseVal = p.noise(currentX * 0.01f, noiseOffset);
                stripeWidth *= 0.8f + 0.4f * noiseVal;
            }

            p.fill(255);
            p.rect(x + currentX, y, stripeWidth, h);
        }
    }
}
