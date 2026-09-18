package com.elshadai.scripturehunt

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * In-memory collection of scriptures the player has revealed this session.
 * v1 can persist to DataStore / map markers.
 */
object DiscoveredScripturesStore {
    val discovered: SnapshotStateList<Scripture> = mutableStateListOf()

    fun isDiscovered(id: String): Boolean = discovered.any { it.id == id }

    fun discover(scripture: Scripture): Boolean {
        if (isDiscovered(scripture.id)) return false
        discovered.add(0, scripture)
        return true
    }

    fun nextHidden(): Scripture? {
        val remaining = ScriptureCatalog.all.filterNot { isDiscovered(it.id) }
        return remaining.randomOrNull()
    }

    fun clearSession() {
        discovered.clear()
    }
}
