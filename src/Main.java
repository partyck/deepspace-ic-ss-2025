import TUIO.TuioProcessing;
import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {
        String[] argsF = {"Floor"};
        Floor floor = new Floor();
        PApplet.runSketch(argsF, floor);

        String[] argsW = {"Wall"};
        Wall wall = new Wall();
        PApplet.runSketch(argsW, wall);

        String[] argsT = {"tuio"};
        TuioDemo tuioDemo = new TuioDemo();
        PApplet.runSketch(argsT, tuioDemo);
    }
}