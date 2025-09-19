package com.redbox.shimeji.live.shimejilife.data.repoT2

import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.AkimejiRepository
import com.redbox.shimeji.live.shimejilife.system.akimeji.SpriteUtil
import com.redbox.shimeji.live.shimejilife.system.akimeji.Sprites
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class SpritesServiceAkimejiT2(private val db: AkimejiRepository, private val helper: HelperT2) {

    //🖤🎶💎
    fun setSizeMultiplier(multiplier: Double, mascotId: Int) {
        runBlocking {
            if (multiplier != sizeMultiplier) {
                sizeMultiplier = multiplier
                if (!cachedSprites.isEmpty()) {
                    for (key in cachedSprites.keys()) {
                        Timber.e("setSizeMultiplierAkimeji t2")
                        cachedSprites[key] = helper.resizeSprites(
                            Sprites(
                                db.getShimejis(
                                    key.toInt(),
                                    SpriteUtil.usedSprites(mascotId)
                                )
                            ),
                            sizeMultiplier
                        )
                    }
                }
            }
        }
    }

    fun getSpritesById(id: Int): Sprites {
        if (!cachedSprites.containsKey(Integer.valueOf(id)) || cachedSprites[Integer.valueOf(id)] == null) {
            addMascot(db, id)
        }
        return cachedSprites[Integer.valueOf(id)]!!
    }

    fun loadSpritesForMascots(ids: List<Int>) {
        invalidateSprites(ids)
        for (id: Int in ids) {
            if (!cachedSprites.containsKey(id)) {
                addMascot(db, id)
            }
        }
    }

    private fun addMascot(db: AkimejiRepository, id: Int) {
        runBlocking {
            val mascotAssets = db.getShimejis(id, SpriteUtil.usedSprites(id))
            if (mascotAssets.isNotEmpty()) {
                cachedSprites[Integer.valueOf(id)] =
                    helper.resizeSprites(
                        Sprites(
                            mascotAssets
                        ),
                        sizeMultiplier
                    )
            }
        }
    }

    private fun invalidateSprites(activeShimejis: List<Int>) {
        val distinctIds = HashSet(activeShimejis)
        val cacheIds = HashSet(cachedSprites.keys)
        cacheIds.removeAll(distinctIds)
        for (id in cacheIds) {
            Timber.e("Invalidating id t2: $id")
            (cachedSprites[id] as Sprites).recycle()
            cachedSprites.remove(id)
        }
    }

    companion object {
        internal var cachedSprites = ConcurrentHashMap<Int, Sprites>(100)
        internal var instance: SpritesServiceAkimejiT2? = null
        internal var sizeMultiplier: Double = 1.0

    }
}