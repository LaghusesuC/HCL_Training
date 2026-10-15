package com.hcl.training.generics;

import com.hcl.training.generics.model.Box;
import com.hcl.training.generics.model.Pair;
import com.hcl.training.generics.model.Product;
import com.hcl.training.generics.resource.CustomResource;
import com.hcl.training.generics.service.ProductCsvReader;
import com.hcl.training.generics.util.GenericToolkit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GenericToolkitTest {

    @BeforeEach
    void setUp() {
        CustomResource.clearCloseHistory();
    }

    // =========================================================================
    // 1. Pair<K,V> Tests
    // =========================================================================

    @Test
    @DisplayName("Pair holds key and value correctly and supports swap()")
    void testPairOperations() {
        Pair<String, Integer> pair = Pair.of("Score", 100);
        assertEquals("Score", pair.getKey());
        assertEquals(100, pair.getValue());

        Pair<Integer, String> swapped = pair.swap();
        assertEquals(100, swapped.getKey());
        assertEquals("Score", swapped.getValue());

        Pair<String, Integer> pair2 = new Pair<>("Score", 100);
        assertEquals(pair, pair2);
        assertEquals(pair.hashCode(), pair2.hashCode());
    }

    // =========================================================================
    // 2. Box<T extends Number> Tests
    // =========================================================================

    @Test
    @DisplayName("Box bounds to Number subtypes and provides doubleValue")
    void testNumericBox() {
        Box<Integer> intBox = new Box<>(50);
        Box<Double> doubleBox = new Box<>(50.5);

        assertEquals(50, intBox.getItem());
        assertEquals(50.0, intBox.doubleValue(), 0.001);
        assertEquals(50.5, doubleBox.doubleValue(), 0.001);

        assertTrue(doubleBox.isGreaterThan(intBox));
        assertFalse(intBox.isGreaterThan(doubleBox));

        Box<Double> emptyBox = new Box<>();
        assertNull(emptyBox.getItem());
        assertEquals(0.0, emptyBox.doubleValue());
    }

    // =========================================================================
    // 3. Generic max() Tests
    // =========================================================================

    @Test
    @DisplayName("max() returns maximum comparable element for collections and varargs")
    void testGenericMax() {
        // Test with Integers
        List<Integer> numbers = Arrays.asList(5, 12, 99, 3, 44);
        assertEquals(99, GenericToolkit.max(numbers));
        assertEquals(99, GenericToolkit.max(5, 12, 99, 3, 44));

        // Test with Strings
        List<String> words = Arrays.asList("apple", "zebra", "banana");
        assertEquals("zebra", GenericToolkit.max(words));

        // Test with Products (natural ordering by price)
        Product pCheap = new Product(1, "Pen", "Office", 1.50, 100);
        Product pExpensive = new Product(2, "Laptop", "Electronics", 1200.00, 5);
        Product pMid = new Product(3, "Desk", "Furniture", 250.00, 10);

        assertEquals(pExpensive, GenericToolkit.max(Arrays.asList(pCheap, pExpensive, pMid)));
    }

    @Test
    @DisplayName("max() throws NoSuchElementException on empty collection")
    void testGenericMaxEmpty() {
        assertThrows(NoSuchElementException.class, () -> GenericToolkit.max(new ArrayList<Integer>()));
    }

    // =========================================================================
    // 4. Wildcard Tests (Upper-bounded, Lower-bounded, PECS copy)
    // =========================================================================

    @Test
    @DisplayName("sumOfNumbers accepts any List<? extends Number>")
    void testUpperBoundedWildcard() {
        List<Integer> ints = Arrays.asList(10, 20, 30);
        List<Double> doubles = Arrays.asList(1.5, 2.5, 3.5);

        assertEquals(60.0, GenericToolkit.sumOfNumbers(ints), 0.001);
        assertEquals(7.5, GenericToolkit.sumOfNumbers(doubles), 0.001);
    }

    @Test
    @DisplayName("addIntegers writes into List<? super Integer>")
    void testLowerBoundedWildcard() {
        List<Number> numList = new ArrayList<>();
        GenericToolkit.addIntegers(numList, 1, 3);

        assertEquals(3, numList.size());
        assertEquals(1, numList.get(0));
        assertEquals(2, numList.get(1));
        assertEquals(3, numList.get(2));
    }

    @Test
    @DisplayName("copy demonstrates PECS: source (? extends T) to dest (? super T)")
    void testPecsCopy() {
        List<Integer> src = Arrays.asList(10, 20, 30);
        List<Number> dest = new ArrayList<>();

        GenericToolkit.copy(src, dest);
        assertEquals(Arrays.asList(10, 20, 30), dest);
    }

    // =========================================================================
    // 5. Try-with-resources CSV Reader Tests
    // =========================================================================

    @Test
    @DisplayName("ProductCsvReader parses products.csv correctly with try-with-resources")
    void testProductCsvReader() throws IOException {
        String filePath = "products.csv";
        if (!new File(filePath).exists()) {
            filePath = "src/main/resources/products.csv";
        }

        List<Product> products = ProductCsvReader.readFromFile(filePath);
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertTrue(products.size() >= 5);

        Product first = products.get(0);
        assertEquals(101, first.getId());
        assertEquals("Mechanical Keyboard", first.getName());
        assertEquals("Electronics", first.getCategory());
        assertEquals(89.99, first.getPrice(), 0.001);
        assertEquals(15, first.getQuantity());
    }

    // =========================================================================
    // 6. Custom AutoCloseable Close Order Tests (LIFO)
    // =========================================================================

    @Test
    @DisplayName("AutoCloseable closes resources in reverse order of declaration (LIFO)")
    void testAutoCloseableCloseOrder() {
        try (CustomResource r1 = new CustomResource("Res-1");
             CustomResource r2 = new CustomResource("Res-2");
             CustomResource r3 = new CustomResource("Res-3")) {

            r1.doWork();
            r2.doWork();
            r3.doWork();
            assertFalse(r1.isClosed());
            assertFalse(r2.isClosed());
            assertFalse(r3.isClosed());
        }

        List<String> closeOrder = CustomResource.getCloseHistory();
        assertEquals(3, closeOrder.size());

        // LIFO Order: Res-3 must close first, then Res-2, then Res-1
        assertEquals("Res-3", closeOrder.get(0));
        assertEquals("Res-2", closeOrder.get(1));
        assertEquals("Res-1", closeOrder.get(2));
    }
}
