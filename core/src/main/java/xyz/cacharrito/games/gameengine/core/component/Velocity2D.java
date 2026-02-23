package xyz.cacharrito.games.gameengine.core.component;

import xyz.cacharrito.games.gameengine.core.ecs.Component;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

public record Velocity2D(Vector2 value) implements Component {
}
