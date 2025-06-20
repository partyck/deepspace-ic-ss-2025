import processing.core.PApplet;
import processing.core.PConstants;

public class SceneApplause extends AbstractScene {

    public SceneApplause(PApplet p) {
        super(p);
    }

    @Override
    public void drawWall() {
        background(0);
    }

    @Override
    public void drawFloor() {
        background(255);
    }

}
