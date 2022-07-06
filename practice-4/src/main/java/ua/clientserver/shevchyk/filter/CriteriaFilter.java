package ua.clientserver.shevchyk.filter;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
public class CriteriaFilter {

    private String[] arrayListOfFields;
    private String[] arrayOfProductName;
    private Double lowerBoundOfPrice;
    private Double upperBoundOfPrice;
    private Integer lowerBoundAmount;
    private Integer upperBoundAmount;
    private String[] arrayGroupName;

    private String orderBy;
    private boolean isAsc;

    private Integer limit;
    private Integer offset;

    public String buildQuery() {
        StringBuilder criteriaQuery = new StringBuilder("SELECT ");
        if (isArrayNonEmpty(arrayListOfFields)) {
            criteriaQuery.append(String.join(", ", arrayListOfFields));
        } else {
            criteriaQuery.append("*");
            arrayListOfFields = new String[]{"ID", "PRODUCT_NAME", "PRICE", "AMOUNT", "GROUP_NAME"};
        }
        criteriaQuery.append(" FROM PRODUCTS");
        List<String> criteria = new ArrayList<>();
        if (isArrayNonEmpty(arrayOfProductName)) {
            for (int i = 0; i < arrayOfProductName.length; i++) {
                arrayOfProductName[i] = convertStringValue(arrayOfProductName[i]);
            }
            criteria.add("PRODUCT_NAME IN(" + String.join(", ", arrayOfProductName) + ")");
        }
        if (isWrapValueInit(lowerBoundOfPrice)) {
            criteria.add("PRICE >= " + lowerBoundOfPrice);
        }
        if (isWrapValueInit(upperBoundOfPrice)) {
            criteria.add("PRICE <= " + upperBoundOfPrice);
        }
        if (isWrapValueInit(lowerBoundAmount)) {
            criteria.add("AMOUNT >= " + lowerBoundAmount);
        }
        if (isWrapValueInit(upperBoundAmount)) {
            criteria.add("AMOUNT <= " + upperBoundAmount);
        }
        if (isArrayNonEmpty(arrayGroupName)) {
            for (int i = 0; i < arrayGroupName.length; i++) {
                arrayGroupName[i] = convertStringValue(arrayGroupName[i]);
            }
            criteria.add("GROUP_NAME IN(" + String.join(", ", arrayGroupName) + ")");
        }

        if (criteria.size() > 0) {
            criteriaQuery.append(" WHERE ");
            String criteriaString = String.join(" AND ", criteria);
            criteriaQuery.append(criteriaString);
        }

        if (isWrapValueInit(orderBy)) {
            criteriaQuery.append(" ORDER BY ").append(orderBy);
            if (!isAsc) {
                criteriaQuery.append(" DESC");
            }
        }

        if (isWrapValueInit(limit)) {
            criteriaQuery.append(" LIMIT ").append(limit);
        }

        if (isWrapValueInit(offset)) {
            criteriaQuery.append(" OFFSET ").append(offset);
        }

        return criteriaQuery.toString();
    }

    public boolean isWrapValueInit(Object value) {
        return Objects.nonNull(value);
    }

    private String getTemplateStringOfQuestions(int number) {
        StringBuilder templateString = new StringBuilder("?");
        for (int i = 1; i < number; i++) {
            templateString.append(",?");
        }
        return templateString.toString();
    }

    public String convertStringValue(String value) {
        return "'" + value + "'";
    }

    public boolean isArrayNonEmpty(Object[] array) {
        return Objects.nonNull(array) && array.length > 0;
    }
}
