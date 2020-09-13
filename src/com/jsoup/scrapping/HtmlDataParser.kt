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

        if (elements.isNotEmpty()) {
            elements.forEach { element ->
                if (element.childrenSize() > 0) {
                    element.allElements.forEach { element1 ->
                        getDataFromElement(element1)
                    }
                } else {
                    getDataFromElement(element)
                }
            }
        }

        return data
    }

    private fun getDataFromElement(element: Element) {
        if (element.tagName() != "body" && element.tagName() != "div") {
            val resource = Resource()
            resource.tagName = element.tagName()

            element.select(LINKS_CSS_QUERY).first()?.let {
                resource.mediaHref = it.text()
            }
            element.select(SRC_CSS_QUERY).first()?.let {
                resource.mediaSrc = it.text()
            }
            if (element.text().isNotEmpty()) {
                resource.resourceText = element.text()
            }

            data.add(resource)
        }
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