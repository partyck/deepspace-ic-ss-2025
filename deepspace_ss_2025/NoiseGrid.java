import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
    
import processing.core.PApplet;
    
public class NoiseGrid {
    int[] xs;
    int[] ys;

    AbstractScene wall;
    AbstractScene floor;

    ArrayList<Tile> tiles;

    boolean linesOn = false;
    boolean tilesOn = true;

    static float speed = 0.5f;
    static float timeOffset = 0f;
    static float noiseScale = 0.09f;

    static float speedFill = 0.09f;
    static float timeOffsetFill = 0f;
    static float noiseScaleFill = 1f;

    float displayTreshold = 0.6f;

    int tileTimer;

    static float noiseLinesForceStrength = 0.9f;
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

    public NoiseGrid() {}

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

        affectDistance = width * 0.2f;
        tileTimer = (int) wall.frameRate() * 1;

        int xLines = 20;
        int yLines = 20;
        xs =  new int[xLines];
        ys =  new int[yLines];
        for(int i = 0; i < xLines; i++) {
            xs[i] = (int) Math.floor((i + 1) * width / (float) xLines);
        }
        for(int j = 0; j < yLines; j++) {
            ys[j] = (int) Math.floor((j + 1) * height / (float) yLines);
        }

        tiles = new ArrayList<>();
        int prevX = 0;
        for(int i = 0; i < xs.length; i++) {
            int prevY = 0;
            for(int j = 0; j < ys.length; j++) {
                tiles.add(new Tile(prevX, prevY, xs[i] - prevX, ys[j] - prevY));
                prevY = ys[j];
            }
            prevX = xs[i];
        }

