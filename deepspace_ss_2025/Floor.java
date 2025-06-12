import processing.core.*;

public class Floor extends PApplet {
    private AbstractScene scene;

    public Floor() {
        setScene(new Blackout(this));
    }

    public void settings() {
        if (Constants.DEV) {
            size(Constants.WIDTH, Constants.FLOOR_HEIGHT, PConstants.P2D);
        }
        else {
            fullScreen(PConstants.P2D, 1);
        }
    }

    public void setup() {
        if (Constants.DEV) {
            windowMove(0, Constants.WALL_HEIGHT + 75);
        }
    }

    @Override
    public void draw() {
        scene.draw();
    }

    public void setScene(AbstractScene scene) {
        this.scene = scene;
    }
}
