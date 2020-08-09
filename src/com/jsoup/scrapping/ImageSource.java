/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping;

/**
 *
 * @author Sabbir
 */
public class ImageSource {
    private String imageName;
    private String imageSrc;
    private String imageHref;
    private int height;
    private int width;

    public ImageSource(String imageName, String imageSrc, String imageHref, int height, int width) {
        this.imageName = imageName;
        this.imageSrc = imageSrc;
        this.imageHref = imageHref;
        this.height = height;
        this.width = width;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getImageHref() {
        return imageHref;
    }

    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    
    
}
