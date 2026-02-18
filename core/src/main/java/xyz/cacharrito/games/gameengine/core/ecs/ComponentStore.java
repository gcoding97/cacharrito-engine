package xyz.cacharrito.games.gameengine.core.ecs;

import java.util.Arrays;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class ComponentStore<T extends Component> {

    private static final int DEFAULT_SIZE = 1000;

    private int[] sparseEntities;
    private int[] denseEntities;
    private T[] components;
    private int currentSize;

    ComponentStore() {
        sparseEntities = initializeIntArray();
        denseEntities = initializeIntArray();
        components = (T[]) new Component[DEFAULT_SIZE];
        currentSize = 0;
    }

    void add(int entityId, T component) {
        if (entityId >= sparseEntities.length) {
            sparseEntities = resizeIntArray(sparseEntities, entityId + DEFAULT_SIZE);
        }
        sparseEntities[entityId] = currentSize;
        if (currentSize >= components.length) {
            denseEntities = resizeIntArray(denseEntities, currentSize + DEFAULT_SIZE);
            components = Arrays.copyOf(components, currentSize + DEFAULT_SIZE);
        }
        denseEntities[currentSize] = entityId;
        components[currentSize++] = component;
    }

    void remove(int entityId) {
        var entityIndex = sparseEntities[entityId];
        if (entityIndex == -1) {
            return;
        }
        denseEntities[entityIndex] = denseEntities[currentSize - 1];
        components[entityIndex] = components[currentSize - 1];
        sparseEntities[denseEntities[entityIndex]] = entityIndex;
        sparseEntities[entityId] = -1;
        denseEntities[currentSize - 1] = -1;
        components[currentSize - 1] = null;
        currentSize--;
    }

    T getForEntity(int entity) {
        if (entity < 0 || entity >= sparseEntities.length) {
            return null;
        }
        var entityIndex = sparseEntities[entity];
        if (entityIndex < 0) {
            return null;
        }
        return components[entityIndex];
    }

    private int[] resizeIntArray(int[] original, int newSize) {
        return IntStream.range(0, newSize)
                .map(i -> i < original.length ? original[i] : -1)
                .toArray();
    }

    private int[] initializeIntArray() {
        return IntStream.range(0, DEFAULT_SIZE)
                .map(_ -> -1)
                .toArray();
    }
}
