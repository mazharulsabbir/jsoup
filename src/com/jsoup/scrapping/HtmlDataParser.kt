/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.lang.StringBuilder
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 *
 * @author Sabbir
 */

private const val BODY = "body"

class HtmlDataParser(private val doc: Document) {
    private val data = mutableListOf<Resource>()

    fun getResources(): List<Resource> {
        val elements = doc.getElementsByTag(BODY)
        val mElementText = StringBuilder()
        mElementText.append(elements.text())

        elements.forEach {
            it.allElements.forEach { element ->
                if (element.tagName() == "br") {
                    println(element.ownText())
                }
//                println(element.ownText()) // current tag text only. eg. <p>Hello<span> World</span></p> | output: Hello

                val matchIndex = mElementText.indexOf(element.text())
                if (blockLevelElements.contains(element.tagName()))
                    mElementText.insert(matchIndex, "\n")
            }
        }

//        println(mElementText.trim())

        return data
    }

    companion object {
        private const val MEDIA_FILES = "embedded_media"
        private const val LINKS_CSS_QUERY = "link[href]"
        private const val SRC_CSS_QUERY = "img[src]~a[href],a[href]~img[src],a[href],[src]"
        val blockLevelElements = arrayOf(
                "hr",
                "table",
                "video",
                "ul",
                "tfoot",
                "section",
                "li",
                "main",
                "nav",
                "noscript",
                "ol",
                "p",
                "pre",
                "address",
                "article",
                "aside",
                "blockquote",
                "canvas",
                "dd",
                "div",
                "dl",
                "dt",
                "fieldset",
                "figcaption",
                "figure",
                "footer",
                "form",
                "h1",
                "h2",
                "h3",
                "h4",
                "h5",
                "h6",
                "header",
                "br"
        )
    }
}