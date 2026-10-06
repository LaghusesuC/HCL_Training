public class MonthlyUsageAnalyser {

    // Threshold slabs for efficiency grades
    public static final int SLAB_LOW = 150;     // <= 150 -> Grade A
    public static final int SLAB_MEDIUM = 300;  // <= 300 -> Grade B, > 300 -> Grade C

    public static void main(String[] args) {
        // 1. Single House Usage (1-D Array: 12 months in kWh)
        int[] house1 = { 120, 140, 180, 220, 310, 340, 290, 260, 210, 175, 130, 115 };

        System.out.println("=== 1-D Array Analysis ===");
        analyseUsage("House 1", house1);

        // 2. Multi-House Usage (2-D Array: 3 Houses x 12 Months)
        int[][] houses = {
            house1,
            {  90, 105, 110, 130, 145, 160, 150, 140, 125, 110,  95,  85 },
            { 320, 350, 380, 420, 450, 480, 460, 430, 390, 360, 330, 310 }
        };

        System.out.println("\n=== 2-D Array Analysis ===");
        for (int i = 0; i < houses.length; i++) {
            analyseUsage("House " + (i + 1), houses[i]);
        }

        // 3. Integer Overflow Demonstration & Fix
        System.out.println("\n=== Integer Overflow Demo ===");
        int a = 1_500_000_000, b = 1_000_000_000;
        System.out.println("int sum (overflow) : " + (a + b));
        System.out.println("long sum (safe)    : " + ((long) a + b));
    }

    // Reusable analysis method for any house's monthly data
    public static void analyseUsage(String label, int[] usage) {
        long total = 0L; // long accumulator prevents overflow
        int min = usage[0], max = usage[0];

        for (int units : usage) {
            total += units;
            if (units < min) min = units;
            if (units > max) max = units;
        }

        // Explicit cast to avoid integer division truncation
        double avg = (double) total / usage.length;

        // Nested ternary operator to assign grade
        char grade = (avg <= SLAB_LOW) ? 'A' : (avg <= SLAB_MEDIUM ? 'B' : 'C');

        System.out.printf("%s -> Total: %4d | Avg: %6.2f | Min: %3d | Max: %3d | Grade: %c%n",
                label, total, avg, min, max, grade);
    }
}
