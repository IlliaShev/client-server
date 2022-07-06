package ua.clientserver.shevchyk.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Product {

    private int id;
    private String productName;
    private double price;
    private int amount;
    private String groupName;

    public Product(String productName, double price, int amount, String groupName) {
        this.productName = productName;
        this.price = price;
        this.amount = amount;
        this.groupName = groupName;
    }

    public Product(int id, String productName, double price, int amount, String groupName) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.amount = amount;
        this.groupName = groupName;
    }
}
