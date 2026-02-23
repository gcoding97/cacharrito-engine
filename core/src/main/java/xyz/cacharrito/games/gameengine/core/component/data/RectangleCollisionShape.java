package xyz.cacharrito.games.gameengine.core.component.data;

import xyz.cacharrito.games.gameengine.core.math.Vector2;

public record RectangleCollisionShape(float width, float height) implements CollisionShape {

    @Override
    public Vector2 getArea() {
        return new Vector2(width, height);
    }
}
