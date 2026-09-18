package com.elshadai.scripturehunt

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * A GPS-placed scripture marker near the player.
 * Positions are seeded from a coarse grid cell so markers stay stable across GPS jitter.
 */
data class ScriptureSpawn(
    val id: String,
    val scriptureId: String,
    val latitude: Double,
    val longitude: Double,
) {
    val scripture: Scripture?
        get() = ScriptureCatalog.byId(scriptureId)

    fun isClaimed(): Boolean = DiscoveredScripturesStore.isDiscovered(scriptureId)

    fun distanceMetersTo(lat: Double, lng: Double): Double =
        Geo.haversineMeters(latitude, longitude, lat, lng)
}

object SpawnGenerator {
    /** ~222 m cells — coarse enough that GPS noise doesn't reshuffle markers. */
    private const val CELL_SCALE = 500.0
    private const val MIN_DISTANCE_M = 80.0
    private const val MAX_DISTANCE_M = 400.0
    const val PROXIMITY_UNLOCK_M = 35.0

    fun cellId(lat: Double, lng: Double): String {
        val latCell = floor(lat * CELL_SCALE).toInt()
        val lngCell = floor(lng * CELL_SCALE).toInt()
        return "$latCell:$lngCell"
    }

    fun cellCenter(lat: Double, lng: Double): Pair<Double, Double> {
        val latC = (floor(lat * CELL_SCALE) + 0.5) / CELL_SCALE
        val lngC = (floor(lng * CELL_SCALE) + 0.5) / CELL_SCALE
        return latC to lngC
    }

    /**
     * Generate 6–12 stable spawns in a ring around the player's current grid cell.
     * Seeded from cell id (+ scripture id per marker) so the set does not jitter every GPS tick.
     */
    fun generateAround(playerLat: Double, playerLng: Double): List<ScriptureSpawn> {
        val cell = cellId(playerLat, playerLng)
        val (originLat, originLng) = cellCenter(playerLat, playerLng)
        val cellRng = Random(cell.hashCode().toLong())
        val count = cellRng.nextInt(6, 13)
        val catalog = ScriptureCatalog.all
        val picked = catalog.indices.shuffled(Random(cell.hashCode().toLong() xor 0x5C71L))
            .take(count.coerceAtMost(catalog.size))

        return picked.map { index ->
            val scripture = catalog[index]
            val spawnRng = Random((cell + "_" + scripture.id).hashCode().toLong())
            val distanceM = MIN_DISTANCE_M + spawnRng.nextDouble() * (MAX_DISTANCE_M - MIN_DISTANCE_M)
            val bearingDeg = spawnRng.nextDouble() * 360.0
            val (lat, lng) = Geo.destinationPoint(originLat, originLng, distanceM, bearingDeg)
            ScriptureSpawn(
                id = "${cell}_${scripture.id}",
                scriptureId = scripture.id,
                latitude = lat,
                longitude = lng,
            )
        }
    }
}

object Geo {
    private const val EARTH_RADIUS_M = 6_371_000.0

    fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_M * c
    }

    fun destinationPoint(lat: Double, lng: Double, distanceM: Double, bearingDeg: Double): Pair<Double, Double> {
        val angular = distanceM / EARTH_RADIUS_M
        val bearing = Math.toRadians(bearingDeg)
        val lat1 = Math.toRadians(lat)
        val lng1 = Math.toRadians(lng)
        val lat2 = asin(
            sin(lat1) * cos(angular) + cos(lat1) * sin(angular) * cos(bearing),
        )
        val lng2 = lng1 + atan2(
            sin(bearing) * sin(angular) * cos(lat1),
            cos(angular) - sin(lat1) * sin(lat2),
        )
        return Math.toDegrees(lat2) to Math.toDegrees(lng2)
    }
}
