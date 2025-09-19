package com.redbox.shimeji.live.shimejilife.ui

import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.Pack
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.ShimejiGif
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator
import com.redbox.shimeji.live.shimejilife.common.constants.AkimejiT2Constants
import com.redbox.shimeji.live.shimejilife.data.repoT1.TeamListingService
import com.redbox.shimeji.live.shimejilife.ShimejiListing
import com.redbox.shimeji.live.shimejilife.data.repoT2.HelperT2
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.AkimejiListing
import com.redbox.shimeji.live.shimejilife.common.constants.AppConstants
import org.json.JSONObject
import java.util.zip.ZipInputStream
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PacksViewModel : ViewModel() {
    val packsState = mutableStateOf<List<Pack>>(emptyList())
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val downloading = mutableStateOf<Set<Int>>(emptySet())
    val message = mutableStateOf<String?>(null)
    val failed = mutableStateOf<Set<Int>>(emptySet())
    // Akimeji (T2) state
    val akimejiThumbsState = mutableStateOf<List<AkimejiListing>>(emptyList())
    val akimejiDownloading = mutableStateOf<Set<Int>>(emptySet())
    val akimejiFailed = mutableStateOf<Set<Int>>(emptySet())

    private val apiShimeji = ServiceLocator.apiServiceShimeji
    private val apiAkimeji = ServiceLocator.apiServiceAkimeji
    private val teamListingService = TeamListingService()
    private val helperT2 = HelperT2(ServiceLocator.appContext)

    fun loadPacks() {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val resp = apiShimeji.getPacks()
                if (resp.isSuccessful) {
                    resp.body()?.let { body ->
                        packsState.value = body.packs
                    } ?: run {
                        error.value = "Empty response"
                    }
                } else {
                    error.value = "HTTP ${resp.code()}"
                }
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                loading.value = false
            }
        }
    }

    fun downloadShimeji(item: ShimejiGif, packId: Int) {
        viewModelScope.launch {
            try {
                downloading.value = downloading.value + item.id
                failed.value = failed.value - item.id
                // Download ZIP of frames from T1 server and save into T1 DB
                val zipUrl = AkimejiT2Constants.SERVER_BASE_PATH_SERVER_PROD2.trimEnd('/') + "/" + (item.shimejiGif ?: "")
                val resp = apiShimeji.downloadImage(zipUrl)
                if (!resp.isSuccessful) {
                    message.value = "Download failed (HTTP ${'$'}{resp.code()})"
                    failed.value = failed.value + item.id
                    return@launch
                }
                val body = resp.body()
                if (body == null) {
                    message.value = "Empty response body"
                    failed.value = failed.value + item.id
                    return@launch
                }
                val thumbnails = ArrayList<android.graphics.Bitmap>()
                withContext(Dispatchers.IO) {
                    body.byteStream().use { stream ->
                        val byteArrayOut = ByteArrayOutputStream()
                        val buffer = ByteArray(4096)
                        var count: Int
                        while (true) {
                            count = stream.read(buffer)
                            if (count == -1) break
                            byteArrayOut.write(buffer, 0, count)
                        }
                        val zis = ZipInputStream(ByteArrayInputStream(byteArrayOut.toByteArray()))
                        while (zis.nextEntry != null) {
                            byteArrayOut.reset()
                            while (true) {
                                count = zis.read(buffer)
                                if (count == -1) break
                                byteArrayOut.write(buffer, 0, count)
                            }
                            val bmp = BitmapFactory.decodeByteArray(byteArrayOut.toByteArray(), 0, byteArrayOut.size())
                            if (bmp != null) thumbnails.add(bmp)
                        }
                    }
                }
                if (thumbnails.isEmpty()) {
                    message.value = "No frames found"
                    failed.value = failed.value + item.id
                    return@launch
                }
                val mascot = ShimejiListing().apply {
                    id = item.id
                    name = item.name
                    visibility = true
                    status = com.redbox.shimeji.live.shimejilife.R.string.download_finish
                    setStatuss = com.redbox.shimeji.live.shimejilife.R.string.download_finish
                }
                withContext(Dispatchers.IO) {
                    teamListingService.addMascot(mascot, thumbnails)
                }
                message.value = "Downloaded ${item.name ?: ("#" + item.id)}"
                failed.value = failed.value - item.id
            } catch (e: Exception) {
                message.value = e.message ?: "Download failed"
                failed.value = failed.value + item.id
            } finally {
                downloading.value = downloading.value - item.id
            }
        }
    }

    fun consumeMessage() {
        message.value = null
    }

    fun loadAkimejiThumbs() {
        viewModelScope.launch {
            try {
                val url = AppConstants.SERVER_BASE_PATH + "akimejiT2/listThumbs2T2.json"
                val resp = apiAkimeji.downloadImage(url)
                if (!resp.isSuccessful) {
                    error.value = "HTTP ${'$'}{resp.code()}"
                    return@launch
                }
                val body = resp.body() ?: return@launch
                val text = withContext(Dispatchers.IO) { body.string() }
                val list = try {
                    // If it's a raw array
                    helperT2.parseJSON(JSONObject(text).getJSONArray("shimejis").toString())
                } catch (_: Exception) {
                    // If wrapped in object with key "shimejis"
                    helperT2.parseJSON(text)
                }
                akimejiThumbsState.value = list
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun downloadAkimeji(thumb: AkimejiListing) {
        viewModelScope.launch {
            try {
                akimejiDownloading.value = akimejiDownloading.value + thumb.id
                akimejiFailed.value = akimejiFailed.value - thumb.id
                // Download from GitHub thumbs endpoint using the id
                val packUrl = AppConstants.SERVER_BASE_PATH + "akimejiT2/thumb/" + thumb.id
                val resp = apiAkimeji.downloadImage(packUrl)
                if (!resp.isSuccessful) {
                    message.value = "Download failed ${'$'}packUrl (HTTP ${'$'}{resp.code()})"
                    akimejiFailed.value = akimejiFailed.value + thumb.id
                    return@launch
                }
                val body = resp.body() ?: run {
                    akimejiFailed.value = akimejiFailed.value + thumb.id
                    return@launch
                }
                val thumbnails = ArrayList<android.graphics.Bitmap>()
                withContext(Dispatchers.IO) {
                    body.byteStream().use { stream ->
                        val byteArrayOut = java.io.ByteArrayOutputStream()
                        val buffer = ByteArray(4096)
                        var count: Int
                        while (true) {
                            count = stream.read(buffer)
                            if (count == -1) break
                            byteArrayOut.write(buffer, 0, count)
                        }
                        val allBytes = byteArrayOut.toByteArray()
                        var entries = 0
                        try {
                            val zis = java.util.zip.ZipInputStream(java.io.ByteArrayInputStream(allBytes))
                            while (zis.nextEntry != null) {
                                entries++
                                byteArrayOut.reset()
                                while (true) {
                                    count = zis.read(buffer)
                                    if (count == -1) break
                                    byteArrayOut.write(buffer, 0, count)
                                }
                                val bmp = BitmapFactory.decodeByteArray(byteArrayOut.toByteArray(), 0, byteArrayOut.size())
                                if (bmp != null) thumbnails.add(bmp)
                            }
                        } catch (_: Exception) {
                            // not a zip stream
                        }
                        if (entries == 0 && thumbnails.isEmpty()) {
                            // Try as a single image (sprite or preview)
                            val bmp = BitmapFactory.decodeByteArray(allBytes, 0, allBytes.size)
                            if (bmp != null) thumbnails.add(bmp)
                        }
                    }
                }
                if (thumbnails.isEmpty()) {
                    message.value = "No frames found"
                    akimejiFailed.value = akimejiFailed.value + thumb.id
                    return@launch
                }
                // Save into T2 DB via PetRepository on IO dispatcher
                ServiceLocator.petRepository.addMascotToDatabase(thumb.id, thumb.name ?: "akimeji_${'$'}{thumb.id}", thumbnails)
                //message.value = "Downloaded ${'$'}{thumb.name ?: ("#" + thumb.id)}"
            } catch (e: Exception) {
                message.value = e.message ?: "Download failed"
                akimejiFailed.value = akimejiFailed.value + thumb.id
            } finally {
                akimejiDownloading.value = akimejiDownloading.value - thumb.id
            }
        }
    }
}

