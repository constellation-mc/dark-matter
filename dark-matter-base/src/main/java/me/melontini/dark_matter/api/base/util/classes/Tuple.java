package me.melontini.dark_matter.api.base.util.classes;

public record Tuple<L, R>(L left, R right) {

    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
