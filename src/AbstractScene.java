import processing.core.PApplet;

public abstract class AbstractScene {
    protected final PApplet p;

    public AbstractScene(PApplet p) {
        this.p = p;
    }

    public abstract void draw();

    public void newString(String newLine) {
        if (!newLine.isBlank()) System.out.println("Key pressed! " + newLine);
    }

    /**
     * Here we have all  processing methods we need
     * Feel free to all one if you need it
     */
    public void arc(float a, float b, float c, float d, float start, float stop) { p.arc(a, b, c, d, start, stop); }
    public void background(int c) { p.background(c); }
    public int color(float r, float g, float b) { return p.color(r, g, b); }
    public void ellipse(float x, float y, float width, float height) { p.ellipse(x, y, width, height); }
    public void fill(int c) { p.fill(c); }
    public void fill(float r, float g, float b) { p.fill(r, g, b); }
    public int height() { return p.height; }
    public float lerp(float a, float b, float t) { return PApplet.lerp(a, b, t); }
    public int lerpColor(int c1, int c2, float atm) {return p.lerpColor(c1, c2, atm); }
    public void line(float x1, float y1, float x2, float y2) {p.line(x1, y1, x2, y2); }
    public void noStroke() { p.noStroke(); }
    public void popMatrix() { p.popMatrix(); }
    public void popStyle() { p.popStyle(); }
    public void pushMatrix() { p.pushMatrix(); }
    public void pushStyle() { p.pushStyle(); }
    public void rect(float x, float y, float w, float h) { p.rect(x, y, w, h); }
    public void rectMode(int mode) { p.rectMode(mode); }
    public void stroke(int c) { p.stroke(c); }
    public void stroke(float r, float g, float b) { p.stroke(r, g, b); }
    public void text(String str, float x, float y) { p.text(str, x, y); }
    public void tint(int rgb, float alpha) { p.tint(rgb, alpha); }
    public void translate(float x, float y) { p.translate(x, y); }
    public int width() { return p.width; }
    public void circle(float x, float y, float extent) { p.circle(x, y, extent);}
}
