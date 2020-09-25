package io.github.haopooby.service

import io.github.haopooby.entity.Ads

interface AdsService {
    fun exposeFor(userId: String): Ads
    fun random(): Ads
}