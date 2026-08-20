package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Drawables.Arrow;
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

    @Test
    public void sequentialRectangleAnimationsContinueFromPreviousEndPosition() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rectangle = new Rectangle(100, 100, 50, 50);

        timeline.record(() -> {
            Animate.rectangle(rectangle, 200, 200, 50, 50, 1.0f, Ease.Linear);
            Animate.rectangle(rectangle, 300, 400, 50, 50, 1.0f, Ease.Linear);
        });

        timeline.update(1.0f);
        assertEquals(200.0f, rectangle.getX(), 0.0001f);
        assertEquals(200.0f, rectangle.getY(), 0.0001f);

        timeline.update(0.5f);
        assertEquals(250.0f, rectangle.getX(), 0.0001f);
        assertEquals(300.0f, rectangle.getY(), 0.0001f);

        timeline.update(0.5f);
        assertEquals(300.0f, rectangle.getX(), 0.0001f);
        assertEquals(400.0f, rectangle.getY(), 0.0001f);
    }

    @Test
    public void arrowWithLiveEndpointsTracksMovingRectangles() {
        Rectangle rect1 = new Rectangle(100, 350, 75, 75);
        Rectangle rect2 = new Rectangle(325, 350, 50, 50);

        LiveVector2f start = new LiveVector2f(() -> new Vector2f(rect1.getX() + rect1.getW() + 20, rect1.getY() + rect1.getH() / 2));
        LiveVector2f end = new LiveVector2f(() -> new Vector2f(rect2.getX() - 20, rect2.getY() + rect2.getH() / 2));
        Arrow arrow = new Arrow(new Line(start, end));

        assertEquals(195.0f, arrow.getLine().getStartPos().x, 0.0001f);
        assertEquals(387.5f, arrow.getLine().getStartPos().y, 0.0001f);
        assertEquals(305.0f, arrow.getLine().getEndPos().x, 0.0001f);
        assertEquals(375.0f, arrow.getLine().getEndPos().y, 0.0001f);

        Animate.rectangle(rect2, 500, 300, 50, 50, 1.0f, Ease.Linear).update(0.5f);

        assertEquals(412.5f, rect2.getX(), 0.0001f);
        assertEquals(325.0f, rect2.getY(), 0.0001f);
        assertEquals(392.5f, arrow.getLine().getEndPos().x, 0.0001f);
        assertEquals(350.0f, arrow.getLine().getEndPos().y, 0.0001f);
    }

    @Test
    public void sequentialCircleAndLineAndTextAnimationsCaptureInitialStateOnBegin() {
        AnimationTimeline timeline = new AnimationTimeline();
        Circle circle = new Circle(new Vector2f(10, 10), 5);
        Line line = new Line(new Vector2f(0, 0), new Vector2f(10, 10));
        Text text = new Text("Peach", Fonts.Default, 20);
        text.setPosition(0, 0);

        timeline.record(() -> {
            Animate.circle(circle, new Vector2f(50, 50), 10, 1.0f, Ease.Linear);
            Animate.circle(circle, new Vector2f(100, 100), 20, 1.0f, Ease.Linear);
            Animate.lineEnd(line, new Vector2f(50, 50), 1.0f, Ease.Linear);
            Animate.lineEnd(line, new Vector2f(100, 100), 1.0f, Ease.Linear);
            Animate.lineStart(line, new Vector2f(20, 20), 1.0f, Ease.Linear);
            Animate.lineStart(line, new Vector2f(80, 80), 1.0f, Ease.Linear);
            Animate.textPosition(text, 50, 50, 1.0f, Ease.Linear);
            Animate.textPosition(text, 100, 100, 1.0f, Ease.Linear);
        });

        // 1st circle animation
        timeline.update(1.0f);
        assertEquals(new Vector2f(50, 50), circle.getCenter());
        assertEquals(10, circle.getRadius());

        // 2nd circle animation halfway
        timeline.update(0.5f);
        assertEquals(new Vector2f(75, 75), circle.getCenter());
        assertEquals(15, circle.getRadius());

        timeline.update(0.5f);
        assertEquals(new Vector2f(100, 100), circle.getCenter());
        assertEquals(20, circle.getRadius());

        // 1st lineEnd animation
        timeline.update(1.0f);
        assertEquals(new Vector2f(50, 50), line.getEndPos());

        // 2nd lineEnd animation halfway
        timeline.update(0.5f);
        assertEquals(new Vector2f(75, 75), line.getEndPos());

        timeline.update(0.5f);
        assertEquals(new Vector2f(100, 100), line.getEndPos());

        // 1st lineStart animation
        timeline.update(1.0f);
        assertEquals(new Vector2f(20, 20), line.getStartPos());

        // 2nd lineStart animation halfway
        timeline.update(0.5f);
        assertEquals(new Vector2f(50, 50), line.getStartPos());

        timeline.update(0.5f);
        assertEquals(new Vector2f(80, 80), line.getStartPos());

        // 1st textPosition animation
        timeline.update(1.0f);
        assertEquals(new Vector2f(50, 50), text.getPosition());

        // 2nd textPosition animation halfway
        timeline.update(0.5f);
        assertEquals(new Vector2f(75, 75), text.getPosition());

        timeline.update(0.5f);
        assertEquals(new Vector2f(100, 100), text.getPosition());
    }

    @Test
    public void imageAnimationValidation() {
        Rectangle rect = new Rectangle(10, 10, 100, 100);
        assertThrows(IllegalArgumentException.class, () -> AnimateDraw.image(null, rect, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> AnimateDraw.image(null, 10, 10, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> AnimateDraw.image(null, 10, 10, 1.0f, 1.0f));
    }

    @Test
    public void imageAnimationCustomDrawActionWorksWithTimeline() {
        AnimationTimeline timeline = new AnimationTimeline();
        AtomicReference<Float> renderedProgress = new AtomicReference<>(-1.0f);

        timeline.record(() -> {
            AnimateDraw.custom(2.0f, renderedProgress::set);
        });

        timeline.update(1.0f);
        timeline.render();
        assertEquals(0.5f, renderedProgress.get(), 0.0001f);

        timeline.update(1.0f);
        timeline.render();
        assertEquals(1.0f, renderedProgress.get(), 0.0001f);
    }

    @Test
    public void animateRectanglePositionAndMove() {
        Rectangle rectangle = new Rectangle(50.0f, -200.0f, 100.0f, 100.0f);
        AnimationTimeline timeline = new AnimationTimeline();

        timeline.record(() -> {
            Animate.rectangle(rectangle, 50.0f, 100.0f, 1.0f, Ease.Linear);
            Animate.rectangleMove(rectangle, 0.0f, 50.0f, 1.0f, Ease.Linear);
            Animate.rectangle(rectangle, new Vector2f(100.0f, 200.0f), 1.0f, Ease.Linear);
            Animate.rectangleMove(rectangle, new Vector2f(10.0f, 20.0f), 1.0f, Ease.Linear);
        });

        // 1st animation: move from (50, -200) to (50, 100)
        timeline.update(0.5f);
        assertEquals(50.0f, rectangle.getX(), 0.0001f);
        assertEquals(-50.0f, rectangle.getY(), 0.0001f);

        timeline.update(0.5f);
        assertEquals(50.0f, rectangle.getX(), 0.0001f);
        assertEquals(100.0f, rectangle.getY(), 0.0001f);

        // 2nd animation: move delta (0, 50) from (50, 100) to (50, 150)
        timeline.update(0.5f);
        assertEquals(50.0f, rectangle.getX(), 0.0001f);
        assertEquals(125.0f, rectangle.getY(), 0.0001f);

        timeline.update(0.5f);
        assertEquals(50.0f, rectangle.getX(), 0.0001f);
        assertEquals(150.0f, rectangle.getY(), 0.0001f);

        // 3rd animation: move to (100, 200)
        timeline.update(1.0f);
        assertEquals(100.0f, rectangle.getX(), 0.0001f);
        assertEquals(200.0f, rectangle.getY(), 0.0001f);

        // 4th animation: move delta (10, 20) to (110, 220)
        timeline.update(1.0f);
        assertEquals(110.0f, rectangle.getX(), 0.0001f);
        assertEquals(220.0f, rectangle.getY(), 0.0001f);
    }

    @Test
    public void animateRectangleValidation() {
        assertThrows(IllegalArgumentException.class, () -> Animate.rectangle(null, 0, 0, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.rectangle(null, new Vector2f(0, 0), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.rectangle(new Rectangle(0, 0, 10, 10), (Vector2f) null, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.rectangleMove(null, 0, 0, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.rectangleMove(null, new Vector2f(0, 0), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.rectangleMove(new Rectangle(0, 0, 10, 10), (Vector2f) null, 1.0f));
    }

    @Test
    public void animateCurvePointsAndMove() {
        Curve curve = new Curve(new Vector2f(0, 0), new Vector2f(10, 10), new Vector2f(20, 20), new Vector2f(30, 30), 0.5f);
        AnimationTimeline timeline = new AnimationTimeline();

        timeline.record(() -> {
            Animate.curve(curve, new Vector2f(5, 5), new Vector2f(15, 25), new Vector2f(35, 45), new Vector2f(55, 65), 1.0f, Ease.Linear);
            Animate.curveP0(curve, new Vector2f(0, 0), 1.0f, Ease.Linear);
            Animate.curveP1(curve, 20.0f, 30.0f, 1.0f, Ease.Linear);
            Animate.curveP2(curve, new Vector2f(40, 50), 1.0f, Ease.Linear);
            Animate.curveP3(curve, 60.0f, 70.0f, 1.0f, Ease.Linear);
            Animate.curveMove(curve, 10.0f, -5.0f, 1.0f, Ease.Linear);
            Animate.move(curve, new Vector2f(-10.0f, 5.0f), 1.0f, Ease.Linear);
        });

        // 1st animation: animate all curve points to (5,5), (15,25), (35,45), (55,65)
        timeline.update(0.5f);
        assertEquals(new Vector2f(2.5f, 2.5f), curve.getP0());
        assertEquals(new Vector2f(12.5f, 17.5f), curve.getP1());
        assertEquals(new Vector2f(27.5f, 32.5f), curve.getP2());
        assertEquals(new Vector2f(42.5f, 47.5f), curve.getP3());

        timeline.update(0.5f);
        assertEquals(new Vector2f(5, 5), curve.getP0());
        assertEquals(new Vector2f(15, 25), curve.getP1());
        assertEquals(new Vector2f(35, 45), curve.getP2());
        assertEquals(new Vector2f(55, 65), curve.getP3());

        // 2nd animation: animate P0 to (0,0)
        timeline.update(1.0f);
        assertEquals(new Vector2f(0, 0), curve.getP0());

        // 3rd animation: animate P1 to (20,30)
        timeline.update(1.0f);
        assertEquals(new Vector2f(20, 30), curve.getP1());

        // 4th animation: animate P2 to (40,50)
        timeline.update(1.0f);
        assertEquals(new Vector2f(40, 50), curve.getP2());

        // 5th animation: animate P3 to (60,70)
        timeline.update(1.0f);
        assertEquals(new Vector2f(60, 70), curve.getP3());

        // 6th animation: curveMove by (10, -5)
        timeline.update(0.5f);
        assertEquals(new Vector2f(5, -2.5f), curve.getP0());
        assertEquals(new Vector2f(25, 27.5f), curve.getP1());
        assertEquals(new Vector2f(45, 47.5f), curve.getP2());
        assertEquals(new Vector2f(65, 67.5f), curve.getP3());

        timeline.update(0.5f);
        assertEquals(new Vector2f(10, -5), curve.getP0());
        assertEquals(new Vector2f(30, 25), curve.getP1());
        assertEquals(new Vector2f(50, 45), curve.getP2());
        assertEquals(new Vector2f(70, 65), curve.getP3());

        // 7th animation: Animate.move(curve) by (-10, 5)
        timeline.update(1.0f);
        assertEquals(new Vector2f(0, 0), curve.getP0());
        assertEquals(new Vector2f(20, 30), curve.getP1());
        assertEquals(new Vector2f(40, 50), curve.getP2());
        assertEquals(new Vector2f(60, 70), curve.getP3());
    }

    @Test
    public void animateCurveValidation() {
        Curve curve = new Curve(new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(2, 2), new Vector2f(3, 3), 0.5f);
        assertThrows(IllegalArgumentException.class, () -> Animate.curve(null, new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f(), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP0(null, new Vector2f(), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP0(curve, (Vector2f) null, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP1(null, new Vector2f(), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP1(curve, (Vector2f) null, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP2(null, new Vector2f(), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP2(curve, (Vector2f) null, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP3(null, new Vector2f(), 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveP3(curve, (Vector2f) null, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveMove(null, 0, 0, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> Animate.curveMove(curve, (Vector2f) null, 1.0f));

        assertThrows(IllegalArgumentException.class, () -> curve.setP0((Vector2f) null));
        assertThrows(IllegalArgumentException.class, () -> curve.setP1((Vector2f) null));
        assertThrows(IllegalArgumentException.class, () -> curve.setP2((Vector2f) null));
        assertThrows(IllegalArgumentException.class, () -> curve.setP3((Vector2f) null));
        assertThrows(IllegalArgumentException.class, () -> curve.move(null));
    }
}
