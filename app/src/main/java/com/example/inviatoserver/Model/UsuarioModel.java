package com.example.inviatoserver.Model;

public class UsuarioModel {

    private String Name;
    private String Phone;
    private String Password;
    private String SliderStatus;
    private String IsStaff;

    public UsuarioModel() {
    }//Constructor_1

    public UsuarioModel(String sliderStatus) {
        SliderStatus = sliderStatus;
    }//Constructor_2

    public UsuarioModel(String name, String phone, String password, String sliderStatus) {
        Name = name;
        Phone = phone;
        Password = password;
        SliderStatus = sliderStatus;
    }//Constructor_3

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }//getName

    public void setName(String name) {
        Name = name;
    }//setName

    public String getPassword() {
        return Password;
    }//getPassword

    public void setPassword(String password) {
        Password = password;
    }//setPassword

    public String getSliderStatus() {
        return SliderStatus;
    }//getSliderStatus

    public void setSliderStatus(String sliderStatus) {
        SliderStatus = sliderStatus;
    }//setSliderStatus

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}//UsuarioModel
