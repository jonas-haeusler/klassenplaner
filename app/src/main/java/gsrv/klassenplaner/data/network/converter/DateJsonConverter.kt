package gsrv.klassenplaner.data.network.converter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.*

internal class DateJsonConverter : TypeAdapter<Date>() {

    override fun write(out: JsonWriter, value: Date?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.time / 1000)
        }
    }

    override fun read(reader: JsonReader): Date {
        return Date(reader.nextLong() * 1000)
    }
}
