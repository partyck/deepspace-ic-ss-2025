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
        color1 = p.color(0, 0, 0);     // Deep blue (outer)
        color2 = p.color(10, 20, 60);     // Deep blue
        color3 = p.color(10, 20, 60);     // Deep blue (outer)
        color4 = p.color(235, 109, 23);   // orange
        color5 = p.color(10, 20, 60);     // Deep blue
        color6 = p.color(10, 20, 60);     // Deep blue (outer)
        
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
            int segColor = getSixColorGradient(interval);
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

    private int getSixColorGradient(float t) {
        t = smoothstep(t);
        if (t < 0.2f) {
            // color1 to color2
            float localT = t / 0.2f;
            return p.lerpColor(color1, color2, localT);
        } else if (t < 0.4f) {
            // color2 to color3
            float localT = (t - 0.2f) / 0.2f;
            return p.lerpColor(color2, color3, localT);
        } else if (t < 0.6f) {
            // color3 to color4
            float localT = (t - 0.4f) / 0.2f;
            return p.lerpColor(color3, color4, localT);
        } else if (t < 0.8f) {
            // color4 to color5
            float localT = (t - 0.6f) / 0.2f;
            return p.lerpColor(color4, color5, localT);
        } else {
            // color5 to color6 (which equals color1)
            float localT = (t - 0.8f) / 0.2f;
            return p.lerpColor(color5, color6, localT);
        }
    }

    private float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }

    private float animationProgress() {
        return timeElapsed / (float) animationTime;
    }

    private void update() {
        speed = (int) p.lerp(speed, targetSpeed, 0.4f);
        timeElapsed += speed;
        if (this.timeElapsed >= this.animationTime) this.timeElapsed = 0;
    }

    @Override
    public void oscEvent(String path, float value) {
        switch(path) {
            case "/Valerio/fader9":
                circleWidth = p.floor(p.map(value, 0, 1, 100, width()));
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
