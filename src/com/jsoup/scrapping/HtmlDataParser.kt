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

    fun getResources(): List<Resource> {
        val resource = mutableListOf<Resource>()

        val elements = doc.getElementsByTag(BODY)
        val data = mutableListOf<String>()

        if (elements.isNotEmpty()) {
            // todo: use string builder to add strings for inline level elements and add line break for block level elements

            elements.forEach { element ->
                if (element.childrenSize() > 0) {
                    element.allElements.forEach {
                        it.select(LINKS_CSS_QUERY).first()?.let { element1 ->
                            if (!data.contains(element1.toString())) data.add(element1.toString())
                        }
                        it.select(SRC_CSS_QUERY).first()?.let { element1 ->
                            if (!data.contains(element1.toString())) data.add(element1.toString())
                        }

                        if (it.text().isNotEmpty()) {
                            if (!data.contains(it.text())) data.add(it.text())
                        }

                        if (blockLevelElements.contains(it.tagName())) {
                            data.add("\n")
                        } else data.add(" ")

                    }
                } else {
                    element.select(LINKS_CSS_QUERY).first()?.let {
                        if (!data.contains(it.toString())) data.add(it.toString())
                    }
                    element.select(SRC_CSS_QUERY).first()?.let {
                        if (!data.contains(it.toString())) data.add(it.toString())
                    }
                    if (element.text().isNotEmpty()) {
                        if (!data.contains(element.text())) data.add(element.text())
                    }

                    if (blockLevelElements.contains(element.tagName())) {
                        data.add("\n")
                    } else data.add(" ")
                }
            }

            data.forEach {
                print(it)
            }
        }

        return resource
    }

    companion object {
        private const val MEDIA_FILES = "embedded_media"
        private const val LINKS_CSS_QUERY = "link[href]"
        private const val SRC_CSS_QUERY = "img[src]~a[href],a[href]~img[src],a[href],[src]"
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
    }
}