import processing.core.PApplet;
import processing.core.PConstants;

public class SceneLineForAudience extends AbstractScene {

    int rectangleHeigth = 100;
    int leftHeigth = 100;
    int rightHeigth = 100;
    int frontWidth = 300;

    public SceneLineForAudience(PApplet p) {
      super(p);
    }

    @Override
    public void drawWall() {
        background(0);
    }

    @Override
    public void drawFloor() {
        background(0);

        noStroke();

        fill(184, 227, 242);
        beginShape();
        vertex(0, height() - leftHeigth);
        vertex((width() - frontWidth) / 2, height() - rectangleHeigth);
        vertex(((width() - frontWidth) / 2) + frontWidth, height() - rectangleHeigth);
        vertex(width(), height() - rightHeigth);
        vertex(width(), height());
        vertex(0, height());
        endShape(PConstants.CLOSE);
    }

        @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/Audience/fader32":
                rectangleHeigth = (int) (value * height());
                System.out.println("    rectangleHeigth: "+rectangleHeigth);
                break;
            case "/Audience/fader40":
                leftHeigth = (int) (value * height());
                System.out.println("    leftHeigth: "+leftHeigth);
                break;
            case "/Audience/fader39":
                rightHeigth = (int) (value * height());
                System.out.println("    rightHeigth: "+rightHeigth);
                break;
            case "/Audience/fader37":
                frontWidth = (int) (value * width());
                System.out.println("    frontWidth: "+frontWidth);
                break;
        }
    }
}
