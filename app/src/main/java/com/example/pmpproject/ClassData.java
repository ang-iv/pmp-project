package com.example.pmpproject;

public class ClassData {
    private String id;
    private String name;
    private String description;
    private String favId;
    private boolean favorite;

    public ClassData() {
    }

    public ClassData(String id, String name, String description, String favId, boolean favorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.favId = favId;
        this.favorite = favorite;
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "ClassData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", favId='" + favId + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}

