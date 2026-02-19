package xyz.cacharrito.games.gameengine.examples.example_01;

import xyz.cacharrito.games.gameengine.core.ecs.Component;
import xyz.cacharrito.games.gameengine.core.ecs.GameSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RenderSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RequireComponents;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.middleware.Window;
import xyz.cacharrito.games.gameengine.core.scene.Scene;

import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class FirstGameScene implements Scene {

    private World world;
    private int[] entities;

    @Override
    public void init(World world, Window window) {
        this.world = world;

        world.addSystem(new RenderRectangleSystem(world, window));
        world.addSystem(new MovementSystem(world));
        world.addSystem(new InputSystem(world, window, 150));
        world.addSystem(new BoundarySystem(world, window));
        world.addSystem(new CollisionSystem(world));

        entities = new int[2];
        int player = world.createEntity();
        world.addComponent(player, new KeepOnScreen());
        world.addComponent(player, new PlayerControlled());
        world.addComponent(player, new Position(0, 0));
        world.addComponent(player, new RectangleSprite(new Rectangle(250, 250), 0, 1, 0, 1));
        world.addComponent(player, new Velocity(0, 0));
        world.addComponent(player, new Collider(new Rectangle(250, 250), new Position(0, 0), false));
        entities[0] = player;
        System.out.printf("Entity %s created%n", player);

        int obstacle = world.createEntity();
        world.addComponent(obstacle, new Position(window.getWidth() / 2f - 25, window.getHeight() / 2f - 25));
        world.addComponent(obstacle, new RectangleSprite(new Rectangle(50, 50), 1, 0, 1, 1));
        world.addComponent(obstacle, new Collider(new Rectangle(50, 50), new Position(0, 0), false));
        entities[1] = obstacle;
        System.out.printf("Entity %s created%n", obstacle);
    }

    @Override
    public void fixedUpdate(float delta) {
        world.update(delta);
    }

    @Override
    public void update(float delta) {
        world.render(delta);
    }

    @Override
    public void dispose() {
        world.destroyEntity(entities[1]);
        world.destroyEntity(entities[0]);
    }

    public record Position(float x, float y) implements Component {
    }

    public record PlayerControlled() implements Component {
    }

    public record KeepOnScreen() implements Component {
    }

    public record Velocity(float x, float y) implements Component {
    }

    public record Rectangle(float width, float height) implements Component {
    }

    public record RectangleSprite(Rectangle rect, float r, float g, float b, float a) implements Component {
    }

    public record Collider(Rectangle rect, Position localPosition, boolean isTrigger) implements Component {
    }

    @RequireComponents({Position.class, Velocity.class})
    public static class MovementSystem extends GameSystem {
        public MovementSystem(World world) {
            super(world);
        }

        @Override
        public void update(int entity, float delta) {
            var pos = getWorld().getComponent(entity, Position.class);
            var velocity = getWorld().getComponent(entity, Velocity.class);
            var newPos = new Position(pos.x() + velocity.x() * delta, pos.y() + velocity.y() * delta);
            getWorld().addComponent(entity, newPos);
        }
    }

    @RenderSystem
    @RequireComponents({Position.class, RectangleSprite.class})
    public static class RenderRectangleSystem extends GameSystem {
        private final Window window;

        public RenderRectangleSystem(World world, Window window) {
            super(world);
            this.window = window;
        }

        @Override
        public void update(int entity, float delta) {
            var pos = getWorld().getComponent(entity, Position.class);
            var sprite = getWorld().getComponent(entity, RectangleSprite.class);
            window.drawQuad(pos.x(), pos.y(), sprite.rect().width(), sprite.rect().height(), sprite.r(), sprite.g(), sprite.b(), sprite.a());
        }
    }

    @RequireComponents({PlayerControlled.class, Velocity.class})
    public static class InputSystem extends GameSystem {

        private final Window window;
        private final float speed;

        public InputSystem(World world, Window window, float speed) {
            super(world);
            this.window = window;
            this.speed = speed;
        }

        @Override
        public void update(int entity, float delta) {
            float x = 0;
            float y = 0;

            if (window.isKeyPressed(GLFW_KEY_W)) y -= speed;
            if (window.isKeyPressed(GLFW_KEY_S)) y += speed;
            if (window.isKeyPressed(GLFW_KEY_A)) x -= speed;
            if (window.isKeyPressed(GLFW_KEY_D)) x += speed;

            getWorld().addComponent(entity, new Velocity(x, y));
        }
    }

    @RequireComponents({Collider.class, KeepOnScreen.class})
    public static class BoundarySystem extends GameSystem {

        private final Window window;

        public BoundarySystem(World world, Window window) {
            super(world);
            this.window = window;
        }

        @Override
        public void update(int entity, float delta) {
            var position = getWorld().getComponent(entity, Position.class);
            var collider = getWorld().getComponent(entity, Collider.class);
            var windowWidth = window.getWidth();
            var windowHeight = window.getHeight();
            var changed = false;
            if (position.x() < 0) {
                position = new Position(0, position.y());
                changed = true;
            } else if (position.x() + collider.rect().width() > windowWidth) {
                position = new Position(windowWidth - collider.rect().width(), position.y());
                changed = true;
            }

            if (position.y() < 0) {
                position = new Position(position.x(), 0);
                changed = true;
            } else if (position.y() + collider.rect().height() > windowHeight) {
                position = new Position(position.x(), windowHeight - collider.rect().height());
                changed = true;
            }

            if (changed) {
                getWorld().addComponent(entity, position);
            }
        }
    }

    @RequireComponents({Collider.class, PlayerControlled.class, Position.class})
    public static class CollisionSystem extends GameSystem {
        public CollisionSystem(World world) {
            super(world);
        }

        private boolean isColliding(Collider oneCol, Position onePos, Collider otherCol, Position otherPos) {
            return onePos.x() < otherPos.x() + otherCol.rect().width() &&
                    onePos.x() + oneCol.rect().width() > otherPos.x() &&
                    onePos.y() < otherPos.y() + otherCol.rect().height() &&
                    onePos.y() + oneCol.rect().height() > otherPos.y();
        }

        @Override
        public void update(int entity, float delta) {
            var collider = getWorld().getComponent(entity, Collider.class);
            var position = getWorld().getComponent(entity, Position.class);
            var colliding = new boolean[1];
            Arrays.stream(getWorld().getEntititesWithComponents(Collider.class, Position.class))
                    .forEach(other -> {
                        if (entity == other) {
                            return;
                        }
                        var otherCollider = getWorld().getComponent(other, Collider.class);
                        var otherPosition = getWorld().getComponent(other, Position.class);
                        colliding[0] = isColliding(collider, position, otherCollider, otherPosition);
                        if (collider.isTrigger() || otherCollider.isTrigger()) {
                            return;
                        }
                        var newEntityPosition = resolveCollision(collider, position, otherCollider, otherPosition);
                        if (Objects.isNull(newEntityPosition)) {
                            return;
                        }
                        getWorld().addComponent(entity, newEntityPosition);
                    });
            if (colliding[0] && collider.isTrigger()) {
                getWorld().addComponent(entity, new RectangleSprite(collider.rect, 1, 0, 0, 1));
            } else if (collider.isTrigger()) {
                getWorld().addComponent(entity, new RectangleSprite(collider.rect, 0, 1, 0, 1));
            }
        }

        private Position resolveCollision(Collider oneCol, Position onePos, Collider otherCol, Position otherPos) {
            float oneCenterX = onePos.x() + (oneCol.rect().width() / 2f);
            float oneCenterY = onePos.y() + (oneCol.rect().height() / 2f);
            float otherCenterX = otherPos.x() + (otherCol.rect().width() / 2f);
            float otherCenterY = otherPos.y() + (otherCol.rect().height() / 2f);

            float centerDistanceX = oneCenterX - otherCenterX;
            float centerDistanceY = oneCenterY - otherCenterY;

            float minDistanceX = (oneCol.rect().width() / 2f) + (otherCol.rect().width() / 2f);
            float minDistanceY = (oneCol.rect().height() / 2f) + (otherCol.rect().height() / 2f);

            float overlapX = minDistanceX - Math.abs(centerDistanceX);
            float overlapY = minDistanceY - Math.abs(centerDistanceY);

            if (overlapX > 0 && overlapY > 0) {
                float newX = onePos.x();
                float newY = onePos.y();
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
                return new Position(newX, newY);
            }
            return null;
        }
    }
}
