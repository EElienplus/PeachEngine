package net.meowsers.Peach;

import net.meowsers.Peach.Animation.AnimationTimeline;
import net.meowsers.Peach.Graphics.Draw;
import net.meowsers.Peach.Graphics.Renderer;
import net.meowsers.Peach.Graphics.Visualize;
import net.meowsers.Peach.Utils.Input;
import net.meowsers.Peach.Utils.Time;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

public abstract class Application {

    Peach peach;
    Window window;
    Renderer renderer;
    private final AnimationTimeline animationTimeline = new AnimationTimeline();

    public abstract void script();

    public void run(String title, int width, int height) {
        peach = new Peach();
        Time.start();
        peach.start(title, width, height);

        window = peach.getWindow();
        renderer = peach.getRenderer();

        new Draw(renderer);
        new Visualize(renderer);
        Input.init(window.getHandle());

        animationTimeline.record(this::script);

        while (window.isRunning()) {
            Time.update();
            peach.update();

            window.clearBackground();
            animationTimeline.update(Time.getDeltaTime());
            animationTimeline.render();

            renderer.render(width, height);

            Input.endFrame();

            glfwSwapBuffers(window.getHandle());
        }

        peach.shutdown();
    }

    public Window getWindow() {
        return window;
    }

    public void animateTogether(Runnable animations) {
        animationTimeline.recordTogether(animations);
    }

    public void wait(float duration) {
        animationTimeline.recordWait(duration);
    }

    public void wait(int duration) {
        wait((float) duration);
    }

}
