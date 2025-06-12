import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PFont;
import processing.core.PImage;

public abstract class AbstractScene {
    protected final PApplet p;
    protected boolean isWall;

    public AbstractScene(PApplet p) {
        this.p = p;
        isWall = !(p instanceof Floor);
    }

    public abstract void drawFloor();
    public abstract void drawWall();
    public void oscEvent(String path, float value) {}
    public void init() {}
    public void keyPressed(char key, int keyCode) {}

    public void draw() {
        if (isWall) {
            drawWall();
        }
        else {
            drawFloor();
        }
    }

    /**
     * Here we have all  processing methods we need
     * Feel free to all one if you need it
     */
    // ---- Drawing basics ----
    public void background(int gray) { p.background(gray); }
    public void background(int r, int g, int b) { p.background(r, g, b); }
    public void fill(int rgb) { p.fill(rgb); }
    public void fill(float gray) { p.fill(gray); }
    public void fill(float gray, float alpha) { p.fill(gray, alpha); }
    public void fill(int r, int g, int b) { p.fill(r, g, b); }
    public void noFill() { p.noFill(); }
    public void stroke(int gray) { p.stroke(gray); }
    public void stroke(int r, int g, int b) { p.stroke(r, g, b); }
    public void strokeWeight(int w) { p.strokeWeight(w); }
    public void noStroke() { p.noStroke(); }
    public int color(float r, float g, float b) { return p.color(r, g, b); }
    public int lerpColor(int c1, int c2, float atm) {return p.lerpColor(c1, c2, atm); }

    // ---- Shapes ----
    public void arc(float a, float b, float c, float d, float start, float stop) { p.arc(a, b, c, d, start, stop); }
    public void circle(float x, float y, float extent) { p.circle(x, y, extent);}
    public void ellipse(float x, float y, float w, float h) { p.ellipse(x, y, w, h); }
    public void line(float x1, float y1, float x2, float y2) { p.line(x1, y1, x2, y2); }
    public void point(float x, float y) { p.point(x, y); }
    public void rect(float x, float y, float w, float h) { p.rect(x, y, w, h); }
    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        p.triangle(x1, y1, x2, y2, x3, y3);
    }
    public void beginShape() { p.beginShape(); }
    public void vertex(float x, float y) { p.vertex(x, y); }
    public void curveVertex(float x, float y) { p.curveVertex(x, y);}
    public void endShape() { p.endShape(); }
    public void endShape(int mode) { p.endShape(mode); }
    public void rectMode(int mode) { p.rectMode(mode); }

    // ---- Text ----
    public void text(String str, float x, float y) { p.text(str, x, y); }
    public void textSize(float size) { p.textSize(size); }
    public void textAlign(int horizAlign) { p.textAlign(horizAlign); }
    public void textAlign(int horizAlign, int vertAlign) { p.textAlign(horizAlign, vertAlign); }
    public void textFont(PFont font) { p.textFont(font); }
    public PFont createFont(String name, float size) { return p.createFont(name, size); }

    // ---- Images ----
    public PImage loadImage(String path) { return p.loadImage(path); }
    public void image(PImage img, float x, float y) { p.image(img, x, y); }
    public void image(PImage img, float x, float y, float w, float h) {p.image(img, x, y, w, h);}
    public void set(int x, int y, PImage img) { p.set(x, y, img); }
    public PGraphics createGraphics(int x, int y) { return p.createGraphics(x, y); }
    public PGraphics createGraphics(int x, int y, String render) { return p.createGraphics(x, y, render); }


    // ---- Input and time ----
    public float mouseX() { return p.mouseX; }
    public float mouseY() { return p.mouseY; }
    public boolean mousePressed() { return p.mousePressed; }
    public int millis() { return p.millis(); }

    // ---- Math and vectors ----
    public float abs(float n) { return PApplet.abs(n); }
    public int abs(int n) { return PApplet.abs(n); }
    public int floor(float n) { return PApplet.floor(n); }
    public float random(float high) { return p.random(high); }
    public float random(float low, float high) { return p.random(low, high); }
    public float dist(float x1, float y1, float x2, float y2) { return PApplet.dist(x1, y1, x2, y2); }
    public float map(float value, float start1, float stop1, float start2, float stop2) {return PApplet.map(value, start1, stop1, start2, stop2);}
    public float constrain(float amt, float low, float high) { return PApplet.constrain(amt, low, high); }
    public float cos(float angle) { return PApplet.cos(angle); }
    public float sin(float angle) { return PApplet.sin(angle); }
    public void noiseDetail(int lod) { p.noiseDetail(lod); }
    public float pow(float base, float exp) { return PApplet.pow(base, exp); }
    public float sqrt(float n) { return PApplet.sqrt(n); }
    public float noise(float x) { return p.noise(x); }
    public float noise(float x, float y) { return p.noise(x, y); }
    public float noise(float x, float y, float z) { return p.noise(x, y, z); }

    // ---- Canvas info ----
    public int width() { return p.width; }
    public int height() { return p.height; }
    public float frameCount() { return p.frameCount; }
    public float frameRate() { return p.frameRate; }
    public void frameRate(float fps) { p.frameRate(fps); }
    public void translate(int x, int y) { p.translate(x, y); }

    // --- Transform ---
    public void scale(float s) {p.scale(s); }
    public void translate(float x, float y) {p.translate(x, y); }

    // ---- Utility ----
    public float lerp(float a, float b, float t) { return PApplet.lerp(a, b, t); }
    public void println(Object obj) { PApplet.println(obj); }
    public void noLoop() { p.noLoop(); }
    public void loop() { p.loop(); }
}
