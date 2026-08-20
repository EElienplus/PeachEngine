package net.meowsers.Peach.Graphics;

import net.meowsers.Peach.Animation.Animate;
import net.meowsers.Peach.Animation.AnimationTimeline;
import net.meowsers.Peach.Animation.DrawAnimation;
import net.meowsers.Peach.Drawables.*;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.Enums.Colors;
import net.meowsers.Peach.Utils.Enums.Ease;
import net.meowsers.Peach.Utils.Enums.Fonts;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DrawTest {

    @Test
    public void testDrawAnimationLifecycle() {
        AtomicInteger renderCount = new AtomicInteger(0);
        DrawAnimation drawAnimation = new DrawAnimation(renderCount::incrementAndGet);

        assertFalse(drawAnimation.isFinished());
        drawAnimation.begin();
        assertTrue(drawAnimation.isFinished());

        drawAnimation.render();
        assertEquals(1, renderCount.get());

        drawAnimation.update(0.16f);
        assertTrue(drawAnimation.isFinished());

        drawAnimation.reset();
        assertFalse(drawAnimation.isFinished());

        assertThrows(IllegalArgumentException.class, () -> new DrawAnimation(null));
    }

    @Test
    public void testDrawMethodsRecordOnTimeline() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect = new Rectangle(10, 20, 30, 40);
        Circle circle = new Circle(new Vector2f(50, 50), 25);
        Line line = new Line(new Vector2f(0, 0), new Vector2f(100, 100));
        Triangle tri = new Triangle(new Vector2f(0, 0), new Vector2f(10, 0), new Vector2f(5, 10));
        Arrow arrow = new Arrow(new Line(new Vector2f(0, 0), new Vector2f(50, 50)));
        Curve curve = new Curve(new Vector2f(0, 0), new Vector2f(25, 50), new Vector2f(75, 50), new Vector2f(100, 0), 1.0f);
        Text text = new Text("Test", Fonts.Default, 20);
        Group group = new Group(new Rectangle(0, 0, 10, 10), new Circle(new Vector2f(20, 20), 5));

        timeline.record(() -> {
            Draw.rectangle(rect, Colors.Red);
            Draw.circle(circle, Colors.Blue);
            Draw.line(line, Colors.Green);
            Draw.triangle(tri, Colors.Yellow);
            Draw.arrow(arrow, 4, Colors.Pink);
            Draw.curve(curve, Colors.Orange);
            Draw.text(text, 100, 200, Colors.White);
            Draw.group(group, Colors.SkyBlue);
        });

        // The timeline update should process all 8 instant Draw calls in the first frame
        timeline.update(0.016f);

        // Rendering should run smoothly without exceptions even in headless environment
        assertDoesNotThrow(timeline::render);
        assertDoesNotThrow(timeline::render);
    }

    @Test
    public void testDrawPersistsAcrossTimelineFrames() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect = new Rectangle(0, 0, 50, 50);
        AtomicInteger renderedFrames = new AtomicInteger(0);

        timeline.record(() -> {
            Draw.rectangle(rect, Colors.Magenta);
            Animate.custom(1.0f, Ease.Linear, (p) -> renderedFrames.incrementAndGet());
        });

        // Frame 1
        timeline.update(0.5f);
        timeline.render();
        assertEquals(1, renderedFrames.get());

        // Frame 2
        timeline.update(0.5f);
        timeline.render();
        assertEquals(2, renderedFrames.get());

        // Frame 3 (after custom animation finishes, Draw should still be rendered)
        timeline.update(0.5f);
        timeline.render();
        // custom animation has finished, but timeline.render() still runs completed animations without error
        assertDoesNotThrow(timeline::render);
    }

    @Test
    public void testDrawFollowsAnimateTransformations() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect = new Rectangle(0, 0, 50, 50);

        timeline.record(() -> {
            Draw.rectangle(rect, Colors.Magenta);
            Animate.rectangle(rect, 100, 200, 50, 50, 1.0f, Ease.Linear);
        });

        timeline.update(0.5f);
        assertEquals(50.0f, rect.getX(), 0.001f);
        assertEquals(100.0f, rect.getY(), 0.001f);

        timeline.update(0.5f);
        assertEquals(100.0f, rect.getX(), 0.001f);
        assertEquals(200.0f, rect.getY(), 0.001f);
    }

    @Test
    public void testDrawInGroupedAnimations() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect1 = new Rectangle(0, 0, 50, 50);
        Rectangle rect2 = new Rectangle(100, 100, 50, 50);

        timeline.record(() -> {
            timeline.recordTogether(() -> {
                Draw.rectangle(rect1, Colors.Red);
                Draw.rectangle(rect2, Colors.Blue);
            });
        });

        timeline.update(0.016f);
        assertDoesNotThrow(timeline::render);
    }

    @Test
    public void testDrawWithWaitSequence() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect1 = new Rectangle(0, 0, 50, 50);
        Rectangle rect2 = new Rectangle(100, 100, 50, 50);

        timeline.record(() -> {
            Draw.rectangle(rect1, Colors.Red);
            timeline.recordWait(1.0f);
            Draw.rectangle(rect2, Colors.Blue);
        });

        // Start: rect1 should be active, wait begins
        timeline.update(0.5f);
        assertDoesNotThrow(timeline::render);

        // Complete wait
        timeline.update(0.5f);
        assertDoesNotThrow(timeline::render);

        // Next update: rect2 is active
        timeline.update(0.1f);
        assertDoesNotThrow(timeline::render);
    }

    @Test
    public void testNullSafetyInDrawMethods() {
        assertDoesNotThrow(() -> Draw.rectangle(null, (Colors) null));
        assertDoesNotThrow(() -> Draw.rectangle(null, (Color) null));
        assertDoesNotThrow(() -> Draw.circle(null, (Colors) null));
        assertDoesNotThrow(() -> Draw.circle(null, (Color) null));
        assertDoesNotThrow(() -> Draw.triangle(null, (Colors) null));
        assertDoesNotThrow(() -> Draw.triangle(null, (Color) null));
        assertDoesNotThrow(() -> Draw.line(null, (Colors) null));
        assertDoesNotThrow(() -> Draw.line(null, (Color) null));
        assertDoesNotThrow(() -> Draw.arrow(null, 2, (Colors) null));
        assertDoesNotThrow(() -> Draw.arrow(null, 2, (Color) null));
        assertDoesNotThrow(() -> Draw.curve(null, (Colors) null));
        assertDoesNotThrow(() -> Draw.curve(null, (Color) null));
        assertDoesNotThrow(() -> Draw.text(null, 0, 0, (Colors) null));
        assertDoesNotThrow(() -> Draw.text(null, 0, 0, (Color) null));
        assertDoesNotThrow(() -> Draw.text(null, (Colors) null));
        assertDoesNotThrow(() -> Draw.text(null, (Color) null));
        assertDoesNotThrow(() -> Draw.image(null, (Rectangle) null));
        assertDoesNotThrow(() -> Draw.image(null, 0, 0));

        assertThrows(IllegalArgumentException.class, () -> Draw.group(null, (Colors) null));
        assertThrows(IllegalArgumentException.class, () -> Draw.group(null, (Color) null));
    }
}
