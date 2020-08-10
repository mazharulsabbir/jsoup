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
public class Resource {
    private String tag;
    private String text;
    private MediaResource media;

    public Resource(String tag, String text, MediaResource media) {
        this.tag = tag;
        this.text = text;
        this.media = media;
    }

    public String getTag() {
        return tag;
    }

    public String getText() {
        return text;
    }

    public MediaResource getMedia() {
        return media;
    }
    
    
    
}
