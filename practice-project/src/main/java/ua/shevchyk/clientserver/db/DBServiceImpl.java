package ua.shevchyk.clientserver.db;

import ua.shevchyk.clientserver.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBServiceImpl implements DBService {
    private Map<String, List<Product>> groupToProduct;
    private Map<String, Product> productNameToProduct;

    public DBServiceImpl() {
        this.groupToProduct = new ConcurrentHashMap<>();
        this.productNameToProduct = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized int getCountOfProduct(String productName) {
        if (!productNameToProduct.containsKey(productName)) {
            throw new RuntimeException("Product does not exist");
        }
        return productNameToProduct.get(productName).getQuantity();
    }

    @Override
    public synchronized int disposeProduct(String productName, int quantity) {
        if (!productNameToProduct.containsKey(productName)) {
            throw new RuntimeException("Product does not exist");
        }
        int oldQuantity = productNameToProduct.get(productName).getQuantity();
        if (oldQuantity < quantity) {
            throw new RuntimeException("Not enough quantity");
        }
        int newQuantity = oldQuantity - quantity;
        productNameToProduct.get(productName).setQuantity(newQuantity);
        return productNameToProduct.get(productName).getQuantity();
    }

    @Override
    public synchronized int addProduct(String productName, int quantity) {
        if (!productNameToProduct.containsKey(productName)) {
            throw new RuntimeException("Product does not exist");
        }
        int oldQuantity = productNameToProduct.get(productName).getQuantity();
        int newQuantity = oldQuantity + quantity;
        productNameToProduct.get(productName).setQuantity(newQuantity);
        System.out.println(newQuantity);
        return productNameToProduct.get(productName).getQuantity();
    }

    @Override
    public synchronized void addGroup(String groupName) {
        if (groupToProduct.containsKey(groupName)) {
            throw new RuntimeException("Group already exist");
        } else {
            groupToProduct.put(groupName, new ArrayList<>());
        }
    }

    @Override
    public synchronized void addProductToGroup(String group, String productName) {
        if (productNameToProduct.containsKey(productName)) {
            throw new RuntimeException("Product already exist");
        } else {
            Product product = Product.builder()
                    .groupName(group)
                    .productName(productName)
                    .build();
            groupToProduct.get(group).add(product);
            productNameToProduct.put(product.getProductName(), product);
        }
    }

    @Override
    public void setPrice(String productName, int newPrice) {
        if (!productNameToProduct.containsKey(productName)) {
            throw new RuntimeException("Product does not exist");
        }
        productNameToProduct.get(productName).setPrice(newPrice);
    }

    public Product getProduct(String productName) {
        return productNameToProduct.get(productName);
    }
}
