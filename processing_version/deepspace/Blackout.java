import processing.core.PApplet;

public class Blackout extends AbstractScene {

    public Blackout(PApplet p) {
        super(p);
    }

    @Override
    public void drawWall() {
        background(0);
    }

    @Override
    public void drawFloor() {
        background(0);
    }
}
