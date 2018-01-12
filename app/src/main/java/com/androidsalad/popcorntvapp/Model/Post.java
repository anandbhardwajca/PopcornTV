package com.androidsalad.popcorntvapp.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Post {

    private String postId;
    private String celebId;
    private String celebName;
    private String celebThumbUrl;
    private String postDesc;
    private String postThumbUrl;
    private int postViews;
    private int postImages;

    public Post() {
    }

    public Post(String postId, String celebId, String celebName, String celebThumbUrl, String postDesc, String postThumbUrl, int postViews, int postImages) {
        this.postId = postId;
        this.celebId = celebId;
        this.celebName = celebName;
        this.celebThumbUrl = celebThumbUrl;
        this.postDesc = postDesc;
        this.postThumbUrl = postThumbUrl;
        this.postViews = postViews;
        this.postImages = postImages;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCelebId() {
        return celebId;
    }

    public void setCelebId(String celebId) {
        this.celebId = celebId;
    }

    public String getCelebName() {
        return celebName;
    }

    public void setCelebName(String celebName) {
        this.celebName = celebName;
    }

    public String getCelebThumbUrl() {
        return celebThumbUrl;
    }

    public void setCelebThumbUrl(String celebThumbUrl) {
        this.celebThumbUrl = celebThumbUrl;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostThumbUrl() {
        return postThumbUrl;
    }

    public void setPostThumbUrl(String postThumbUrl) {
        this.postThumbUrl = postThumbUrl;
    }

    public int getPostViews() {
        return postViews;
    }

    public void setPostViews(int postViews) {
        this.postViews = postViews;
    }

    public int getPostImages() {
        return postImages;
    }

    public void setPostImages(int postImages) {
        this.postImages = postImages;
    }

    @Exclude
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("celebId", celebId);
        result.put("celebName", celebName);
        result.put("celebThumbUrl", celebThumbUrl);
        result.put("postDesc", postDesc);
        result.put("postThumbUrl", postThumbUrl);
        result.put("postViews", postViews);
        result.put("postImages", postImages);

        return result;

    }


}
