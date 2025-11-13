package com.byt.s30062;

import com.byt.s30062.model.Device;
import com.byt.s30062.model.Product;
import com.byt.s30062.model.enums.Line;
import com.byt.s30062.model.enums.PortType;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTest {

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
    @DisplayName("Should create device with valid attributes")
    void testValidDevice() {
        List<PortType> ports = Arrays.asList(PortType.UsbC, PortType.Lightning);
        LocalDate releaseDate = LocalDate.of(2023, 9, 15);
        
        Device device = new Device(Line.iPhone, ports, releaseDate, "iPhone 15", "Blue", 999.0);
        
        assertEquals("iPhone 15", device.getName());
        assertEquals(999.0, device.getCurrentPrice());
        assertEquals(Line.iPhone, device.getLine());
        assertEquals(2, device.getPorts().size());
        assertEquals(releaseDate, device.getReleaseDate());
    }

    @Test
    @DisplayName("Should validate ports list (multi-value attribute)")
    void testPortsValidation() {
        LocalDate releaseDate = LocalDate.of(2023, 9, 15);
        
        // Null ports
        assertThrows(IllegalArgumentException.class,
            () -> new Device(Line.iPhone, null, releaseDate, "iPhone", "Black", 999.0));
        
        // Empty ports
        assertThrows(IllegalArgumentException.class,
            () -> new Device(Line.iPhone, Arrays.asList(), releaseDate, "iPhone", "Black", 999.0));
        
        // Ports with null element
        assertThrows(IllegalArgumentException.class,
            () -> new Device(Line.iPhone, Arrays.asList(PortType.UsbC, null), releaseDate, "iPhone", "Black", 999.0));
    }

    @Test
    @DisplayName("Should validate release date")
    void testReleaseDateValidation() {
        List<PortType> ports = Arrays.asList(PortType.UsbC);
        
        // Future date
        assertThrows(IllegalArgumentException.class,
            () -> new Device(Line.iPhone, ports, LocalDate.now().plusDays(1), "iPhone", "Black", 999.0));
        
        // Too old (before 1970)
        assertThrows(IllegalArgumentException.class,
            () -> new Device(Line.iPhone, ports, LocalDate.of(1969, 12, 31), "iPhone", "Black", 999.0));
    }

    @Test
    @DisplayName("Should maintain encapsulation of ports list")
    void testPortsEncapsulation() {
        List<PortType> ports = Arrays.asList(PortType.UsbC, PortType.Lightning);
        Device device = new Device(Line.iPhone, ports, LocalDate.of(2023, 9, 15), "iPhone 15", "Blue", 999.0);
        
        List<PortType> returnedPorts1 = device.getPorts();
        List<PortType> returnedPorts2 = device.getPorts();
        
        // Should return different instances
        assertNotSame(returnedPorts1, returnedPorts2);
        
        // Modifying returned list shouldn't affect internal state
        returnedPorts1.clear();
        assertEquals(2, device.getPorts().size());
    }

    @Test
    @DisplayName("Should store Device in Product extent (inheritance)")
    void testInheritance() {
        Product regularProduct = new Product("AirPods", "White", 199.0);
        
        List<PortType> ports = Arrays.asList(PortType.Lightning);
        Device device = new Device(Line.iPhone, ports, LocalDate.of(2023, 9, 15), "iPhone 15", "Blue", 999.0);
        
        // Both should be in Product extent
        assertEquals(2, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(regularProduct));
        assertTrue(Product.getExtent().contains(device));
        
        // Verify Device type is preserved
        assertTrue(Product.getExtent().stream().anyMatch(p -> p instanceof Device));
    }

    @Test
    @DisplayName("Should persist Device with Product extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Product regularProduct = new Product("AirPods", "White", 199.0);
        
        List<PortType> ports = Arrays.asList(PortType.UsbC, PortType.Hdmi);
        Device device = new Device(Line.MacBook, ports, LocalDate.of(2023, 10, 1), "MacBook Pro", "Silver", 2499.0);
        
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        
        assertEquals(2, Product.getExtent().size());
        
        // Verify Device type and attributes are preserved
        Device loadedDevice = (Device) Product.getExtent().stream()
            .filter(p -> p instanceof Device)
            .findFirst()
            .orElseThrow();
        
        assertEquals("MacBook Pro", loadedDevice.getName());
        assertEquals(Line.MacBook, loadedDevice.getLine());
        assertEquals(2, loadedDevice.getPorts().size());
        assertTrue(loadedDevice.getPorts().contains(PortType.UsbC));
        assertTrue(loadedDevice.getPorts().contains(PortType.Hdmi));
    }
}
