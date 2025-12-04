package com.byt.s30062;

import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StaffSupervisionAssociationTest {

    private Staff supervisor;
    private Staff intern1;
    private Staff intern2;

    @BeforeEach
    void setup() {
        Staff.clearExtent();
        supervisor = new Staff("Alice", "Supervisor", LocalDate.of(1980, 1, 1), 75000.0, false);
        intern1 = new Staff("Bob", "Intern1", LocalDate.of(2000, 5, 15), 25000.0, true);
        intern2 = new Staff("Carol", "Intern2", LocalDate.of(2001, 8, 20), 25000.0, true);
    }

    @Test
    @DisplayName("Non-intern can supervise an intern via supervise method")
    void testSuperviseIntern() {
        supervisor.supervise(intern1);

        assertEquals(supervisor, intern1.getSupervisor(),
                "Intern should have supervisor set");
        assertTrue(supervisor.getSupervises().contains(intern1),
                "Supervisor should have intern in their supervises list");
        assertEquals(1, supervisor.getSupervises().size());
    }

    @Test
    @DisplayName("Supervisor can supervise multiple interns")
    void testSuperviseManyInterns() {
        supervisor.supervise(intern1);
        supervisor.supervise(intern2);

        assertEquals(2, supervisor.getSupervises().size());
        assertTrue(supervisor.getSupervises().contains(intern1));
        assertTrue(supervisor.getSupervises().contains(intern2));

        assertEquals(supervisor, intern1.getSupervisor());
        assertEquals(supervisor, intern2.getSupervisor());
    }

    @Test
    @DisplayName("Intern can set supervisor via setSupervisor method")
    void testInternSetSupervisor() {
        intern1.setSupervisor(supervisor);

        assertEquals(supervisor, intern1.getSupervisor(),
                "Intern should have supervisor set");
        assertTrue(supervisor.getSupervises().contains(intern1),
                "Supervisor should have intern in their supervises list");
    }

    @Test
    @DisplayName("getSupervises returns defensive copy")
    void testGetSuperviseDefensiveCopy() {
        supervisor.supervise(intern1);

        var supervises1 = supervisor.getSupervises();
        var supervises2 = supervisor.getSupervises();

        assertNotSame(supervises1, supervises2,
                "getSupervises should return different list instances");
        assertEquals(supervises1, supervises2);

        // Modifying returned list should not affect supervisor's internal collection
        supervises1.clear();
        assertEquals(1, supervisor.getSupervises().size());
    }

    @Test
    @DisplayName("Only non-interns can supervise")
    void testOnlyNonInternsCanSupervise() {
        Staff internSupervisor = new Staff("Dave", "InternSuper", LocalDate.of(2000, 3, 1), 25000.0, true);

        assertThrows(IllegalArgumentException.class,
                () -> internSupervisor.supervise(intern1),
                "Interns cannot supervise other interns");
    }

    @Test
    @DisplayName("Only interns can be supervised")
    void testOnlyInternsCanBeSupervised() {
        Staff nonIntern = new Staff("Eve", "NonIntern", LocalDate.of(1985, 6, 10), 60000.0, false);

        assertThrows(IllegalArgumentException.class,
                () -> supervisor.supervise(nonIntern),
                "Non-interns cannot be supervised");
    }

    @Test
    @DisplayName("Staff cannot supervise themselves")
    void testCannotSuperviseSelf() {
        assertThrows(IllegalArgumentException.class,
                () -> supervisor.supervise(supervisor),
                "Staff cannot supervise themselves");

        assertThrows(IllegalArgumentException.class,
                () -> intern1.setSupervisor(intern1),
                "Intern cannot set themselves as supervisor");
    }

    @Test
    @DisplayName("Intern cannot have multiple supervisors simultaneously")
    void testInternCannotHaveMultipleSupervisors() {
        Staff supervisor2 = new Staff("Frank", "Supervisor2", LocalDate.of(1975, 9, 5), 75000.0, false);

        supervisor.supervise(intern1);

        assertThrows(IllegalArgumentException.class,
                () -> supervisor2.supervise(intern1),
                "Intern cannot be supervised by another supervisor");
    }

    @Test
    @DisplayName("Reassigning intern supervisor via setSupervisor removes from old supervisor")
    void testReassignSupervisor() {
        Staff supervisor2 = new Staff("Frank", "Supervisor2", LocalDate.of(1975, 9, 5), 75000.0, false);

        supervisor.supervise(intern1);
        assertEquals(1, supervisor.getSupervises().size());

        intern1.setSupervisor(supervisor2);
        assertEquals(supervisor2, intern1.getSupervisor());
        assertEquals(0, supervisor.getSupervises().size(),
                "Original supervisor should not supervise intern anymore");
        assertEquals(1, supervisor2.getSupervises().size());
        assertTrue(supervisor2.getSupervises().contains(intern1));
    }

    @Test
    @DisplayName("stopSupervising removes intern from supervision list")
    void testStopSupervising() {
        supervisor.supervise(intern1);
        supervisor.supervise(intern2);

        assertEquals(2, supervisor.getSupervises().size());

        supervisor.stopSupervising(intern1);

        assertEquals(1, supervisor.getSupervises().size());
        assertFalse(supervisor.getSupervises().contains(intern1));
        assertNull(intern1.getSupervisor(),
                "Intern should have no supervisor after stopSupervising");
    }

    @Test
    @DisplayName("clearSupervisor removes intern from supervisor's list")
    void testClearSupervisor() {
        supervisor.supervise(intern1);

        assertEquals(1, supervisor.getSupervises().size());

        intern1.clearSupervisor();

        assertNull(intern1.getSupervisor());
        assertEquals(0, supervisor.getSupervises().size(),
                "Supervisor should not supervise intern anymore");
    }

    @Test
    @DisplayName("setSupervisor with null clears supervisor")
    void testSetSupervisorNull() {
        supervisor.supervise(intern1);

        intern1.setSupervisor(null);

        assertNull(intern1.getSupervisor());
        assertEquals(0, supervisor.getSupervises().size());
    }

    @Test
    @DisplayName("Changing non-intern to intern fails if currently supervising others")
    void testSetInternFailsIfSupervising() {
        supervisor.supervise(intern1);

        assertThrows(IllegalStateException.class,
                () -> supervisor.setIntern(true),
                "Cannot become intern if currently supervising others");
    }

    @Test
    @DisplayName("Changing intern to non-intern fails if currently supervised")
    void testSetNonInternFailsIfSupervised() {
        supervisor.supervise(intern1);

        assertThrows(IllegalStateException.class,
                () -> intern1.setIntern(false),
                "Cannot become non-intern if currently supervised");
    }

    @Test
    @DisplayName("Can change to non-intern after clearing supervisor")
    void testChangeToNonInternAfterClearingSupervisor() {
        supervisor.supervise(intern1);

        intern1.clearSupervisor();

        // Should not throw
        intern1.setIntern(false);
        assertFalse(intern1.isIntern());
    }

    @Test
    @DisplayName("Multiple supervisors can supervise different interns")
    void testMultipleSupervisorsMultipleInterns() {
        Staff supervisor2 = new Staff("Frank", "Supervisor2", LocalDate.of(1975, 9, 5), 75000.0, false);
        Staff intern3 = new Staff("Grace", "Intern3", LocalDate.of(2001, 11, 30), 25000.0, true);

        supervisor.supervise(intern1);
        supervisor.supervise(intern2);
        supervisor2.supervise(intern3);

        assertEquals(2, supervisor.getSupervises().size());
        assertEquals(1, supervisor2.getSupervises().size());

        assertEquals(supervisor, intern1.getSupervisor());
        assertEquals(supervisor, intern2.getSupervisor());
        assertEquals(supervisor2, intern3.getSupervisor());
    }

    @Test
    @DisplayName("Intern with no supervisor has null supervisor")
    void testInternWithNoSupervisor() {
        assertNull(intern1.getSupervisor());
        assertTrue(intern1.getSupervisor() == null);
    }

    @Test
    @DisplayName("Non-intern with no supervised interns has empty supervises list")
    void testNonInternWithNoSupervises() {
        assertEquals(0, supervisor.getSupervises().size());
        assertTrue(supervisor.getSupervises().isEmpty());
    }

    @Test
    @DisplayName("Setting same supervisor twice is idempotent")
    void testSetSupervisorIdempotent() {
        intern1.setSupervisor(supervisor);
        intern1.setSupervisor(supervisor);

        assertEquals(supervisor, intern1.getSupervisor());
        assertEquals(1, supervisor.getSupervises().size(),
                "Should only appear once even if setSupervisor called twice");
    }

    @Test
    @DisplayName("Supervising same intern twice is idempotent")
    void testSuperviseIdempotent() {
        supervisor.supervise(intern1);
        supervisor.supervise(intern1);

        assertEquals(1, supervisor.getSupervises().size());
        assertTrue(supervisor.getSupervises().contains(intern1));
    }

    @Test
    @DisplayName("Null handling in supervise and setSupervisor methods")
    void testNullHandling() {
        // Should not throw
        supervisor.supervise(null);
        intern1.setSupervisor(null);
        supervisor.stopSupervising(null);

        assertEquals(0, supervisor.getSupervises().size());
        assertNull(intern1.getSupervisor());
    }
}
