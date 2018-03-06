package com.fooddv.fooddelivery.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Offer implements Serializable, Parcelable {

    int id;
    int offer_id;
    String name;
    String description;
    double price;
    String image;

    protected Offer(Parcel in) {
        id = in.readInt();
        offer_id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        image = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Offer(int id, int offer_id, String name, String description, double price, String image, int quantity) {
        this.id = id;
        this.offer_id = offer_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    int quantity;

    public Offer(int id, String name, String desc, double price, int quantity) {
        this.offer_id = id;
        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.quantity = quantity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer)) return false;

        Offer offer = (Offer) o;

        if (id != offer.id) return false;
        return name.equals(offer.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(offer_id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeDouble(price);
        parcel.writeString(image);
        parcel.writeInt(quantity);
    }
}
