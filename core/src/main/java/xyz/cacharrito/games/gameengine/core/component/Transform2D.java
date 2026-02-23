package xyz.cacharrito.games.gameengine.core.component;

import xyz.cacharrito.games.gameengine.core.ecs.Component;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

public record Transform2D(Vector2 position, float rotation, Vector2 scale) implements Component {

    public static final Transform2D DEFAULT = new Transform2D(Vector2.ZERO, 0, Vector2.ONE);


}
