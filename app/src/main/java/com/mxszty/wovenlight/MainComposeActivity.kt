package com.mxszty.wovenlight

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.mxszty.wovenlight.manager.DataManager
import com.mxszty.wovenlight.model.CoordinateItem
import com.mxszty.wovenlight.model.UserAgentItem
import com.mxszty.wovenlight.model.UrlItem

class MainComposeActivity : ComponentActivity() {

    inner class LocationInterface {
        private var cachedLatitude: Double? = null
        private var cachedLongitude: Double? = null
        private var locationFetchInProgress = false
        private val handler = android.os.Handler(android.os.Looper.getMainLooper())

        fun refreshLocationCache() {
            if (locationFetchInProgress) {
                return
            }

            if (ContextCompat.checkSelfPermission(this@MainComposeActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@MainComposeActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainComposeActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    requestCodeLocationPermissions)
                return
            }

            locationFetchInProgress = true
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            var location: Location? = null

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }

            if (location == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            cachedLatitude = location?.latitude ?: 39.9042
            cachedLongitude = location?.longitude ?: 116.4074
            locationFetchInProgress = false
        }

        @JavascriptInterface
        @Suppress("UNUSED")
        fun getLatitude(): Double {
            val useVirtualLocation = DataManager.getUseVirtualLocation(this@MainComposeActivity)

            if (useVirtualLocation) {
                val coord = DataManager.getSelectedCoord(this@MainComposeActivity)
                if (coord != null) {
                    return coord.latitude
                }
                return 39.9042
            }

            if (cachedLatitude == null) {
                handler.post { refreshLocationCache() }
            }
            return cachedLatitude ?: 39.9042
        }

        @JavascriptInterface
        @Suppress("UNUSED")
        fun getLongitude(): Double {
            val useVirtualLocation = DataManager.getUseVirtualLocation(this@MainComposeActivity)

            if (useVirtualLocation) {
                val coord = DataManager.getSelectedCoord(this@MainComposeActivity)
                if (coord != null) {
                    return coord.longitude
                }
                return 116.4074
            }

            if (cachedLongitude == null) {
                handler.post { refreshLocationCache() }
            }
            return cachedLongitude ?: 116.4074
        }

        @JavascriptInterface
        @Suppress("UNUSED")
        fun getUseVirtualLocation(): Boolean {
            return DataManager.getUseVirtualLocation(this@MainComposeActivity)
        }

        @JavascriptInterface
        @Suppress("UNUSED")
        fun showToast(message: String) {
            runOnUiThread {
                android.widget.Toast.makeText(this@MainComposeActivity, message, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var webView: WebView
    private lateinit var locationInterface: LocationInterface

    private val requestCodeLocationPermissions = 1001

    var showSettings by mutableStateOf(false)
    var showQuickSwitchDialog by mutableStateOf(false)

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        setupSystemBars()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSystemBars()

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@MainComposeActivity.handleBackPress()
            }
        })

        setContent {
            val context = LocalContext.current
            val isDarkTheme = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

            fun getDynamicColor(dynamicResId: Int, fallbackResId: Int): Color {
                return if (Build.VERSION.SDK_INT >= 31) {
                    Color(ContextCompat.getColor(context, dynamicResId))
                } else {
                    Color(ContextCompat.getColor(context, fallbackResId))
                }
            }

            @Suppress("NewApi")
            val colorScheme = if (isDarkTheme) {
                darkColorScheme(
                    primary = getDynamicColor(android.R.color.system_accent1_500, R.color.m3_primary),
                    onPrimary = getDynamicColor(android.R.color.system_accent1_0, R.color.m3_on_primary),
                    primaryContainer = getDynamicColor(android.R.color.system_accent1_800, R.color.m3_primary_container),
                    onPrimaryContainer = getDynamicColor(android.R.color.system_accent1_100, R.color.m3_on_primary_container),
                    secondary = getDynamicColor(android.R.color.system_accent2_500, R.color.m3_secondary),
                    onSecondary = getDynamicColor(android.R.color.system_accent2_0, R.color.m3_on_secondary),
                    secondaryContainer = getDynamicColor(android.R.color.system_accent2_800, R.color.m3_secondary_container),
                    onSecondaryContainer = getDynamicColor(android.R.color.system_accent2_100, R.color.m3_on_secondary_container),
                    tertiary = getDynamicColor(android.R.color.system_accent3_500, R.color.m3_tertiary),
                    onTertiary = getDynamicColor(android.R.color.system_accent3_0, R.color.m3_on_tertiary),
                    tertiaryContainer = getDynamicColor(android.R.color.system_accent3_800, R.color.m3_tertiary_container),
                    onTertiaryContainer = getDynamicColor(android.R.color.system_accent3_100, R.color.m3_on_tertiary_container),
                    background = getDynamicColor(android.R.color.system_neutral1_900, R.color.m3_background),
                    onBackground = getDynamicColor(android.R.color.system_neutral1_100, R.color.m3_on_background),
                    surface = getDynamicColor(android.R.color.system_neutral1_900, R.color.m3_surface),
                    onSurface = getDynamicColor(android.R.color.system_neutral1_100, R.color.m3_on_surface),
                    surfaceVariant = getDynamicColor(android.R.color.system_neutral2_800, R.color.m3_surface_variant),
                    onSurfaceVariant = getDynamicColor(android.R.color.system_neutral2_300, R.color.m3_on_surface_variant),
                    outline = getDynamicColor(android.R.color.system_neutral2_600, R.color.m3_outline),
                    outlineVariant = getDynamicColor(android.R.color.system_neutral2_800, R.color.m3_outline_variant),
                    error = Color(ContextCompat.getColor(context, R.color.m3_error)),
                    onError = Color(ContextCompat.getColor(context, R.color.m3_on_error)),
                    errorContainer = Color(ContextCompat.getColor(context, R.color.m3_error_container)),
                    onErrorContainer = Color(ContextCompat.getColor(context, R.color.m3_on_error_container))
                )
            } else {
                lightColorScheme(
                    primary = getDynamicColor(android.R.color.system_accent1_600, R.color.m3_primary),
                    onPrimary = getDynamicColor(android.R.color.system_accent1_0, R.color.m3_on_primary),
                    primaryContainer = getDynamicColor(android.R.color.system_accent1_100, R.color.m3_primary_container),
                    onPrimaryContainer = getDynamicColor(android.R.color.system_accent1_900, R.color.m3_on_primary_container),
                    secondary = getDynamicColor(android.R.color.system_accent2_600, R.color.m3_secondary),
                    onSecondary = getDynamicColor(android.R.color.system_accent2_0, R.color.m3_on_secondary),
                    secondaryContainer = getDynamicColor(android.R.color.system_accent2_100, R.color.m3_secondary_container),
                    onSecondaryContainer = getDynamicColor(android.R.color.system_accent2_900, R.color.m3_on_secondary_container),
                    tertiary = getDynamicColor(android.R.color.system_accent3_600, R.color.m3_tertiary),
                    onTertiary = getDynamicColor(android.R.color.system_accent3_0, R.color.m3_on_tertiary),
                    tertiaryContainer = getDynamicColor(android.R.color.system_accent3_100, R.color.m3_tertiary_container),
                    onTertiaryContainer = getDynamicColor(android.R.color.system_accent3_900, R.color.m3_on_tertiary_container),
                    background = getDynamicColor(android.R.color.system_neutral1_100, R.color.m3_background),
                    onBackground = getDynamicColor(android.R.color.system_neutral1_900, R.color.m3_on_background),
                    surface = getDynamicColor(android.R.color.system_neutral1_100, R.color.m3_surface),
                    onSurface = getDynamicColor(android.R.color.system_neutral1_900, R.color.m3_on_surface),
                    surfaceVariant = getDynamicColor(android.R.color.system_neutral2_100, R.color.m3_surface_variant),
                    onSurfaceVariant = getDynamicColor(android.R.color.system_neutral2_700, R.color.m3_on_surface_variant),
                    outline = getDynamicColor(android.R.color.system_neutral2_500, R.color.m3_outline),
                    outlineVariant = getDynamicColor(android.R.color.system_neutral2_300, R.color.m3_outline_variant),
                    error = Color(ContextCompat.getColor(context, R.color.m3_error)),
                    onError = Color(ContextCompat.getColor(context, R.color.m3_on_error)),
                    errorContainer = Color(ContextCompat.getColor(context, R.color.m3_error_container)),
                    onErrorContainer = Color(ContextCompat.getColor(context, R.color.m3_on_error_container))
                )
            }

            MaterialTheme(colorScheme = colorScheme) {
                Material3App()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(webView: WebView, userAgent: String) {
        val webSettings = webView.settings

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        webSettings.setGeolocationEnabled(true)

        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        if (userAgent.isNotEmpty()) {
            webSettings.userAgentString = userAgent
        } else {
            webSettings.userAgentString = WebSettings.getDefaultUserAgent(this)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                if (ContextCompat.checkSelfPermission(
                        this@MainComposeActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    callback?.invoke(origin, true, false)
                } else {
                    ActivityCompat.requestPermissions(
                        this@MainComposeActivity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        requestCodeLocationPermissions
                    )
                }
            }
        }

        locationInterface = LocationInterface()
        webView.addJavascriptInterface(locationInterface, "AndroidLocation")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectLocationScripts(view)
            }
        }
    }

    private fun injectLocationScripts(webView: WebView?) {
        webView?.evaluateJavascript("""
            (function() {
                console.log('Location Guard: Initializing...');

                var originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
                var originalWatchPosition = navigator.geolocation.watchPosition;
                var originalClearWatch = navigator.geolocation.clearWatch;

                console.log('Location Guard: Saved original geolocation methods');

                var locationToastShown = false;

                function createPosition(lat, lng) {
                    return {
                        coords: {
                            latitude: lat,
                            longitude: lng,
                            accuracy: 10,
                            altitude: null,
                            altitudeAccuracy: null,
                            heading: null,
                            speed: null
                        },
                        timestamp: Date.now()
                    };
                }

                function showLocationToastOnce(message) {
                    if (!locationToastShown) {
                        locationToastShown = true;
                        try {
                            window.AndroidLocation.showToast(message);
                        } catch (e) {
                            console.error('Location Guard: Error showing toast:', e.message);
                        }
                    }
                }

                function getFakePosition() {
                    var lat = 39.9042;
                    var lng = 116.4074;

                    if (typeof window.AndroidLocation !== 'undefined' && window.AndroidLocation !== null) {
                        try {
                            lat = window.AndroidLocation.getLatitude();
                            lng = window.AndroidLocation.getLongitude();
                            console.log('Location Guard: Fake position:', lat, lng);
                            showLocationToastOnce('虚拟位置: ' + lat + ', ' + lng);
                        } catch (e) {
                            console.error('Location Guard: Error getting fake location:', e.message);
                        }
                    }

                    return createPosition(lat, lng);
                }

                navigator.geolocation.getCurrentPosition = function(success, error, options) {
                    console.log('Location Guard: getCurrentPosition called');

                    if (typeof window.AndroidLocation !== 'undefined' && window.AndroidLocation !== null) {
                        try {
                            var useVirtual = window.AndroidLocation.getUseVirtualLocation();
                            console.log('Location Guard: useVirtual =', useVirtual);

                            if (useVirtual) {
                                console.log('Location Guard: Using FAKE location');
                                var position = getFakePosition();
                                if (typeof success === 'function') {
                                    success(position);
                                }
                                return;
                            } else {
                                console.log('Location Guard: Using REAL location');
                                showLocationToastOnce('使用真实位置');
                                if (typeof originalGetCurrentPosition === 'function') {
                                    originalGetCurrentPosition.call(navigator.geolocation, success, error, options);
                                } else {
                                    console.log('Location Guard: Original getCurrentPosition not available');
                                    var position = createPosition(39.9042, 116.4074);
                                    if (typeof success === 'function') {
                                        success(position);
                                    }
                                }
                                return;
                            }
                        } catch (e) {
                            console.error('Location Guard: Error:', e.message);
                        }
                    }

                    console.log('Location Guard: AndroidLocation not available, using fake');
                    var position = getFakePosition();
                    if (typeof success === 'function') {
                        success(position);
                    }
                };

                navigator.geolocation.watchPosition = function(success, error, options) {
                    console.log('Location Guard: watchPosition called');

                    if (typeof window.AndroidLocation !== 'undefined' && window.AndroidLocation !== null) {
                        try {
                            var useVirtual = window.AndroidLocation.getUseVirtualLocation();
                            console.log('Location Guard: useVirtual =', useVirtual);

                            if (useVirtual) {
                                console.log('Location Guard: Using FAKE location for watch');
                                var position = getFakePosition();
                                if (typeof success === 'function') {
                                    success(position);
                                }
                                return Math.floor(Math.random() * 1000000);
                            } else {
                                console.log('Location Guard: Using REAL location for watch');
                                showLocationToastOnce('使用真实位置');
                                if (typeof originalWatchPosition === 'function') {
                                    return originalWatchPosition.call(navigator.geolocation, success, error, options);
                                } else {
                                    console.log('Location Guard: Original watchPosition not available');
                                    var position = createPosition(39.9042, 116.4074);
                                    if (typeof success === 'function') {
                                        success(position);
                                    }
                                    return Math.floor(Math.random() * 1000000);
                                }
                            }
                        } catch (e) {
                            console.error('Location Guard: Error:', e.message);
                        }
                    }

                    console.log('Location Guard: AndroidLocation not available, using fake');
                    var position = getFakePosition();
                    if (typeof success === 'function') {
                        success(position);
                    }
                    return Math.floor(Math.random() * 1000000);
                };

                navigator.geolocation.clearWatch = function(watchId) {
                    console.log('Location Guard: clearWatch called for id:', watchId);
                    if (typeof originalClearWatch === 'function') {
                        originalClearWatch.call(navigator.geolocation, watchId);
                    }
                };

                if (!navigator.permissions) {
                    navigator.permissions = {
                        query: function(options) {
                            return Promise.resolve({
                                state: 'granted',
                                onchange: null
                            });
                        }
                    };
                    console.log('Location Guard: Added mock permissions API');
                }

                console.log('Location Guard: Initialization complete');
            })();
        """.trimIndent(), null)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeLocationPermissions) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                webView.postDelayed({ webView.reload() }, 500)
            }
        }
    }

    private fun setupSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val surfaceColor = if (Build.VERSION.SDK_INT >= 31) {
            ContextCompat.getColor(this, android.R.color.system_neutral1_100)
        } else {
            ContextCompat.getColor(this, R.color.m3_surface)
        }

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        val isLightBackground = isLightColor(surfaceColor)
        insetsController.isAppearanceLightStatusBars = isLightBackground
        insetsController.isAppearanceLightNavigationBars = isLightBackground

        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun isLightColor(color: Int): Boolean {
        val red = android.graphics.Color.red(color) / 255.0
        val green = android.graphics.Color.green(color) / 255.0
        val blue = android.graphics.Color.blue(color) / 255.0

        val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue

        return luminance > 0.5
    }

    private fun handleBackPress() {
        if (showSettings) {
            showSettings = false
        } else if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Material3App() {
        val context = LocalContext.current

        var showAboutDialog by remember { mutableStateOf(false) }

        var urlList by remember { mutableStateOf<List<UrlItem>>(emptyList()) }
        var uaList by remember { mutableStateOf<List<UserAgentItem>>(emptyList()) }
        var coordList by remember { mutableStateOf<List<CoordinateItem>>(emptyList()) }
        var useVirtualLocation by remember { mutableStateOf(false) }

        var currentUrlName by remember { mutableStateOf("") }
        var currentUaName by remember { mutableStateOf("") }
        var selectedUrlId by remember { mutableStateOf("") }
        var selectedUaId by remember { mutableStateOf("") }
        var selectedCoordId by remember { mutableStateOf("") }
        var versionName by remember {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            mutableStateOf(info.versionName ?: "")
        }

        fun refreshData() {
            urlList = DataManager.getUrlList(context)
            uaList = DataManager.getUserAgentList(context)
            coordList = DataManager.getCoordinateList(context)
            useVirtualLocation = DataManager.getUseVirtualLocation(context)

            val selectedUrl = DataManager.getSelectedUrl(context)
            val selectedUa = DataManager.getSelectedUA(context)
            currentUrlName = selectedUrl?.name ?: "未选择"
            currentUaName = selectedUa?.name ?: "系统默认"
            selectedUrlId = DataManager.getSelectedUrlId(context)
            selectedUaId = DataManager.getSelectedUAId(context)
            selectedCoordId = DataManager.getSelectedCoordId(context)
        }

        LaunchedEffect(Unit) {
            refreshData()
        }

        LaunchedEffect(showSettings) {
            if (!showSettings) {
                refreshData()
                if (::webView.isInitialized) {
                    webView.reload()
                }
            }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            if (showSettings) {
                SettingsScreen(onBack = { showSettings = false })
            } else {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "$currentUrlName-$currentUaName",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    },
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp, 8.dp, 12.dp, 12.dp)
                                .windowInsetsPadding(WindowInsets.navigationBars),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Card(
                                onClick = { showAboutDialog = true },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("关于", fontSize = MaterialTheme.typography.labelMedium.fontSize)
                                }
                            }

                            Card(
                                onClick = { showSettings = true },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("配置", fontSize = MaterialTheme.typography.labelMedium.fontSize)
                                }
                            }

                            Card(
                                onClick = {
                                    refreshData()
                                    showQuickSwitchDialog = true
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("快捷切换", fontSize = MaterialTheme.typography.labelMedium.fontSize)
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        var lastClickTime by remember { mutableLongStateOf(0) }
                        FloatingActionButton(
                            onClick = {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime < 300L) {
                                    if (this@MainComposeActivity::webView.isInitialized) {
                                        val selectedUrl = DataManager.getSelectedUrl(context)
                                        if (selectedUrl != null) {
                                            this@MainComposeActivity.webView.loadUrl(selectedUrl.url)
                                            android.widget.Toast.makeText(context, "已重新加载原始URL", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    if (this@MainComposeActivity::webView.isInitialized) {
                                        this@MainComposeActivity.webView.reload()
                                    }
                                }
                                lastClickTime = currentTime
                            },
                            modifier = Modifier.padding(16.dp).size(56.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "刷新",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End
                ) { innerPadding ->
                    val selectedUrl = DataManager.getSelectedUrl(context)

                    if (selectedUrl == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "请先配置软件(*^_^*)",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "由MXSZTY制作",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .padding(innerPadding)
                                .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        this@MainComposeActivity.webView = this
                                        layoutParams = android.view.ViewGroup.LayoutParams(
                                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        val selectedUa = DataManager.getSelectedUA(context)
                                        setupWebView(this, selectedUa?.userAgent ?: "")
                                        loadUrl(selectedUrl.url)
                                    }
                                },
                                update = { _ -> },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            if (showAboutDialog) {
                AlertDialog(
                    onDismissRequest = { showAboutDialog = false },
                    title = { Text("关于") },
                    text = {
                        Column {
                            Text("WovenLight GV", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                            Text("版本 $versionName")
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                            Text("由MXSZTY制作")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAboutDialog = false }) {
                            Text("确定", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }

            if (showQuickSwitchDialog) {
                AlertDialog(
                    onDismissRequest = { showQuickSwitchDialog = false },
                    title = {
                        Text("快捷切换", style = MaterialTheme.typography.titleLarge)
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                                    Text("URL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (urlList.isEmpty()) {
                                        Text("暂无URL", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            contentPadding = PaddingValues(vertical = 8.dp)
                                        ) {
                                            items(urlList) { item ->
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
                                                        selectedUrlId = item.id
                                                        currentUrlName = item.name
                                                        if (::webView.isInitialized) {
                                                            webView.loadUrl(item.url)
                                                        }
                                                    }
                                                ) {
                                                    Text(
                                                        item.name,
                                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Medium,
                                                        color = if (item.id == selectedUrlId) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                                    Text("User Agent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    LazyColumn(
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            contentPadding = PaddingValues(vertical = 8.dp)
                                        ) {
                                        item {
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
                                                    selectedUaId = ""
                                                    currentUaName = "系统默认"
                                                    if (::webView.isInitialized) {
                                                        webView.settings.userAgentString = WebSettings.getDefaultUserAgent(context)
                                                        webView.reload()
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    "系统默认",
                                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (selectedUaId.isEmpty()) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        items(uaList) { item ->
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
                                                    selectedUaId = item.id
                                                    currentUaName = item.name
                                                    if (::webView.isInitialized) {
                                                        webView.settings.userAgentString = item.userAgent
                                                        webView.reload()
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    item.name,
                                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (item.id == selectedUaId) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
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
                                                if (::webView.isInitialized) {
                                                    webView.reload()
                                                }
                                            }
                                        )
                                    }

                                    if (useVirtualLocation) {
                                        Text("坐标", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                                        if (coordList.isEmpty()) {
                                            Text("暂无坐标", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                                contentPadding = PaddingValues(vertical = 8.dp)
                                            ) {
                                                items(coordList) { item ->
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
                                                            selectedCoordId = item.id
                                                            if (::webView.isInitialized) {
                                                                webView.reload()
                                                            }
                                                        }
                                                    ) {
                                                        Text(
                                                            item.name,
                                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Medium,
                                                            color = if (item.id == selectedCoordId) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showQuickSwitchDialog = false }) {
                            Text("关闭", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}