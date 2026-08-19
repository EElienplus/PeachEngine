package net.meowsers;

import net.meowsers.Peach.Animation.Animate;
import net.meowsers.Peach.Application;
import net.meowsers.Peach.Animation.AnimateDraw;
import net.meowsers.Peach.Drawables.*;
import net.meowsers.Peach.Graphics.Visualize;
import net.meowsers.Peach.Utils.Enums.Colors;
import net.meowsers.Peach.Utils.Enums.Fonts;
import net.meowsers.Peach.Utils.Enums.LogType;
import net.meowsers.Peach.Utils.Log;
import org.joml.Vector2f;

public class App extends Application {

    @Override
    public void script() {
        getWindow().clearBackground(Colors.DarkGreen);

        Text vecText = Visualize._vec4(69, 314, 55, 77, 50, 50, 25);
        AnimateDraw.text(vecText, Colors.White, 2);

        Vector2f charPos = Visualize.getCharPosition(vecText, 50, 50, 5);
        Arrow arrow = new Arrow(new Line(new Vector2f(50, 200), charPos));
        AnimateDraw.arrow(arrow, 4, Colors.White, 1);

        wait(1);

        Animate.textPosition(vecText, 300, 150, 1.25f);

    }
}
