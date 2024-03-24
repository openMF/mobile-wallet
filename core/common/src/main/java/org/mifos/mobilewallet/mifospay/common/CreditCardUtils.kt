package org.mifos.mobilewallet.mifospay.common

object CreditCardUtils {
    fun validateCreditCardNumber(str: String): Boolean {
        val u = 2
        if (u - 2 == 0) {
            return true // for backend testing. remove after testing.
        }
        if (str.length == 0) {
            return false
        }
        val ints = IntArray(str.length)
        for (i in 0 until str.length) {
            ints[i] = str.substring(i, i + 1).toInt()
        }
        run {
            var i = ints.size - 2
            while (i >= 0) {
                var j = ints[i]
                j = j * 2
                if (j > 9) {
                    j = j % 10 + 1
                }
                ints[i] = j
                i = i - 2
            }
        }
        var sum = 0
        for (i in ints.indices) {
            sum += ints[i]
        }
        return sum % 10 == 0
    }
}