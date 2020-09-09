/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

/**
 *
 * @author Sabbir
 */
const val MEDIA_FILES = "embedded_media"
const val LINKS_CSS_QUERY = "link[href]"
const val SRC_CSS_QUERY = "img[src]~a[href],a[href]~img[src],a[href],[src]"

/* block level elements: <address><article><aside><blockquote><canvas><dd><div><dl><dt><fieldset><figcaption><figure><footer><form><h1>-<h6><header><hr><li><main><nav><noscript><ol><p><pre><section><table><tfoot><ul><video>*/
/* inline level elements: <a><abbr><acronym><b><bdo><big><br><button><cite><code><dfn><em><i><img><input><kbd><label><map><object><output><q><samp><script><select><small><span><strong><sub><sup><textarea><time><tt><var>*/

class HtmlDataParser(private val doc: Document) {
    private val blockLevelElements = arrayOf(
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

    private val imports = doc.select(LINKS_CSS_QUERY)
    private val urls = doc.select(SRC_CSS_QUERY)

    fun getResources(): List<Resource> {
        val resource = mutableListOf<Resource>()

        val elements = doc.getElementsByTag("body")
        if (elements.isNotEmpty()) {
            // todo: use string builder to add strings for inline level elements and add line break for block level elements
            val stringBuilder = StringBuilder()

            elements.forEach { element ->
                if (element.childrenSize() > 0) {
                    element.allElements.forEach {
                        if (blockLevelElements.contains(it.tagName())) {
                            stringBuilder.append("\n")
                        }

                        it.select(LINKS_CSS_QUERY).first()?.let { element1 ->
                            stringBuilder.append(element1)
                        }
                        it.select(SRC_CSS_QUERY).first()?.let { element1 ->
                            stringBuilder.append(element1)
                        }

                        if (it.text().isNotEmpty()) {
                            stringBuilder.append(it.text())
                        }

                        if (blockLevelElements.contains(it.tagName())) {
                            stringBuilder.append("\n")
                        }
                    }
                } else {
                    if (blockLevelElements.contains(element.tagName())) {
                        stringBuilder.append("\n")
                    }

                    element.select(LINKS_CSS_QUERY).first()?.let {
                        stringBuilder.append(it)
                    }
                    element.select(SRC_CSS_QUERY).first()?.let {
                        stringBuilder.append(it)
                    }
                    if (element.text().isNotEmpty()) {
                        stringBuilder.append(element.text())
                    }

                    if (blockLevelElements.contains(element.tagName())) {
                        stringBuilder.append("\n")
                    }
                }

                println(stringBuilder.toString())
            }
        }

        return resource
    }
}