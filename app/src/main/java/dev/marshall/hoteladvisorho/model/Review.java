package dev.marshall.hoteladvisorho.model;

/**
 * Created by Marshall on 24/03/2018.
 */

public class Review {
    private String phone;
    private String username;
    private String review;
    private  String image;
    private  String hotelId;

    public Review() {
    }

    public Review(String phone, String username, String review, String image, String hotelId) {
        this.phone = phone;
        this.username = username;
        this.review = review;
        this.image = image;
        this.hotelId = hotelId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
