package com.byt.s30062;

import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Report;
import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @BeforeEach
    void setup() {
        Report.clearExtent();
        Staff.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Report.clearExtent();
        Staff.clearExtent();
        new File("report_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create report with valid attributes")
    void testValidReport() {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        Report r = new Report(m, "Monthly sales report content here");
        
        assertNotNull(r.getDateGenerated());
        assertEquals("Monthly sales report content here", r.getContent());
    }

    @Test
    @DisplayName("Should reject invalid inputs")
    void testValidations() {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        
        // Null manager
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(null, "Content"));
        
        // Null content
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, null));
        
        // Blank content
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, "   "));
        
        // Content too short
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, "Short"));
        
        // Content too long
        String longContent = "A".repeat(10001);
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, longContent));
    }

    @Test
    @DisplayName("Should store reports in extent")
    void testExtent() {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        
        assertEquals(0, Report.getExtent().size());
        
        Report r1 = new Report(m, "First report content here");
        assertEquals(1, Report.getExtent().size());
        
        Report r2 = new Report(m, "Second report content here");
        assertEquals(2, Report.getExtent().size());
        
        assertTrue(Report.getExtent().contains(r1));
        assertTrue(Report.getExtent().contains(r2));
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        Report r = new Report(m, "Report content here");
        
        var extent1 = Report.getExtent();
        var extent2 = Report.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, Report.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        Report r1 = new Report(m, "First report content");
        Report r2 = new Report(m, "Second report content");
        
        Report.saveExtent();
        assertTrue(new File("report_extent.ser").exists());
        
        Report.clearExtent();
        assertEquals(0, Report.getExtent().size());
        
        Report.loadExtent();
        assertEquals(2, Report.getExtent().size());
    }
}
