package xyz.cacharrito.games.gameengine.core.component.input;

import lombok.Getter;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_A;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_B;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_BACK;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_START;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_APOSTROPHE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_GRAVE_ACCENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_MULTIPLY;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_NUM_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_WORLD_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_WORLD_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_6;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_7;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_8;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public enum InputKey {

    // --- KEYBOARD (Spanish layout) ---
    KEY_A(GLFW_KEY_A, false, false), KEY_B(GLFW_KEY_B, false, false), KEY_C(GLFW_KEY_C, false, false), KEY_D(GLFW_KEY_D, false, false),
    KEY_E(GLFW_KEY_E, false, false), KEY_F(GLFW_KEY_F, false, false), KEY_G(GLFW_KEY_G, false, false), KEY_H(GLFW_KEY_H, false, false),
    KEY_I(GLFW_KEY_I, false, false), KEY_J(GLFW_KEY_J, false, false), KEY_K(GLFW_KEY_K, false, false), KEY_L(GLFW_KEY_L, false, false),
    KEY_M(GLFW_KEY_M, false, false), KEY_N(GLFW_KEY_N, false, false), KEY_O(GLFW_KEY_O, false, false), KEY_P(GLFW_KEY_P, false, false),
    KEY_Q(GLFW_KEY_Q, false, false), KEY_R(GLFW_KEY_R, false, false), KEY_S(GLFW_KEY_S, false, false), KEY_T(GLFW_KEY_T, false, false),
    KEY_U(GLFW_KEY_U, false, false), KEY_V(GLFW_KEY_V, false, false), KEY_W(GLFW_KEY_W, false, false), KEY_X(GLFW_KEY_X, false, false),
    KEY_Y(GLFW_KEY_Y, false, false), KEY_Z(GLFW_KEY_Z, false, false),
    KEY_0(GLFW_KEY_0, false, false), KEY_1(GLFW_KEY_1, false, false), KEY_2(GLFW_KEY_2, false, false), KEY_3(GLFW_KEY_3, false, false),
    KEY_4(GLFW_KEY_4, false, false), KEY_5(GLFW_KEY_5, false, false), KEY_6(GLFW_KEY_6, false, false), KEY_7(GLFW_KEY_7, false, false),
    KEY_8(GLFW_KEY_8, false, false), KEY_9(GLFW_KEY_9, false, false),
    KEY_ORDINAL(GLFW_KEY_WORLD_1, false, false),    // º / ª
    KEY_QUOTE(GLFW_KEY_APOSTROPHE, false, false),   // '
    KEY_BACKTICK(GLFW_KEY_GRAVE_ACCENT, false, false), // `
    KEY_PLUS(GLFW_KEY_EQUAL, false, false),         // + (En GLFW, Equal suele ser el '+' físico)
    KEY_CEDILLA(GLFW_KEY_BACKSLASH, false, false),  // Ç
    KEY_COMMA(GLFW_KEY_COMMA, false, false),        // ,
    KEY_PERIOD(GLFW_KEY_PERIOD, false, false),       // .
    KEY_MINUS(GLFW_KEY_SLASH, false, false),        // - (En teclados ISO a veces es SLASH)
    KEY_LESS_THAN(GLFW_KEY_WORLD_2, false, false),  // < (Depende del driver, suele ser World_2)
    KEY_TILDE(GLFW_KEY_SEMICOLON, false, false),    // Ñ / ;
    KEY_SPACE(GLFW_KEY_SPACE, false, false), KEY_ENTER(GLFW_KEY_ENTER, false, false), KEY_ESCAPE(GLFW_KEY_ESCAPE, false, false),
    KEY_BACKSPACE(GLFW_KEY_BACKSPACE, false, false), KEY_TAB(GLFW_KEY_TAB, false, false),
    KEY_LSHIFT(GLFW_KEY_LEFT_SHIFT, false, false), KEY_RSHIFT(GLFW_KEY_RIGHT_SHIFT, false, false),
    KEY_LCTRL(GLFW_KEY_LEFT_CONTROL, false, false), KEY_RCTRL(GLFW_KEY_RIGHT_CONTROL, false, false),
    KEY_LALT(GLFW_KEY_LEFT_ALT, false, false), KEY_RALT(GLFW_KEY_RIGHT_ALT, false, false),
    KEY_UP(GLFW_KEY_UP, false, false), KEY_DOWN(GLFW_KEY_DOWN, false, false), KEY_LEFT(GLFW_KEY_LEFT, false, false), KEY_RIGHT(GLFW_KEY_RIGHT, false, false),
    KEY_INSERT(GLFW_KEY_INSERT, false, false), KEY_DELETE(GLFW_KEY_DELETE, false, false), KEY_HOME(GLFW_KEY_HOME, false, false),
    KEY_END(GLFW_KEY_END, false, false), KEY_PAGE_UP(GLFW_KEY_PAGE_UP, false, false), KEY_PAGE_DOWN(GLFW_KEY_PAGE_DOWN, false, false),
    KEY_F1(GLFW_KEY_F1, false, false), KEY_F2(GLFW_KEY_F2, false, false), KEY_F3(GLFW_KEY_F3, false, false), KEY_F4(GLFW_KEY_F4, false, false),
    KEY_F5(GLFW_KEY_F5, false, false), KEY_F6(GLFW_KEY_F6, false, false), KEY_F7(GLFW_KEY_F7, false, false), KEY_F8(GLFW_KEY_F8, false, false),
    KEY_F9(GLFW_KEY_F9, false, false), KEY_F10(GLFW_KEY_F10, false, false), KEY_F11(GLFW_KEY_F11, false, false), KEY_F12(GLFW_KEY_F12, false, false),

    // --- NUMPAD ---
    NUMPAD_0(GLFW_KEY_KP_0, false, false), NUMPAD_1(GLFW_KEY_KP_1, false, false), NUMPAD_2(GLFW_KEY_KP_2, false, false),
    NUMPAD_3(GLFW_KEY_KP_3, false, false), NUMPAD_4(GLFW_KEY_KP_4, false, false), NUMPAD_5(GLFW_KEY_KP_5, false, false),
    NUMPAD_6(GLFW_KEY_KP_6, false, false), NUMPAD_7(GLFW_KEY_KP_7, false, false), NUMPAD_8(GLFW_KEY_KP_8, false, false), NUMPAD_9(GLFW_KEY_KP_9, false, false),
    NUMPAD_DIVIDE(GLFW_KEY_KP_DIVIDE, false, false), NUMPAD_MULTIPLY(GLFW_KEY_KP_MULTIPLY, false, false),
    NUMPAD_SUBTRACT(GLFW_KEY_KP_SUBTRACT, false, false), NUMPAD_ADD(GLFW_KEY_KP_ADD, false, false),
    NUMPAD_DECIMAL(GLFW_KEY_KP_DECIMAL, false, false), NUMPAD_ENTER(GLFW_KEY_KP_ENTER, false, false),
    NUMPAD_NUMLOCK(GLFW_KEY_NUM_LOCK, false, false),


    // --- MOUSE ---
    MOUSE_LEFT(GLFW_MOUSE_BUTTON_LEFT, false, true),
    MOUSE_RIGHT(GLFW_MOUSE_BUTTON_RIGHT, false, true),
    MOUSE_MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE, false, true),
    MOUSE_BACK(GLFW_MOUSE_BUTTON_4, false, true),
    MOUSE_FORWARD(GLFW_MOUSE_BUTTON_5, false, true),
    MOUSE_EXTRA_1(GLFW_MOUSE_BUTTON_6, false, true),
    MOUSE_EXTRA_2(GLFW_MOUSE_BUTTON_7, false, true),
    MOUSE_EXTRA_3(GLFW_MOUSE_BUTTON_8, false, true),

    // --- GAMEPAD ---
    GAMEPAD_SOUTH(GLFW_GAMEPAD_BUTTON_A, true, false),
    GAMEPAD_EAST(GLFW_GAMEPAD_BUTTON_B, true, false),
    GAMEPAD_WEST(GLFW_GAMEPAD_BUTTON_X, true, false),
    GAMEPAD_NORTH(GLFW_GAMEPAD_BUTTON_Y, true, false),
    GAMEPAD_L_BUMPER(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, true, false),
    GAMEPAD_R_BUMPER(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, true, false),
    GAMEPAD_SELECT(GLFW_GAMEPAD_BUTTON_BACK, true, false),
    GAMEPAD_START(GLFW_GAMEPAD_BUTTON_START, true, false),
    GAMEPAD_LEFT_STICK_BUTTON(GLFW_GAMEPAD_BUTTON_LEFT_THUMB, true, false),
    GAMEPAD_RIGHT_STICK_BUTTON(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, true, false),
    GAMEPAD_DPAD_UP(GLFW_GAMEPAD_BUTTON_DPAD_UP, true, false),
    GAMEPAD_DPAD_RIGHT(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, true, false),
    GAMEPAD_DPAD_DOWN(GLFW_GAMEPAD_BUTTON_DPAD_DOWN, true, false),
    GAMEPAD_DPAD_LEFT(GLFW_GAMEPAD_BUTTON_DPAD_LEFT, true, false),

    UNKNOWN(-1, false, false);

    @Getter
    private final int glfwBind;

    @Getter
    private final boolean isGamepad;

    @Getter
    private final boolean isMouse;

    InputKey(int glfwBind, boolean isGamepad, boolean isMouse) {
        this.glfwBind = glfwBind;
        this.isGamepad = isGamepad;
        this.isMouse = isMouse;
    }

}
