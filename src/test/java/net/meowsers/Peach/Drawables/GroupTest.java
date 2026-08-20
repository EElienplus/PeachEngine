package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Animation.Animate;
import net.meowsers.Peach.Animation.AnimationTimeline;
import net.meowsers.Peach.Utils.Enums.Ease;
import net.meowsers.Peach.Utils.Enums.Fonts;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {

    @Test
    public void testGroupConstructionAndItemManagement() {
        Rectangle rect = new Rectangle(100, 200, 50, 50);
        Circle circle = new Circle(new Vector2f(300, 400), 25);
        Text text = new Text("Test", Fonts.Default, 16);
        text.setPosition(50, 80);

        Group group = new Group(rect, circle);
        assertEquals(2, group.size());
        assertFalse(group.isEmpty());
        assertTrue(group.contains(rect));
        assertTrue(group.contains(circle));
        assertFalse(group.contains(text));

        group.add(text);
        assertEquals(3, group.size());
        assertTrue(group.contains(text));
        assertEquals(rect, group.get(0));
        assertEquals(circle, group.get(1));
        assertEquals(text, group.get(2));

        List<Rectangle> rectangles = group.getItems(Rectangle.class);
        assertEquals(1, rectangles.size());
        assertEquals(rect, rectangles.get(0));

        group.remove(circle);
        assertEquals(2, group.size());
        assertFalse(group.contains(circle));

        group.clear();
        assertEquals(0, group.size());
        assertTrue(group.isEmpty());
    }

    @Test
    public void testGroupValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Group((Object[]) null));
        assertThrows(IllegalArgumentException.class, () -> new Group((Rectangle) null));
        assertThrows(IllegalArgumentException.class, () -> new Group((Vector2f) null, new Rectangle(0, 0, 10, 10)));

        Group group = new Group();
        assertThrows(IllegalArgumentException.class, () -> group.add((Object) null));
        assertThrows(IllegalArgumentException.class, () -> group.add(group));
        assertThrows(IllegalArgumentException.class, () -> group.move(null));
        assertThrows(IllegalArgumentException.class, () -> group.setPosition(null));
        assertThrows(IllegalArgumentException.class, () -> group.getItems(null));
    }

    @Test
    public void testMoveAffectsAllContainedDrawables() {
        Rectangle rect = new Rectangle(100, 200, 50, 60);
        Circle circle = new Circle(new Vector2f(300, 400), 20);
        Triangle triangle = new Triangle(new Vector2f(10, 20), new Vector2f(30, 40), new Vector2f(50, 60));
        Line line = new Line(new Vector2f(100, 100), new Vector2f(200, 200));
        Arrow arrow = new Arrow(new Line(new Vector2f(150, 150), new Vector2f(250, 250)));
        Curve curve = new Curve(new Vector2f(0, 0), new Vector2f(10, 20), new Vector2f(30, 40), new Vector2f(50, 60), 0.5f);
        Text text = new Text("Hello", Fonts.Default, 20);
        text.setPosition(50, 70);
        Vector2f vec = new Vector2f(5, 5);

        Group group = new Group(rect, circle, triangle, line, arrow, curve, text, vec);

        group.move(40, -30);

        // Rectangle
        assertEquals(140.0f, rect.getX(), 0.0001f);
        assertEquals(170.0f, rect.getY(), 0.0001f);
        assertEquals(50.0f, rect.getW(), 0.0001f);
        assertEquals(60.0f, rect.getH(), 0.0001f);

        // Circle
        assertEquals(340.0f, circle.getCenter().x, 0.0001f);
        assertEquals(370.0f, circle.getCenter().y, 0.0001f);
        assertEquals(20, circle.getRadius());

        // Triangle
        assertEquals(new Vector2f(50, -10), triangle.getPointA());
        assertEquals(new Vector2f(70, 10), triangle.getPointB());
        assertEquals(new Vector2f(90, 30), triangle.getPointC());

        // Line
        assertEquals(new Vector2f(140, 70), line.getStartPos());
        assertEquals(new Vector2f(240, 170), line.getEndPos());

        // Arrow
        assertEquals(new Vector2f(190, 120), arrow.getLine().getStartPos());
        assertEquals(new Vector2f(290, 220), arrow.getLine().getEndPos());

        // Curve
        assertEquals(new Vector2f(40, -30), curve.getP0());
        assertEquals(new Vector2f(50, -10), curve.getP1());
        assertEquals(new Vector2f(70, 10), curve.getP2());
        assertEquals(new Vector2f(90, 30), curve.getP3());

        // Text
        assertEquals(new Vector2f(90, 40), text.getPosition());

        // Vector2f
        assertEquals(new Vector2f(45, -25), vec);
    }

    @Test
    public void testSetPositionAndTranslate() {
        Rectangle rect1 = new Rectangle(100, 100, 50, 50);
        Rectangle rect2 = new Rectangle(200, 200, 50, 50);
        Group group = new Group(rect1, rect2);

        assertEquals(100.0f, group.getX(), 0.0001f);
        assertEquals(100.0f, group.getY(), 0.0001f);

        group.setPosition(300, 400);

        assertEquals(300.0f, group.getX(), 0.0001f);
        assertEquals(400.0f, group.getY(), 0.0001f);
        assertEquals(300.0f, rect1.getX(), 0.0001f);
        assertEquals(400.0f, rect1.getY(), 0.0001f);
        assertEquals(400.0f, rect2.getX(), 0.0001f);
        assertEquals(500.0f, rect2.getY(), 0.0001f);

        group.setX(350);
        assertEquals(350.0f, rect1.getX(), 0.0001f);
        assertEquals(450.0f, rect2.getX(), 0.0001f);

        group.setY(450);
        assertEquals(450.0f, rect1.getY(), 0.0001f);
        assertEquals(550.0f, rect2.getY(), 0.0001f);
    }

    @Test
    public void testNestedGroups() {
        Rectangle rect1 = new Rectangle(10, 10, 20, 20);
        Rectangle rect2 = new Rectangle(50, 50, 20, 20);
        Group subGroup = new Group(rect1);
        Group parentGroup = new Group(subGroup, rect2);

        parentGroup.move(100, 200);

        assertEquals(110.0f, rect1.getX(), 0.0001f);
        assertEquals(210.0f, rect1.getY(), 0.0001f);
        assertEquals(150.0f, rect2.getX(), 0.0001f);
        assertEquals(250.0f, rect2.getY(), 0.0001f);

        subGroup.move(5, 5);
        assertEquals(115.0f, rect1.getX(), 0.0001f);
        assertEquals(215.0f, rect1.getY(), 0.0001f);
        assertEquals(150.0f, rect2.getX(), 0.0001f);
        assertEquals(250.0f, rect2.getY(), 0.0001f);
    }

    @Test
    public void testBoundingBoxAndCenter() {
        Rectangle rect1 = new Rectangle(100, 100, 50, 50);
        Rectangle rect2 = new Rectangle(200, 300, 100, 50);
        Group group = new Group(rect1, rect2);

        assertEquals(new Vector2f(100, 100), group.getMin());
        assertEquals(new Vector2f(300, 350), group.getMax());
        assertEquals(200.0f, group.getWidth(), 0.0001f);
        assertEquals(250.0f, group.getHeight(), 0.0001f);
        assertEquals(new Vector2f(200, 225), group.getCenter());

        Rectangle bounds = group.getBoundingBox();
        assertEquals(100.0f, bounds.getX(), 0.0001f);
        assertEquals(100.0f, bounds.getY(), 0.0001f);
        assertEquals(200.0f, bounds.getW(), 0.0001f);
        assertEquals(250.0f, bounds.getH(), 0.0001f);
    }

    @Test
    public void testAnimateGroupInterpolatesAndAffectsAllItems() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect1 = new Rectangle(100, 100, 50, 50);
        Rectangle rect2 = new Rectangle(200, 200, 50, 50);
        Group group = new Group(rect1, rect2);

        timeline.record(() -> {
            Animate.group(group, 300, 300, 1.0f, Ease.Linear);
        });

        timeline.update(0.5f);
        assertEquals(200.0f, group.getX(), 0.0001f);
        assertEquals(200.0f, group.getY(), 0.0001f);
        assertEquals(200.0f, rect1.getX(), 0.0001f);
        assertEquals(200.0f, rect1.getY(), 0.0001f);
        assertEquals(300.0f, rect2.getX(), 0.0001f);
        assertEquals(300.0f, rect2.getY(), 0.0001f);

        timeline.update(0.5f);
        assertEquals(300.0f, group.getX(), 0.0001f);
        assertEquals(300.0f, group.getY(), 0.0001f);
        assertEquals(300.0f, rect1.getX(), 0.0001f);
        assertEquals(300.0f, rect1.getY(), 0.0001f);
        assertEquals(400.0f, rect2.getX(), 0.0001f);
        assertEquals(400.0f, rect2.getY(), 0.0001f);
    }

    @Test
    public void testSequentialGroupAnimations() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect = new Rectangle(100, 100, 50, 50);
        Group group = new Group(rect);

        timeline.record(() -> {
            Animate.group(group, 200, 200, 1.0f, Ease.Linear);
            Animate.groupMove(group, 50, 100, 1.0f, Ease.Linear);
        });

        // 1st animation
        timeline.update(1.0f);
        assertEquals(200.0f, group.getX(), 0.0001f);
        assertEquals(200.0f, group.getY(), 0.0001f);
        assertEquals(200.0f, rect.getX(), 0.0001f);
        assertEquals(200.0f, rect.getY(), 0.0001f);

        // 2nd animation halfway
        timeline.update(0.5f);
        assertEquals(225.0f, group.getX(), 0.0001f);
        assertEquals(250.0f, group.getY(), 0.0001f);
        assertEquals(225.0f, rect.getX(), 0.0001f);
        assertEquals(250.0f, rect.getY(), 0.0001f);

        // 2nd animation complete
        timeline.update(0.5f);
        assertEquals(250.0f, group.getX(), 0.0001f);
        assertEquals(300.0f, group.getY(), 0.0001f);
        assertEquals(250.0f, rect.getX(), 0.0001f);
        assertEquals(300.0f, rect.getY(), 0.0001f);
    }

    @Test
    public void testAnimateMoveGeneric() {
        AnimationTimeline timeline = new AnimationTimeline();
        Rectangle rect = new Rectangle(50, 50, 20, 20);

        timeline.record(() -> {
            Animate.move(rect, 100, 200, 1.0f, Ease.Linear);
        });

        timeline.update(0.5f);
        assertEquals(100.0f, rect.getX(), 0.0001f);
        assertEquals(150.0f, rect.getY(), 0.0001f);

        timeline.update(0.5f);
        assertEquals(150.0f, rect.getX(), 0.0001f);
        assertEquals(250.0f, rect.getY(), 0.0001f);
    }

    @Test
    public void testMoveGroupUpwardsOutOfScreen() {
        AnimationTimeline timeline = new AnimationTimeline();
        Text text = new Text("Test", Fonts.Default, 20);
        text.setPosition(100, 50);
        Rectangle rect1 = new Rectangle(100, 350, 75, 75);
        Rectangle rect2 = new Rectangle(325, 350, 50, 50);
        Circle circle = new Circle(new Vector2f(200, 150), 20);
        net.meowsers.Peach.Utils.LiveVector2f start = new net.meowsers.Peach.Utils.LiveVector2f(() -> new Vector2f(rect1.getX() + rect1.getW() + 20, rect1.getY() + rect1.getH() / 2));
        net.meowsers.Peach.Utils.LiveVector2f end = new net.meowsers.Peach.Utils.LiveVector2f(() -> new Vector2f(rect2.getX() - 20, rect2.getY() + rect2.getH() / 2));
        Arrow arrow = new Arrow(new Line(start, end));

        Group group = new Group(text, rect1, rect2, circle, arrow);

        timeline.record(() -> {
            timeline.recordWait(1.0f);
            Animate.groupMove(group, 0, -600, 1.0f, Ease.Linear);
        });

        // wait 1 second
        timeline.update(1.0f);
        assertEquals(50.0f, text.getPosition().y, 0.0001f);
        assertEquals(350.0f, rect1.getY(), 0.0001f);
        assertEquals(350.0f, rect2.getY(), 0.0001f);
        assertEquals(150.0f, circle.getCenter().y, 0.0001f);

        // move halfway
        timeline.update(0.5f);
        assertEquals(-250.0f, text.getPosition().y, 0.0001f);
        assertEquals(50.0f, rect1.getY(), 0.0001f);
        assertEquals(50.0f, rect2.getY(), 0.0001f);
        assertEquals(-150.0f, circle.getCenter().y, 0.0001f);

        // move complete
        timeline.update(0.5f);
        assertEquals(-550.0f, text.getPosition().y, 0.0001f);
        assertEquals(-250.0f, rect1.getY(), 0.0001f);
        assertEquals(-250.0f, rect2.getY(), 0.0001f);
        assertEquals(-450.0f, circle.getCenter().y, 0.0001f);

        // Arrow endpoints dynamically track rect1 and rect2
        assertEquals(new Vector2f(rect1.getX() + rect1.getW() + 20, rect1.getY() + rect1.getH() / 2), arrow.getLine().getStartPos());
        assertEquals(new Vector2f(rect2.getX() - 20, rect2.getY() + rect2.getH() / 2), arrow.getLine().getEndPos());
    }
}
