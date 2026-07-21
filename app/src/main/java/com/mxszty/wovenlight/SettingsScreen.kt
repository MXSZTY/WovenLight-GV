package com.mxszty.wovenlight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mxszty.wovenlight.manager.DataManager
import com.mxszty.wovenlight.model.CoordinateItem
import com.mxszty.wovenlight.model.UserAgentItem
import com.mxszty.wovenlight.model.UrlItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var urlList by remember { mutableStateOf(DataManager.getUrlList(context)) }
    var uaList by remember { mutableStateOf(DataManager.getUserAgentList(context)) }
    var coordList by remember { mutableStateOf(DataManager.getCoordinateList(context)) }
    var useVirtualLocation by remember { mutableStateOf(DataManager.getUseVirtualLocation(context)) }

    var showUrlDialog by remember { mutableStateOf(false) }
    var showUaDialog by remember { mutableStateOf(false) }
    var showCoordDialog by remember { mutableStateOf(false) }

    var editingUrl: UrlItem? by remember { mutableStateOf(null) }
    var editingUa: UserAgentItem? by remember { mutableStateOf(null) }
    var editingCoord: CoordinateItem? by remember { mutableStateOf(null) }

    var urlNameInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var uaNameInput by remember { mutableStateOf("") }
    var uaInput by remember { mutableStateOf("") }
    var coordNameInput by remember { mutableStateOf("") }
    var coordLatInput by remember { mutableStateOf("") }
    var coordLngInput by remember { mutableStateOf("") }

    var selectedUrlId by remember { mutableStateOf(DataManager.getSelectedUrlId(context)) }
    var selectedUaId by remember { mutableStateOf(DataManager.getSelectedUAId(context)) }
    var selectedCoordId by remember { mutableStateOf(DataManager.getSelectedCoordId(context)) }

    fun refreshLists() {
        urlList = DataManager.getUrlList(context)
        uaList = DataManager.getUserAgentList(context)
        coordList = DataManager.getCoordinateList(context)
        useVirtualLocation = DataManager.getUseVirtualLocation(context)
        selectedUrlId = DataManager.getSelectedUrlId(context)
        selectedUaId = DataManager.getSelectedUAId(context)
        selectedCoordId = DataManager.getSelectedCoordId(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("配置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                windowInsets = TopAppBarDefaults.windowInsets
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "URL 管理",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = {
                                editingUrl = null
                                urlNameInput = ""
                                urlInput = ""
                                showUrlDialog = true
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "添加")
                            }
                        }

                        if (urlList.isEmpty()) {
                            Text(
                                "暂无URL，请添加",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        } else {
                            Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                urlList.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (item.id == selectedUrlId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (item.id == selectedUrlId) 8.dp else 2.dp
                                        ),
                                        onClick = {
                                            DataManager.setSelectedUrlId(context, item.id)
                                            refreshLists()
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    item.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    item.url.take(30) + if (item.url.length > 30) "..." else "",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = {
                                                    editingUrl = item
                                                    urlNameInput = item.name
                                                    urlInput = item.url
                                                    showUrlDialog = true
                                                }) {
                                                    Icon(Icons.Filled.Edit, contentDescription = "编辑", modifier = Modifier.size(18.dp))
                                                }
                                                IconButton(onClick = {
                                                    DataManager.deleteUrl(context, item.id)
                                                    refreshLists()
                                                }) {
                                                    Icon(Icons.Filled.Delete, contentDescription = "删除", modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "User Agent 管理",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = {
                                editingUa = null
                                uaNameInput = ""
                                uaInput = ""
                                showUaDialog = true
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "添加")
                            }
                        }

                        Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedUaId.isEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (selectedUaId.isEmpty()) 8.dp else 2.dp
                                ),
                                onClick = {
                                    DataManager.setSelectedUAId(context, "")
                                    refreshLists()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "系统默认",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            "使用浏览器默认User Agent",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (uaList.isEmpty()) {
                                Text(
                                    "暂无自定义UA，请添加",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                uaList.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (item.id == selectedUaId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (item.id == selectedUaId) 8.dp else 2.dp
                                        ),
                                        onClick = {
                                            DataManager.setSelectedUAId(context, item.id)
                                            refreshLists()
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    item.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    item.userAgent.take(30) + if (item.userAgent.length > 30) "..." else "",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = {
                                                    editingUa = item
                                                    uaNameInput = item.name
                                                    uaInput = item.userAgent
                                                    showUaDialog = true
                                                }) {
                                                    Icon(Icons.Filled.Edit, contentDescription = "编辑", modifier = Modifier.size(18.dp))
                                                }
                                                IconButton(onClick = {
                                                    DataManager.deleteUserAgent(context, item.id)
                                                    refreshLists()
                                                }) {
                                                    Icon(Icons.Filled.Delete, contentDescription = "删除", modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("使用虚拟位置")
                            Switch(
                                checked = useVirtualLocation,
                                onCheckedChange = {
                                    useVirtualLocation = it
                                    DataManager.setUseVirtualLocation(context, it)
                                }
                            )
                        }

                        if (useVirtualLocation) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "坐标管理",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        editingCoord = null
                                        coordNameInput = ""
                                        coordLatInput = ""
                                        coordLngInput = ""
                                        showCoordDialog = true
                                    }
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "添加")
                                }
                            }

                            if (coordList.isEmpty()) {
                                Text(
                                    "暂无坐标，请添加",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    coordList.forEach { item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (item.id == selectedCoordId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = if (item.id == selectedCoordId) 8.dp else 2.dp
                                            ),
                                            onClick = {
                                                DataManager.setSelectedCoordId(context, item.id)
                                                refreshLists()
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        item.name,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Text(
                                                        "${item.latitude}, ${item.longitude}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    IconButton(
                                                        onClick = {
                                                            editingCoord = item
                                                            coordNameInput = item.name
                                                            coordLatInput = item.latitude.toString()
                                                            coordLngInput = item.longitude.toString()
                                                            showCoordDialog = true
                                                        }
                                                    ) {
                                                        Icon(Icons.Filled.Edit, contentDescription = "编辑", modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            DataManager.deleteCoordinate(context, item.id)
                                                            refreshLists()
                                                        }
                                                    ) {
                                                        Icon(Icons.Filled.Delete, contentDescription = "删除", modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showUrlDialog) {
        AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            title = { Text(if (editingUrl != null) "编辑URL" else "添加URL") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = urlNameInput,
                        onValueChange = { urlNameInput = it },
                        label = { Text("名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (urlNameInput.isNotEmpty() && urlInput.isNotEmpty()) {
                            if (editingUrl != null) {
                                DataManager.updateUrl(context, editingUrl!!.id, urlNameInput, urlInput)
                            } else {
                                DataManager.addUrl(context, urlNameInput, urlInput)
                            }
                            refreshLists()
                            showUrlDialog = false
                        }
                    },
                    enabled = urlNameInput.isNotEmpty() && urlInput.isNotEmpty()
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUrlDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showUaDialog) {
        AlertDialog(
            onDismissRequest = { showUaDialog = false },
            title = { Text(if (editingUa != null) "编辑UA" else "添加UA") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = uaNameInput,
                        onValueChange = { uaNameInput = it },
                        label = { Text("名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uaInput,
                        onValueChange = { uaInput = it },
                        label = { Text("User Agent") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uaNameInput.isNotEmpty() && uaInput.isNotEmpty()) {
                            if (editingUa != null) {
                                DataManager.updateUserAgent(context, editingUa!!.id, uaNameInput, uaInput)
                            } else {
                                DataManager.addUserAgent(context, uaNameInput, uaInput)
                            }
                            refreshLists()
                            showUaDialog = false
                        }
                    },
                    enabled = uaNameInput.isNotEmpty() && uaInput.isNotEmpty()
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUaDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showCoordDialog) {
        AlertDialog(
            onDismissRequest = { showCoordDialog = false },
            title = { Text(if (editingCoord != null) "编辑坐标" else "添加坐标") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = coordNameInput,
                        onValueChange = { coordNameInput = it },
                        label = { Text("名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = coordLatInput,
                            onValueChange = { coordLatInput = it },
                            label = { Text("纬度") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = coordLngInput,
                            onValueChange = { coordLngInput = it },
                            label = { Text("经度") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (coordNameInput.isNotEmpty() && coordLatInput.isNotEmpty() && coordLngInput.isNotEmpty()) {
                            try {
                                val lat = coordLatInput.toDouble()
                                val lng = coordLngInput.toDouble()
                                if (editingCoord != null) {
                                    DataManager.updateCoordinate(context, editingCoord!!.id, coordNameInput, lat, lng)
                                } else {
                                    DataManager.addCoordinate(context, coordNameInput, lat, lng)
                                }
                                refreshLists()
                                showCoordDialog = false
                            } catch (_: NumberFormatException) {
                            }
                        }
                    },
                    enabled = coordNameInput.isNotEmpty() && coordLatInput.isNotEmpty() && coordLngInput.isNotEmpty()
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCoordDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
