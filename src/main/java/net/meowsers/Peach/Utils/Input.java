package net.meowsers.Peach.Utils;

import net.meowsers.Peach.Utils.Enums.Key;
import net.meowsers.Peach.Utils.Enums.MouseButton;
import org.lwjgl.glfw.GLFW;

public class Input {

    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private static final boolean[] prevKeys = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private static final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
    private static final boolean[] prevMouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

    private static double mouseX, mouseY;
    private static double scrollX, scrollY;

    /**
     * Registers GLFW input callbacks for the target window.
     * Call this right after window creation.
     */
    public static void init(long window) {
        GLFW.glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
            }
        });

        GLFW.glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button >= 0 && button < mouseButtons.length) {
                mouseButtons[button] = (action != GLFW.GLFW_RELEASE);
            }
        });

        GLFW.glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = ypos;
        });

        GLFW.glfwSetScrollCallback(window, (win, xoffset, yoffset) -> {
            scrollX = xoffset;
            scrollY = yoffset;
        });
    }

    /**
     * Updates frame state history.
     * CALL THIS AT THE END OF YOUR MAIN LOOP FRAME (after processing inputs & rendering).
     */
    public static void endFrame() {
        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
        System.arraycopy(mouseButtons, 0, prevMouseButtons, 0, mouseButtons.length);
        scrollX = 0;
        scrollY = 0;
    }

    // --- Keyboard Queries ---

    /** Returns true every frame the key is held down. */
    public static boolean isKeyDown(Key key) {
        return key != null && keys[key.getCode()];
    }

    /** Returns true ONLY on the initial frame the key was pressed. */
    public static boolean isKeyPressed(Key key) {
        return key != null && keys[key.getCode()] && !prevKeys[key.getCode()];
    }

    /** Returns true ONLY on the frame the key was released. */
    public static boolean isKeyReleased(Key key) {
        return key != null && !keys[key.getCode()] && prevKeys[key.getCode()];
    }

    // --- Mouse Button Queries ---

    /** Returns true every frame the button is held down. */
    public static boolean isMouseButtonDown(MouseButton button) {
        return button != null && mouseButtons[button.getCode()];
    }

    /** Returns true ONLY on the initial frame the button was pressed. */
    public static boolean isMouseButtonPressed(MouseButton button) {
        return button != null && mouseButtons[button.getCode()] && !prevMouseButtons[button.getCode()];
    }

    /** Returns true ONLY on the frame the button was released. */
    public static boolean isMouseButtonReleased(MouseButton button) {
        return button != null && !mouseButtons[button.getCode()] && prevMouseButtons[button.getCode()];
    }

    // --- Mouse Movement & Scroll Queries ---

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static double getScrollX() {
        return scrollX;
    }

    public static double getScrollY() {
        return scrollY;
    }
}