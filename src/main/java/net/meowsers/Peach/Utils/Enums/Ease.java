package net.meowsers.Peach.Utils.Enums;

public enum Ease {
    Linear,
    In,
    Out,
    InOut;

    public float apply(float progress) {
        float clampedProgress = Math.max(0.0f, Math.min(progress, 1.0f));

        switch (this) {
            case In:
                return clampedProgress * clampedProgress;
            case Out:
                return 1.0f - ((1.0f - clampedProgress) * (1.0f - clampedProgress));
            case InOut:
                if (clampedProgress < 0.5f) {
                    return 2.0f * clampedProgress * clampedProgress;
                }

                float remainingProgress = -2.0f * clampedProgress + 2.0f;
                return 1.0f - ((remainingProgress * remainingProgress) / 2.0f);
            default:
                return clampedProgress;
        }
    }
}
