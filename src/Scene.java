/**
 * Scene.java
 * called when scene is activated/exit/changed
 * If you want fade, use tint() or pushStyle()/popStyle() in here.
 */
import processing.core.PApplet;

public interface Scene {
    void enter();
    void exit();
    void update();

    void render(PApplet p, float yOff, float alpha);
    int getFullHeight();
}
