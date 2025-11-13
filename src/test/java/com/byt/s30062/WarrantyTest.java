package com.byt.s30062;

import com.byt.s30062.model.Customer;
import com.byt.s30062.model.Product;
import com.byt.s30062.model.Purchase;
import com.byt.s30062.model.Warranty;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WarrantyTest {

    @BeforeEach
    void setup() {
        Warranty.clearExtent();
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Warranty.clearExtent();
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
        new File("warranty_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create warranty with valid attributes")
    void testValidWarranty() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Purchase purchase = new Purchase(c, Arrays.asList(p));
        
        LocalDate endDate = LocalDate.now().plusYears(2);
        Warranty w = new Warranty(purchase, p, endDate);
        
        assertEquals(purchase, w.getPurchase());
        assertEquals(p, w.getProduct());
        assertEquals(endDate, w.getEndDate());
    }

    @Test
    @DisplayName("Should calculate derived attributes correctly")
    void testDerivedAttributes() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Purchase purchase = new Purchase(c, Arrays.asList(p));
        
        LocalDate endDate = LocalDate.now().plusYears(2);
        Warranty w = new Warranty(purchase, p, endDate);
        
        // getStartDate is derived from purchase date
        assertEquals(purchase.getPurchaseDate().toLocalDate(), w.getStartDate());
        
        // isValid is derived
        assertTrue(w.isValid());
    }

    @Test
    @DisplayName("Should reject invalid inputs")
    void testValidations() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Purchase purchase = new Purchase(c, Arrays.asList(p));
        
        // Null inputs
        assertThrows(IllegalArgumentException.class, 
            () -> new Warranty(null, p, LocalDate.now().plusYears(2)));
        assertThrows(IllegalArgumentException.class, 
            () -> new Warranty(purchase, null, LocalDate.now().plusYears(2)));
        assertThrows(IllegalArgumentException.class, 
            () -> new Warranty(purchase, p, null));
        
        // End date before purchase date
        assertThrows(IllegalArgumentException.class, 
            () -> new Warranty(purchase, p, LocalDate.now().minusDays(1)));
        
        // Less than minimum period (1 year)
        assertThrows(IllegalArgumentException.class, 
            () -> new Warranty(purchase, p, LocalDate.now().plusMonths(6)));
        
        // Product not in purchase
        Product otherProduct = new Product("iPad", "Silver", 799.0);
        assertThrows(IllegalArgumentException.class, 
            () -> new Warranty(purchase, otherProduct, LocalDate.now().plusYears(2)));
    }

    @Test
    @DisplayName("Should prolong warranty correctly")
    void testProlong() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Purchase purchase = new Purchase(c, Arrays.asList(p));
        
        LocalDate endDate = LocalDate.now().plusYears(2);
        Warranty w = new Warranty(purchase, p, endDate);
        
        int originalExtentSize = Warranty.getExtent().size();
        
        // Prolong creates a new warranty
        w.prolong(Period.ofYears(1));
        
        assertEquals(originalExtentSize + 1, Warranty.getExtent().size());
    }

    @Test
    @DisplayName("Should store warranties in extent")
    void testExtent() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Purchase purchase = new Purchase(c, Arrays.asList(p));
        
        assertEquals(0, Warranty.getExtent().size());
        
        Warranty w1 = new Warranty(purchase, p, LocalDate.now().plusYears(2));
        assertEquals(1, Warranty.getExtent().size());
        
        Product p2 = new Product("iPad", "Silver", 799.0);
        Purchase purchase2 = new Purchase(c, Arrays.asList(p2));
        Warranty w2 = new Warranty(purchase2, p2, LocalDate.now().plusYears(3));
        assertEquals(2, Warranty.getExtent().size());
        
        assertTrue(Warranty.getExtent().contains(w1));
        assertTrue(Warranty.getExtent().contains(w2));
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Purchase purchase = new Purchase(c, Arrays.asList(p));
        Warranty w = new Warranty(purchase, p, LocalDate.now().plusYears(2));
        
        var extent1 = Warranty.getExtent();
        var extent2 = Warranty.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, Warranty.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "Silver", 799.0);
        Purchase pu1 = new Purchase(c, Arrays.asList(p1));
        Purchase pu2 = new Purchase(c, Arrays.asList(p2));
        
        Warranty w1 = new Warranty(pu1, p1, LocalDate.now().plusYears(2));
        Warranty w2 = new Warranty(pu2, p2, LocalDate.now().plusYears(3));
        
        Warranty.saveExtent();
        assertTrue(new File("warranty_extent.ser").exists());
        
        Warranty.clearExtent();
        assertEquals(0, Warranty.getExtent().size());
        
        Warranty.loadExtent();
        assertEquals(2, Warranty.getExtent().size());
    }
}
