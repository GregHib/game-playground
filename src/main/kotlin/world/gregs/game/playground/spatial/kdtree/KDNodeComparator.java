package world.gregs.game.playground.spatial.kdtree;

import java.util.Comparator;

public abstract class KDNodeComparator<T> {
    // This should return a comparator for whatever axis is passed in
    protected abstract Comparator getComparator(int axis);

    // Return squared distance between current and other
    protected abstract double squaredDistance(T other);

    // Return squared distance between one axis only
    protected abstract double axisSquaredDistance(T other, int axis);
}
