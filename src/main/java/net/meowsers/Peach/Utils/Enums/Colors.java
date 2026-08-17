package net.meowsers.Peach.Utils.Enums;

import net.meowsers.Peach.Utils.Color;

public enum Colors {
    White(new Color(1.f, 1.f, 1.f, 1.f)),
    Black(new Color(0.f, 0.f, 0.f, 1.f)),
    Red(new Color(1.f, 0.f, 0.f, 1.f)),
    Green(new Color(0.f, 1.f, 0.f, 1.f)),
    Blue(new Color(0.f, 0.f, 1.f, 1.f)),
    Yellow(new Color(1.f, 1.f, 0.f, 1.f)),
    Cyan(new Color(0.f, 1.f, 1.f, 1.f)),
    Magenta(new Color(1.f, 0.f, 1.f, 1.f)),
    Gray(new Color(0.5f, 0.5f, 0.5f, 1.f)),
    DarkGray(new Color(0.25f, 0.25f, 0.25f, 1.f)),
    LightGray(new Color(0.75f, 0.75f, 0.75f, 1.f)),
    Orange(new Color(1.f, 0.65f, 0.f, 1.f)),
    Pink(new Color(1.f, 0.75f, 0.8f, 1.f)),
    Purple(new Color(0.5f, 0.f, 0.5f, 1.f)),
    Brown(new Color(0.6f, 0.4f, 0.2f, 1.f)),
    Gold(new Color(1.f, 0.84f, 0.f, 1.f)),
    Silver(new Color(0.75f, 0.75f, 0.75f, 1.f)),
    Navy(new Color(0.125f, 0.165f, 0.267f, 1.f)),
    Teal(new Color(0.f, 0.5f, 0.5f, 1.f)),
    Maroon(new Color(0.5f, 0.f, 0.f, 1.f)),
    Olive(new Color(0.5f, 0.5f, 0.f, 1.f)),
    Lime(new Color(0.f, 1.f, 0.f, 1.f)),
    Indigo(new Color(0.29f, 0.f, 0.51f, 1.f)),
    Violet(new Color(0.93f, 0.51f, 0.93f, 1.f)),
    Turquoise(new Color(0.25f, 0.88f, 0.82f, 1.f)),
    Coral(new Color(1.f, 0.5f, 0.31f, 1.f)),
    Salmon(new Color(0.98f, 0.5f, 0.45f, 1.f)),
    Crimson(new Color(0.86f, 0.08f, 0.24f, 1.f)),
    Chocolate(new Color(0.82f, 0.41f, 0.12f, 1.f)),
    Khaki(new Color(0.94f, 0.9f, 0.55f, 1.f)),
    Plum(new Color(0.87f, 0.63f, 0.87f, 1.f)),
    Orchid(new Color(0.85f, 0.44f, 0.84f, 1.f)),
    SkyBlue(new Color(0.53f, 0.81f, 0.92f, 1.f)),
    SlateGray(new Color(0.44f, 0.5f, 0.56f, 1.f)),
    Tomato(new Color(1.f, 0.39f, 0.28f, 1.f)),
    Wheat(new Color(0.96f, 0.87f, 0.7f, 1.f)),
    DarkGreen(new Color(0.2f, 0.3f, 0.3f, 1.0f));

    private Color color;

    Colors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}
