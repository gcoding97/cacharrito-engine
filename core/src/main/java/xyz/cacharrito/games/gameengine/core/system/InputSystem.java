package xyz.cacharrito.games.gameengine.core.system;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import xyz.cacharrito.games.gameengine.core.Engine;
import xyz.cacharrito.games.gameengine.core.component.InputSettings;
import xyz.cacharrito.games.gameengine.core.component.input.InputEventListener;
import xyz.cacharrito.games.gameengine.core.ecs.GameSystem;
import xyz.cacharrito.games.gameengine.core.ecs.RequireComponents;
import xyz.cacharrito.games.gameengine.core.ecs.World;
import xyz.cacharrito.games.gameengine.core.math.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwJoystickIsGamepad;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

@RequireComponents({InputSettings.class})
public class InputSystem extends GameSystem {

    private final Map<Integer, Map<String, Method>> actionMethodMap;
    private final GLFWGamepadState gamepadState;

    private final float deadZone;
    private final double[] mouseXPos;
    private final double[] mouseYPos;

    public InputSystem(World world) {
        super(world);
        actionMethodMap = new HashMap<>();
        gamepadState = GLFWGamepadState.create();
        deadZone = 0.1f;
        mouseXPos = new double[1];
        mouseYPos = new double[1];
    }

    @Override
    public void onEntitySignatureChangedCallback(int entityId) {
        var inputSettings = getWorld().getComponent(entityId, InputSettings.class);
        var eventListener = inputSettings.eventListener();
        inputSettings.actionList().forEach(action -> {
            try {
                var actionMethod = eventListener.getClass().getMethod(action.name(), Boolean.class, Float.class);
                actionMethodMap.compute(entityId, (_, value) -> {
                    Map<String, Method> actionMap = value;
                    if (actionMap == null) {
                        actionMap = new HashMap<>();
                    }
                    actionMap.put(action.name(), actionMethod);
                    return actionMap;
                });
            } catch (NoSuchMethodException e) {
                System.err.println("There's no method named " + action.name());
                e.printStackTrace();
            }
        });
    }


    @Override
    public void update(int entity, float delta) {
        var inputSettings = getWorld().getComponent(entity, InputSettings.class);
        var eventListener = inputSettings.eventListener();
        var gamepadId = getFirstGamepadId();
        if (gamepadId != -1) {
            glfwGetGamepadState(gamepadId, gamepadState);
            handleJoystickEvents(delta, eventListener);
        }
        handleMouseMotionEvent(eventListener);

        inputSettings.actionList().forEach(action -> invokeAction(entity, action.name(), eventListener,
                action.linkedKeyList().stream().anyMatch(key ->
                        (!key.isMouse() && !key.isGamepad() && Engine.getMainWindow().isKeyPressed(key.getGlfwBind()))
                                || (key.isGamepad() && gamepadId != -1 && gamepadState.buttons(key.getGlfwBind()) != 0)
                                || (key.isMouse() && Engine.getMainWindow().isMouseButtonPressed(key.getGlfwBind()))),
                delta));
    }

    private void handleMouseMotionEvent(InputEventListener eventListener) {
        glfwGetCursorPos(Engine.getMainWindow().getWindow(), mouseXPos, mouseYPos);
        eventListener.handleMouseMotionEvent(new Vector2((float) mouseXPos[0], (float) mouseYPos[0]));
    }

    private void handleJoystickEvents(float delta, InputEventListener eventListener) {
        var leftAxisX = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X);
        var leftAxisY = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y);
        var leftAxis = new Vector2(applyDeadzone(leftAxisX), applyDeadzone(leftAxisY));
        eventListener.handleLeftJoystickEvent(leftAxis, delta);
        var rightAxisX = gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_X);
        var rightAxisY = gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_Y);
        var rightAxis = new Vector2(applyDeadzone(rightAxisX), applyDeadzone(rightAxisY));
        eventListener.handleRightJoystickEvent(rightAxis, delta);
    }

    private float applyDeadzone(float value) {
        return Math.abs(value) > deadZone ? value : 0;
    }

    private int getFirstGamepadId() {
        for (int i = 0; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            if (glfwJoystickPresent(i) && glfwJoystickIsGamepad(i)) {
                return i;
            }
        }
        return -1;
    }

    private void invokeAction(int entity, String actionMethodName, InputEventListener eventListener, boolean isPressed, float delta) {
        var actionMethod = actionMethodMap.get(entity).get(actionMethodName);
        try {
            actionMethod.invoke(eventListener, isPressed, delta);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not invoke " + actionMethodName);
            e.printStackTrace();
        }
    }

}
