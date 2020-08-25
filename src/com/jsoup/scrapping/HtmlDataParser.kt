/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*
import java.util.function.Consumer

/**
 *
 * @author Sabbir
 */
const val MEDIA = "media"

class HtmlDataParser(private val doc: Document) {
    private var resource: MutableList<Resource>? = null

    fun getResource(): List<Resource>? {
        val imports = doc.select("link[href]")
        val urls = doc.select("img[src]~a[href],a[href]~img[src],a[href],[src]")
        parseHtmlToData(getMediaUrls(urls), getImportUrls(imports))

        return resource
    }

    private fun parseHtmlToData(links: ArrayList<MediaResource>, imports: ArrayList<String>) {
        val tempDataList: MutableList<Resource> = ArrayList()
        val mTag = mutableListOf<String>()
        val e = doc.allElements
        val tagText = HashMap<String, List<String>>()
        val tagIndex: MutableMap<String?, Int> = HashMap()

        e.stream().map { element: Element -> element.tag().toString() }.map { tag: String ->
            mTag.add(tag)
            tag
        }.map { tag: String ->
            tagIndex.putIfAbsent(tag, 0)
            tag
        }.filter { tag: String ->
            (!tagText.containsKey(tag)
                    && tag != "a"
                    && tag != "img"
                    && tag != "iframe"
                    && tag != "link"
                    && tag != "body"
                    && tag != "head"
                    && tag != "div"
                    && tag != "ul"
                    && tag != "ol"
                    && tag != "#root"
                    && tag != "html")
        }.forEachOrdered { tag: String ->
            // not an image or link or iframe
            val data = doc.getElementsByTag(tag)
            val array: MutableList<String> = ArrayList()
            data.forEach(Consumer { text: Element -> array.add(text.text()) })
            tagText.putIfAbsent(tag, array)
        }

        val textDocs = HashMap<String?, String>()
        var index = 0
        var importIndex = 0
        val result: MutableList<String> = ArrayList()

        // ================== data parsing ===================
        for (tag in mTag) {
            if (tagText.containsKey(tag)) {
                var tagIndx = tagIndex[tag]!!
                if (tagText[tag]!!.isNotEmpty() && tagText[tag]!!.size > tagIndx) {
                    if (tagText[tag]!![tagIndx].isNotEmpty()) {
                        if (!result.contains(tagText[tag]!![tagIndx])) {
                            result.add(tagText[tag]!![tagIndx])
                            tempDataList.add(
                                    Resource(
                                            tag,
                                            tagText[tag]!![tagIndx],
                                            null
                                    )
                            )
                        }
                    }

                    // update contents
                    textDocs[tag] = tagText[tag]!![tagIndx]
                    tagIndx++
                    tagIndex.replace(tag, tagIndx) // update tag index
                }
            } else if ("img" == tag || "iframe" == tag || "a" == tag) {
                if (index < links.size) {
                    val data = links[index]
                    val strBuilder = StringBuilder()
                    if (data.imageSrc!!.isNotEmpty()) {
                        strBuilder.append(data.imageSrc)
                        strBuilder.append(" * ")
                    }
                    if (data.imageHref!!.isNotEmpty()) {
                        strBuilder.append(data.imageHref)
                        strBuilder.append(" * ")
                    }
                    strBuilder.append(data.height).append("x").append(data.width)
                    if (data.imageName!!.isNotEmpty()) {
                        strBuilder.append(" * ")
                        strBuilder.append("(").append(data.imageName).append(")")
                    }

                    if (!result.contains(strBuilder.toString())) {
                        result.add(strBuilder.toString())
                        tempDataList.add(
                                Resource(
                                        tag,
                                        MEDIA,
                                        data
                                )
                        )
                        index++
                    }
                }

            } else if ("link" == tag) {
                if (importIndex < imports.size) {
//                    System.out.println("imports * " + imports.get(importIndex));
                    if (!result.contains(imports[importIndex])) {
                        result.add(imports[importIndex])
                        tempDataList.add(
                                Resource(
                                        tag,
                                        imports[importIndex],
                                        null
                                )
                        )
                        importIndex++
                    }
                }
            }
        } // ends of tag loop

        resource = mutableListOf()

        var i = 0
        val increment = 1
        while (i < tempDataList.size) {
            val temp = i + increment
            val current = tempDataList[i] // current index data

            if (temp < tempDataList.size) {
                val (tag, nextText, media) = tempDataList[temp] // next data

                val currentText = current.text

                if (nextText != null && currentText != null) {
                    if (currentText.contains(nextText)) { // string matched
                        val matchIndexStarts = currentText.indexOf(nextText)
                        if (MEDIA == current.text) {
                            resource!!.add(current)
                        } else {
                            val sub = matchIndexStarts.let { currentText.substring(0, it) }
                            val r = Resource(current.tag, sub, current.media)
                            resource!!.add(r)
                        }
                    } else { // not matched
                        if (nextText == MEDIA && "a" == tag) {
                            val r = Resource(current.tag, current.text, media)
                            resource!!.add(r)
                            i++
                        } else {
                            resource!!.add(current)
                        }
                    }
                } else { // text is null
                    resource!!.add(current)
                }
            } else { // last index
                resource!!.add(current)
            }
            i++
        }
    }

