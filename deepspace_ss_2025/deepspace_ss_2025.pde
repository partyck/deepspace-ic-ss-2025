import processing.core.PApplet;
import processing.video.*;
import TUIO.*;
import oscP5.*;
import themidibus.*;

import java.util.LinkedList;

TuioClient tracker;
Capture cam;
OscP5 oscP5;
MidiBus midiSound, midiController;

Floor floor;
LinkedList<AbstractScene[]> scenes;
AbstractScene currentSceneWall = null;
AbstractScene currentSceneFloor = null;
boolean initialized = false;

public void settings() {
    if (Constants.DEV) {
        size(Constants.WIDTH, Constants.WALL_HEIGHT, P2D);
    } else {
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

    oscP5 = new OscP5(this, 10000);

    MidiBus.list();
    try {
        if (MidiBus.availableInputs() != null && MidiBus.availableOutputs() != null) {
            midiSound = new MidiBus(this, Constants.MIDI_SOUND_IN, Constants.MIDI_SOUND_OUT);
            println("midiSound loaded.");
        } else {
            println("midiSound not available — inputs/outputs are null");
        }
    } catch (Exception e) {
        println("Couldn't load midiSound: " + e);
    }

    try {
        if (MidiBus.availableInputs() != null && MidiBus.availableOutputs() != null) {
            midiController = new MidiBus(this, Constants.MIDI_CONTROL_IN, Constants.MIDI_CONTROL_OUT);
            println("midiController loaded.");
        } else {
            println("midiController not available — inputs/outputs are null");
        }
    } catch (Exception e) {
        println("Couldn't load midiController: " + e);
    }

    loadCamera();

    // Add scenes
    scenes.add(new AbstractScene[]{new Blackout(this), new Blackout(floor)});
    scenes.add(new AbstractScene[]{new Scene01Intro(this, tracker), new Scene01Intro(floor, tracker)});
    scenes.add(new AbstractScene[]{new Scene02Rectangles(this, tracker), new Scene02Rectangles(floor, tracker)});
    scenes.add(new AbstractScene[]{new Scene07_DifferentSpeeds(this), new Scene07_DifferentSpeeds(floor)});
    scenes.add(new AbstractScene[]{new SceneCamera(this, cam, tracker), new SceneCamera(floor, cam, tracker)});
    scenes.add(new AbstractScene[]{new SceneRave(this, tracker), new SceneRave(floor, tracker)});
    scenes.add(new AbstractScene[]{new Scene00_Curtain(this), new Scene00_Curtain(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro(this), new Scene01_Intro(floor)});
    scenes.add(new AbstractScene[]{new Scene01_Intro_v1(this), new Scene01_Intro_v1(floor)});
    scenes.add(new AbstractScene[]{new Scene02ValerioMorning(this), new Scene02ValerioMorning(floor)});
    scenes.add(new AbstractScene[]{new Scene05_Sophie(this), new Scene05_Sophie(floor)});
    scenes.add(new AbstractScene[]{new SceneOne(this), new SceneOne(floor)});
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

    if (midiSound != null) {
        midiSound.sendMessage(0xB0, 0, 1, frameCount % 127);
    } else {
        println("🎛️ midiSound not available, skipping MIDI trigger.");
    }

    System.out.println("Next scene: " + currentScenes[0].getClass().getSimpleName());
}

void oscEvent(OscMessage oscMessage) {
    println("osc message in: "+oscMessage.addrPattern()+", value: "+oscMessage.get(0).floatValue());
    if (oscMessage.addrPattern().equals("/nextScene")) {
        nextScene();
    } else {
        if (currentSceneWall != null) currentSceneWall.oscEvent(oscMessage.addrPattern(), oscMessage.get(0).floatValue());
        if (currentSceneFloor != null) currentSceneFloor.oscEvent(oscMessage.addrPattern(), oscMessage.get(0).floatValue());
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
