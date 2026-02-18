package xyz.cacharrito.games.gameengine.core.middleware;

import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFWErrorCallback;
import xyz.cacharrito.games.gameengine.core.middleware.properties.WindowProperties;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM_X11;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwInitHint;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glVertex2f;

@RequiredArgsConstructor
public class Window {

    private final WindowProperties props;

    private long window;

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11); // TODO: Choose platform dynamically
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to start GLFW");
        }
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        window = glfwCreateWindow(props.width(), props.height(), props.title(), 0, 0);
        if (window == 0) {
            throw new IllegalStateException("Unable to create the window");
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(props.vSyncInterval());
        createCapabilities();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, props.width(), props.height(), 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glfwSwapBuffers(window);
        glfwShowWindow(window);
        glfwFocusWindow(window);
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
        glColor4f(r, g, b, a);
        glBegin(GL_QUADS);
        glVertex2f(x, y);                   // Top Left
        glVertex2f(x + width, y);           // Top Right
        glVertex2f(x + width, y + height);  // Bottom Right
        glVertex2f(x, y + height);          // Bottom Left
        glEnd();
    }
}
