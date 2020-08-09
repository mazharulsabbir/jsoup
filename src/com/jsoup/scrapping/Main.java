/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping;

import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 *
 * @author Sabbir
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static ArrayList<String> mTag = new ArrayList();
    static Document doc = Jsoup.parse(
            "<div class=\\\"no-overflow\\\"><p></p>\\r\\n<h5><img src=\\\"http://cpelearn.daffodilvarsity.edu.bd/pluginfile.php/391302/mod_label/intro/Lecture-2.png\\\" alt=\\\"\\\" width=\\\"1244\\\" height=\\\"426\\\" role=\\\"presentation\\\" class=\\\"img-responsive atto_image_button_text-top\\\"><br></h5><ul>\\r\\n</ul>\\r\\n<p></p>\\r\\n<p><b><span>Reading Materials:</span></b><br></p>\\r\\n<p></p>\\r\\n<ul>\\r\\n    <li><span><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><a href=\\\"http://cpelearn.daffodilvarsity.edu.bd/pluginfile.php/391302/mod_label/intro/Lecture-2.pdf?time=1590838502526\\\" target=\\\"_blank\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\">Lecture Slide</span></a></span></span></span></li><li><span><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><a href=\\\"http://cpelearn.daffodilvarsity.edu.bd/pluginfile.php/391302/mod_label/intro/Lecture-2.pdf?time=1588394176495\\\" target=\\\"_blank\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\">Video Slide</span></a></span></span></span>\\r\\n        </span>\\r\\n    </li>\\r\\n</ul><iframe src=\\\"https://drive.google.com/file/d/1gPemladkysvP9uHpi4wHaqQxHbUbvWRP/preview\\\" width=\\\"640\\\" height=\\\"480\\\"></iframe></div>"
    );

    public static void main(String[] args) {
        // TODO code application logic here

        Elements imports = doc.select("link[href]");
        Elements urls = doc.select("img[src]~a[href],a[href]~img[src],a[href],[src]");
//        System.out.println(urls);
//        System.out.println(imports);

        extractResource(printUrls(urls), printImports(imports));
    }

    private static void extractResource(ArrayList<ImageSource> links, ArrayList<String> imports) {

        Map<String, String> map = new HashMap<>();

        Elements e = doc.getAllElements();
        System.out.println(doc);
        System.out.println("");
        System.out.println("=================================\n");

        for (Element element : e) {
            String tag = element.tag().toString();
            mTag.add(tag);

            if (!map.containsValue(element.select(tag))
                    && !tag.equals("a")
                    && !tag.equals("img")
                    && !tag.equals("iframe")
                    && !tag.equals("link")
                    && !tag.equals("#root")
                    && !tag.equals("html")) { // not an image or link or iframe

                if (!map.containsKey(tag) && !map.containsValue(element.text())) {
                    if ("body".equals(tag)) {
                        if (element.childNodeSize() == 0) {
                            map.put(tag, element.text());
                        }
                    } else if ("div".equals(tag)) {
                        if (element.childNodeSize() == 0) {
                            map.put(tag, element.text());
                        }
                    } else {
                        map.put(tag, element.text());
                    }
                }else {
                    if(!map.containsValue(element.text())){
                        if ("body".equals(tag)) {
                            if (element.childNodeSize() == 0) {
                                map.put(tag, element.text());
                            }
                        } else if ("div".equals(tag)) {
                            if (element.childNodeSize() == 0) {
                                map.put(tag, element.text());
                            }
                        } else {
                            map.put(tag, element.text());
                        }
                    }
                }
            }
        }        

        System.out.println(map.keySet());
        
        //todo: need to extract href and src   
        HashMap<String, String> textDocs = new HashMap<>();
        int index = 0;
        int importIndex = 0;

        for (String tag : mTag) {

            if (map.containsKey(tag)) {
                if (!map.get(tag).isEmpty()) {
                    if (!textDocs.containsKey(tag) && !textDocs.containsValue(map.get(tag))) {
                        System.out.println(map.get(tag));
                        textDocs.put(tag, map.get(tag));
                    }
                }

            } else if (tag == "img" || tag == "iframe" || tag == "a") {

                if (index < links.size()) {
                    var data = links.get(index);

                    System.out.print(data.getImageName() + " ");
                    System.out.print(data.getImageSrc() + " ");
                    System.out.print(data.getImageHref() + " ");
                    System.out.print(data.getHeight() + "x" + data.getWidth());
                    System.out.println("");
                    index++;
                } else {
                    System.out.println("overflow");
                }
            } else if (tag == "link") {
                if (importIndex < imports.size()) {
                    System.out.println(imports.get(importIndex));
                    importIndex++;
                }
            }
        }
        System.out.println("");
        System.out.println(mTag);
    }

    private static ArrayList<String> printImports(Elements imports) {
        ArrayList<String> mImports = new ArrayList<>();

//        System.out.println("Imports: " + imports.size());
        for (Element link : imports) {
            mImports.add(link.attr("href").replace("\\\"", ""));
            mImports.add(link.attr("rel").replace("\\\"", ""));
        }

        return mImports;
    }

    private static ArrayList<ImageSource> printUrls(Elements urls) {
        Set<String> hrefSets = new HashSet<>();
        Set<String> srcSets = new HashSet<>();

        ArrayList<ImageSource> links = new ArrayList<>();

        urls.forEach((Element link) -> {
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
                        links.add(new ImageSource(name, src, aHref, height, width));
                    }
                }
            } else {
                if (!src.isEmpty()) {
                    if (!srcSets.contains(src) && !hrefSets.contains(aHref)) {
                        srcSets.add(src);
                        hrefSets.add(aHref);
                        links.add(new ImageSource(name, src, aHref, height, width));
                    }
                } else {
                    if (!hrefSets.contains(aHref)) {
                        srcSets.add(src);
                        links.add(new ImageSource(name, src, aHref, height, width));
                    }
                }
            }
        });

        return links;
    }

    private static HashMap<String, String> selectIframeOrSrc(String tag, Element link) {
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

    private static int convertToInt(String str) {
        int res = 0;
        if (!str.isEmpty() && str != null) {
            res = Integer.parseInt(str);
        }

        return res;
    }
}
