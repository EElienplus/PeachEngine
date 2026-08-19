package net.meowsers.Peach.Graphics;

import net.meowsers.Peach.Drawables.*;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.Enums.Colors;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

public class Draw {
    private static Renderer renderer;

    // Pre-allocate static buffers
    private static final int[] QUAD_INDICES = {0, 1, 2, 2, 3, 0};
    private static final float[] QUAD_VERTS = new float[4 * RenderBatch.INPUT_VERTEX_SIZE];
    private static final float[] TRI_VERTS = new float[3 * RenderBatch.INPUT_VERTEX_SIZE];

    // Pre-calculated circle values
    private static final int CIRCLE_SEGMENTS = 64;
    private static final float[] CIRCLE_COS = new float[CIRCLE_SEGMENTS];
    private static final float[] CIRCLE_SIN = new float[CIRCLE_SEGMENTS];
    private static final int[] CIRCLE_INDICES = new int[CIRCLE_SEGMENTS * 3];
    private static final float[] CIRCLE_VERTS = new float[(CIRCLE_SEGMENTS + 1) * RenderBatch.INPUT_VERTEX_SIZE];

    private static float[] DYNAMIC_VERTS = new float[2048];
    private static int[] DYNAMIC_INDICES = new int[3072];

    private static void ensureDynamicCapacity(int requiredVerts, int requiredIndices) {
        if (DYNAMIC_VERTS.length < requiredVerts) {
            DYNAMIC_VERTS = new float[requiredVerts * 2];
        }
        if (DYNAMIC_INDICES.length < requiredIndices) {
            DYNAMIC_INDICES = new int[requiredIndices * 2];
        }
    }

