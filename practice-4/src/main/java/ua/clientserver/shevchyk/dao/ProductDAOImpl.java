package ua.clientserver.shevchyk.dao;

import ua.clientserver.shevchyk.filter.CriteriaFilter;
import ua.clientserver.shevchyk.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    private Connection connection;


    public ProductDAOImpl(Connection connection) {
        this.connection = connection;
    }


    public int addProduct(Product product) {
        String createQuery = "INSERT INTO PRODUCTS (PRODUCT_NAME, PRICE, AMOUNT, GROUP_NAME) values (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createQuery)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setInt(3, product.getAmount());
            preparedStatement.setString(4, product.getGroupName());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                product.setId(generatedKeys.getInt(1));
            }
            return product.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCount() {
        String countQuery = "SELECT COUNT(*) as count_products FROM PRODUCTS";
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(countQuery)) {
                return resultSet.getInt("count_products");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product getProduct(int id) {
        String readQuery = "SELECT * FROM PRODUCTS WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(readQuery)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException(String.format("No product with id %d was found in table", id));
                }
                return new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("PRODUCT_NAME"),
                        resultSet.getDouble("PRICE"),
                        resultSet.getInt("AMOUNT"),
                        resultSet.getString("GROUP_NAME")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateProduct(Product product) {
        String updateQuery = "UPDATE PRODUCTS SET PRODUCT_NAME = ?, PRICE = ?, AMOUNT = ?, GROUP_NAME = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setInt(3, product.getAmount());
            preparedStatement.setString(4, product.getGroupName());
            preparedStatement.setInt(5, product.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException(String.format("Updating product failed, no product with id %d exists in database.", product.getId()));
            }

            return product.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteProduct(int id) {
        String deleteQuery = "DELETE FROM PRODUCTS WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException(String.format("Deleting product failed, no product with id %d exists in database ", id));
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> listByCriteria(CriteriaFilter criteriaFilter) {
        List<Product> result = new ArrayList<>();
        String criteriaQuery = criteriaFilter.buildQuery();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(criteriaQuery)) {
            while (resultSet.next()) {
                result.add(formProduct(resultSet, List.of(criteriaFilter.getArrayListOfFields())));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product formProduct(ResultSet resultSet, List<String> listOfFields) throws SQLException {
        Product product = new Product();
        if(listOfFields.contains("ID")) {
            product.setId(resultSet.getInt("ID"));
        }
        if(listOfFields.contains("PRODUCT_NAME")) {
            product.setProductName(resultSet.getString("PRODUCT_NAME"));
        }
        if(listOfFields.contains("PRICE")) {
            product.setPrice(resultSet.getDouble("PRICE"));
        }
        if(listOfFields.contains("AMOUNT")) {
            product.setAmount(resultSet.getInt("AMOUNT"));
        }
        if(listOfFields.contains("GROUP_NAME")) {
            product.setGroupName(resultSet.getString("GROUP_NAME"));
        }
        return product;
    }

}
