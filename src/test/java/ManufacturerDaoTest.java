import mate.jdbc.Main;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.jdbc.JdbcManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.provider.Provider;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ManufacturerDaoTest extends Provider {

    private Manufacturer manufacturer;
    private ManufacturerDao manufacturerDao;
    private Connection connection;

    @BeforeEach
    public void setUp() {
        Provider provider = new Provider();
        connection = provider.getConnection();
        manufacturer = new Manufacturer();
        ScriptRunner runner = new ScriptRunner(connection);
        try {
            Reader reader = new BufferedReader(new FileReader(Objects.requireNonNull(Main.class.getClassLoader().getResource(SCHEMA_FILENAME)).getFile()));
            runner.runScript(reader);
        } catch (FileNotFoundException e) {
            throw new DataProcessingException("runScript failed: ", e);
        }
        manufacturerDao = new JdbcManufacturerDao(provider);
    }

    @Test
    public void addTest() {
        assertEquals(manufacturerDao.getAll().size(), 0);
        addManufacturer();
        assertEquals(manufacturerDao.getAll().size(), 1);
    }

    @Test
    public void getTest() {
        addManufacturer();
        Manufacturer result = manufacturerDao.get(manufacturer.getId()).orElse(null);
        assertEquals(manufacturer, result);
    }

    @Test
    public void getAllTest() {
        addManufacturer();
        List<Manufacturer> expected = Arrays.asList(manufacturer);
        List<Manufacturer> actual = manufacturerDao.getAll();
        assertEquals(expected, actual);

    }

    @Test
    public void updateTest() {
        addManufacturer();
        assertEquals(manufacturerDao.getAll().size(), 1);
        manufacturer.setName("manufacturerOne");
        Manufacturer actual = manufacturerDao.update(manufacturer);
        assertEquals(manufacturer, actual);
        assertEquals(manufacturerDao.getAll().size(), 1);
    }

    @Test
    public void deleteTest() {
        addManufacturer();
        assertEquals(manufacturerDao.getAll().size(), 1);
        assertTrue(manufacturerDao.delete(manufacturer.getId()));
        assertEquals(manufacturerDao.getAll().size(), 0);
    }

    public void addManufacturer(){
        manufacturer.setName("manufacturer");
        manufacturer.setCountry("country");
        assertEquals(manufacturer, manufacturerDao.create(manufacturer));
    }

}