    static {
        // Pre-calculate unit circle when engine starts
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            float theta = (float) (2.0f * Math.PI * i / CIRCLE_SEGMENTS);
            CIRCLE_COS[i] = (float) Math.cos(theta);
            CIRCLE_SIN[i] = (float) Math.sin(theta);
        }
        // Pre-calculate circle fan indices
        int idx = 0;
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            CIRCLE_INDICES[idx++] = 0;
            CIRCLE_INDICES[idx++] = i + 1;
            CIRCLE_INDICES[idx++] = (i + 1) % CIRCLE_SEGMENTS + 1;
        }
    }

    public Draw(Renderer renderer) {
        Draw.renderer = renderer;
    }


    public static void rectangle(Rectangle rectangle, Colors color) {
        rectangle(rectangle, color.getColor());
    }
    public static void rectangle(Rectangle rectangle, Color color) {
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();
        float x = rectangle.getX(), y = rectangle.getY(), w = rectangle.getW(), h = rectangle.getH();

        setQuadVerts(
                x, y,          r, g, b, a, 0f, 0f,
                x, y + h,      r, g, b, a, 0f, 0f,
                x + w, y + h,  r, g, b, a, 0f, 0f,
                x + w, y,      r, g, b, a, 0f, 0f
        );

        renderer.submit(QUAD_VERTS, QUAD_INDICES, 0, 0);
    }

    public static void image(Texture texture, Rectangle rectangle) {
        float x = rectangle.getX(), y = rectangle.getY(), w = rectangle.getW(), h = rectangle.getH();

        setQuadVerts(
                x, y,          1f, 1f, 1f, 1f, 0f, 1f,
                x, y + h,      1f, 1f, 1f, 1f, 0f, 0f,
                x + w, y + h,  1f, 1f, 1f, 1f, 1f, 0f,
                x + w, y,      1f, 1f, 1f, 1f, 1f, 1f
        );

        renderer.submit(QUAD_VERTS, QUAD_INDICES, texture.getTextureID(), 0);
    }
    public static void image(Texture texture, float x, float y, float scale) {
        image(texture, new Rectangle(x, y, texture.getWidth() * scale, texture.getHeight() * scale));
    }
    public static void image(Texture texture, float x, float y) {
        image(texture, x, y, 1.f);
    }

    public static void text(Text text, float x, float y, Colors color) {
        text(text, x, y, color.getColor());
    }
    public static void text(Text text, float x, float y, Color color) {
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();
        Text.FontData fontData = text.getFontData();

        if (fontData == null || fontData.atlasTexture == null) return;

        // Scale 128px high-res glyphs down to requested font size
        float scale = (float) text.getFontSize() / text.getBakedSize();
        float cursorX = x;

        for (char c : text.getString().toCharArray()) {
            Text.Glyph glyph = fontData.glyphs.get(c);
            if (glyph == null) continue;

            float xPos = cursorX + (glyph.bearingX * scale);
            float yPos = y - (glyph.bearingY * scale);

            float w = glyph.width * scale;
            float h = glyph.height * scale;

            if (w > 0 && h > 0) {
                setQuadVerts(
                        xPos, yPos,         r, g, b, a, glyph.u0, glyph.v0,
                        xPos, yPos + h,     r, g, b, a, glyph.u0, glyph.v1,
                        xPos + w, yPos + h, r, g, b, a, glyph.u1, glyph.v1,
                        xPos + w, yPos,     r, g, b, a, glyph.u1, glyph.v0
                );

                renderer.submit(QUAD_VERTS, QUAD_INDICES, fontData.atlasTexture.getTextureID(), 0);
            }

            cursorX += (glyph.advance * scale);
        }
    }

    public static void line(Line line, int thickness, Colors color) {
        line(line, thickness, color.getColor());
    }
    public static void line(Line line, int thickness, Color color) {
        float startX = line.getStartPos().x;
        float startY = line.getStartPos().y;
        float endX = line.getEndPos().x;
        float endY = line.getEndPos().y;

        float dx = endX - startX;
        float dy = endY - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return;

        dx /= length;
        dy /= length;

        float halfThick = thickness / 2.0f;
        float nx = -dy * halfThick;
        float ny = dx * halfThick;

        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        setQuadVerts(
                startX - nx, startY - ny,  r, g, b, a, 0f, 0f,
                startX + nx, startY + ny,  r, g, b, a, 0f, 0f,
                endX + nx, endY + ny,      r, g, b, a, 0f, 0f,
                endX - nx, endY - ny,      r, g, b, a, 0f, 0f
        );

        renderer.submit(QUAD_VERTS, QUAD_INDICES, 0, 0);
    }

    public static void curve(Curve curve, Colors color) {
        curve(curve, 1, color.getColor());
    }
    public static void curve(Curve curve, Color color) {
        curve(curve, 1, color);
    }
    public static void curve(Curve curve, int thickness, Colors color) {
        curve(curve, thickness, color.getColor());
    }
    public static void curve(Curve curve, int thickness, Color color) {
        List<Vector2f> points = curve.getPoints();
        int numPoints = points.size();

        if (numPoints < 2) return;

        int vertCount = numPoints * 2;
        int requiredVertsLength = vertCount * RenderBatch.INPUT_VERTEX_SIZE;
        int requiredIndicesLength = (numPoints - 1) * 6;

        ensureDynamicCapacity(requiredVertsLength, requiredIndicesLength);

        float halfThick = thickness / 2.0f;
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        for (int i = 0; i < numPoints; i++) {
            Vector2f current = points.get(i);
            float nx, ny;

            if (i == 0) {
                Vector2f next = points.get(i + 1);
                float dx = next.x - current.x;
                float dy = next.y - current.y;
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                nx = -dy / len;
                ny = dx / len;
            } else if (i == numPoints - 1) {
                Vector2f prev = points.get(i - 1);
                float dx = current.x - prev.x;
                float dy = current.y - prev.y;
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                nx = -dy / len;
                ny = dx / len;
            } else {
                Vector2f prev = points.get(i - 1);
                Vector2f next = points.get(i + 1);

                float dir1X = current.x - prev.x;
                float dir1Y = current.y - prev.y;
                float len1 = (float) Math.sqrt(dir1X * dir1X + dir1Y * dir1Y);
                dir1X /= len1; dir1Y /= len1;

                float dir2X = next.x - current.x;
                float dir2Y = next.y - current.y;
                float len2 = (float) Math.sqrt(dir2X * dir2X + dir2Y * dir2Y);
                dir2X /= len2; dir2Y /= len2;

                float tangentX = dir1X + dir2X;
                float tangentY = dir1Y + dir2Y;
                float tangentLen = (float) Math.sqrt(tangentX * tangentX + tangentY * tangentY);

                if (tangentLen == 0) {
                    nx = -dir1Y;
                    ny = dir1X;
                } else {
                    tangentX /= tangentLen;
                    tangentY /= tangentLen;
                    nx = -tangentY;
                    ny = tangentX;

                    float dot = nx * (-dir1Y) + ny * dir1X;
                    dot = Math.max(0.1f, Math.min(dot, 1.0f));
                    nx /= dot;
                    ny /= dot;
                }
            }

            int offset = i * 2 * RenderBatch.INPUT_VERTEX_SIZE;

            DYNAMIC_VERTS[offset] = current.x + nx * halfThick;
            DYNAMIC_VERTS[offset + 1] = current.y + ny * halfThick;
            DYNAMIC_VERTS[offset + 2] = r; DYNAMIC_VERTS[offset + 3] = g;
            DYNAMIC_VERTS[offset + 4] = b; DYNAMIC_VERTS[offset + 5] = a;
            DYNAMIC_VERTS[offset + 6] = 0f; DYNAMIC_VERTS[offset + 7] = 0f;

            DYNAMIC_VERTS[offset + 8] = current.x - nx * halfThick;
            DYNAMIC_VERTS[offset + 9] = current.y - ny * halfThick;
            DYNAMIC_VERTS[offset + 10] = r; DYNAMIC_VERTS[offset + 11] = g;
            DYNAMIC_VERTS[offset + 12] = b; DYNAMIC_VERTS[offset + 13] = a;
            DYNAMIC_VERTS[offset + 14] = 0f; DYNAMIC_VERTS[offset + 15] = 0f;
        }

        int idxOffset = 0;
        for (int i = 0; i < numPoints - 1; i++) {
            int v = i * 2;

            DYNAMIC_INDICES[idxOffset++] = v;
            DYNAMIC_INDICES[idxOffset++] = v + 1;
            DYNAMIC_INDICES[idxOffset++] = v + 2;

            DYNAMIC_INDICES[idxOffset++] = v + 2;
            DYNAMIC_INDICES[idxOffset++] = v + 1;
            DYNAMIC_INDICES[idxOffset++] = v + 3;
        }

        renderer.submit(DYNAMIC_VERTS, DYNAMIC_INDICES, 0, 0);
    }

    public static void circle(Circle circle, Colors color) {
        circle(circle, color.getColor());
    }
    public static void circle(Circle circle, Color color) {
        Vector2f center = circle.getCenter();
        float radius = circle.getRadius();
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        CIRCLE_VERTS[0] = center.x; CIRCLE_VERTS[1] = center.y;
        CIRCLE_VERTS[2] = r; CIRCLE_VERTS[3] = g; CIRCLE_VERTS[4] = b; CIRCLE_VERTS[5] = a;
        CIRCLE_VERTS[6] = 0f; CIRCLE_VERTS[7] = 0f;

        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            int offset = (i + 1) * RenderBatch.INPUT_VERTEX_SIZE;
            CIRCLE_VERTS[offset]     = center.x + radius * CIRCLE_COS[i];
            CIRCLE_VERTS[offset + 1] = center.y + radius * CIRCLE_SIN[i];
            CIRCLE_VERTS[offset + 2] = r;
            CIRCLE_VERTS[offset + 3] = g;
            CIRCLE_VERTS[offset + 4] = b;
            CIRCLE_VERTS[offset + 5] = a;
            CIRCLE_VERTS[offset + 6] = 0f;
            CIRCLE_VERTS[offset + 7] = 0f;
        }

        renderer.submit(CIRCLE_VERTS, CIRCLE_INDICES, 0, 0);
    }

    public static void triangle(Triangle tri, Colors color) {
        triangle(tri, color.getColor());
    }
    public static void triangle(Triangle tri, Color color) {
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        setTriVerts(
                tri.getPointA().x, tri.getPointA().y, r, g, b, a, 0f, 0f,
                tri.getPointB().x, tri.getPointB().y, r, g, b, a, 0f, 0f,
                tri.getPointC().x, tri.getPointC().y, r, g, b, a, 0f, 0f
        );

        renderer.submit(TRI_VERTS, 0, 0);
    }

    public static void arrow(Arrow arrow, int thickness, float headLength, float headWidth, Colors color) {
        arrow(arrow, thickness, headLength, headWidth, color.getColor());
    }
    public static void arrow(Arrow arrow, int thickness, float headLength, float headWidth, Color color) {
        Line line = arrow.getLine();
        if (line == null) return;

        Vector2f start = line.getStartPos();
        Vector2f end = line.getEndPos();

        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return;

        float dirX = dx / length;
        float dirY = dy / length;

        float sweep = headLength * 0.35f;

        float baseX = end.x - dirX * headLength;
        float baseY = end.y - dirY * headLength;

        float indentX = baseX + dirX * sweep;
        float indentY = baseY + dirY * sweep;

        Vector2f shaftEnd = new Vector2f(indentX, indentY);
        Line shaft = new Line(start, shaftEnd);
        line(shaft, thickness, color);

        circle(new Circle(start, (int) (thickness / 2.0f)), color);

        float perpX = -dirY;
        float perpY = dirX;
        float halfWidth = headWidth / 2.0f;

        float leftX = baseX + perpX * halfWidth;
        float leftY = baseY + perpY * halfWidth;

        float rightX = baseX - perpX * halfWidth;
        float rightY = baseY - perpY * halfWidth;


        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        setQuadVerts(
                end.x, end.y,       r, g, b, a, 0f, 0f, // P0: The Tip
                leftX, leftY,       r, g, b, a, 0f, 0f, // P1: Left Wing
                indentX, indentY,   r, g, b, a, 0f, 0f, // P2: Inner Indent
                rightX, rightY,     r, g, b, a, 0f,     0f  // P3: Right Wing
        );

        renderer.submit(QUAD_VERTS, QUAD_INDICES, 0, 0);
    }
    public static void arrow(Arrow arrow, int thickness, Colors color) {
        arrow(arrow, thickness, color.getColor());
    }
    public static void arrow(Arrow arrow, int thickness, Color color) {
        arrow(arrow, thickness, thickness * 5.0f, thickness * 4.0f, color);
    }


    private static void setQuadVerts(float x0, float y0, float r0, float g0, float b0, float a0, float u0, float v0,
                                     float x1, float y1, float r1, float g1, float b1, float a1, float u1, float v1,
                                     float x2, float y2, float r2, float g2, float b2, float a2, float u2, float v2,
                                     float x3, float y3, float r3, float g3, float b3, float a3, float u3, float v3) {
        QUAD_VERTS[0] = x0; QUAD_VERTS[1] = y0; QUAD_VERTS[2] = r0; QUAD_VERTS[3] = g0; QUAD_VERTS[4] = b0; QUAD_VERTS[5] = a0; QUAD_VERTS[6] = u0; QUAD_VERTS[7] = v0;
        QUAD_VERTS[8] = x1; QUAD_VERTS[9] = y1; QUAD_VERTS[10]= r1; QUAD_VERTS[11]= g1; QUAD_VERTS[12]= b1; QUAD_VERTS[13]= a1; QUAD_VERTS[14]= u1; QUAD_VERTS[15]= v1;
        QUAD_VERTS[16]= x2; QUAD_VERTS[17]= y2; QUAD_VERTS[18]= r2; QUAD_VERTS[19]= g2; QUAD_VERTS[20]= b2; QUAD_VERTS[21]= a2; QUAD_VERTS[22]= u2; QUAD_VERTS[23]= v2;
        QUAD_VERTS[24]= x3; QUAD_VERTS[25]= y3; QUAD_VERTS[26]= r3; QUAD_VERTS[27]= g3; QUAD_VERTS[28]= b3; QUAD_VERTS[29]= a3; QUAD_VERTS[30]= u3; QUAD_VERTS[31]= v3;
    }

    private static void setTriVerts(float x0, float y0, float r0, float g0, float b0, float a0, float u0, float v0,
                                    float x1, float y1, float r1, float g1, float b1, float a1, float u1, float v1,
                                    float x2, float y2, float r2, float g2, float b2, float a2, float u2, float v2) {
        TRI_VERTS[0] = x0; TRI_VERTS[1] = y0; TRI_VERTS[2] = r0; TRI_VERTS[3] = g0; TRI_VERTS[4] = b0; TRI_VERTS[5] = a0; TRI_VERTS[6] = u0; TRI_VERTS[7] = v0;
        TRI_VERTS[8] = x1; TRI_VERTS[9] = y1; TRI_VERTS[10]= r1; TRI_VERTS[11]= g1; TRI_VERTS[12]= b1; TRI_VERTS[13]= a1; TRI_VERTS[14]= u1; TRI_VERTS[15]= v1;
        TRI_VERTS[16]= x2; TRI_VERTS[17]= y2; TRI_VERTS[18]= r2; TRI_VERTS[19]= g2; TRI_VERTS[20]= b2; TRI_VERTS[21]= a2; TRI_VERTS[22]= u2; TRI_VERTS[23]= v2;
    }
}