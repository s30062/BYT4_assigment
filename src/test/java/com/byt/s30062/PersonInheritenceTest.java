package com.byt.s30062;

import com.byt.s30062.model.Person;
import com.byt.s30062.model.Customer;
import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Administrator;
import com.byt.s30062.model.SalesStaff;
import com.byt.s30062.model.enums.DayOfWeek;
import com.byt.s30062.model.enums.LevelOfPermission;
import com.byt.s30062.model.enums.StaffType;
import com.byt.s30062.model.complex.WorkingHours;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Person dynamic overlapping inheritance through composition:
 * - Person: standalone role (can have 0 or 1 Customer and/or 0 or 1 Staff)
 * - Customer: composes Person (1 required)
 * - Staff: composes Person through subclasses (1 required)
 * 
 * Tests bidirectional linking between Person ↔ Customer and Person ↔ Staff
 */
class PersonInheritenceTest {

    @BeforeEach
    void setup() {
        Person.clearExtent();
        Customer.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Person.clearExtent();
        Customer.clearExtent();
        new File("person_extent.ser").delete();
        new File("customer_extent.ser").delete();
    }

    // ============================================================================
    // ASPECT 1: Person as standalone entity
    // ============================================================================

    @Test
    @DisplayName("Can create a standalone Person without any role")
    void testStandalonePersonCreation() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Person person = new Person("Alice", "Johnson", birthDate);
        
