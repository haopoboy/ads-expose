package io.github.haopooby

import io.github.haopooby.entity.Ads
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UtilsUnitTest {

    @Test
    fun getNoMapAds() {
        val noAdsMap = Utils.getNoAdsMap()
        assertThat(noAdsMap).isNotNull.isEmpty()
        (0..10).forEach { _ ->
            assertThat(noAdsMap[Ads()]).isEqualTo(Ads.NO_ADS)
            assertThat(noAdsMap.getOrDefault(Ads(), Ads())).isEqualTo(Ads.NO_ADS)
        }
    }
}
