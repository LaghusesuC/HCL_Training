package com.hcl.training.generics.service;

import com.hcl.training.generics.model.Product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to parse products.csv using Java's modern try-with-resources statement.
 * Try-with-resources guarantees that opened stream resources are safely closed
 * even if exceptions occur during parsing.
 */
public class ProductCsvReader {

    /**
     * Reads products from a local file path using try-with-resources.
     *
     * @param filePath path to the CSV file
     * @return list of parsed Product objects
     * @throws IOException if an I/O error occurs while reading
     */
    public static List<Product> readFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        List<Product> products = new ArrayList<>();
        // Try-with-resources ensures BufferedReader is closed automatically upon completion
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            parseLines(reader, products);
        }
        return products;
    }

    /**
     * Reads products from the application classpath / resources using try-with-resources.
     *
     * @param resourcePath relative classpath resource (e.g. "/products.csv" or "products.csv")
     * @return list of parsed Product objects
     * @throws IOException if resource is not found or cannot be read
     */
    public static List<Product> readFromClasspath(String resourcePath) throws IOException {
        String normalizedPath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        InputStream is = ProductCsvReader.class.getResourceAsStream(normalizedPath);
        if (is == null) {
            throw new IOException("Classpath resource not found: " + resourcePath);
        }

        List<Product> products = new ArrayList<>();
        // Multiple resources in try-with-resources statement (InputStream and BufferedReader)
        try (InputStream stream = is;
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            parseLines(reader, products);
        }
        return products;
    }

    private static void parseLines(BufferedReader reader, List<Product> products) throws IOException {
        String line;
        boolean isHeader = true;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            // Skip CSV header line
            if (isHeader) {
                isHeader = false;
                continue;
            }

            String[] tokens = line.split(",");
            if (tokens.length >= 5) {
                try {
                    int id = Integer.parseInt(tokens[0].trim());
                    String name = tokens[1].trim();
                    String category = tokens[2].trim();
                    double price = Double.parseDouble(tokens[3].trim());
                    int quantity = Integer.parseInt(tokens[4].trim());

                    products.add(new Product(id, name, category, price, quantity));
                } catch (NumberFormatException e) {
                    System.err.printf("Skipping malformed row at line %d: %s (%s)%n",
                            lineNumber, line, e.getMessage());
                }
            }
        }
    }
}
