package com.example.cuait;

public class foundmodel {

    String User;
    String UserID;

    String date;
    String description;

    String item;
    String owner;
    String phone;
    String place;
    String Status;
    String ImageURL;
    String lookingForOwner;

    public String getLookingForOwner() {
        return lookingForOwner;
    }

    public void setLookingForOwner(String lookingForOwner) {
        this.lookingForOwner = lookingForOwner;
    }




    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }







    foundmodel(){

    }

    public foundmodel(String date, String User, String description, String UserID, String item,String owner,String place,String Status,String ImageURL,String lookingForOwner) {
        this.date = date;
        this.User = User;
        this.description = description;
        this.item=item;
        this.UserID=UserID;
        this.place=place;
        this.owner=owner;
        this.Status=Status;
        this.ImageURL=ImageURL;
        this.lookingForOwner=lookingForOwner;


    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserid(){
        return UserID;
    }

    public void setUserid(String UserID) {
        this.UserID = UserID;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String User) {
        this.User = User;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getItem() {
        return item;
    }

    public void setitem(String item) {
        this.item = item;
    }

    public String getImageURL(){
       return ImageURL;
    }

    public void setImageURL(String ImageURL){
        this.ImageURL=ImageURL;
    }



}
