import processing.core.PApplet;

public class Floor extends PApplet {
    private AbstractScene scene;

    public Floor() {
        setScene(new Blackout(this));
    }

    public void settings() {
        size(Constants.WIDTH, Constants.FLOOR_HEIGHT);
    }

    public void setup() {
    }

    @Override
    public void draw() {
        scene.draw();
    }

    public void setScene(AbstractScene scene) {
        this.scene = scene;
    }
}
