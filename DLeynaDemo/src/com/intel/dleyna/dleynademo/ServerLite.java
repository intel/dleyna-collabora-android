package com.intel.dleyna.dleynademo;

import android.graphics.Bitmap;

public class ServerLite {

    private String mObjectPath;
    private String mDisplayName;
    private String mIconUrl;
    private Bitmap mBitmap;

    ServerLite(String objectPath, String displayName, String iconUrl) {
        mObjectPath = objectPath;
        mDisplayName = displayName;
        mIconUrl = iconUrl;
    }

    public String getObjectPath() {
        return mObjectPath;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public void setBitmap(Bitmap bmp) {
        mBitmap = bmp;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("path=").append(getObjectPath()).append(",")
                .append("name=").append(getDisplayName()).append(",")
                .append("iconUrl=").append(getIconUrl());
        return sb.toString();
    }
}
