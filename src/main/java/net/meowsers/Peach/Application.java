package net.meowsers.Peach;

import net.meowsers.Peach.Graphics.Renderer;
import net.meowsers.Peach.Utils.Time;

public abstract class Application {

    Peach peach = new Peach();

    private float deltaTime;

    public abstract void start();
    public abstract void update(float deltaTime);
    public abstract void shutdown();

    public void run(String title, int width, int height) {
        Time.start();
        peach.start(title, width, height);

        Window window = peach.getWindow();
        Renderer renderer = peach.getRenderer();

        start();

        while(window.isRunning()) {
            Time.update();
            peach.update();

            deltaTime = Time.getDeltaTime();

            update(deltaTime);

        }
        shutdown();
        peach.shutdown();


    }

}
