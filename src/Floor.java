import processing.core.PApplet;

public class Floor extends PApplet {
    public void settings() {
        size(384, 216);
    }
    public void draw() {
        background(0);
        fill(255);
        ellipse(100, 50, 10, 10);
    }
}
