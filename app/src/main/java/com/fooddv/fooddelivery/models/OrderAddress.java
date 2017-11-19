package com.fooddv.fooddelivery.models;

/**
 * Created by vr on 2017-11-19.
 */

public class OrderAddress {

    private boolean isprofile;
    private Adress opt_address;

    public OrderAddress(boolean isprofile, Adress opt_address) {
        this.isprofile = isprofile;
        this.opt_address = opt_address;
    }

    public boolean is_profile() {
        return isprofile;
    }

    public void setIs_profile(boolean isprofile) {
        this.isprofile = isprofile;
    }

    public Adress getOpt_address() {
        return opt_address;
    }

    public void setOpt_address(Adress opt_address) {
        this.opt_address = opt_address;
    }
}
