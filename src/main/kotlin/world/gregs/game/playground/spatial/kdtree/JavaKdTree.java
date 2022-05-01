package world.gregs.game.playground.spatial.kdtree;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Queue;

public class JavaKdTree {

    // helper data type representing a node of a kd-tree
    private static class KdNode {
        private KdNode left;
        private KdNode right;
        private final boolean vertical;
        private final double x;
        private final double y;

        public KdNode(final double x, final double y, final KdNode l,
                      final KdNode r, final boolean v) {
            this.x = x;
            this.y = y;
            left = l;
            right = r;
            vertical = v;
        }
    }

    private static final Rectangle CONTAINER = new Rectangle(0, 0, 1, 1);
    private KdNode root;
    private int size;

    // construct an empty tree of points
    public JavaKdTree() {
        size = 0;
        root = null;
    }

    // does the tree contain the point p?
    public boolean contains(final Point2D p) {
        return contains(root, p.getX(), p.getY());
    }

    // helper: does the subtree rooted at node contain (x, y)?
    private boolean contains(KdNode node, double x, double y) {
        if (node == null) return false;
        if (node.x == x && node.y == y) return true;

        if (node.vertical && x < node.x || !node.vertical && y < node.y)
            return contains(node.left, x, y);
        else
            return contains(node.right, x, y);
    }


    // helper: add point p to subtree rooted at node
    private KdNode insert(final KdNode node, final Point2D p,
                          final boolean vertical) {
        // if new node, create it
        if (node == null) {
            size++;
            return new KdNode(p.getX(), p.getY(), null, null, vertical);
        }

        // if already in, return it
        if (node.x == p.getX() && node.y == p.getY()) return node;

        // else, insert it where corresponds (left - right recursive call)
        if (node.vertical && p.getX() < node.x || !node.vertical && p.getY() < node.y)
            node.left = insert(node.left, p, !node.vertical);
        else
            node.right = insert(node.right, p, !node.vertical);

        return node;
    }

    // add the point p to the tree (if it is not already in the tree)
    public void insert(final Point2D p) {
        root = insert(root, p, true);
    }

    // is the tree empty?
    public boolean isEmpty() {
        return size == 0;
    }

    public double distanceSquaredTo(Point2D from, Point2D that) {
        double dx = from.getX() - that.getX();
        double dy = from.getY() - that.getY();
        return dx * dx + dy * dy;
    }

    // helper: get the left rectangle of node inside parent's rect
    private Rectangle leftRect(final Rectangle rect, final KdNode node) {
        if (node.vertical)
            return new Rectangle((int) rect.getMinX(), (int) rect.getMinY(), (int) node.x, (int) rect.getMaxY());
        else
            return new Rectangle((int) rect.getMinX(), (int) rect.getMinY(), (int) rect.getMaxX(), (int) node.y);
    }

    public double distanceSquaredTo(Rectangle rect, Point2D p) {
        double dx = 0.0, dy = 0.0;
        if (p.getX() < rect.getMinX()) dx = p.getX() - rect.getMinX();
        else if (p.getX() > rect.getMaxX()) dx = p.getX() - rect.getMaxX();
        if (p.getY() < rect.getMinY()) dy = p.getY() - rect.getMinY();
        else if (p.getY() > rect.getMaxY()) dy = p.getY() - rect.getMaxY();
        return dx * dx + dy * dy;
    }

    // helper: nearest neighbor of (x,y) in subtree rooted at node
    private Point2D nearest(final KdNode node, final Rectangle rect,
                            final double x, final double y, final Point2D candidate) {
        if (node == null) return candidate;

        double dqn = 0.0;
        double drq = 0.0;
        Rectangle left = null;
        Rectangle rigt = null;
        final Point2D query = new Point2D() {
            @Override
            public double getX() {
                return x;
            }

            @Override
            public double getY() {
                return y;
            }

            @Override
            public void setLocation(double x, double y) {

            }
        };
        Point2D nearest = candidate;

        if (nearest != null) {
            dqn = distanceSquaredTo(query, nearest);
            drq = distanceSquaredTo(rect, query);
        }

        if (nearest == null || dqn > drq) {
            final Point2D point = new Point2D() {
                @Override
                public double getX() {
                    return node.x;
                }

                @Override
                public double getY() {
                    return node.y;
                }

                @Override
                public void setLocation(double x, double y) {

                }
            };
            if (nearest == null || dqn > distanceSquaredTo(query, point))
                nearest = point;

            if (node.vertical) {
                left = new Rectangle((int) rect.getMinX(), (int) rect.getMinY(), (int) node.x, (int) rect.getMaxY());
                rigt = new Rectangle((int) node.x, (int) rect.getMinY(), (int) rect.getMaxX(), (int) rect.getMaxY());

                if (x < node.x) {
                    nearest = nearest(node.left, left, x, y, nearest);
                    nearest = nearest(node.right, rigt, x, y, nearest);
                } else {
                    nearest = nearest(node.right, rigt, x, y, nearest);
                    nearest = nearest(node.left, left, x, y, nearest);
                }
            } else {
                left = new Rectangle((int) rect.getMinX(), (int) rect.getMinY(), (int) rect.getMaxX(), (int) node.y);
                rigt = new Rectangle((int) rect.getMinX(), (int) node.y, (int) rect.getMaxX(), (int) rect.getMaxY());

                if (y < node.y) {
                    nearest = nearest(node.left, left, x, y, nearest);
                    nearest = nearest(node.right, rigt, x, y, nearest);
                } else {
                    nearest = nearest(node.right, rigt, x, y, nearest);
                    nearest = nearest(node.left, left, x, y, nearest);
                }
            }
        }

        return nearest;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(final Point2D p) {
        return nearest(root, CONTAINER, p.getX(), p.getY(), null);
    }

    // helper: points in subtree rooted at node inside rect
    private void range(final KdNode node, final Rectangle nrect,
                       final Rectangle rect, final Queue<Point2D> queue) {
        if (node == null) return;

        if (rect.intersects(nrect)) {
            final Point2D p = new Point2D() {
                @Override
                public double getX() {
                    return node.x;
                }

                @Override
                public double getY() {
                    return node.y;
                }

                @Override
                public void setLocation(double x, double y) {

                }
            };
            if (rect.contains(p)) queue.add(p);
            range(node.left, leftRect(nrect, node), rect, queue);
            range(node.right, rightRect(nrect, node), rect, queue);
        }
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(final Rectangle rect) {
        final Queue<Point2D> queue = new LinkedList<>();
        range(root, CONTAINER, rect, queue);

        return queue;
    }

    // helper: get the right rectangle of node inside parent's rect
    private Rectangle rightRect(final Rectangle rect, final KdNode node) {
        if (node.vertical)
            return new Rectangle((int) node.x, (int) rect.getMinY(), (int) rect.getMaxX(), (int) rect.getMaxY());
        else
            return new Rectangle((int) rect.getMinX(), (int) node.y, (int) rect.getMaxX(), (int) rect.getMaxY());
    }

    // number of points in the tree
    public int size() {
        return size;
    }

}