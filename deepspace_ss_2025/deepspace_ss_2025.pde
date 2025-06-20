import processing.core.PApplet;
import processing.video.*;
import TUIO.*;
import oscP5.*;

import java.util.LinkedList;

TuioClient tracker;
Capture cam;
OscP5 oscP5;

Floor floor;
LinkedList<AbstractScene[]> scenes;
int currentSceneIndex = -1;
AbstractScene currentSceneWall = null;
AbstractScene currentSceneFloor = null;
boolean initialized = false;

public void settings() {
    if (Constants.DEV) {
        size(Constants.WIDTH, Constants.WALL_HEIGHT, P2D);
    } else {
        fullScreen(P2D, 1);
    }

    floor = new Floor();
    String[] argsFloor = {"floor"};
    PApplet.runSketch(argsFloor, floor);
    scenes = new LinkedList<>();

    tracker = new TuioClient();
    tracker.connect();
}

void setup() {
    if (Constants.DEV) {
        windowMove(0, 50);
        surface.setLocation(0, 50);
        surface.setResizable(true);
    }
    noCursor();
    surface.setTitle("Wall");

    oscP5 = new OscP5(this, 10000);

    loadCamera();

    // Add scenes
    scenes.add(new AbstractScene[]{new SceneLineForAudience(this), new SceneLineForAudience(floor)});
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
    scenes.add(new AbstractScene[]{new Scene01Intro(this, tracker), new Scene01Intro(floor, tracker)});
    scenes.add(new AbstractScene[]{new Scene02Rectangles(this, tracker), new Scene02Rectangles(floor, tracker)});
    scenes.add(new AbstractScene[]{new SceneCamera(this, cam), new SceneCamera(floor, cam)});
    scenes.add(new AbstractScene[]{new Scene07_DifferentSpeeds(this), new Scene07_DifferentSpeeds(floor)});
    scenes.add(new AbstractScene[]{new Scene02ValerioMorning(this), new Scene02ValerioMorning(floor)});
    scenes.add(new AbstractScene[]{new SceneRave(this, tracker), new SceneRave(floor, tracker)});
    scenes.add(new AbstractScene[]{new SceneOne(this), new SceneOne(floor)});
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
    scenes.add(new AbstractScene[]{new SceneApplause(this), new SceneApplause(floor)});
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
}

public void draw() {
    if (!initialized) {
        nextScene();
        initialized = true;
    }
    if (currentSceneWall != null) {
        currentSceneWall.draw();
    }
}

void closeAll() {
    if (cam != null) cam.stop();
    oscP5.stop();
    tracker.disconnect();
    exit();
}

void nextScene() {
    currentSceneIndex++;
    if (currentSceneIndex >= scenes.size()) {
        currentSceneIndex = 0;
    }
    AbstractScene[] currentScenes = scenes.get(currentSceneIndex);
    currentSceneWall = currentScenes[0];
    currentSceneFloor = currentScenes[1];
    currentSceneWall.init();
    currentSceneFloor.init();
    floor.setScene(currentSceneFloor);



    System.out.println("Next scene: " + currentScenes[0].getClass().getSimpleName());
}

void previousScene() {
    currentSceneIndex--;
    if (currentSceneIndex < 0) {
        currentSceneIndex = scenes.size() - 1;
    }
    AbstractScene[] currentScenes = scenes.get(currentSceneIndex);
    currentSceneWall = currentScenes[0];
    currentSceneFloor = currentScenes[1];
    currentSceneWall.init();
    currentSceneFloor.init();
    floor.setScene(currentSceneFloor);

    System.out.println("Previous scene: " + currentScenes[0].getClass().getSimpleName());
}

void oscEvent(OscMessage oscMessage) {
    String pattern = oscMessage.addrPattern();
    float value = oscMessage.get(0).floatValue();
    println("osc message in: "+pattern+", value: "+oscMessage.get(0).floatValue());
    
    switch (pattern) {
        case "/nextScene":
            nextScene();
            break;
        case "/previousScene":
            previousScene();
            break;
        case "/close":
            closeAll();
            break;
        default :
            if (currentSceneWall != null) currentSceneWall.oscEvent(pattern, value);
            if (currentSceneFloor != null) currentSceneFloor.oscEvent(pattern, value);
            break;	
    }
}

void controllerChange(int channel, int number, int value) {
    println("MIDI in: Channel: " + channel + " Number: " + number + " Value: " + value);
    if (currentSceneWall != null) currentSceneWall.midiIn(number, value);
    if (currentSceneFloor != null) currentSceneFloor.midiIn(number, value);
}

void mousePressed() {
    nextScene();
}

void keyPressed() {
    if (key == ' ') {
        nextScene();
    } else {
        String value = key == CODED ? "KeyCode" : key + "";
        System.out.println("key pressed: " + value);
        if (currentSceneWall != null) currentSceneWall.keyPressed(key, keyCode);
        if (currentSceneFloor != null) currentSceneFloor.keyPressed(key, keyCode);
    }
}

void loadCamera() {
    String[] cameras = Capture.list();
    if (cameras.length == 0) {
        println("There are no cameras available for capture.");
    } else {
        println("Available cameras:");
        String cameraName = cameras[0];
        for (int i = 0; i < cameras.length; i++) {
            println(cameras[i]);
            if (cameras[i].equals(Constants.CAMERA_NAME)) {
                cameraName = Constants.CAMERA_NAME;
            }
        }
        println("Camera in use: " + cameraName);
        cam = new Capture(this, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, cameraName, Constants.CAMERA_FPS);
        cam.start();
    }
}
