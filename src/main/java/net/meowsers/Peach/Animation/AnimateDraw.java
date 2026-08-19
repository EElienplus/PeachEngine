package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Drawables.Arrow;
import net.meowsers.Peach.Drawables.Circle;
import net.meowsers.Peach.Drawables.Curve;
import net.meowsers.Peach.Drawables.Line;
import net.meowsers.Peach.Drawables.Rectangle;
import net.meowsers.Peach.Drawables.Text;
import net.meowsers.Peach.Drawables.Triangle;
import net.meowsers.Peach.Graphics.Draw;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.Enums.AnimationType;
import net.meowsers.Peach.Utils.Enums.Colors;
import net.meowsers.Peach.Utils.Enums.Ease;
import org.joml.Vector2f;

import java.util.List;

public class AnimateDraw extends TimedAnimation {

    private static final Ease DEFAULT_EASE = Ease.InOut;
    private static final AnimationType DEFAULT_ANIMATION_TYPE = AnimationType.OutlineThenFill;
    private static final int OUTLINE_THICKNESS = 2;
    private static final int CIRCLE_SEGMENTS = 64;

    @FunctionalInterface
    public interface DrawAction {
        void draw(float progress);
    }

    @FunctionalInterface
    private interface FilledShapeAction {
        void draw(Color color);
    }

    @FunctionalInterface
    private interface OutlineAction {
        void draw(float progress, Color color);
    }

    private final DrawAction drawAction;
    private final AnimationType animationType;
    private float currentProgress;

    private AnimateDraw(float duration, Ease ease, AnimationType animationType, DrawAction drawAction) {
        super(duration, ease);

        if (animationType == null) {
            throw new IllegalArgumentException("Animation type can't be null.");
        }
        if (drawAction == null) {
            throw new IllegalArgumentException("Draw action can't be null.");
        }

        this.animationType = animationType;
        this.drawAction = drawAction;
    }

    public static AnimateDraw custom(float duration, DrawAction drawAction) {
        return custom(duration, DEFAULT_EASE, drawAction);
    }

    public static AnimateDraw custom(float duration, Ease ease, DrawAction drawAction) {
        return create(duration, ease, DEFAULT_ANIMATION_TYPE, drawAction);
    }

    public static AnimateDraw line(Line line, int thickness, Colors color, float duration) {
        return line(line, thickness, color.getColor(), duration);
    }

