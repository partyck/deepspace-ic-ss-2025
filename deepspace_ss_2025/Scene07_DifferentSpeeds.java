import processing.core.*;
import TUIO.*;
import java.util.ArrayList;

// key R - rotate scene
// key D - change animation direction
// key N - adding noise
// arrow keys up/down - speed up/down
// arrow key left/right - change width of stripes (three states: 1- equal width, 2 getting unequal,3 noisy width)

public class Scene07_DifferentSpeeds extends AbstractScene {
    private float animationOffsetTop = 0f;
    private float animationOffsetBottom = 0f;
    private static float speedTop = 1.0f;
    private static float noiseOffset = 0f;
    private static float rotationAngle = 0f;
    private static float targetRotationAngle = 0f;
    private static int direction = 1;
    private static int stripeMode = 0;
    private static boolean isKeyRegistered = false;
    private static float noiseStrength = 5.0f;

    private final int stripeThicknessTop = (width()/27);
    private final int stripeThicknessBottom = (width()/20);
    private final float overlap = height()/10;
    private final float SPEED_CHANGE_AMOUNT = 0.5f;
    private final float ROTATION_SPEED = 0.05f;
    private final float floorStripeBlockHeight = (width()/10);

    private final int[][] topColors = {
        {p.color(249, 199, 142), p.color(242, 200, 191)},  // horizontal
        {p.color(223, 222, 196), p.color(200, 255, 200)}  //  vertical
    };
    
    private final int[][] bottomColors = {
        {p.color(222, 191, 197), p.color(235, 228, 217)}, // horizontal
        {p.color(198, 219, 209), p.color(200, 255, 200)}  //  vertical
    };

    public Scene07_DifferentSpeeds(PApplet p) {
        super(p);
        if (!isKeyRegistered) {
            p.registerMethod("keyEvent", this);
            isKeyRegistered = true;
        }
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/Stripes/push52":
                speedTop = Math.min(speedTop + SPEED_CHANGE_AMOUNT, 5.0f);
                break;
            case "/Stripes/push53":
                speedTop = Math.max(speedTop - SPEED_CHANGE_AMOUNT, 0.0f);
                break;
            case "/Stripes/toggle10":
                targetRotationAngle = (value == 1) ? PConstants.HALF_PI : 0;
                System.out.println("osc Rotation "+ targetRotationAngle);
                break;
            case "/Stripes/toggle11":
                direction = (value == 0) ? 1 : -1;
                System.out.println("osc Direction " + direction);
                break;
            case "/Stripes/push54":
                stripeMode = (stripeMode + 1) % 3;
                break;
            case "/Stripes/push55":
                stripeMode = (stripeMode + 2) % 3;
                break;
            case "/Stripes/push56":
                noiseStrength = (noiseStrength < 3.0f) ? noiseStrength + 1.0f : 0.0f;
                System.out.println("Noise Strength (×): " + noiseStrength);
                break;
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
        animationOffsetTop = (animationOffsetTop + speedTop * direction) % 10000;
        animationOffsetBottom = (animationOffsetBottom - speedTop * direction) % 10000;

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
        p.translate(p.width/2f, p.height/2f);
        p.rotate(rotationAngle);
        p.background(0);

        if (isRotated) {
            // mirror the floor exactly
            p.translate(-p.width/2f, -p.height/2f);
            drawFloorStripes(0, 0, p.width, p.height);
        } else {
            p.translate(-p.width/2f, -p.height/2f);
            float croppedHeight = p.height/4f;
            drawStripes(0, p.height - croppedHeight, p.width, croppedHeight,
                        stripeThicknessTop, speedTop * direction);
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
        float currentOffset = (thickness == stripeThicknessTop) ? animationOffsetTop : animationOffsetBottom;
        float currentX = -(currentOffset % (baseStripeWidth * 2));
        int stripeIndex = 0;
        int colorIndex = (rotationAngle > 0.1f) ? 1 : 0;

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

            // Draw the stripe
            if (thickness == stripeThicknessTop) {
                drawGradientRect(x + currentX, y, stripeWidth, h, 
                               topColors[colorIndex][0], topColors[colorIndex][1]);
            } else {
                drawGradientRect(x + currentX, y, stripeWidth, h, 
                               bottomColors[colorIndex][0], bottomColors[colorIndex][1]);
            }

            currentX += stripeWidth + baseStripeWidth;
            stripeIndex++;
        }
    }

    private void drawGradientRect(float x, float y, float w, float h, int color1, int color2) {
        for (int i = 0; i < w; i++) {
            float t = PApplet.map(i, 0, w, 0, 1);
            int gradCol = p.lerpColor(color1, color2, t);
            p.stroke(gradCol);
            p.line(x + i, y, x + i, y + h);
        }
    }
}