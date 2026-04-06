package com.mycompany.hotelmanagementsystem.entity;

public class RoomImage {
    private int imageId;
    private int typeId;
    private String imageUrl;

    public RoomImage() {}

    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
