package by.shop.model;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class EmployeeTest {

    SessionFactory factory;
    StandardServiceRegistry registry;


    @Before
    public void setUp() throws Exception {


        registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.test.cfg.xml") // configures settings from hibernate.cfg.xml
                .build();

        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory(); // класс генерит сессию

    }

    @Test
    public void create() {

        Employee employee = new Employee();
        employee.setBirthDate(new Date());
        employee.setFirstName("First Employee");
        employee.setLastName("Last Name Employee");


        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setCity("Minsk");
        employeeDetails.setCountry("Belarus");
        employeeDetails.setMobileNumber("65653563");
        employeeDetails.setStreet("Platonova");
        employeeDetails.setEmployee(employee);


        employee.setEmployeeDetails(employeeDetails);


        //when
        String employeeId = save(employee);
        String employeeDetailsId = employee.getEmployeeDetails().getId();

        Employee savedEmployee = get(employeeId);

        //then

        assertNotNull(employeeId);
        assertNotNull(employeeDetailsId);
        assertNotNull(savedEmployee.getEmployeeDetails());

        assertEquals(employeeDetailsId, savedEmployee.getEmployeeDetails().getId());


    }

    private String save(Employee employee) {

        Session sess = factory.openSession();
        Transaction tx = null;
        String employeeId;

        try {
            tx = sess.beginTransaction();
            employeeId = (String) sess.save(employee);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            sess.close();

        }

        return employeeId;
    }

    public Employee get(String employeeId) { // get by ID
        Session appSession = factory.openSession(); // сессия связи с БД

        Employee employee;
        Transaction tx = null;
        try {
            tx = appSession.beginTransaction();
            employee = appSession.get(Employee.class, employeeId);

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            throw e;

        } finally {
            appSession.close();
        }


        return employee;

    }


    @After
    public void tearDown() throws Exception {
    }
}
