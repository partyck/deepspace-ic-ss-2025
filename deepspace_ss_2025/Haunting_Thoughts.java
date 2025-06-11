
import processing.core.PApplet;
import TUIO.*;
import processing.core.PVector;
import processing.core.PConstants;
import processing.event.KeyEvent;
import java.util.ArrayList;

public class Haunting_Thoughts extends AbstractScene {

    private TuioClient tracker;
    private Ptc[] ptcs;

    // Values formerly controlled by sliders, now controlled by keyboard
    private float gMag = 1.0f;    // "Force"
    private float gUnity = 100.0f;  // "Unity"
    private float gBgAlpha = 255.0f;// "Haunt"

    // Constructor
    public Haunting_Thoughts(PApplet p, TuioClient tracker) {
        super(p);
        this.tracker = tracker;
        initPtcs(60);

        // Register this class to receive keyboard events
        p.registerMethod("keyEvent", this);
    }

    // Main draw loops
    @Override
    public void drawWall() {
        updateAndDisplay();
    }

    @Override
    public void drawFloor() {
        updateAndDisplay();
    }

    // Keyboard event handler
    public void keyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.PRESS) {
            char key = p.key;

            // --- "Haunt" Controls ---
            if (key == 'g' || key == 'G') {
                gBgAlpha -= 10;
            } else if (key == 'h' || key == 'H') {
                gBgAlpha += 10;
            }

            // --- "Unity" Controls ---
            if (key == 'y' || key == 'Y') {
                gUnity -= 10;
            } else if (key == 'u' || key == 'U') {
                gUnity += 10;
            }

            // --- "Force" Controls ---
            if (key == 'd' || key == 'D') {
                gMag -= 0.1f;
            } else if (key == 'f' || key == 'F') {
                gMag += 0.1f;
            }

            // Constrain values to their original ranges
            gBgAlpha = p.constrain(gBgAlpha, 6, 255);
            gUnity = p.constrain(gUnity, 0, 240);
            gMag = p.constrain(gMag, -1, 1);
        }
    }

    private void updateAndDisplay() {
        updatePtcs();

        p.noStroke();
        p.fill(0, gBgAlpha); // Use gBgAlpha for background fade
        p.rect(0, 0, p.width, p.height);

        drawPtcs();
        drawCnts();

        // Display current values on screen for feedback
        p.fill(255);
        p.textSize(14);
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.text("Haunt (G/H): " + p.nf(gBgAlpha, 0, 0), 10, 10);
        p.text("Unity (Y/U): " + p.nf(gUnity, 0, 0), 10, 30);
        p.text("Force (D/F): " + p.nf(gMag, 0, 2), 10, 50);
    }

    private void initPtcs(int amt) {
        ptcs = new Ptc[amt];
        for (int i = 0; i < ptcs.length; i++) {
            ptcs[i] = new Ptc(p);
        }
    }

    private void updatePtcs() {
        // Use TUIO tracker for particle interaction
        ArrayList<TuioCursor> tuioCursorList = tracker.getTuioCursorList();

        if (!tuioCursorList.isEmpty()) {
            for (TuioCursor tcur : tuioCursorList) {
                float cursorX = tcur.getScreenX(p.width);
                float cursorY = tcur.getScreenY(p.height);
                for (Ptc ptc : ptcs) {
                    ptc.update(cursorX, cursorY);
                }
            }
        } else {
            // If no cursors, let particles drift
            for (Ptc ptc : ptcs) {
                ptc.update();
            }
        }
    }

    private void drawPtcs() {
        for (Ptc ptc : ptcs) {
            ptc.drawPtc();
        }
    }

    private void drawCnts() {
        for (int i = 0; i < ptcs.length; i++) {
            for (int j = i + 1; j < ptcs.length; j++) {
                float d = PApplet.dist(ptcs[i].pos.x, ptcs[i].pos.y, ptcs[j].pos.x, ptcs[j].pos.y);
                if (d < gUnity) {
                    float scalar = p.map(d, 0, gUnity, 1, 0);
                    ptcs[i].drawCnt(ptcs[j], scalar);
                }
            }
        }
    }

    // Inner class for Particles (Ptc)
    private class Ptc {

        PVector pos, pPos, vel, acc;
        float decay, weight, magScalar;
        final float gVelMax = 10; // moved from outer class

        Ptc(PApplet p) {
            pos = new PVector(p.random(p.width), p.random(p.height));
            pPos = new PVector(pos.x, pos.y);
            vel = new PVector(0, 0);
            acc = new PVector(0, 0);
            weight = p.random(1, 10);
            decay = p.map(weight, 1, 10, 0.95f, 0.85f);
            magScalar = p.map(weight, 1, 10, 0.5f, 0.05f);
        }

        void update(float tgtX, float tgtY) {
            pPos.set(pos);
            acc.set(tgtX - pos.x, tgtY - pos.y);
            acc.normalize();
            acc.mult(gMag * magScalar);
            vel.add(acc);
            vel.limit(gVelMax);
            pos.add(vel);
            acc.set(0, 0);
            boundaryCheck();
        }

        void update() {
            pPos.set(pos);
            vel.add(acc);
            vel.mult(decay);
            pos.add(vel);
            acc.set(0, 0);
            boundaryCheck();
        }

        void drawPtc() {
            p.strokeWeight(weight);
            p.stroke(0, 255);
            // Draw lines if there are active TUIO cursors
            if (!tracker.getTuioCursorList().isEmpty()) {
                p.line(pos.x, pos.y, pPos.x, pPos.y);
            } else {
                p.point(pos.x, pos.y);
            }
        }

        void drawCnt(Ptc coPtc, float scalar) {
            p.strokeWeight((weight + coPtc.weight) * 0.5f * scalar);
            p.stroke(0, 255 * scalar);
            p.line(pos.x, pos.y, coPtc.pos.x, coPtc.pos.y);
        }

        void boundaryCheck() {
            if (pos.x > p.width) {
                pos.x = p.width;
                vel.x *= -1;
            } else if (pos.x < 0) {
                pos.x = 0;
                vel.x *= -1;
            }
            if (pos.y > p.height) {
                pos.y = p.height;
                vel.y *= -1;
            } else if (pos.y < 0) {
                pos.y = 0;
                vel.y *= -1;
            }
        }
    }
}
