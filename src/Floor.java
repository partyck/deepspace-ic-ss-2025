import processing.core.PApplet;

public class Floor extends PApplet {
    private final int canvasWidth, canvasHeight;

    public Floor(int w, int h) {
        this.canvasWidth = w;
        this.canvasHeight = h;
    }

    // Must override settings() to set size before setup()
    @Override
    public void settings() {
        size(canvasWidth, canvasHeight);
    }
    public void draw() {
        background(0);
        fill(255);
        ellipse(100, 50, 10, 10);
    }
}
