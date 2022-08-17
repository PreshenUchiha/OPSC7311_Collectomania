package com.devops.collectomania;

import java.util.ArrayList;

public class TempUser {
    public static String Email = null,Fullname = null, SelectedCollection = null, SelectedItem = null, profilePic;
    public static String encodedEmail(){
        return Email.replace(".",",").toLowerCase();
    }

    public static ArrayList<String>editDeets = new ArrayList<>();

    public static ArrayList<String>userDeets = new ArrayList<>();

}
