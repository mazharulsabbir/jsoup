/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Sabbir
 */
public class HtmlDataParser {

    private final Document doc;
    private List<Resource> resource;

    public HtmlDataParser(Document doc) {
        this.doc = doc;
    }

    public List<Resource> getResource() {
        Elements imports = doc.select("link[href]");
        Elements urls = doc.select("img[src]~a[href],a[href]~img[src],a[href],[src]");

        getResult(printUrls(urls), printImports(imports));
        return resource;
    }

    private void getResult(ArrayList<MediaResource> links, ArrayList<String> imports) {
        resource = new ArrayList<>();

        ArrayList<String> mTag = new ArrayList();
        Elements e = doc.getAllElements();

        HashMap<String, List<String>> tagText = new HashMap<>();
        Map<String, Integer> tagIndex = new HashMap<>();

        e.stream().map((element) -> element.tag().toString()).map((tag) -> {
            mTag.add(tag);
            return tag;
        }).map((tag) -> {
            tagIndex.putIfAbsent(tag, 0);
            return tag;
        }).filter((tag) -> (!tagText.containsKey(tag)
                && !tag.equals("a")
                && !tag.equals("img")
                && !tag.equals("iframe")
                && !tag.equals("link")
                && !tag.equals("body")
                && !tag.equals("head")
                && !tag.equals("div")
                && !tag.equals("ul")
                && !tag.equals("#root")
                && !tag.equals("html"))).forEachOrdered((tag) -> {
            // not an image or link or iframe

            Elements data = doc.getElementsByTag(tag);
            List<String> array = new ArrayList<>();
            data.forEach((text) -> {
                array.add(text.text());
            });

            tagText.putIfAbsent(tag, array);
        });

        HashMap<String, String> textDocs = new HashMap<>();
        int index = 0;
        int importIndex = 0;

        List<String> result = new ArrayList<>();
        // ================== data parsing ===================

        for (String tag : mTag) {

            if (tagText.containsKey(tag)) {
                int tagIndx = tagIndex.get(tag);

                if (!tagText.get(tag).isEmpty() && tagText.get(tag).size() > tagIndx) {
                    if (!tagText.get(tag).get(tagIndx).isEmpty()) {
//                        System.out.print(tag + ": " + tagIndx + " * ");
//                        System.out.println(tagText.get(tag).get(tagIndx));
                        if (!result.contains(tagText.get(tag).get(tagIndx))) {
                            result.add(tagText.get(tag).get(tagIndx));

                            resource.add(
                                    new Resource(
                                            tag,
                                            tagText.get(tag).get(tagIndx),
                                            new MediaResource()
                                    )
                            );
                        }
                    }

                    // update contents
                    textDocs.put(tag, tagText.get(tag).get(tagIndx));
                    tagIndx++;
                    tagIndex.replace(tag, tagIndx); // update tag index
                }

            } else if ("img".equals(tag) || "iframe".equals(tag) || "a".equals(tag)) {

                if (index < links.size()) {
                    var data = links.get(index);

                    StringBuilder strBuilder = new StringBuilder();

                    if (!data.getImageSrc().isEmpty()) {
                        strBuilder.append(data.getImageSrc());
                        strBuilder.append(" * ");
                    }

                    if (!data.getImageHref().isEmpty()) {
                        strBuilder.append(data.getImageHref());
                        strBuilder.append(" * ");
                    }

                    strBuilder.append(data.getHeight()).append("x").append(data.getWidth());

                    if (!data.getImageName().isEmpty()) {
                        strBuilder.append(" * ");
                        strBuilder.append("(").append(data.getImageName()).append(")");
                    }

//                    System.out.println("media * " + str);
                    if (!result.contains(strBuilder.toString())) {
                        result.add(strBuilder.toString());                        
                        
                        resource.add(
                                new Resource(
                                        tag,
                                        "media",
                                        data
                                )
                        );
                        index++;
                    }

                } else {
//                    System.out.println("overflow");
                }
            } else if ("link".equals(tag)) {
                if (importIndex < imports.size()) {
//                    System.out.println("imports * " + imports.get(importIndex));

                    if (!result.contains(imports.get(importIndex))) {
                        result.add(imports.get(importIndex));
                        resource.add(
                                new Resource(
                                        tag,
                                        imports.get(importIndex),
                                        new MediaResource()
                                )
                        );
                        importIndex++;
                    }
                }
            }
        } // ends of tag loop
    }

    private ArrayList<String> printImports(Elements imports) {
        ArrayList<String> mImports = new ArrayList<>();

//        System.out.println("Imports: " + imports.size());
        imports.stream().map((link) -> {
            mImports.add(link.attr("href").replace("\\\"", ""));
            return link;
        }).forEachOrdered((link) -> {
            mImports.add(link.attr("rel").replace("\\\"", ""));
        });

        return mImports;
    }

    private ArrayList<MediaResource> printUrls(Elements urls) {
        Set<String> hrefSets = new HashSet<>();
        Set<String> srcSets = new HashSet<>();

        ArrayList<MediaResource> links = new ArrayList<>();

        urls.forEach((link) -> {
            var src = "";
            int height = 0;
            int width = 0;
            var aHref = "";
            var name = link.text().replace("\\\"", "");

            var mElementTag = "img";
            src = selectIframeOrSrc(mElementTag, link).get("src");
            height = convertToInt(selectIframeOrSrc(mElementTag, link).get("height"));
            width = convertToInt(selectIframeOrSrc(mElementTag, link).get("width"));

            if (src.isEmpty()) {
                mElementTag = "iframe";
                src = selectIframeOrSrc(mElementTag, link).get("src");
                height = convertToInt(selectIframeOrSrc(mElementTag, link).get("height"));
                width = convertToInt(selectIframeOrSrc(mElementTag, link).get("width"));
            }

            if (link.select("a") != null) {
                aHref = link.select("a").attr("href").replace("\\\"", "");
            } else {
                aHref = link.attr("href").replace("\\\"", "");
            }

            if (aHref.isEmpty()) {
                if (!src.isEmpty()) {
                    if (!srcSets.contains(src)) {
                        srcSets.add(src);
                        links.add(new MediaResource(name, src, aHref, height, width));
                    }
                }
            } else {
                if (!src.isEmpty()) {
                    if (!srcSets.contains(src) && !hrefSets.contains(aHref)) {
                        srcSets.add(src);
                        hrefSets.add(aHref);
                        links.add(new MediaResource(name, src, aHref, height, width));
                    }
                } else {
                    if (!hrefSets.contains(aHref)) {
                        srcSets.add(src);
                        links.add(new MediaResource(name, src, aHref, height, width));
                    }
                }
            }
        });

        return links;
    }

    private HashMap<String, String> selectIframeOrSrc(String tag, Element link) {
        HashMap<String, String> map = new HashMap<>();

        if (link.select(tag) != null) {
            map.put("src", link.select(tag).attr("src").replace("\\\"", ""));
            map.put("height", link.select(tag).attr("height").replace("\\\"", ""));
            map.put("width", link.select(tag).attr("width").replace("\\\"", ""));
        } else {
            map.put("src", link.attr("src").replace("\\\"", ""));
            map.put("height", link.attr("height").replace("\\\"", ""));
            map.put("width", link.attr("width").replace("\\\"", ""));
        }

        return map;
    }

    private int convertToInt(String str) {
        int res = 0;
        if (!str.isEmpty()) {
            try {
                res = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("Can't convert '" + str + "' to int. Error: " + e.getLocalizedMessage());
            }
        }

        return res;
    }

}
