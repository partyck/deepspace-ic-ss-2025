import processing.core.PApplet;

public class Wall extends PApplet {
    private final int canvasWidth, canvasHeight;

    public Wall(int w, int h) {
        this.canvasWidth = w;
        this.canvasHeight = h;
    }

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void draw() {
        background(255);
        fill(0);
        ellipse(100, 50, 10, 10);
    }
}
