package com.androidsalad.popcorntvapp.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Photo {

    private String photoId;
    private String photoFullUrl;

    public Photo() {
    }

    public Photo(String photoId, String photoFullUrl) {
        this.photoId = photoId;
        this.photoFullUrl = photoFullUrl;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoFullUrl() {
        return photoFullUrl;
    }

    public void setPhotoFullUrl(String photoFullUrl) {
        this.photoFullUrl = photoFullUrl;
    }

    @Exclude
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("photoId", photoId);
        result.put("photoFullUrl", photoFullUrl);

        return result;
    }

}
