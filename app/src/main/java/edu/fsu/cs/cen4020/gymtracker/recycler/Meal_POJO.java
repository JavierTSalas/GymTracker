package edu.fsu.cs.cen4020.gymtracker.recycler;


import java.util.Map;

public class Meal_POJO {

    public String title;
    public String description;
    public String document_id;
    public String content;

    @Override
    public String toString() {
        return "Meal_POJO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", document_id='" + document_id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Meal_POJO(Map<String, Object> data, String documentId) {
        this.title = (String) data.get("title");
        this.description = ((String) data.get("description")).replace("<br />","\n");
        this.content = ((String) data.get("content")).replace("<br />","\n");
        this.document_id = documentId;


    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
