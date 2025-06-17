import java.util.ArrayList;

import processing.core.*;
import TUIO.*;

public class Scene02ValerioMorning extends AbstractScene {
    private final int color1;
    private final int color2;
    private int timeElapsed;
    private int speed;
    private int animationTime;

    public Scene02ValerioMorning(PApplet p) {
        super(p);
        color1 = color(255, 255, 255);
        color2 = color(0, 0, 0);
        timeElapsed = 0;
        speed = 1;
        animationTime = 100000;

    }

    @Override
    public void drawWall() {
        background(0);
        float centerX = this.width() * 0.5f;
        float centerY = this.height();
        display(centerX, centerY);
    }

    @Override
    public void drawFloor() {
        background(0);
        float centerX = this.width() * 0.5f;
        float centerY = 0;
        display(centerX, centerY);
    }

    public void display(float centerX, float centerY) {
        float circleWidth = width() - (width() * speed / 100f);
        int segments = 200;
        float distance = PConstants.TWO_PI / segments;

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
        circle(centerX, centerY, width() * 0.1f);

        update();
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
                System.out.println(speed / 100f);
                break;
            case "/Valerio/fader10":
              
            default:
                // code block
        }
    }

    // inner classes

    private class Dancer {
        float x;
        float y;
        long cursorId;
        
        Dancer(TuioCursor cursor) {
            this.cursorId = cursor.getSessionID();
            x = cursor.getScreenX(width()); 
            y = cursor.getScreenY(height());
        }

        boolean isLinkedTo(TuioCursor cursor) {
            return cursorId == cursor.getSessionID();
        }

        void update(TuioCursor cursor) {
            // x = cursor.getScreenX(Constants.WIDTH) - width() / 2f;
            // y = cursor.getScreenY(Constants.FLOOR_HEIGHT) - height() / 2f;
            x = cursor.getScreenX(width());
            y = cursor.getScreenY(height());
        }
    }

}
