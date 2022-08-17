package com.devops.collectomania;

public class Item {
    String itemUri;
    String name,category;
    Double value;
    int quantity;
    String doPurchase;


    public Item(){

    }


    public Item(String itemUri, String name, String category, Double value, int quantity, String doPurchase) {
        this.itemUri = itemUri;
        this.name = name;
        this.category = category;
        this.value = value;
        this.quantity = quantity;
        this.doPurchase = doPurchase;
    }

    public String getItemUri() {
        return itemUri;
    }

    public void setItemUri(String itemUri) {
        this.itemUri = itemUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getDoPurchase() {
        return doPurchase;
    }

    public void setDoPurchase(String doPurchase) {
        this.doPurchase = doPurchase;
    }

}
