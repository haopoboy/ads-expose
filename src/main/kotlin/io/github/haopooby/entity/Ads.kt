package io.github.haopooby.entity

import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(indexes = [Index(columnList = "url")])
@Document
class Ads(
        var title: String? = null,
        var url: String? = null,
        var capIntervalMin: Int = 60,
        var capNum: Int = 3,
        var exposedLimited: Boolean = capNum >= 0
) : Uuid() {

    companion object {
        val NO_ADS = Ads("No ads", capNum = -1)
    }
}