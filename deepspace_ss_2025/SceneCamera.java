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
    private int aphaTint = 20;
    PImage circleMask;

    public SceneCamera(PApplet p, Capture cam) {
        super(p);
        this.cam = cam;
        p.imageMode(PConstants.CENTER);
        PImage frame = cam.get();
        buffer = createGraphics(frame.width, frame.height);
        circleMask = loadImage("circle_mask.jpg");
    }

    @Override
    public void drawWall() {
        background(0);
        if (cam.available()) {
            cam.read();
        }
        PImage frame = cam.get();
        frame.filter(12);

        int offsetD = (int) map(aphaTint, 0, 100, 0, 2);
        float offsetX = random(-1 * offsetD, offsetD);
        float offsetY = random(-1 * offsetD, offsetD);

        buffer.beginDraw();
        buffer.tint(255, aphaTint);
        buffer.image(frame, offsetX, offsetY);
        buffer.endDraw();
        buffer.mask(circleMask);

        float aspectRatio = (float) buffer.height / (float) buffer.width;
        image(buffer, width() * 0.5f, height() * 0.5f, width() * 0.5f, width() * 0.5f * aspectRatio);
    }

    @Override
    public void drawFloor() {
        background(0);
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/cam/fader1":
                aphaTint = floor(map(value, 0, 1, 0, 50));
                System.out.println("    aphaTint: "+aphaTint);
                break;
            default:
        }
    }

}