import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Belirli bir aralıktaki noktaların dışbükeyliğini kontrol eden iş parçacığı görevi.
 */
public class ConvexityCheckTask implements Runnable {
    private final List<Point> points;
    private final int startIndex;
    private final int endIndex;
    private final int firstValidOrientation;
    private final AtomicBoolean isStillConvex;
    private final int n;

    public ConvexityCheckTask(List<Point> points, int startIndex, int endIndex, int firstValidOrientation, AtomicBoolean isStillConvex) {
        this.points = points;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.firstValidOrientation = firstValidOrientation;
        this.isStillConvex = isStillConvex;
        this.n = points.size();
    }

    @Override
    public void run() {
        System.out.println("Checking convexity from index " + startIndex + " to " + endIndex);
        for (int i = startIndex; i < endIndex; i++) {
            if (!isStillConvex.get()  || Thread.currentThread().isInterrupted()) {
                return;
            }

            Point p = points.get(i);
            Point q = points.get((i + 1) % n);
            Point r = points.get((i + 2) % n);

            int orientation = ConvexChecker.calculateOrientation(p, q, r);
            
            if (orientation != 0 && orientation != firstValidOrientation) {
                isStillConvex.set(false);
                return;
            }
        }
    }
}
