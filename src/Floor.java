import processing.core.PApplet;

public class Floor extends PApplet {
    private final int canvasWidth, canvasHeight;
    SceneManager mgr;

    public Floor(int w, int h) {
        this.canvasWidth = w;
        this.canvasHeight = h;
    }

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void setup() {
        mgr = new SceneManager(height);
        mgr.addScene(new SceneNoHome(width, height));
        // same scene list as Wall.java!
    }

    public void draw() {
        background(0);
        fill(255);
        ellipse(100, 50, 10, 10);
        mgr.update();
        mgr.render(this, true); // lower half
    }

    @Override
    public void keyPressed() {
        if (key == '9') {
            mgr.switchTo(0);
        }
    }
}
