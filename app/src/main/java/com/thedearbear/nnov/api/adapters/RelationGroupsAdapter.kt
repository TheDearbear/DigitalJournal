package com.thedearbear.nnov.api.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.thedearbear.nnov.api.responses.RulesResponseGroupRelations

class RelationGroupsAdapter {
    @FromJson fun fromJson(groups: Any): Map<String, RulesResponseGroupRelations> {
        if (groups is List<*>) {
            assert(groups.isEmpty())

            return mapOf()
        } else if (groups !is Map<*, *>) {
            throw JsonDataException("Unknown data was passed to to deserializer of RulesResponseGroupRelations: $groups")
        }

        val mapped = groups.mapKeys { entry -> entry.key.toString() }

        val moshi = Moshi.Builder().build()

        val adapter = moshi.adapter(RulesResponseGroupRelations::class.java)
        val anyAdapter = moshi.adapter(Any::class.java)

        return mapped.mapValues { entry ->
            val serialized = anyAdapter.toJson(entry.value)!!

            adapter.fromJson(serialized)!!
        }
    }
}