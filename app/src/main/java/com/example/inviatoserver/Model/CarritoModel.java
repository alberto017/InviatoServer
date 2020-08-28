package com.example.inviatoserver.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class CarritoModel implements Parcelable {

    private String productID;
    private String productName;
    private String quantity;
    private String price;
    private String discount;

    public CarritoModel() {

    }

    public CarritoModel(String productID, String productName, String quantity, String price, String discount) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    protected CarritoModel(Parcel in) {
        productID = in.readString();
        productName = in.readString();
        quantity = in.readString();
        price = in.readString();
        discount = in.readString();
    }

    public static final Parcelable.Creator<CarritoModel> CREATOR = new Parcelable.Creator<CarritoModel>() {
        @Override
        public CarritoModel createFromParcel(Parcel in) {
            return new CarritoModel(in);
        }

        @Override
        public CarritoModel[] newArray(int size) {
            return new CarritoModel[size];
        }
    };

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }


    //Metodos propios de la implementacion Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(productID);
        parcel.writeString(productName);
        parcel.writeString(quantity);
        parcel.writeString(price);
        parcel.writeString(discount);
    }//writeToParcel
}
