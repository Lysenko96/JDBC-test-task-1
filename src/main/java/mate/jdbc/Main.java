package mate.jdbc;

import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.provider.Provider;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Objects;

import static mate.jdbc.provider.Provider.SCHEMA_FILENAME;

public class Main {

    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Connection connection = new Provider().getConnection();
        ScriptRunner runner = new ScriptRunner(connection);
        try {
            Reader reader = new BufferedReader(new FileReader(Objects.requireNonNull(Main.class.getClassLoader().getResource(SCHEMA_FILENAME)).getFile()));
            runner.runScript(reader);
        } catch (FileNotFoundException e) {
            throw new DataProcessingException("runScript failed: ", e);
        }

        ManufacturerDao manufacturerDao = (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("manufacturer");
        manufacturer.setCountry("country");
        // initialize field values using setters or constructor
        manufacturerDao.create(manufacturer);
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("manufacturer1");
        manufacturer1.setCountry("country1");
        manufacturerDao.create(manufacturer1);
        // test other methods from ManufacturerDao

        System.out.println(manufacturerDao.get(manufacturer.getId()).orElse(null));

        System.out.println(manufacturerDao.getAll());

        manufacturer1.setName("manufacturerOne");
        manufacturer1.setCountry("countryOne");

        System.out.println(manufacturerDao.update(manufacturer1));

        System.out.println(manufacturerDao.getAll());

        System.out.println(manufacturerDao.delete(manufacturer.getId()));

        System.out.println(manufacturerDao.getAll());
    }
}
