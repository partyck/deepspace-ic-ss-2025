import processing.core.*;

public class Scene02ValerioMorning extends AbstractScene {
    private final int color1;
    private final int color2;
    private int timeElapsed;
    private int speed;
    private int animationTime;

    public Scene02ValerioMorning(PApplet p) {
        super(p);
        color1 = color(239, 179, 83);
        color2 = color(41, 0, 142);
        timeElapsed = 0;
        speed = 10;
        animationTime = 100000;
    }

    @Override
    public void drawWall() {
        float centerX = this.width() * 0.5f;
        float centerY = this.height();
        display(centerX, centerY);
    }

    @Override
    public void drawFloor() {
        float centerX = this.width() * 0.5f;
        float centerY = 0;
        display(centerX, centerY);
    }

    public void display(float centerX, float centerY) {
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
        circle(centerX, centerY, this.width() * 0.3f);
        arc(centerX, centerY, circleWidth + 10, circleWidth + 10, offSet, offSet + PConstants.TWO_PI * 0.05f);

        this.update();
    }

    private float animationProgress() {
        return timeElapsed / (float) animationTime;
    }

    private void update() {
        timeElapsed += speed;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/Valerio/fader9":
                speed = floor(map(value, 0, 1, 1, 100));
                System.out.println("    speed: "+speed);
                break;
            case "/Valerio/fader10":
              
            default:
                // code block
        }
    }

}
