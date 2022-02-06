package com.example.kobzeva;

public class Post {

    private String description;
    private String urlImage;

    public Post(String url, String desc) {
        urlImage = url;
        description = desc;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getDescription() {
        return description;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
