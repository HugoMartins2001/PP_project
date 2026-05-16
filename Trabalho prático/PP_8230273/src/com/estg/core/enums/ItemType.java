package com.estg.core.enums;

/**
 *
 * @author hugol
 */
public enum ItemType {

    CLOTHING,
    MEDICINE,
    NON_PERISHABLE_FOOD,
    PERISHABLE_FOOD;

    public String ItemType_To_String(ItemType it) {
        switch (it) {
            case CLOTHING:
                return "Clothing";
            case MEDICINE:
                return "Medicine";
            case NON_PERISHABLE_FOOD:
                return "Non perishable food";
            case PERISHABLE_FOOD:
                return "Perishable food";
            default:
                return "clothing";
        }

    }

}
