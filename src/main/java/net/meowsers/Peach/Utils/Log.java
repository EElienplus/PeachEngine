package net.meowsers.Peach.Utils;

public class Log {

    public static void log(LogType type, String message) {
        if(type == LogType.Message) {
            System.out.println(message);
        } else if (type == LogType.Warning) {
            System.out.println(ConsoleColor.YELLOW.colorize(message));
        } else if (type == LogType.Error) {
            assert false : ConsoleColor.RED.colorize(message);
        }
    }
}
