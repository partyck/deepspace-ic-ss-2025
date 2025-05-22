import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.*;

public class SceneCamera extends AbstractScene {
    private Capture cam;
    private final int cropTop = 200;
    private final int cropBottom = 200;
    private PGraphics buffer;
    private int circleDiameter;

    public SceneCamera(PApplet p, Capture cam) {
        super(p);
        this.cam = cam;
        p.imageMode(PConstants.CENTER);
        PImage frame = cam.get();

        buffer = createGraphics(frame.height, frame.height);
        buffer.beginDraw();
        buffer.background(0);
        buffer.endDraw();

        circleDiameter = (int) (width() * 0.4);
    }

    @Override
    public void drawWall() {
        background(30);
        if (cam.available() == true) {
            cam.read();
        }
        background(0);
        PImage frame = cam.get();
        frame.filter(12);

        float offsetX = random(-2, 2);
        float offsetY = random(-2, 2);

        buffer.beginDraw();
        buffer.tint(255, 20);
        buffer.image(frame, offsetX, offsetY); 
        buffer.endDraw();


        int croppedHeight = buffer.height - cropTop - cropBottom;

        if (croppedHeight < 0) {
            croppedHeight = 0;
        }
        PImage croppedImage = buffer.get(0, cropTop, buffer.width, croppedHeight);
        float aspectRatio = (float) croppedImage.height / (float) croppedImage.width;

        p.image(croppedImage, width() / 2, height() / 2, width(), width() * aspectRatio);


    }

    @Override
    public void drawFloor() {
        background(0);
    }

}