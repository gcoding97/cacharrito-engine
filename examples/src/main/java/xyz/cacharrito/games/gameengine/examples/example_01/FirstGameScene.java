package xyz.cacharrito.games.gameengine.examples.example_01;

import xyz.cacharrito.games.gameengine.core.Engine;
import xyz.cacharrito.games.gameengine.core.component.AffectedByDefaultKinematicSystem;
import xyz.cacharrito.games.gameengine.core.component.Collider2D;
import xyz.cacharrito.games.gameengine.core.component.InputSettings;
import xyz.cacharrito.games.gameengine.core.component.Transform2D;
import xyz.cacharrito.games.gameengine.core.component.Velocity2D;
import xyz.cacharrito.games.gameengine.core.component.data.CollisionShape;
import xyz.cacharrito.games.gameengine.core.component.data.RectangleCollisionShape;
import xyz.cacharrito.games.gameengine.core.component.input.InputAction;
import xyz.cacharrito.games.gameengine.core.component.input.InputEventListener;
import xyz.cacharrito.games.gameengine.core.component.input.InputKey;
import xyz.cacharrito.games.gameengine.core.ecs.Component;
import xyz.cacharrito.games.gameengine.core.ecs.GameSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RenderSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RequireComponents;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.graphics.Window;
import xyz.cacharrito.games.gameengine.core.math.Vector2;
import xyz.cacharrito.games.gameengine.core.scene.Scene;

import java.util.List;

public class FirstGameScene implements Scene {

    private World world;
    private int[] entities;

