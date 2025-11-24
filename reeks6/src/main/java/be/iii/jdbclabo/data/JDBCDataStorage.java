package be.iii.jdbclabo.data;

import be.iii.jdbclabo.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDBCDataStorage implements IDataStorage {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Value("${select_products}")
    private String selectProducts;

    @Value("${select_customers}")
    private String selectCustomers;

    public JDBCDataStorage(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    // MET JDBC CLIENT (JdbcTemplate), meeste wordt hier voor ons gedaan
    @Override
    public List<IProduct> getProducts() throws DataExceptie {
        try {
            return jdbcTemplate.query(selectProducts, (rs, rowNum) ->
                    new Product(
                            rs.getString("productCode"),
                            rs.getString("productName"),
                            rs.getString("productLine"),
                            rs.getString("productScale"),
                            rs.getString("productVendor"),
                            rs.getString("productDescription"),
                            rs.getInt("quantityInStock"),
                            rs.getDouble("buyPrice"),
                            rs.getDouble("MSRP")
                    )
            );
        } catch (Exception e) {
            throw new DataExceptie("Database error in getProducts: " + e.getMessage());
        }
    }


    // WITHOUT JDBC CLIENT, RAW JDBC INSTEAD
    @Override
    public List<ICustomer> getCustomers() throws DataExceptie {
        List<ICustomer> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectCustomers);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("customerNumber"),
                        rs.getString("customerName"),
                        rs.getString("contactLastName"),
                        rs.getString("contactFirstName"),
                        rs.getString("phone"),
                        rs.getString("addressLine1"),
                        rs.getString("addressLine2"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("postalcode"),
                        rs.getString("country"),
                        rs.getInt("salesRepEmployeeNumber"),
                        rs.getInt("creditLimit")
                );
                result.add(c);
            }
            return result;

        } catch (SQLException e) {
            throw new DataExceptie("Database error in getCustomers: " + e.getMessage());
        }
    }

    @Override
    public List<IOrderWithoutDetails> getOrders(int customerNumber) throws DataExceptie {
        return null;
    }

    @Override
    public int maxCustomerNumber() throws DataExceptie {
        return 0;
    }

    @Override
    public int maxOrderNumber() throws DataExceptie {
        return 0;
    }


    @Override
    public void addOrder(IOrder order) throws DataExceptie {

    }


    @Override
    public void addCustomer(ICustomer customer) throws DataExceptie {

    }


    @Override
    public void modifyCustomer(ICustomer customer) throws DataExceptie {
    }


    @Override
    public void deleteCustomer(int customerNumber) throws DataExceptie {
    }


    @Override
    public double getTotal(int customerNumber) throws DataExceptie {
        return 0;
    }
}
