package ua.clientserver.shevchyk;

import ua.clientserver.shevchyk.dao.ProductDAOImpl;
import ua.clientserver.shevchyk.db_source.DatasourceFactory;
import ua.clientserver.shevchyk.filter.CriteriaFilter;
import ua.clientserver.shevchyk.model.Product;

import java.sql.Connection;
import java.sql.SQLException;

import static ua.clientserver.shevchyk.Constants.DB_FILE_NAME;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Product product1 = new Product("Computer", 15.5, 3, "Технікa");
        Product product2 = new Product("Potato", 5.5, 100, "Овочі");
        Connection connection = DatasourceFactory.getConnection(DB_FILE_NAME);
        ProductDAOImpl productDAO = new ProductDAOImpl(connection);
        CriteriaFilter criteriaFilter = CriteriaFilter.builder()
                .arrayListOfFields(new String[]{"PRODUCT_NAME", "PRICE"})
                .orderBy("PRICE")
                .isAsc(false)
                .limit(6)
                .offset(3)
                .build();
        productDAO.listByCriteria(criteriaFilter)
                .forEach(System.out::println);
    }
}