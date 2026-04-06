<<<<<<< HEAD:src/main/java/com/mycompany/hotelmanagementsystem/util/RoomSelectionItem.java
package com.mycompany.hotelmanagementsystem.util;
=======
package com.mycompany.hotelmanagementsystem.utils;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b:src/main/java/com/mycompany/hotelmanagementsystem/utils/RoomSelectionItem.java

/**
 * Represents a customer's room type selection with quantity.
 * Used in the multi-room booking flow.
 */
public class RoomSelectionItem {
    private int typeId;
    private int quantity;

    public RoomSelectionItem() {}

    public RoomSelectionItem(int typeId, int quantity) {
        this.typeId = typeId;
        this.quantity = quantity;
    }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
