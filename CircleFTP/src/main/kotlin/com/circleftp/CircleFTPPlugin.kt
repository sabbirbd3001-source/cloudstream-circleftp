package com.circleftp

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class CircleFTPPlugin : Plugin() {
    override fun load(context: Context) {
        // Register CircleFTP provider
        registerMainAPI(CircleFTPProvider())
    }
}
