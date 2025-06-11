import processing.core.PApplet;
import TUIO.*;

import java.util.ArrayList;
import java.util.HashMap;

// Buttons to press 'q' to start animation, 'w' to fix position of all rectangles
// slider to controll 'x' and 'y' position of all rectangles

public class Scene02Rectangles extends AbstractScene {
    TuioClient tracker;
    private static HashMap<Integer, Rectangle> rectangles = new HashMap<>();
    private static float animationSize = 0;
    private static boolean isAnimating = false;

    private class Rectangle {
        float x, y;
        float width = Float.valueOf(width() / 10) ; // 50;
        float height = Float.valueOf(height() / 10);
        boolean isFixed = false;
        float maxHeight = 0;
        int animationType;
        float animationProgress = 0;
        float targetWidth;
        float targetHeight;
        
        Rectangle(float x, float y) {
            this.x = x;
            this.y = y;
            // Assign random animation type (0-5)
            this.animationType = (int)p.random(6);
            // Set target sizes based on animation type
            switch(animationType) {
                case 0: // Tall and thin
                    targetWidth = Float.valueOf((width() / 10) /2); 
                    targetHeight = Float.valueOf((height() / 10) * 8);
                    break;
                case 1: // Wide and short
                    targetWidth = Float.valueOf((width() / 10) * 1);
                    targetHeight = Float.valueOf((height() / 10) * 2);
                    break;
                case 2: // Square but large
                    targetWidth = Float.valueOf((width() / 10) / 2);
                    targetHeight = Float.valueOf((height() / 10) * 5);
                    break;
                case 3: // Tall and medium width
                    targetWidth = Float.valueOf((width() / 10) / 2);
                    targetHeight = Float.valueOf((height() / 10) * 9);
                    break;
                case 4: // Medium square
                    targetWidth = Float.valueOf((width() / 10 )* 1);
                    targetHeight = Float.valueOf((height() / 10) * 1);
                    break;
                case 5: // Wide and medium height
                    targetWidth = Float.valueOf((width() / 10) * 1);
                    targetHeight = Float.valueOf((height() / 10) * 6);
                    break;
            }
        }

        void updateAnimation() {
            if (isAnimating) {
                animationProgress += 0.02; // Animation speed
                if (animationProgress > 1) {
                    animationProgress = 1;
                }
                
                // Different easing functions for different animation types
                float easedProgress;
                switch(animationType) {
                    case 0: // Linear
                        easedProgress = animationProgress;
                        break;
                    case 1: // Ease out
                        easedProgress = 1 - (1 - animationProgress) * (1 - animationProgress);
                        break;
                    case 2: // Ease in
                        easedProgress = animationProgress * animationProgress;
                        break;
                    case 3: // Bounce
                        easedProgress = 1 - (float)Math.cos(animationProgress * Math.PI * 2);
                        break;
                    case 4: // Elastic
                        easedProgress = (float)Math.sin(animationProgress * Math.PI * 4) * (1 - animationProgress) + animationProgress;
                        break;
                    case 5: // Smooth step
                        easedProgress = animationProgress * animationProgress * (3 - 2 * animationProgress);
                        break;
                    default:
                        easedProgress = animationProgress;
                }
                
                width = p.lerp(width, targetWidth, easedProgress);
                height = p.lerp(height, targetHeight, easedProgress);
                maxHeight = Math.max(maxHeight, height);
            }
        }
    }

    public Scene02Rectangles(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        p.registerMethod("keyEvent", this);
    }

    @Override
    public void drawWall() {
        background(0);
        fill(255);
        textSize(24); 
        String text = "Scene Rectangles".toUpperCase();
        float textWidth = p.textWidth(text);
        float textHeight = p.textAscent() + p.textDescent();
        text(text, width() - textWidth - 20, 20 + textHeight);

        // Save the current transformation state
        p.pushMatrix();
        // Translate to the bottom of the wall
        p.translate(0, height());
        // Call display with the translated coordinates
        display();
        // Restore the transformation state
        p.popMatrix();
    }

    @Override
    public void drawFloor() {
        display();
    }

    public void keyEvent(processing.event.KeyEvent event) {
        if (event.getAction() == processing.event.KeyEvent.PRESS) {
            if (event.getKey() == 'q' || event.getKey() == 'Q') {
                isAnimating = true;
                // Reset animation progress for all rectangles
                for (Rectangle rect : rectangles.values()) {
                    rect.animationProgress = 0;
                }
            } else if (event.getKey() == 'w' || event.getKey() == 'W') {
                for (Rectangle rect : rectangles.values()) {
                    if (!rect.isFixed) {
                        rect.isFixed = true;
                    }
                }
            }
        }
    }

    private void display() {
        background(0);

        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();
        
        // Update or create rectangles for each cursor
        for (TuioCursor tcur : tuioCursorList) {
            int cursorId = tcur.getCursorID();
            if (!rectangles.containsKey(cursorId)) {
                rectangles.put(cursorId, new Rectangle(
                    tcur.getScreenX(this.width()),
                    tcur.getScreenY(this.height())
                ));
            }
            
            Rectangle rect = rectangles.get(cursorId);
            if (!rect.isFixed) {
                rect.x = tcur.getScreenX(this.width());
                rect.y = tcur.getScreenY(this.height());
            }
            rect.updateAnimation();
        }

        // Draw all rectangles
        p.noStroke();
        p.fill(255);
        for (Rectangle rect : rectangles.values()) {
            p.rect(rect.x - rect.width/2, rect.y - rect.height/2, rect.width, rect.height);
        }

        // Draw cursor paths just for debugging
        p.stroke(p.color(0, 0, 255));
        for (TuioCursor tcur : tuioCursorList) {
            ArrayList<TuioPoint> pointList = tcur.getPath();
            if (!pointList.isEmpty()) {
                TuioPoint startPoint = pointList.get(0);
                for (TuioPoint end_point : pointList) {
                    p.line(startPoint.getScreenX(this.width()), startPoint.getScreenY(this.height()), 
                         end_point.getScreenX(this.width()), end_point.getScreenY(this.height()));
                    startPoint = end_point;
                }
            }
        }
    }
}
