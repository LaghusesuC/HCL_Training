package com.hcl.training.generics.model;

import java.util.Objects;

/**
 * Product model representing an item from products.csv.
 * Implements Comparable to enable natural ordering by price (highest price first, or ascending).
 */
public class Product implements Comparable<Product> {
    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private final int quantity;

    public Product(int id, String name, String category, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Natural order comparison based on product price (ascending).
     * If prices are equal, breaks tie using product ID.
     */
    @Override
    public int compareTo(Product other) {
        if (other == null) return 1;
        int priceCompare = Double.compare(this.price, other.price);
        if (priceCompare != 0) {
            return priceCompare;
        }
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                Double.compare(product.price, price) == 0 &&
                quantity == product.quantity &&
                Objects.equals(name, product.name) &&
                Objects.equals(category, product.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price, quantity);
    }

    @Override
    public String toString() {
        return String.format("Product #%d: %s [%s] - $%.2f (Qty: %d)", id, name, category, price, quantity);
    }
}
