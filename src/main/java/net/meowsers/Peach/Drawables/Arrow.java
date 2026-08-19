package net.meowsers.Peach.Drawables;

public class Arrow {
    private Line line;
    private Triangle arrowHead;

    public Arrow(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
