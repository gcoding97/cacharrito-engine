package xyz.cacharrito.games.gameengine.core.graphics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import xyz.cacharrito.games.gameengine.core.graphics.properties.WindowProperties;

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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

@RequiredArgsConstructor
public class Window {

    private final WindowProperties props;

    @Getter
    private long window;

    @Getter
    private int width;

    @Getter
    private int height;

    private final Matrix4f projectionMatrix = new Matrix4f();
    private ShaderProgram shaderProgram;
    private int vaoId, vboId, eboId;

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
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void prepareFrame() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void endFrame() {
        glfwSwapBuffers(window);
    }

    public void pollInput() {
        glfwPollEvents();
    }

    public void drawQuad(float x, float y, float width, float height, float r, float g, float b, float a) {
        shaderProgram.bind();

        var transform = new Matrix4f()
                .translate(x, y, 0)
                .scale(width, height, 1);
        shaderProgram.setUniform("uProjection", projectionMatrix);
        shaderProgram.setUniform("uTransform", transform);
        shaderProgram.setUniform("uColor", r, g, b, a);
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public boolean isMouseButtonPressed(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
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
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        projectionMatrix.setOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
    }
}
