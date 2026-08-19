package net.meowsers.Peach.Animation;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnimationTimelineTest {

    @Test
    public void recordsDirectAnimationCallsFromScriptSequentially() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicReference<Float> firstProgress = new AtomicReference<>(-1.0f);
        AtomicReference<Float> secondProgress = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            Animate.custom(1.0f, firstProgress::set);
            Animate.custom(1.0f, secondProgress::set);
        });

        timeline.update(0.5f);
        assertEquals(0.5f, firstProgress.get(), 0.0001f);
        assertEquals(-1.0f, secondProgress.get(), 0.0001f);

        timeline.update(0.5f);
        timeline.update(0.5f);

        assertEquals(1.0f, firstProgress.get(), 0.0001f);
        assertEquals(0.5f, secondProgress.get(), 0.0001f);
    }

    @Test
    public void keepsCompletedDrawAnimationsVisible() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicInteger firstDrawCount = new AtomicInteger();
        AtomicInteger secondDrawCount = new AtomicInteger();
        AtomicReference<Float> firstProgress = new AtomicReference<>(-1.0f);
        AtomicReference<Float> secondProgress = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            AnimateDraw.custom(1.0f, (progress) -> {
                firstProgress.set(progress);
                firstDrawCount.incrementAndGet();
            });
            AnimateDraw.custom(1.0f, (progress) -> {
                secondProgress.set(progress);
                secondDrawCount.incrementAndGet();
            });
        });

        timeline.update(1.0f);
        timeline.render();
        timeline.update(0.5f);
        timeline.render();

        assertEquals(2, firstDrawCount.get());
        assertEquals(1.0f, firstProgress.get(), 0.0001f);
        assertEquals(1, secondDrawCount.get());
        assertEquals(0.5f, secondProgress.get(), 0.0001f);
    }

    @Test
    public void completedDrawingReflectsLaterPropertyAnimations() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicReference<Float> property = new AtomicReference<>(0.0f);
        AtomicReference<Float> drawnValue = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            AnimateDraw.custom(0.0f, (progress) -> drawnValue.set(property.get()));
            Animate.custom(1.0f, property::set);
        });

        timeline.update(0.0f);
        timeline.update(0.5f);
        timeline.render();

        assertEquals(0.5f, drawnValue.get(), 0.0001f);
    }

    @Test
    public void animatesGroupedCallsAtTheSameTime() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicReference<Float> firstProgress = new AtomicReference<>(-1.0f);
        AtomicReference<Float> secondProgress = new AtomicReference<>(-1.0f);
        AtomicReference<Float> followingProgress = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            timeline.recordTogether(() -> {
                Animate.custom(1.0f, firstProgress::set);
                Animate.custom(2.0f, secondProgress::set);
            });
            Animate.custom(1.0f, followingProgress::set);
        });

        timeline.update(0.5f);
        assertEquals(0.5f, firstProgress.get(), 0.0001f);
        assertEquals(0.125f, secondProgress.get(), 0.0001f);
        assertEquals(-1.0f, followingProgress.get(), 0.0001f);

        timeline.update(1.5f);
        timeline.update(0.5f);

        assertEquals(1.0f, firstProgress.get(), 0.0001f);
        assertEquals(1.0f, secondProgress.get(), 0.0001f);
        assertEquals(0.5f, followingProgress.get(), 0.0001f);
    }

    @Test
    public void keepsCompletedGroupedDrawAnimationsVisible() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicInteger firstDrawCount = new AtomicInteger();
        AtomicInteger secondDrawCount = new AtomicInteger();

        timeline.record(() -> {
            timeline.recordTogether(() -> {
                AnimateDraw.custom(0.5f, (progress) -> firstDrawCount.incrementAndGet());
                AnimateDraw.custom(1.0f, (progress) -> secondDrawCount.incrementAndGet());
            });
            Animate.custom(1.0f, (progress) -> {
            });
        });

        timeline.update(1.0f);
        timeline.render();
        timeline.update(0.5f);
        timeline.render();

        assertEquals(2, firstDrawCount.get());
        assertEquals(2, secondDrawCount.get());
    }

    @Test
    public void waitDelaysFollowingAnimation() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicReference<Float> progress = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            timeline.recordWait(1.0f);
            Animate.custom(1.0f, progress::set);
        });

        timeline.update(0.75f);
        assertEquals(-1.0f, progress.get(), 0.0001f);

        timeline.update(0.25f);
        timeline.update(0.5f);
        assertEquals(0.5f, progress.get(), 0.0001f);
    }

    @Test
    public void ignoresEmptyAnimationGroups() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicReference<Float> progress = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            timeline.recordTogether(() -> {
            });
            Animate.custom(1.0f, progress::set);
        });

        timeline.update(0.5f);

        assertEquals(0.5f, progress.get(), 0.0001f);
    }

    @Test
    public void rejectsInvalidGroupsAndWaits() {
        AnimationTimeline timeline = new AnimationTimeline();

        assertThrows(IllegalArgumentException.class, () -> timeline.recordTogether(null));
        assertThrows(IllegalArgumentException.class, () -> timeline.recordWait(-1.0f));
        assertThrows(IllegalStateException.class, () -> timeline.recordTogether(() -> {
        }));
        assertThrows(IllegalStateException.class, () -> timeline.recordWait(1.0f));
    }

    @Test
    public void rejectsNullScript() {
        AnimationTimeline timeline = new AnimationTimeline();

        assertThrows(IllegalArgumentException.class, () -> timeline.record((Runnable) null));
    }

    @Test
    public void stopsRecordingWhenScriptFails() {
        AnimationTimeline timeline = new AnimationTimeline();

        assertThrows(IllegalStateException.class, () -> timeline.record(() -> {
            throw new IllegalStateException("Broken script");
        }));

        timeline.record(() -> Animate.custom(0.0f, (progress) -> {
        }));
        timeline.update(0.0f);
    }
}
