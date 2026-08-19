package net.meowsers.Peach.Graphics;

import net.meowsers.Peach.Drawables.Text;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.LiveVector2f;
import net.meowsers.Peach.Utils.Enums.Colors;
import net.meowsers.Peach.Utils.Enums.Fonts;
import org.joml.Vector2f;

import java.util.Arrays;

public class Visualize {

    private static Renderer renderer;

    public Visualize(Renderer renderer) {
        Visualize.renderer = renderer;
    }

    // Ik all this will produce GC overhead and it's not the best for all of this but I can't really be bothered
    // I'll fix it up when I feel like it lol

    public static Text _int(int n, float x, float y, int fontSize) {
        return createText(Integer.toString(n), x, y, fontSize);
    }

    public static Text _int(int n, float x, float y, int fontSize, Color color) {
        return drawText(_int(n, x, y, fontSize), color);
    }

    public static Text _int(int n, float x, float y, int fontSize, Colors color) {
        return _int(n, x, y, fontSize, color.getColor());
    }

    public static Text _float(float n, float x, float y, int fontSize) {
        return createText(Float.toString(n), x, y, fontSize);
    }

    public static Text _float(float n, float x, float y, int fontSize, Color color) {
        return drawText(_float(n, x, y, fontSize), color);
    }

    public static Text _float(float n, float x, float y, int fontSize, Colors color) {
        return _float(n, x, y, fontSize, color.getColor());
    }

    public static Text _bool(boolean value, float x, float y, int fontSize) {
        return createText(Boolean.toString(value), x, y, fontSize);
    }

    public static Text _bool(boolean value, float x, float y, int fontSize, Color color) {
        return drawText(_bool(value, x, y, fontSize), color);
    }

    public static Text _bool(boolean value, float x, float y, int fontSize, Colors color) {
        return _bool(value, x, y, fontSize, color.getColor());
    }

    public static Text _intArr(int[] arr, float x, float y, int fontSize) {
        return createText(Arrays.toString(arr), x, y, fontSize);
    }

    public static Text _intArr(int[] arr, float x, float y, int fontSize, Color color) {
        return drawText(_intArr(arr, x, y, fontSize), color);
    }

    public static Text _intArr(int[] arr, float x, float y, int fontSize, Colors color) {
        return _intArr(arr, x, y, fontSize, color.getColor());
    }

    public static Text _floatArr(float[] arr, float x, float y, int fontSize) {
        return createText(Arrays.toString(arr), x, y, fontSize);
    }

    public static Text _floatArr(float[] arr, float x, float y, int fontSize, Color color) {
        return drawText(_floatArr(arr, x, y, fontSize), color);
    }

    public static Text _floatArr(float[] arr, float x, float y, int fontSize, Colors color) {
        return _floatArr(arr, x, y, fontSize, color.getColor());
    }

    public static Text _boolArr(boolean[] arr, float x, float y, int fontSize) {
        return createText(Arrays.toString(arr), x, y, fontSize);
    }

    public static Text _boolArr(boolean[] arr, float x, float y, int fontSize, Color color) {
        return drawText(_boolArr(arr, x, y, fontSize), color);
    }

    public static Text _boolArr(boolean[] arr, float x, float y, int fontSize, Colors color) {
        return _boolArr(arr, x, y, fontSize, color.getColor());
    }

    public static Text _stringArr(String[] arr, float x, float y, int fontSize) {
        return createText(Arrays.toString(arr), x, y, fontSize);
    }

    public static Text _stringArr(String[] arr, float x, float y, int fontSize, Color color) {
        return drawText(_stringArr(arr, x, y, fontSize), color);
    }

    public static Text _stringArr(String[] arr, float x, float y, int fontSize, Colors color) {
        return _stringArr(arr, x, y, fontSize, color.getColor());
    }

    public static <T> Text _vec2(T a, T b, float x, float y, int fontSize) {
        return createText("(" + a + ", " + b + ")", x, y, fontSize);
    }

    public static <T> Text _vec2(T a, T b, float x, float y, int fontSize, Color color) {
        return drawText(_vec2(a, b, x, y, fontSize), color);
    }

    public static <T> Text _vec2(T a, T b, float x, float y, int fontSize, Colors color) {
        return _vec2(a, b, x, y, fontSize, color.getColor());
    }

    public static <T> Text _vec3(T a, T b, T c, float x, float y, int fontSize) {
        return createText("(" + a + ", " + b + ", " + c + ")", x, y, fontSize);
    }

    public static <T> Text _vec3(T a, T b, T c, float x, float y, int fontSize, Color color) {
        return drawText(_vec3(a, b, c, x, y, fontSize), color);
    }

    public static <T> Text _vec3(T a, T b, T c, float x, float y, int fontSize, Colors color) {
        return _vec3(a, b, c, x, y, fontSize, color.getColor());
    }

    public static <T> Text _vec4(T a, T b, T c, T d, float x, float y, int fontSize) {
        return createText("(" + a + ", " + b + ", " + c + ", " + d + ")", x, y, fontSize);
    }

    public static <T> Text _vec4(T a, T b, T c, T d, float x, float y, int fontSize, Color color) {
        return drawText(_vec4(a, b, c, d, x, y, fontSize), color);
    }

    public static <T> Text _vec4(T a, T b, T c, T d, float x, float y, int fontSize, Colors color) {
        return _vec4(a, b, c, d, x, y, fontSize, color.getColor());
    }

    private static Text createText(String string, float x, float y, int fontSize) {
        Text text = new Text(string, Fonts.Default, fontSize);
        text.setPosition(x, y);
        return text;
    }

    private static Text drawText(Text text, Color color) {
        Vector2f position = text.getPosition();
        Draw.text(text, position.x, position.y, color);
        return text;
    }

    public static Vector2f getCharPosition(Text text, float startX, float startY, int targetIndex) {
        if (text == null) {
            throw new IllegalArgumentException("Text can't be null.");
        }

        Vector2f textPosition = text.getPosition();
        float xOffset = startX - textPosition.x;
        float yOffset = startY - textPosition.y;

        return new LiveVector2f(() -> {
            Vector2f currentPosition = text.getPosition();
            return calculateCharPosition(text, currentPosition.x + xOffset, currentPosition.y + yOffset, targetIndex);
        });
    }

    private static Vector2f calculateCharPosition(Text text, float startX, float startY, int targetIndex) {
        String renderedString = text.getString();

        if (targetIndex < 0 || targetIndex >= renderedString.length()) {
            return new Vector2f(startX, startY);
        }

        Text.FontData fontData = text.getFontData();
        if (fontData == null) return new Vector2f(startX, startY);

        float scale = (float) text.getFontSize() / text.getBakedSize();
        float cursorX = startX;

        for (int i = 0; i < targetIndex; i++) {
            char c = renderedString.charAt(i);
            Text.Glyph glyph = fontData.glyphs.get(c);
            if (glyph != null) {
                cursorX += (glyph.advance * scale);
            }
        }

        // Add bearing for exact visual left-edge bounding box
        Text.Glyph targetGlyph = fontData.glyphs.get(renderedString.charAt(targetIndex));
        if (targetGlyph != null) {
            cursorX += (targetGlyph.bearingX * scale);
        }

        return new Vector2f(cursorX, startY);
    }
}