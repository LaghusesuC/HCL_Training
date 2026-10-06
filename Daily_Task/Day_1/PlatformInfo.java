public class PlatformInfo {
    public static void main(String[] args) {
        // 1. System Properties
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");

        // 2. Runtime Information
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        long maxMemory = runtime.maxMemory();   // in bytes
        long freeMemory = runtime.freeMemory(); // in bytes

        // 3. Display Information
        System.out.println("==========================================");
        System.out.println("         Platform Information            ");
        System.out.println("==========================================");
        System.out.println("Java Version        : " + javaVersion);
        System.out.println("Operating System    : " + osName);
        System.out.println("Available Processors: " + processors);
        System.out.printf("Max Heap Memory     : %d MB (%d bytes)%n", maxMemory / (1024 * 1024), maxMemory);
        System.out.printf("Free Heap Memory    : %d MB (%d bytes)%n", freeMemory / (1024 * 1024), freeMemory);
        System.out.println("==========================================");
    }
}
