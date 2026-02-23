package xyz.cacharrito.games.gameengine.core.system;

import xyz.cacharrito.games.gameengine.core.component.AffectedByDefaultKinematicSystem;
import xyz.cacharrito.games.gameengine.core.component.Transform2D;
import xyz.cacharrito.games.gameengine.core.component.Velocity2D;
import xyz.cacharrito.games.gameengine.core.ecs.GameSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RequireComponents;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

@RequireComponents({Transform2D.class, Velocity2D.class, AffectedByDefaultKinematicSystem.class})
public class KinematicSystem2D extends GameSystem {
    public KinematicSystem2D(World world) {
        super(world);
    }

    @Override
    public void update(int entity, float delta) {
        var transform2D = getWorld().getComponent(entity, Transform2D.class);
        var velocity = getWorld().getComponent(entity, Velocity2D.class);
        var newPos = new Transform2D(new Vector2(transform2D.position().x() + velocity.value().x() * delta, transform2D.position().y() + velocity.value().y() * delta), transform2D.rotation(), transform2D.scale());
        getWorld().addComponent(entity, newPos);
    }
}
