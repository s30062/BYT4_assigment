package com.byt.s30062;

import com.byt.s30062.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ReportPurchaseAssociationTest {

    @BeforeEach
    void setup() {
        Report.clearExtent();
        Purchase.clearExtent();
        Manager.clearExtent();
        Customer.clearExtent();
        Product.clearExtent();
        Unit.clearExtent();
    }

    private Manager createManager() {
        return new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
    }

    private Customer createCustomer() {
        return new Customer("Jane", "Doe", LocalDate.of(1995, 3, 15), LocalDate.now());
    }

    private Purchase createPurchase(Customer customer) {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(customer);
        p.addToCart(purchase, u);
        return purchase;
    }

    @Test
    @DisplayName("Should link Report to Purchase and maintain bidirectional link")
    void testReportLinksToPurchase() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive quarterly report.");
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        assertEquals(0, r.getPurchases().size());
        assertEquals(0, p.getReports().size());
        
        r.addPurchase(p);
        
        // Forward link
        assertEquals(1, r.getPurchases().size());
        assertTrue(r.getPurchases().contains(p));
        
        // Reverse link
        assertEquals(1, p.getReports().size());
        assertTrue(p.getReports().contains(r));
    }

    @Test
    @DisplayName("Should support multiple reports per purchase")
    void testMultipleReportsPerPurchase() {
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        Manager m1 = createManager();
        Manager m2 = new Manager("Jane", "Johnson", LocalDate.of(1985, 7, 20), 6000.0, false);
        
        Report r1 = new Report(m1, "This is the first comprehensive quarterly report.");
        Report r2 = new Report(m2, "This is the second comprehensive quarterly report.");
        Report r3 = new Report(m1, "This is the third comprehensive quarterly report.");
        
        r1.addPurchase(p);
        r2.addPurchase(p);
        r3.addPurchase(p);
        
        assertEquals(3, p.getReports().size());
        assertTrue(p.getReports().contains(r1));
        assertTrue(p.getReports().contains(r2));
        assertTrue(p.getReports().contains(r3));
    }

    @Test
    @DisplayName("Should support multiple purchases per report")
    void testMultiplePurchasesPerReport() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive analysis of multiple purchases.");
        
        Customer c1 = createCustomer();
        Customer c2 = new Customer("Bob", "Wilson", LocalDate.of(1990, 5, 15), LocalDate.now());
        
        Purchase p1 = createPurchase(c1);
        Product p2Prod = new Product("iPad", "White", 599.0);
        Unit p2Unit = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2Prod);
        Purchase p2 = new Purchase(c2);
        p2Prod.addToCart(p2, p2Unit);
        
        r.addPurchase(p1);
        r.addPurchase(p2);
        
        assertEquals(2, r.getPurchases().size());
        assertTrue(r.getPurchases().contains(p1));
        assertTrue(r.getPurchases().contains(p2));
    }

    @Test
    @DisplayName("Should remove report from purchase via removePurchase")
    void testRemovePurchaseFromReport() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive report for removal testing.");
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        r.addPurchase(p);
        assertEquals(1, r.getPurchases().size());
        assertEquals(1, p.getReports().size());
        
        r.removePurchase(p);
        
        assertEquals(0, r.getPurchases().size());
        assertEquals(0, p.getReports().size());
    }

    @Test
    @DisplayName("Should remove report from purchase via removeReport")
    void testRemoveReportFromPurchase() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive report for removal testing.");
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        r.addPurchase(p);
        assertEquals(1, r.getPurchases().size());
        assertEquals(1, p.getReports().size());
        
        p.removeReport(r);
        
        assertEquals(0, r.getPurchases().size());
        assertEquals(0, p.getReports().size());
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency when unlinking")
    void testBidirectionalConsistencyOnUnlink() {
        Manager m = createManager();
        Report r1 = new Report(m, "This is the first comprehensive bidirectional test.");
        Report r2 = new Report(m, "This is the second comprehensive bidirectional test.");
        
        Customer c = createCustomer();
        Purchase p1 = createPurchase(c);
        Product p2Prod = new Product("MacBook", "Silver", 1999.0);
        Unit p2Unit = new Unit(LocalDate.of(2024, 1, 17), "SN003", p2Prod);
        Purchase p2 = new Purchase(c);
        p2Prod.addToCart(p2, p2Unit);
        
        r1.addPurchase(p1);
        r1.addPurchase(p2);
        r2.addPurchase(p1);
        
        // Remove via Report
        r1.removePurchase(p1);
        assertFalse(r1.getPurchases().contains(p1));
        assertFalse(p1.getReports().contains(r1));
        assertTrue(p1.getReports().contains(r2));
        
        // Remove via Purchase
        p2.removeReport(r1);
        assertFalse(r1.getPurchases().contains(p2));
        assertFalse(p2.getReports().contains(r1));
    }

    @Test
    @DisplayName("Should prevent null purchase in addPurchase")
    void testNullPurchaseRejected() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive null testing report.");
        
        r.addPurchase(null);
        assertEquals(0, r.getPurchases().size());
    }

    @Test
    @DisplayName("Should prevent null report in addReport")
    void testNullReportRejected() {
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        p.addReport(null);
        assertEquals(0, p.getReports().size());
    }

    @Test
    @DisplayName("Should provide defensive copy of purchases list in Report")
    void testGetPurchasesReturnsDefensiveCopy() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive defensive copy test.");
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        r.addPurchase(p);
        
        var purchases1 = r.getPurchases();
        var purchases2 = r.getPurchases();
        
        assertNotSame(purchases1, purchases2);
        assertEquals(purchases1, purchases2);
        assertEquals(1, purchases1.size());
        
        purchases1.clear();
        assertEquals(1, r.getPurchases().size());
    }

    @Test
    @DisplayName("Should provide defensive copy of reports list in Purchase")
    void testGetReportsReturnsDefensiveCopy() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive defensive copy test.");
        Customer c = createCustomer();
        Purchase p = createPurchase(c);
        
        r.addPurchase(p);
        
        var reports1 = p.getReports();
        var reports2 = p.getReports();
        
        assertNotSame(reports1, reports2);
        assertEquals(reports1, reports2);
        assertEquals(1, reports1.size());
        
        reports1.clear();
        assertEquals(1, p.getReports().size());
    }

    @Test
    @DisplayName("Should handle complex many-to-many scenarios")
    void testComplexManyToMany() {
        Manager m1 = createManager();
        Manager m2 = new Manager("Jane", "Johnson", LocalDate.of(1985, 7, 20), 6000.0, false);
        
        Report r1 = new Report(m1, "This is the first comprehensive complex report.");
        Report r2 = new Report(m2, "This is the second comprehensive complex report.");
        Report r3 = new Report(m1, "This is the third comprehensive complex report.");
        
        Customer c1 = createCustomer();
        Customer c2 = new Customer("Bob", "Wilson", LocalDate.of(1990, 5, 15), LocalDate.now());
        
        Purchase p1 = createPurchase(c1);
        Product p2Prod = new Product("iPad", "White", 599.0);
        Unit p2Unit = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2Prod);
        Purchase p2 = new Purchase(c2);
        p2Prod.addToCart(p2, p2Unit);
        
        // r1 linked to p1 and p2
        r1.addPurchase(p1);
        r1.addPurchase(p2);
        
        // r2 linked to p1
        r2.addPurchase(p1);
        
        // r3 linked to p2
        r3.addPurchase(p2);
        
        // Verify
        assertEquals(2, r1.getPurchases().size());
        assertEquals(1, r2.getPurchases().size());
        assertEquals(1, r3.getPurchases().size());
        
        assertEquals(2, p1.getReports().size());
        assertEquals(2, p2.getReports().size());
        
        // Remove r1 from p1
        r1.removePurchase(p1);
        
        assertEquals(1, r1.getPurchases().size());
        assertEquals(1, p1.getReports().size());
        assertTrue(p1.getReports().contains(r2));
    }

    @Test
    @DisplayName("Should handle removing non-existent purchase gracefully")
    void testRemoveNonExistentPurchase() {
        Manager m = createManager();
        Report r = new Report(m, "This is a comprehensive report for non-existent removal.");
        Customer c = createCustomer();
        Purchase p1 = createPurchase(c);
        Product p2Prod = new Product("iPad", "White", 599.0);
        Unit p2Unit = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2Prod);
        Purchase p2 = new Purchase(c);
        p2Prod.addToCart(p2, p2Unit);
        
        r.addPurchase(p1);
        
        // Remove p2 which is not in r
        r.removePurchase(p2);
        
        // p1 still linked
        assertEquals(1, r.getPurchases().size());
        assertTrue(r.getPurchases().contains(p1));
    }
}
