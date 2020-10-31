package io.github.haopooby

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.haopooby.entity.Ads
import java.text.DecimalFormat
import java.util.concurrent.ConcurrentHashMap

class Utils {
    companion object {
        fun formatAsDecimal(n: Number): String = DecimalFormat("#,###").format(n)

        fun getObjectMapper() = Mapper.INSTANCE
        fun getNoAdsMap() = NoAdsMap.INSTANCE

        private class Mapper {
            companion object {
                val INSTANCE = ObjectMapper()
            }
        }

        private class NoAdsMap {
            companion object {
                val INSTANCE = Utils.NoAdsMap()
            }
        }
    }

    class NoAdsMap : ConcurrentHashMap<Ads, Ads>() {

        override fun get(key: Ads): Ads {
            return Ads.NO_ADS
        }

        override fun getOrDefault(key: Ads, defaultValue: Ads): Ads {
            return Ads.NO_ADS
        }
    }
}