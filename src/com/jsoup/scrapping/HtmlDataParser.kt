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

        val mElementOwnText = mutableListOf<String>()
        val builder = StringBuilder()

        elements.forEach {
            it.allElements.forEach { element ->
                element.ownText()?.let { str ->
                    builder.append(str)
                    if (blockLevelElements.contains(element.tagName())) {
                        mElementOwnText.add(builder.toString())
                        builder.clear()
                    }
                }
            }
        }

        mElementOwnText.forEach {
            val matchIndex = mElementText.trim().indexOf(it.trim())
            if(it.trim().isNotEmpty()){
                println("$it Index: $matchIndex Contains:${mElementText.contains(it, true)}")
            }
            if (matchIndex > 0)
                mElementText.insert(matchIndex, "\n")
        }

//        println(mElementText)

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