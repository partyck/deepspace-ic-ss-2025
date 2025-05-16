import TUIO.TuioClient;
import TUIO.TuioCursor;

import java.util.ArrayList;

public class FloorTracking {
    private static FloorTracking INSTANCE;
    private final TuioClient client;

    private FloorTracking() {
        this.client = new TuioClient();
        this.client.connect();
    }

    public static FloorTracking getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FloorTracking();
        }
        return INSTANCE;
    }

    public ArrayList<TuioCursor> getCursorList() {
        return client.getTuioCursorList();
    }

}
