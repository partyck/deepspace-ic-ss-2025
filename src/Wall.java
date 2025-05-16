import processing.core.PApplet;

public class Wall extends PApplet {
    private AbstractScene scene;

    public Wall() {}

    public void settings() {
        size(Main.WIDTH, Main.WALL_HEIGHT);
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
