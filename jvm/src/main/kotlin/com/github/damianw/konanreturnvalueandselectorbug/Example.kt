package com.github.damianw.konanreturnvalueandselectorbug

import java.math.BigDecimal

actual typealias Decimal = BigDecimal

actual operator fun Decimal.plus(augend: Decimal): Decimal = add(augend)
actual operator fun Decimal.minus(augend: Decimal): Decimal = subtract(augend)

actual object Decimals {

    actual val ZERO: Decimal = BigDecimal.ZERO

}
