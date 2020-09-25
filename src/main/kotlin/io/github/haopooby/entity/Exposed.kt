package io.github.haopooby.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(indexes = [Index(columnList = "userId,adsId")])
@Document
class Exposed(
        var userId: String? = null,
        var adsId: String? = null,
        var expiredAt: Date? = null,
        var createdAt: Date? = Date()
) : Uuid()