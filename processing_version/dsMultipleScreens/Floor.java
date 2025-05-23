import processing.core.PApplet;

public class Floor extends PApplet {
    private AbstractScene scene;

    public Floor() {
        setScene(new Blackout(this));
    }

    public void settings() {
        if (Constants.DEV) {
            size(Constants.WIDTH, Constants.FLOOR_HEIGHT);
        }
        else {
            fullScreen(2);
        }
    }

    public void setup() {
        if (Constants.DEV) {
            windowMove(0, Constants.WALL_HEIGHT + 50);
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
