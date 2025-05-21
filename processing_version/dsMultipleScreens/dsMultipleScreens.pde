import processing.core.PApplet;
import processing.video.*;
import TUIO.*;
import oscP5.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

TuioClient tracker;
Capture cam;
OscP5 oscP5;

Floor floor;
LinkedList<AbstractScene[]> scenes;
AbstractScene currentSceneWall = null;
AbstractScene currentSceneFloor = null;


public void settings() {
    size(Constants.WIDTH, Constants.WALL_HEIGHT);
    
    floor = new Floor();
    String[] argsFloor = {"floor"};
    PApplet.runSketch(argsFloor, floor);
    scenes = new LinkedList<>();

    tracker = new TuioClient();
    tracker.connect();
}

void setup() {
    // setup osc client
    oscP5 = new OscP5(this, 10000);

    // setup camera capture
    String[] cameras = Capture.list();
    if (cameras.length == 0) {
        println("There are no cameras available for capture.");
    } else {
        println("Available cameras:");
        for (int i = 0; i < cameras.length; i++) {
            println(cameras[i]);
        }
        cam = new Capture(this, cameras[0]);
        cam.start();
    }

    // Add all the scenes in order
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
    scenes.add(new AbstractScene[]{new Scene01Intro(this), new Scene01Intro(floor)});
    scenes.add(new AbstractScene[]{new Scene00_Curtain(this), new Scene00_Curtain(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro(this), new Scene01_Intro(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro_v1(this), new Scene01_Intro_v1(floor)});
    scenes.add(new AbstractScene[]{new Scene02ValerioMorning(this), new Scene02ValerioMorning(floor)});
    scenes.add(new AbstractScene[]{new Scene05_Sophie(this), new Scene05_Sophie(floor)});
    scenes.add(new AbstractScene[]{new Scene07_DifferentSpeeds(this), new Scene07_DifferentSpeeds(floor)});
    scenes.add(new AbstractScene[]{new SceneCamera(this, cam), new SceneCamera(floor, cam)});
    scenes.add(new AbstractScene[]{new SceneRooms(this, tracker), new SceneRooms(floor, tracker)});
    scenes.add(new AbstractScene[]{new SceneFloorTracker(this, tracker), new SceneFloorTracker(floor, tracker)});
    scenes.add(new AbstractScene[]{new HanifTest2(this), new HanifTest2(floor)});
    // scenes.add(new AbstractScene[]{new SceneOne(this), new SceneOne(floor)});
    nextScene();
}

public void draw() {
    currentSceneWall.draw();
}

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage oscMessage) {
  println("osc message in: "+oscMessage.addrPattern()+", value: "+oscMessage.get(0).floatValue());
  if (oscMessage.addrPattern().equals("/nextScene")) {
    nextScene();
  }
  else {
    currentSceneWall.oscEvent(oscMessage.addrPattern(), oscMessage.get(0).floatValue());
    currentSceneFloor.oscEvent(oscMessage.addrPattern(), oscMessage.get(0).floatValue());
  }
}


void mousePressed() {
  nextScene();
}

void nextScene() {
    AbstractScene[] currentScenes = scenes.poll();
    if (currentScenes == null) {
        currentScenes = new AbstractScene[]{new Blackout(this), new Blackout(floor)};
    }
    currentSceneWall = currentScenes[0];
    currentSceneFloor = currentScenes[1];
    floor.setScene(currentScenes[1]);
    System.out.println("Next scene: " + currentScenes[0].getClass().getSimpleName());
}
