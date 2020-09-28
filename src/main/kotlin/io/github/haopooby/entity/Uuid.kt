package io.github.haopooby.entity

import org.springframework.data.annotation.Id
import java.util.*
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class Uuid(
        @get:Id @get:javax.persistence.Id var id: String = UUID.randomUUID().toString()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Uuid

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}