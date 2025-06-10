import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;

/**
 * Çokgenin dışbükey olup olmadığını paralel olarak kontrol eder.
 */
public class ConvexChecker {

    public static int calculateOrientation(Point p, Point q, Point r) {
        /* 
            Üç noktanın oryantasyonunu hesaplar.
            0: Noktalar aynı hizada (collinear)
            1: Saat yönünde (clockwise)
            -1: Saat yönünün tersine (counterclockwise)
         */
        double val = (q.x - p.x) * (r.y - p.y) - (q.y - p.y) * (r.x - p.x);
        if (val == 0) return 0;
        return (val > 0) ? 1 : -1;
    }

    public boolean isConvex(List<Point> points) {
        int n = points.size();
        if (n < 3) return true;

        // İlk üç noktanın oryantasyonunu bul
        // Bu oryantasyon, diğer tüm üçlüler için referans olacak
        // Eğer üç nokta aynı ise, dışbükeylik kontrolü yapılamaz
        int firstValidOrientation = 0;
        int tripletsChecked = 0;
        for (int i = 0; i < n; i++) {
            Point p = points.get(i);
            Point q = points.get((i + 1) % n);
            Point r = points.get((i + 2) % n);
            firstValidOrientation = calculateOrientation(p, q, r);
            tripletsChecked++;
            if (firstValidOrientation != 0) break;
        }
        System.out.println("Ilk gecerli oryantasyon: " + firstValidOrientation);
        if (firstValidOrientation == 0 && tripletsChecked == n) {
            return true;
        }

        AtomicBoolean isStillConvex = new AtomicBoolean(true);
        
        // Tüm noktaları kontrol etmek için thread havuzu oluştur
        // Kullanılacak thread sayısını, mevcut işlemci çekirdek sayısına göre ayarla
        int numThreads = Math.min(n, Runtime.getRuntime().availableProcessors());
        // Eğer thread sayısı 0 veya negatif ise, en az 1 thread kullan
        if (numThreads <= 0) numThreads = 1;
        // Thread havuzunu oluştur
        List<Runnable> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        /* 
            Ornek olarak, n = 1000 ve numThreads = 4 ise,
            her bir thread 250 nokta kontrol eder.
         */
        int pointsPerThread = (n + numThreads - 1) / numThreads;
        System.out.println("Thread sayisi: " + numThreads);
        System.out.println("Her thread'in kontrol edecegi nokta sayisi: " + pointsPerThread);

        // Thread'leri başlat
        for (int i = 0; i < numThreads; i++) {
            // Her thread için başlangıç ve bitiş indekslerini hesapla
            int start = i * pointsPerThread;
            int end = Math.min((i + 1) * pointsPerThread, n);
            // Eğer başlangıç indeksi bitiş indeksinden küçükse, thread'i başlat
            if (start >= n) break; // Eğer başlangıç indeksi n'den büyükse, döngüyü kır
            if (end > n) end = n; // Bitiş indeksi n'den büyükse, n olarak ayarla

            // Thread'in kontrol edeceği noktaları al
            if (start < end) {
                ConvexityCheckTask task = new ConvexityCheckTask(points, start, end, firstValidOrientation, isStillConvex);
                // Thread'i havuza ekle
                tasks.add(task);
            }
        }
        // Thread'leri çalıştır
        for (Runnable task : tasks) {
            executor.execute(task);
        }

        // Thread'lerin bitmesini bekle
        executor.shutdown();
        try {
            while (!executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                if (!isStillConvex.get()) {
                    executor.shutdownNow();
                    break;
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return isStillConvex.get();
    }
}
