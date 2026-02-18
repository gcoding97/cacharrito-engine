package xyz.cacharrito.games.gameengine.core;

import xyz.cacharrito.games.gameengine.core.ecs.Component;
import xyz.cacharrito.games.gameengine.core.ecs.GameSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RenderSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RequireComponents;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.middleware.Window;
import xyz.cacharrito.games.gameengine.core.middleware.properties.WindowProperties;

import static java.lang.System.nanoTime;

public class MainTestCore {

    private static final Double NS_PER_FRAME = 1_000_000_000d / 60d;
    private static final Integer VSYNC_BUFFER_INTERVAL = 1;
    private static final Float DESIRED_DELTA_TIME = 1f/60f;

    static void main() {
        var window = new Window(new WindowProperties("Cacharrito Engine", VSYNC_BUFFER_INTERVAL, 800, 600));
        window.init();

        var world = new World();
        world.addSystem(new MovementSystem(world));
        world.addSystem(new RenderRectangleSystem(world, window));

        int player = world.createEntity();
        world.addComponent(player, new Position(0, 0));
        world.addComponent(player, new Rectangle(1, 0, 0, 1, 250, 250));
        System.out.printf("Entity %s created%n", player);

        var delta = 0d;
        var start = nanoTime();
        var lastTime = start;
        var now = start;
        while(!window.shouldClose()) {
            now = nanoTime();
            delta += (now - lastTime);
            lastTime = now;

            window.pollInput();


            while(delta >= NS_PER_FRAME) {
                world.update(DESIRED_DELTA_TIME);
                delta -= NS_PER_FRAME;
            }
            window.prepareFrame();
            world.render(DESIRED_DELTA_TIME);
            window.endFrame();
        }
        var end = nanoTime();
        System.out.println("Game opened " + (end - start) / 1000000000 + " seconds");
    }

    public static class Position implements Component {
        public float x, y;
        public Position(float x, float y) { this.x = x; this.y = y; }
        @Override public String toString() { return "(x: %s, y: %s)".formatted(x, y); }
    }

    public record Rectangle(float r, float g, float b, float a, float width, float height) implements Component{}

    @RequireComponents(Position.class)
    public static class MovementSystem extends GameSystem {
        public MovementSystem(World world) { super(world); }

        @Override
        public void update(float delta) {
            forEachEntity(entityId -> {
                var pos = getWorld().getComponent(entityId, Position.class);
                pos.x += 10 * delta;
                pos.y += 5 * delta;

                if (pos.x % 1 < 0.1) {
                    System.out.println("Entidad " + entityId + " moviéndose -> " + pos);
                }
            });
        }
    }

    @RenderSystem
    @RequireComponents({Position.class, Rectangle.class})
    public static class RenderRectangleSystem extends GameSystem {
        private final Window window;

        public RenderRectangleSystem(World world, Window window) {
            super(world);
            this.window = window;
        }

        @Override
        public void update(float delta) {
            forEachEntity(entityId -> {
                var pos = getWorld().getComponent(entityId, Position.class);
                var rect = getWorld().getComponent(entityId, Rectangle.class);
                window.drawQuad(pos.x, pos.y, rect.width(), rect.height(), rect.r(), rect.g(), rect.b(), rect.a());
            });
        }
    }

}
