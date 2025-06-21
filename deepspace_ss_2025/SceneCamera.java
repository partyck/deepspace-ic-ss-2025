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
    private TuioClient tracker;
    private Capture cam;
    private int noiseDetail = 4;
    private LinkedList<int[]> buffer;
    private ArrayList<Dancer> dancers;

    int windowWidth;
    int vwX, vwY;
    int hwX, hwY;
    int bufferOffset = 0;
    boolean delay = true;
    boolean showCamera = false;
    float haloIterations = 100;
    int alphaFade = 255;
    float alpha = 6;
    int spotDiameter = 100;
    int[] colors;


    public SceneCamera(PApplet p, Capture cam, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        colors = new int[]{
            color(63, 0, 255),
            color(11, 56, 158),
            color(35, 41, 122),
            color(0, 71, 171)
        };
        dancers = new ArrayList<>();
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
    public void init() {
        rectMode(PConstants.CORNER);
        translate(0, 0);
    }

    @Override
    public void drawWall() {
        background(0);
        if (!showCamera) return;
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
        fill(0, alphaFade);
        noStroke();
        rect(0, 0, width(), height());
        
        updateDancers();

        
        for (int di = 0; di < dancers.size(); di++) {
            Dancer d = dancers.get(di);
            fill(colors[di % colors.length], alpha);
            for (int i = 0; i < haloIterations; i++) {
                float diameter = spotDiameter * (i / haloIterations);
                circle(d.x, d.y, diameter);
            }
        }
    }

     @Override
    public void oscEvent(String path, float value) {
        System.out.println("oscEvent camera");
        switch(path) {
            case "/cam2/toggle5":
                delay = value == 1;
                System.out.println("    delay: "+delay);
                break;
            case "/cam2/toggle8":
                showCamera = value == 1;
                System.out.println("    showCamera: "+showCamera);
                break;
            case "/cam2/fader30":
                bufferOffset = (int) map(value, 0, 1, 0, 300);
                System.out.println("    bufferOffset: "+bufferOffset+ "; "+bufferOffset * 0.33f+ "; "+bufferOffset * 0.66f);
                break;
            case "/cam2/fader33":
                alpha = map(value, 0, 1, 0, 50);
                System.out.println("    alpha: "+alpha);
                break;
            case "/cam2/fader34":
                alphaFade = (int ) (value * 255);
                System.out.println("    alphaFade: "+alphaFade);
                break;
            case "/cam2/fader35":
                spotDiameter = (int) (value * width() * 0.2f);
                System.out.println("    spotDiameter: "+spotDiameter);
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

    private void updateDancers() {
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();
        for (int dI = 0; dI < dancers.size(); dI++) {
            boolean toDelete = true;
            Dancer dancer = dancers.get(dI);
            for (int tI = 0; tI < tuioCursorList.size(); tI++) {
                TuioCursor cursor = tuioCursorList.get(tI);
                if (dancer.isLinkedTo(cursor)) {
                    dancer.update(cursor);
                    toDelete = false;
                    break;
                }
            }
            if (toDelete) {
                dancers.remove(dI);
                dI--;
            }
        }

        if (tuioCursorList.size() > dancers.size()) {
            for (int tI = 0; tI < tuioCursorList.size(); tI++) {
                TuioCursor cursor = tuioCursorList.get(tI);
                boolean toAdd = true;
                for (int dI = 0; dI < dancers.size(); dI++) {
                    Dancer dancer = dancers.get(dI);
                    if (dancer.isLinkedTo(cursor)) {
                        toAdd = false;
                        break;
                    }
                }
                if (toAdd) {
                    dancers.add(new Dancer(cursor));
                }
            }
        }

    }

    // inner classes

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
