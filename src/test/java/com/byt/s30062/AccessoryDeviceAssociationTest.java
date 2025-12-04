package com.byt.s30062;

import com.byt.s30062.model.Accessory;
import com.byt.s30062.model.Device;
import com.byt.s30062.model.enums.AccessoryType;
import com.byt.s30062.model.enums.Line;
import com.byt.s30062.model.enums.PortType;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryDeviceAssociationTest {

    @BeforeEach
    void setup() {
        Device.clearExtent();
    }

    private Device createDevice(String name, Line line) {
        return new Device(line, Arrays.asList(PortType.UsbC), LocalDate.of(2023, 9, 15), name, "Black", 999.0);
    }

    @Test
    @DisplayName("Should add device to accessory's designedFor with device name as qualifier")
    void testAddDesignedForDevice() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d = createDevice("iPhone 15", Line.iPhone);
        
        assertEquals(0, ac.getAllDesignedFor().size());
        
        ac.addDesignedFor(d);
        
        assertEquals(1, ac.getAllDesignedFor().size());
        assertTrue(ac.getAllDesignedFor().containsKey("iPhone 15"));
        assertEquals(d, ac.getDesignedForByName("iPhone 15"));
    }

    @Test
    @DisplayName("Should maintain bidirectional link when adding device")
    void testBidirectionalLinkOnAdd() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d = createDevice("iPhone 15", Line.iPhone);
        
        ac.addDesignedFor(d);
        
        // Forward link
        assertEquals(d, ac.getDesignedForByName("iPhone 15"));
        
        // Reverse link
        assertTrue(d.getAccessories().contains(ac));
    }

    @Test
    @DisplayName("Should support multiple devices for one accessory")
    void testMultipleDevicesPerAccessory() {
        Accessory ac = new Accessory("Universal Case", "Black", 19.99, AccessoryType.PhoneCase);
        Device d1 = createDevice("iPhone 15", Line.iPhone);
        Device d2 = new Device(Line.iPhone, Arrays.asList(PortType.Lightning), LocalDate.of(2023, 9, 15), "iPhone 14", "Black", 899.0);
        Device d3 = new Device(Line.iPad, Arrays.asList(PortType.UsbC), LocalDate.of(2023, 9, 15), "iPad Air", "Silver", 999.0);
        
        ac.addDesignedFor(d1);
        ac.addDesignedFor(d2);
        ac.addDesignedFor(d3);
        
        assertEquals(3, ac.getAllDesignedFor().size());
        assertTrue(ac.getAllDesignedFor().containsKey("iPhone 15"));
        assertTrue(ac.getAllDesignedFor().containsKey("iPhone 14"));
        assertTrue(ac.getAllDesignedFor().containsKey("iPad Air"));
    }

    @Test
    @DisplayName("Should retrieve device by name qualifier")
    void testGetDesignedForByName() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d = createDevice("iPhone 15", Line.iPhone);
        
        ac.addDesignedFor(d);
        
        Device retrieved = ac.getDesignedForByName("iPhone 15");
        assertEquals(d, retrieved);
    }

    @Test
    @DisplayName("Should return null for non-existent device name")
    void testGetDesignedForByNameNotFound() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        assertNull(ac.getDesignedForByName("NonExistent"));
    }

    @Test
    @DisplayName("Should prevent duplicate device associations")
    void testNoDuplicateDevices() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d = createDevice("iPhone 15", Line.iPhone);
        
        ac.addDesignedFor(d);
        
        // Try to add same device again
        assertThrows(IllegalArgumentException.class,
            () -> ac.addDesignedFor(d));
        
        // Still only one device
        assertEquals(1, ac.getAllDesignedFor().size());
    }

    @Test
    @DisplayName("Should prevent null device")
    void testNullDeviceRejected() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        
        assertThrows(IllegalArgumentException.class,
            () -> ac.addDesignedFor(null));
    }

    @Test
    @DisplayName("Should remove device from qualified association")
    void testRemoveDesignedFor() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d1 = createDevice("iPhone 15", Line.iPhone);
        Device d2 = new Device(Line.iPhone, Arrays.asList(PortType.Lightning), LocalDate.of(2023, 9, 15), "iPhone 14", "Black", 899.0);
        
        ac.addDesignedFor(d1);
        ac.addDesignedFor(d2);
        
        assertEquals(2, ac.getAllDesignedFor().size());
        
        // Remove one device
        ac.removeDesignedFor("iPhone 15");
        
        assertEquals(1, ac.getAllDesignedFor().size());
        assertNull(ac.getDesignedForByName("iPhone 15"));
        assertEquals(d2, ac.getDesignedForByName("iPhone 14"));
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency when removing")
    void testBidirectionalConsistencyOnRemove() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d = createDevice("iPhone 15", Line.iPhone);
        
        ac.addDesignedFor(d);
        assertTrue(d.getAccessories().contains(ac));
        
        ac.removeDesignedFor("iPhone 15");
        
        assertFalse(d.getAccessories().contains(ac));
    }

    @Test
    @DisplayName("Should handle removing non-existent device gracefully")
    void testRemoveNonExistentDevice() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d = createDevice("iPhone 15", Line.iPhone);
        
        ac.addDesignedFor(d);
        
        // Remove non-existent device should not throw
        ac.removeDesignedFor("NonExistent");
        
        // Original device still there
        assertEquals(1, ac.getAllDesignedFor().size());
    }

    @Test
    @DisplayName("Should return defensive copy of all designed for devices")
    void testGetAllDesignedForReturnsCopy() {
        Accessory ac = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Device d1 = createDevice("iPhone 15", Line.iPhone);
        Device d2 = new Device(Line.iPhone, Arrays.asList(PortType.Lightning), LocalDate.of(2023, 9, 15), "iPhone 14", "Black", 899.0);
        
        ac.addDesignedFor(d1);
        ac.addDesignedFor(d2);
        
        var map1 = ac.getAllDesignedFor();
        var map2 = ac.getAllDesignedFor();
        
        // Different instances
        assertNotSame(map1, map2);
        
        // Same content
        assertEquals(map1, map2);
        assertEquals(2, map1.size());
        
        // Modifying returned map doesn't affect accessory
        map1.clear();
        assertEquals(2, ac.getAllDesignedFor().size());
    }

    @Test
    @DisplayName("Should support qualified association with multiple accessories")
    void testMultipleAccessoriesPerDevice() {
        Device d = createDevice("iPhone 15", Line.iPhone);
        Accessory ac1 = new Accessory("iPhone Case", "Blue", 29.99, AccessoryType.PhoneCase);
        Accessory ac2 = new Accessory("Screen Protector", "Clear", 9.99, AccessoryType.PhoneCase);
        Accessory ac3 = new Accessory("Charger Cable", "White", 19.99, AccessoryType.PhoneCase);
        
        ac1.addDesignedFor(d);
        ac2.addDesignedFor(d);
        ac3.addDesignedFor(d);
        
        // Device has all accessories
        assertEquals(3, d.getAccessories().size());
        assertTrue(d.getAccessories().contains(ac1));
        assertTrue(d.getAccessories().contains(ac2));
        assertTrue(d.getAccessories().contains(ac3));
    }

    @Test
    @DisplayName("Should handle accessory designed for multiple devices")
    void testAccessoryForMultipleDevices() {
        Accessory ac = new Accessory("Universal Charger", "White", 39.99, AccessoryType.PhoneCase);
        Device d1 = createDevice("iPhone 15", Line.iPhone);
        Device d2 = new Device(Line.iPhone, Arrays.asList(PortType.Lightning), LocalDate.of(2023, 9, 15), "iPhone 14", "Black", 899.0);
        Device d3 = new Device(Line.iPad, Arrays.asList(PortType.UsbC), LocalDate.of(2023, 9, 15), "iPad Pro", "Silver", 1299.0);
        
        ac.addDesignedFor(d1);
        ac.addDesignedFor(d2);
        ac.addDesignedFor(d3);
        
        assertEquals(3, ac.getAllDesignedFor().size());
        
        // All devices link back to the accessory
        assertTrue(d1.getAccessories().contains(ac));
        assertTrue(d2.getAccessories().contains(ac));
        assertTrue(d3.getAccessories().contains(ac));
    }
}
