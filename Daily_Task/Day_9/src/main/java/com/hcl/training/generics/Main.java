package com.hcl.training.generics;

import com.hcl.training.generics.model.Box;
import com.hcl.training.generics.model.Pair;
import com.hcl.training.generics.model.Product;
import com.hcl.training.generics.resource.CustomResource;
import com.hcl.training.generics.service.ProductCsvReader;
import com.hcl.training.generics.util.GenericToolkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main application for Day 9:
 * 1. Generic Toolkit: Pair<K,V>, Box<T extends Number>, <T extends Comparable<T>> max()
 * 2. Wildcard methods: Upper-bounded (? extends), Lower-bounded (? super), PECS copy, Unbounded (?)
 * 3. Reading products.csv using try-with-resources
 * 4. Custom AutoCloseable demonstrating reverse close order (LIFO)
 */
public class Main {

    public static void main(String[] args) {
        printHeader("DAY 9: GENERIC TOOLKIT & RESOURCE MANAGEMENT");

        demonstratePair();
        demonstrateNumberBox();
        demonstrateGenericMax();
        demonstrateWildcardMethods();
        demonstrateCsvReadingWithTryWithResources();
        demonstrateAutoCloseableCloseOrder();

        printHeader("DAY 9 DEMONSTRATION COMPLETE");
    }

    /**
     * 1. Generic Pair<K,V>
     */
    private static void demonstratePair() {
        printSection("1. Generic Pair<K,V> Demonstration");

        Pair<String, Integer> studentScore = Pair.of("Alice", 95);
        Pair<Integer, String> employee = new Pair<>(1001, "John Developer");
        Pair<String, Double> productPrice = Pair.of("Mechanical Keyboard", 89.99);

        System.out.println("Created Pairs:");
        System.out.println("  Student Score : " + studentScore);
        System.out.println("  Employee      : " + employee);
        System.out.println("  Product Price : " + productPrice);

        // Swap key and value
        Pair<Integer, String> swapped = studentScore.swap();
        System.out.println("Swapped Student Score: " + swapped);
        System.out.println("Key: " + swapped.getKey() + ", Value: " + swapped.getValue());
    }

    /**
     * 2. Bounded Box<T extends Number>
     */
    private static void demonstrateNumberBox() {
        printSection("2. Bounded Type Box<T extends Number> Demonstration");

        Box<Integer> intBox = new Box<>(42);
        Box<Double> doubleBox = new Box<>(99.95);
        Box<Long> longBox = new Box<>(1_000_000L);

        // Compile-time safety: Box<String> stringBox = new Box<>("Hello"); // Error: String not within bound!

        System.out.println("Integer Box : " + intBox + " -> doubleValue = " + intBox.doubleValue());
        System.out.println("Double Box  : " + doubleBox + " -> doubleValue = " + doubleBox.doubleValue());
        System.out.println("Long Box    : " + longBox + " -> doubleValue = " + longBox.doubleValue());

        // Comparison across different numeric boxes using wildcard
        System.out.println("Is doubleBox > intBox? " + doubleBox.isGreaterThan(intBox));
        System.out.println("Is intBox > longBox?   " + intBox.isGreaterThan(longBox));
    }

    /**
     * 3. Bounded Type Parameter: <T extends Comparable<T>> max()
     */
    private static void demonstrateGenericMax() {
        printSection("3. Generic Method: <T extends Comparable<T>> max()");

        // Finding max integer
        Integer maxInt = GenericToolkit.max(14, 88, 23, 95, 42);
        System.out.println("Max of [14, 88, 23, 95, 42]           : " + maxInt);

        // Finding max string
        List<String> names = Arrays.asList("Banana", "Watermelon", "Apple", "Mango", "Cherry");
        String maxString = GenericToolkit.max(names);
        System.out.println("Max of " + names + " : " + maxString);

        // Finding max product based on natural ordering (highest price)
        Product p1 = new Product(1, "Mouse", "Accessories", 25.50, 10);
        Product p2 = new Product(2, "Monitor", "Electronics", 299.99, 5);
        Product p3 = new Product(3, "Keyboard", "Accessories", 75.00, 20);

        Product maxProduct = GenericToolkit.max(p1, p2, p3);
        System.out.println("Max Product by price: " + maxProduct);
    }

