package xyz.cacharrito.games.gameengine.core.ecs;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
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

    void destroyEntity(int entityId) {
        if (activeEntities.get(entityId)) {
            activeEntities.clear(entityId);
            freeEntityIds.add(entityId);
            entitySignatures.remove(entityId);
        }
    }

}
