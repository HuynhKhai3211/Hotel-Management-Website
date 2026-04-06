<<<<<<< HEAD:src/main/java/com/mycompany/hotelmanagementsystem/entity/RoomSuggestionItem.java
package com.mycompany.hotelmanagementsystem.entity;
=======
package com.mycompany.hotelmanagementsystem.model;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b:src/main/java/com/mycompany/hotelmanagementsystem/model/RoomSuggestionItem.java

public class RoomSuggestionItem {
    private int bookingRoomId;
    private int suggestedRoomId;
    private String roomTypeName;
    private String suggestedRoomNumber;

    public RoomSuggestionItem() {}

    public RoomSuggestionItem(int bookingRoomId, int suggestedRoomId, String roomTypeName, String suggestedRoomNumber) {
        this.bookingRoomId = bookingRoomId;
        this.suggestedRoomId = suggestedRoomId;
        this.roomTypeName = roomTypeName;
        this.suggestedRoomNumber = suggestedRoomNumber;
    }

    public int getBookingRoomId() { return bookingRoomId; }
    public void setBookingRoomId(int bookingRoomId) { this.bookingRoomId = bookingRoomId; }

    public int getSuggestedRoomId() { return suggestedRoomId; }
    public void setSuggestedRoomId(int suggestedRoomId) { this.suggestedRoomId = suggestedRoomId; }

    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }

    public String getSuggestedRoomNumber() { return suggestedRoomNumber; }
    public void setSuggestedRoomNumber(String suggestedRoomNumber) { this.suggestedRoomNumber = suggestedRoomNumber; }
}
