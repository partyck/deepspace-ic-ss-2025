import processing.core.PApplet;

public class Wall extends PApplet {
    private final int canvasWidth, canvasHeight;
    SceneManager mgr;

    public Wall(int w, int h) {
        this.canvasWidth = w;
        this.canvasHeight = h;
    }

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void setup() {
        mgr = new SceneManager(height);
        mgr.addScene(new SceneNoHome(width, height));
        // mgr.addScene(new SceneTwo(...));  // future scenes
    }

    @Override
    public void draw() {
        background(255);
        fill(0);
        ellipse(100, 50, 10, 10);
        mgr.update();
        mgr.render(this, false);  // upper half
    }

    @Override
    public void keyPressed() {
        if (key == '9') {
            mgr.switchTo(0);    // scene index for SceneNoHome
        }
        // if you add more: key '8' → mgr.switchTo(1), etc.
    }
}
