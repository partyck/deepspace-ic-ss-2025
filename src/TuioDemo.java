import TUIO.*;
import processing.core.PApplet;

public class TuioDemo extends PApplet {
    TuioProcessing tuioClient;

    public void settings() {
        // minimize GUI
        size(1,1);
        noLoop();

        // we create an instance of the TuioProcessing client
        // since we add "this" class as an argument the TuioProcessing class expects
        // an implementation of the TUIO callback methods in this class (see below)
        tuioClient = new TuioProcessing(this);
    }

    public void draw() {
        background(255);
    }
    // --------------------------------------------------------------
    // these callback methods are called whenever a TUIO event occurs
    // there are three callbacks for add/set/del events for each object/cursor/blob type
    // the final refresh callback marks the end of each TUIO frame

    // called when an object is added to the scene
    public void addTuioObject(TuioObject tobj) {
        println("add obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());
    }

    // called when an object is moved
    public void updateTuioObject (TuioObject tobj) {
        println("set obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()
                +" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel());
    }

    // called when an object is removed from the scene
    public void removeTuioObject(TuioObject tobj) {
        println("del obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+")");
    }

    // --------------------------------------------------------------
// called when a cursor is added to the scene
    public void addTuioCursor(TuioCursor tcur) {
        println("add cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY());
    }

    // called when a cursor is moved
    public void updateTuioCursor (TuioCursor tcur) {
        println("set cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY()
                +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
    }

    // called when a cursor is removed from the scene
    public void removeTuioCursor(TuioCursor tcur) {
        println("del cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+")");
    }

    // --------------------------------------------------------------
// called when a blob is added to the scene
    public void addTuioBlob(TuioBlob tblb) {
        println("add blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+") "+tblb.getX()+" "+tblb.getY()+" "+tblb.getAngle()+" "+tblb.getWidth()+" "+tblb.getHeight()+" "+tblb.getArea());
    }

    // called when a blob is moved
    public void updateTuioBlob (TuioBlob tblb) {
        println("set blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+") "+tblb.getX()+" "+tblb.getY()+" "+tblb.getAngle()+" "+tblb.getWidth()+" "+tblb.getHeight()+" "+tblb.getArea()
                +" "+tblb.getMotionSpeed()+" "+tblb.getRotationSpeed()+" "+tblb.getMotionAccel()+" "+tblb.getRotationAccel());
    }

    // called when a blob is removed from the scene
    public void removeTuioBlob(TuioBlob tblb) {
        println("del blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+")");
    }

    // --------------------------------------------------------------
// called at the end of each TUIO frame
    public void refresh(TuioTime frameTime) {
        println("frame #"+frameTime.getFrameID()+" ("+frameTime.getTotalMilliseconds()+")");
    }
}
