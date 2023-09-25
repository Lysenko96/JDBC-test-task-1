package mate.jdbc.dao.jdbc;

import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.provider.Provider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class JdbcManufacturerDao implements ManufacturerDao {

    private static final String ADD_MANUFACTURER = "INSERT INTO manufacturers (name, country, is_deleted) VALUES (?,?,?)";
    private static final String GET_MANUFACTURER_BY_ID = "SELECT * FROM manufacturers WHERE id = ?";
    private static final String GET_ALL_MANUFACTURERS = "SELECT * FROM manufacturers";
    private static final String UPDATE_MANUFACTURER = "UPDATE manufacturers SET name = ?, country = ?, is_deleted = ? WHERE id = ?";
    private static final String DELETE_MANUFACTURER_BY_ID = "DELETE FROM manufacturers WHERE id = ?";
    private Provider provider;

    public JdbcManufacturerDao() {
        this.provider = new Provider();
    }

    public JdbcManufacturerDao(Provider provider) {
        this.provider = provider;
    }

    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD_MANUFACTURER, Statement.RETURN_GENERATED_KEYS)) {
            setToStatement(manufacturer, ps);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                manufacturer.setId(rs.getObject("id", Long.class));
            }
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("created failed: ", e);
        }
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_MANUFACTURER_BY_ID)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Manufacturer manufacturer = null;
            if (rs.next()) {
                manufacturer = parseFromResultSet(new Manufacturer(), rs);
            }
            return Optional.of(manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("get failed: ", e);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_MANUFACTURERS)) {
            ResultSet rs = ps.executeQuery();
            List<Manufacturer> manufacturers = new ArrayList<>();
            Manufacturer manufacturer = null;
            while (rs.next()) {
                manufacturer = parseFromResultSet(new Manufacturer(), rs);
                manufacturers.add(manufacturer);
            }
            return manufacturers;
        } catch (SQLException e) {
            throw new DataProcessingException("getAll failed: ", e);
        }
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_MANUFACTURER)) {
            setToStatement(manufacturer, ps);
            ps.setLong(4, manufacturer.getId());
            ps.executeUpdate();
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("update failed: ", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MANUFACTURER_BY_ID)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("delete failed: ", e);
        }
    }

    private Manufacturer parseFromResultSet(Manufacturer manufacturer, ResultSet rs) throws SQLException {
        manufacturer.setId(rs.getObject("id", Long.class));
        manufacturer.setName(rs.getString("name"));
        manufacturer.setCountry(rs.getString("country"));
        return manufacturer;
    }

    private void setToStatement(Manufacturer manufacturer, PreparedStatement ps) throws SQLException {
        ps.setString(1, manufacturer.getName());
        ps.setString(2, manufacturer.getCountry());
        ps.setBoolean(3, false);
    }
}