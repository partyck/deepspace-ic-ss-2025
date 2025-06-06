import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.*;
import TUIO.*;

public class SceneCamera extends AbstractScene {
    private Capture cam;
    private TuioClient tracker;

    private PGraphics buffer;
    private int aphaTint = 20;
    private int noiseDetail = 2;
    private PImage circleMask;

    private ArrayList<Dancer> dancers;
    private ArrayList<Dancer> hull;

    private Grid grid;
    
    private static float timeOffset = 0f;
    private static float speed = 0.001f;

    public SceneCamera(PApplet p, Capture cam, TuioClient tracker) {
        super(p);
        this.tracker  = tracker;
        grid =  new Grid();
        dancers = new ArrayList<>();
        grid.isWall = !(p instanceof Floor);
        if (!(p instanceof Floor)) {
            this.cam = cam;
            PImage frame = cam.get();
            buffer = createGraphics(frame.width, frame.height);
            circleMask = loadImage("circle_mask.jpg");
        }
    }

    @Override
    public void init() {
        p.imageMode(PConstants.CENTER);
        noiseDetail(noiseDetail);
    }

    @Override
    public void drawWall() {
        background(0);

        if (cam.available()) {
            cam.read();
        }
        PImage frame = cam.get();
        frame.filter(12);

        int offsetD = (int) map(aphaTint, 0, 100, 0, 2);
        float offsetX = random(-1 * offsetD, offsetD);
        float offsetY = random(-1 * offsetD, offsetD);

        buffer.beginDraw();
        buffer.tint(255, aphaTint);
        buffer.image(frame, offsetX, offsetY);
        buffer.endDraw();
        buffer.mask(circleMask);

        float aspectRatio = (float) buffer.height / (float) buffer.width;
        image(buffer, width() * 0.5f, height() * 0.5f, width() * 0.5f, width() * 0.5f * aspectRatio);
        System.out.println("wall frameRate: "+frameRate());
        timeOffset += speed;
    }

    @Override
    public void drawFloor() {
        background(0);
        updateDancers();
        calculateConvexHull();
        
        for (Dancer dancer : dancers) {
            grid.affect(dancer.x, dancer.y);
        }
        
        grid.update();
        noStroke();
        fill(255);   
        grid.display();

        stroke(255, 0, 0);
        strokeWeight(6);
        for (Dancer p : dancers) {
            point(p.x, p.y);
        }

        System.out.println("floor frameRate: "+frameRate());
    }