    public static AnimateDraw line(Line line, int thickness, Color color, float duration) {
        return line(line, thickness, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw line(Line line, int thickness, Colors color, float duration, Ease ease) {
        return line(line, thickness, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw line(Line line, int thickness, Colors color, float duration, AnimationType animationType) {
        return line(line, thickness, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw line(Line line, int thickness, Colors color, float duration, Ease ease, AnimationType animationType) {
        return line(line, thickness, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw line(Line line, int thickness, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(line, color, ease, animationType, "Line and color can't be null.");

        return create(duration, ease, animationType, (progress) -> {
            Vector2f start = line.getStartPos();
            Vector2f end = line.getEndPos();
            Draw.line(new Line(new Vector2f(start), lerp(start, end, progress)), thickness, color);
        });
    }

    public static AnimateDraw arrow(Arrow arrow, int thickness, Colors color, float duration) {
        return arrow(arrow, thickness, color.getColor(), duration);
    }

    public static AnimateDraw arrow(Arrow arrow, int thickness, Color color, float duration) {
        return arrow(arrow, thickness, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw arrow(Arrow arrow, int thickness, Colors color, float duration, Ease ease) {
        return arrow(arrow, thickness, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw arrow(Arrow arrow, int thickness, Colors color, float duration, AnimationType animationType) {
        return arrow(arrow, thickness, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw arrow(Arrow arrow, int thickness, Colors color, float duration, Ease ease, AnimationType animationType) {
        return arrow(arrow, thickness, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw arrow(Arrow arrow, int thickness, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(arrow, color, ease, animationType, "Arrow and color can't be null.");

        return create(duration, ease, animationType, (progress) -> {
            Line line = arrow.getLine();
            Vector2f start = line.getStartPos();
            Vector2f end = line.getEndPos();
            Draw.arrow(new Arrow(new Line(new Vector2f(start), lerp(start, end, progress))), thickness, color);
        });
    }

    public static AnimateDraw rectangle(Rectangle rectangle, Colors color, float duration) {
        return rectangle(rectangle, color.getColor(), duration);
    }

    public static AnimateDraw rectangle(Rectangle rectangle, Color color, float duration) {
        return rectangle(rectangle, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw rectangle(Rectangle rectangle, Colors color, float duration, Ease ease) {
        return rectangle(rectangle, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw rectangle(Rectangle rectangle, Colors color, float duration, AnimationType animationType) {
        return rectangle(rectangle, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw rectangle(Rectangle rectangle, Colors color, float duration, Ease ease, AnimationType animationType) {
        return rectangle(rectangle, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw rectangle(Rectangle rectangle, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(rectangle, color, ease, animationType, "Rectangle and color can't be null.");

        return create(duration, ease, animationType, (progress) -> {
            if (animationType == AnimationType.Scale) {
                float width = rectangle.getW() * progress;
                float height = rectangle.getH() * progress;
                Draw.rectangle(new Rectangle(
                        rectangle.getX() + ((rectangle.getW() - width) / 2.0f),
                        rectangle.getY() + ((rectangle.getH() - height) / 2.0f),
                        width,
                        height
                ), color);
                return;
            }

            drawStyledShape(
                    progress,
                    animationType,
                    color,
                    (fillColor) -> Draw.rectangle(rectangle, fillColor),
                    (outlineProgress, outlineColor) -> drawRectangleOutline(rectangle, outlineProgress, outlineColor)
            );
        });
    }

    public static AnimateDraw circle(Circle circle, Colors color, float duration) {
        return circle(circle, color.getColor(), duration);
    }

    public static AnimateDraw circle(Circle circle, Color color, float duration) {
        return circle(circle, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw circle(Circle circle, Colors color, float duration, Ease ease) {
        return circle(circle, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw circle(Circle circle, Colors color, float duration, AnimationType animationType) {
        return circle(circle, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw circle(Circle circle, Colors color, float duration, Ease ease, AnimationType animationType) {
        return circle(circle, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw circle(Circle circle, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(circle, color, ease, animationType, "Circle and color can't be null.");

        return create(duration, ease, animationType, (progress) -> {
            if (animationType == AnimationType.Scale) {
                int radius = Math.max(0, Math.round(circle.getRadius() * progress));
                Draw.circle(new Circle(new Vector2f(circle.getCenter()), radius), color);
                return;
            }

            drawStyledShape(
                    progress,
                    animationType,
                    color,
                    (fillColor) -> Draw.circle(circle, fillColor),
                    (outlineProgress, outlineColor) -> drawCircleOutline(circle, outlineProgress, outlineColor)
            );
        });
    }

    public static AnimateDraw triangle(Triangle triangle, Colors color, float duration) {
        return triangle(triangle, color.getColor(), duration);
    }

    public static AnimateDraw triangle(Triangle triangle, Color color, float duration) {
        return triangle(triangle, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw triangle(Triangle triangle, Colors color, float duration, Ease ease) {
        return triangle(triangle, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw triangle(Triangle triangle, Colors color, float duration, AnimationType animationType) {
        return triangle(triangle, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw triangle(Triangle triangle, Colors color, float duration, Ease ease, AnimationType animationType) {
        return triangle(triangle, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw triangle(Triangle triangle, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(triangle, color, ease, animationType, "Triangle and color can't be null.");

        return create(duration, ease, animationType, (progress) -> {
            if (animationType == AnimationType.Scale) {
                Vector2f center = triangleCenter(triangle);
                Draw.triangle(new Triangle(
                        lerp(center, triangle.getPointA(), progress),
                        lerp(center, triangle.getPointB(), progress),
                        lerp(center, triangle.getPointC(), progress)
                ), color);
                return;
            }

            drawStyledShape(
                    progress,
                    animationType,
                    color,
                    (fillColor) -> Draw.triangle(triangle, fillColor),
                    (outlineProgress, outlineColor) -> drawTriangleOutline(triangle, outlineProgress, outlineColor)
            );
        });
    }

    public static AnimateDraw text(Text text, float x, float y, Colors color, float duration) {
        return text(text, x, y, color.getColor(), duration);
    }

    public static AnimateDraw text(Text text, Colors color, float duration) {
        return text(text, color.getColor(), duration);
    }

    public static AnimateDraw text(Text text, Color color, float duration) {
        return text(text, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw text(Text text, Colors color, float duration, Ease ease) {
        return text(text, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw text(Text text, Colors color, float duration, AnimationType animationType) {
        return text(text, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw text(Text text, Colors color, float duration, Ease ease, AnimationType animationType) {
        return text(text, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw text(Text text, Color color, float duration, Ease ease, AnimationType animationType) {
        if (text == null) {
            throw new IllegalArgumentException("Text and color can't be null.");
        }

        Vector2f position = text.getPosition();
        return text(text, position.x, position.y, color, duration, ease, animationType);
    }

    public static AnimateDraw text(Text text, float x, float y, Color color, float duration) {
        return text(text, x, y, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw text(Text text, float x, float y, Colors color, float duration, Ease ease) {
        return text(text, x, y, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw text(Text text, float x, float y, Colors color, float duration, AnimationType animationType) {
        return text(text, x, y, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw text(Text text, float x, float y, Colors color, float duration, Ease ease, AnimationType animationType) {
        return text(text, x, y, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw text(Text text, float x, float y, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(text, color, ease, animationType, "Text and color can't be null.");
        text.setPosition(x, y);
        Text animatedText = new Text("", text.getFontPath(), text.getFontSize());

        return create(duration, ease, animationType, (progress) -> {
            Vector2f position = text.getPosition();

            if (animationType == AnimationType.Scale) {
                int fontSize = Math.max(1, Math.round(text.getFontSize() * progress));
                Draw.text(new Text(text.getString(), text.getFontPath(), fontSize), position.x, position.y, color);
                return;
            }

            float revealProgress = getOutlineProgress(animationType, progress);
            int visibleChars = visibleCharacterCount(text.getString(), revealProgress);
            animatedText.setString(text.getString().substring(0, visibleChars));

            float fillOpacity = getFillProgress(animationType, progress);
            float textOpacity = animationType == AnimationType.OutlineThenFill
                    ? 0.35f + (0.65f * fillOpacity)
                    : progress;
            Draw.text(animatedText, position.x, position.y, withOpacity(color, textOpacity));
        });
    }

    public static AnimateDraw curve(Curve curve, int thickness, Colors color, float duration) {
        return curve(curve, thickness, color.getColor(), duration);
    }

    public static AnimateDraw curve(Curve curve, int thickness, Color color, float duration) {
        return curve(curve, thickness, color, duration, DEFAULT_EASE, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw curve(Curve curve, int thickness, Colors color, float duration, Ease ease) {
        return curve(curve, thickness, color.getColor(), duration, ease, DEFAULT_ANIMATION_TYPE);
    }

    public static AnimateDraw curve(Curve curve, int thickness, Colors color, float duration, AnimationType animationType) {
        return curve(curve, thickness, color.getColor(), duration, DEFAULT_EASE, animationType);
    }

    public static AnimateDraw curve(Curve curve, int thickness, Colors color, float duration, Ease ease, AnimationType animationType) {
        return curve(curve, thickness, color.getColor(), duration, ease, animationType);
    }

    public static AnimateDraw curve(Curve curve, int thickness, Color color, float duration, Ease ease, AnimationType animationType) {
        validate(curve, color, ease, animationType, "Curve and color can't be null.");

        return create(duration, ease, animationType, (progress) -> drawOpenPath(curve.getPoints(), progress, thickness, color));
    }

    @Override
    protected void apply(float progress) {
        currentProgress = progress;
    }

    @Override
    public void render() {
        drawAction.draw(currentProgress);
    }

    @Override
    public void reset() {
        super.reset();
        currentProgress = 0.0f;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    static float getOutlineProgress(AnimationType animationType, float progress) {
        float clampedProgress = clamp(progress);

        if (animationType == AnimationType.OutlineThenFill) {
            return Math.min(clampedProgress * 2.0f, 1.0f);
        }
        if (animationType == AnimationType.OutlineWithFill) {
            return clampedProgress;
        }

        return 0.0f;
    }

    static float getFillProgress(AnimationType animationType, float progress) {
        float clampedProgress = clamp(progress);

        if (animationType == AnimationType.OutlineThenFill) {
            return Math.max((clampedProgress * 2.0f) - 1.0f, 0.0f);
        }
        if (animationType == AnimationType.OutlineWithFill) {
            return clampedProgress;
        }

        return 1.0f;
    }

    private static AnimateDraw create(float duration, Ease ease, AnimationType animationType, DrawAction drawAction) {
        return AnimationTimeline.record(new AnimateDraw(duration, ease, animationType, drawAction));
    }

    private static void drawStyledShape(float progress, AnimationType animationType, Color color,
                                        FilledShapeAction fillAction, OutlineAction outlineAction) {
        float fillProgress = getFillProgress(animationType, progress);
        float outlineProgress = getOutlineProgress(animationType, progress);

        if (fillProgress > 0.0f) {
            fillAction.draw(withOpacity(color, fillProgress));
        }
        if (outlineProgress > 0.0f) {
            outlineAction.draw(outlineProgress, color);
        }
    }

    private static void drawRectangleOutline(Rectangle rectangle, float progress, Color color) {
        Vector2f[] points = {
                new Vector2f(rectangle.getX(), rectangle.getY()),
                new Vector2f(rectangle.getX() + rectangle.getW(), rectangle.getY()),
                new Vector2f(rectangle.getX() + rectangle.getW(), rectangle.getY() + rectangle.getH()),
                new Vector2f(rectangle.getX(), rectangle.getY() + rectangle.getH())
        };
        drawClosedPath(points, progress, OUTLINE_THICKNESS, color);
    }

    private static void drawCircleOutline(Circle circle, float progress, Color color) {
        Vector2f[] points = new Vector2f[CIRCLE_SEGMENTS];

        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            float angle = (float) (Math.PI * 2.0 * i / CIRCLE_SEGMENTS);
            points[i] = new Vector2f(
                    circle.getCenter().x + ((float) Math.cos(angle) * circle.getRadius()),
                    circle.getCenter().y + ((float) Math.sin(angle) * circle.getRadius())
            );
        }

        drawClosedPath(points, progress, OUTLINE_THICKNESS, color);
    }

    private static void drawTriangleOutline(Triangle triangle, float progress, Color color) {
        drawClosedPath(new Vector2f[]{
                triangle.getPointA(),
                triangle.getPointB(),
                triangle.getPointC()
        }, progress, OUTLINE_THICKNESS, color);
    }

    private static void drawClosedPath(Vector2f[] points, float progress, int thickness, Color color) {
        if (points.length < 2 || progress <= 0.0f) {
            return;
        }

        float totalLength = 0.0f;

        for (int i = 0; i < points.length; i++) {
            totalLength += points[i].distance(points[(i + 1) % points.length]);
        }

        drawPath(points, points.length, totalLength * clamp(progress), thickness, color, true);
    }

    private static void drawOpenPath(List<Vector2f> points, float progress, int thickness, Color color) {
        if (points.size() < 2 || progress <= 0.0f) {
            return;
        }

        Vector2f[] path = points.toArray(new Vector2f[0]);
        float totalLength = 0.0f;

        for (int i = 0; i < path.length - 1; i++) {
            totalLength += path[i].distance(path[i + 1]);
        }

        drawPath(path, path.length - 1, totalLength * clamp(progress), thickness, color, false);
    }

    private static void drawPath(Vector2f[] points, int segmentCount, float targetLength,
                                 int thickness, Color color, boolean closed) {
        float drawnLength = 0.0f;

        for (int i = 0; i < segmentCount; i++) {
            Vector2f start = points[i];
            Vector2f end = closed ? points[(i + 1) % points.length] : points[i + 1];
            float segmentLength = start.distance(end);

            if (drawnLength + segmentLength <= targetLength) {
                Draw.line(new Line(start, end), thickness, color);
                drawnLength += segmentLength;
                continue;
            }

            float remainingLength = targetLength - drawnLength;
            if (remainingLength > 0.0f && segmentLength > 0.0f) {
                Draw.line(new Line(start, lerp(start, end, remainingLength / segmentLength)), thickness, color);
            }
            break;
        }
    }

    private static Vector2f triangleCenter(Triangle triangle) {
        return new Vector2f(
                (triangle.getPointA().x + triangle.getPointB().x + triangle.getPointC().x) / 3.0f,
                (triangle.getPointA().y + triangle.getPointB().y + triangle.getPointC().y) / 3.0f
        );
    }

    private static int visibleCharacterCount(String text, float progress) {
        if (progress >= 1.0f) {
            return text.length();
        }

        return Math.max(0, Math.min(text.length(), (int) Math.floor(text.length() * progress)));
    }

    private static Color withOpacity(Color color, float opacity) {
        return new Color(color.getR(), color.getG(), color.getB(), color.getA() * clamp(opacity));
    }

    private static void validate(Object drawable, Color color, Ease ease, AnimationType animationType, String message) {
        if (drawable == null || color == null) {
            throw new IllegalArgumentException(message);
        }
        if (ease == null) {
            throw new IllegalArgumentException("Ease can't be null.");
        }
        if (animationType == null) {
            throw new IllegalArgumentException("Animation type can't be null.");
        }
    }

    private static float clamp(float progress) {
        return Math.max(0.0f, Math.min(progress, 1.0f));
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
