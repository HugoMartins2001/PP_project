package enums;

/**
 *
 * @author hugol
 */
public enum ItemType {

    CLOTHING,
    MEDICINE;

    public String ItemType_To_String(ItemType type) {
        switch (type) {
            case CLOTHING:
                return "Your item is clothes";
            case MEDICINE:
                return "Your item is medicine";
            default:
                return "Your item is clothes";
        }

    }

}
