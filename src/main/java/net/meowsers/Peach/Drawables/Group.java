package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import org.joml.Vector2f;

import java.util.*;

public class Group implements Iterable<Object> {
    private final List<Object> items = new ArrayList<>();
    private final Vector2f position = new Vector2f();

    public Group() {
    }

    public Group(Object... items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null.");
        }
        add(items);
        if (!this.items.isEmpty()) {
            this.position.set(getMin());
        }
    }

    public Group(Collection<?> items) {
        if (items == null) {
            throw new IllegalArgumentException("Items collection cannot be null.");
        }
        add(items);
        if (!this.items.isEmpty()) {
            this.position.set(getMin());
        }
    }

    public Group(Vector2f position, Object... items) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null.");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null.");
        }
        this.position.set(position);
        add(items);
    }

    public Group(Vector2f position, Collection<?> items) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null.");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items collection cannot be null.");
        }
        this.position.set(position);
        add(items);
    }

    public Group(float x, float y, Object... items) {
        this(new Vector2f(x, y), items);
    }

    public Group(float x, float y, Collection<?> items) {
        this(new Vector2f(x, y), items);
    }

    public Group add(Object item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        if (item == this) {
            throw new IllegalArgumentException("Cannot add a group to itself.");
        }
        items.add(item);
        return this;
    }

    public Group add(Object... items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null.");
        }
        for (Object item : items) {
            add(item);
        }
        return this;
    }

    public Group add(Collection<?> items) {
        if (items == null) {
            throw new IllegalArgumentException("Items collection cannot be null.");
        }
        for (Object item : items) {
            add(item);
        }
        return this;
    }

    public boolean remove(Object item) {
        return items.remove(item);
    }

    public boolean removeAll(Collection<?> items) {
        if (items == null) {
            throw new IllegalArgumentException("Items collection cannot be null.");
        }
        return this.items.removeAll(items);
    }

    public void clear() {
        items.clear();
    }

    public List<Object> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<Object> getDrawables() {
        return getItems();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getItems(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        List<T> result = new ArrayList<>();
        for (Object item : items) {
            if (type.isInstance(item)) {
                result.add((T) item);
            }
        }
        return result;
    }

    public Object get(int index) {
        return items.get(index);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean contains(Object item) {
        return items.contains(item);
    }

    @Override
    public Iterator<Object> iterator() {
        return items.iterator();
    }

    public Vector2f getPosition() {
        return LiveVector2f.resolve(position);
    }

    public float getX() {
        return getPosition().x;
    }

    public float getY() {
        return getPosition().y;
    }

    public void setPosition(Vector2f targetPosition) {
        if (targetPosition == null) {
            throw new IllegalArgumentException("Target position cannot be null.");
        }
        setPosition(targetPosition.x, targetPosition.y);
    }

    public void setPosition(float x, float y) {
        float dx = x - position.x;
        float dy = y - position.y;
        move(dx, dy);
    }

    public void setX(float x) {
        setPosition(x, position.y);
    }

    public void setY(float y) {
        setPosition(position.x, y);
    }

    public void move(float dx, float dy) {
        position.add(dx, dy);
        for (Object item : items) {
            moveDrawable(item, dx, dy);
        }
    }

    public void move(Vector2f delta) {
        if (delta == null) {
            throw new IllegalArgumentException("Delta cannot be null.");
        }
        move(delta.x, delta.y);
    }

    public void translate(float dx, float dy) {
        move(dx, dy);
    }

    public void translate(Vector2f delta) {
        move(delta);
    }

    public Vector2f getMin() {
        if (items.isEmpty()) {
            return new Vector2f(position);
        }

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;

        for (Object item : items) {
            if (item instanceof Rectangle) {
                Rectangle r = (Rectangle) item;
                minX = Math.min(minX, r.getX());
                minY = Math.min(minY, r.getY());
            } else if (item instanceof Circle) {
                Circle c = (Circle) item;
                minX = Math.min(minX, c.getCenter().x - c.getRadius());
                minY = Math.min(minY, c.getCenter().y - c.getRadius());
            } else if (item instanceof Triangle) {
                Triangle t = (Triangle) item;
                minX = Math.min(minX, Math.min(t.getPointA().x, Math.min(t.getPointB().x, t.getPointC().x)));
                minY = Math.min(minY, Math.min(t.getPointA().y, Math.min(t.getPointB().y, t.getPointC().y)));
            } else if (item instanceof Line) {
                Line l = (Line) item;
                minX = Math.min(minX, Math.min(l.getStartPos().x, l.getEndPos().x));
                minY = Math.min(minY, Math.min(l.getStartPos().y, l.getEndPos().y));
            } else if (item instanceof Arrow) {
                Arrow a = (Arrow) item;
                if (a.getLine() != null) {
                    minX = Math.min(minX, Math.min(a.getLine().getStartPos().x, a.getLine().getEndPos().x));
                    minY = Math.min(minY, Math.min(a.getLine().getStartPos().y, a.getLine().getEndPos().y));
                }
            } else if (item instanceof Curve) {
                Curve c = (Curve) item;
                minX = Math.min(minX, Math.min(Math.min(c.getP0().x, c.getP1().x), Math.min(c.getP2().x, c.getP3().x)));
                minY = Math.min(minY, Math.min(Math.min(c.getP0().y, c.getP1().y), Math.min(c.getP2().y, c.getP3().y)));
            } else if (item instanceof Text) {
                Text t = (Text) item;
                minX = Math.min(minX, t.getPosition().x);
                minY = Math.min(minY, t.getPosition().y);
            } else if (item instanceof Group) {
                Group g = (Group) item;
                Vector2f gMin = g.getMin();
                minX = Math.min(minX, gMin.x);
                minY = Math.min(minY, gMin.y);
            } else if (item instanceof Vector2f) {
                Vector2f v = (Vector2f) item;
                minX = Math.min(minX, v.x);
                minY = Math.min(minY, v.y);
            }
        }

        if (Float.isInfinite(minX) || Float.isInfinite(minY)) {
            return new Vector2f(position);
        }

        return new Vector2f(minX, minY);
    }

    public Vector2f getMax() {
        if (items.isEmpty()) {
            return new Vector2f(position);
        }

        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (Object item : items) {
            if (item instanceof Rectangle) {
                Rectangle r = (Rectangle) item;
                maxX = Math.max(maxX, r.getX() + r.getW());
                maxY = Math.max(maxY, r.getY() + r.getH());
            } else if (item instanceof Circle) {
                Circle c = (Circle) item;
                maxX = Math.max(maxX, c.getCenter().x + c.getRadius());
                maxY = Math.max(maxY, c.getCenter().y + c.getRadius());
            } else if (item instanceof Triangle) {
                Triangle t = (Triangle) item;
                maxX = Math.max(maxX, Math.max(t.getPointA().x, Math.max(t.getPointB().x, t.getPointC().x)));
                maxY = Math.max(maxY, Math.max(t.getPointA().y, Math.max(t.getPointB().y, t.getPointC().y)));
            } else if (item instanceof Line) {
                Line l = (Line) item;
                maxX = Math.max(maxX, Math.max(l.getStartPos().x, l.getEndPos().x));
                maxY = Math.max(maxY, Math.max(l.getStartPos().y, l.getEndPos().y));
            } else if (item instanceof Arrow) {
                Arrow a = (Arrow) item;
                if (a.getLine() != null) {
                    maxX = Math.max(maxX, Math.max(a.getLine().getStartPos().x, a.getLine().getEndPos().x));
                    maxY = Math.max(maxY, Math.max(a.getLine().getStartPos().y, a.getLine().getEndPos().y));
                }
            } else if (item instanceof Curve) {
                Curve c = (Curve) item;
                maxX = Math.max(maxX, Math.max(Math.max(c.getP0().x, c.getP1().x), Math.max(c.getP2().x, c.getP3().x)));
                maxY = Math.max(maxY, Math.max(Math.max(c.getP0().y, c.getP1().y), Math.max(c.getP2().y, c.getP3().y)));
            } else if (item instanceof Text) {
                Text t = (Text) item;
                maxX = Math.max(maxX, t.getPosition().x + t.getWidth());
                maxY = Math.max(maxY, t.getPosition().y + t.getFontSize());
            } else if (item instanceof Group) {
                Group g = (Group) item;
                Vector2f gMax = g.getMax();
                maxX = Math.max(maxX, gMax.x);
                maxY = Math.max(maxY, gMax.y);
            } else if (item instanceof Vector2f) {
                Vector2f v = (Vector2f) item;
                maxX = Math.max(maxX, v.x);
                maxY = Math.max(maxY, v.y);
            }
        }

        if (Float.isInfinite(maxX) || Float.isInfinite(maxY)) {
            return new Vector2f(position);
        }

        return new Vector2f(maxX, maxY);
    }

    public float getWidth() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        return Math.max(0, max.x - min.x);
    }

    public float getHeight() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        return Math.max(0, max.y - min.y);
    }

    public Vector2f getCenter() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        return new Vector2f((min.x + max.x) / 2.0f, (min.y + max.y) / 2.0f);
    }

    public Rectangle getBoundingBox() {
        Vector2f min = getMin();
        return new Rectangle(min.x, min.y, getWidth(), getHeight());
    }

    public void resetPositionToBounds() {
        this.position.set(getMin());
    }

    public static void moveDrawable(Object item, float dx, float dy) {
        if (item == null) {
            return;
        }

        if (item instanceof Rectangle) {
            Rectangle rect = (Rectangle) item;
            rect.setX(rect.getX() + dx);
            rect.setY(rect.getY() + dy);
        } else if (item instanceof Circle) {
            Circle circle = (Circle) item;
            Vector2f center = circle.getCenter();
            circle.setCenter(new Vector2f(center.x + dx, center.y + dy));
        } else if (item instanceof Triangle) {
            Triangle tri = (Triangle) item;
            Vector2f a = tri.getPointA();
            Vector2f b = tri.getPointB();
            Vector2f c = tri.getPointC();
            tri.setPointA(new Vector2f(a.x + dx, a.y + dy));
            tri.setPointB(new Vector2f(b.x + dx, b.y + dy));
            tri.setPointC(new Vector2f(c.x + dx, c.y + dy));
        } else if (item instanceof Line) {
            Line line = (Line) item;
            Vector2f start = line.getStartPos();
            Vector2f end = line.getEndPos();
            line.setStartPos(new Vector2f(start.x + dx, start.y + dy));
            line.setEndPos(new Vector2f(end.x + dx, end.y + dy));
        } else if (item instanceof Arrow) {
            Arrow arrow = (Arrow) item;
            Line line = arrow.getLine();
            if (line != null) {
                Vector2f start = line.getStartPos();
                Vector2f end = line.getEndPos();
                line.setStartPos(new Vector2f(start.x + dx, start.y + dy));
                line.setEndPos(new Vector2f(end.x + dx, end.y + dy));
            }
        } else if (item instanceof Curve) {
            Curve curve = (Curve) item;
            Vector2f p0 = curve.getP0();
            Vector2f p1 = curve.getP1();
            Vector2f p2 = curve.getP2();
            Vector2f p3 = curve.getP3();
            curve.setP0(new Vector2f(p0.x + dx, p0.y + dy));
            curve.setP1(new Vector2f(p1.x + dx, p1.y + dy));
            curve.setP2(new Vector2f(p2.x + dx, p2.y + dy));
            curve.setP3(new Vector2f(p3.x + dx, p3.y + dy));
        } else if (item instanceof Text) {
            Text text = (Text) item;
            Vector2f pos = text.getPosition();
            text.setPosition(pos.x + dx, pos.y + dy);
        } else if (item instanceof Group) {
            Group group = (Group) item;
            group.move(dx, dy);
        } else if (item instanceof Vector2f) {
            Vector2f vec = (Vector2f) item;
            vec.add(dx, dy);
        }
    }
}
