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
data class Resource(
        var tagName: String?,
        var resourceText: String?,
        var mediaName: String?,
        var mediaSrc: String?,
        var mediaHref: String?,
        var mediaheight: Int? = 0,
        var mediawidth: Int? = 0
) {
    constructor() : this(
            null,
            null,
            null,
            null,
            null,
            null,
            null
    )
}