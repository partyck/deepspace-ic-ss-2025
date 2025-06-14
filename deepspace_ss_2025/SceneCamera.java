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
    private TuioClient tracker;
    private int noiseDetail = 4;

    private ArrayList<Dancer> dancers;
    private ArrayList<Dancer> hull;
    private LinkedList<int[]> buffer;

    private NoiseGrid grid;
    private float alphaFade = 3.0f;

    boolean showWallGrid = true;

    float noiseScale = 0.001f;
    float baseNoiseAmount = 10;
    float influenceRadius = 10;
    float maxPush = 300;
    float lerpAmount = 0.05f;
    float[][] offsets;
    int circles = 8;
    int points = 32;
    int separation = 50;

    public SceneCamera(PApplet p, Capture cam, TuioClient tracker) {
        super(p);
        this.tracker  = tracker;
        grid = NoiseGrid.getInstance();
        dancers = new ArrayList<>();
        hull = new ArrayList<>();
        if (p instanceof Floor) {
            grid.setFloor(this);
        } else {
            grid.setWall(this);
            this.cam = cam;
            buffer = new LinkedList<>();
        }
        grid.init();
    }

    @Override
    public void init() {
        noiseDetail(noiseDetail);
        circles = floor(width() / separation);
        updateGrid();
        background(0);
    }

    @Override
    public void drawWall() {
        // fill(0, alphaFade);
        // noStroke();
        // rect(0, 0, width(), height());
        background(0);

        if (cam.available()) {
            cam.read();
            cam.filter(12);
            cam.loadPixels();
            cam.updatePixels();
        }
        PImage frame = cam.get();
        frame.filter(12);
        float aspectRatio = (float) frame.height / (float) frame.width;

        buffer.add(frame.pixels);
        if (buffer.size() > 300) {
            int[] bufferFrame = buffer.remove();
            int[] bufferFrame1 = buffer.get(100);
            int[] bufferFrame2 = buffer.get(200);
            PImage bufferImage = new PImage(frame.width, frame.height, bufferFrame, false, p);
            PImage bufferImage1 = new PImage(frame.width, frame.height, bufferFrame1, false, p);
            PImage bufferImage2 = new PImage(frame.width, frame.height, bufferFrame2, false, p);
            // image(bufferImage, 0, 0, width() * 0.25f, width() * 0.25f * aspectRatio);
            // image(bufferImage1, width() - (width() * 0.25f), 0, width() * 0.25f, width() * 0.25f * aspectRatio);
            // image(bufferImage2, width() - (width() * 0.25f), height() - width() * 0.25f * aspectRatio, width() * 0.25f, width() * 0.25f * aspectRatio);
            image(bufferImage, 0, 0);
            image(bufferImage1, width() - (width() * 0.25f), 0);
            image(bufferImage2, width() - (width() * 0.25f), height() - width() * 0.25f * aspectRatio);
        }

        image(cam, 0, 0, 0, 0);
        image(frame, width() * 0.25f, (height() - width() * 0.5f * aspectRatio) * 0.5f, width() * 0.5f, width() * 0.5f * aspectRatio);

        System.out.println("wall frameRate: "+frameRate());
    }

    @Override
    public void drawFloor() {
        fill(0, alphaFade);
        noStroke();
        rect(0, 0, width(), height());
        updateDancers();
        calculateConvexHull();
        
        for (Dancer dancer : dancers) {
            grid.affect(dancer.x, dancer.y);
        }

        stroke(255);
        strokeWeight(1);
        noFill();
        drawCircles();
        
        // grid.update();
        // grid.displayFloor();

        stroke(255, 0, 0);
        strokeWeight(6);
        for (Dancer p : dancers) {
            point(p.x, p.y);
        }
        // System.out.println("floor frameRate: "+frameRate());
    }

    private void drawCircles() {
        float centerX = width() / 2;
        float centerY = height() / 2;
        float zoff = frameCount() * 0.01f;
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();

        for (int circleI = 0 ; circleI < circles ; circleI++) {
            int noiseRadius = (circleI + 1) * separation;
            beginShape();
            for (int angleI = 0; angleI < points; angleI++) {
                float radius = offsets[circleI][angleI];
                float angle = map(angleI, 0, points, 0, PConstants.TWO_PI);
                int x = (int) (centerX + cos(angle) * radius);
                int y = (int) (centerY + sin(angle) * radius);
                int xn = (int) (centerX + cos(angle) * noiseRadius);
                int yn = (int) (centerY + sin(angle) * noiseRadius);

                float n = noise(xn * noiseScale, yn * noiseScale, zoff);
                float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);
                float influence = 0;
                
                for(TuioCursor cursor: tuioCursorList) {
                    int px = cursor.getScreenX(width());
                    int py = cursor.getScreenY(height());
                    float dx = x - px;
                    float dy = y - py;
                    float d = dist(x, y, px, py);

                    if (d < influenceRadius) {
                        float strength = 1 - (d / influenceRadius);
                        strength *= strength;
                        float dRadius = dist(centerX, centerY, px, py);
                        influence = (radius - dRadius) * strength * maxPush;
                    }
                }
                float target = radius + baseWave + influence;
                offsets[circleI][angleI] = lerp(radius, target, lerpAmount);

                x = (int) (centerX + cos(angle) * offsets[circleI][angleI]);
                y = (int) (centerY + sin(angle) * offsets[circleI][angleI]);
                curveVertex(x, y);
            }

            for (int angleI = 0; angleI < 3; angleI++) {
                float angle = map(angleI, 0, points, 0, PConstants.TWO_PI);
                int x = (int) (centerX + cos(angle) * offsets[circleI][angleI]);
                int y = (int) (centerY + sin(angle) * offsets[circleI][angleI]);
                curveVertex(x, y);
            }
            endShape(PConstants.CLOSE);
        }

        // for (int circleI = 0; circleI < circles; circleI++) {
        //     for (int angleI = 0; angleI < points; angleI++) {
        //         float radius = offsets[circleI][angleI];
        //         float angle = map(angleI, 0, points, 0, PConstants.TWO_PI);
        //         int x = (int) (centerX + cos(angle) * radius);
        //         int y = (int) (centerY + sin(angle) * radius);
        //         stroke(255, 0, 0);
        //         strokeWeight(6);
        //         point(x, y);
        //     }
        // }
    }

    void updateGrid() {
        offsets = new float[circles][points];
        for (int circleI = 0; circleI < circles; circleI++) {
            int radius = (circleI + 1) * separation;
            for (int angleI = 0; angleI < points; angleI++) {
                offsets[circleI][angleI] = radius;
            }
        }
    }

    @Override
    public void midiIn(int slider, int value) {
        // grid.midiIn(slider, value);
        switch(slider) {
            case 0:
                NoiseGrid.speed = value / 127f;
                System.out.println("    grid.speed: "+NoiseGrid.speed);
                break;
            case 1:
                NoiseGrid.noiseScale = value / 127f;
                System.out.println("    grid.noiseScale: "+NoiseGrid.noiseScale);
                break;
            case 2:
                NoiseGrid.speedFill = value / 127f;
                System.out.println("    grid.speedFill: "+NoiseGrid.speedFill);
                break;
            case 3:
                NoiseGrid.noiseScaleFill = value / 127f;
                System.out.println("    grid.noiseScaleFill: "+NoiseGrid.noiseScaleFill);
                break;
            case 4:
                noiseDetail = floor(map(value, 0, 127, 1, 8));
                noiseDetail(noiseDetail);
                System.out.println("    noiseDetail: "+noiseDetail);
                break;
            case 5:
                NoiseGrid.noiseLinesForceStrength = value / 127f;
                System.out.println("    grid.noiseLinesForceStrength: "+NoiseGrid.noiseLinesForceStrength);
                break;
            case 6:
                alphaFade = 255 * (value / 127f);
                System.out.println("    alphaFade: "+alphaFade);
                break;
            case 7:
                grid.displayTreshold = 1 - (value / 127f);
                System.out.println("    displayTreshold: "+grid.displayTreshold);
                break;
            case 64:
                grid.linesOn = value == 127;
                System.out.println("    linesOn: "+grid.linesOn + " value: "+value);
                break;
            case 48:
                grid.tilesOn = value == 127;
                System.out.println("    tilesOn: "+grid.tilesOn + " value: "+value);
                break;
            default:
                System.out.println("    default: "+value);
        }
    }

    @Override
    public void oscEvent(String path, float value) {
        System.out.println("oscEvent camera");
        switch(path) {
            case "/cam/fader2":
                NoiseGrid.speed = value;
                System.out.println("    grid.speed: "+NoiseGrid.speed);
                break;
            case "/cam/fader3":
                noiseScale = map(value, 0, 1, 0.0001f, 0.1f);
                System.out.println("    noiseScale: "+noiseScale);
                break;
            case "/cam/fader4":
                NoiseGrid.speedFill = map(value, 0, 1, 0.000001f, 0.1f);
                System.out.println("    grid.speedFill: "+NoiseGrid.speedFill);
                break;
            case "/cam/fader5":
                NoiseGrid.noiseScaleFill = value;
                System.out.println("    grid.noiseScaleFill: "+NoiseGrid.noiseScaleFill);
                break;
            case "/cam/fader6":
                noiseDetail = floor(map(value, 0, 1, 1, 8));
                noiseDetail(noiseDetail);
                System.out.println("    noiseDetail: "+noiseDetail);
                break;
            case "/cam/fader7":
                NoiseGrid.noiseLinesForceStrength = value;
                System.out.println("    grid.noiseLinesForceStrength: "+NoiseGrid.noiseLinesForceStrength);
                break;
            case "/cam/fader8":
                alphaFade = 255 * value;
                System.out.println("    alphaFade: "+alphaFade);
                break;
            case "/cam/fader9":
                grid.displayTreshold = 1 - value;
                System.out.println("    displayTreshold: "+grid.displayTreshold);
                break;
            case "/cam/toggle1":
                grid.linesOn = value == 1.0f;
                System.out.println("    linesOn: "+grid.linesOn + " value: "+value);
                break;
            case "/cam/toggle2":
                grid.tilesOn = value == 1.0f;
                System.out.println("    tilesOn: "+grid.tilesOn + " value: "+value);
                break;
            case "/cam/toggle3":
                showWallGrid = value == 1.0f;
                System.out.println("    showWallGrid: "+showWallGrid);
                break;
            default:
                System.out.println("    default: "+value);
        }
    }
    
    @Override
    public void keyPressed(char key, int keyCode) {
        switch(key) {
            case 't':
                grid.tilesOn = !grid.tilesOn;
                System.out.println("    grid.linesOn: "+grid.tilesOn);
                break;
            case 'l':
                grid.linesOn = !grid.linesOn;
                System.out.println("    grid.linesOn: "+grid.linesOn);
                break;
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
            x = cursor.getScreenX(width());
            y = cursor.getScreenY(height());
        }
    }
}
