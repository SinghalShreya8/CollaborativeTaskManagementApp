package com.example.taskmanagement.ui.main;

public class WonderModel {

//    String cardName;
    int imageResourceId;
//    int isfav;
//    int isturned;
    String title, date, assigned_by;

    public void setTitle(String title){
        this.title=title;
    }
    public  void setDate(String date){
        this.date=date;
    }
    public void setAssigned_by(String assigned_by){
        this.assigned_by=assigned_by;
    }

    public String getTitle(){
        return  title;
    }

    public String getAssigned_by() {
        return assigned_by;
    }

    public String getDate() {
        return date;
    }

//    public int getIsturned() {
//        return isturned;
//    }
//
//    public void setIsturned(int isturned) {
//        this.isturned = isturned;
//    }
//
//    public int getIsfav() {
//        return isfav;
//    }
//
//    public void setIsfav(int isfav) {
//        this.isfav = isfav;
//    }
//
//    public String getCardName() {
//        return cardName;
//    }
//
//    public void setCardName(String cardName) {
//        this.cardName = cardName;
//    }
//
    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}

