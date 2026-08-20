package net.meowsers.Peach.Utils;

import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathUtilsTest {

    @Test
    public void testLerp() {
        assertEquals(10.0f, MathUtils.lerp(10.0f, 20.0f, 0.0f), 0.0001f);
        assertEquals(15.0f, MathUtils.lerp(10.0f, 20.0f, 0.5f), 0.0001f);
        assertEquals(20.0f, MathUtils.lerp(10.0f, 20.0f, 1.0f), 0.0001f);
    }

    @Test
    public void testAdd() {
        Vector2f a = new Vector2f(10, 20);
        Vector2f b = new Vector2f(5, 15);
        Vector2f result = MathUtils.add(a, b);

        assertEquals(15.0f, result.x, 0.0001f);
        assertEquals(35.0f, result.y, 0.0001f);
    }

    @Test
    public void testSub() {
        Vector2f a = new Vector2f(10, 20);
        Vector2f b = new Vector2f(5, 15);
        Vector2f result = MathUtils.sub(a, b);

        assertEquals(5.0f, result.x, 0.0001f);
        assertEquals(5.0f, result.y, 0.0001f);
    }

    @Test
    public void testAddAndSubNullValidation() {
        Vector2f v = new Vector2f(1, 2);

        assertThrows(IllegalArgumentException.class, () -> MathUtils.add(null, v));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.add(v, null));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.sub(null, v));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.sub(v, null));
    }
}
