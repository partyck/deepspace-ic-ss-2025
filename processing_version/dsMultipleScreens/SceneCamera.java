import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.video.*;

public class SceneCamera extends AbstractScene {
    private Capture cam;
    private final int cropTop = 200;
    private final int cropBottom = 200;

    public SceneCamera(PApplet p, Capture cam) {
        super(p);
        this.cam = cam;
        p.imageMode(PConstants.CENTER); // Set image mode to center for easy centering. [1, 5]
        // System.out.println("scene cam constr");
    }

    @Override
    public void drawWall() {
        background(30);
        if (cam.available() == true) {
            cam.read();
            // System.out.println("scene cam drawWall");
        }
        background(0); // Set a black background

        // Make a copy of the camera image to modify
        PImage frame = cam.get();

        // Apply black and white filter
        frame.filter(12);

        // Calculate the new height after cropping
        int croppedHeight = frame.height - cropTop - cropBottom;

        // Ensure croppedHeight is not negative
        if (croppedHeight < 0) {
            croppedHeight = 0;
        }

        // Get the cropped portion of the image
        PImage croppedImage = frame.get(0, cropTop, frame.width, croppedHeight);

        // Calculate the aspect ratio of the cropped image
        float aspectRatio = (float) croppedImage.height / (float) croppedImage.width;

        // Display the scaled, cropped, and filtered image centered on the canvas. [1, 4, 5]
        // The image() function can take width and height parameters to scale the image. [3]
        p.image(croppedImage, width() / 2, height() / 2, width(), width() * aspectRatio);
    }

    @Override
    public void drawFloor() {
        background(30);
    }

}