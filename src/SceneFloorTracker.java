import TUIO.*;
import processing.core.PApplet;

import java.util.ArrayList;

public class SceneFloorTracker extends AbstractScene {
    private final TuioClient client;

    public SceneFloorTracker(Performance p) {
        super(p);
        this.client = new TuioClient();
        this.client.connect();
    }

    @Override
    public void draw() {
        background(0);

        ArrayList<TuioCursor> tuioCursorList = client.getTuioCursorList();
        for (TuioCursor tcur : tuioCursorList) {
            ArrayList<TuioPoint> pointList = tcur.getPath();

            if (!pointList.isEmpty()) {
                stroke(color(0, 0, 255));
                TuioPoint startPoint = pointList.getFirst();
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
