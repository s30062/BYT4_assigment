package com.byt.s30062;

import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Report;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportManagerAssociationTest {

    @BeforeEach
    void setup() {
        Manager.clearExtent();
        Report.clearExtent();
    }

    @Test
    @DisplayName("Should link Report to Manager and maintain forward and reverse links")
    void testReportLinksToManager() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        String content = "This is a comprehensive report about Q1 performance.";
        
        assertEquals(0, m.getReports().size());
        
        // Create report
        Report report = new Report(m, content);
        
        // Forward link verified
        assertEquals(m, report.getManager());
        
        // Reverse link verified
        assertEquals(1, m.getReports().size());
        assertTrue(m.getReports().contains(report));
    }

    @Test
    @DisplayName("Should support multiple reports per Manager")
    void testMultipleReportsPerManager() {
        Manager m = new Manager("Jane", "Doe", LocalDate.of(1985, 3, 15), 6000.0, false);
        
        String content1 = "This is the first comprehensive monthly report.";
        String content2 = "This is the second comprehensive monthly report.";
        String content3 = "This is the third comprehensive monthly report.";
        
        Report report1 = new Report(m, content1);
        Report report2 = new Report(m, content2);
        Report report3 = new Report(m, content3);
        
        assertEquals(3, m.getReports().size());
        assertTrue(m.getReports().contains(report1));
        assertTrue(m.getReports().contains(report2));
        assertTrue(m.getReports().contains(report3));
    }

    @Test
    @DisplayName("Should allow Manager to have no reports initially")
    void testManagerWithoutReports() {
        Manager m = new Manager("Bob", "Johnson", LocalDate.of(1975, 7, 20), 7000.0, false);
        
        assertEquals(0, m.getReports().size());
    }

    @Test
    @DisplayName("Should maintain Report reference to Manager immutably")
    void testReportManagerReferenceImmutable() {
        Manager m1 = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        Manager m2 = new Manager("Jane", "Doe", LocalDate.of(1985, 3, 15), 6000.0, false);
        
        String content = "This is a comprehensive quarterly performance report.";
        Report report = new Report(m1, content);
        
        // Report references m1
        assertEquals(m1, report.getManager());
        
        // m1 contains report
        assertEquals(1, m1.getReports().size());
        
        // m2 does not contain report
        assertEquals(0, m2.getReports().size());
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency with multiple managers")
    void testBidirectionalConsistencyMultipleManagers() {
        Manager m1 = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        Manager m2 = new Manager("Jane", "Doe", LocalDate.of(1985, 3, 15), 6000.0, false);
        
        String content1 = "This is the first comprehensive departmental report.";
        String content2 = "This is the second comprehensive departmental report.";
        
        Report report1 = new Report(m1, content1);
        Report report2 = new Report(m2, content2);
        
        // Each manager has their own report
        assertEquals(1, m1.getReports().size());
        assertEquals(1, m2.getReports().size());
        
        // Reports reference correct managers
        assertEquals(m1, report1.getManager());
        assertEquals(m2, report2.getManager());
        
        // Cross-manager checks
        assertFalse(m1.getReports().contains(report2));
        assertFalse(m2.getReports().contains(report1));
    }

    @Test
    @DisplayName("Should remove Report from system when unlinking from Manager side")
    void testUnlinkFromManagerSideDeletesReport() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        
        String content1 = "This is the first comprehensive performance analysis.";
        String content2 = "This is the second comprehensive performance analysis.";
        
        Report report1 = new Report(m, content1);
        Report report2 = new Report(m, content2);
        
        assertEquals(2, m.getReports().size());
        assertEquals(2, Report.getExtent().size());
        
        // Unlink from manager side
        m.removeReport(report1);
        
        // Removed from manager's reports
        assertEquals(1, m.getReports().size());
        assertFalse(m.getReports().contains(report1));
        assertTrue(m.getReports().contains(report2));
        
        // Removed from extent (mandatory relationship violated)
        assertEquals(1, Report.getExtent().size());
        assertFalse(Report.getExtent().contains(report1));
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency when unlinking via delete")
    void testBidirectionalConsistencyOnDelete() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        
        String content1 = "This is the first comprehensive quarterly business report.";
        String content2 = "This is the second comprehensive quarterly business report.";
        
        Report report1 = new Report(m, content1);
        Report report2 = new Report(m, content2);
        
        // Verify both linked
        assertEquals(2, m.getReports().size());
        assertEquals(m, report1.getManager());
        assertEquals(m, report2.getManager());
        
        // Delete first report
        report1.delete();
        
        // Second report still linked
        assertEquals(1, m.getReports().size());
        assertTrue(m.getReports().contains(report2));
        assertEquals(m, report2.getManager());
        
        // Delete second report
        report2.delete();
        
        // Manager has no reports
        assertEquals(0, m.getReports().size());
        assertEquals(0, Report.getExtent().size());
    }

    @Test
    @DisplayName("Should enforce mandatory relationship - Report cannot exist without Manager")
    void testMandatoryManagerRelationship() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        String content = "This is a comprehensive mandatory relationship test report.";
        
        Report report = new Report(m, content);
        
        // Report must have a manager
        assertEquals(m, report.getManager());
        assertTrue(m.getReports().contains(report));
        
        // Unlinking from manager removes report from system
        m.removeReport(report);
        
        assertEquals(0, Report.getExtent().size());
        assertEquals(0, m.getReports().size());
    }

    @Test
    @DisplayName("Should prevent null Manager in Report")
    void testNullManagerRejected() {
        String content = "This should be rejected due to null manager.";
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(null, content));
    }

    @Test
    @DisplayName("Should provide defensive copy of reports list")
    void testGetReportsReturnsDefensiveCopy() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        String content = "This is a defensive copy test report content here.";
        
        Report report = new Report(m, content);
        
        var reports1 = m.getReports();
        var reports2 = m.getReports();
        
        // Different list instances
        assertNotSame(reports1, reports2);
        
        // Same content
        assertEquals(reports1, reports2);
        assertEquals(1, reports1.size());
        
        // Modifying returned list doesn't affect manager
        reports1.clear();
        assertEquals(1, m.getReports().size());
    }

    @Test
    @DisplayName("Should maintain Report-Manager link in extent")
    void testReportManagerLinkInExtent() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        
        String content1 = "This is the first comprehensive extent test report.";
        String content2 = "This is the second comprehensive extent test report.";
        
        Report report1 = new Report(m, content1);
        Report report2 = new Report(m, content2);
        
        // Both reports in extent
        assertTrue(Report.getExtent().contains(report1));
        assertTrue(Report.getExtent().contains(report2));
        
        // Both linked to manager
        assertEquals(2, m.getReports().size());
    }

    @Test
    @DisplayName("Should correctly count reports across different managers")
    void testReportCountingMultipleManagers() {
        Manager m1 = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        Manager m2 = new Manager("Jane", "Doe", LocalDate.of(1985, 3, 15), 6000.0, false);
        Manager m3 = new Manager("Bob", "Johnson", LocalDate.of(1975, 7, 20), 7000.0, false);
        
        // m1 has 2 reports
        Report r1 = new Report(m1, "This is the first comprehensive counting test report.".repeat(2));
        Report r2 = new Report(m1, "This is the second comprehensive counting test report.".repeat(2));
        
        // m2 has 1 report
        Report r3 = new Report(m2, "This is the third comprehensive counting test report.".repeat(2));
        
        // m3 has 1 report
        Report r4 = new Report(m3, "This is the fourth comprehensive counting test report.".repeat(2));
        
        assertEquals(2, m1.getReports().size());
        assertEquals(1, m2.getReports().size());
        assertEquals(1, m3.getReports().size());
        assertEquals(4, Report.getExtent().size());
    }

    @Test
    @DisplayName("Should handle sequential unlinking of all reports")
    void testSequentialUnlinkingAllReports() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        
        String content1 = "This is the first comprehensive sequential unlinking test.".repeat(2);
        String content2 = "This is the second comprehensive sequential unlinking test.".repeat(2);
        String content3 = "This is the third comprehensive sequential unlinking test.".repeat(2);
        
        Report report1 = new Report(m, content1);
        Report report2 = new Report(m, content2);
        Report report3 = new Report(m, content3);
        
        assertEquals(3, m.getReports().size());
        
        // Unlink all reports
        m.removeReport(report1);
        assertEquals(2, m.getReports().size());
        
        m.removeReport(report2);
        assertEquals(1, m.getReports().size());
        
        m.removeReport(report3);
        assertEquals(0, m.getReports().size());
        assertEquals(0, Report.getExtent().size());
    }

    @Test
    @DisplayName("Should reject invalid report content")
    void testReportContentValidation() {
        Manager m = new Manager("John", "Smith", LocalDate.of(1980, 5, 10), 5000.0, false);
        
        // Too short
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, "short"));
        
        // Empty
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, ""));
        
        // Null
        assertThrows(IllegalArgumentException.class, 
            () -> new Report(m, null));
    }
}
