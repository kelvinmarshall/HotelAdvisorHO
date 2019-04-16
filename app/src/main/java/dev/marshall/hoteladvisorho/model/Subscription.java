package dev.marshall.hoteladvisorho.model;

/**
 * Created by Marshall on 17/03/2018.
 */

public class Subscription {
  private   String phone,name,plan,amount,paymentstate;

    public Subscription() {
    }

    public Subscription(String phone, String name, String plan, String amount, String paymentstate) {
        this.phone = phone;
        this.name = name;
        this.plan = plan;
        this.amount = amount;
        this.paymentstate = paymentstate;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentstate() {
        return paymentstate;
    }

    public void setPaymentstate(String paymentstate) {
        this.paymentstate = paymentstate;
    }
}
