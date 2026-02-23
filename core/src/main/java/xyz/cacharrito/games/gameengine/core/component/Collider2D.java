package xyz.cacharrito.games.gameengine.core.component;

import xyz.cacharrito.games.gameengine.core.component.data.CollisionShape;
import xyz.cacharrito.games.gameengine.core.ecs.Component;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

public record Collider2D(CollisionShape shape, Vector2 localPosition, boolean isTrigger, int collisionLayer,
                         int collisionMask) implements Component {
}
