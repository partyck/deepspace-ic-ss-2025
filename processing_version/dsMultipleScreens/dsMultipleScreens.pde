import processing.core.PApplet;
import TUIO.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

TuioClient tracker;
Floor floor;
LinkedList<AbstractScene[]> scenes;
AbstractScene currentScene = null;


public void settings() {
    size(Constants.WIDTH, Constants.WALL_HEIGHT);
    
    floor = new Floor();
    String[] argsFloor = {"floor"};
    PApplet.runSketch(argsFloor, floor);
    scenes = new LinkedList<>();

    tracker = new TuioClient();
    tracker.connect();

    // Add all the scenes in order
    scenes.add(new AbstractScene[]{new SceneOne(this), new SceneOne(floor)});
    scenes.add(new AbstractScene[]{new SceneValerioMorning(this), new SceneValerioMorning(floor)});
    scenes.add(new AbstractScene[]{new SceneRooms(this, tracker), new SceneRooms(floor, tracker)});
    scenes.add(new AbstractScene[]{new SceneFloorTracker(this, tracker), new SceneFloorTracker(floor, tracker)});
    nextScene();
}

public void draw() {
    // System.out.println("Main draw : " + currentScene.getClass().getSimpleName());
    currentScene.draw();
}

void mousePressed() {
  nextScene();
}

void nextScene() {
    AbstractScene[] currentScenes = scenes.poll();
    if (currentScenes == null) {
        currentScenes = new AbstractScene[]{new Blackout(this), new Blackout(floor)};
    }
    currentScene = currentScenes[0];
    floor.setScene(currentScenes[1]);
    System.out.println("Next scene: " + currentScenes[0].getClass().getSimpleName());
}