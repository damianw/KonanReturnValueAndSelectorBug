package com.github.damianw.konanreturnvalueandselectorbug

import platform.Foundation.*

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Decimal = NSDecimalNumber

actual operator fun Decimal.plus(augend: Decimal): Decimal = decimalNumberByAdding(augend)
actual operator fun Decimal.minus(augend: Decimal): Decimal = decimalNumberBySubtracting(augend)

actual object Decimals {

    actual val ZERO = Decimal("0.0")

}
