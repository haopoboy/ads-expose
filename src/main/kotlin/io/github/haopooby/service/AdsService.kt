package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import java.util.*

interface AdsService {
    fun exposeFor(userId: String): Ads
    fun random(userId: String = UUID.randomUUID().toString()): Ads
}