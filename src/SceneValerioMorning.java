import processing.core.PApplet;
import processing.core.*;

public class SceneValerioMorning extends AbstractScene {
    private int color1;
    private int color2;
    private int timeElapsed;
    private final int animationTime;

    public SceneValerioMorning(PApplet p) {
        super(p);
        color1 = color(239, 179, 83);
        color2 = color(41, 0, 142);
        timeElapsed = 0;
        animationTime = 10000;
    }

    @Override
    public void draw() {
        float centerX = this.width() * 0.5f;
        float centerY = this.width() * 0.5f;
        float circleWidth = this.width() * 0.75f;
        int segments = 100;
        float distance = PConstants.TWO_PI / segments;

        background(0);
        noStroke();
        float offSet = segments * animationProgress();

        for (int i = 0; i < segments; i++ ) {
            float interval = (float) i / (float) segments;
            fill(lerpColor(color1, color2, interval));
            float angleStart = offSet + distance * i;
            float angleEnd = offSet + distance * (i + 1) + 0.01f;
            arc(centerX, centerY, circleWidth, circleWidth,  angleStart, angleEnd);
        }

        fill(0);
        circle(centerX, centerY, this.width() * 0.5f);
        arc(centerX, centerY, circleWidth + 10, circleWidth + 10, offSet, offSet + PConstants.TWO_PI * 0.1f);

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
