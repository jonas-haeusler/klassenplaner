package gsrv.klassenplaner.util

import java.util.*

/**
 * Returns the current weekday formatted with a long name such as "Monday".
 */
fun Calendar.getWeekday(): String? =
    getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

var Calendar.year
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

var Calendar.month
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

var Calendar.dayOfMonth
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_MONTH, value)

var Calendar.hourOfDay
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) = set(Calendar.HOUR_OF_DAY, value)

var Calendar.minute
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)
