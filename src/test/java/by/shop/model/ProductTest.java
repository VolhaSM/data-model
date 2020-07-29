package by.shop.model;


import by.shop.datasourse.MySqlDataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.internal.SessionImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;


public class ProductTest {

    SessionFactory factory;
    private IDatabaseConnection connection;


    @Before
    public void setUp() throws Exception {

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.test.cfg.xml") // configures settings from hibernate.cfg.xml
                .build();
        try {
            factory = new MetadataSources(registry).buildMetadata().buildSessionFactory(); // класс генерит сессию
        } catch (Exception e) {
            e.printStackTrace();
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    public void create() {

        //Given

        Product product = new Product();
        //product.id = 1;
        product.name = "Lenovo Notebook";
        product.productNumber = "132445";
        product.serialNumber = "dhe87550";
        product.producedDate = new Date();

        //When

        Session sess = factory.openSession();
        Transaction tx = null;
        String productId;

        try {
            tx = sess.beginTransaction();

            //do some work
            productId = (String) sess.save(product);
            product.serialNumber = "88888-554";
            sess.saveOrUpdate(product);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            sess.close();

        }

        //Then

        assertTrue(productId.length() > 1);
    }


    @Test
    public void read() throws DatabaseUnitException, SQLException {
        //Given:
        IDatabaseConnection connection = new MySqlConnection(
                MySqlDataSource.getTestConnection(),
                "shop_test");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(ProductTest.class
                .getResourceAsStream("ProductTest.xml"));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

        Session appSession = factory.openSession();

        //When
        Product product = null;
        try {
            product = appSession.get(Product.class, "4028e7b773912199017391219e860000");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appSession.close();
        }

        //Then
        assertNotNull(product);
        assertEquals("Lenovo Notebook", product.name);
        DatabaseOperation.DELETE.execute(connection, dataSet);
        connection.close();

    }

    @Test
    public void update() throws DatabaseUnitException, SQLException {

        //Given:
        IDatabaseConnection connection = new MySqlConnection(
                MySqlDataSource.getTestConnection(),
                "shop_test"); // подключение к базе
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(ProductTest.class
                .getResourceAsStream("ProductTest.xml")); // инсертим то что лежит в xml
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet); // инсертить контент из икс эмэль дб юнит

        Session appSession = factory.openSession(); // сессия связи с БД

        Product product = null;
        Transaction tx = null;
        try {
            tx = appSession.beginTransaction();
            product = appSession.get(Product.class, "4028e7b773912199017391219e860001"); // getById

            product.setProductNumber("65767677");
            product.setSerialNumber("5555-8888");


            appSession.flush(); // сделать persistent
            tx.commit();
        }

            catch (Exception e){
                e.printStackTrace();
                tx.rollback();

            }
        finally {
                appSession.close();
            }


        //Then
        connection.close();

        }



    @Test
    public void delete () throws SQLException, DatabaseUnitException {

        //Given:
        IDatabaseConnection connection = new MySqlConnection(
                MySqlDataSource.getTestConnection(),
                "shop_test");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(ProductTest.class
                .getResourceAsStream("ProductTest.xml"));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

        Session appSession = factory.openSession();

        Product product1 = null;
        Product product2 = null;
        Transaction tx = null;
        try {
            tx = appSession.beginTransaction();
            product1 = appSession.get(Product.class, "4028e7b773912199017391219e860000");
            appSession.delete(product1);

            //tx = appSession.beginTransaction();
            product2 = appSession.get(Product.class, "4028e7b773912199017391219e860001");
            appSession.delete(product2);

            tx.commit();
        }
        catch (Exception e){
            e.printStackTrace();
            tx.rollback();

        }
        finally {
            appSession.close();
        }

    }

    @After
    public void tearDown() throws Exception {

        if (!factory.isClosed()) {
            factory.close();
            factory = null;
        }
    }
}