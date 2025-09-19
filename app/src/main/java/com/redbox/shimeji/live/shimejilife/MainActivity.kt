package com.redbox.shimeji.live.shimejilife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.Pack
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.ShimejiGif
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb.Mascots
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator
import com.redbox.shimeji.live.shimejilife.ui.PacksViewModel
import com.redbox.shimeji.live.shimejilife.system.shimeji.displayservice.ShimejiService
import com.redbox.shimeji.live.shimejilife.system.akimeji.displayservice.AkimejiService
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.Pets as PetsT2
import com.redbox.shimeji.live.shimejilife.ui.theme.AkimejiTheme
import timber.log.Timber
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AkimejiTheme {
                App()
            }
        }
    }
}

@Composable
private fun App() {
    val vm: PacksViewModel = viewModel()
    val context = LocalContext.current
    val packs = vm.packsState.value
    val loading = vm.loading.value
    val error = vm.error.value
    val downloadedLive = ServiceLocator.mascotRepository.getLiveDataOfMascotsInDb()
    val downloaded: List<Mascots?> by (downloadedLive?.observeAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) })
    val downloadedPadorus: List<PetsT2> by ServiceLocator.petRepository.getLiveDataOfMascotsInDb().observeAsState(initial = emptyList())
    val downloading = vm.downloading.value
    val failed = vm.failed.value
    // Akimeji (T2)
    val akimejiThumbs = vm.akimejiThumbsState.value
    val akimejiDownloading = vm.akimejiDownloading.value
    val akimejiFailed = vm.akimejiFailed.value
    var activeIds by remember { mutableStateOf(ServiceLocator.helper.getActiveTeamMembers()) }
    var activeAkimejiIds by remember { mutableStateOf(ServiceLocator.helperT2.getActiveTeamMembers()) }
    LaunchedEffect(downloaded) {
        // Refresh active ids when downloads change (in case first selection depends on new data)
        activeIds = ServiceLocator.helper.getActiveTeamMembers()
    }
    LaunchedEffect(downloadedPadorus) {
        activeAkimejiIds = ServiceLocator.helperT2.getActiveTeamMembers()
    }

    var selectedPack by remember { mutableStateOf<Pack?>(null) }

    LaunchedEffect(Unit) { vm.loadPacks() }
    LaunchedEffect(Unit) { vm.loadAkimejiThumbs() }


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Downloaded", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (!hasOverlayPermission(context)) {
                            requestOverlayPermission(context)
                        } else {
                            if (activeIds.isEmpty()) {
                                android.widget.Toast.makeText(context, R.string.first, android.widget.Toast.LENGTH_SHORT).show()
                                context.stopService(android.content.Intent(context, ShimejiService::class.java))
                            } else {
                                // Ensure full initialization path runs: registers listeners and loads sprites
                                context.startService(android.content.Intent(context, ShimejiService::class.java))
                            }
                        }
                    }) { Text("Start") }
                    Button(onClick = {
                        context.stopService(android.content.Intent(context, ShimejiService::class.java))
                    }) { Text("Stop") }
                }
            }
            DownloadedList(
                items = downloaded,
                activeIds = activeIds.toSet(),
                onToggle = { mascot ->
                    mascot?.id?.let { id ->
                        activeIds = toggleActiveMascot(id, context)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Downloaded Padorus", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (!hasOverlayPermission(context)) {
                            requestOverlayPermission(context)
                        } else {
                            if (activeAkimejiIds.isEmpty()) {
                                android.widget.Toast.makeText(context, R.string.first, android.widget.Toast.LENGTH_SHORT).show()
                                context.stopService(android.content.Intent(context, AkimejiService::class.java))
                            } else {
                                context.startService(android.content.Intent(context, AkimejiService::class.java))
                            }
                        }
                    }) { Text("Start") }
                    Button(onClick = {
                        context.stopService(android.content.Intent(context, AkimejiService::class.java))
                    }) { Text("Stop") }
                }
            }
            DownloadedAkimejiList(
                items = downloadedPadorus,
                activeIds = activeAkimejiIds.toSet(),
                onToggle = { pet ->
                    pet.id?.let { id ->
                        activeAkimejiIds = toggleActiveAkimeji(id, context)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            Text("Padorus (Akimeji T2)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            Column {
                akimejiThumbs.forEach { t ->
                    val isDownloading = akimejiDownloading.contains(t.id)
                    val isFailed = akimejiFailed.contains(t.id)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = t.url,
                                contentDescription = null,
                                modifier = Modifier.height(56.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.width(12.dp))
                            Column { Text(t.name ?: "Unnamed"); Text("#${t.id}", style = MaterialTheme.typography.bodySmall) }
                        }
                        Button(onClick = { if (!isDownloading) vm.downloadAkimeji(t) }, enabled = !isDownloading) {
                            Text(when { isDownloading -> "Downloading..."; isFailed -> "Retry"; else -> "Download" })
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (selectedPack == null) "Packs" else selectedPack!!.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            if (selectedPack == null) {
                PacksList(packs = packs, loading = loading, onOpen = { selectedPack = it })
            } else {
                ShimejiList(
                    pack = selectedPack!!,
                    onBack = { selectedPack = null },
                    onDownload = { vm.downloadShimeji(it, selectedPack!!.id) },
                    downloadedIds = downloaded.mapNotNull { it?.id }.toSet(),
                    downloadingIds = downloading,
                    failedIds = failed
                )
            }
        }
    }
}

@Composable
private fun PacksList(packs: List<Pack>, loading: Boolean, onOpen: (Pack) -> Unit) {
    if (loading) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }
    Column {
        packs.forEach { pack ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(pack) }
                    .padding(16.dp)
            ) {
                Text(pack.title, fontWeight = FontWeight.SemiBold)
                pack.promobanner?.let { url ->
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        contentScale = ContentScale.Crop,
                        onError = { Timber.e(it.result.throwable) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimejiList(
    pack: Pack,
    onBack: () -> Unit,
    onDownload: (ShimejiGif) -> Unit,
    downloadedIds: Set<Int>,
    downloadingIds: Set<Int>,
    failedIds: Set<Int>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Back") }
        pack.shimejigif.forEach { item ->
                val isDownloaded = downloadedIds.contains(item.id)
                val isDownloading = downloadingIds.contains(item.id)
                val isFailed = failedIds.contains(item.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(item.thumb),
                            contentDescription = null,
                            modifier = Modifier.height(56.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(item.name ?: "Unnamed")
                            Text(item.nick ?: "", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    val buttonText = when {
                        isDownloaded -> "Downloaded"
                        isDownloading -> "Downloading..."
                        isFailed -> "Retry"
                        else -> "Download"
                    }
                    Button(
                        onClick = { if (!isDownloaded && !isDownloading) onDownload(item) },
                        enabled = !isDownloaded && !isDownloading,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) { Text(buttonText) }
                }
                if (isFailed) {
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
                        Text("Failed to download. Please retry.", color = MaterialTheme.colorScheme.error)
                    }
                }
        }
    }
}

private fun hasOverlayPermission(context: android.content.Context): Boolean {
    return android.provider.Settings.canDrawOverlays(context)
}

private fun requestOverlayPermission(context: android.content.Context) {
    val intent = android.content.Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION",
        ("package:" + context.packageName).toUri())
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun toggleActiveMascot(id: Int, context: android.content.Context): List<Int> {
    val helper = ServiceLocator.helper
    val current = helper.getActiveTeamMembers().toMutableList()
    if (current.contains(id)) {
        current.remove(id)
    } else {
        current.add(id)
    }
    helper.saveActiveTeamMembers(current)
    return current
}

private fun toggleActiveAkimeji(id: Int, context: android.content.Context): List<Int> {
    val helper = ServiceLocator.helperT2
    val current = helper.getActiveTeamMembers().toMutableList()
    if (current.contains(id)) {
        current.remove(id)
    } else {
        current.add(id)
    }
    helper.saveActiveTeamMembers(current)
    return current
}

@Composable
private fun DownloadedList(items: List<Mascots?>, activeIds: Set<Int>, onToggle: (Mascots?) -> Unit) {
    if (items.isEmpty()) {
        Text("No shimejis downloaded yet", modifier = Modifier.padding(16.dp))
        return
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically) {
        items.forEach { pet ->
            val bmp = pet?.bitmap?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            if (bmp != null) {
                val isActive = pet?.id?.let { activeIds.contains(it) } == true
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .height(64.dp)
                        .padding(end = 12.dp)
                        .clickable { onToggle(pet) }
                        .then(
                            if (isActive)
                                Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            else
                                Modifier.alpha(0.5f)
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun DownloadedAkimejiList(items: List<PetsT2>, activeIds: Set<Int>, onToggle: (PetsT2) -> Unit) {
    if (items.isEmpty()) {
        Text("No padorus downloaded yet", modifier = Modifier.padding(16.dp))
        return
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically) {
        items.forEach { pet ->
            val bmp = BitmapFactory.decodeByteArray(pet.bitmap, 0, pet.bitmap.size)
            val isActive = pet.id?.let { activeIds.contains(it) } == true
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .height(64.dp)
                    .padding(end = 12.dp)
                    .clickable { onToggle(pet) }
                    .then(
                        if (isActive)
                            Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        else
                            Modifier.alpha(0.5f)
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}