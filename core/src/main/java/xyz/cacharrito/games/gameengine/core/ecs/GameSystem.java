package xyz.cacharrito.games.gameengine.core.ecs;

import lombok.Getter;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;
import java.util.function.IntConsumer;

public abstract class GameSystem {

    @Getter
    final World world;
    final BitSet signature;
    private final BitSet entities;

    public GameSystem(World world) {
        this.world = world;
        this.signature = new BitSet();
        this.entities = new BitSet();

        var requireComponents = getClass().getAnnotation(RequireComponents.class);
        if (Objects.isNull(requireComponents)) {
            return;
        }
        Arrays.stream(requireComponents.value()).sequential()
                .forEach(required -> signature.set(world.getComponentId(required)));
    }

    void onEntitySignatureChanged(int entityId, BitSet entitySignature) {
        boolean interested = true;
        for (int i = signature.nextSetBit(0); i >= 0; i = signature.nextSetBit(i+1)) {
            if (!entitySignature.get(i)) {
                interested = false;
                break;
            }
        }
        boolean isMember = entities.get(entityId);

        if (interested && !isMember) {
            entities.set(entityId);
        } else if (!interested && isMember) {
            entities.clear(entityId);
        }
    }

    public void forEachEntity(IntConsumer action) {
        for (int i = entities.nextSetBit(0); i >= 0; i = entities.nextSetBit(i + 1)) {
            action.accept(i);
        }
    }

    public abstract void update(float delta);

}
