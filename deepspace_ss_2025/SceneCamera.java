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
    private int aphaTint = 10;
    private int noiseDetail = 2;
    private PImage circleMask;

    private ArrayList<Dancer> dancers;
    private ArrayList<Dancer> hull;

    private NoiseGrid grid;
    private float alphaFade = 3.0f;


    public SceneCamera(PApplet p, Capture cam, TuioClient tracker) {
        super(p);
        this.tracker  = tracker;
        grid = NoiseGrid.getInstance();
        dancers = new ArrayList<>();
        if (p instanceof Floor) {
            grid.setFloor(this);
        } else {
            grid.setWall(this);
            this.cam = cam;
            PImage frame = cam.get();
            buffer = createGraphics(frame.width, frame.height);
            circleMask = loadImage("circle_mask.jpg");
        }
        grid.init();
    }

    @Override
    public void init() {
        p.imageMode(PConstants.CENTER);
        noiseDetail(noiseDetail);
    }

    @Override
    public void drawWall() {
        fill(0, alphaFade);
        noStroke();
        rect(0, 0, width(), height());
        grid.displayWall();

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
        // System.out.println("wall frameRate: "+frameRate());
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
        
        grid.update();
        grid.displayFloor();

        stroke(255, 0, 0);
        strokeWeight(6);
        for (Dancer p : dancers) {
            point(p.x, p.y);
        }
        // System.out.println("floor frameRate: "+frameRate());
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
