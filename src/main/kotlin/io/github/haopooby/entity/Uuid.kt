package io.github.haopooby.entity

import org.springframework.data.annotation.Id
import java.util.*
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class Uuid(
        @get:Id @get:javax.persistence.Id var id: String = UUID.randomUUID().toString()
)