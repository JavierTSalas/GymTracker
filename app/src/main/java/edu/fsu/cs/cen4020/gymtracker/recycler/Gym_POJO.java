package edu.fsu.cs.cen4020.gymtracker.recycler;


import java.io.Serializable;
import java.util.Map;

public class Gym_POJO {
    public String city;
    public String icon_url;
    public String name;
    public String state;
    public String street_address;
    public String zipcode;

    public Gym_POJO(String city, String icon_url, String name, String state, String street_address, String zipcode, String document_id) {
        this.city = city;
        this.icon_url = icon_url;
        this.name = name;
        this.state = state;
        this.street_address = street_address;
        this.zipcode = zipcode;
        this.document_id = document_id;
    }

    public Gym_POJO(Map<String, Object> data, String documentId) {
        this.city = (String) data.get("city");
        this.icon_url = (String) data.get("icon_url");
        this.name = (String) data.get("name");
        this.state = (String) data.get("state");
        this.street_address = (String) data.get("street_address");
        this.zipcode = (String) data.get("zipcode");
        this.document_id = documentId;
        ;

    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String document_id;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return "Gym_POJO{" +
                "city='" + city + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", street_address='" + street_address + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", document_id='" + document_id + '\'' +
                '}';
    }
}
