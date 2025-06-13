import processing.core.PApplet;
import processing.video.*;
import TUIO.*;
import oscP5.*;
import themidibus.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

TuioClient tracker;
Capture cam;
OscP5 oscP5;
MidiBus midiSound, midiController;

Floor floor;
LinkedList<AbstractScene[]> scenes;
AbstractScene currentSceneWall = null;
AbstractScene currentSceneFloor = null;


public void settings() {
    if (Constants.DEV) {
        size(Constants.WIDTH, Constants.WALL_HEIGHT, P2D);
    }
    else {
        fullScreen(P2D, 2);
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
    surface.setTitle("Wall");
    
    // setup osc client
    oscP5 = new OscP5(this, 10000);
    
    MidiBus.list();
    midiSound = new MidiBus(this, Constants.MIDI_SOUND_IN, Constants.MIDI_SOUND_OUT);
    midiController = new MidiBus(this, Constants.MIDI_CONTROL_IN, Constants.MIDI_CONTROL_OUT);

    loadCamera();

    // Add all the scenes in order
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
    scenes.add(new AbstractScene[]{new Scene01Intro(this, tracker), new Scene01Intro(floor, tracker)});
    scenes.add(new AbstractScene[]{new Scene02Rectangles(this, tracker), new Scene02Rectangles(floor, tracker)});
    scenes.add(new AbstractScene[]{new Scene07_DifferentSpeeds(this), new Scene07_DifferentSpeeds(floor)});
    scenes.add(new AbstractScene[]{new SceneCamera(this, cam, tracker), new SceneCamera(floor, cam, tracker)});
    scenes.add(new AbstractScene[]{new Scene00_Curtain(this), new Scene00_Curtain(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro(this), new Scene01_Intro(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro_v1(this), new Scene01_Intro_v1(floor)});
    scenes.add(new AbstractScene[]{new Scene02ValerioMorning(this), new Scene02ValerioMorning(floor)});
    scenes.add(new AbstractScene[]{new Scene05_Sophie(this), new Scene05_Sophie(floor)});
    scenes.add(new AbstractScene[]{new SceneOne(this), new SceneOne(floor)});
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
    nextScene();
}

public void draw() {
    currentSceneWall.draw();
}

// incoming osc message are forwarded to the oscEvent method.
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

void keyPressed() {
    if (key == ' ') {
        nextScene();
    }
    else {
        String value = key == CODED ? "KeyCode" : key + "";
        System.out.println("key pressed: " + value);
        currentSceneWall.keyPressed(key, keyCode);
        currentSceneFloor.keyPressed(key, keyCode);
    }
}


void controllerChange(int channel, int number, int value) {
    println("MIDI in: Channel: " + channel + " Number: " + number + " Value: " + value);
    currentSceneWall.midiIn(number, value);
    currentSceneFloor.midiIn(number, value);
}

void nextScene() {
    AbstractScene[] currentScenes = scenes.poll();
    if (currentScenes == null) {
        exit();
        return;
    }
    currentSceneWall = currentScenes[0];
    currentSceneFloor = currentScenes[1];
    currentSceneWall.init();
    currentSceneFloor.init();
    floor.setScene(currentSceneFloor);
    midiSound.sendMessage(0xB0, 0, 1, 1);
    System.out.println("Next scene: " + currentScenes[0].getClass().getSimpleName());
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
            cameraName = cameras[i].equals(Constants.CAMERA_NAME) ? Constants.CAMERA_NAME : cameraName;
        }
        println("Camera in use: " + cameraName);
        cam = new Capture(this, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, cameraName, Constants.CAMERA_FPS);
        cam.start();
    }
}
