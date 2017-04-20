package com.haven.hotels.hotelshaven.other;

import java.io.Serializable;

/**
 * Created by AfolabSa on 26/03/2017.
 */

public class HhData implements Serializable {

    private String checkinText;
    private String checkoutText;
    private String adultText;
    private String childrenText;
    private String roomText;
    private String destinationText;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public HhData()
    {

    }

    public String getDestinationText() {
        return destinationText;
    }

    public String getCheckinText() {
        return checkinText;
    }

    public void setCheckinText(String checkinText) {
        this.checkinText = checkinText;
    }

    public String getCheckoutText() {
        return checkoutText;
    }

    public void setCheckoutText(String checkoutText) {
        this.checkoutText = checkoutText;
    }

    public String getAdultText() {
        return adultText;
    }

    public void setAdultText(String adultText) {
        this.adultText = adultText;
    }

    public String getChildrenText() {
        return childrenText;
    }

    public void setChildrenText(String childrenText) {
        this.childrenText = childrenText;
    }

    public String getRoomText() {
        return roomText;
    }

    public void setRoomText(String roomText) {
        this.roomText = roomText;
    }

    public void setDestinationText(String destinationText)
    {
        this.destinationText = destinationText;
    }
}
