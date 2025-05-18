import processing.core.PApplet;
import TUIO.*;

import java.util.ArrayList;

public class SceneFloorTracker extends AbstractScene {
    TuioClient tracker;

    public SceneFloorTracker(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker  = tracker;
    }

    @Override
    public void drawWall() {
        background(0);
    }

    @Override
    public void drawFloor() {
        display();
    }

    private void display() {
        background(0);

        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();
        for (TuioCursor tcur : tuioCursorList) {
            ArrayList<TuioPoint> pointList = tcur.getPath();
            System.out.println(tcur.getSessionID());

            if (!pointList.isEmpty()) {
                stroke(color(0, 0, 255));
                TuioPoint startPoint = pointList.get(0);
                for (TuioPoint end_point : pointList) {
                    line(startPoint.getScreenX(this.width()), startPoint.getScreenY(this.height()), end_point.getScreenX(this.width()), end_point.getScreenY(this.height()));
                    startPoint = end_point;
                }

                stroke(192, 192, 192);
                fill(192, 192, 192);
                ellipse(tcur.getScreenX(this.width()), tcur.getScreenY(this.height()), 10, 10);
                fill(0);
                text("" + tcur.getCursorID(), tcur.getScreenX(this.width()) - 5, tcur.getScreenY(this.height()) + 5);
            }
        }
    }
}
