package net.meowsers.Peach.Animation;

public interface Animation {

    default void begin() {

    }

    void update(float deltaTime);

    default void render() {

    }

    boolean isFinished();

    void reset();
}
