package me.melontini.dark_matter.util.classes;

public record Tuple<L, R>(L left, R right) {
    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
