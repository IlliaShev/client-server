package ua.clientserver.shevchyk.dao;

import ua.clientserver.shevchyk.filter.CriteriaFilter;
import ua.clientserver.shevchyk.model.Product;

import java.util.List;

public interface ProductDAO {

    int getCount();

    int addProduct(Product product);

    Product getProduct(int id);

    int updateProduct(Product product);

    int deleteProduct(int id);

    List<Product> listByCriteria(CriteriaFilter criteriaFilter);
}
