import processing.core.PApplet;
import processing.core.PConstants;
import TUIO.*;

import java.util.ArrayList;

public class SceneRooms extends AbstractScene {
    TuioClient tracker;
    ArrayList<Room> rooms;

    public SceneRooms(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker  = tracker;
        rooms = new ArrayList();
        rooms.add(new Room(100, 100, 40, 40));
        rooms.add(new Room(200, 100, 40, 60));
        rooms.add(new Room(300, 100, 40, 500));
    }

    @Override
    public void drawWall() {
        background(0);
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();
        for (int index = 0; index < rooms.size(); index++) {
            Room room = rooms.get(index);
            translate(0, Constants.WALL_HEIGHT);
            room.show();
            if (tuioCursorList.size() >= index + 1) {
                TuioCursor cursor = tuioCursorList.get(index);
                // System.out.println(cursor.getCursorID());
                room.update(cursor.getScreenX(this.width()), cursor.getScreenY(Constants.FLOOR_HEIGHT));
            }
        }
    }

    @Override
    public void drawFloor() {
        background(0);
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();
        for (int index = 0; index < rooms.size(); index++) {
            Room room = rooms.get(index);
            room.show();
            if (tuioCursorList.size() >= index + 1) {
                TuioCursor cursor = tuioCursorList.get(index);
                // System.out.println(cursor.getCursorID());
                room.update(cursor.getScreenX(this.width()), cursor.getScreenY(Constants.FLOOR_HEIGHT));
            }
        }
    }

    class Room {
        int x,y,w,h;
        public Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void show() {
            fill(255);
            noStroke();
            rectMode(PConstants.CENTER);
            rect(x, y, w, h);
        }

        void update(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
