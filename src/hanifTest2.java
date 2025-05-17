


import controlP5.*;
import processing.core.PVector;
import peasy.*;

import java.util.ArrayList;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.P2D;


public class hanifTest2 extends AbstractScene{


    PeasyCam cam;    // Declare PeasyCam

    ArrayList<Particle> particles = new ArrayList<Particle>();


    public hanifTest2(Performance p) {
        super(p);
//        p.size(800, 800, p.P3D);
        p.smooth();
        p.imageMode(CENTER);

        cam = new PeasyCam(p, 500);  // Initialize orbit camera with distance 500
    }





    @Override
    public void draw() {
        p.background(0,100);

        p.ambientLight(100, 100, 100);  // Global lighting
        p.pointLight(120, 120, 120, 140, 160, 144);
        // Particle system
        if (random(1) > 0.97) {
            for (int i = 0; i < 5; i++) {
                particles.add(new Particle());
            }
        }

        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            if (p.pos.mag() < 800) {
                p.update();
                p.show();
            } else {
                particles.remove(i);
            }
        }

        noFill();
        stroke(255);

        int iCount = 60;

        for (int i = 0; i < iCount; i++) {
            float h = map(i, 0, iCount - 1, height() * 0.1f, height() * 1.5f);
            float pulse = p.sin(p.radians(i * 10 + p.frameCount * 0.5f)) * 100;

            float strokeCol = p.map(pulse, -20, 20, 250, 100);
            stroke((int) strokeCol);
            float weight = map(i, 0, iCount - 1, 5, 3);
            p.strokeWeight(weight);

            float start = i * 500;
            float stop = start + p.sin(p.radians(p.frameCount + i)) * 360;

            p.pushMatrix();
            p.translate(0, 0, pulse);

            float ff = p.sin((p.frameCount / 100.0f) % 500);
            float ffm = map(ff, -1, 1, -35, 35);

            p.rotateX(p.radians(ffm));
            p.rotateY(p.radians(ffm / 3));
            p.rotateZ(p.radians(ffm / 2));

            arc3D(0, 0, h, start, stop);
            p.popMatrix();
        }


    }


    void arc3D(float x, float y, float radius, float start, float stop) {
        int steps = 100;
        float angleStep = p.radians((stop - start) / steps);
        float angle = p.radians(start);
        beginShape();
        for (int i = 0; i <= steps; i++) {
            float vx = x + p.cos(angle) * radius / 2;
            float vy = y + p.sin(angle) * radius / 2;
            vertex(vx, vy);
            angle += angleStep;
        }
        endShape();
    }

    class Particle {
        PVector pos;
        PVector vel;

        Particle() {
            pos = new PVector(0, 0, 0);
            vel = PVector.random3D().normalize().mult(random(4, 6));
        }

        void update() {
            pos.add(vel);
        }

        void show() {
            p.pushMatrix();
            p.translate(pos.x, pos.y, pos.z);
            noStroke();
            p.fill(250, 150);
            p.sphere(10);
            p.popMatrix();
        }
    }



}
