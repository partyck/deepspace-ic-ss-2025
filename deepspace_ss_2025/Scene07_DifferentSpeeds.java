import processing.core.*;
import TUIO.*;
import java.util.ArrayList;

public class Scene07_DifferentSpeeds extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    private static float speedTop = 1.0f;
    private static float noiseOffset = 0f;
    private static float rotationAngle = 0f;
    private static float targetRotationAngle = 0f;
    private static boolean isKeyRegistered = false;

    private final int stripeThicknessTop = 20;
    private final int stripeThicknessBottom = 40;
    private final float overlap = 20f;
    private final float SPEED_CHANGE_AMOUNT = 0.5f;
    private final float ROTATION_SPEED = 0.05f;

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
            if (event.getKeyCode() == 38) {
                speedTop = Math.min(speedTop + SPEED_CHANGE_AMOUNT, 5.0f);
            } else if (event.getKeyCode() == 40) {
                speedTop = Math.max(speedTop - SPEED_CHANGE_AMOUNT, 0.0f);
            } else if (event.getKey() == 'r' || event.getKey() == 'R') {
                targetRotationAngle = (targetRotationAngle == 0) ? PConstants.HALF_PI : 0;
            }
        }
    }

    private void updateAnimationState() {
        timeElapsed = (timeElapsed + 1) % animationTime;
        if (speedTop > 0.0f) {
            noiseOffset += 0.005 * speedTop;
        }

        float angleDiff = targetRotationAngle - rotationAngle;
        if (Math.abs(angleDiff) > 0.001f) {
            rotationAngle += angleDiff * ROTATION_SPEED;
        }
    }

    @Override
    public void drawWall() {
        updateAnimationState();
        boolean isRotated = Math.abs(rotationAngle - PConstants.HALF_PI) < 0.01f;

        p.pushMatrix();
        p.translate(p.width / 2f, p.height / 2f);
        p.rotate(rotationAngle);
        p.background(0);

        p.translate(-p.width / 2f, -p.height / 2f);

        if (isRotated) {
            drawFloorStripes(0, 0);
        } else {
            float croppedHeight = p.height / 4f;
            drawStripes(0, p.height - croppedHeight, p.width, croppedHeight, stripeThicknessTop, speedTop);
        }

        p.popMatrix();
    }

    @Override
    public void drawFloor() {
        updateAnimationState();
        boolean isRotated = Math.abs(rotationAngle - PConstants.HALF_PI) < 0.01f;

        p.pushMatrix();
        p.translate(p.width / 2f, p.height / 2f);
        p.rotate(rotationAngle);
        p.background(0);

        p.translate(-p.width / 2f, -p.height / 2f);

        if (isRotated) {
            drawFloorStripes(0, 0);
        } else {
            p.translate(0, p.height / 2f - 50);
            drawStripes(0, 0, p.width, p.height / 2f, stripeThicknessBottom, -speedTop);
            drawStripes(0, -p.height / 2f, p.width, p.height / 2f, stripeThicknessTop, speedTop);
            drawStripes(0, -overlap, p.width, overlap, stripeThicknessTop, speedTop);
        }

        p.popMatrix();
    }

    private void drawFloorStripes(float offsetX, float offsetY) {
        drawStripes(offsetX, offsetY + p.height / 2f, p.width, p.height / 2f, stripeThicknessBottom, -speedTop);
        drawStripes(offsetX, offsetY, p.width, p.height / 2f, stripeThicknessTop, speedTop);
        drawStripes(offsetX, offsetY - overlap, p.width, overlap, stripeThicknessTop, speedTop);
    }

    private void drawStripes(float x, float y, float w, float h, int thickness, float speed) {
        float baseStripeWidth = thickness;
        float totalWidth = w + baseStripeWidth * 2;
        float offset = (timeElapsed * speed) % baseStripeWidth;

        for (float currentX = -offset; currentX < totalWidth; currentX += baseStripeWidth * 2) {
            float stripeWidth = baseStripeWidth;
            if (speedTop > 4.5f) {
                float noiseVal = p.noise(currentX * 0.01f, noiseOffset);
                stripeWidth *= 0.8f + 0.4f * noiseVal;
            }

            p.fill(255);
            p.rect(x + currentX, y, stripeWidth, h);
        }
    }
}
