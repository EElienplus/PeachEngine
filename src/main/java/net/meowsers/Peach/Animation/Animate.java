package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Drawables.Circle;
import net.meowsers.Peach.Drawables.Line;
import net.meowsers.Peach.Drawables.Rectangle;
import net.meowsers.Peach.Drawables.Text;
import net.meowsers.Peach.Utils.Enums.Ease;
import org.joml.Vector2f;

public class Animate extends TimedAnimation {

    @FunctionalInterface
    public interface ProgressAction {
        void apply(float progress);
    }

    @FunctionalInterface
    public interface FloatSetter {
        void set(float value);
    }

    @FunctionalInterface
    public interface Vector2Setter {
        void set(Vector2f value);
    }

    private final ProgressAction progressAction;

    private Animate(float duration, Ease ease, ProgressAction progressAction) {
        super(duration, ease);

        if (progressAction == null) {
            throw new IllegalArgumentException("Progress action can't be null.");
        }

        this.progressAction = progressAction;
    }

    public static Animate custom(float duration, ProgressAction progressAction) {
        return custom(duration, Ease.InOut, progressAction);
    }

    public static Animate custom(float duration, Ease ease, ProgressAction progressAction) {
        return AnimationTimeline.record(new Animate(duration, ease, progressAction));
    }

    public static Animate floatValue(float from, float to, float duration, FloatSetter setter) {
        return floatValue(from, to, duration, Ease.InOut, setter);
    }

    public static Animate floatValue(float from, float to, float duration, Ease ease, FloatSetter setter) {
        if (setter == null) {
            throw new IllegalArgumentException("Setter can't be null.");
        }

        return custom(duration, ease, (progress) -> setter.set(lerp(from, to, progress)));
    }

    public static Animate vector2(Vector2f from, Vector2f to, float duration, Vector2Setter setter) {
        return vector2(from, to, duration, Ease.InOut, setter);
    }

    public static Animate vector2(Vector2f from, Vector2f to, float duration, Ease ease, Vector2Setter setter) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Vector points can't be null.");
        }
        if (setter == null) {
            throw new IllegalArgumentException("Setter can't be null.");
        }

        Vector2f fromCopy = new Vector2f(from);
        Vector2f toCopy = new Vector2f(to);

        return custom(duration, ease, (progress) -> setter.set(lerp(fromCopy, toCopy, progress)));
    }

    public static Animate lineEnd(Line line, Vector2f targetEndPos, float duration) {
        return lineEnd(line, targetEndPos, duration, Ease.InOut);
    }

    public static Animate lineEnd(Line line, Vector2f targetEndPos, float duration, Ease ease) {
        if (line == null || targetEndPos == null) {
            throw new IllegalArgumentException("Line and target end position can't be null.");
        }

        Vector2f startEndPos = new Vector2f(line.getEndPos());
        Vector2f targetEndPosCopy = new Vector2f(targetEndPos);

        return vector2(startEndPos, targetEndPosCopy, duration, ease, line::setEndPos);
    }

    public static Animate lineStart(Line line, Vector2f targetStartPos, float duration) {
        return lineStart(line, targetStartPos, duration, Ease.InOut);
    }

    public static Animate lineStart(Line line, Vector2f targetStartPos, float duration, Ease ease) {
        if (line == null || targetStartPos == null) {
            throw new IllegalArgumentException("Line and target start position can't be null.");
        }

        Vector2f startStartPos = new Vector2f(line.getStartPos());
        Vector2f targetStartPosCopy = new Vector2f(targetStartPos);

        return vector2(startStartPos, targetStartPosCopy, duration, ease, line::setStartPos);
    }

    public static Animate rectangle(Rectangle rectangle, float targetX, float targetY, float targetW, float targetH, float duration) {
        return rectangle(rectangle, targetX, targetY, targetW, targetH, duration, Ease.InOut);
    }

    public static Animate rectangle(Rectangle rectangle, float targetX, float targetY, float targetW, float targetH,
                                    float duration, Ease ease) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }

        float startX = rectangle.getX();
        float startY = rectangle.getY();
        float startW = rectangle.getW();
        float startH = rectangle.getH();

        return custom(duration, ease, (progress) -> {
            rectangle.setX(lerp(startX, targetX, progress));
            rectangle.setY(lerp(startY, targetY, progress));
            rectangle.setW(lerp(startW, targetW, progress));
            rectangle.setH(lerp(startH, targetH, progress));
        });
    }

    public static Animate circle(Circle circle, Vector2f targetCenter, int targetRadius, float duration) {
        return circle(circle, targetCenter, targetRadius, duration, Ease.InOut);
    }

    public static Animate circle(Circle circle, Vector2f targetCenter, int targetRadius, float duration, Ease ease) {
        if (circle == null || targetCenter == null) {
            throw new IllegalArgumentException("Circle and target center can't be null.");
        }

        Vector2f startCenter = new Vector2f(circle.getCenter());
        Vector2f targetCenterCopy = new Vector2f(targetCenter);
        int startRadius = circle.getRadius();

        return custom(duration, ease, (progress) -> {
            circle.setCenter(lerp(startCenter, targetCenterCopy, progress));
            circle.setRadius(Math.round(lerp(startRadius, targetRadius, progress)));
        });
    }

    public static Animate textString(Text text, String targetString, float duration) {
        return textString(text, targetString, duration, Ease.InOut);
    }

    public static Animate textString(Text text, String targetString, float duration, Ease ease) {
        if (text == null || targetString == null) {
            throw new IllegalArgumentException("Text and target string can't be null.");
        }

        String target = targetString;

        return custom(duration, ease, (progress) -> {
            int visibleChars = Math.max(0, Math.min(target.length(), (int) Math.floor(target.length() * progress)));

            if (progress >= 1.0f) {
                text.setString(target);
                return;
            }

            text.setString(target.substring(0, visibleChars));
        });
    }

    public static Animate textPosition(Text text, Vector2f targetPosition, float duration) {
        return textPosition(text, targetPosition, duration, Ease.InOut);
    }

    public static Animate textPosition(Text text, Vector2f targetPosition, float duration, Ease ease) {
        if (text == null || targetPosition == null) {
            throw new IllegalArgumentException("Text and target position can't be null.");
        }

        return vector2(text.getPosition(), targetPosition, duration, ease, text::setPosition);
    }

    public static Animate textPosition(Text text, float targetX, float targetY, float duration) {
        return textPosition(text, new Vector2f(targetX, targetY), duration);
    }

    public static Animate textPosition(Text text, float targetX, float targetY, float duration, Ease ease) {
        return textPosition(text, new Vector2f(targetX, targetY), duration, ease);
    }

    @Override
    protected void apply(float progress) {
        progressAction.apply(progress);
    }

    private static float lerp(float from, float to, float progress) {
        return from + ((to - from) * progress);
    }

    private static Vector2f lerp(Vector2f from, Vector2f to, float progress) {
        return new Vector2f(
                lerp(from.x, to.x, progress),
                lerp(from.y, to.y, progress)
        );
    }
}