        updateNoiseDistances();
    }

    void affect(float targetX, float targetY) {
        for(int i = 0; i < xs.length; i++) {
            int currentX = xs[i];
            if (Math.abs(targetX - currentX) < affectDistance) {
                float forceX = targetX - currentX;
                float distance = Math.abs(forceX);
                float strength = (float) Math.pow(PApplet.map(affectDistance - distance, 0, affectDistance, 0, 1), 2);
                xs[i] = (int) Math.floor(currentX + forceX * strength);
            }
        }
        for(int j = 0; j < ys.length; j++) {
            int currentY = ys[j];
            if (Math.abs(targetY - currentY + wallHeight) < affectDistance) {
                float forceY = targetY - currentY + wallHeight;
                float distance = Math.abs(forceY);
                float strength = (float) Math.pow(PApplet.map(affectDistance - distance, 0, affectDistance, 0, 1), 2);
                ys[j] = (int) Math.floor(currentY + forceY * strength);
            }
        }

        for(Tile tile : tiles) {
            tile.affect((int) targetX, (int) targetY);
        }
    }

    void update() {
        updateNoiseDistances();
        updateTiles();
        timeOffset += speed;
        timeOffsetFill += speedFill;
    }

    void displayWall() {
        if (linesOn) {
            wall.stroke(255);
            wall.strokeWeight(1);
            for(int i = 0; i < xs.length -1; i++ ) {
                wall.line(xs[i], 0, xs[i], wallHeight);
            }
            for(int i = 0; i < ys.length -1; i++ ) {
                wall.line(0, ys[i], width, ys[i]);
            }
        }
        if (tilesOn) {
            for (int i = 0; i < tiles.size(); i++) {
                tiles.get(i).display(true);
            }
        }
    }

    void displayFloor() {
        if (linesOn) {
            ArrayList<Integer> floorYs = floorYLines();
            floor.stroke(255);
            floor.strokeWeight(1);
            for(int i = 0; i < xs.length -1; i++ ) {
                floor.line(xs[i], 0, xs[i], floorHeight);
            }
            for(int i = 0; i < floorYs.size(); i++ ) {
                floor.line(0, floorYs.get(i), width, floorYs.get(i));
            }
        }
        if (tilesOn) {
            for (int i = 0; i < tiles.size(); i++) {
                tiles.get(i).display(false);
            }
        }
    }

    private void updateTiles() {
        float fx0 = (xs.length - xs.length * noiseScaleFill) / 2f;
        float fy0 = (ys.length - ys.length * noiseScaleFill) / 2f;
        int prevX = 0;
        for(int i = 0; i < xs.length; i++) {
            int prevY = 0;
            int x = xs[i];
            for(int j = 0; j < ys.length; j++) {
                int y = ys[j];
                float n = wall.noise(fx0 + i * noiseScaleFill, fy0 + j * noiseScaleFill, timeOffsetFill);
                Tile tile = tiles.get(i * ys.length + j);
                tile.update(prevX, prevY, x - prevX, y - prevY, n);
                prevY = y;
            }
            prevX = x;
        }
    }

    private void updateNoiseDistances() {
        updateDistances(xs, width, 1000);
        updateDistances(ys, height, 2000);
    }

    private void updateDistances(int[] lines, int totalDistance, int noiseOffset) {
        float[] hns = new float[lines.length];
        float total = 0;
        for(int i = 0; i < lines.length; i++ ) {
            hns[i] = wall.noise(noiseOffset + i * noiseScale, timeOffset);
            total += hns[i];
        }
        
        int accumulation = 0;
        for(int i = 0; i < lines.length; i++ ) {
            accumulation += (int) totalDistance * (hns[i] / total);
            if (i == lines.length - 1) {
                lines[i] = totalDistance;
            }else {
                int currentV = lines[i];
                int value = currentV + (int) Math.floor((accumulation - currentV) * noiseLinesForceStrength);
                lines[i] = value;
            }
        }
    }

    private ArrayList<Integer> floorYLines() {
        ArrayList<Integer> floorYs = new ArrayList<>();
        for(int i = 0; i < ys.length -1; i++ ) {
            if (ys[i] > wallHeight) {
                floorYs.add(ys[i] - wallHeight);
            }
        }
        return floorYs;
    }

    class Tile {
        int x;
        int y;
        int w;
        int h;
        float n;
        int timer = 0;

        // Gradient colors
        private final int gradientColor1 = wall.color(196, 5, 9);
        private final int gradientColor2 = wall.color(235, 109, 23);
        private final int gradientColor3 = wall.color(235, 109, 23); 
        private final int gradientColor4 = wall.color(196, 5, 9);

        Tile(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.n = 0;
        }

        void display(boolean isWall) {
            if (isWall && y > wallHeight) return;
            if (!isWall && y + h < wallHeight) return;
            AbstractScene scene = getScene(isWall);
            int currentY = isWall ? y : y - wallHeight;
            if (timer > 0) {
                drawTimerGradient(scene, x, currentY, w, h);
                return;
            }
            if (n > displayTreshold) {
                drawGradientRect(scene, x, currentY, w, h, isWall);
                return;
            }
            return;
        }

        private void drawTimerGradient(AbstractScene scene, float x, float y, float w, float h) {
            scene.noStroke();
            
            int leftColor = scene.color(10, 20, 60); 
            int rightColor = scene.color(10, 20, 60); 
            
            // Draw horizontal gradient
            for (int i = 0; i < w; i++) {
                float t = PApplet.map(i, 0, w, 0, 1);
                int gradCol = scene.lerpColor(leftColor, rightColor, t);
                scene.fill(gradCol);
                scene.rect(x + i, y, 1, h);
            }
        }

        private void drawGradientRect(AbstractScene scene, float x, float y, float w, float h, boolean isWall) {
            // Removed pushStyle()/popStyle() calls - not available in AbstractScene
            scene.noStroke();
            
            // Choose colors based on wall/floor
            int c1, c2;
            if (isWall) {
                c1 = gradientColor1;
                c2 = gradientColor2;
            } else {
                c1 = gradientColor3;
                c2 = gradientColor4;
            }
            
            // Vertical gradient
            for (int i = 0; i < h; i++) {
                float t = PApplet.map(i, 0, h, 0, 1);
                int gradCol = scene.lerpColor(c1, c2, t);
                scene.fill(gradCol);
                scene.rect(x, y + i, w, 1);
            }
        }

        void update(int prevX, int prevY, int w, int h, float noise) {
            this.x = prevX;
            this.y = prevY;
            this.w = w;
            this.h = h;
            this.n = noise;
            if (timer > 0) {
                timer--;
            }
        }

        void affect(int targetX, int targetY) {
            if (targetX >= x && targetX <= x + w && targetY + wallHeight >= y && targetY + wallHeight <= y + h) {
                timer = tileTimer;
            }
        }

        AbstractScene getScene(boolean isWall) {
            if (isWall) {
                return wall;
            }
            else {
                return floor;
            }
        }
    }
}