    /**
     * 4. Wildcards & PECS Principle
     */
    private static void demonstrateWildcardMethods() {
        printSection("4. Wildcards (Upper-Bounded, Lower-Bounded, Unbounded & PECS)");

        // Upper-bounded: List<? extends Number> (Producer - Read Only)
        List<Integer> intList = Arrays.asList(10, 20, 30, 40);
        List<Double> doubleList = Arrays.asList(5.5, 10.25, 20.75);
        System.out.println("Sum of Integer List <? extends Number> : " + GenericToolkit.sumOfNumbers(intList));
        System.out.println("Sum of Double List  <? extends Number> : " + GenericToolkit.sumOfNumbers(doubleList));

        // Lower-bounded: List<? super Integer> (Consumer - Write Only)
        List<Number> numList = new ArrayList<>();
        GenericToolkit.addIntegers(numList, 1, 5);
        System.out.println("Populated List<Number> via <? super Integer> : " + numList);

        // PECS copy: copy from List<? extends T> to List<? super T>
        List<Integer> source = Arrays.asList(100, 200, 300);
        List<Number> destination = new ArrayList<>();
        GenericToolkit.copy(source, destination);
        System.out.println("Destination after PECS copy(source, dest): " + destination);

        // Unbounded: List<?> (Print any list)
        System.out.println("Printing lists via unbounded wildcard List<?>:");
        GenericToolkit.printList(namesSample());
    }

    private static List<String> namesSample() {
        return Arrays.asList("Java Generics", "PECS Rule", "Try-With-Resources", "AutoCloseable");
    }

    /**
     * 5. Reading products.csv with try-with-resources
     */
    private static void demonstrateCsvReadingWithTryWithResources() {
        printSection("5. Reading products.csv with Try-With-Resources");

        List<Product> products = null;
        try {
            // First attempt to read from classpath resource
            products = ProductCsvReader.readFromClasspath("products.csv");
        } catch (IOException e) {
            // Fallback to local file path if run from project root
            try {
                String fallbackPath = "products.csv";
                if (!new File(fallbackPath).exists()) {
                    fallbackPath = "src/main/resources/products.csv";
                }
                products = ProductCsvReader.readFromFile(fallbackPath);
            } catch (IOException ex) {
                System.err.println("Could not load products.csv: " + ex.getMessage());
            }
        }

        if (products != null && !products.isEmpty()) {
            System.out.println("Successfully loaded " + products.size() + " products from CSV:");
            for (Product p : products) {
                System.out.println("  " + p);
            }

            // Find max product from CSV using Generic max()
            Product mostValuable = GenericToolkit.max(products);
            System.out.println("\nMost expensive product (via GenericToolkit.max):");
            System.out.println("  -> " + mostValuable.getName() + " ($" + mostValuable.getPrice() + ")");
        }
    }

    /**
     * 6. Custom AutoCloseable showing close order
     */
    private static void demonstrateAutoCloseableCloseOrder() {
        printSection("6. Custom AutoCloseable & Close Order (LIFO / Reverse Order)");

        System.out.println("Entering try-with-resources block...");
        System.out.println("Declaring: Resource-A (Database) -> Resource-B (Network) -> Resource-C (File)");

        try (CustomResource r1 = new CustomResource("Resource-A [DatabaseConnection]");
             CustomResource r2 = new CustomResource("Resource-B [NetworkSocket]");
             CustomResource r3 = new CustomResource("Resource-C [FileWriter]")) {

            System.out.println("\n--- Inside try block: performing tasks ---");
            r1.doWork();
            r2.doWork();
            r3.doWork();
            System.out.println("--- End of try block reached, exiting scope ---\n");

        } // All resources automatically closed HERE in reverse order of declaration (LIFO)

        System.out.println("Exited try-with-resources block.");
        System.out.println("Notice that Resource-C closed first, then Resource-B, then Resource-A!");
    }

    private static void printHeader(String title) {
        System.out.println("================================================================================");
        System.out.println("  " + title);
        System.out.println("================================================================================");
    }

    private static void printSection(String sectionTitle) {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println(sectionTitle);
        System.out.println("--------------------------------------------------------------------------------");
    }
}
