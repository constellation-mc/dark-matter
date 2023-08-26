package me.melontini.dark_matter.api.base.util.classes;

public class MutableTuple<L, R> {

    public static <L, R> MutableTuple<L, R> of(L left, R right) {
        return new MutableTuple<>(left, right);
    }

    private L left;
    private R right;

    public MutableTuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    public void left(L left) {
        this.left = left;
    }

    public void right(R right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "MutableTuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
