import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.clientserver.shevchyk.Constants;
import ua.clientserver.shevchyk.dao.ProductDAO;
import ua.clientserver.shevchyk.dao.ProductDAOImpl;
import ua.clientserver.shevchyk.db_source.DatasourceFactory;
import ua.clientserver.shevchyk.filter.CriteriaFilter;
import ua.clientserver.shevchyk.model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlTests {

    private static Connection connection;
    private static ProductDAO productDAO;
    private static List<Product> products;

    @BeforeAll()
    public static void initDB() throws SQLException, ClassNotFoundException {
        connection = DatasourceFactory.getConnection(Constants.DB_FILE_NAME);
        DatasourceFactory.dropTable();
        DatasourceFactory.createProductTable();
        productDAO = new ProductDAOImpl(connection);
        products = new ArrayList<>();
        Product computer = new Product("Computer", 653, 15, "Electronics");
        Product phone = new Product("Phone", 321, 9, "Electronics");
        Product headPhones = new Product("Headphones", 127, 142, "Electronics");
        Product bed = new Product("Bed", 478, 17, "Home&Garden");
        Product bicycle = new Product("Bicycle", 333, 77, "Sport");
        Product tShirt = new Product("T-shirt", 35, 1422, "Sport");
        computer.setId(productDAO.addProduct(computer));
        phone.setId(productDAO.addProduct(phone));
        headPhones.setId(productDAO.addProduct(headPhones));
        bed.setId(productDAO.addProduct(bed));
        bicycle.setId(productDAO.addProduct(bicycle));
        tShirt.setId(productDAO.addProduct(tShirt));
        products.add(computer);
        products.add(phone);
        products.add(bed);
        products.add(headPhones);
        products.add(bicycle);
        products.add(tShirt);
    }

    @Test
    public void countTest() {
        int numberOfProductsInDb = productDAO.getCount();
        Assertions.assertEquals(numberOfProductsInDb, products.size());
    }

    @Test
    public void getTest() {
        for (Product product : products) {
            Product expectedPhone = productDAO.getProduct(product.getId());
            Assertions.assertEquals(expectedPhone, product);
        }
    }

    @Test
    public void updateTest() {
        int updatedPrice = 150;
        Product productToUpdate = products.get(3);
        productToUpdate.setPrice(updatedPrice);
        productDAO.updateProduct(productToUpdate);

        Product productFromDb = productDAO.getProduct(productToUpdate.getId());
        Assertions.assertEquals(updatedPrice, productFromDb.getPrice());

    }

    @Test
    public void deleteTest() {
        int indexToDelete = 4;

        productDAO.deleteProduct(indexToDelete);
        products.remove(indexToDelete - 1);
        int count = productDAO.getCount();
        Assertions.assertEquals(count, products.size());
    }

    @Test
    public void filterTest() {
        String groupNameToFilter = "Electronics";
        double upperBoundOfPrice = 520.0;
        CriteriaFilter criteriaFilter = CriteriaFilter.builder()
                .arrayGroupName(new String[]{groupNameToFilter})
                .upperBoundOfPrice(upperBoundOfPrice)
                .build();

        List<Product> filteredProducts = productDAO.listByCriteria(criteriaFilter);

        Assertions.assertTrue(filteredProducts.stream().allMatch(product -> product.getGroupName().equals(groupNameToFilter) &&
                product.getPrice() <= upperBoundOfPrice));

    }

    @Test
    public void filterTest2() {
        String productNameToFilter = "Bicycle";
        double lowerBoundOfPrice = 520.0;
        CriteriaFilter criteriaFilter = CriteriaFilter.builder()
                .arrayOfProductName(new String[]{productNameToFilter})
                .lowerBoundOfPrice(lowerBoundOfPrice)
                .build();

        List<Product> filteredProducts = productDAO.listByCriteria(criteriaFilter);

        Assertions.assertEquals(0, filteredProducts.size());

    }

}
