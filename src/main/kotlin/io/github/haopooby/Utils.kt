package io.github.haopooby

import java.text.DecimalFormat

class Utils {
    companion object {
        fun formatAsDecimal(n: Number): String = DecimalFormat("#,###").format(n)
    }
}