package xyz.cacharrito.games.gameengine.core.ecs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class EntityManager {

    private int nextEntityId;
    private final Queue<Integer> freeEntityIds;
    private final BitSet activeEntities;
    private final Map<Integer, BitSet> entitySignatures;

    EntityManager() {
        nextEntityId = 1;
        freeEntityIds = new ArrayDeque<>();
        activeEntities = new BitSet();
        entitySignatures = new HashMap<>();
    }

    int createEntity() {
        int id;
        if (!freeEntityIds.isEmpty()) {
            id = freeEntityIds.poll();
        } else {
            id = nextEntityId++;
        }
        activeEntities.set(id, true);
        entitySignatures.put(id, new BitSet());
        return id;
    }

    void setEntitySignature(int entityId, int componentId) {
        entitySignatures.computeIfPresent(entityId, (_, signature) -> {
            signature.set(componentId);
            return signature;
        });
    }

    BitSet getEntitySignature(int entityId) {
        return entitySignatures.get(entityId);
    }

    int[] getEntitiesWithSignature(BitSet wantedSignature) {
        List<Integer> entities = new ArrayList<>();
        for (int i = activeEntities.nextSetBit(0); i >= 0; i = activeEntities.nextSetBit(i + 1)) {
            var controlBitSet = new BitSet();
            controlBitSet.or(wantedSignature);
            controlBitSet.and(entitySignatures.get(i));
            if (controlBitSet.equals(wantedSignature)) {
                entities.add(i);
            }
        }
        return entities.stream().mapToInt(Integer::intValue).toArray();
    }

    void destroyEntity(int entityId) {
        if (activeEntities.get(entityId)) {
            activeEntities.clear(entityId);
            freeEntityIds.add(entityId);
            entitySignatures.remove(entityId);
        }
    }

}
