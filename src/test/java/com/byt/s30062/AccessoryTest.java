package com.byt.s30062;

import com.byt.s30062.model.Accessory;
import com.byt.s30062.model.Product;
import com.byt.s30062.model.enums.AccessoryType;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryTest {

    @BeforeEach
    void setup() {
        Product.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Product.clearExtent();
        new File("product_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create accessory with valid attributes")
    void testValidAccessory() {
        Accessory accessory = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        assertEquals("iPhone Case", accessory.getName());
        assertEquals(29.99, accessory.getCurrentPrice());
        assertEquals(AccessoryType.PhoneCase, accessory.getType());
    }

    @Test
    @DisplayName("Should reject null accessory type")
    void testNullType() {
        assertThrows(IllegalArgumentException.class,
            () -> new Accessory("iPhone Case", "Blue", 29.99, null));
    }

    @Test
    @DisplayName("Should set type with validation")
    void testSetType() {
        Accessory accessory = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        accessory.setType(AccessoryType.LaptopBag);
        assertEquals(AccessoryType.LaptopBag, accessory.getType());
        
        assertThrows(IllegalArgumentException.class, () -> accessory.setType(null));
    }

    @Test
    @DisplayName("Should handle all accessory types")
    void testAllAccessoryTypes() {
        Accessory ac1 = new Accessory("Phone Case", "Red", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("Laptop Bag", "Black", 49.99, AccessoryType.LaptopBag);
        Accessory ac3 = new Accessory("Laptop Sleeve", "Gray", 39.99, AccessoryType.LaptopSleeve);
        Accessory ac4 = new Accessory("AirTag Keychain", "White", 19.99, AccessoryType.AirTagKeychain);
        Accessory ac5 = new Accessory("AirPods Etui", "Silver", 24.99, AccessoryType.AirPodsEtui);
        
        assertEquals(AccessoryType.PhoneCase, ac1.getType());
        assertEquals(AccessoryType.LaptopBag, ac2.getType());
        assertEquals(AccessoryType.LaptopSleeve, ac3.getType());
        assertEquals(AccessoryType.AirTagKeychain, ac4.getType());
        assertEquals(AccessoryType.AirPodsEtui, ac5.getType());
    }

    @Test
    @DisplayName("Should inherit Product validations")
    void testInheritedProductValidations() {
        // Null name
        assertThrows(IllegalArgumentException.class,
            () -> new Accessory(null, "Blue", 29.99, AccessoryType.PhoneCase));
        
        // Blank name
        assertThrows(IllegalArgumentException.class,
            () -> new Accessory("", "Blue", 29.99, AccessoryType.PhoneCase));
        
        // Negative price
        assertThrows(IllegalArgumentException.class,
            () -> new Accessory("iPhone Case", "Blue", -10.0, AccessoryType.PhoneCase));
        
        // Zero price
        assertThrows(IllegalArgumentException.class,
            () -> new Accessory("iPhone Case", "Blue", 0.0, AccessoryType.PhoneCase));
    }

    @Test
    @DisplayName("Should update price inherited from Product")
    void testInheritedPriceUpdate() {
        Accessory accessory = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        assertEquals(29.99, accessory.getCurrentPrice());
        
        accessory.updatePrice(24.99);
        assertEquals(24.99, accessory.getCurrentPrice());
        
        assertEquals(2, accessory.getPriceHistory().size());
        assertEquals(29.99, accessory.getPriceHistory().get(0));
        assertEquals(24.99, accessory.getPriceHistory().get(1));
    }

    @Test
    @DisplayName("Should store Accessory in Product extent (inheritance)")
    void testInheritance() {
        Product regularProduct = new Product("AirPods", "White", 199.0);
        Accessory accessory = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        // Both should be in Product extent
        assertEquals(2, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(regularProduct));
        assertTrue(Product.getExtent().contains(accessory));
        
        // Verify Accessory type is preserved
        assertTrue(Product.getExtent().stream().anyMatch(p -> p instanceof Accessory));
    }

    @Test
    @DisplayName("Should implement equals correctly with different types")
    void testEqualsWithDifferentTypes() {
        Accessory ac1 = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.LaptopBag);
        Accessory ac3 = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        // Different types - not equal
        assertNotEquals(ac1, ac2);
        
        // Same name, color (implicit in Product name comparison), and type - equal
        assertEquals(ac1, ac3);
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void testHashCode() {
        Accessory ac1 = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        assertEquals(ac1.hashCode(), ac2.hashCode());
    }

    @Test
    @DisplayName("Should persist Accessory with Product extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Product regularProduct = new Product("AirPods", "White", 199.0);
        Accessory accessory = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        
        assertEquals(2, Product.getExtent().size());
        
        // Verify Accessory type and attributes are preserved
        Accessory loadedAccessory = (Accessory) Product.getExtent().stream()
            .filter(p -> p instanceof Accessory)
            .findFirst()
            .orElseThrow();
        
        assertEquals("iPhone Case", loadedAccessory.getName());
        assertEquals(29.99, loadedAccessory.getCurrentPrice());
        assertEquals(AccessoryType.PhoneCase, loadedAccessory.getType());
    }

    @Test
    @DisplayName("Should handle multiple accessories with different types")
    void testMultipleAccessories() {
        Accessory ac1 = new Accessory("Phone Case", "Red", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("Laptop Bag", "Black", 49.99, AccessoryType.LaptopBag);
        Accessory ac3 = new Accessory("Laptop Sleeve", "Gray", 39.99, AccessoryType.LaptopSleeve);
        
        assertEquals(3, Product.getExtent().size());
        assertEquals(AccessoryType.PhoneCase, ac1.getType());
        assertEquals(AccessoryType.LaptopBag, ac2.getType());
        assertEquals(AccessoryType.LaptopSleeve, ac3.getType());
    }

    @Test
    @DisplayName("Should handle optional color field")
    void testOptionalColor() {
        Accessory ac1 = new Accessory("Phone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("Phone Case", null, 29.99, AccessoryType.PhoneCase);
        
        assertEquals("Phone Case", ac1.getName());
        assertEquals("Phone Case", ac2.getName());
    }

    @Test
    @DisplayName("Should calculate total price correctly with multiple items")
    void testMultipleAccessoriesPricing() {
        Accessory ac1 = new Accessory("Phone Case", "Red", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("Laptop Bag", "Black", 49.99, AccessoryType.LaptopBag);
        Accessory ac3 = new Accessory("Laptop Sleeve", "Gray", 39.99, AccessoryType.LaptopSleeve);
        
        double total = ac1.getCurrentPrice() + ac2.getCurrentPrice() + ac3.getCurrentPrice();
        assertEquals(119.97, total, 0.01);
    }
}
