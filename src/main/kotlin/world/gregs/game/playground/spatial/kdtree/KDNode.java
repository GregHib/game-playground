package world.gregs.game.playground.spatial.kdtree;

public class KDNode<T extends KDNodeComparator<T>> {
    KDNode<T> left;
    KDNode<T> right;
    T location;

    public KDNode( KDNode<T> left, KDNode<T> right, T location ) {
        this.left = left;
        this.right = right;
        this.location = location;
    }
}
