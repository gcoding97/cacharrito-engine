package xyz.cacharrito.games.gameengine.core;

import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.middleware.Window;
import xyz.cacharrito.games.gameengine.core.middleware.properties.WindowProperties;
import xyz.cacharrito.games.gameengine.core.scene.Scene;

import static java.lang.System.nanoTime;

public class Engine {

    private static final float NS_PER_FRAME = 1_000_000_000f / 60f;
    private static final Integer VSYNC_BUFFER_INTERVAL = 1;
    private static final float DESIRED_DELTA_TIME = 1f / 60f;
    private static Scene currentScene;

    public static void startGame(Scene firstScene) {
        currentScene = firstScene;

        var window = new Window(new WindowProperties("Cacharrito Engine", VSYNC_BUFFER_INTERVAL, 800, 600));
        window.init();

        var world = new World();
        currentScene.init(world, window);

        var fixedDelta = 0f;
        var delta = 0f;
        var start = nanoTime();
        var lastTime = start;
        var now = start;
        while (!window.shouldClose()) {
            now = nanoTime();
            fixedDelta += (now - lastTime);
            delta = now - lastTime;
            lastTime = now;

            window.pollInput();

            while (fixedDelta >= NS_PER_FRAME) {
                currentScene.fixedUpdate(DESIRED_DELTA_TIME);
                fixedDelta -= NS_PER_FRAME;
            }
            window.prepareFrame();
            currentScene.update(delta / 1_000_000_000f);
            window.endFrame();
        }
        currentScene.dispose();
    }


}
