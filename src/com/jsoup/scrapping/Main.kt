/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsoup.scrapping

import com.google.gson.Gson
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.concurrent.thread
import java.io.FileWriter
import java.io.IOException
import java.util.Arrays

/**
 *
 * @author Sabbir
 */
object Main {
    private const val REPlACE_WHITE_SPACE = "(\\\\r\\\\n|\\\\n|\\\\t)"

    @JvmStatic
    fun main(args: Array<String>) {
        thread {
            Thread.sleep(1000)

            var docs = Jsoup.connect("https://medex.com.bd/brands").execute().body()

            var classes = Jsoup.parse(docs).body().getElementsByClass("hoverable-block")

            printMedicineElements(classes = classes)

            for (i in 1..644) {
                docs = Jsoup.connect("https://medex.com.bd/brands?page=$i").execute().body()
                classes = Jsoup.parse(docs).body().getElementsByClass("hoverable-block")
                printMedicineElements(classes = classes)
            }
        }
    }

    private fun printMedicineElements(classes: Elements) {
        classes.forEach {
            var group: String = ""
            try {
                group = it.getElementsByClass("col-xs-12")[2].text()
            } catch (e: Exception) {
                println("Error to get group name")
            }
            val values = MedicineInfoRequest(
                params = Params(
                    it.getElementsByTag("a").attr("href"),
                    it.getElementsByTag("img").attr("src"),
                    it.getElementsByClass("data-row-top").removeClass("md-icon-container").text(),
                    it.getElementsByClass("grey-ligten").text(),
                    it.getElementsByClass("data-row-company").text(),
                    group,
                    it.getElementsByClass("package-pricing").text()
                )
            )

            val objectMapper = Gson()
            val requestBody: String = objectMapper
                .toJson(values)

            val client = HttpClient.newBuilder().build();
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://192.168.1.104:8069/school/of/thought/medicine"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString());
            println(response.body())
        }
    }
}

class Customer {
    var id: String? = null
    var name: String? = null
    var address: String? = null
    var age: Int = 0

    constructor() {}
    constructor(id: String?, name: String?, address: String?, age: Int) {
        this.id = id
        this.name = name
        this.address = address
        this.age = age
    }

    override fun toString(): String {
        return "Customer [id=" + id + ", name=" + name + ", address=" + address + ", age=" + age + "]"
    }
}