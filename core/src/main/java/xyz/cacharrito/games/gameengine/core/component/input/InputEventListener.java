package xyz.cacharrito.games.gameengine.core.component.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

@RequiredArgsConstructor
public abstract class InputEventListener {

    @Getter
    private final World world;

    @Getter
    private final int entity;

    public void handleLeftJoystickEvent(Vector2 axis, float delta) {
    }

    public void handleRightJoystickEvent(Vector2 axis, float delta) {
    }

    public void handleMouseMotionEvent(Vector2 axis) {
    }

}