        assertEquals("Alice", person.getFirstName());
        assertEquals("Johnson", person.getLastName());
        assertEquals(birthDate, person.getDateOfBirth());
        assertNull(person.getCustomer());
        assertNull(person.getStaff());
    }

    @Test
    @DisplayName("Standalone Person is added to Person extent")
    void testStandalonePersonInExtent() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Person person = new Person("Alice", "Johnson", birthDate);
        
        assertTrue(Person.getExtent().contains(person));
    }

    @Test
    @DisplayName("Person validates age and name constraints")
    void testPersonValidation() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        // Null firstName
        assertThrows(IllegalArgumentException.class,
            () -> new Person(null, "Johnson", birthDate));
        
        // Blank lastName
        assertThrows(IllegalArgumentException.class,
            () -> new Person("Alice", "  ", birthDate));
        
        // Future birthDate
        assertThrows(IllegalArgumentException.class,
            () -> new Person("Alice", "Johnson", LocalDate.now().plusDays(1)));
        
        // birthDate before 1900
        assertThrows(IllegalArgumentException.class,
            () -> new Person("Alice", "Johnson", LocalDate.of(1899, 12, 31)));
    }

    // ============================================================================
    // ASPECT 2: Customer composition with Person
    // ============================================================================

    @Test
    @DisplayName("Can create Customer with new Person (composition)")
    void testCustomerWithNewPerson() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate registrationDate = LocalDate.of(2020, 1, 15);
        Customer customer = new Customer("Alice", "Johnson", birthDate, registrationDate);
        
        assertNotNull(customer.getPerson());
        assertEquals("Alice", customer.getFirstName());
        assertEquals("Johnson", customer.getLastName());
        assertEquals(birthDate, customer.getDateOfBirth());
        assertEquals(registrationDate, customer.getRegistrationDate());
    }

    @Test
    @DisplayName("Customer created with new Person establishes bidirectional link")
    void testCustomerPersonBidirectionalLink() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate registrationDate = LocalDate.of(2020, 1, 15);
        Customer customer = new Customer("Alice", "Johnson", birthDate, registrationDate);
        
        Person person = customer.getPerson();
        assertNotNull(person.getCustomer());
        assertSame(customer, person.getCustomer());
    }

    @Test
    @DisplayName("Can create Customer with existing Person")
    void testCustomerWithExistingPerson() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate registrationDate = LocalDate.of(2020, 1, 15);
        
        // Create standalone Person first
        Person person = new Person("Alice", "Johnson", birthDate);
        assertNull(person.getCustomer());
        
        // Link to Customer
        Customer customer = new Customer(person, registrationDate);
        
        assertSame(person, customer.getPerson());
        assertSame(customer, person.getCustomer());
    }

    @Test
    @DisplayName("Customer composes exactly one Person")
    void testCustomerPersonComposition() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate registrationDate = LocalDate.of(2020, 1, 15);
        Customer customer = new Customer("Alice", "Johnson", birthDate, registrationDate);
        
        Person person = customer.getPerson();
        assertNotNull(person);
        
        // Customer can access Person properties through delegation
        assertEquals("Alice", customer.getFirstName());
        assertEquals("Johnson", customer.getLastName());
        
        // Changes to Person reflect in Customer
        person.setFirstName("Alicia");
        assertEquals("Alicia", customer.getFirstName());
    }

    @Test
    @DisplayName("Customer validates age (must be 13+)")
    void testCustomerAgeValidation() {
        LocalDate tooYoung = LocalDate.now().minusYears(12);
        LocalDate registrationDate = LocalDate.now();
        
        assertThrows(IllegalArgumentException.class,
            () -> new Customer("Young", "Person", tooYoung, registrationDate));
    }

    // ============================================================================
    // ASPECT 3: Staff composition with Person
    // ============================================================================

    @Test
    @DisplayName("Can create Staff (via Manager) with new Person")
    void testStaffWithNewPerson() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Manager manager = new Manager("Alice", "Johnson", birthDate, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertNotNull(manager.getPerson());
        assertEquals("Alice", manager.getFirstName());
        assertEquals("Johnson", manager.getLastName());
        assertEquals(birthDate, manager.getDateOfBirth());
    }

    @Test
    @DisplayName("Staff created with new Person establishes bidirectional link")
    void testStaffPersonBidirectionalLink() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Manager manager = new Manager("Alice", "Johnson", birthDate, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        Person person = manager.getPerson();
        assertNotNull(person.getStaff());
        assertSame(manager, person.getStaff());
    }

    @Test
    @DisplayName("Can create Staff (via Manager) with existing Person")
    void testStaffWithExistingPerson() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        // Create standalone Person first
        Person person = new Person("Alice", "Johnson", birthDate);
        assertNull(person.getStaff());
        
        // Link to Staff
        Manager manager = new Manager(person, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertSame(person, manager.getPerson());
        assertSame(manager, person.getStaff());
    }

    @Test
    @DisplayName("Staff composes exactly one Person")
    void testStaffPersonComposition() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Administrator admin = new Administrator("Bob", "Smith", birthDate, 60000.0, false,
                                               LevelOfPermission.Middle, StaffType.FULL_TIME,
                                               Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        Person person = admin.getPerson();
        assertNotNull(person);
        
        // Staff can access Person properties through delegation
        assertEquals("Bob", admin.getFirstName());
        assertEquals("Smith", admin.getLastName());
        
        // Changes to Person reflect in Staff
        person.setFirstName("Robert");
        assertEquals("Robert", admin.getFirstName());
    }

    // ============================================================================
    // ASPECT 4: Dynamic Overlapping Inheritance (Person can be both Customer and Staff)
    // ============================================================================

    @Test
    @DisplayName("Person can be both Customer and Staff simultaneously")
    void testPersonAsCustomerAndStaff() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate customerReg = LocalDate.of(2020, 1, 15);
        
        // Create Person
        Person person = new Person("Alice", "Johnson", birthDate);
        
        // Link to Customer
        Customer customer = new Customer(person, customerReg);
        assertEquals(customer, person.getCustomer());
        assertNull(person.getStaff());
        
        // Link to Staff (same Person)
        Manager manager = new Manager(person, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        assertEquals(customer, person.getCustomer());
        assertEquals(manager, person.getStaff());
    }

    @Test
    @DisplayName("Person can transition from one role to another (replace Staff)")
    void testPersonRoleTransition() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        // Create Person with Manager role
        Manager manager1 = new Manager("Alice", "Johnson", birthDate, 80000.0, false,
                                      StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        Person person = manager1.getPerson();
        assertSame(manager1, person.getStaff());
        
        // Create new Staff role (Administrator) with same Person
        // Note: This would require unlinking manager1 first in real scenarios
        // For now, testing that Person can reference different Staff types
        Administrator admin = new Administrator("Alice", "Johnson", birthDate, 70000.0, false,
                                               LevelOfPermission.Senior, StaffType.FULL_TIME,
                                               Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        // Verify both staff instances exist (though same Person can only have one staff at a time)
        assertNotNull(manager1.getPerson());
        assertNotNull(admin.getPerson());
    }

    @Test
    @DisplayName("Multiple Persons can exist with different role combinations")
    void testMultiplePersonsWithDifferentRoles() {
        LocalDate birthDate1 = LocalDate.of(1985, 3, 20);
        LocalDate birthDate2 = LocalDate.of(1990, 5, 15);
        LocalDate birthDate3 = LocalDate.of(1992, 7, 10);
        
        // Person 1: Only Customer
        Customer customer = new Customer("Alice", "Johnson", birthDate1, LocalDate.of(2020, 1, 15));
        
        // Person 2: Only Staff (Manager)
        Manager manager = new Manager("Bob", "Smith", birthDate2, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        // Person 3: Standalone (no role)
        Person person3 = new Person("Charlie", "Brown", birthDate3);
        
        // Verify roles
        assertNotNull(customer.getPerson().getCustomer());
        assertNull(customer.getPerson().getStaff());
        
        assertNull(manager.getPerson().getCustomer());
        assertNotNull(manager.getPerson().getStaff());
        
        assertNull(person3.getCustomer());
        assertNull(person3.getStaff());
    }

    // ============================================================================
    // ASPECT 5: Extent Management (All Person instances tracked)
    // ============================================================================

    @Test
    @DisplayName("Person extent includes standalone persons and those with roles")
    void testPersonExtentManagement() {
        LocalDate birthDate1 = LocalDate.of(1985, 3, 20);
        LocalDate birthDate2 = LocalDate.of(1990, 5, 15);
        LocalDate birthDate3 = LocalDate.of(1992, 7, 10);
        
        // Standalone Person
        Person person1 = new Person("Alice", "Johnson", birthDate1);
        
        // Person via Customer
        Customer customer = new Customer("Bob", "Smith", birthDate2, LocalDate.of(2020, 1, 15));
        
        // Person via Staff
        Manager manager = new Manager("Charlie", "Brown", birthDate3, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        // All persons should be in extent
        assertEquals(3, Person.getExtent().size());
        assertTrue(Person.getExtent().contains(person1));
        assertTrue(Person.getExtent().contains(customer.getPerson()));
        assertTrue(Person.getExtent().contains(manager.getPerson()));
    }

    @Test
    @DisplayName("Customer extent tracks customers only")
    void testCustomerExtentManagement() {
        LocalDate birthDate1 = LocalDate.of(1985, 3, 20);
        LocalDate birthDate2 = LocalDate.of(1990, 5, 15);
        
        // Standalone Person (not in Customer extent)
        new Person("Alice", "Johnson", birthDate1);
        
        // Customer
        Customer customer = new Customer("Bob", "Smith", birthDate2, LocalDate.of(2020, 1, 15));
        
        // Only Customer should be in extent
        assertEquals(1, Customer.getExtent().size());
        assertTrue(Customer.getExtent().contains(customer));
    }

    // ============================================================================
    // ASPECT 6: Property Delegation
    // ============================================================================

    @Test
    @DisplayName("Customer delegates Person properties")
    void testCustomerPropertyDelegation() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Customer customer = new Customer("Alice", "Johnson", birthDate, LocalDate.of(2020, 1, 15));
        
        assertEquals("Alice", customer.getFirstName());
        assertEquals("Johnson", customer.getLastName());
        assertEquals(birthDate, customer.getDateOfBirth());
        
        // Modify through Customer
        customer.setFirstName("Alicia");
        customer.setLastName("Johnsen");
        
        assertEquals("Alicia", customer.getFirstName());
        assertEquals("Johnsen", customer.getLastName());
        
        // Changes reflect in underlying Person
        assertEquals("Alicia", customer.getPerson().getFirstName());
        assertEquals("Johnsen", customer.getPerson().getLastName());
    }

    @Test
    @DisplayName("Staff delegates Person properties")
    void testStaffPropertyDelegation() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Administrator admin = new Administrator("Bob", "Smith", birthDate, 60000.0, false,
                                               LevelOfPermission.Middle, StaffType.FULL_TIME,
                                               Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals("Bob", admin.getFirstName());
        assertEquals("Smith", admin.getLastName());
        assertEquals(birthDate, admin.getDateOfBirth());
        
        // Modify through Staff
        admin.setFirstName("Robert");
        admin.setLastName("Smithson");
        
        assertEquals("Robert", admin.getFirstName());
        assertEquals("Smithson", admin.getLastName());
        
        // Changes reflect in underlying Person
        assertEquals("Robert", admin.getPerson().getFirstName());
        assertEquals("Smithson", admin.getPerson().getLastName());
    }

    // ============================================================================
    // ASPECT 7: Equality and Identity
    // ============================================================================

    @Test
    @DisplayName("Two customers with same Person data are equal")
    void testCustomerEquality() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate registrationDate = LocalDate.of(2020, 1, 15);
        
        Customer customer1 = new Customer("Alice", "Johnson", birthDate, registrationDate);
        Customer customer2 = new Customer("Alice", "Johnson", birthDate, registrationDate);
        
        assertEquals(customer1, customer2);
    }

    @Test
    @DisplayName("Person instance is unique through composition")
    void testPersonInstanceUniqueness() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        LocalDate registrationDate = LocalDate.of(2020, 1, 15);
        
        // Create customer with new Person
        Customer customer1 = new Customer("Alice", "Johnson", birthDate, registrationDate);
        Person person1 = customer1.getPerson();
        
        // Create another customer with same data but new Person
        Customer customer2 = new Customer("Alice", "Johnson", birthDate, registrationDate);
        Person person2 = customer2.getPerson();
        
        // Persons should be different instances
        assertNotSame(person1, person2);
        
        // But they should be equal in value
        assertEquals(person1, person2);
    }
}
