package xyz.cacharrito.games.gameengine.core.ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class World {

    private final Map<Class<? extends Component>, ComponentStore<? extends Component>> componentRegistry;
    private final Map<Class<? extends Component>, Integer> componentIdMap;
    private final List<GameSystem> logicSystemList;
    private final List<GameSystem> renderSystemList;
    private final EntityManager entityManager;

    private int nextComponentId;

    public World() {
        componentRegistry = new HashMap<>();
        componentIdMap = new HashMap<>();
        logicSystemList = new ArrayList<>();
        renderSystemList = new ArrayList<>();
        entityManager = new EntityManager();
        nextComponentId = 0;
    }

    public int createEntity() {
        return entityManager.createEntity();
    }

    public void destroyEntity(int entityId) {
        componentRegistry.forEach((_, store) -> store.remove(entityId));
        entityManager.destroyEntity(entityId);
        notifySystems(entityId, new BitSet());
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> void addComponent(int entity, T component) {
        var hasToChangeSignature = !entityManager.getEntitySignature(entity).get(getComponentId(component.getClass()));
        componentRegistry.compute(component.getClass(), (_, store) -> {
            ComponentStore<T> usedStore;
            if (store == null) {
                usedStore = new ComponentStore<>();
                usedStore.add(entity, component);
            } else {
                usedStore = (ComponentStore<T>) store;
                usedStore.add(entity, component);
            }
            if (hasToChangeSignature) {
                entityManager.setEntitySignature(entity, getComponentId(component.getClass()));
            }
            return usedStore;
        });
        if (hasToChangeSignature) {
            notifySystems(entity, entityManager.getEntitySignature(entity));
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(int entity, Class<T> componentClass) {
        var store = componentRegistry.get(componentClass);
        return Objects.isNull(store) ? null : (T) store.getForEntity(entity);
    }

    public void addSystem(GameSystem system) {
        if (system.getClass().getAnnotation(RenderSystem.class) != null) {
            renderSystemList.add(system);
        } else {
            logicSystemList.add(system);
        }
    }

    public void update(float delta) {
        logicSystemList.forEach(system -> system.innerUpdate(delta));
    }

    public void render(float delta) {
        renderSystemList.forEach(system -> system.innerUpdate(delta));
    }

    @SafeVarargs
    public final int[] getEntititesWithComponents(Class<? extends Component>... componentClasses) {
        var wantedSignature = new BitSet();
        Arrays.stream(componentClasses).forEach(componentClass -> wantedSignature.set(getComponentId(componentClass)));
        return entityManager.getEntitiesWithSignature(wantedSignature);
    }

    int getComponentId(Class<? extends Component> componentClass) {
        return componentIdMap.computeIfAbsent(componentClass, _ -> nextComponentId++);
    }

    private void notifySystems(int entityId, BitSet signature) {
        logicSystemList.forEach(s -> s.onEntitySignatureChanged(entityId, signature));
        renderSystemList.forEach(s -> s.onEntitySignatureChanged(entityId, signature));
    }
}
