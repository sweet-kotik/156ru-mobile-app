package com.example.a156ru

import android.util.Xml
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class RssParser {

    private val dateFormats = listOf(
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH),
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    )

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parse(inputStream: InputStream): List<Advert> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, "UTF-8") // Явно указываем кодировку
        }
        return readFeed(parser)
    }

    private fun readFeed(parser: XmlPullParser): List<Advert> {
        val entries = mutableListOf<Advert>()

        // Ищем корневой тег <rss>
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "rss") {
                return readChannel(parser)
            }
        }
        return entries
    }

    private fun readItem(parser: XmlPullParser): Advert {
        var id = ""
        var title = ""
        var img: String? = null
        var city = ""
        var link = ""
        var rawDate = ""
        var pubDate = ""

        parser.require(XmlPullParser.START_TAG, null, "item")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue
            when (parser.name) {
                "id" -> id = readText(parser)
                "title" -> title = readText(parser).trim()
                "image" -> img = readText(parser).takeIf { it.isNotBlank() }
                "city" -> city = readText(parser).trim()
                "link" -> link = readText(parser)
                "pubdate" -> rawDate = readText(parser).also { pubDate = parseDate(it) ?: it }
                else -> skip(parser)
            }
        }
        return Advert(id, title, img, city, link, pubDate)
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) return
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    private fun parseDate(rawDate: String): String? {
        dateFormats.forEach { format ->
            try {
                val date = format.parse(rawDate) ?: return null
                return SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(date)
            } catch (e: Exception) {}
        }
        return null
    }

    suspend fun parseFromUrl(category: String): List<Advert> {
        return try {
            val url = "https://156.ru/api/ads.php?f=get_ads&cat=$category&rss"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            // Проверка успешного ответа
            if (!response.isSuccessful) {
                println("❌ HTTP error: ${response.code}")
                return emptyList()
            }

            // Получаем данные как строку в UTF-8
            val xmlString = response.body?.string() ?: return emptyList()
            println("📦 RAW XML:\n$xmlString") // Логируем сырые данные

            // Создаем поток с явным указанием кодировки
            val inputStream = ByteArrayInputStream(xmlString.toByteArray(StandardCharsets.UTF_8))
            parse(inputStream)
        } catch (e: Exception) {
            println("❌ Network error: ${e.localizedMessage}")
            emptyList()
        }
    }

    private fun readChannel(parser: XmlPullParser): List<Advert> {
        val items = mutableListOf<Advert>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "channel") {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.eventType == XmlPullParser.START_TAG && parser.name == "item") {
                        items.add(readItem(parser))
                    }
                }
            }
        }
        return items
    }
}