package xyz.cacharrito.games.gameengine.core.system;

import xyz.cacharrito.games.gameengine.core.component.Collider2D;
import xyz.cacharrito.games.gameengine.core.component.Transform2D;
import xyz.cacharrito.games.gameengine.core.ecs.GameSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RequireComponents;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

import java.util.Arrays;
import java.util.Objects;

@RequireComponents({Collider2D.class, Transform2D.class})
public class CollisionSystem2D extends GameSystem {

    public CollisionSystem2D(World world) {
        super(world);
    }

    @Override
    public void update(int entity, float delta) {
        var collider = getWorld().getComponent(entity, Collider2D.class);
        var transform2D = getWorld().getComponent(entity, Transform2D.class);
        Arrays.stream(getWorld().getEntititesWithComponents(Collider2D.class, Transform2D.class))
                .forEach(other -> {
                    if (entity == other) {
                        return;
                    }
                    var otherCollider = getWorld().getComponent(other, Collider2D.class);
                    var otherTransform = getWorld().getComponent(other, Transform2D.class);
                    var intersects = intersects(collider, transform2D, otherCollider, otherTransform);
                    // TODO: Make triggering works
                    if (!intersects || collider.isTrigger() || otherCollider.isTrigger()) {
                        return;
                    }
                    var newEntityPosition = resolveCollision(collider, transform2D, otherCollider, otherTransform);
                    if (Objects.isNull(newEntityPosition)) {
                        return;
                    }
                    getWorld().addComponent(entity, new Transform2D(newEntityPosition, transform2D.rotation(), transform2D.scale()));
                });
    }

    private boolean intersects(Collider2D one, Transform2D oneTransform, Collider2D other, Transform2D otherTransform) {
        var canCollide = (one.collisionMask() & other.collisionLayer()) != 0; // We just check if this can collide with the other, not if the other can collide with this
        return canCollide &&
                oneTransform.position().x() + one.localPosition().x() < otherTransform.position().x() + other.localPosition().x() + other.shape().getArea().x() &&
                oneTransform.position().x() + one.localPosition().x() + one.shape().getArea().x() > otherTransform.position().x() + other.localPosition().x() &&
                oneTransform.position().y() + one.localPosition().y() < otherTransform.position().y() + other.localPosition().y() + other.shape().getArea().y() &&
                oneTransform.position().y() + one.localPosition().y() + one.shape().getArea().y() > otherTransform.position().y() + other.localPosition().y();
    }

    private Vector2 resolveCollision(Collider2D one, Transform2D thisTransform, Collider2D other, Transform2D otherTransform) {
        var canCollide = (one.collisionMask() & other.collisionLayer()) != 0; // We just check if this can collide with the other, not if the other can collide with this
        if (!canCollide) {
            return null;
        }
        float oneCenterX = thisTransform.position().x() + one.localPosition().x() + (one.shape().getArea().x() / 2f);
        float oneCenterY = thisTransform.position().y() + one.localPosition().y() + (one.shape().getArea().y() / 2f);
        float otherCenterX = otherTransform.position().x() + other.localPosition().x() + (other.shape().getArea().x() / 2f);
        float otherCenterY = otherTransform.position().y() + other.localPosition().y() + (other.shape().getArea().y() / 2f);

        float centerDistanceX = oneCenterX - otherCenterX;
        float centerDistanceY = oneCenterY - otherCenterY;

        float minDistanceX = (one.shape().getArea().x() / 2f) + (other.shape().getArea().x() / 2f);
        float minDistanceY = (one.shape().getArea().y() / 2f) + (other.shape().getArea().y() / 2f);

        float overlapX = minDistanceX - Math.abs(centerDistanceX);
        float overlapY = minDistanceY - Math.abs(centerDistanceY);

        if (overlapX > 0 && overlapY > 0) {
            float newX = thisTransform.position().x();
            float newY = thisTransform.position().y();
            if (overlapX < overlapY) {
                if (centerDistanceX > 0) {
                    newX += overlapX;
                } else {
                    newX -= overlapX;
                }
            } else {
                if (centerDistanceY > 0) {
                    newY += overlapY;
                } else {
                    newY -= overlapY;
                }
            }
            return new Vector2(newX, newY);
        }
        return null;
    }

}
