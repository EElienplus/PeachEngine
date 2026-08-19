package net.meowsers.Peach.Utils;

import org.joml.Vector2f;

import java.util.function.Supplier;

public class LiveVector2f extends Vector2f {

    private final Supplier<Vector2f> value;

    public LiveVector2f() {
        value = () -> this;
    }

    public LiveVector2f(Supplier<Vector2f> value) {
        if (value == null) {
            throw new IllegalArgumentException("Live vector value can't be null.");
        }

        this.value = value;
        refresh();
    }

    public LiveVector2f refresh() {
        Vector2f currentValue = value.get();

        if (currentValue == null) {
            throw new IllegalStateException("Live vector value can't return null.");
        }

        set(currentValue);
        return this;
    }

    public static Vector2f resolve(Vector2f value) {
        if (value instanceof LiveVector2f liveValue) {
            return new Vector2f(liveValue.refresh());
        }

        return value;
    }
}
