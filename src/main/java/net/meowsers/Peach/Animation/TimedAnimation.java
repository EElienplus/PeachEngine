package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Utils.Enums.Ease;

public abstract class TimedAnimation implements Animation {

    private final float duration;
    private final Ease ease;
    private float elapsedTime;
    private float progress;

    private boolean started;
    private boolean finished;

    public TimedAnimation(float duration) {
        this(duration, Ease.InOut);
    }

    public TimedAnimation(float duration, Ease ease) {
        if (duration < 0.0f) {
            throw new IllegalArgumentException("Animation duration can't be negative.");
        }
        if (ease == null) {
            throw new IllegalArgumentException("Ease can't be null.");
        }

        this.duration = duration;
        this.ease = ease;
    }

    @Override
    public void begin() {
        started = true;
    }

    @Override
    public void update(float deltaTime) {
        if (finished) {
            return;
        }

        if (!started) {
            begin();
        }

        if (duration == 0.0f) {
            elapsedTime = 0.0f;
            progress = 1.0f;
            apply(ease.apply(progress));
            finished = true;
            return;
        }

        float safeDeltaTime = Math.max(deltaTime, 0.0f);
        elapsedTime += safeDeltaTime;
        progress = Math.min(elapsedTime / duration, 1.0f);

        apply(ease.apply(progress));

        if (progress >= 1.0f) {
            finished = true;
        }
    }

    protected abstract void apply(float progress);

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void reset() {
        elapsedTime = 0.0f;
        progress = 0.0f;
        started = false;
        finished = false;
    }

    public float getDuration() {
        return duration;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public float getProgress() {
        return progress;
    }

    public Ease getEase() {
        return ease;
    }
}
