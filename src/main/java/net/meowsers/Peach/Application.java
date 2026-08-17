package net.meowsers.Peach;

import net.meowsers.Peach.Graphics.Draw;
import net.meowsers.Peach.Graphics.Renderer;
import net.meowsers.Peach.Utils.Time;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

public abstract class Application {

    Peach peach;
    Window window;
    Renderer renderer;

    private float deltaTime;

    public abstract void start();
    public abstract void update(float deltaTime);
    public abstract void shutdown();

    public void run(String title, int width, int height) {
        peach = new Peach();
        Time.start();
        peach.start(title, width, height);

        window = peach.getWindow();
        renderer = peach.getRenderer();

        new Draw(renderer);

        start();

        while(window.isRunning()) {
            Time.update();
            peach.update();

            deltaTime = Time.getDeltaTime();

            update(deltaTime);

            renderer.render(width, height);

            glfwSwapBuffers(window.getHandle());
        }
        shutdown();
        peach.shutdown();


    }

    public Window getWindow() {
        return window;
    }

}
