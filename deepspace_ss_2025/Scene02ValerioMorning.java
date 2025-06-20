import java.util.ArrayList;

import processing.core.*;
import TUIO.*;

public class Scene02ValerioMorning extends AbstractScene {
    private final int color1;
    private final int color2;
    private int timeElapsed;
    private int targetSpeed;
    private int speed;
    private int animationTime;
    private int segments;
    private int circleWidth;
    private float distance;

    public Scene02ValerioMorning(PApplet p) {
        super(p);
        color1 = color(255, 255, 255);
        color2 = color(0, 0, 0);
        timeElapsed = 0;
        speed = 10;
        targetSpeed = speed;
        animationTime = 1000000;
        segments = 300;
        circleWidth = width();
        distance = PConstants.TWO_PI / segments;
    }

    @Override
    public void init() {
        rectMode(PConstants.CORNER);
        translate(0, 0);
    }

    @Override
    public void drawWall() {
        background(0);
        float centerX = width() * 0.5f;
        float centerY = height();
        display(centerX, centerY);
    }

    @Override
    public void drawFloor() {
        background(0);
        float centerX = width() * 0.5f;
        float centerY = 0;
        display(centerX, centerY);
    }

    public void display(float centerX, float centerY) {
        // float circleWidth = width() - (width() * speed / 30000f);

        noStroke();
        float offSet = segments * animationProgress();

        pushMatrix();
        translate(centerX, centerY);
        rotate(PConstants.TWO_PI * animationProgress());
        for (int i = 0; i < segments; i++ ) {
            float interval = (float) i / (float) segments;
            fill(lerpColor(color1, color2, interval));
            float angleStart = distance * i;
            float angleEnd = distance * (i + 1) + 0.01f;
            arc(0, 0, circleWidth, circleWidth,  angleStart, angleEnd);
        }
        popMatrix();

        fill(0);
        circle(centerX, centerY, width() * 0.1f);

        update();
    }

    private float animationProgress() {
        return timeElapsed / (float) animationTime;
    }

    private void update() {
        speed = (int) lerp(speed, targetSpeed, 0.4f);
        timeElapsed += speed;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/Valerio/fader9":
                circleWidth = floor(map(value, 0, 1, 100, width()));
                // speed = floor(map(value, 0, 1, 100, 10000));
                System.out.println("    circleWidth: "+circleWidth);
                break;
            case "/Valerio/toggle1":
                targetSpeed = targetSpeed * 2;
                System.out.println("    targetSpeed: " + targetSpeed + " speed: " + speed + " value: "+value);
                break;
            case "/Valerio/toggle2":
                targetSpeed = targetSpeed / 2;
                System.out.println("    targetSpeed: " + targetSpeed + " speed: " + speed + " value: "+value);
                break;
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
