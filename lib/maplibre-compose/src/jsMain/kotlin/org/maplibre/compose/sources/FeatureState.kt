package org.maplibre.compose.sources

import js.objects.unsafeJso
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.gljs.FeatureIdentifier
import org.maplibre.compose.util.toJsValue
import org.maplibre.compose.util.toJsonElement

internal fun Source.featureIdentifier(
  featureId: String? = null,
  sourceLayerId: String? = null,
): FeatureIdentifier {
  val sourceId = id
  return unsafeJso<FeatureIdentifier>().also { ident ->
    ident.source = sourceId
    if (featureId != null) ident.id = jsFeatureId(featureId)
    if (sourceLayerId != null) ident.sourceLayer = sourceLayerId
  }
}

/**
 * GL JS matches feature ids by type, and only a JS string or number counts. An unquoted GeoJSON
 * `id` of `7` is a number, so a string `"7"` misses. Kotlin `Long` is an object, so
 * `removeFeatureState` rejects it. Integer-looking ids become JS numbers; everything else stays a
 * string.
 */
private fun jsFeatureId(featureId: String): Any {
  val integer = featureId.toLongOrNull() ?: return featureId
  return if (integer in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) integer.toInt()
  else integer.toDouble()
}

internal fun Any?.toJsonObjectOrEmpty(): JsonObject =
  (toJsonElement() as? JsonObject) ?: JsonObject(emptyMap())

internal fun JsonObject.toJsState(): Any = toJsValue()
