package net.meowsers.Peach.Animation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class AnimationTimeline {

    private static final ThreadLocal<AnimationTimeline> recordingTimeline = new ThreadLocal<>();

    private final Queue<Animation> animations = new ArrayDeque<>();
    private final List<Animation> completedAnimations = new ArrayList<>();
    private final Deque<List<Animation>> recordingGroups = new ArrayDeque<>();

    private Animation currentAnimation;

    public static boolean isRecording() {
        return recordingTimeline.get() != null;
    }

    public void record(Runnable script) {
        if (script == null) {
            throw new IllegalArgumentException("Script can't be null.");
        }
        if (recordingTimeline.get() != null) {
            throw new IllegalStateException("An animation script is already being recorded.");
        }

        recordingTimeline.set(this);

        try {
            script.run();
        } finally {
            recordingTimeline.remove();
        }
    }

    public void recordTogether(Runnable animations) {
        if (animations == null) {
            throw new IllegalArgumentException("Animations can't be null.");
        }

        ensureRecording();

        List<Animation> groupAnimations = new ArrayList<>();
        recordingGroups.push(groupAnimations);

        try {
            animations.run();
        } finally {
            recordingGroups.pop();
        }

        if (!groupAnimations.isEmpty()) {
            recordAnimation(new AnimationGroup(groupAnimations));
        }
    }

    public void recordWait(float duration) {
        WaitAnimation waitAnimation = new WaitAnimation(duration);
        ensureRecording();
        recordAnimation(waitAnimation);
    }

    public void update(float deltaTime) {
        while (true) {
            if (currentAnimation == null) {
                currentAnimation = animations.poll();

                if (currentAnimation != null) {
                    currentAnimation.begin();
                }
            }

            if (currentAnimation == null) {
                return;
            }

            currentAnimation.update(deltaTime);

            if (currentAnimation.isFinished()) {
                completedAnimations.add(currentAnimation);
                boolean wasInstant = (currentAnimation instanceof DrawAnimation)
                        || (currentAnimation instanceof TimedAnimation && ((TimedAnimation) currentAnimation).getDuration() == 0.0f);
                currentAnimation = null;

                if (wasInstant) {
                    continue;
                }
            }
            break;
        }
    }

    public void render() {
        for (Animation animation : completedAnimations) {
            animation.render();
        }

        if (currentAnimation != null) {
            currentAnimation.render();
        }
    }

    static Animate record(Animate animation) {
        AnimationTimeline timeline = recordingTimeline.get();

        if (timeline != null) {
            timeline.recordAnimation(animation);
        }

        return animation;
    }

    static AnimateDraw record(AnimateDraw animation) {
        AnimationTimeline timeline = recordingTimeline.get();

        if (timeline != null) {
            timeline.recordAnimation(animation);
        }

        return animation;
    }

    public static DrawAnimation record(DrawAnimation animation) {
        AnimationTimeline timeline = recordingTimeline.get();

        if (timeline != null) {
            timeline.recordAnimation(animation);
        }

        return animation;
    }

    private void recordAnimation(Animation animation) {
        if (recordingGroups.isEmpty()) {
            animations.offer(animation);
            return;
        }

        recordingGroups.peek().add(animation);
    }

    private void ensureRecording() {
        if (recordingTimeline.get() != this) {
            throw new IllegalStateException("Timeline entries can only be added while recording a script.");
        }
    }
}
