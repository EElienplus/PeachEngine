package net.meowsers.Peach.Animation;

public class DrawAnimation implements Animation {

    private final Runnable drawAction;
    private boolean finished;

    public DrawAnimation(Runnable drawAction) {
        if (drawAction == null) {
            throw new IllegalArgumentException("Draw action can't be null.");
        }
        this.drawAction = drawAction;
    }

    @Override
    public void begin() {
        finished = true;
    }

    @Override
    public void update(float deltaTime) {
        finished = true;
    }

    @Override
    public void render() {
        drawAction.run();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void reset() {
        finished = false;
    }

    public Runnable getDrawAction() {
        return drawAction;
    }
}
