import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {
        int canvasWidth = 400; // work with small resolution and later add 4K --> 3840x2160
        int canvasHeight = 225;
        float scale = 0.2f;
        int deepSpaceWidth = (int) (3840 * scale);
        int deepSpaceHeight = (int) (4320 * scale);

        String[] argsPerformance = {"Performance"};
        Performance performance = new Performance(deepSpaceWidth, deepSpaceHeight);
        PApplet.runSketch(argsPerformance, performance);

        String[] argsF = {"Floor"};
        Floor floor = new Floor(canvasWidth, canvasHeight);
        PApplet.runSketch(argsF, floor);

        String[] argsW = {"Wall"};
        Wall wall = new Wall(canvasWidth, canvasHeight);
        PApplet.runSketch(argsW, wall);

        String[] argsP = {"Preview"};
        ScenePreview preview = new ScenePreview(canvasWidth, canvasHeight);
        PApplet.runSketch(argsP, preview);

        String[] argsT = {"tuio"};
        TuioDemo tuioDemo = new TuioDemo();
        PApplet.runSketch(argsT, tuioDemo);
    }
}