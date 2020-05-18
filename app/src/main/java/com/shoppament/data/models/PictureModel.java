package com.shoppament.data.models;

public class PictureModel extends BaseModel{
    private String id;
    private String name;
    private String path;

    public PictureModel() {

    }
    public PictureModel(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
