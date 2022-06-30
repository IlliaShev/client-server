package ua.shevchyk.clientserver.db;

public interface DBService {

    int getCountOfProduct(String productName);

    int disposeProduct(String productName, int quantity);

    int addProduct(String productName, int quantity);

    void addGroup(String groupName);

    void addProductToGroup(String group, String productName);

    void setPrice(String productName, int newPrice);
}
