package com.example.DistributedImageParser.model;

public class Machine {

    private String adress;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public Machine setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAdress() {
        return adress;
    }

    public Machine setAdress(String adress) {
        this.adress = adress;
        return this;
    }
}
