// Preview.java
import processing.core.PApplet;

public class ScenePreview extends PApplet {
    private final int canvasWidth, canvasHeight;
    private SceneNoHome scene;  // or SceneManager if you like

    public ScenePreview(int w, int h) {
        this.canvasWidth = w;
        this.canvasHeight = h;
    }

    public void settings() {
        size(canvasWidth, canvasHeight * 2);
    }

    @Override
    public void setup() {
        // build & init your scene
        scene = new SceneNoHome(canvasWidth, canvasHeight);
        scene.enter();
    }

    @Override
    public void draw() {
        // step its animation…
        scene.update();
        // draw the full scene at offset 0 with full opacity
        scene.render(this, 0, 1f);
    }
}
