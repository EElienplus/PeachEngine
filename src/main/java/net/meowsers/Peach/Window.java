package net.meowsers.Peach;

import net.meowsers.Peach.Utils.Color;
import net.meowsers.Peach.Utils.Enums.Colors;
import net.meowsers.Peach.Utils.Log;
import net.meowsers.Peach.Utils.Enums.LogType;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL41.*;

public class Window {

    private long handle = 0;
    private boolean running;
    private Color backgroundColor = Colors.Black.getColor();

    public Window(String title, int width, int height) {
        running = true;

        handle = glfwCreateWindow(width, height, title, 0, 0);
        if(handle == 0) {
            Log.log(LogType.Error, "Failed to create glfw window!");
        }

        glfwMakeContextCurrent(handle);
        glfwSetFramebufferSizeCallback(handle, this::frameBufferSizeCallback);
        GL.createCapabilities();

        int dummyTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, dummyTex);
        ByteBuffer whitePixel = BufferUtils.createByteBuffer(4).put(new byte[]{(byte)255, (byte)255, (byte)255, (byte)255});
        whitePixel.flip();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, whitePixel);
        glBindTexture(GL_TEXTURE_2D, 0);

        // Automatically fetch the true physical framebuffer size for the viewport
        // Cuz on retina displays it gets fucked up
        int[] fbWidth = new int[1];
        int[] fbHeight = new int[1];
        glfwGetFramebufferSize(handle, fbWidth, fbHeight);
        glViewport(0, 0, fbWidth[0], fbHeight[0]);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
    }



    public void update() {
        running = !glfwWindowShouldClose(handle);
        glfwPollEvents();

    }

    private void frameBufferSizeCallback(long window, int width, int height) {
        glViewport(0, 0, width, height);
    }

    public long getWindow() {
        return handle;
    }

    public boolean isRunning() {
        return running;
    }

    public void clearBackground(Color color) {
        if(color == null) {
            throw new IllegalArgumentException("Background color can't be null.");
        }

        backgroundColor = color;
        clearBackground();
    }

    public void clearBackground() {
        glClearColor(backgroundColor.getR(), backgroundColor.getG(),backgroundColor.getB(), backgroundColor.getA());
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void clearBackground(Colors color) {
        clearBackground(color.getColor());
    }

    public void shutdown() {
        glfwDestroyWindow(handle);
    }

    public long getHandle() {
        return handle;
    }
}
