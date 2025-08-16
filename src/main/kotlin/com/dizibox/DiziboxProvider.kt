package com.dizibox

import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.model.SAnime
import eu.kanade.tachiyomi.source.model.SEpisode
import eu.kanade.tachiyomi.source.model.Video
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class DiziboxProvider : HttpSource() {

    override val name = "DiziBox"
    override val baseUrl = "https://www.dizibox.so"
    override val lang = "tr"
    override val supportsLatest = true

    private val client = OkHttpClient()

    override fun fetchPopularAnime(): List<SAnime> {
        val animeList = mutableListOf<SAnime>()
        val url = "$baseUrl/diziler/"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body!!.string())

        // Dizileri A-Z listeden çek
        val items = doc.select("ul.alphabetical-category-list li a")
        for (element in items) {
            val anime = SAnime.create().apply {
                title = element.text().trim()
                url = element.attr("href")
            }
            animeList.add(anime)
        }

        return animeList
    }

    override fun fetchEpisodes(anime: SAnime): List<SEpisode> {
        val episodeList = mutableListOf<SEpisode>()
        val request = Request.Builder().url("$baseUrl${anime.url}").build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body!!.string())

        val items = doc.select("a.season-episode")
        for (element in items) {
            val episode = SEpisode.create().apply {
                name = element.text().trim()
                url = element.attr("href")
            }
            episodeList.add(episode)
        }

        return episodeList
    }

    override fun fetchVideoList(episode: SEpisode): List<Video> {
        val videoList = mutableListOf<Video>()
        val request = Request.Builder().url("$baseUrl${episode.url}").build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body!!.string())

        // iframe video URL’lerini al
        val iframe = doc.selectFirst("iframe")
        iframe?.let {
            val videoUrl = it.attr("src")
            videoList.add(Video(videoUrl, "HD", videoUrl))
        }

        return videoList
    }

    override fun searchAnime(query: String): List<SAnime> {
        val results = mutableListOf<SAnime>()
        val popular = fetchPopularAnime()
        for (anime in popular) {
            if (anime.title.contains(query, ignoreCase = true)) results.add(anime)
        }
        return results
    }
}
