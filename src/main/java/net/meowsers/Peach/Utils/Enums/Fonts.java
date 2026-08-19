package net.meowsers.Peach.Utils.Enums;

public enum Fonts {
    Default("src/main/resources/Fonts/JetBrainsMono.ttf"),
    JetBrainsMono("src/main/resources/Fonts/JetBrainsMono.ttf");

    private String fontPath;
    Fonts(String fontPath) {
        this.fontPath = fontPath;
    }
    public String path() {return fontPath;}
}
