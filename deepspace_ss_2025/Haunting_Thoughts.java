import processing.core.PApplet;
import java.util.ArrayList;

public class Haunting_Thoughts extends AbstractScene {

    private Ptc[] ptcs;
    private float gMag = 1, gVelMax = 10, gUnity, gUnityT, gBgAlpha = 255, gBgAlphaT = 255;
    private ArrayList<Slider> slidersList;
    private Slider sliderHaunt, sliderUnity, sliderForce;
    private boolean onPressed;

    public Haunting_Thoughts(PApplet p) {
        super(p);
        initPtcs(60);
        initSliders();
    }

    @Override
    public void drawWall() {
        p.background(0);
        updateAndDisplay();
    }

    @Override
    public void drawFloor() {
        p.background(0);
        updateAndDisplay();
    }

    private void updateAndDisplay() {
        gUnity = p.lerp(gUnity, gUnityT, 0.02f);
        gBgAlpha = p.lerp(gBgAlpha, gBgAlphaT, 0.02f);
        gMag = sliderForce.value;

        updatePtcs();
        updateSliders();

        p.noStroke();
        p.fill(255, gBgAlpha);
        p.rect(0, 0, p.width, p.height);

        drawPtcs();
        drawCnts();
        drawSliders();
    }

    private void initPtcs(int amt) {
        ptcs = new Ptc[amt];
        for (int i = 0; i < ptcs.length; i++) {
            ptcs[i] = new Ptc(p);
        }
    }

    private void updatePtcs() {
        if (onPressed) {
            for (Ptc ptc : ptcs) {
                ptc.update(p.mouseX, p.mouseY);
            }
        } else {
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

    private class Ptc {
        PVector pos, pPos, vel, acc;
        float decay, weight, magScalar;

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
            if (onPressed) p.line(pos.x, pos.y, pPos.x, pPos.y);
            else p.point(pos.x, pos.y);
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

    private void initSliders() {
        slidersList = new ArrayList<>();
        sliderHaunt = new Slider(p, 100, 30, 120, 20);
        sliderHaunt.setTag("Haunt");
        sliderHaunt.setValue(32, 6, 255);
        sliderUnity = new Slider(p, 100, 55, 120, 20);
        sliderUnity.setTag("Unity");
        sliderUnity.setValue(100, 0, 240);
        sliderForce = new Slider(p, 100, 80, 120, 20);
        sliderForce.setTag("Force");
        sliderForce.setValue(1, -1, 1);

        slidersList.add(sliderHaunt);
        slidersList.add(sliderUnity);
        slidersList.add(sliderForce);
    }

    private void updateSliders() {
        for (Slider slider : slidersList) {
            if (slider.active) {
                slider.update();
                break;
            }
        }
    }

    private void drawSliders() {
        for (Slider slider : slidersList) {
            slider.display();
        }
    }

    private class Slider {
        PVector pos, nameTagPos, valueTagPos;
        float w, h, innerW, value, valueMin, valueMax;
        boolean active;
        String nameTag, valueTag;

        Slider(PApplet p, float x, float y, float w, float h) {
            pos = new PVector(x, y);
            nameTagPos = new PVector(x - 10, y);
            valueTagPos = new PVector(x + w * 0.5f, y);
            this.w = w;
            this.h = h;
        }

        void setTag(String nameTag) {
            this.nameTag = nameTag;
        }

        void setValue(float value, float valueMin, float valueMax) {
            this.value = value;
            this.valueMin = valueMin;
            this.valueMax = valueMax;
            valueTag = p.nf(value, 0, 2);
            innerW = p.map(value, valueMin, valueMax, 0, w);
        }

        void update() {
            innerW = PApplet.constrain(p.mouseX - pos.x, 0, w);
            value = p.map(innerW, 0, w, valueMin, valueMax);
            valueTag = p.nf(value, 0, 2);
        }

        void display() {
            p.noStroke();
            p.fill(0);
            p.rect(pos.x, pos.y, w, h);
            p.fill(255, 0, 0);
            p.rect(pos.x, pos.y, innerW, h);

            p.fill(255);
            p.rect(pos.x - 10 - p.textWidth(nameTag), pos.y, 10 + p.textWidth(nameTag), h);

            p.fill(0);
            p.textAlign(PApplet.LEFT, PApplet.CENTER);
            p.text(nameTag, pos.x - 15 - p.textWidth(nameTag), pos.y + h / 2);

            p.fill(255);
            p.textAlign(PApplet.CENTER, PApplet.CENTER);
            p.text(valueTag, pos.x + w / 2, pos.y + h / 2);
        }
    }

    @Override
    public void mousePressed() {
        for (Slider slider : slidersList) {
            if (p.mouseX > slider.pos.x && p.mouseX < slider.pos.x + slider.w && p.mouseY > slider.pos.y && p.mouseY < slider.pos.y + slider.h) {
                slider.active = true;
                return;
            }
        }
        onPressed = true;
        gUnityT = sliderUnity.value;
        gBgAlphaT = sliderHaunt.value;
    }

    @Override
    public void mouseReleased() {
        for (Slider slider : slidersList) {
            slider.active = false;
        }
        onPressed = false;
        gUnityT = 0;
        gBgAlphaT = 255;
    }
}
