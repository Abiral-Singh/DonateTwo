package com.abiralsingh.donatetwo;

public class Donate_Item {

    String postTitle;
    String tag;
    String postDesc;

    public Donate_Item() {
        //empty constructor needed
    }

    public Donate_Item(String postTitle, String tag, String postDesc) {
        this.postTitle = postTitle;
        this.tag = tag;
        this.postDesc = postDesc;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getTag() {
        return tag;
    }

    public String getPostDesc() {
        return postDesc;
    }
}
