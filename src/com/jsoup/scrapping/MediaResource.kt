/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

/**
 *
 * @author Sabbir
 */
data class MediaResource(
        var imageName: String? = null,
        var imageSrc: String? = null,
        var imageHref: String? = null,
        var height: Int = 0,
        var width: Int = 0
)