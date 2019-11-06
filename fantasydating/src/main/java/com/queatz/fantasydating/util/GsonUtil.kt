package com.queatz.fantasydating.util

import com.arangodb.velocypack.internal.util.DateUtil
import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.time.Instant
import java.util.*

class InstantTypeConverter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
    override fun serialize(
        src: Instant,
        srcType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(DateUtil.format(Date.from(src)))
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): Instant? {
        return try {
            DateUtil.parse(json.asString).toInstant()
        } catch (e: ParseException) {
            null
        }
    }
}