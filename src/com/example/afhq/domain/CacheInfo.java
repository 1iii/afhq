package com.example.afhq.domain;

import android.graphics.drawable.Drawable;
/**
 * ����ʵ��
 * @author �Ľ�
 *
 */
public class CacheInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    //Ӧ�ô�С
    private String codeSize;
    //���ݴ�С
    private String dataSize;
    //�����С
    private String cacheSize;

    public String getName() {
        return name;
    }

    public String getDataSize() {
        return dataSize;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public String getCodeSize() {
        return codeSize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public void setCodeSize(String codeSize) {
        this.codeSize = codeSize;
    }

    public String getPackageName() {
        return packageName;
    }
}
