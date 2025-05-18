import processing.core.PApplet;
import TUIO.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

TuioClient client;
Floor floor;
LinkedList<AbstractScene[]> scenes;
AbstractScene currentScene = null;


public void settings() {
    size(Constants.WIDTH, Constants.WALL_HEIGHT);
    
    floor = new Floor();
    String[] argsFloor = {"floor"};
    PApplet.runSketch(argsFloor, floor);
    scenes = new LinkedList<>();

    client = new TuioClient();
    client.connect();

    // Add all the scenes in order
    scenes.add(new AbstractScene[]{new SceneOne(this), new SceneOne(floor)});
    scenes.add(new AbstractScene[]{new Scene00_Curtain(this), new Scene00_Curtain(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro(this), new Scene01_Intro(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro_v1(this), new Scene01_Intro_v1(floor)});
    scenes.add(new AbstractScene[]{new SceneValerioMorning(this), new SceneValerioMorning(floor)});
    scenes.add(new AbstractScene[]{new SceneFloorTracker(this, client), new SceneFloorTracker(floor, client)});
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
