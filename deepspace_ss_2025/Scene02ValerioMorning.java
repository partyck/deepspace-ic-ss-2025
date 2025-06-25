import java.util.ArrayList;
import processing.core.*;
import TUIO.*;

public class Scene02ValerioMorning extends AbstractScene {
    // Five colors for gradient
    private int color1;
    private int color2; 
    private int color3;
    private int color4;
    private int color5;
    private int color6;
    private int timeElapsed;
    private int targetSpeed;
    private int speed;
    private int animationTime;
    private int segments;
    private int circleWidth;
    private float distance;

    public Scene02ValerioMorning(PApplet p) {
        super(p);
        // Initialize colors after super() call
        color1 = p.color(77,141,143);    // Warm orange
        color2 = p.color(255, 238, 195);   // Pale white-yellow
        color3 = p.color(77, 141, 143);   // Soft blue
        color4 = p.color(0, 0, 0);         // Deep black
        
        timeElapsed = 0;
        speed = 10;
        targetSpeed = speed;
        animationTime = 1000000;
        segments = 720;
        circleWidth = width();
        distance = PConstants.TWO_PI / segments;
    }

    @Override
    public void init() {
        p.rectMode(PConstants.CORNER);
        p.translate(0, 0);
    }

    @Override
    public void drawWall() {
        p.background(0);  // Simple black background
        float centerX = width() * 0.5f;
        float centerY = height();
        display(centerX, centerY);
    }

    @Override
    public void drawFloor() {
        p.background(0);  // Simple black background
        float centerX = width() * 0.5f;
        float centerY = 0;
        display(centerX, centerY);
    }

    public void display(float centerX, float centerY) {
        p.noStroke();

        p.pushMatrix();
        p.translate(centerX, centerY);
        p.rotate(PConstants.TWO_PI * animationProgress());
        
        for (int i = 0; i < segments; i++) {
            float interval = (float) i / (float) segments;
            int segColor = getGradientColor(interval);
            p.fill(segColor);
            
            float angleStart = distance * i;
            float angleEnd = distance * (i + 1) + 0.01f;
            p.arc(0, 0, circleWidth, circleWidth, angleStart, angleEnd);
        }
        p.popMatrix();

        // Black center circle
        p.fill(0);
        p.circle(centerX, centerY, width() * 0.1f);

        update();
    }

    private int getGradientColor(float t) {
        t = smoothstep(t);
        if (t < 0.05f) {
            return p.lerpColor(color1, color2, t / 0.05f); // orange to pale
        } else if (t < 0.25f) {
            return p.lerpColor(color2, color3, (t - 0.05f) / 0.20f); // pale to blue
        } else if (t < 0.60f) {
            return p.lerpColor(color3, color4, (t - 0.25f) / 0.35f); // blue to black
        } else {
            return p.lerpColor(color4, color1, (t - 0.60f) / 0.40f); // black to orange
        }
    }

    private float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }

    private float animationProgress() {
        return timeElapsed / (float) animationTime;
    }

    private void update() {
        speed = (int) PApplet.lerp(speed, targetSpeed, 0.4f);
        timeElapsed += speed;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/Valerio/fader9":
                circleWidth = PApplet.floor(PApplet.map(value, 0, 1, 100, width()));
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

    // Inner classes remain the same
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
            x = cursor.getScreenX(width());
            y = cursor.getScreenY(height());
        }
    }
}
