package com.circleftp

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.Qualities
import java.net.URLEncoder

class CircleFTPProvider : MainAPI() {
    override var mainUrl = "http://new.circleftp.net:5000/api"
    override var name = "Circle FTP"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override var lang = "bn"
    override val hasMainPage = true

    private val uploadUrl = "http://new.circleftp.net:5000/uploads"
    private val mapper = jacksonObjectMapper()

    private fun toSearchResponse(node: JsonNode): SearchResponse? {
        val id = node.get("id")?.asInt() ?: return null
        val title = node.get("title")?.asText() ?: node.get("name")?.asText() ?: return null
        val typeStr = node.get("type")?.asText() ?: "singleVideo"

        // Filter out non-video media (games, software repacks)
        if (typeStr.equals("singleFile", ignoreCase = true) || typeStr.equals("multiFile", ignoreCase = true)) {
            return null
        }

        val image = node.get("image")?.asText() ?: node.get("imageSm")?.asText()
        val posterUrl = if (!image.isNullOrBlank()) "$uploadUrl/$image" else null
        val year = node.get("year")?.asText()?.toIntOrNull()
        val quality = node.get("quality")?.asText()
        val dataUrl = "$mainUrl/posts/$id"

        return if (typeStr.equals("series", ignoreCase = true)) {
            newTvSeriesSearchResponse(title, dataUrl, TvType.TvSeries) {
                this.posterUrl = posterUrl
                this.year = year
                if (!quality.isNullOrBlank()) addQuality(quality)
            }
        } else {
            newMovieSearchResponse(title, dataUrl, TvType.Movie) {
                this.posterUrl = posterUrl
                this.year = year
                if (!quality.isNullOrBlank()) addQuality(quality)
            }
        }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val homePageLists = mutableListOf<HomePageList>()
        val response = app.get("$mainUrl/home-page/getHomePagePosts").text
        val root = mapper.readTree(response)

        // 1. Latest Releases
        root.get("latestPost")?.let { arr ->
            if (arr.isArray) {
                val items = arr.mapNotNull { toSearchResponse(it) }
                if (items.isNotEmpty()) {
                    homePageLists.add(HomePageList("Latest Releases", items))
                }
            }
        }

        // 2. Most Visited
        root.get("mostVisitedPosts")?.let { arr ->
            if (arr.isArray) {
                val items = arr.mapNotNull { toSearchResponse(it) }
                if (items.isNotEmpty()) {
                    homePageLists.add(HomePageList("Most Visited", items))
                }
            }
        }

        // 3. Category Sections (English Movies, Hindi Movies, TV Series, South Indian, etc.)
        root.get("categoryPosts")?.let { arr ->
            if (arr.isArray) {
                for (catNode in arr) {
                    val catName = catNode.get("name")?.asText() ?: "Category"
                    val postsNode = catNode.get("posts")
                    if (postsNode != null && postsNode.isArray) {
                        val items = postsNode.mapNotNull { toSearchResponse(it) }
                        if (items.isNotEmpty()) {
                            homePageLists.add(HomePageList(catName, items))
                        }
                    }
                }
            }
        }

        return newHomePageResponse(homePageLists, hasNext = false)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val encoded = URLEncoder.encode(query.trim(), "UTF-8")
        val url = "$mainUrl/posts?searchTerm=$encoded&order=desc"
        val response = app.get(url).text
        val root = mapper.readTree(response)
        val posts = root.get("posts") ?: return emptyList()
        return posts.mapNotNull { toSearchResponse(it) }
    }

    override suspend fun load(url: String): LoadResponse {
        val response = app.get(url).text
        val root = mapper.readTree(response)

        val title = root.get("title")?.asText() ?: root.get("name")?.asText() ?: "Unknown"
        val type = root.get("type")?.asText() ?: "singleVideo"
        val image = root.get("image")?.asText() ?: root.get("imageSm")?.asText()
        val posterUrl = if (!image.isNullOrBlank()) "$uploadUrl/$image" else null
        val plot = root.get("metaData")?.asText()
        val year = root.get("year")?.asText()?.toIntOrNull()
        val tags = root.get("tags")?.asText()?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }

        return if (type.equals("series", ignoreCase = true)) {
            val episodesList = mutableListOf<Episode>()
            val contentNode = root.get("content")
            if (contentNode != null && contentNode.isArray) {
                for (seasonNode in contentNode) {
                    val seasonName = seasonNode.get("seasonName")?.asText() ?: "Season 1"
                    val seasonNum = Regex("""\d+""").find(seasonName)?.value?.toIntOrNull() ?: 1
                    val episodesNode = seasonNode.get("episodes")
                    if (episodesNode != null && episodesNode.isArray) {
                        for ((idx, epNode) in episodesNode.withIndex()) {
                            val epTitle = epNode.get("title")?.asText() ?: "Episode ${idx + 1}"
                            val epLink = epNode.get("link")?.asText() ?: ""
                            if (epLink.isNotBlank()) {
                                val epNum = Regex("""(?:E|Episode\s*)(\d+)""", RegexOption.IGNORE_CASE)
                                    .find(epTitle)?.groupValues?.get(1)?.toIntOrNull() ?: (idx + 1)
                                episodesList.add(
                                    newEpisode(epLink) {
                                        this.name = epTitle
                                        this.season = seasonNum
                                        this.episode = epNum
                                    }
                                )
                            }
                        }
                    }
                }
            }
            newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodesList) {
                this.posterUrl = posterUrl
                this.plot = plot
                this.year = year
                this.tags = tags
            }
        } else {
            val videoLink = root.get("content")?.asText() ?: ""
            newMovieLoadResponse(title, url, TvType.Movie, videoLink) {
                this.posterUrl = posterUrl
                this.plot = plot
                this.year = year
                this.tags = tags
            }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        if (data.isBlank()) return false

        val quality = when {
            data.contains("2160p", ignoreCase = true) || data.contains("4K", ignoreCase = true) -> Qualities.P2160.value
            data.contains("1080p", ignoreCase = true) -> Qualities.P1080.value
            data.contains("720p", ignoreCase = true) -> Qualities.P720.value
            data.contains("480p", ignoreCase = true) -> Qualities.P480.value
            else -> Qualities.Unknown.value
        }

        callback.invoke(
            ExtractorLink(
                source = this.name,
                name = this.name,
                url = data,
                referer = "",
                quality = quality,
                type = ExtractorLinkType.VIDEO
            )
        )
        return true
    }
}
