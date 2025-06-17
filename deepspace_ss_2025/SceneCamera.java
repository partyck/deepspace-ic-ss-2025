import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.*;
import TUIO.*;

public class SceneCamera extends AbstractScene {
    private Capture cam;
    private int noiseDetail = 4;
    private LinkedList<int[]> buffer;

    int windowWidth;
    int vwX, vwY;
    int hwX, hwY;

    public SceneCamera(PApplet p, Capture cam) {
        super(p);
        windowWidth =  (int) (width() * 0.01);
        vwX = (width() / 2) - (windowWidth / 2);
        vwY = 0;
        hwX = 0;
        hwY = (height() / 2) - (windowWidth / 2);
        if (!(p instanceof Floor)) {
            this.cam = cam;
            buffer = new LinkedList<>();
        }
    }

    @Override
    public void drawWall() {
        background(0);

        if (cam.available()) {
            cam.read();
            cam.filter(12);
            cam.loadPixels();
            cam.updatePixels();
        }
        PImage frame = cam.get();
        float aspectRatio = (float) frame.height / (float) frame.width;

        buffer.add(frame.pixels);
        if (buffer.size() > 300) {
            int[] bufferFrame = buffer.remove();
            int[] bufferFrame1 = buffer.get(100);
            int[] bufferFrame2 = buffer.get(200);
            // PImage bufferImage = new PImage(frame.width, frame.height, bufferFrame, false, p);
            // PImage bufferImage1 = new PImage(frame.width, frame.height, bufferFrame1, false, p);
            // PImage bufferImage2 = new PImage(frame.width, frame.height, bufferFrame2, false, p);
            // image(bufferImage, 0, 0, width() * 0.25f, width() * 0.25f * aspectRatio);
            // image(bufferImage1, width() - (width() * 0.25f), 0, width() * 0.25f, width() * 0.25f * aspectRatio);
            // image(bufferImage2, width() - (width() * 0.25f), height() - width() * 0.25f * aspectRatio, width() * 0.25f, width() * 0.25f * aspectRatio);
            // image(bufferImage, 0, 0);
            // image(bufferImage1, width() - (width() * 0.25f), 0);
            // image(bufferImage2, width() - (width() * 0.25f), height() - width() * 0.25f * aspectRatio);
        }

        image(cam, 0, 0, 0, 0);
        image(frame, width() * 0.25f, (height() - width() * 0.5f * aspectRatio) * 0.5f, width() * 0.5f, width() * 0.5f * aspectRatio);

        noStroke();
        fill(0);
        rect(vwX, vwY, windowWidth, height());
        rect(hwX, hwY, width(), windowWidth);

        // System.out.println("wall frameRate: "+frameRate());
    }

    @Override
    public void drawFloor() {
        background(0);
    }
}
