package net.meowsers.Peach.Graphics;

import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private static final int MAX_VERTICES = 8000;
    private static final int MAX_INDICES = 12000;

    private List<RenderBatch> batches;
    private Shader defaultShader;

    public Renderer(Shader defaultShader) {
        this.batches = new ArrayList<>();
        this.defaultShader = defaultShader;
    }

    public void submit(float[] vertices, int[] indices, int textureId, int zIndex) {
        int vertexCount = vertices.length / RenderBatch.INPUT_VERTEX_SIZE;

        if (indices == null) {
            indices = new int[(vertexCount - 2) * 3];
            int idx = 0;
            for (int i = 1; i < vertexCount - 1; i++) {
                indices[idx++] = 0;
                indices[idx++] = i;
                indices[idx++] = i + 1;
            }
        }

        int indexCount = indices.length;
        boolean added = false;

        for (RenderBatch batch : batches) {
            if (batch.getZIndex() == zIndex && batch.canFit(vertexCount, indexCount) && batch.canFitTexture(textureId)) {
                batch.push(vertices, indices, textureId);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_VERTICES, MAX_INDICES, zIndex, defaultShader);
            newBatch.start();
            batches.add(newBatch);
            newBatch.push(vertices, indices, textureId);
            Collections.sort(batches);
        }
    }

    public void submit(float[] vertices, int textureId, int zIndex) {
        submit(vertices, null, textureId, zIndex);
    }

    public void render(float windowWidth, float windowHeight) {
        Matrix4f projectionMatrix = new Matrix4f().ortho2D(0, windowWidth, windowHeight, 0);
        Matrix4f viewMatrix = new Matrix4f();

        for (RenderBatch batch : batches) {
            batch.render(projectionMatrix, viewMatrix);
            batch.clear();
        }
    }

    public void destroy() {
        batches.clear();
    }
}