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
    private static final String RELPACE_WHITE_SPACE = "(\\\\r\\\\n|\\\\n)";
    private static final String HTML = "<div class=\\\"no-overflow\\\"><p></p>\\r\\n<h5><b><b><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\">Lecture-8:</span></b></b>\\r\\n</h5>\\r\\n<p><b><b></b></b>\\r\\n</p>\\r\\n<p><b><b>Topics of discussion:</b></b>\\r\\n</p>\\r\\n<p></p>\\r\\n<p></p>\\r\\n<p></p>\\r\\n<ul>\\r\\n    <li>Virtual Simulator (TinkerCAD)</li>\\r\\n</ul>\\r\\n<p></p>\\r\\n<p><b></b></p>\\r\\n<p><b><b><span><span>Expected learning outcome:</span></span></b></b>\\r\\n</p>\\r\\n<p></p>\\r\\n<ul>\\r\\n    <li>Understand the application of tinkerCAD</li>\\r\\n    <li>Creating account with TinkerCAD</li>\\r\\n    <li>Implement basic code with TinkerCAD</li>\\r\\n</ul>\\r\\n<p></p>\\r\\n<p><b>Reading Materials:</b><br></p>\\r\\n<p></p>\\r\\n<ul>\\r\\n    <li><span><span><span><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><a href=\\\"http://cpelearn.daffodilvarsity.edu.bd/pluginfile.php/391216/mod_label/intro/Lecture-8.pdf?time=1588394741992\\\" target=\\\"_blank\\\">Lecture Slide</a></span></span>\\r\\n        </span>\\r\\n        </span>\\r\\n        </span>\\r\\n    </li>\\r\\n</ul>\\r\\n<ul>\\r\\n    <li><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\">Video Tutorial</span></span>\\r\\n    </li><iframe src=\\\"https://drive.google.com/file/d/1_bSEWLllVEES5BLJqs5RRN2S0PT6SaGg/preview\\\" width=\\\"640\\\" height=\\\"480\\\"></iframe>\\r\\n</ul>\\r\\n<ul>\\r\\n    <li><a href=\\\"https://www.tinkercad.com/circuits\\\" target=\\\"_blank\\\"><span class=\\\"\\\" style=\\\"color: rgb(39, 174, 96);\\\">Link to TinkerCAD</span></a></li>\\r\\n</ul><br>\\r\\n<p></p><br><br>\\r\\n<p></p>\\r\\n<p></p><br><br>\\r\\n<p></p></div>";

    static ArrayList<String> mTag = new ArrayList();
    static Document doc = Jsoup.parse(
            HTML.replaceAll(RELPACE_WHITE_SPACE, "")
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

        Elements e = doc.getAllElements();
        System.out.println(doc);
        System.out.println("");
        System.out.println("=================================Result=================================\n");

        HashMap<String, List<String>> tagText = new HashMap<>();
        Map<String, Integer> tagIndex = new HashMap<>();

        for (Element element : e) {
            String tag = element.tag().toString();

            mTag.add(tag);
            tagIndex.putIfAbsent(tag, 0);

            if (!tagText.containsKey(tag)
                    && !tag.equals("a")
                    && !tag.equals("img")
                    && !tag.equals("iframe")
                    && !tag.equals("link")
                    && !tag.equals("body")
                    && !tag.equals("head")
                    && !tag.equals("div")
                    && !tag.equals("ul")
                    && !tag.equals("#root")
                    && !tag.equals("html")) { // not an image or link or iframe

                Elements data = doc.getElementsByTag(tag);
                List<String> array = new ArrayList<>();
                for (Element text : data) {
                    array.add(text.text());
                }

                tagText.putIfAbsent(tag, array);
            }
        }

        //todo: need to extract href and src   
        HashMap<String, String> textDocs = new HashMap<>();
        int index = 0;
        int importIndex = 0;

        List<String> result = new ArrayList<>();

        for (String tag : mTag) {

            if (tagText.containsKey(tag)) {
                int tagIndx = tagIndex.get(tag);

                if (!tagText.get(tag).isEmpty() && tagText.get(tag).size() > tagIndx) {
                    if (!tagText.get(tag).get(tagIndx).isEmpty()) {
//                        System.out.print(tag + ": " + tagIndx + " * ");
//                        System.out.println(tagText.get(tag).get(tagIndx));
                        if (!result.contains(tagText.get(tag).get(tagIndx))) {
                            result.add(tagText.get(tag).get(tagIndx));
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

                    strBuilder.append(data.getHeight() + "x" + data.getWidth());

                    if (!data.getImageName().isEmpty()) {
                        strBuilder.append(" * ");
                        strBuilder.append("(" + data.getImageName() + ")");
                    }

//                    System.out.println("media * " + str);
                    if (!result.contains(strBuilder.toString())) {
                        result.add(strBuilder.toString());
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
                        importIndex++;
                    }
                }
            }
        } // ends of tag loop

        result.forEach(str -> {
            System.out.println(str);
        });
        System.out.println("\n===============================Result Ends===============================");

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
