package com.intel.dleyna.dleynademo;

import android.graphics.Bitmap;

public class ContainedObjectLite {

    private String mObjectPath;
    private String mDisplayName;
    private final String mUrl;
    private String mIconUrl;
    private String mMimeType;
    private boolean mIsContainer;
    private Bitmap mBitmap;

    ContainedObjectLite(String objectPath, String displayName, String url, String iconUrl, String mimeType, boolean isContainer) {
        mObjectPath = objectPath;
        mDisplayName = displayName;
        mUrl = url;
        mIconUrl = iconUrl;
        mMimeType = mimeType;
        mIsContainer = isContainer;
    }

    public String getObjectPath() {
        return mObjectPath;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public boolean isContainer() {
        return mIsContainer;
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
                .append("url=").append(getUrl()).append(",")
                .append("iconUrl=").append(getIconUrl()).append(",")
                .append("mimeType").append(getMimeType()).append(",")
                .append("isContainer").append(isContainer());
        return sb.toString();
    }
}
