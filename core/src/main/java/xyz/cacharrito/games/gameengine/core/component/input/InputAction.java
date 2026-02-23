package xyz.cacharrito.games.gameengine.core.component.input;

import java.util.List;

public record InputAction(String name, List<InputKey> linkedKeyList) {
}
