package enums;

/**
 *
 * @author hugol
 */
public enum ContainerType {

    PERISHABLE_FOOD,
    NON_PERISHABLE_FOOD,
    CLOTHING,
    MEDICINE;

    public String ContainerType_To_String(ContainerType type) {
        switch (type) {
            case PERISHABLE_FOOD:
                return "This is a Perishibla food container type";
            case NON_PERISHABLE_FOOD:
                return "This is a Non perishibla food container type";
            case CLOTHING:
                return "This is a clothing container type";
            case MEDICINE:
                return "This is a medicine container type";
            default:
                return "This is a Perishibla food";

        }

    }

}
