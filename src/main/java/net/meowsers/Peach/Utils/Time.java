package net.meowsers.Peach.Utils;

public class Time {

    private static double startTime = 0.0;
    private static double lastFrameTime = 0.0;

    private static float deltaTime = 0.0f;
    private static float unscaledDeltaTime = 0.0f;
    private static float timeScale = 1.0f;

    private static float fps = 0.0f;
    private static int fpsFrameCount = 0;
    private static float fpsTimer = 0.0f;
    private static long totalFrames = 0;

    public static void start() {
        startTime = getTimeInSeconds();
        lastFrameTime = startTime;
    }

    public static void update() {
        double currentTime = getTimeInSeconds();
        unscaledDeltaTime = (float) (currentTime - lastFrameTime);
        deltaTime = unscaledDeltaTime * timeScale;
        lastFrameTime = currentTime;

        // FPS and frame tracking
        totalFrames++;
        fpsFrameCount++;
        fpsTimer += unscaledDeltaTime;

        // Recalculate FPS twice a second to keep the reading stable
        if (fpsTimer >= 0.5f) {
            fps = fpsFrameCount / fpsTimer;
            fpsFrameCount = 0;
            fpsTimer = 0.0f;
        }
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static float getUnscaledDeltaTime() {
        return unscaledDeltaTime;
    }

    public static float getFPS() {
        return fps;
    }

    public static float getTimeSinceStart() {
        return (float) (getTimeInSeconds() - startTime);
    }


    public static float getTimeScale() {
        return timeScale;
    }

    /**
     * Scale speed of time in your game (e.g., 0.5f for slow-mo, 1.0f for normal, 0.0f to pause logic)
     */
    public static void setTimeScale(float scale) {
        timeScale = Math.max(0.0f, scale);
    }

    public static boolean isPaused() {
        return timeScale == 0.0f;
    }

    public static long getTotalFrames() {
        return totalFrames;
    }

    private static double getTimeInSeconds() {
        return System.nanoTime() * 1e-9;
    }
}