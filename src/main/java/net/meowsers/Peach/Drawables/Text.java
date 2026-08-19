package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import net.meowsers.Peach.Utils.Enums.Fonts;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Text {
    private String string;
    private String fontPath;
    private int fontSize = 48;
    private Vector2f position = new Vector2f();
    private static final int BAKED_SIZE = 128; // Bake high-resolution glyphs

    private static final Map<String, FontData> fontCache = new HashMap<>();


    public static class Glyph {
        public float u0, v0, u1, v1;
        public int width, height;
        public int bearingX, bearingY;
        public int advance;
    }

    public static class FontData {
        public Texture atlasTexture;
        public Map<Character, Glyph> glyphs = new HashMap<>();
    }

    public Text(String text, String fontPath) {
        this.string = text;
        this.fontPath = fontPath;
    }

    public Text(String text, Fonts font) {
        this.string = text;
        this.fontPath = font.path();
    }

    public Text(String text, String fontPath, int fontSize) {
        this.string = text;
        this.fontPath = fontPath;
        this.fontSize = fontSize;
    }

    public Text(String text, Fonts font, int fontSize) {
        this.string = text;
        this.fontPath = font.path();
        this.fontSize = fontSize;
    }

    private static void loadFontIfNeeded(String path) {
        if (fontCache.containsKey(path)) return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pLibrary = stack.mallocPointer(1);
            if (FreeType.FT_Init_FreeType(pLibrary) != 0) {
                throw new RuntimeException("Could not init FreeType Library");
            }
            long library = pLibrary.get(0);

            PointerBuffer pFace = stack.mallocPointer(1);
            if (FreeType.FT_New_Face(library, path, 0, pFace) != 0) {
                throw new RuntimeException("Failed to load font at: " + path);
            }

            FT_Face face = FT_Face.create(pFace.get(0));
            // Rasterize FreeType face at 128px high-res target
            FreeType.FT_Set_Pixel_Sizes(face, 0, BAKED_SIZE);

            FT_GlyphSlot slot = face.glyph();

            int atlasWidth = 2048; // Expanded atlas size for 128px glyphs
            int atlasHeight = 2048;
            ByteBuffer atlasBuffer = BufferUtils.createByteBuffer(atlasWidth * atlasHeight * 4);

            FontData fontData = new FontData();

            int padding = 4;
            int currentX = padding;
            int currentY = padding;
            int maxRowHeight = 0;

            for (char c = 32; c < 128; c++) {
                if (FreeType.FT_Load_Char(face, c, FreeType.FT_LOAD_RENDER) != 0) {
                    continue;
                }

                FT_Bitmap bitmap = slot.bitmap();
                int w = bitmap.width();
                int h = bitmap.rows();
                int pitch = Math.abs(bitmap.pitch());

                if (currentX + w + padding >= atlasWidth) {
                    currentX = padding;
                    currentY += maxRowHeight + padding;
                    maxRowHeight = 0;
                }

                if (w > 0 && h > 0) {
                    ByteBuffer srcBuffer = bitmap.buffer(pitch * h);
                    for (int row = 0; row < h; row++) {
                        for (int col = 0; col < w; col++) {
                            byte val = srcBuffer.get(row * pitch + col);
                            int atlasIndex = ((currentY + row) * atlasWidth + (currentX + col)) * 4;

                            atlasBuffer.put(atlasIndex, (byte) 255);
                            atlasBuffer.put(atlasIndex + 1, (byte) 255);
                            atlasBuffer.put(atlasIndex + 2, (byte) 255);
                            atlasBuffer.put(atlasIndex + 3, val);
                        }
                    }
                }

                int advance = (int) (slot.metrics().horiAdvance() >> 6);
                if (advance <= 0) {
                    advance = (w > 0) ? w + 2 : (BAKED_SIZE / 3);
                }

                Glyph glyph = new Glyph();
                glyph.u0 = (float) currentX / atlasWidth;
                glyph.v0 = (float) currentY / atlasHeight;
                glyph.u1 = (float) (currentX + w) / atlasWidth;
                glyph.v1 = (float) (currentY + h) / atlasHeight;
                glyph.width = w;
                glyph.height = h;
                glyph.bearingX = slot.bitmap_left();
                glyph.bearingY = slot.bitmap_top();
                glyph.advance = advance;

                fontData.glyphs.put(c, glyph);

                currentX += w + padding;
                if (h > maxRowHeight) {
                    maxRowHeight = h;
                }
            }

            atlasBuffer.position(0);
            fontData.atlasTexture = new Texture(atlasBuffer, atlasWidth, atlasHeight, GL_RGBA);

            // GL_LINEAR provides smooth anti-aliased edge sampling
            glBindTexture(GL_TEXTURE_2D, fontData.atlasTexture.getTextureID());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            FreeType.FT_Done_Face(face);
            FreeType.FT_Done_FreeType(library);

            fontCache.put(path, fontData);
        }
    }

    public FontData getFontData() {
        loadFontIfNeeded(fontPath);
        return fontCache.get(fontPath);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getFontPath() {
        return fontPath;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getBakedSize() {
        return BAKED_SIZE;
    }

    public Vector2f getPosition() {
        return LiveVector2f.resolve(position);
    }

    public void setPosition(Vector2f position) {
        if (position == null) {
            throw new IllegalArgumentException("Text position can't be null.");
        }

        this.position.set(position);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }
}