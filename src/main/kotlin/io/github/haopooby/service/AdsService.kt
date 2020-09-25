package io.github.haopooby.service

import io.github.haopooby.entity.Ads

interface AdsService {
    fun exposeTo(userId: String): Ads
}