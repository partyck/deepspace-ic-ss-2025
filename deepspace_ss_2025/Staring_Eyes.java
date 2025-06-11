
import processing.core.PApplet;
import TUIO.*;

public class Staring_Eyes extends AbstractScene {

    private float blinkDuration = 0.52f; // Duration of the blink in seconds
    private int displayDuration = 8; // Duration to display the image in seconds
    private float totalDuration;
    private boolean isBlinking; // Whether the eyes are blinking
    private float blue, red, green, pupilWidth;
    private TuioClient tracker;

    public Staring_Eyes(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        totalDuration = blinkDuration + displayDuration;
    }

    // TOP HALF (wall)
    @Override
    public void drawWall() {
        updateAndDisplay();
    }

    // BOTTOM HALF (floor)
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
        // Get the list of active cursors from the tracker
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();

        float targetX = p.width / 2.0f;  // Default target X (center)
        float targetY = p.height / 2.0f; // Default target Y (center)

        // If there's at least one cursor, use its position as the target
        if (!tuioCursorList.isEmpty()) {
            TuioCursor firstCursor = tuioCursorList.get(0);
            targetX = firstCursor.getScreenX(p.width);
            targetY = firstCursor.getScreenY(p.height);
        }

        // Set the fill color for the eyeballs
        p.fill(red, green, blue);
        p.stroke(255);
        p.strokeWeight(3);

        // Draw the eyeballs with outline
        p.ellipse(1300, 450, 320, 320);
        p.ellipse(500, 450, 320, 320);

        // Draw the pupils with eye tracking based on the target
        float pupilX = p.map(targetX, 0, p.width, 1265, 1335);
        float pupilY = p.map(targetY, 0, p.height, 415, 485);
        float pupilA = p.map(targetX, 0, p.width, 465, 535);

        p.fill(0);
        p.noStroke();
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
