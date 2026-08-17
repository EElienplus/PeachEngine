package net.meowsers.Peach.Graphics;

import net.meowsers.Peach.Shapes.Circle;
import net.meowsers.Peach.Shapes.Line;
import net.meowsers.Peach.Shapes.Rectangle;
import net.meowsers.Peach.Shapes.Triangle;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.Enums.Colors;
import org.joml.Vector2f;

public class Draw {
    private static Renderer renderer;

    // Pre alocate static buffers
    private static final int[] QUAD_INDICES = {0, 1, 2, 2, 3, 0};
    private static final float[] QUAD_VERTS = new float[4 * RenderBatch.INPUT_VERTEX_SIZE];
    private static final float[] TRI_VERTS = new float[3 * RenderBatch.INPUT_VERTEX_SIZE];

    // pre calculated circle stuff
    private static final int CIRCLE_SEGMENTS = 64;
    private static final float[] CIRCLE_COS = new float[CIRCLE_SEGMENTS];
    private static final float[] CIRCLE_SIN = new float[CIRCLE_SEGMENTS];
    private static final int[] CIRCLE_INDICES = new int[CIRCLE_SEGMENTS * 3];
    private static final float[] CIRCLE_VERTS = new float[(CIRCLE_SEGMENTS + 1) * RenderBatch.INPUT_VERTEX_SIZE];

    static {
        // pre calculate unit circle when the engine starts
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            float theta = (float) (2.0f * Math.PI * i / CIRCLE_SEGMENTS);
            CIRCLE_COS[i] = (float) Math.cos(theta);
            CIRCLE_SIN[i] = (float) Math.sin(theta);
        }
        // pre calculate circle fan indices
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

    public static void circle(Circle circle, Colors color) {
        circle(circle, color.getColor());
    }
    public static void circle(Circle circle, Color color) {
        Vector2f center = circle.getCenter();
        float radius = circle.getRadius();
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        // Center vertex
        CIRCLE_VERTS[0] = center.x; CIRCLE_VERTS[1] = center.y;
        CIRCLE_VERTS[2] = r; CIRCLE_VERTS[3] = g; CIRCLE_VERTS[4] = b; CIRCLE_VERTS[5] = a;
        CIRCLE_VERTS[6] = 0f; CIRCLE_VERTS[7] = 0f;

        // Perimeter vertices (using the pre-calculated arrays)
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