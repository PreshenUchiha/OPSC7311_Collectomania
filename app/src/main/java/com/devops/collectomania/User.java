package com.devops.collectomania;

public class User {

    public String name;
    public String lastName;
    public String phonenumber;
    public String profilePic;

    public User(String name, String lastName, String phonenumber, String profilePic) {
        this.name = name;
        this.lastName = lastName;
        this.phonenumber = phonenumber;
        this.profilePic = profilePic;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


}
