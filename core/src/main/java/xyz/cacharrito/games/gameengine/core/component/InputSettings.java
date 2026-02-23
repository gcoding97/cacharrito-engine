package xyz.cacharrito.games.gameengine.core.component;

import xyz.cacharrito.games.gameengine.core.component.input.InputAction;
import xyz.cacharrito.games.gameengine.core.component.input.InputEventListener;
import xyz.cacharrito.games.gameengine.core.ecs.Component;

import java.util.List;

public record InputSettings(List<InputAction> actionList, InputEventListener eventListener) implements Component {
}
