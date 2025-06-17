import processing.core.*;
import TUIO.*;
import java.util.ArrayList;

// key R - rotate scene
// key D - change animation direction
// key N - adding noise
// arrow keys up/down - speed up/down
// arrow key left/right - change width of stripes (three states: 1- equal width, 2 getting unequal,3 noisy width)

public class Scene07_DifferentSpeeds extends AbstractScene {
    private float animationOffset = 0f;
    private static float speedTop = 1.0f;
    private static float noiseOffset = 0f;
    private static float rotationAngle = 0f;
    private static float targetRotationAngle = 0f;
    private static int direction = 1;
    private static int stripeMode = 0;
    private static boolean isKeyRegistered = false;
    private static float noiseStrength = 5.0f;

    private final int stripeThicknessTop = 80;
    private final int stripeThicknessBottom = 120;
    private final float overlap = 20f;
    private final float SPEED_CHANGE_AMOUNT = 0.5f;
    private final float ROTATION_SPEED = 0.05f;
    private final float floorStripeBlockHeight = height();

    public Scene07_DifferentSpeeds(PApplet p) {
        super(p);
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
            } else if (event.getKey() == 'd' || event.getKey() == 'D') {
                direction *= -1;
            } else if (event.getKeyCode() == 39) {
                stripeMode = (stripeMode + 1) % 3;
            } else if (event.getKeyCode() == 37) {
                stripeMode = (stripeMode + 2) % 3;
            } else if (event.getKey() == 'n' || event.getKey() == 'N') {
                noiseStrength = (noiseStrength < 3.0f) ? noiseStrength + 1.0f : 0.0f;
                System.out.println("Noise Strength (×): " + noiseStrength);
            }
        }
    }

    private void updateAnimationState() {
        animationOffset = (animationOffset + speedTop * direction) % 10000;
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

        if (isRotated) {
            float floorContentWidth = p.height;
            float floorContentHeight = floorStripeBlockHeight;
            p.translate(-floorContentWidth / 2f, -floorContentHeight / 2f);
            drawFloorStripes(0, 0, floorContentWidth, floorContentHeight);
        } else {
            p.translate(-p.width / 2f, -p.height / 2f);
            float croppedHeight = p.height / 4f;
            drawStripes(0, p.height - croppedHeight, p.width, croppedHeight, stripeThicknessTop, speedTop * direction);
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
            drawFloorStripes(0, 0, p.width, p.height);
        } else {
            p.translate(0, p.height / 2f - 50);
            drawStripes(0, 0, p.width, p.height / 2f, stripeThicknessBottom, -speedTop * direction);
            drawStripes(0, -p.height / 2f, p.width, p.height / 2f, stripeThicknessTop, speedTop * direction);
            drawStripes(0, -overlap, p.width, overlap, stripeThicknessTop, speedTop * direction);
        }

        p.popMatrix();
    }

    private void drawFloorStripes(float offsetX, float offsetY, float canvasW, float canvasH) {
        drawStripes(offsetX, offsetY + canvasH / 2f, canvasW, canvasH / 2f, stripeThicknessBottom, -speedTop * direction);
        drawStripes(offsetX, offsetY, canvasW, canvasH / 2f, stripeThicknessTop, speedTop * direction);
        drawStripes(offsetX, offsetY - overlap, canvasW, overlap, stripeThicknessTop, speedTop * direction);
    }

    private void drawStripes(float x, float y, float w, float h, int thickness, float speed) {
        float baseStripeWidth = thickness;
        float currentX = -(animationOffset % (baseStripeWidth * 2));
        int stripeIndex = 0;

        while (currentX < w + baseStripeWidth * 2) {
            float stripeWidth = baseStripeWidth;

            if (stripeMode == 1 && stripeIndex % 5 == 0) {
                stripeWidth *= 3.5f;
            } else if (stripeMode == 2) {
                if (stripeIndex % 5 == 0) {
                    stripeWidth *= 3.5f;
                } else if (stripeIndex % 3 == 0) {
                    float noiseVal = p.noise(currentX * 0.01f, noiseOffset);
                    stripeWidth *= 0.2f + 0.2f * noiseVal * noiseStrength;
                }
            } else if (speedTop > 4.5f) {
                float noiseVal = p.noise(currentX * 0.01f, noiseOffset);
                stripeWidth *= 0.8f + 0.4f * noiseVal * noiseStrength;
            }

            p.fill(255);
            p.rect(x + currentX, y, stripeWidth, h);

            currentX += stripeWidth + baseStripeWidth;
            stripeIndex++;
        }
    }
}
