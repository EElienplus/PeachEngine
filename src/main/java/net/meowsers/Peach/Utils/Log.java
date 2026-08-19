package net.meowsers.Peach.Utils;

import net.meowsers.Peach.Utils.Enums.ConsoleColor;
import net.meowsers.Peach.Utils.Enums.LogType;

import static org.lwjgl.glfw.GLFW.*;

public class Log {

    public static void log(LogType type, String message)  {
        if(type == LogType.Message) {
            System.out.println(message);
        } else if (type == LogType.Warning) {
            System.out.println(ConsoleColor.YELLOW.colorize(message));
        } else if (type == LogType.Error) {
            try {
                throw new Exception(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
