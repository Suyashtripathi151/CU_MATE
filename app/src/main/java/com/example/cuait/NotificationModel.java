package com.example.cuait;

public class NotificationModel {


    String Name;
    String BodyPreview;
    String Subject;

    String Time;

    NotificationModel(){

    }

    public NotificationModel(String Name, String BodyPreview, String Subject,String Time) {
        this.Name = Name;
        this.BodyPreview = BodyPreview;
        this.Subject = Subject;
        this.Time=Time;

    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getBodyPreview() {
        return BodyPreview;
    }

    public void setBodyPreview(String BodyPreview) {
        this.BodyPreview = BodyPreview;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String Subject) {
        this.Subject = Subject;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Subject) {
        this.Time = Time;
    }











}
