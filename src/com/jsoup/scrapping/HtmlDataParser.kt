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
const val MEDIA = "media"

class HtmlDataParser(private val doc: Document) {

    private val imports = doc.select("link[href]")
    private val urls = doc.select("img[src]~a[href],a[href]~img[src],a[href],[src]")

    fun getResource(): List<Resource>? {
        val tempResource = getResourceByDocument(doc) // can contain duplicate entries
        val resource = mutableListOf<Resource>()

        var i = 0
        val increment = 1

        // check & remove duplicates
        while (i < tempResource.size) {
            val temp = i + increment
            val current = tempResource[i] // current index data

            if (temp < tempResource.size) {
                val (tag, nextText, media) = tempResource[temp] // next data

                val currentText = current.text

                if (nextText != null && currentText != null) {
                    if (currentText.contains(nextText)) { // string matched
                        val matchIndexStarts = currentText.indexOf(nextText)
                        if (MEDIA == current.text) {
                            resource.add(current)
                        } else {
                            val sub = matchIndexStarts.let { currentText.substring(0, it) }
                            val r = Resource(current.tag, sub, current.media)
                            resource.add(r)
                        }
                    } else { // not matched
                        if (nextText == MEDIA && "a" == tag) {
                            val r = Resource(current.tag, current.text, media)
                            resource.add(r)
                            i++
                        } else {
                            resource.add(current)
                        }
                    }
                } else { // text is null
                    resource.add(current)
                }
            } else { // last index
                if (i > 1) {
                    val prev = tempResource[i - 1]
                    if (prev.text != null && current.text != null) {
                        if (!prev.text.contains(current.text)) {
                            resource.add(current)
                        }
                    } else {
                        resource.add(current)
                    }
                } else {
                    resource.add(current)
                }
            }
            i++
        }

        return resource
    }

    private fun getTagList(doc: Document): List<String> {
        val element = doc.allElements
        val tags = mutableListOf<String>()

        element.map {
            tags.add(it.tagName())
        }

        return tags
    }

    private fun getResourceByDocument(doc: Document): List<Resource> {
        val tagText = hashMapOf<String, List<String?>>()
        val tagIndexMap = hashMapOf<String, Int>()
        val tags = getTagList(doc)

        tags.map { tag ->
            if (!tagIndexMap.containsKey(tag))
                tagIndexMap[tag] = 0

            // ignore those tags
            if (!tagText.containsKey(tag)
                    && tag != "a"
                    && tag != "img"
                    && tag != "iframe"
                    && tag != "link"
                    && tag != "body"
                    && tag != "head"
                    && tag != "div"
                    && tag != "#root"
                    && tag != "html"
            ) {
                val data = doc.getElementsByTag(tag)
                val array: MutableList<String> = ArrayList()
                data.forEach(Consumer { text: Element -> array.add(text.text()) })
                tagText.putIfAbsent(tag, array)
            }
        }

        return getTempDataList(tags, tagText, tagIndexMap)
    }

    private fun getTempDataList(
            tags: List<String>,
            tagTextMap: HashMap<String, List<String?>>,
            tagIndexMap: HashMap<String, Int>
    ): List<Resource> {
        val tempDataList = mutableListOf<Resource>()
        val imports = getImportUrls()
        val links = getMediaUrls()

        val textDocs = HashMap<String, String?>()
        var index = 0
        var importIndexCount = 0
        val result = mutableListOf<String>()

        tags.map { tag ->
            if (tagTextMap.containsKey(tag)) {
                var tagIndexCount = tagIndexMap[tag]!!
                if (!tagTextMap[tag].isNullOrEmpty() && tagTextMap[tag]!!.size > tagIndexCount) {
                    if (tagTextMap[tag]!![tagIndexCount]?.isNotEmpty()!!) {
                        if (!result.contains(tagTextMap[tag]!![tagIndexCount])) {
                            tagTextMap[tag]!![tagIndexCount]?.let { result.add(it) }
                            tempDataList.add(
                                    Resource(tag, tagTextMap[tag]!![tagIndexCount], null)
                            )
                        }
                    }

                    // update contents
                    textDocs[tag] = tagTextMap[tag]!![tagIndexCount]
                    tagIndexCount++
                    tagIndexMap.replace(tag, tagIndexCount) // update tag index
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
                                Resource(tag, MEDIA, data)
                        )
                        index++
                    }
                }

            } else if ("link" == tag) {
                if (importIndexCount < imports.size) {
                    if (!result.contains(imports[importIndexCount])) {
                        result.add(imports[importIndexCount])
                        tempDataList.add(
                                Resource(tag, imports[importIndexCount], null)
                        )
                        importIndexCount++
                    }
                }
            }
        }

        return tempDataList
    }

    private fun getImportUrls(): ArrayList<String> {
        val mImports = ArrayList<String>()

//        System.out.println("Imports: " + imports.size());
        imports.stream().map { link: Element ->
            mImports.add(link.attr("href").replace("\\\"", ""))
            link
        }.forEachOrdered { link: Element -> mImports.add(link.attr("rel").replace("\\\"", "")) }
        return mImports
    }

    private fun getMediaUrls(): ArrayList<MediaResource> {
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