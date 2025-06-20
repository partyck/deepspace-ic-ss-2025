import processing.core.PApplet;
import processing.core.PConstants;

public class SceneLineForAudience extends AbstractScene {
    private int timeElapsed;
    private final int animationTime;

    public SceneLineForAudience(PApplet p) {
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

     @Override
    public void oscEvent(String path, float value) {
        System.out.println("oscEvent camera");
        switch(path) {
            case "/lineAudience/fader30":
                // lineYPosition = (int) map(value, 0, 1, 0, height());
                // System.out.println("    lineYPosition: "+lineYPosition);
                break;
        }
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

    private float easeOutSeventh(float t) {
        return 1 - (float)Math.pow(1 - t, 7);
    }


    private float animationProgress() {
        float linearProgress = timeElapsed / (float) animationTime;
        return easeOutSeventh(linearProgress);
    }

    private void update() {
        this.timeElapsed++;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

}
