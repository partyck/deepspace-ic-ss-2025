import processing.core.PApplet;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class Performance {
    private final Wall wall;
    private final Floor floor;
    private final LinkedList<AbstractScene[]> scenes;
    private AbstractScene[] currentScene;

    public Performance() {
        listenConsole();
        wall = new Wall();
        floor = new Floor();
        scenes = new LinkedList<>();
    }

    public void settings() {
        String[] argsWall = {"Wall"};
        PApplet.runSketch(argsWall, wall);

        String[] argsFloor = {"Wall"};
        PApplet.runSketch(argsFloor, floor);

        // Add all the scenes in order
        scenes.add(new AbstractScene[]{new SceneOne(wall), new SceneOne(floor)});
        scenes.add(new AbstractScene[]{new SceneValerioMorning(wall), new SceneValerioMorning(floor)});
        scenes.add(new AbstractScene[]{new SceneFloorTracker(wall), new SceneFloorTracker(floor)});
        nextScene();
    }

    public void nextScene() {
        currentScene = scenes.poll();
        if (currentScene == null) {
            currentScene = new AbstractScene[]{new Blackout(wall), new Blackout(floor)};
        }
        wall.setScene(currentScene[0]);
        floor.setScene(currentScene[1]);
        System.out.println("Next scene: " + currentScene[0].getClass().getSimpleName());
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
                    currentScene[0].newCommand(line);
                    currentScene[1].newCommand(line);
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }
}
