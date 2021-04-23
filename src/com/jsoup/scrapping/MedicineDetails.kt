package com.jsoup.scrapping

import com.google.gson.Gson
import org.jsoup.Jsoup
import java.io.FileWriter
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

data class MedicineInfo(
    val instance: Int?,
    val name: String?,
    val manufacturer: String?,
    val manufacturerUrl: String?,
    val genericName: String?,
    val strength: String?,
    val imageUrl: String?,
    val unitPrice: String?,
    val packPrice: String?,
    val alsoAvailable: List<AlsoAvailable>?,
    val details: List<Details>?
)

data class Body(val params: MedicineInfo)

data class AlsoAvailable(val url: String?, val data: String?)

data class Details(val header: String?, val body: String?)

data class Medicine(
    val id: Int,
    val name: String,
    val company: String,
    val group: String,
    val grey_lighten: String,
    val image_url: String,
    val detail_url: String,
    val price: String
)

object MedicineDetails {
    private const val URL = "http://www.medhis.life:8069"

    //    private const val URL = "http://192.168.1.101:8069"
    private const val CSV_HEADER = "json"

    @JvmStatic
    fun main(args: Array<String>) {
        val client = HttpClient.newBuilder().build()

        println("calling: /school/of/thought/medicine/get")
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$URL/school/of/thought/medicine/get"))
            .GET()
            .build()

        println("getting response")
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val objectMapper = Gson()
        println("parsing data")
        val requestBody: List<Medicine> = objectMapper.fromJson(response.body(), Array<Medicine>::class.java).toList()

//        val jsonList = mutableListOf<String>()
        println("medicine length :${requestBody.size}")

        var index = 0
        requestBody.forEach {
            getMedicineDetailInfo(it)
            index++
            println("Current Index: $index. Medicine id: ${it.id}")
//            jsonList.add(getMedicineDetailInfo(it))
        }
//        storeToCSV(jsonList)
    }

    private fun getMedicineDetailInfo(medicine: Medicine) {
        try {
            val docs = Jsoup.connect(medicine.detail_url).execute().body()

            val header = Jsoup.parse(docs).body().getElementsByClass("ac-header")
            val body = Jsoup.parse(docs).body().getElementsByClass("ac-body")

            val manufacturer = Jsoup.parse(docs).body().getElementsByClass("calm-link").text()
            val manufacturerUrl = Jsoup.parse(docs).body().getElementsByClass("calm-link").attr("href")

            val packageInfo = Jsoup.parse(docs).body().getElementsByClass("package-container").text().split("(")
            var unitPrice = ""
            var packPrice = ""

            if (packageInfo.size == 2) {
                unitPrice = packageInfo[0].replace(")", "")
                packPrice = packageInfo[1].replace(")", "")
            } else if (packageInfo.size == 1) {
                unitPrice = packageInfo[0].replace(")", "")
            }

            val alsoAvailable = mutableListOf<AlsoAvailable>()

            Jsoup.parse(docs).body().getElementsByClass("btn-sibling-brands").forEach {
                val url = it.attr("href")
                val info = it.text()

                alsoAvailable.add(
                    AlsoAvailable(
                        url = url,
                        data = info
                    )
                )
            }

            val details = mutableListOf<Details>()
            header.forEachIndexed { index, element ->
                details.add(
                    Details(
                        header = element.text(),
                        body = body[index].html()
                    )
                )
            }

            val medicineInfo = MedicineInfo(
                instance = medicine.id,
                imageUrl = medicine.image_url,
                name = medicine.name,
                genericName = medicine.group,
                strength = medicine.grey_lighten,
                manufacturer = manufacturer,
                manufacturerUrl = manufacturerUrl,
                alsoAvailable = alsoAvailable,
                details = details,
                unitPrice = unitPrice,
                packPrice = packPrice
            )

            saveMedicineDetail(medicineInfo)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveMedicineDetail(medicineInfo: MedicineInfo): String {
        val objectMapper = Gson()

        val requestBody = objectMapper
            .toJson(Body(medicineInfo))

        try {
            val client = HttpClient.newBuilder().build();
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$URL/school/of/thought/medicine/details"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            println(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return requestBody
    }

    private fun storeToCSV(json: List<String>) {
        var fileWriter: FileWriter? = null

        try {
            fileWriter = FileWriter("customer.csv")

            fileWriter.append(CSV_HEADER)
            fileWriter.append('\n')

            for (customer in json) {
                fileWriter.append(customer.toString())
//                fileWriter.append(',')
//                fileWriter.append(customer.name)
//                fileWriter.append(',')
//                fileWriter.append(customer.address)
//                fileWriter.append(',')
//                fileWriter.append(customer.age.toString())
                fileWriter.append('\n')
            }

            println("Write CSV successfully!")
        } catch (e: Exception) {
            println("Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                println("Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }
}