    @Override
    public void init(World world, Window window) {
        this.world = world;

        world.addSystem(new RenderRectangleSystem(world));
        world.addSystem(new BoundarySystem(world));

        entities = new int[2];
        int player = world.createEntity();
        world.addComponent(player, new KeepOnScreen());
        world.addComponent(player, new AffectedByDefaultKinematicSystem());
        world.addComponent(player, Transform2D.DEFAULT);
        world.addComponent(player, new RectangleSprite(new RectangleCollisionShape(250, 250), 0, 0, 1, 0, 1));
        world.addComponent(player, new Velocity2D(new Vector2(0, 0)));
        world.addComponent(player, new Collider2D(new RectangleCollisionShape(250, 250), Vector2.ZERO, false, 1, 2));
        world.addComponent(player, new InputSettings(List.of(
                new InputAction("moveUp", List.of(InputKey.KEY_W, InputKey.KEY_UP, InputKey.GAMEPAD_DPAD_UP)),
                new InputAction("moveDown", List.of(InputKey.KEY_S, InputKey.KEY_DOWN, InputKey.GAMEPAD_DPAD_DOWN)),
                new InputAction("moveLeft", List.of(InputKey.KEY_A, InputKey.KEY_LEFT, InputKey.GAMEPAD_DPAD_LEFT)),
                new InputAction("moveRight", List.of(InputKey.KEY_D, InputKey.KEY_RIGHT, InputKey.GAMEPAD_DPAD_RIGHT))),
                new PlayerInputEventListener(world, player, 10000)));
        entities[0] = player;
        System.out.printf("Entity %s created%n", player);

        int obstacle = world.createEntity();
        world.addComponent(obstacle, new Transform2D(new Vector2(window.getWidth() / 2f - 25, window.getHeight() / 2f - 25), 0, Vector2.ONE));
        world.addComponent(obstacle, new RectangleSprite(new RectangleCollisionShape(50, 50), 0, 1, 0, 1, 1));

        world.addComponent(obstacle, new Collider2D(new RectangleCollisionShape(50, 50), Vector2.ZERO, false, 2, 0));
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

    public record KeepOnScreen() implements Component {
    }

    public record RectangleSprite(CollisionShape rect, int renderZIndex, float r, float g, float b,
                                  float a) implements Component {
    }

    @RenderSystem
    @RequireComponents({Transform2D.class, RectangleSprite.class})
    public static class RenderRectangleSystem extends GameSystem {
        private final Window window;

        public RenderRectangleSystem(World world) {
            super(world);
            this.window = Engine.getMainWindow();
        }

        @Override
        public void update(int entity, float delta) {
            var transform2D = getWorld().getComponent(entity, Transform2D.class);
            var sprite = getWorld().getComponent(entity, RectangleSprite.class);
            window.drawQuad(transform2D.position().x(), transform2D.position().y(), sprite.renderZIndex(), sprite.rect().getArea().x(), sprite.rect().getArea().y(), sprite.r(), sprite.g(), sprite.b(), sprite.a());
        }
    }

    @RequireComponents({Transform2D.class, Collider2D.class, KeepOnScreen.class})
    public static class BoundarySystem extends GameSystem {

        private final Window window;

        public BoundarySystem(World world) {
            super(world);
            this.window = Engine.getMainWindow();
        }

        @Override
        public void update(int entity, float delta) {
            var transform2D = getWorld().getComponent(entity, Transform2D.class);
            var collider = getWorld().getComponent(entity, Collider2D.class);
            var windowWidth = window.getWidth();
            var windowHeight = window.getHeight();
            var changed = false;
            if (transform2D.position().x() < 0) {
                transform2D = new Transform2D(new Vector2(0, transform2D.position().y()), transform2D.rotation(), transform2D.scale());
                changed = true;
            } else if (transform2D.position().x() + collider.shape().getArea().x() > windowWidth) {
                transform2D = new Transform2D(new Vector2(windowWidth - collider.shape().getArea().x(), transform2D.position().y()), transform2D.rotation(), transform2D.scale());
                changed = true;
            }

            if (transform2D.position().y() < 0) {
                transform2D = new Transform2D(new Vector2(transform2D.position().x(), 0), transform2D.rotation(), transform2D.scale());
                changed = true;
            } else if (transform2D.position().y() + collider.shape().getArea().y() > windowHeight) {
                transform2D = new Transform2D(new Vector2(transform2D.position().x(), windowHeight - collider.shape().getArea().y()), transform2D.rotation(), transform2D.scale());
                changed = true;
            }

            if (changed) {
                getWorld().addComponent(entity, transform2D);
            }
        }
    }

    @SuppressWarnings("unused") // Used by ReflectionAPI
    public static class PlayerInputEventListener extends InputEventListener {

        private boolean velocityChanged = false;

        @Override
        public void handleLeftJoystickEvent(Vector2 axis, float delta) {
            var velocity = getWorld().getComponent(getEntity(), Velocity2D.class);
            var newVelocity = new Velocity2D(axis.multiply(speed).multiply(delta));
            getWorld().addComponent(getEntity(), newVelocity);
            velocityChanged = !newVelocity.value().equals(velocity.value());
        }

        @Override
        public void handleRightJoystickEvent(Vector2 axis, float delta) {
            if (velocityChanged) {
                return;
            }
            var velocity = getWorld().getComponent(getEntity(), Velocity2D.class);
            var newVelocity = new Velocity2D(axis.multiply(speed).multiply(delta));
            getWorld().addComponent(getEntity(), newVelocity);
            velocityChanged = !newVelocity.value().equals(velocity.value());
        }

        private final float speed;

        public PlayerInputEventListener(World world, int entity, float speed) {
            super(world, entity);
            this.speed = speed;
        }

        public void moveUp(Boolean isPressed, Float delta) {
            if (velocityChanged) {
                return;
            }
            var velocity = getWorld().getComponent(getEntity(), Velocity2D.class);
            if (isPressed && velocity.value().y() >= 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x(), velocity.value().y() - speed * delta)));
            } else if (!isPressed && velocity.value().y() < 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x(), velocity.value().y() + speed * delta)));
            }
        }

        public void moveDown(Boolean isPressed, Float delta) {
            if (velocityChanged) {
                return;
            }
            var velocity = getWorld().getComponent(getEntity(), Velocity2D.class);
            if (isPressed && velocity.value().y() <= 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x(), velocity.value().y() + speed * delta)));
            } else if (!isPressed && velocity.value().y() > 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x(), velocity.value().y() - speed * delta)));
            }
        }

        public void moveLeft(Boolean isPressed, Float delta) {
            if (velocityChanged) {
                return;
            }
            var velocity = getWorld().getComponent(getEntity(), Velocity2D.class);
            if (isPressed && velocity.value().x() >= 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x() - speed * delta, velocity.value().y())));
            } else if (!isPressed && velocity.value().x() < 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x() + speed * delta, velocity.value().y())));
            }
        }

        public void moveRight(Boolean isPressed, Float delta) {
            if (velocityChanged) {
                velocityChanged = false;
                return;
            }
            var velocity = getWorld().getComponent(getEntity(), Velocity2D.class);
            if (isPressed && velocity.value().x() <= 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x() + speed * delta, velocity.value().y())));
            } else if (!isPressed && velocity.value().x() > 0) {
                getWorld().addComponent(getEntity(), new Velocity2D(new Vector2(velocity.value().x() - speed * delta, velocity.value().y())));
            }
            velocityChanged = false;
        }
    }

}
