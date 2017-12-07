package com.fooddv.fooddelivery.models;

/**
 * Created by vr on 2017-11-19.
 */

public class Adress {

    private boolean isprofile;
    private String opt_address;

    public Adress(boolean isprofile, String opt_address) {
        this.isprofile = isprofile;
        this.opt_address = opt_address;
    }

    public boolean isprofile() {
        return isprofile;
    }

    public void setIsprofile(boolean isprofile) {
        this.isprofile = isprofile;
    }

    public String getOpt_address() {
        return opt_address;
    }

    public void setOpt_address(String opt_address) {
        this.opt_address = opt_address;
    }
}
