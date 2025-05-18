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
        p.imageMode(PConstants.CENTER);
    }

    @Override
    public void drawWall() {
        background(30);
        if (cam.available() == true) {
            cam.read();
            // System.out.println("scene cam drawWall");
        }
        background(0);
        PImage frame = cam.get();
        frame.filter(12);
        int croppedHeight = frame.height - cropTop - cropBottom;

        if (croppedHeight < 0) {
            croppedHeight = 0;
        }
        PImage croppedImage = frame.get(0, cropTop, frame.width, croppedHeight);
        float aspectRatio = (float) croppedImage.height / (float) croppedImage.width;
        p.image(croppedImage, width() / 2, height() / 2, width(), width() * aspectRatio);
    }

    @Override
    public void drawFloor() {
        background(30);
    }

}