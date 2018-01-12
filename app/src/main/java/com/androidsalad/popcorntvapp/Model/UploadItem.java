package com.androidsalad.popcorntvapp.Model;

public class UploadItem {

    private String imageUrl;
    private String itemName;
    private Boolean uploadStatus;

    public UploadItem() {
    }

    public UploadItem(String imageUrl, String itemName, Boolean uploadStatus) {
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.uploadStatus = uploadStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Boolean getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(Boolean uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
