package com.example.inviatoserver.Model;

public class PlatilloModel {
    private String Name;
    private String Image;
    private String Description;
    private String Price;
    private String MenuID;
    private String Discount;
    private String Status;

    public PlatilloModel() {

    }

    public PlatilloModel(String name, String image, String description, String price, String menuID, String discount, String status) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        MenuID = menuID;
        Discount = discount;
        Status = status;
    }

    public PlatilloModel(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getMenuID() {
        return MenuID;
    }

    public void setMenuID(String menuID) {
        MenuID = menuID;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
