package net.meowsers.Peach;

import net.meowsers.Peach.Graphics.Renderer;
import net.meowsers.Peach.Graphics.Shader;
import net.meowsers.Peach.Utils.Log;
import net.meowsers.Peach.Utils.Enums.LogType;

import static org.lwjgl.glfw.GLFW.*;

public class Peach {

    private Window window;
    private Renderer renderer;
    Shader shader;

    public void start(String title, int width, int height) {
        if(!glfwInit()) {
            Log.log(LogType.Error, "Failed to initialize glfw!");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // Since I'm on a mac

        window = new Window(title, width, height);

        shader = new Shader("src/main/resources/Shaders/default.glsl");
        shader.compile();

        renderer = new Renderer(shader);

    }

    public void update() {
        window.update();
    }

    public void shutdown() {
        window.shutdown();
        glfwTerminate();
    }

    public Window getWindow() {
        return window;
    }
    public Renderer getRenderer() {
        return renderer;
    }
}
