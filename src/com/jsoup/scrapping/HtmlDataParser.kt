/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.StringBuilder

/**
 *
 * @author Sabbir
 */

private const val BODY = "body"

class HtmlDataParser(private val doc: Document) {
    private val data = mutableListOf<Resource>()

    fun getResources(): List<Resource> {
        val elements = doc.getElementsByTag(BODY)
        val mElementText = elements.text().replace("\\s".toRegex(), " ")

        val mElementTextList = mutableListOf<Resource>()

        elements.forEach { element ->
            if (element.childrenSize() > 0) {
                element.allElements.forEach {
                    mElementTextList.add(it.getResource())
                }
            } else {
                mElementTextList.add(element.getResource())
            }
        }

        val mTextList = mutableListOf<String>()
        mElementTextList.distinctBy { it.resourceText?.trim() }.forEachIndexed { index, resource ->
            if (resource.tagName == "body" || resource.tagName == "div") return@forEachIndexed

            if (!mTextList.contains(resource.resourceText?.trim())) {
                mTextList.add(resource.resourceText?.trim().toString())
                println(resource.resourceText?.trim())
            }
        }

        return data
    }

    private fun Element.getResource(): Resource {
        val href: String? = select(SRC_CSS_QUERY).attr("href")
        val src: String? = select(SRC_CSS_QUERY).attr("src")
        val text: String? = select(SRC_CSS_QUERY).text()
        val height = select(SRC_CSS_QUERY).attr("height")
        val width = select(SRC_CSS_QUERY).attr("width")

        return Resource(
                tagName(),
                StringBuilder(wholeText()),
                text,
                src,
                href,
                height,
                width
        )
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