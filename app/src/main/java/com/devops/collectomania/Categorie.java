package com.devops.collectomania;

public class Categorie {

    public String catagory;
    public String imageuri;
    public int goal;

    public Categorie(String catagory, String imageuri, int goal) {
        this.catagory = catagory;
        this.imageuri = imageuri;
        this.goal =goal;
    }

    public Categorie(){

    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }


    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

}
