package net.meowsers.Peach.Utils;

import net.meowsers.Peach.Utils.Enums.Key;
import net.meowsers.Peach.Utils.Enums.MouseButton;
import org.lwjgl.glfw.GLFW;

public class Input {

    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private static final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

    private static double mouseX, mouseY;
    private static double scrollX, scrollY;

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

    public static boolean isKeyDown(Key key) {
        return key != null && keys[key.getCode()];
    }

    public static boolean isMouseButtonDown(MouseButton button) {
        return button != null && mouseButtons[button.getCode()];
    }

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

    public static void endFrame() {
        scrollX = 0;
        scrollY = 0;
    }
}