package net.meowsers.Peach.Graphics;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("#type");

            for (String shaderStr : splitString) {
                if (shaderStr.trim().isEmpty()) continue;

                int eol = shaderStr.indexOf("\n");
                if (eol == -1) continue;

                String type = shaderStr.substring(0, eol).trim();
                String code = shaderStr.substring(eol + 1).trim();

                if (type.equalsIgnoreCase("vertex")) {
                    vertexSource = code;
                } else if (type.equalsIgnoreCase("fragment")) {
                    fragmentSource = code;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Could not open shader file: '" + filepath + "'");
        }
    }

    public Shader(String vertexSource, String fragmentSource) {
        this.filepath = "Inline Shader";
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
    }

    public void compile() {
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.err.println("ERROR: '" + filepath + "' Vertex shader compilation failed.");
            System.err.println(glGetShaderInfoLog(vertexID, len));
            return;
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.err.println("ERROR: '" + filepath + "' Fragment shader compilation failed.");
            System.err.println(glGetShaderInfoLog(fragmentID, len));
            return;
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.err.println("ERROR: '" + filepath + "' Shader linking failed.");
            System.err.println(glGetProgramInfoLog(shaderProgramID, len));
            return;
        }

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public void bind() {
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void unbind() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        bind();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        bind();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        bind();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        bind();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        bind();
        glUniform1i(varLocation, val);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        bind();
        glUniform1iv(varLocation, array);
    }

    public int getShaderProgramID() {
        return shaderProgramID;
    }
}