    private fun getImportUrls(imports: Elements): ArrayList<String> {
        val mImports = ArrayList<String>()

//        System.out.println("Imports: " + imports.size());
        imports.stream().map { link: Element ->
            mImports.add(link.attr("href").replace("\\\"", ""))
            link
        }.forEachOrdered { link: Element -> mImports.add(link.attr("rel").replace("\\\"", "")) }
        return mImports
    }

    private fun getMediaUrls(urls: Elements): ArrayList<MediaResource> {
        val hrefSets: MutableSet<String> = HashSet()
        val srcSets: MutableSet<String?> = HashSet()
        val links = ArrayList<MediaResource>()
        urls.forEach(Consumer { link: Element ->
            var src: String?
            var height: Int?
            var width: Int?

            val name = link.text().replace("\\\"", "")
            var mElementTag = "img"
            src = selectIframeOrSrc(mElementTag, link)["src"]
            height = convertToInt(selectIframeOrSrc(mElementTag, link)["height"])
            width = convertToInt(selectIframeOrSrc(mElementTag, link)["width"])
            if (src!!.isEmpty()) {
                mElementTag = "iframe"
                src = selectIframeOrSrc(mElementTag, link)["src"]
                height = convertToInt(selectIframeOrSrc(mElementTag, link)["height"])
                width = convertToInt(selectIframeOrSrc(mElementTag, link)["width"])
            }
            val aHref = if (link.select("a") != null) {
                link.select("a").attr("href").replace("\\\"", "")
            } else {
                link.attr("href").replace("\\\"", "")
            }

            if (aHref.isEmpty()) {
                if (src!!.isNotEmpty()) {
                    if (!srcSets.contains(src)) {
                        srcSets.add(src)
                        links.add(MediaResource(name, src, aHref, height, width))
                    }
                }
            } else {
                if (src!!.isNotEmpty()) {
                    if (!srcSets.contains(src) && !hrefSets.contains(aHref)) {
                        srcSets.add(src)
                        hrefSets.add(aHref)
                        links.add(MediaResource(name, src, aHref, height, width))
                    }
                } else {
                    if (!hrefSets.contains(aHref)) {
                        srcSets.add(src)
                        links.add(MediaResource(name, src, aHref, height, width))
                    }
                }
            }
        })
        return links
    }

    private fun selectIframeOrSrc(tag: String, link: Element): HashMap<String, String> {
        val map = HashMap<String, String>()
        if (link.select(tag) != null) {
            map["src"] = link.select(tag).attr("src").replace("\\\"", "")
            map["height"] = link.select(tag).attr("height").replace("\\\"", "")
            map["width"] = link.select(tag).attr("width").replace("\\\"", "")
        } else {
            map["src"] = link.attr("src").replace("\\\"", "")
            map["height"] = link.attr("height").replace("\\\"", "")
            map["width"] = link.attr("width").replace("\\\"", "")
        }
        return map
    }

    private fun convertToInt(str: String?): Int {
        var res = 0
        if (str!!.isNotEmpty()) {
            try {
                res = str.toInt()
            } catch (e: NumberFormatException) {
                println("Can't convert '" + str + "' to int. Error: " + e.localizedMessage)
            }
        }
        return res
    }

}