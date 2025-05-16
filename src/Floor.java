import processing.core.PApplet;

public class Floor extends PApplet {
    private AbstractScene scene;

    public Floor() {}

    public void settings() {
        size(Main.WIDTH, Main.FLOOR_HEIGHT);
    }

    public void setup() {
        setScene(new Blackout(this));
    }

    @Override
    public void draw() {
        this.scene.draw();
    }

    public void setScene(AbstractScene scene) {
        this.scene = scene;
    }
}
