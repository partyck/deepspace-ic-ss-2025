import processing.core.PApplet;

public class Wall extends PApplet {

    public void settings() {
        size(384, 216);
    }

    public void draw() {
        background(255);
        fill(0);
        ellipse(100, 50, 10, 10);
    }
}
