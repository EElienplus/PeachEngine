package net.meowsers.Peach;

import net.meowsers.Peach.Graphics.Renderer;
import net.meowsers.Peach.Utils.Log;
import net.meowsers.Peach.Utils.LogType;

import static org.lwjgl.glfw.GLFW.*;

public class Peach {

    private Window window;
    private Renderer renderer;

    public void start(String title, int width, int height) {
        if(!glfwInit()) {
            Log.log(LogType.Error, "Failed to initialize glfw!");
        }

        glfwWindowHint(GLFW_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // Since I'm on a mac

    }

    public void update() {

    }

    public void shutdown() {

    }
}
