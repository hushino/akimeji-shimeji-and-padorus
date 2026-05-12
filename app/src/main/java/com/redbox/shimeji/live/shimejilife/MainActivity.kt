package com.redbox.shimeji.live.shimejilife

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb.Mascots
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.Pack
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator
import com.redbox.shimeji.live.shimejilife.system.akimeji.displayservice.AkimejiService
import com.redbox.shimeji.live.shimejilife.system.shimeji.displayservice.ShimejiService
import com.redbox.shimeji.live.shimejilife.ui.PacksViewModel
import com.redbox.shimeji.live.shimejilife.ui.theme.AkimejiTheme
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.Pets as PetsT2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AkimejiTheme {
                MainScreen()
            }
        }
    }
}

enum class Screen(val icon: ImageVector, val label: String) {
    Library(Icons.Default.Inventory, "Library"),
    Padorus(Icons.Default.AutoAwesome, "Padorus"),
    Shimejis(Icons.Default.Collections, "Shimejis")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val vm: PacksViewModel = viewModel()
    var currentScreen by rememberSaveable { mutableStateOf(Screen.Library) }
    var selectedPack by remember { mutableStateOf<Pack?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadPacks()
        vm.loadAkimejiThumbs()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (selectedPack != null) selectedPack!!.title else stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    if (selectedPack != null) {
                        IconButton(onClick = { selectedPack = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "About")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Screen.entries.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentScreen == screen && selectedPack == null,
                        onClick = {
                            currentScreen = screen
                            selectedPack = null
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AnimatedContent(
                targetState = if (selectedPack != null) "pack_detail" else currentScreen.name,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { target ->
                when (target) {
                    Screen.Library.name -> LibraryScreen(vm)
                    Screen.Padorus.name -> PadorusScreen(vm)
                    Screen.Shimejis.name -> ShimejisScreen(vm, onOpenPack = { selectedPack = it })
                    "pack_detail" -> PackDetailScreen(selectedPack!!, vm)
                }
            }
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
fun LibraryScreen(vm: PacksViewModel) {
    val context = LocalContext.current
    val downloadedLive = ServiceLocator.mascotRepository.getLiveDataOfMascotsInDb()
    val downloaded: List<Mascots?> by (downloadedLive?.observeAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) })
    val downloadedPadorus: List<PetsT2> by ServiceLocator.petRepository.getLiveDataOfMascotsInDb().observeAsState(initial = emptyList())

    var activeIds by remember { mutableStateOf(ServiceLocator.helper.getActiveTeamMembers()) }
    var activeAkimejiIds by remember { mutableStateOf(ServiceLocator.helperT2.getActiveTeamMembers()) }

    var isShimejiRunning by remember { mutableStateOf(isServiceRunning(context, ShimejiService::class.java)) }
    var isAkimejiRunning by remember { mutableStateOf(isServiceRunning(context, AkimejiService::class.java)) }

    LaunchedEffect(downloaded) {
        activeIds = ServiceLocator.helper.getActiveTeamMembers()
    }
    LaunchedEffect(downloadedPadorus) {
        activeAkimejiIds = ServiceLocator.helperT2.getActiveTeamMembers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Overlay Permission Alert
        if (!hasOverlayPermission(context)) {
            AlertCard(
                title = "Permission Required",
                message = "Overlay permission is needed to show shimejis on your screen.",
                actionLabel = "Grant",
                onAction = { requestOverlayPermission(context) }
            )
        }

        // Shimeji Management
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ServiceControlCard(
                title = "Shimeji Service",
                isActive = isShimejiRunning,
                onStart = {
                    if (!hasOverlayPermission(context)) {
                        requestOverlayPermission(context)
                    } else {
                        if (activeIds.isEmpty()) {
                            android.widget.Toast.makeText(context, R.string.first, android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            context.startService(android.content.Intent(context, ShimejiService::class.java))
                        }
                    }
                    isShimejiRunning = isServiceRunning(context, ShimejiService::class.java)
                },
                onStop = { 
                    context.stopService(android.content.Intent(context, ShimejiService::class.java))
                    isShimejiRunning = false
                }
            )

            SectionHeader("Downloaded Shimejis")
            DownloadedList(
                items = downloaded,
                activeIds = activeIds.toSet(),
                onToggle = { mascot ->
                    mascot?.id?.let { id ->
                        activeIds = toggleActiveMascot(id, context)
                    }
                }
            )
        }

        HorizontalDivider(modifier = Modifier.alpha(0.5f))

        // Padoru Management
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ServiceControlCard(
                title = "Padoru Service",
                isActive = isAkimejiRunning,
                onStart = {
                    if (!hasOverlayPermission(context)) {
                        requestOverlayPermission(context)
                    } else {
                        if (activeAkimejiIds.isEmpty()) {
                            android.widget.Toast.makeText(context, R.string.first, android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            context.startService(android.content.Intent(context, AkimejiService::class.java))
                        }
                    }
                    isAkimejiRunning = isServiceRunning(context, AkimejiService::class.java)
                },
                onStop = { 
                    context.stopService(android.content.Intent(context, AkimejiService::class.java))
                    isAkimejiRunning = false
                }
            )

            SectionHeader("Downloaded Padorus")
            DownloadedAkimejiList(
                items = downloadedPadorus,
                activeIds = activeAkimejiIds.toSet(),
                onToggle = { pet ->
                    pet.id?.let { id ->
                        activeAkimejiIds = toggleActiveAkimeji(id, context)
                    }
                }
            )
        }
    }
}

@Composable
fun PadorusScreen(vm: PacksViewModel) {
    val loading = vm.loading.value
    val error = vm.error.value
    val akimejiThumbs = vm.akimejiThumbsState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (error != null) {
            ErrorMessage(error)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader("Available Padorus", "New & Hot")
            if (akimejiThumbs.isEmpty() && loading) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                akimejiThumbs.forEach { t ->
                    BrowseItemCard(
                        name = t.name ?: "Unnamed",
                        id = t.id,
                        imageUrl = t.url,
                        isDownloading = vm.akimejiDownloading.value.contains(t.id),
                        isFailed = vm.akimejiFailed.value.contains(t.id),
                        onDownload = { vm.downloadAkimeji(t) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShimejisScreen(vm: PacksViewModel, onOpenPack: (Pack) -> Unit) {
    val packs = vm.packsState.value
    val loading = vm.loading.value
    val error = vm.error.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (error != null) {
            ErrorMessage(error)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader("Shimeji Packs", "Collections")
            if (loading && packs.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                packs.forEach { pack ->
                    PackCard(pack, onClick = { onOpenPack(pack) })
                }
            }
        }
    }
}

@Composable
fun PackDetailScreen(pack: Pack, vm: PacksViewModel) {
    val downloadedIds = ServiceLocator.mascotRepository.getLiveDataOfMascotsInDb()
        ?.observeAsState(initial = emptyList())?.value?.mapNotNull { it?.id }?.toSet() ?: emptySet()
    
    val downloadingIds = vm.downloading.value
    val failedIds = vm.failed.value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Shimejis in this pack:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(pack.shimejigif) { item ->
            val isDownloaded = downloadedIds.contains(item.id)
            val isDownloading = downloadingIds.contains(item.id)
            val isFailed = failedIds.contains(item.id)

            BrowseItemCard(
                name = item.name ?: "Unnamed",
                id = item.id,
                imageUrl = item.thumb,
                isDownloading = isDownloading,
                isFailed = isFailed,
                isDownloaded = isDownloaded,
                onDownload = { if (!isDownloaded && !isDownloading) vm.downloadShimeji(item, pack.id) }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ServiceControlCard(title: String, isActive: Boolean, onStart: () -> Unit, onStop: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape,
                        modifier = Modifier.size(14.dp)
                    ) {}
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Text(
                            if (isActive) "Service is active" else "Service is stopped",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStart,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Start")
                }
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
fun AlertCard(title: String, message: String, actionLabel: String, onAction: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = onAction,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun BrowseItemCard(
    name: String,
    id: Int,
    imageUrl: String?,
    isDownloading: Boolean,
    isFailed: Boolean,
    isDownloaded: Boolean = false,
    onDownload: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("#$id", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
                
                if (isDownloaded) {
                    FilledTonalButton(onClick = {}, enabled = false) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Installed")
                    }
                } else {
                    Button(
                        onClick = onDownload,
                        enabled = !isDownloading,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text(if (isFailed) "Retry" else "Get")
                        }
                    }
                }
            }
            if (isDownloading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun PackCard(pack: Pack, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            pack.promobanner?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(pack.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        "${pack.shimejigif.size} Shimejis included",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About Akimeji") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Version 2.1 - Material 3 Rework", fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.imagehomebuttonshimeji), style = MaterialTheme.typography.bodySmall)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Open Source", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(
                        "This project is open source! We welcome contributions from the community to help make Akimeji even better.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, "https://github.com/hushino/akimeji-shimeji-and-padorus".toUri())
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View on GitHub")
                    }
                }
                
                Text(
                    text = "Art credits available in the credits section.",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.alpha(0.7f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun ErrorMessage(error: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(12.dp))
            Text(error, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DownloadedList(items: List<Mascots?>, activeIds: Set<Int>, onToggle: (Mascots?) -> Unit) {
    if (items.isEmpty()) {
        EmptyState("No Shimejis downloaded yet.")
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { mascot ->
            val bmp = mascot?.bitmap?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            if (bmp != null) {
                val isActive = mascot.id?.let { activeIds.contains(it) } == true
                DownloadedItem(
                    bitmap = bmp.asImageBitmap(),
                    isActive = isActive,
                    onClick = { onToggle(mascot) }
                )
            }
        }
    }
}

@Composable
private fun DownloadedAkimejiList(items: List<PetsT2>, activeIds: Set<Int>, onToggle: (PetsT2) -> Unit) {
    if (items.isEmpty()) {
        EmptyState("No Padorus downloaded yet.")
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { pet ->
            val bmp = BitmapFactory.decodeByteArray(pet.bitmap, 0, pet.bitmap.size)
            val isActive = pet.id?.let { activeIds.contains(it) } == true
            DownloadedItem(
                bitmap = bmp.asImageBitmap(),
                isActive = isActive,
                onClick = { onToggle(pet) }
            )
        }
    }
}

@Composable
fun DownloadedItem(bitmap: androidx.compose.ui.graphics.ImageBitmap, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isActive) 3.dp else 0.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isActive) 1f else 0.7f),
            contentScale = ContentScale.Fit
        )
        if (isActive) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopEnd).size(20.dp),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Utility functions kept from original code
private fun isServiceRunning(context: android.content.Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    @Suppress("DEPRECATION")
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

private fun hasOverlayPermission(context: android.content.Context): Boolean {
    return android.provider.Settings.canDrawOverlays(context)
}

private fun requestOverlayPermission(context: android.content.Context) {
    val intent = android.content.Intent(
        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
        ("package:" + context.packageName).toUri()
    )
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
