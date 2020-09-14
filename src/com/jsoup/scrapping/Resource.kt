/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

import java.lang.StringBuilder

/**
 *
 * @author Sabbir
 */
data class Resource(
        var tagName: String?,
        var resourceText: StringBuilder?,
        var mediaName: String?,
        var mediaSrc: String?,
        var mediaHref: String?,
        var mediaheight: Any?,
        var mediawidth: Any?
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