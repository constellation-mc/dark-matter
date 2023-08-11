package me.melontini.dark_matter.api.base.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class NullList<E> extends ArrayList<E> {
    @Override
    public void trimToSize() {
    }

    @Override
    public void ensureCapacity(int minCapacity) {
    }

    @Override
    public E set(int index, E element) {
        return get(index);
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public void add(int index, E element) {
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public E remove(int index) {
        return get(index);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public void sort(Comparator<? super E> c) {
    }
}
