import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
    
import processing.core.PApplet;
    
public class NoiseGrid {
    int xLines;
    int yLines;
    int yWallLines;
    int yFloorLines;
    ArrayList<Integer> xs;
    ArrayList<Integer> ys;

    AbstractScene wall;
    AbstractScene floor;

    static float speed = 0.001f;
    static float timeOffset = 0f;
    static float noiseScale = 0.0001f;

    static float speedFill = 0.001f;
    static float timeOffsetFill = 0f;
    static float noiseScaleFill = 0.000001f;

    static float noiseLinesForceStrength = 0.1f;
    float affectDistance;

    int width;
    int height;
    int wallHeight;
    int floorHeight;
    
    private static NoiseGrid INSTANCE = null;

    public static NoiseGrid getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new NoiseGrid();
        }
        
        return INSTANCE;
    }

    public NoiseGrid() {
        xs = new ArrayList();
        ys = new ArrayList();
    }

    public void setWall(AbstractScene wall) { this.wall = wall; }
    public void setFloor(AbstractScene floor) { this.floor = floor; }


    public void init() {
        if (wall == null || floor == null) {
            return;
        }
        width = wall.width();
        wallHeight = wall.height();
        floorHeight = floor.height();
        height = wallHeight + floorHeight;

        xLines = width / 40;
        yWallLines = wallHeight / 40;
        yFloorLines = floorHeight / 40;
        yLines = yWallLines + yFloorLines;
        affectDistance = width * 0.2f;

        for(int i = 0; i < xLines; i++) {
            xs.add((int) Math.floor((i + 1) * width / (float) xLines));
        }
        for(int j = 0; j < yLines + 1; j++) {
            ys.add((int) Math.floor((j + 1) * height / (float) yLines));
        }

        updateNoiseDistances();
    }

    void affect(float targetX, float targetY) {
        System.out.println("Affecting  : " + targetX + ", X: " + xs.size() );
        for(int i = 0; i < xs.size(); i++) {
            int currentX = xs.get(i);
            if (Math.abs(targetX - currentX) < affectDistance) {
                xs.remove(i);
                float forceX = targetX - currentX;
                float distance = Math.abs(forceX);
                float strength = (float) Math.pow(PApplet.map(affectDistance - distance, 0, affectDistance, 0, 1), 2);
                xs.add(i, (int) Math.floor(currentX + forceX * strength));
            }
        }
        for(int j = 0; j < ys.size(); j++) {
            int currentY = ys.get(j);
            if (Math.abs(targetY - currentY + wallHeight) < affectDistance) {
                ys.remove(j);
                float forceY = targetY - currentY + wallHeight;
                float distance = Math.abs(forceY);
                float strength = (float) Math.pow(PApplet.map(affectDistance - distance, 0, affectDistance, 0, 1), 2);
                ys.add(j, (int) Math.floor(currentY + forceY * strength));
            }
        }
    }

    void update() {
        updateNoiseDistances();
        timeOffset += speed;
        timeOffsetFill += speedFill;
    }

    void displayWall() {
        // ----------GREEN LINES
        wall.stroke(0, 255, 0);
        wall.strokeWeight(1);
        // for(int i = 0; i < xs.size() -1; i++ ) {
        //     wall.line(xs.get(i), 0, xs.get(i), wallHeight);
        // }
        // for(int i = 0; i < ys.size() -1; i++ ) {
        //     wall.line(0, ys.get(i), width, ys.get(i));
        // }


        // wall.noStroke();
        float prevX = 0;
        float fx0 = (xs.size() - xs.size() * noiseScaleFill) / 2f;
        float fy0 = (ys.size() - ys.size() * noiseScaleFill) / 2f;
        for(int i = 0; i < xs.size(); i++) {
            int x = xs.get(i);
            float prevY = 0;
            for(int j = 0; j < ys.size() - 1; j++) {
                int y = ys.get(j);
                float n = wall.noise(fx0 + i * noiseScaleFill, fy0 + j * noiseScaleFill, timeOffsetFill);
                if (n > 0.5) {
                    wall.fill(PApplet.map(n, 0.5f, 1, 150, 260));
                    wall.rect(prevX, prevY, x - prevX, y - prevY);
                }
                prevY = y;
            }
            prevX = x;
        }
    }

    void displayFloor() {
        // ----------GREEN LINES
        // List<Integer> floorYs = ys.subList(yWallLines , ys.size());
        ArrayList<Integer> floorYs = floorYLines();
        floor.stroke(0, 255, 0);
        floor.strokeWeight(1);
        // for(int i = 0; i < xs.size() -1; i++ ) {
        //     floor.line(xs.get(i), 0, xs.get(i), floorHeight);
        // }
        // for(int i = 0; i < floorYs.size(); i++ ) {
        //     floor.line(0, floorYs.get(i), width, floorYs.get(i));
        // }

        // floor.noStroke();
        float prevX = 0;
        float fx0 = (xs.size() - xs.size() * noiseScaleFill) / 2f;
        float fy0 = ys.size() / 2f;
        for(int i = 0; i < xs.size(); i++) {
            int x = xs.get(i);
            float prevY = 0;
            for(int j = 0; j < floorYs.size(); j++) {
                int y = floorYs.get(j);
                float n = wall.noise(fx0 + i * noiseScaleFill, fy0 + j * noiseScaleFill, timeOffsetFill);
                if (n > 0.5) {
                    floor.fill(PApplet.map(n, 0.5f, 1, 150, 260));
                    floor.rect(prevX, prevY, x - prevX, y - prevY);
                }
                prevY = y;
            }
            prevX = x;
        }
    }

    private void updateNoiseDistances() {
        updateDistances(xs, xLines, width, 1000);
        updateDistances(ys, yLines, height, 2000);
    }

    private void updateDistances(ArrayList<Integer> lines, int nLines, int totalDistance, int noiseOffset) {
        float[] hns = new float[nLines];
        float total = 0;
        for(int i = 0; i < nLines; i++ ) {
            hns[i] = wall.noise(noiseOffset + i * noiseScale, timeOffset);
            total += hns[i];
        }
        
        int accumulation = 0;
        for(int i = 0; i < nLines; i++ ) {
            accumulation += (int) totalDistance * (hns[i] / total);
            int value = (i == nLines - 1) ? totalDistance : accumulation;
            int currentV = lines.remove(i);
            lines.add(i, currentV + (int) Math.floor((value - currentV) * noiseLinesForceStrength));
        }
    }

    private ArrayList<Integer> floorYLines() {
        ArrayList<Integer> floorYs = new ArrayList<>();
        for(int i = 0; i < ys.size() -1; i++ ) {
            if (ys.get(i) > wallHeight) {
                floorYs.add(ys.get(i) - wallHeight);
            }
        }
        yWallLines = ys.size() - floorYs.size();
        yFloorLines = floorYs.size();
        return floorYs;
    }
}