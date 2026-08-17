package net.meowsers.Peach.Utils.Enums;

import org.lwjgl.glfw.GLFW;

public enum MouseButton {
    LEFT(GLFW.GLFW_MOUSE_BUTTON_1),
    RIGHT(GLFW.GLFW_MOUSE_BUTTON_2),
    MIDDLE(GLFW.GLFW_MOUSE_BUTTON_3),
    BUTTON_4(GLFW.GLFW_MOUSE_BUTTON_4),
    BUTTON_5(GLFW.GLFW_MOUSE_BUTTON_5),
    BUTTON_6(GLFW.GLFW_MOUSE_BUTTON_6),
    BUTTON_7(GLFW.GLFW_MOUSE_BUTTON_7),
    BUTTON_8(GLFW.GLFW_MOUSE_BUTTON_8);

    private final int code;

    MouseButton(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}