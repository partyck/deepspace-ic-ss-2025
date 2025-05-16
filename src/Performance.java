import processing.core.PApplet;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class Performance extends PApplet {
    private final int canvasWidth, canvasHeight;
    private final LinkedList<AbstractScene> scenes;
    private AbstractScene currentScene;

    public Performance(int width, int height) {
        listenConsole();
        canvasWidth = width;
        canvasHeight = height;
        scenes = new LinkedList<>();
    }

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    @Override
    public void setup() {
        // Add all the scenes in order
        scenes.add(new SceneOne(this));
        scenes.add(new SceneValerioMorning(this));
        scenes.add(new SceneFloorTracker(this));
        nextScene();
    }

    @Override
    public void draw() {
        currentScene.draw();
    }

    public void nextScene() {
        currentScene = scenes.poll();
        if (currentScene == null) {
            currentScene = new Blackout(this);
        }
        println("Next scene: " + currentScene.getClass().getSimpleName());
    }

    private void listenConsole() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (Objects.equals(line, "next")) {
                    nextScene();
                }
                else {
                    currentScene.newString(line);
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }
}
