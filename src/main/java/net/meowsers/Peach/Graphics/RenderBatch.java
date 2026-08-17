package net.meowsers.Peach.Graphics;

import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderBatch implements Comparable<RenderBatch> {

    public static final int INPUT_VERTEX_SIZE = 8;
    private static final int VERTEX_SIZE = 9;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private float[] vertices;
    private int[] indices;
    private int numVertices = 0;
    private int numIndices = 0;
    private int maxVertices;
    private int maxIndices;

    private List<Integer> textureIds;
    private int zIndex;

    private int vaoID, vboID, eboID;
    private Shader shader;

    public RenderBatch(int maxVertices, int maxIndices, int zIndex, Shader shader) {
        this.zIndex = zIndex;
        this.shader = shader;
        this.maxVertices = maxVertices;
        this.maxIndices = maxIndices;
        this.vertices = new float[this.maxVertices * VERTEX_SIZE];
        this.indices = new int[this.maxIndices];
        this.textureIds = new ArrayList<>();
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indices.length * Integer.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE_BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, VERTEX_SIZE_BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, VERTEX_SIZE_BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, 1, GL_FLOAT, false, VERTEX_SIZE_BYTES, 8 * Float.BYTES);
        glEnableVertexAttribArray(3);

        glBindVertexArray(0);
    }

    public void push(float[] rawVertices, int[] rawIndices, int textureId) {
        int vertexCount = rawVertices.length / INPUT_VERTEX_SIZE;
        int texSlot = 0;

        if (textureId > 0) {
            if (!textureIds.contains(textureId)) {
                textureIds.add(textureId);
            }
            texSlot = textureIds.indexOf(textureId) + 1;
        }

        int offset = this.numVertices * VERTEX_SIZE;
        for (int i = 0; i < vertexCount; i++) {
            int inOffset = i * INPUT_VERTEX_SIZE;

            this.vertices[offset++] = rawVertices[inOffset];
            this.vertices[offset++] = rawVertices[inOffset + 1];
            this.vertices[offset++] = rawVertices[inOffset + 2];
            this.vertices[offset++] = rawVertices[inOffset + 3];
            this.vertices[offset++] = rawVertices[inOffset + 4];
            this.vertices[offset++] = rawVertices[inOffset + 5];
            this.vertices[offset++] = rawVertices[inOffset + 6];
            this.vertices[offset++] = rawVertices[inOffset + 7];
            this.vertices[offset++] = texSlot;
        }

        int indexOffset = this.numVertices;
        for (int i = 0; i < rawIndices.length; i++) {
            this.indices[this.numIndices + i] = rawIndices[i] + indexOffset;
        }

        this.numVertices += vertexCount;
        this.numIndices += rawIndices.length;
    }

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        if (numVertices == 0) return;

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indices);

        shader.bind();
        if (projectionMatrix != null) shader.uploadMat4f("uProjection", projectionMatrix);
        if (viewMatrix != null) shader.uploadMat4f("uView", viewMatrix);

        // Bind active textures and map unused shader sampler slots to a safe fallback unit
        int activeCount = textureIds.size();
        int[] uploadSlots = new int[8];
        for (int i = 0; i < 8; i++) {
            if (i < activeCount) {
                glActiveTexture(GL_TEXTURE0 + i + 1);
                glBindTexture(GL_TEXTURE_2D, textureIds.get(i));
                uploadSlots[i] = i + 1;
            } else {
                uploadSlots[i] = activeCount > 0 ? 1 : 0;
            }
        }
        shader.uploadIntArray("uTextures", uploadSlots);

        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        for (int i = 0; i < activeCount; i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        shader.unbind();
    }

    public void clear() {
        numVertices = 0;
        numIndices = 0;
        textureIds.clear();
    }

    public boolean canFit(int vertexCount, int indexCount) {
        return (this.numVertices + vertexCount <= maxVertices) && (this.numIndices + indexCount <= maxIndices);
    }

    public boolean canFitTexture(int textureId) {
        return textureId <= 0 || textureIds.contains(textureId) || textureIds.size() < 7;
    }

    public int getZIndex() { return this.zIndex; }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.getZIndex());
    }
}