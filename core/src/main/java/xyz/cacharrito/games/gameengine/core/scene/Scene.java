package xyz.cacharrito.games.gameengine.core.scene;

import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.middleware.Window;

public interface Scene {
    void init(World world, Window window);

    void fixedUpdate(float delta);

    void update(float delta);

    void dispose();
}
