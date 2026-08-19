package net.meowsers.Peach.Animation;

import java.util.ArrayList;
import java.util.List;

public class AnimationGroup implements Animation {

    private final List<Animation> animations;

    public AnimationGroup(List<Animation> animations) {
        if (animations == null) {
            throw new IllegalArgumentException("Animations can't be null.");
        }

        this.animations = new ArrayList<>(animations);

        for (Animation animation : this.animations) {
            if (animation == null) {
                throw new IllegalArgumentException("Animation in group can't be null.");
            }
        }
    }

    @Override
    public void begin() {
        for (Animation animation : animations) {
            animation.begin();
        }
    }

    @Override
    public void update(float deltaTime) {
        for (Animation animation : animations) {
            if (!animation.isFinished()) {
                animation.update(deltaTime);
            }
        }
    }

    @Override
    public void render() {
        for (Animation animation : animations) {
            animation.render();
        }
    }

    @Override
    public boolean isFinished() {
        for (Animation animation : animations) {
            if (!animation.isFinished()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void reset() {
        for (Animation animation : animations) {
            animation.reset();
        }
    }
}