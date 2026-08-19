package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Drawables.Circle;
import net.meowsers.Peach.Drawables.Curve;
import net.meowsers.Peach.Drawables.Line;
import net.meowsers.Peach.Drawables.Rectangle;
import net.meowsers.Peach.Drawables.Text;
import net.meowsers.Peach.Graphics.Visualize;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.LiveVector2f;
import net.meowsers.Peach.Utils.Enums.AnimationType;
import net.meowsers.Peach.Utils.Enums.Ease;
import net.meowsers.Peach.Utils.Enums.Fonts;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimateAndAnimateDrawTest {

    @Test
    public void animateLineEndInterpolates() {
        Line line = new Line(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f));
        Animate animateLine = Animate.lineEnd(line, new Vector2f(10.0f, 10.0f), 2.0f);

        animateLine.update(1.0f);

        assertEquals(5.0f, line.getEndPos().x, 0.0001f);
        assertEquals(5.0f, line.getEndPos().y, 0.0001f);

        animateLine.update(1.0f);

        assertEquals(10.0f, line.getEndPos().x, 0.0001f);
        assertEquals(10.0f, line.getEndPos().y, 0.0001f);
        assertTrue(animateLine.isFinished());
    }

    @Test
    public void animateVector2InterpolatesCopiedPositions() {
        Vector2f from = new Vector2f(10.0f, 20.0f);
        Vector2f target = new Vector2f(30.0f, 40.0f);
        AtomicReference<Vector2f> position = new AtomicReference<>();
        Animate animation = Animate.vector2(from, target, 2.0f, Ease.Linear, position::set);

        from.set(100.0f, 100.0f);
        target.set(200.0f, 200.0f);
        animation.update(1.0f);

        assertEquals(20.0f, position.get().x, 0.0001f);
        assertEquals(30.0f, position.get().y, 0.0001f);
    }

    @Test
    public void animateVector2RejectsNullPositions() {
        assertThrows(IllegalArgumentException.class, () -> Animate.vector2(null, new Vector2f(), 1.0f, (position) -> {
        }));
        assertThrows(IllegalArgumentException.class, () -> Animate.vector2(new Vector2f(), null, 1.0f, (position) -> {
        }));
    }

    @Test
    public void animatedTextKeepsObjectsUsingItsPositionAttached() {
        Text text = new Text("Attached", Fonts.Default, 20);
        text.setPosition(10.0f, 20.0f);
        Line attachedLine = new Line(new Vector2f(), text.getPosition());

        Animate.textPosition(text, 30.0f, 40.0f, 1.0f, Ease.Linear).update(0.5f);

        assertEquals(new Vector2f(20.0f, 30.0f), attachedLine.getEndPos());
    }

    @Test
    public void animatedShapeKeepsObjectsUsingItsPositionAttached() {
        Circle circle = new Circle(new Vector2f(10.0f, 20.0f), 5);
        Line attachedLine = new Line(new Vector2f(), circle.getCenter());

        Animate.circle(circle, new Vector2f(30.0f, 40.0f), 10, 1.0f, Ease.Linear).update(0.5f);

        assertEquals(new Vector2f(20.0f, 30.0f), attachedLine.getEndPos());
    }

    @Test
    public void livePositionsRefreshAllVectorBasedDrawableGeometry() {
        AtomicReference<Vector2f> position = new AtomicReference<>(new Vector2f(10.0f, 20.0f));
        LiveVector2f livePosition = new LiveVector2f(position::get);
        Rectangle rectangle = new Rectangle(livePosition, 30.0f, 40.0f);
        Curve curve = new Curve(
                new Vector2f(),
                new Vector2f(),
                new Vector2f(),
                livePosition,
                1.0f
        );

        position.set(new Vector2f(50.0f, 60.0f));

        assertEquals(50.0f, rectangle.getX(), 0.0001f);
        assertEquals(60.0f, rectangle.getY(), 0.0001f);
        assertEquals(new Vector2f(50.0f, 60.0f), curve.getPointBasedOnT(1.0f));
        assertEquals(new Vector2f(50.0f, 60.0f), curve.getPoints().get(curve.getPoints().size() - 1));
    }

    @Test
    public void livePositionsRejectMissingValues() {
        assertThrows(IllegalArgumentException.class, () -> new LiveVector2f(null));

        AtomicBoolean returnValue = new AtomicBoolean(true);
        LiveVector2f position = new LiveVector2f(() -> returnValue.get() ? new Vector2f() : null);
        returnValue.set(false);

        assertThrows(IllegalStateException.class, position::refresh);
    }

    @Test
    public void animateSupportsCustomEase() {
        AtomicReference<Float> value = new AtomicReference<>(-1.0f);
        Animate animation = Animate.floatValue(0.0f, 1.0f, 1.0f, Ease.Linear, value::set);

        animation.update(0.25f);

        assertEquals(Ease.Linear, animation.getEase());
        assertEquals(0.25f, value.get(), 0.0001f);
    }

    @Test
    public void animateDrawCustomUsesCurrentProgress() {
        AtomicReference<Float> sampledProgress = new AtomicReference<>(-1.0f);
        AnimateDraw animateDraw = AnimateDraw.custom(2.0f, sampledProgress::set);

        animateDraw.update(1.0f);
        animateDraw.render();
        assertEquals(0.5f, sampledProgress.get(), 0.0001f);

        animateDraw.update(1.0f);
        animateDraw.render();
        assertEquals(1.0f, sampledProgress.get(), 0.0001f);
        assertTrue(animateDraw.isFinished());
    }

    @Test
    public void animateDrawZeroDurationCompletesImmediately() {
        AtomicReference<Float> sampledProgress = new AtomicReference<>(-1.0f);
        AnimateDraw animateDraw = AnimateDraw.custom(0.0f, sampledProgress::set);

        animateDraw.update(0.0f);
        animateDraw.render();

        assertEquals(1.0f, sampledProgress.get(), 0.0001f);
        assertTrue(animateDraw.isFinished());
    }

    @Test
    public void animateDrawDefaultsAndOverloadsAreCustomizable() {
        Circle circle = new Circle(new Vector2f(10.0f, 10.0f), 8);
        Color color = new Color(1.0f, 0.5f, 0.25f, 0.75f);

        AnimateDraw defaultAnimation = AnimateDraw.circle(circle, color, 2.0f);
        AnimateDraw customizedAnimation = AnimateDraw.circle(
                circle,
                color,
                2.0f,
                Ease.Linear,
                AnimationType.Scale
        );

        assertEquals(Ease.InOut, defaultAnimation.getEase());
        assertEquals(AnimationType.OutlineThenFill, defaultAnimation.getAnimationType());
        assertEquals(Ease.Linear, customizedAnimation.getEase());
        assertEquals(AnimationType.Scale, customizedAnimation.getAnimationType());
    }

    @Test
    public void visualizedTextCanBeAnimatedFromItsStoredPosition() {
        Text text = Visualize._vec4(69, 314, 55, 77, 50.0f, 60.0f, 25);
        Color color = new Color(1.0f, 1.0f, 1.0f);

        AnimateDraw defaultAnimation = AnimateDraw.text(text, color, 1.0f);
        AnimateDraw customizedAnimation = AnimateDraw.text(
                text,
                color,
                1.0f,
                Ease.Linear,
                AnimationType.Scale
        );

        assertEquals("(69, 314, 55, 77)", text.getString());
        assertEquals(new Vector2f(50.0f, 60.0f), text.getPosition());
        assertEquals(Ease.InOut, defaultAnimation.getEase());
        assertEquals(AnimationType.OutlineThenFill, defaultAnimation.getAnimationType());
        assertEquals(Ease.Linear, customizedAnimation.getEase());
        assertEquals(AnimationType.Scale, customizedAnimation.getAnimationType());

        Line line = new Line(
                new Vector2f(0.0f, 0.0f),
                new LiveVector2f(() -> new Vector2f(text.getPosition()).add(25.0f, 10.0f))
        );
        Animate movement = Animate.textPosition(text, 100.0f, 120.0f, 1.0f, Ease.Linear);
        movement.update(0.5f);

        assertEquals(new Vector2f(75.0f, 90.0f), text.getPosition());
        assertEquals(new Vector2f(100.0f, 100.0f), line.getEndPos());
    }

    @Test
    public void animateDrawRejectsNullCustomizationOptions() {
        Circle circle = new Circle(new Vector2f(), 8);
        Color color = new Color(1.0f, 1.0f, 1.0f);

        assertThrows(IllegalArgumentException.class, () -> AnimateDraw.circle(
                circle,
                color,
                1.0f,
                null,
                AnimationType.Scale
        ));
        assertThrows(IllegalArgumentException.class, () -> AnimateDraw.circle(
                circle,
                color,
                1.0f,
                Ease.Linear,
                null
        ));
    }

    @Test
    public void outlineThenFillUsesSequentialPhases() {
        assertEquals(0.5f, AnimateDraw.getOutlineProgress(AnimationType.OutlineThenFill, 0.25f), 0.0001f);
        assertEquals(0.0f, AnimateDraw.getFillProgress(AnimationType.OutlineThenFill, 0.25f), 0.0001f);
        assertEquals(1.0f, AnimateDraw.getOutlineProgress(AnimationType.OutlineThenFill, 0.75f), 0.0001f);
        assertEquals(0.5f, AnimateDraw.getFillProgress(AnimationType.OutlineThenFill, 0.75f), 0.0001f);
    }

    @Test
    public void outlineWithFillUsesSimultaneousPhases() {
        assertEquals(0.25f, AnimateDraw.getOutlineProgress(AnimationType.OutlineWithFill, 0.25f), 0.0001f);
        assertEquals(0.25f, AnimateDraw.getFillProgress(AnimationType.OutlineWithFill, 0.25f), 0.0001f);
        assertEquals(1.0f, AnimateDraw.getOutlineProgress(AnimationType.OutlineWithFill, 2.0f), 0.0001f);
        assertEquals(0.0f, AnimateDraw.getFillProgress(AnimationType.OutlineWithFill, -1.0f), 0.0001f);
    }
}
