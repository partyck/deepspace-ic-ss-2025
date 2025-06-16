import processing.core.PApplet;
import TUIO.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Scene02Rectangles extends AbstractScene {
    TuioClient tracker;
    private static HashMap<Integer, Rectangle> rectangles = new HashMap<>();
    private static boolean isAnimating = false;

    private class Rectangle {
        float x, y;
        float width = Float.valueOf(width() / 10);
        float height = Float.valueOf(height() / 10);
        boolean isFixed = false;
        float maxHeight = 0;
        int animationType;
        float animationProgress = 0;
        float targetWidth;
        float targetHeight;
        float expansionSpeed = 2.0f;

        TuioCursor lastCursor = null;
        boolean wasCursorInside = false;

        Rectangle(float x, float y) {
            this.x = x;
            this.y = y;
            this.animationType = (int) random(6);
            switch (animationType) {
                case 0:
                    targetWidth = Float.valueOf((width() / 10) / 2);
                    targetHeight = Float.valueOf((height() / 10) * 8);
                    break;
                case 1:
                    targetWidth = Float.valueOf((width() / 10));
                    targetHeight = Float.valueOf((height() / 10) * 2);
                    break;
                case 2:
                    targetWidth = Float.valueOf((width() / 10) / 2);
                    targetHeight = Float.valueOf((height() / 10) * 5);
                    break;
                case 3:
                    targetWidth = Float.valueOf((width() / 10) / 2);
                    targetHeight = Float.valueOf((height() / 10) * 9);
                    break;
                case 4:
                    targetWidth = Float.valueOf((width() / 10));
                    targetHeight = Float.valueOf((height() / 10));
                    break;
                case 5:
                    targetWidth = Float.valueOf((width() / 10));
                    targetHeight = Float.valueOf((height() / 10) * 6);
                    break;
            }
        }

        void updateAnimation() {
            if (isAnimating) {
                animationProgress += 0.02;
                if (animationProgress > 1) animationProgress = 1;

                float easedProgress;
                switch (animationType) {
                    case 0: easedProgress = animationProgress; break;
                    case 1: easedProgress = 1 - (1 - animationProgress) * (1 - animationProgress); break;
                    case 2: easedProgress = animationProgress * animationProgress; break;
                    case 3: easedProgress = 1 - (float) Math.cos(animationProgress * Math.PI * 2); break;
                    case 4: easedProgress = (float) Math.sin(animationProgress * Math.PI * 4) * (1 - animationProgress) + animationProgress; break;
                    case 5: easedProgress = animationProgress * animationProgress * (3 - 2 * animationProgress); break;
                    default: easedProgress = animationProgress;
                }

                width = lerp(width, targetWidth, easedProgress);
                height = lerp(height, targetHeight, easedProgress);
                maxHeight = Math.max(maxHeight, height);
            }
        }

        void updateWithClosestCursor(ArrayList<TuioCursor> cursors, int sceneWidth, int sceneHeight) {
            TuioCursor closest = null;
            float minDist = Float.MAX_VALUE;

            for (TuioCursor tcur : cursors) {
                float cx = tcur.getScreenX(sceneWidth);
                float cy = tcur.getScreenY(sceneHeight);
                float d = dist(cx, cy, x, y);
                if (d < minDist) {
                    minDist = d;
                    closest = tcur;
                }
            }

            if (closest == null) return;

            float cx = closest.getScreenX(sceneWidth);
            float cy = closest.getScreenY(sceneHeight);

            boolean isInside = cx >= x - width / 2 && cx <= x + width / 2 &&
                            cy >= y - height / 2 && cy <= y + height / 2;

            if (!isInside) {
                // Expand continuously while cursor stays outside
                if (cx < x - width / 2) {
                    width += expansionSpeed;
                    x -= expansionSpeed / 2;
                } else if (cx > x + width / 2) {
                    width += expansionSpeed;
                    x += expansionSpeed / 2;
                }

                if (cy < y - height / 2) {
                    height += expansionSpeed;
                    y -= expansionSpeed / 2;
                } else if (cy > y + height / 2) {
                    height += expansionSpeed;
                    y += expansionSpeed / 2;
                }
            }

            // Update tracking state
            wasCursorInside = isInside;
            lastCursor = closest;
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

        p.pushMatrix();
        p.translate(0, height());
        display();
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
                for (Rectangle rect : rectangles.values()) {
                    rect.animationProgress = 0;
                }
            } else if (event.getKey() == 'w' || event.getKey() == 'W') {
                for (Rectangle rect : rectangles.values()) {
                    rect.isFixed = true;
                }
            }
        }
    }

    private void display() {
        background(0);
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();

        for (TuioCursor tcur : tuioCursorList) {
            int cursorId = tcur.getCursorID();
            if (!rectangles.containsKey(cursorId)) {
                rectangles.put(cursorId, new Rectangle(
                        tcur.getScreenX(this.width()),
                        tcur.getScreenY(this.height())
                ));
            }
        }

        for (Rectangle rect : rectangles.values()) {
            if (!rect.isFixed) {
                for (TuioCursor tcur : tuioCursorList) {
                    if (rect == rectangles.get(tcur.getCursorID())) {
                        rect.x = tcur.getScreenX(this.width());
                        rect.y = tcur.getScreenY(this.height());
                    }
                }
            } else {
                rect.updateWithClosestCursor(tuioCursorList, this.width(), this.height());
            }

            rect.updateAnimation();
        }

        p.noStroke();
        p.fill(255);
        for (Rectangle rect : rectangles.values()) {
            p.rect(rect.x - rect.width / 2, rect.y - rect.height / 2, rect.width, rect.height);
        }
    }
}
