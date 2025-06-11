import processing.core.PApplet;

public class Staring_Eyes extends AbstractScene {

    private float blinkDuration = 0.52f; // Duration of the blink in seconds
    private int displayDuration = 8; // Duration to display the image in seconds
    private float totalDuration;
    private boolean isBlinking; // Whether the eyes are blinking
    private float blue, red, green, pupilWidth;

    public Staring_Eyes(PApplet p) {
        super(p);
        totalDuration = blinkDuration + displayDuration;
    }

    @Override
    public void drawWall() {
        updateAndDisplay();
    }

    @Override
    public void drawFloor() {
        updateAndDisplay();
    }

    private void updateAndDisplay() {
        // Calculate the current time in seconds
        float currentTime = p.millis() / 1000.0f;
        float cycleTime = currentTime % totalDuration;

        // Determine if we are in the blink phase
        isBlinking = cycleTime > displayDuration;

        // Draw the background
        p.background(0);

        // Draw the eyes if not blinking
        if (!isBlinking) {
            drawEyes();
        }

        // Update eye parameters for the next iteration when blinking is complete
        if (isBlinking && cycleTime > totalDuration - 0.1f) {
            updateEyeParameters();
        }
    }

    private void drawEyes() {
        // Set the fill color for the eyeballs
        p.fill(red, green, blue);
        p.stroke(255); // Set the outline color to white
        p.strokeWeight(3); // Set the outline weight

        // Draw the eyeballs with outline
        p.ellipse(1300, 450, 320, 320);
        p.ellipse(500, 450, 320, 320);

        // Draw the pupils with eye tracking
        float pupilX = p.map(p.mouseX, 0, p.width, 1265, 1335);
        float pupilY = p.map(p.mouseY, 0, p.height, 415, 485);
        float pupilA = p.map(p.mouseX, 0, p.width, 465, 535);

        p.fill(0);
        p.noStroke(); // No outline for the pupils
        p.ellipse(pupilX, pupilY, pupilWidth, 290);
        p.ellipse(pupilA, pupilY, pupilWidth, 290);

        // Draw the eye shape roof
        p.fill(0);
        p.noStroke();
        p.quad(1125, 600, 1125, 200, 1600, 350, 1175, 450);
        p.quad(675, 600, 675, 200, 200, 350, 625, 450);
    }

    // Update the eye colors and pupil width for the next iteration
    private void updateEyeParameters() {
        blue = p.random(256);
        red = p.random(256);
        green = p.random(256);
        pupilWidth = p.random(10, 150);
    }
}
