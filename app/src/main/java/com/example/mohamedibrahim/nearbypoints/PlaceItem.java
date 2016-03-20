package com.example.mohamedibrahim.nearbypoints;

import android.graphics.Bitmap;

public class PlaceItem {
    private Bitmap icon;
    private String iconUrl;
    private String name;
    private String address;
    boolean selected = false;


    public PlaceItem(Bitmap icon, String name, String address, boolean selected, String iconUrl) {
        this.icon = icon;
        this.iconUrl = iconUrl;
        this.name = name;
        this.address = address;
        this.selected = selected;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String IconURL) {
        this.iconUrl = IconURL;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}