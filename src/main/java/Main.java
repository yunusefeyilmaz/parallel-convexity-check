import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    
    public static void main(String[] args) {
        ConvexChecker checker = new ConvexChecker();
        List<Point> points = new ArrayList<>();
        
        // Test: Kare (disbukey)
        points.add(new Point(0, 0));
        points.add(new Point(1, 0));
        points.add(new Point(1, 1));
        points.add(new Point(0, 1));
        System.out.println("-----------------TEST-----------------");
        System.out.println("Kare: " + points);
        if (checker.isConvex(points)) {
            System.out.println("Verilen cokgen DISBUKEYDIR.");
        } else {
            System.out.println("Verilen cokgen ICBUKEYDIR.");
        }
        points.clear();
        // Test: Icbukey
        points.add(new Point(0, 0));
        points.add(new Point(3, 0));
        points.add(new Point(1, 1)); // girinti
        points.add(new Point(3, 2));
        points.add(new Point(0, 2));
        System.out.println("-----------------TEST-----------------");
        System.out.println("Icbukey sekil: " + points);
        if (checker.isConvex(points)) {
            System.out.println("Verilen cokgen DISBUKEYDIR.");
        } else {
            System.out.println("Verilen cokgen ICBUKEYDIR.");
        }
        points.clear();
        System.out.println("------------------PROGRAM------------------");
        
        try {
            // Argumanlar var ise onları kullan
            if (args.length > 0) {
                for (String arg: args) {
                    String[] parts = arg.split(",");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Gecersiz nokta formati: " + arg);
                    }
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    points.add(new Point(x, y));
                }

                System.out.println("Girilen noktalar: " + points);
                if (checker.isConvex(points)) {
                    System.out.println("Verilen cokgen DISBUKEYDIR.");
                } else {
                    System.out.println("Verilen cokgen ICBUKEYDIR.");
                }
            } 
            // Arguman yok ise rastgele noktalar olustur
            else {
                Random random = new Random();
                int numberOfPoints = 10;
                for (int i = 0; i < numberOfPoints; i++) {
                    int x = random.nextInt() * 1000;
                    int y = random.nextInt() * 1000;
                    points.add(new Point(x, y));
                }
                System.out.println(numberOfPoints + " adet rastgele nokta olusturuldu.");
                //System.out.println("Rastgele noktalar: " + points);
                if (checker.isConvex(points)) {
                    System.out.println("Verilen cokgen DISBUKEYDIR.");
                } else {
                    System.out.println("Verilen cokgen ICBUKEYDIR.");
                }
            }

        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

}
