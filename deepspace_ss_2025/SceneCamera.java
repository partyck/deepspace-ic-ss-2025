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
    int bufferOffset = 0;
    boolean delay = true;

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
        if (cam == null) return;
        if (cam.available()) {
            cam.read();
            cam.filter(12);
            cam.loadPixels();
            cam.updatePixels();
        }
        PImage frame = cam.get();
        float aspectRatio = (float) frame.height / (float) frame.width;

        buffer.add(frame.pixels);
        image(cam, 0, 0, 0, 0);
        image(frame, width() * 0.25f, (height() - width() * 0.5f * aspectRatio) * 0.5f, width() * 0.5f, width() * 0.5f * aspectRatio);

        if (buffer.size() > 300) {
            if (delay) {
                PImage bufferImage = getSubset(buffer.get(300 - bufferOffset) , 0, 0, frame.width * 0.5f, frame.width * 0.5f * aspectRatio);
                PImage bufferImage1 = getSubset(buffer.get(300 - (int) (bufferOffset * 0.33f)), frame.width * 0.5f, 0, frame.width * 0.5f, frame.width * 0.5f * aspectRatio);
                PImage bufferImage2 = getSubset(buffer.get(300 - (int) (bufferOffset * 0.66f)), 0, frame.height * 0.5f, frame.width * 0.5f, frame.width * 0.5f * aspectRatio);
                image(bufferImage, width() * 0.25f, (height() - width() * 0.5f * aspectRatio) * 0.5f, width() * 0.25f, width() * 0.25f * aspectRatio);
                image(bufferImage1, width() * 0.5f, (height() - width() * 0.5f * aspectRatio) * 0.5f, width() * 0.25f, width() * 0.25f * aspectRatio);
                image(bufferImage2, width() * 0.25f, height() * 0.5f, width() * 0.25f, width() * 0.25f * aspectRatio);
            }
            buffer.remove();
        }

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

     @Override
    public void oscEvent(String path, float value) {
        System.out.println("oscEvent camera");
        switch(path) {
            case "/cam2/toggle5":
                delay = value == 1;
                System.out.println("    delay: "+delay);
                break;
            case "/cam2/fader30":
                bufferOffset = (int) map(value, 0, 1, 0, 300);
                System.out.println("    bufferOffset: "+bufferOffset+ "; "+bufferOffset * 0.33f+ "; "+bufferOffset * 0.66f);
                break;
        }
    }

    PImage getSubset(int[] source, float xf, float yf, float wf, float hf) {
        int x = (int) xf;
        int y = (int) yf;
        int w = (int) wf;
        int h = (int) hf;
        int[] subset = new int[w * h];

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
            int sourceX = x + i;
            int sourceY = y + j;

            int sourceIndex = sourceY * Constants.CAMERA_WIDTH + sourceX;
            int destIndex = j * w + i;

            subset[destIndex] = source[sourceIndex];
            }
        }

        return new PImage(w, h, subset, false, p);
    }
}
