package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.Enums.Fonts;
import net.meowsers.Peach.Window;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextTest {

    @Test
    public void getCenteredXThrowsOnNullWindowOrText() {
        Text text = new Text("Hello", Fonts.Default, 24);

        assertThrows(IllegalArgumentException.class, () -> Text.getCenteredX(null, text));
        assertThrows(IllegalArgumentException.class, () -> Text.getCenteredX(null, text, "Custom"));
        assertThrows(IllegalArgumentException.class, () -> Text.getCenteredX(null, null));
        assertThrows(IllegalArgumentException.class, () -> Text.getCenteredX(null, "Hello", Fonts.Default, 24));
        assertThrows(IllegalArgumentException.class, () -> Text.getCenteredX(null, "Hello", "fontPath", 24));
    }

    @Test
    public void getWidthReturnsZeroOnNullOrEmptyString() {
        Text textNull = new Text(null, Fonts.Default, 24);
        assertEquals(0.0f, textNull.getWidth(), 0.0001f);
        assertEquals(0.0f, textNull.getWidth(null), 0.0001f);
        assertEquals(0.0f, textNull.getWidth(""), 0.0001f);

        Text textEmpty = new Text("", Fonts.Default, 24);
        assertEquals(0.0f, textEmpty.getWidth(), 0.0001f);
        assertEquals(0.0f, textEmpty.getWidth(null), 0.0001f);
        assertEquals(0.0f, textEmpty.getWidth(""), 0.0001f);
    }
}