    @Override
    public void oscEvent(String path, float value) {
        System.out.println("oscEvent camera");
        switch(path) {
            case "/cam/fader1":
                aphaTint = floor(map(value, 0, 1, 0, 50));
                System.out.println("    aphaTint: "+aphaTint);
                break;
            case "/cam/fader2":
                speed = value;
                System.out.println("    grid.speed: "+speed);
                break;
            case "/cam/fader3":
                // grid.noiseScale = map(value, 0, 1, 0.00001f, 1f);
                grid.noiseScale = value;
                System.out.println("    grid.noiseScale: "+grid.noiseScale);
                break;
            case "/cam/fader4":
                grid.speedFill = map(value, 0, 1, 0.000001f, 0.1f);
                System.out.println("    grid.speedFill: "+grid.speedFill);
                break;
            case "/cam/fader5":
                grid.noiseScaleFill = map(value, 0, 1, 0.00000001f, 0.1f);
                System.out.println("    grid.noiseScaleFill: "+grid.noiseScaleFill);
                break;
            case "/cam/fader6":
                noiseDetail = floor(map(value, 0, 1, 1, 8));
                noiseDetail(noiseDetail);
                System.out.println("    noiseDetail: "+noiseDetail);
                break;
            case "/cam/fader7":
                grid.noiseLinesForceStrength = value;
                System.out.println("    grid.noiseLinesForceStrength: "+grid.noiseLinesForceStrength);
                break;
            default:
                System.out.println("    default: "+value);
        }
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
                    dancers.add(new Dancer(this, cursor));
                }
            }
        }

    }

    void calculateConvexHull() {
        if (dancers.size() < 3) {
            hull = new ArrayList<Dancer>(dancers);
            return;
        }

        Collections.sort(dancers, new Comparator<Dancer>() {
            @Override
            public int compare(Dancer p1, Dancer p2) {
            if (p1.x != p2.x) {
                return Float.compare(p1.x, p2.x);
            }
            return Float.compare(p1.y, p2.y);
            }
        });

        ArrayList<Dancer> upper = new ArrayList<Dancer>();
        for (Dancer p : dancers) {
            while (upper.size() >= 2 && crossProduct(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
            upper.remove(upper.size() - 1);
            }
            upper.add(p);
        }

        ArrayList<Dancer> lower = new ArrayList<Dancer>();
        for (int i = dancers.size() - 1; i >= 0; i--) {
            Dancer p = dancers.get(i);
            while (lower.size() >= 2 && crossProduct(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(p);
        }
        hull.clear();
        hull.addAll(upper);
        
        if (!lower.isEmpty() && !upper.isEmpty() && lower.get(lower.size() - 1).equals(upper.get(0))) {
            lower.remove(lower.size() - 1);
        }

        if (!lower.isEmpty() && !(hull.size() > 0 && lower.get(0).equals(hull.get(hull.size() -1)))) {
            lower.remove(0);
        } else if (!lower.isEmpty() && hull.size() > 0 && lower.get(0).equals(upper.get(upper.size() -1))) {
            lower.remove(0);
        }
        
        hull.addAll(lower);
        
        if (hull.size() > 1 && hull.get(0).equals(hull.get(hull.size() - 1))) {
            hull.remove(hull.size() - 1);
        }
    }

    float crossProduct(Dancer p1, Dancer p2, Dancer p3) {
        return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
    }


    // inner classes

    private class Dancer {
        float x;
        float y;
        long cursorId;
        SceneCamera scene;
        
        Dancer(SceneCamera scene, TuioCursor cursor) {
            this.scene = scene;
            this.cursorId = cursor.getSessionID();
            x = cursor.getScreenX(scene.width()); 
            y = cursor.getScreenY(scene.height());
        }

        boolean isLinkedTo(TuioCursor cursor) {
            return cursorId == cursor.getSessionID();
        }

        void update(TuioCursor cursor) {
            // x = cursor.getScreenX(Constants.WIDTH) - width() / 2f;
            // y = cursor.getScreenY(Constants.FLOOR_HEIGHT) - height() / 2f;
            x = cursor.getScreenX(Constants.WIDTH);
            y = cursor.getScreenY(Constants.FLOOR_HEIGHT);
        }
    }

    private class Grid {
        int xLines;
        int yLines;
        ArrayList<Integer> xs;
        ArrayList<Integer> ys;
        
        // float timeOffset;
        // float speed;
        float noiseScale;
        
        float timeOffsetFill;
        float speedFill;
        float noiseScaleFill;

        float affectDistance;
        float noiseLinesForceStrength;
        boolean isWall = true;

        private Grid() {
            xLines = width() / 40;
            yLines = height() / 40;
            xs = new ArrayList();
            ys = new ArrayList();

            for(int i = 0; i < xLines; i++) {
                xs.add(floor((i + 1) * width() / (float) xLines));
            }
            for(int j = 0; j < yLines; j++) {
                ys.add(floor((j + 1) * height() / (float) yLines));
            }
            
            // timeOffset = 0f;
            // speed = 0.001f;
            noiseScale = 0.0001f;
            
            timeOffsetFill = 0f;
            speedFill = 0.001f;
            noiseScaleFill = 0.000001f;

            affectDistance = width() * 0.2f;

            noiseLinesForceStrength = 0.1f;
            
            updateNoiseDistances();
        }

        void affect(float targetX, float targetY) {
            println("Affecting  : " + targetX + ", X: " + xs.size() );
            for(int i = 0; i < xs.size(); i++) {
                int currentX = xs.get(i);
                if (Math.abs(targetX - currentX) < affectDistance) {
                    xs.remove(i);
                    float forceX = targetX - currentX;
                    float distance = abs(forceX);
                    float strength = pow(map(affectDistance - distance, 0, affectDistance, 0, 1), 2);
                    xs.add(i, floor(currentX + forceX * strength));
                }
            }
            for(int j = 0; j < ys.size(); j++) {
                int currentY = ys.get(j);
                if (Math.abs(targetY - currentY) < affectDistance) {
                    ys.remove(j);
                    float forceY = targetY - currentY;
                    float distance = abs(forceY);
                    float strength = pow(map(affectDistance - distance, 0, affectDistance, 0, 1), 2);
                    ys.add(j, floor(currentY + forceY * strength));
                }
            }
        }

        void update() {
            updateNoiseDistances();
        }

        void display() {
            // ----------GREEN LINES
            stroke(0, 255, 0);
            strokeWeight(1);
            for(int i = 0; i < xs.size() -1; i++ ) {
                line(xs.get(i), 0, xs.get(i), height());
            }
            for(int i = 0; i < ys.size() -1; i++ ) {
                line(0, ys.get(i), width(), ys.get(i));
            }

            // float prevX = 0;
            // float fx0 = (xs.size() - xs.size() * noiseScaleFill) / 2f;
            // float fy0 = (ys.size() - ys.size() * noiseScaleFill) / 2f;
            // for(int i = 0; i < xs.size(); i++) {
            //     int x = xs.get(i);
            //     float prevY = 0;
            //     for(int j = 0; j < ys.size(); j++) {
            //         int y = ys.get(j);
            //         float n = noise(fx0 + i * noiseScaleFill, fy0 + j * noiseScaleFill, timeOffsetFill);
            //         if (n > 0.5) {
            //             fill(map(n, 0.5f, 1, 150, 260));
            //             rect(prevX, prevY, x - prevX, y - prevY);
            //         }
            //         prevY = y;
            //     }
            //     prevX = x;
            // }
        }

        private void updateNoiseDistances() {
            updateDistances(xs, xLines, width(), 1000);
            // updateDistances(ys, yLines, height(), 2000);
            // timeOffset += speed;
            timeOffsetFill += speedFill;
        }

        private void updateDistances(ArrayList<Integer> lines, int nLines, int totalDistance, int noiseOffset) {
            float[] hns = new float[nLines];
            float total = 0;
            for(int i = 0; i < nLines; i++ ) {
                System.out.println("i: " + i + ", noiseOffset: " + noiseOffset + i * noiseScale + ", timeOffset: " + timeOffset);
                hns[i] = noise(noiseOffset + i * noiseScale, timeOffset);
                total += hns[i];
            }
            
            int accumulation = 0;
            for(int i = 0; i < nLines; i++ ) {
                accumulation += (int) totalDistance * (hns[i] / total);
                int value = (i == nLines - 1) ? totalDistance : accumulation;
                int currentV = lines.remove(i);
                lines.add(i, currentV + floor((value - currentV) * noiseLinesForceStrength));
            }
        }

        private float signum(float n) {
            if (n > 0) return 1;
            if (n < 0) return -1;
            return 0;
        }
    }

    class Tile {
        int x;
        int y;
        int w;
        int h;

        Tile(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
