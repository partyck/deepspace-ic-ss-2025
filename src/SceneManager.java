// SceneManager.java
import processing.core.PApplet;
import java.util.*;

public class SceneManager {
    private final List<Scene> scenes = new ArrayList<>();
    private Scene current, next;
    private float t = 1;        // 0…1 fade progress
    private final int halfH;

    public SceneManager(int halfHeight) {
        this.halfH = halfHeight;
    }

    public void addScene(Scene s) {
        scenes.add(s);
        if (current == null) {
            current = s;
            current.enter();
        }
    }

    /** jump to scene index i with fade */
    public void switchTo(int i) {
        if (i < 0 || i >= scenes.size() || scenes.get(i) == current) return;
        next = scenes.get(i);
        next.enter();
        t = 0;  // start fade
    }

    public void update() {
        if (next != null) {
            t += 0.02f; // fade speed
            if (t >= 1) {
                current.exit();
                current = next;
                next = null;
                t = 1;
            }
        }
        current.update();
        if (next != null) next.update();
    }

    public void render(PApplet p, boolean lowerHalf) {
        float yOff = lowerHalf ? -halfH : 0;
        // draw current
        current.render(p, yOff, next == null ? 1 : (1 - t));
        // if transitioning, draw next on top
        if (next != null) {
            next.render(p, yOff, t);
        }
    }
}
