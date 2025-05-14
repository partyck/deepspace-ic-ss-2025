import TUIO.TuioProcessing;
import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {
        int canvasWidth = 400;
        int canvasHeight = 225;
        String[] argsF = {"Floor"};
        Floor floor = new Floor(canvasWidth, canvasHeight);
        PApplet.runSketch(argsF, floor);

        String[] argsW = {"Wall"};
        Wall wall = new Wall(canvasWidth, canvasHeight);
        PApplet.runSketch(argsW, wall);

        String[] argsT = {"tuio"};
        TuioDemo tuioDemo = new TuioDemo();
        PApplet.runSketch(argsT, tuioDemo);
    }
}