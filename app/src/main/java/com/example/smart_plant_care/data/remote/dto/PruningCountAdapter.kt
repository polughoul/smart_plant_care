package com.example.smart_plant_care.data.remote.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PruningCountAdapter : JsonDeserializer<PruningCount?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PruningCount? {
        if (json.isJsonNull) return null

        val target = when {
            json.isJsonObject -> json
            json.isJsonArray -> json.asJsonArray.firstOrNull()
            else -> null
        } ?: return null

        return runCatching { context.deserialize<PruningCount>(target, PruningCount::class.java) }
            .getOrNull()
    }
}

