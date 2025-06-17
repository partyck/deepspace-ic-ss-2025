public class Constants {
    public static final boolean DEV = true;
    
    // Camera configuration
    public static final String CAMERA_NAME = "48MP USB Camera";
    public static final int CAMERA_WIDTH = 640;
    public static final int CAMERA_HEIGHT = 480;
    public static final float CAMERA_FPS = 20f;
    public static final String MIDI_SOUND_IN = "CircuitPython usb_midi.ports[0]";
    public static final String MIDI_SOUND_OUT = "CircuitPython usb_midi.ports[0]";
    public static final String MIDI_CONTROL_IN = "KNOB";
    public static final String MIDI_CONTROL_OUT = "SLIDER";

    
    // Screen configuration for Development
    public static final float SCALE = 0.2f;
    public static final int WIDTH = (int) (3840 * SCALE);
    public static final int WALL_HEIGHT = (int) (2160 * SCALE);
    public static final int FLOOR_HEIGHT = (int) (1800 * SCALE);
    public static final int TOTAL_HEIGHT = WALL_HEIGHT + FLOOR_HEIGHT;
}
