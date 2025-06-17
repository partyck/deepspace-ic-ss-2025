import processing.core.PApplet;
import processing.core.PConstants;

public class SceneOne extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    public SceneOne(PApplet p) {
        super(p);
        this.timeElapsed = 0;
        this.animationTime = (int) (frameRate() * 60 * 3);
    }

    @Override
    public void drawWall() {
        display();
    }

    @Override
    public void drawFloor() {
        display();
    }

    private void display() {
        background(30);
        noStroke();
        fill(255);

        float rectW = lerp(this.width(), 0, animationProgress());

        rectMode(PConstants.CENTER);
        rect(this.width()/2f, this.height()/2f, rectW, this.height());

        rectMode(PConstants.CORNER);

        this.update();
    }

    private float animationProgress() {
        return timeElapsed / (float) animationTime;
    }

    private void update() {
        this.timeElapsed++;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

}
