package net.meowsers;

import net.meowsers.Peach.Animation.Animate;
import net.meowsers.Peach.Application;
import net.meowsers.Peach.Animation.AnimateDraw;
import net.meowsers.Peach.Drawables.*;
import net.meowsers.Peach.Drawables.Rectangle;
import net.meowsers.Peach.Graphics.Draw;
import net.meowsers.Peach.Graphics.Visualize;
import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.Enums.AnimationType;
import net.meowsers.Peach.Utils.Enums.Colors;
import net.meowsers.Peach.Utils.Enums.Fonts;
import net.meowsers.Peach.Utils.Enums.LogType;
import net.meowsers.Peach.Utils.LiveVector2f;
import net.meowsers.Peach.Utils.Log;
import net.meowsers.Peach.Utils.MathUtils;
import org.joml.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    @Override
    public void script() {
        getWindow().clearBackground(Colors.DarkGray);

        wait(.5f);

        Text text1 = new Text("Hello, Everyone!", Fonts.Default, 35);
        Text text2 = new Text("And hello from Peach!", Fonts.Default, 20);

        Rectangle rect1 = new Rectangle(100, 350, 75, 75);
        Rectangle rect2 = new Rectangle(325, 350, 50, 50);

        LiveVector2f start = new LiveVector2f(() -> new Vector2f(rect1.getX() + rect1.getW() + 20, rect1.getY() + rect1.getH() / 2));
        LiveVector2f end = new LiveVector2f(() -> new Vector2f(rect2.getX() - 20, rect2.getY() + rect2.getH() / 2));
        Arrow arrow = new Arrow(new Line(start, end));

        AnimateDraw.text(text1, Text.getCenteredX(getWindow(), text1), 50, Colors.White, 1.5f);
        AnimateDraw.text(text2, Text.getCenteredX(getWindow(), text2), 100, Colors.Pink, 1.5f);

        List<Circle> circles = new ArrayList<>();
        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 3; y++) {
                Circle circle = new Circle(new Vector2f(Text.getCenteredX(getWindow(), text2) + 25 + 50 * x, 150 + 50 * y), 20);
                circles.add(circle);
                AnimateDraw.circle(circle, Colors.Crimson, .1f);
            }
        }

        AnimateDraw.rectangle(rect1, Colors.Magenta, 1);
        AnimateDraw.arrow(arrow, 4, Colors.Red, 1, AnimationType.Scale);
        AnimateDraw.rectangle(rect2, Colors.SkyBlue, 1);

        Animate.rectangle(rect2, 500, 300, 50, 50, 1.5f);

        animateTogether(() -> {
            Animate.rectangle(rect1, 150, 250, 75, 75, 1.5f);
            Animate.rectangle(rect2, 500, 450, 50, 50, 1.5f);
        });

        animateTogether(() -> {
            Animate.rectangle(rect1, 500, 300, 75, 75, 1.5f);
            Animate.rectangle(rect2, 150, 400, 50, 50, 1.5f);
        });

        wait(.5f);

        animateTogether(() -> {
            Animate.rectangle(rect1, 100, 350, 75, 75, .75f);
            Animate.rectangle(rect2, 500, 300, 50, 50, .75f);
        });

        wait(1);

        Group group = new Group(text1, text2, rect1, rect2, arrow);
        group.add(circles);

        Animate.groupMove(group, 0, -getWindow().getHeight(), 1.5f);

        Texture peach = new Texture("/Users/meowsers/Documents/peach.png");
        float scale = 0.5f;
        Rectangle peachRect = new Rectangle(100, -peach.getHeight() * scale, peach.getWidth() * scale, peach.getHeight() * scale);
        Draw.image(peach, peachRect);
        Animate.rectangle(peachRect, 100, 80, 1.5f);
        scale = 0.8f;
        Animate.rectangle(peachRect, 100, 80, peach.getWidth()*scale, peach.getHeight()*scale, 1.f);
        wait(.5f);
        Animate.rectangle(peachRect, getWindow().getWidth(), 80, 1.5f);

        wait(1);

        Text vecShowcaseText = new Text("These are all vector 3s!", Fonts.Default, 30);
        AnimateDraw.text(vecShowcaseText, Text.getCenteredX(getWindow(), vecShowcaseText), 50, Colors.White, 1);

        Text vec3Showcase1 = Visualize._vec3(3, 1, 4, Text.getCenteredX(getWindow(), vecShowcaseText), 80, 20);
        Text vec3Showcase2 = Visualize._vec3(3.14, 6.9, 5.555, Text.getCenteredX(getWindow(), vecShowcaseText), 80, 20);
        Text vec3Showcase3 = Visualize._vec3("Hello", "World!", "Peach", Text.getCenteredX(getWindow(), vecShowcaseText), 80, 20);
        Text vec3Showcase4 = Visualize._vec3(true, false, false, Text.getCenteredX(getWindow(), vecShowcaseText), 80, 20);

        AnimateDraw.text(vec3Showcase1, Text.getCenteredX(getWindow(), vec3Showcase1), 120, Colors.Pink, 1);
        AnimateDraw.text(vec3Showcase2, Text.getCenteredX(getWindow(), vec3Showcase2), 160, Colors.Pink, 1);
        AnimateDraw.text(vec3Showcase3, Text.getCenteredX(getWindow(), vec3Showcase3), 200, Colors.Pink, 1);
        AnimateDraw.text(vec3Showcase4, Text.getCenteredX(getWindow(), vec3Showcase4), 240, Colors.Pink, 1);

        Group vecGroup = new Group(vec3Showcase1, vec3Showcase2, vec3Showcase3, vec3Showcase4);
        Animate.group(vecGroup, Text.getCenteredX(getWindow(), vec3Showcase4), getWindow().getHeight()+100, 1);
        Animate.textPosition(vecShowcaseText, vecShowcaseText.getPosition().x, -100, 1);

        Text curveShowcaseText1 = new Text("What about some curves?", Fonts.Default, 30);
        AnimateDraw.text(curveShowcaseText1, Text.getCenteredX(getWindow(), curveShowcaseText1), 50, Colors.White, 1.5f);

        Curve curve1 = new Curve(new Vector2f(100, 100), new Vector2f(250, 100), new Vector2f(100, 250), new Vector2f(250, 250), .5f);
        AnimateDraw.curve(curve1, 4, Colors.White, 1.f);

        Curve curve2 = new Curve(new Vector2f(350, 150), new Vector2f(400, 100), new Vector2f(500, 100), new Vector2f(550, 150), .5f);
        AnimateDraw.curve(curve2, 4, Colors.White, 1.f);

        Curve curve3 = new Curve(new Vector2f(150, 300), new Vector2f(250, 400), new Vector2f(300, 250), new Vector2f(450, 250), .5f);
        AnimateDraw.curve(curve3, 4, Colors.White, 1.f);

        Curve curve4 = new Curve(new Vector2f(400, 350), new Vector2f(500, 400), new Vector2f(550, 250), new Vector2f(450, 300), .5f);
        AnimateDraw.curve(curve4, 4, Colors.White, 1.f);

        Curve curve5 = new Curve(new Vector2f(650, 400), new Vector2f(650, 350), new Vector2f(550, 250), new Vector2f(750, 300), .5f);
        AnimateDraw.curve(curve5, 4, Colors.White, 1.f);

        wait(.5f);

        animateTogether(() -> {
            Animate.curve(curve1, new Vector2f(100, 120), new Vector2f(220, 50), new Vector2f(120, 300), new Vector2f(260, 230), 0.75f);
            Animate.curve(curve2, new Vector2f(340, 170), new Vector2f(420, 50), new Vector2f(480, 160), new Vector2f(560, 130), 0.75f);
            Animate.curve(curve3, new Vector2f(140, 280), new Vector2f(280, 430), new Vector2f(260, 210), new Vector2f(460, 270), 0.75f);
            Animate.curve(curve4, new Vector2f(410, 330), new Vector2f(470, 440), new Vector2f(580, 220), new Vector2f(440, 320), 0.75f);
            Animate.curve(curve5, new Vector2f(640, 420), new Vector2f(680, 310), new Vector2f(520, 280), new Vector2f(760, 280), 0.75f);
        });

        animateTogether(() -> {
            Animate.curve(curve1, new Vector2f(110, 80), new Vector2f(270, 140), new Vector2f(80, 210), new Vector2f(240, 270), 0.75f);
            Animate.curve(curve2, new Vector2f(360, 130), new Vector2f(380, 140), new Vector2f(520, 60), new Vector2f(540, 170), 0.75f);
            Animate.curve(curve3, new Vector2f(160, 320), new Vector2f(220, 370), new Vector2f(340, 290), new Vector2f(440, 230), 0.75f);
            Animate.curve(curve4, new Vector2f(390, 370), new Vector2f(530, 360), new Vector2f(520, 280), new Vector2f(460, 280), 0.75f);
            Animate.curve(curve5, new Vector2f(660, 380), new Vector2f(620, 390), new Vector2f(580, 220), new Vector2f(740, 320), 0.75f);
        });

        animateTogether(() -> {
            Animate.curve(curve1, new Vector2f(100, 100), new Vector2f(250, 100), new Vector2f(100, 250), new Vector2f(250, 250), 0.75f);
            Animate.curve(curve2, new Vector2f(350, 150), new Vector2f(400, 100), new Vector2f(500, 100), new Vector2f(550, 150), 0.75f);
            Animate.curve(curve3, new Vector2f(150, 300), new Vector2f(250, 400), new Vector2f(300, 250), new Vector2f(450, 250), 0.75f);
            Animate.curve(curve4, new Vector2f(400, 350), new Vector2f(500, 400), new Vector2f(550, 250), new Vector2f(450, 300), 0.75f);
            Animate.curve(curve5, new Vector2f(650, 400), new Vector2f(650, 350), new Vector2f(550, 250), new Vector2f(750, 300), 0.75f);
        });

        animateTogether(() -> {
            Animate.textString(curveShowcaseText1, "Pretty cool, no?", 1);
            Animate.textPosition(curveShowcaseText1, new Vector2f(Text.getCenteredX(getWindow(), curveShowcaseText1, "Pretty cool, no?"), 50), 1);

        });

    }
}
