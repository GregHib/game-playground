package world.gregs.game.playground.pathfinding.bfs;

import java.util.Arrays;

public class BenchmarkCounter1dCollisionJava {

    static final int columns = 128;
    static final int rows = 128;
    static final int startX = 64;
    static final int startY = 64;

    int visit = 0;

    int[] frontier = new int[columns * rows];
    boolean[] collision = new boolean[columns * rows];
    int[] queue = new int[columns * rows];

    public BenchmarkCounter1dCollisionJava() {
        Arrays.fill(queue, -1);
    }

    static int pack(int x, int y) {
        return y | (x << 16);
    }

    static int index(int x, int y) {
        return x + (y * 128);
    }

    int getX(int value) {
        return value >> 16;
    }

    int getY(int value) {
        return value & 0xffff;
    }

    int getDistance(int value) {
        return value >> 16;
    }

    int getVisit(int value) {
        return value & 0xffff;
    }

    void bfs() {
        visit++;
        int writeIndex = 0;
        int readIndex = 0;
        queue[writeIndex++] = pack(startX, startY);
        frontier[index(startX, startY)] = pack(0, visit);
        while (readIndex < writeIndex) {
            int parent = queue[readIndex];
            readIndex++;
            int parentX = getX(parent);
            int parentY = getY(parent);

            int x = -1;
            int y = 0;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = 1;
            y = 0;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = 0;
            y = -1;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = 0;
            y = 1;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = -1;
            y = -1;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = 1;
            y = -1;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = -1;
            y = 1;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }

            x = 1;
            y = 1;
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit);
                queue[writeIndex] = pack(parentX + x, parentY + y);
                writeIndex += 1;
            }
        }
    }

    public static void main(String[] args) {
        BenchmarkCounter1dCollisionJava bfs = new BenchmarkCounter1dCollisionJava();
        // Warmup
        for (int i = 0; i < 20; i++) {
            bfs.bfs();
        }
        int count = 1000;
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            bfs.bfs();
        }
        long nano = System.nanoTime() - start;
        System.out.println("Took " + nano + "ns total " + (nano / count) + "ns avg");

        /*for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                System.out.print(bfs.getVisit(bfs.frontier[index(x, y)]) + " ");
            }
        }*/
    }
}
