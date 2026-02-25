package xyz.cacharrito.games.gameengine.core.graphics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import xyz.cacharrito.games.gameengine.core.graphics.properties.WindowProperties;

import java.nio.FloatBuffer;
import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM_X11;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwInitHint;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL46.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL46.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.GL_FLOAT;
import static org.lwjgl.opengl.GL46.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL46.GL_TRIANGLES;
import static org.lwjgl.opengl.GL46.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL46.glBindBuffer;
import static org.lwjgl.opengl.GL46.glBindVertexArray;
import static org.lwjgl.opengl.GL46.glBufferData;
import static org.lwjgl.opengl.GL46.glClear;
import static org.lwjgl.opengl.GL46.glClearColor;
import static org.lwjgl.opengl.GL46.glDeleteBuffers;
import static org.lwjgl.opengl.GL46.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL46.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL46.glGenBuffers;
import static org.lwjgl.opengl.GL46.glGenVertexArrays;
import static org.lwjgl.opengl.GL46.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL46.glVertexAttribPointer;
import static org.lwjgl.opengl.GL46.glViewport;

@RequiredArgsConstructor
public class Window {

    private static final int MAX_INSTANCES = 10000;
    private static final int INSTANCE_DATA_SIZE = 20; // 16 (mat4) + 4 (vec4)

    private final WindowProperties props;

    @Getter
    private long window;

    @Getter
    private int width;

    @Getter
    private int height;

    private final Matrix4f projectionMatrix = new Matrix4f();
    private ShaderProgram shaderProgram;
    private final FloatBuffer instanceDataBuffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_SIZE);
    private final QuadInstance[] quadPool = new QuadInstance[MAX_INSTANCES];
    private final int[] indexes = new int[MAX_INSTANCES];
    private int vaoId;
    private int vertexVboId;
    private int instanceVboId;
    private int eboId;
    private int instanceCount = 0;

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11); // TODO: Choose platform dynamically
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to start GLFW");
        }
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        window = glfwCreateWindow(props.width(), props.height(), props.title(), 0, 0);
        width = props.width();
        height = props.height();
        if (window == 0) {
            throw new IllegalStateException("Unable to create the window");
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(props.vSyncInterval());
        createCapabilities();
        createVao();
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glfwSwapBuffers(window);
        glfwShowWindow(window);
        glfwFocusWindow(window);
        glfwSetWindowSizeCallback(window, (_, width, height) -> {
            this.width = width;
            this.height = height;
            glViewport(0, 0, width, height);
            projectionMatrix.setOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
        });

        for (int i = 0; i < MAX_INSTANCES; i++) {
            quadPool[i] = new QuadInstance();
            indexes[i] = i;
        }
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void prepareFrame() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void endFrame() {
        flushBatch();
        glfwSwapBuffers(window);
    }

    public void pollInput() {
        glfwPollEvents();
    }

    public void drawQuad(float x, float y, int z, float width, float height, float r, float g, float b, float a) {
        if (instanceCount >= MAX_INSTANCES) {
            flushBatch();
        }

        var quad = quadPool[instanceCount++];
        quad.z = z;
        quad.transform.identity().translate(x, y, z).scale(width, height, 1);
        quad.r = r;
        quad.g = g;
        quad.b = b;
        quad.a = a;
    }

    public void flushBatch() {
        if (instanceCount == 0) return;
        for (int i = 0; i < instanceCount; i++) {
            indexes[i] = i;
        }
        quickSort(0, instanceCount - 1);
        IntStream.range(0, instanceCount).forEach(i -> {
            var quad = quadPool[indexes[i]];
            int offset = i * INSTANCE_DATA_SIZE;
            quad.transform.get(offset, instanceDataBuffer);
            instanceDataBuffer.put(offset + 16, quad.r);
            instanceDataBuffer.put(offset + 17, quad.g);
            instanceDataBuffer.put(offset + 18, quad.b);
            instanceDataBuffer.put(offset + 19, quad.a);
        });
        instanceDataBuffer.position(0);
        instanceDataBuffer.limit(instanceCount * INSTANCE_DATA_SIZE);

        shaderProgram.bind();
        shaderProgram.setUniform("uProjection", projectionMatrix);
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, instanceDataBuffer.limit(instanceCount * INSTANCE_DATA_SIZE));
        glBindVertexArray(vaoId);
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, instanceCount);
        glBindVertexArray(0);
        shaderProgram.unbind();

        instanceDataBuffer.clear();
        instanceCount = 0;
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public boolean isMouseButtonPressed(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vertexVboId);
        glDeleteBuffers(instanceVboId);
        glDeleteBuffers(eboId);
        shaderProgram.cleanup();
    }

    private void createVao() {
        shaderProgram = new ShaderProgram("default.vert", "default.frag");

        var vertex = new float[]{
                0, 0,
                0, 1,
                1, 1,
                1, 0,
        };

        var index = new int[]{
                0, 1, 2,
                2, 3, 0
        };

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        vertexVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        instanceVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboId);
        int stride = 80;
        glBufferData(GL_ARRAY_BUFFER, MAX_INSTANCES * INSTANCE_DATA_SIZE * 4, GL_DYNAMIC_DRAW);
        for (int i = 0; i < 4; i++) {
            glEnableVertexAttribArray(1 + i);
            glVertexAttribPointer(1 + i, 4, GL_FLOAT, false, stride, (long) i * 16);
            glVertexAttribDivisor(i + 1, 1);
        }
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, stride, 64);
        glVertexAttribDivisor(5, 1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        projectionMatrix.setOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivotZ = quadPool[indexes[high]].z;
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            int currentZ = quadPool[indexes[j]].z;
            int currentOriginalIndex = indexes[j];
            if (currentZ > pivotZ || (currentZ == pivotZ && currentOriginalIndex < indexes[high])) {
                i++;
                int temp = indexes[i];
                indexes[i] = indexes[j];
                indexes[j] = temp;
            }
        }

        int temp = indexes[i + 1];
        indexes[i + 1] = indexes[high];
        indexes[high] = temp;

        return i + 1;
    }

    private static class QuadInstance {
        final Matrix4f transform = new Matrix4f();
        float r;
        float g;
        float b;
        float a;
        int z;
    }
}
