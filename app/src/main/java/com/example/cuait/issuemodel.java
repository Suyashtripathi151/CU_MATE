package com.example.cuait;

public class issuemodel {

    String query;
    String username;
    String usertype;
    String userID;
    Integer upvotes;


    issuemodel(){

    }

    public issuemodel(String query, String username, String usertype,String userID,Integer upvotes) {
        this.query = query;
        this.username = username;
        this.usertype = usertype;
        this.upvotes=upvotes;
        this.userID=userID;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserID(){
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }


    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }



}
