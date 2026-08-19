package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Curve {
    private Vector2f p0, p1, p2, p3;

    private List<Vector2f> points = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();

    // Maximum pixel deviation allowed before subdividing again.
    // Lower = smoother curve, Higher = fewer vertices.
    private float tolerance;
    private static final int MAX_DEPTH = 10; // Prevents infinite recursion stack overflows

    public Curve(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float tolerance) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.tolerance = tolerance;

        rebuild();
    }

    public void rebuild() {
        Vector2f start = getP0();
        Vector2f control1 = getP1();
        Vector2f control2 = getP2();
        Vector2f end = getP3();

        points.clear();
        lines.clear();

        // Add the very first point
        points.add(new Vector2f(start.x, start.y));

        // Recursively subdivide and add points
        subdivide(start.x, start.y, control1.x, control1.y, control2.x, control2.y, end.x, end.y, 0);

        // Build lines from the resulting optimized point list
        populateLinesList();
    }

    private void subdivide(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, int depth) {
        if (depth >= MAX_DEPTH || isFlat(x0, y0, x1, y1, x2, y2, x3, y3)) {
            // Once the segment is flat enough, or we hit max depth, add the end point
            points.add(new Vector2f(x3, y3));
            return;
        }

        // De Casteljau's algorithm mathematically optimized to exactly t=0.5
        // Using + and * 0.5f is exceptionally fast for the CPU

        float x01 = (x0 + x1) * 0.5f;
        float y01 = (y0 + y1) * 0.5f;
        float x12 = (x1 + x2) * 0.5f;
        float y12 = (y1 + y2) * 0.5f;
        float x23 = (x2 + x3) * 0.5f;
        float y23 = (y2 + y3) * 0.5f;

        float x012 = (x01 + x12) * 0.5f;
        float y012 = (y01 + y12) * 0.5f;
        float x123 = (x12 + x23) * 0.5f;
        float y123 = (y12 + y23) * 0.5f;

        float x0123 = (x012 + x123) * 0.5f;
        float y0123 = (y012 + y123) * 0.5f;

        // Subdivide the left side
        subdivide(x0, y0, x01, y01, x012, y012, x0123, y0123, depth + 1);

        // Subdivide the right side
        subdivide(x0123, y0123, x123, y123, x23, y23, x3, y3, depth + 1);
    }

    private boolean isFlat(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        float dx = x3 - x0;
        float dy = y3 - y0;
        float lenSq = dx * dx + dy * dy;
        float tolSq = tolerance * tolerance;

        // Edge case: Start and end point are exactly the same
        if (lenSq == 0) {
            float d1 = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
            float d2 = (x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0);
            return d1 <= tolSq && d2 <= tolSq;
        }

        // Calculate the cross product (area of parallelogram) to find perpendicular distance
        float area1 = Math.abs(dx * (y1 - y0) - dy * (x1 - x0));
        float area2 = Math.abs(dx * (y2 - y0) - dy * (x2 - x0));

        // Normally distance = Area / Length. So Distance^2 <= Tolerance^2
        // Equates to: Area^2 <= Tolerance^2 * Length^2
        return (area1 * area1 <= tolSq * lenSq) && (area2 * area2 <= tolSq * lenSq);
    }

    private void populateLinesList() {
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new Line(points.get(i), points.get(i + 1)));
        }
    }

    public Vector2f getPointBasedOnT(float t) {
        t = Math.max(0.0f, Math.min(t, 1.0f)); // Clamp between 0 and 1
        float u = 1.0f - t;
        float tt = t * t;
        float uu = u * u;

        Vector2f start = getP0();
        Vector2f control1 = getP1();
        Vector2f control2 = getP2();
        Vector2f end = getP3();

        // Explicit polynomial weights: (1-t)^3, 3(1-t)^2*t, 3(1-t)*t^2, t^3
        float w0 = uu * u;
        float w1 = 3 * uu * t;
        float w2 = 3 * u * tt;
        float w3 = tt * t;

        float pX = w0 * start.x + w1 * control1.x + w2 * control2.x + w3 * end.x;
        float pY = w0 * start.y + w1 * control1.y + w2 * control2.y + w3 * end.y;

        return new Vector2f(pX, pY);
    }

    public List<Vector2f> getPoints() {
        rebuild();
        return points;
    }

    public List<Line> getLines() {
        rebuild();
        return lines;
    }

    public float getTolerance() {
        return tolerance;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    public Vector2f getP0() {
        return LiveVector2f.resolve(p0);
    }

    public void setP0(Vector2f p0) {
        this.p0.set(p0);
    }

    public Vector2f getP1() {
        return LiveVector2f.resolve(p1);
    }

    public void setP1(Vector2f p1) {
        this.p1.set(p1);
    }

    public Vector2f getP2() {
        return LiveVector2f.resolve(p2);
    }

    public void setP2(Vector2f p2) {
        this.p2.set(p2);
    }

    public Vector2f getP3() {
        return LiveVector2f.resolve(p3);
    }

    public void setP3(Vector2f p3) {
        this.p3.set(p3);
    }
}