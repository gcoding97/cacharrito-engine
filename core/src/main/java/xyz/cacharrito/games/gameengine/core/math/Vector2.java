package xyz.cacharrito.games.gameengine.core.math;

public record Vector2(float x, float y) {

    public static final Vector2 ZERO = new Vector2(0, 0);
    public static final Vector2 RIGHT = new Vector2(1, 0);
    public static final Vector2 LEFT = new Vector2(-1, 0);
    public static final Vector2 UP = new Vector2(0, -1);
    public static final Vector2 DOWN = new Vector2(0, 1);
    public static final Vector2 ONE = new Vector2(1, 1);

    public boolean equals(Object o) {
        return o instanceof Vector2(float x1, float y1) && (o == this || (x == x1 && y == y1));
    }

    public Vector2 multiply(float value) {
        return new Vector2(x * value, y * value);
    }

}
