package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Utils.Enums.Ease;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimedAnimationTest {

    private static class ProbeAnimation extends TimedAnimation {
        float lastProgress;

        ProbeAnimation(float duration) {
            super(duration);
        }

        ProbeAnimation(float duration, Ease ease) {
            super(duration, ease);
        }

        @Override
        protected void apply(float progress) {
            lastProgress = progress;
        }
    }

    @Test
    public void progressesAndClamps() {
        ProbeAnimation animation = new ProbeAnimation(2.0f);

        animation.update(0.5f);
        assertEquals(0.25f, animation.getProgress(), 0.0001f);
        assertEquals(0.125f, animation.lastProgress, 0.0001f);

        animation.update(10.0f);

        assertEquals(1.0f, animation.getProgress(), 0.0001f);
        assertEquals(1.0f, animation.lastProgress, 0.0001f);
        assertTrue(animation.isFinished());
    }

    @Test
    public void zeroDurationCompletesImmediately() {
        ProbeAnimation animation = new ProbeAnimation(0.0f);

        animation.update(0.0f);

        assertEquals(1.0f, animation.getProgress(), 0.0001f);
        assertEquals(1.0f, animation.lastProgress, 0.0001f);
        assertTrue(animation.isFinished());
    }

    @Test
    public void negativeDeltaTimeIsIgnored() {
        ProbeAnimation animation = new ProbeAnimation(1.0f);

        animation.update(-1.0f);

        assertEquals(0.0f, animation.getProgress(), 0.0001f);
        assertEquals(0.0f, animation.lastProgress, 0.0001f);
    }

    @Test
    public void supportsEveryEaseAndDefaultsToInOut() {
        assertEquals(0.25f, Ease.Linear.apply(0.25f), 0.0001f);
        assertEquals(0.0625f, Ease.In.apply(0.25f), 0.0001f);
        assertEquals(0.4375f, Ease.Out.apply(0.25f), 0.0001f);
        assertEquals(0.125f, Ease.InOut.apply(0.25f), 0.0001f);

        ProbeAnimation defaultAnimation = new ProbeAnimation(1.0f);
        ProbeAnimation linearAnimation = new ProbeAnimation(1.0f, Ease.Linear);

        defaultAnimation.update(0.25f);
        linearAnimation.update(0.25f);

        assertEquals(Ease.InOut, defaultAnimation.getEase());
        assertEquals(0.125f, defaultAnimation.lastProgress, 0.0001f);
        assertEquals(0.25f, linearAnimation.lastProgress, 0.0001f);
    }
}
