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
    private int noiseDetail = 2;

    private ArrayList<Dancer> dancers;
    private ArrayList<Dancer> hull;

    private NoiseGrid grid;
    private float alphaFade = 3.0f;

    boolean showWallGrid = false;

    float baseNoiseAmount = 80;
    float influenceRadius = 400;
    float maxPush = 300;
    float lerpAmount = 0.05f;
    float[][] offsets;
    int cols, rows;
    int circles = 10;
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
        }
        grid.init();
        updateGrid();
    }

    @Override
    public void init() {
        p.imageMode(PConstants.CENTER);
        noiseDetail(noiseDetail);
        background(0);
    }

    @Override
    public void drawWall() {
        fill(0, alphaFade);
        noStroke();
        rect(0, 0, width(), height());

        if (showWallGrid) {
            grid.displayWall();
        }

        if (cam.available()) {
            cam.read();
            cam.filter(12);
            cam.loadPixels();
            cam.updatePixels();
        }
        PImage frame = cam.get();
        frame.filter(12);

        float aspectRatio = (float) frame.height / (float) frame.width;
        image(cam, 0, 0, 0, 0);
        image(frame, width() * 0.5f, height() * 0.5f, width() * 0.5f, width() * 0.5f * aspectRatio);
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

        translate(width() / 2, height() / 2);
        stroke(255);
        strokeWeight(1);
        noFill();
        drawCircles();
        
        // grid.update();
        // grid.displayFloor();

        // stroke(255, 0, 0);
        // strokeWeight(6);
        // for (Dancer p : dancers) {
        //     point(p.x, p.y);
        // }
        System.out.println("floor frameRate: "+frameRate());
    }

    private void drawCircles() {

        float zoff = frameCount() * 0.01f;
        for (int circleI = 0 ; circleI < circles ; circleI++) {
            int radius = circleI * separation;
            beginShape();
            for (int point = 0; point < points; point++) {
                float angle = map(point, 0, points, 0, PConstants.TWO_PI);
                int x = (int) cos(angle) * radius;
                int y = (int) sin(angle) * radius;

                float n = noise(circleI * 0.05f, angle * 0.05f, zoff);
                float baseWave = map(n, 0, 1, -baseNoiseAmount, baseNoiseAmount);
                
                float dx = x - mouseX();
                float dy = x - mouseY();
                float d = dist(x, y, mouseX(), mouseY());
                float influence = 0;

                if (d < influenceRadius) {
                    float strength = 1 - (d / influenceRadius);
                    strength *= strength;

                    float direction = dx > 0 ? 1 : -1;
                    float direction2 = direction * sin(n);
                    influence = direction2 * strength * maxPush;
                }

                float target = radius + baseWave + influence;
                offsets[circleI][point] = lerp(offsets[circleI][point], target, lerpAmount);

                x = (int) (cos(angle) * offsets[circleI][point]);
                y = (int) (sin(angle) * offsets[circleI][point]);
                curveVertex(x, y);
            }

            for (int point = 0; point < 3; point++) {
                float angle = map(point, 0, points, 0, PConstants.TWO_PI);
                int x = (int) (cos(angle) * offsets[circleI][point]);
                int y = (int) (sin(angle) * offsets[circleI][point]);
                curveVertex(x, y);
            }
            endShape(PConstants.CLOSE);
        }

        // for (int r = 0 ; r < circles ; r++) {
        //     int radius = r * 100;
        //     for (int i = 0; i < points; i++) {
        //         float angle = map(i, 0, points, 0, PConstants.TWO_PI);
        //         float x = cos(angle) * radius;
        //         float y = sin(angle) * radius;
        //         stroke(255, 0, 0);
        //         strokeWeight(i + 1 * 2);
        //         point(x, y);
        //     }
        // }
    }

    void updateGrid() {
        offsets = new float[circles][points];
        for (int r = 0; r < circles; r++) {
            int radius = r * separation;
            for (int i = 0; i < points; i++) {
                offsets[r][i] = radius;
            }
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
                NoiseGrid.noiseScale = value;
                System.out.println("    grid.noiseScale: "+NoiseGrid.noiseScale);
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
