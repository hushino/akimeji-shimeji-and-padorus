package com.redbox.shimeji.live.shimejilife.system.shimeji.displayservice

import android.app.*
import android.app.Notification.Builder
import android.app.Notification.PRIORITY_LOW
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build.VERSION
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.redbox.shimeji.live.shimejilife.R
import com.redbox.shimeji.live.shimejilife.common.constants.AppConstants
import com.redbox.shimeji.live.shimejilife.data.repoT1.Helper
import com.redbox.shimeji.live.shimejilife.data.repoT1.SpritesService
import com.redbox.shimeji.live.shimejilife.system.shimeji.ShimejiView
import com.redbox.shimeji.live.shimejilife.system.shimeji.ShimejiView.MascotEventNotifier
import com.redbox.shimeji.live.shimejilife.system.shimeji.Sprites
import timber.log.Timber
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator

class ShimejiService : Service() {

    val helper: Helper by lazy { ServiceLocator.helper }
    val spritesService: SpritesService by lazy { ServiceLocator.spritesService }

    internal var isShimejiVisible = true

    //https://developer.android.com/reference/android/view/Display //getRefreshRate()
    lateinit var mWindowManager: WindowManager

    private var prefListener: PreferenceChangeListener? = null
    private var prefs: SharedPreferences? = null

    private var screenStatusReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val kgMgr = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val action = intent.action
            when {
                "android.intent.action.SCREEN_OFF" == action -> {
                    this@ShimejiService.onScreenOff()
                }
                kgMgr.isKeyguardLocked -> {
                    setForegroundNotification(true, true)
                }
                "android.intent.action.USER_PRESENT" == action -> {
                    if (isShimejiVisible) {
                        setForegroundNotification(true)
                        this@ShimejiService.onScreenOn()
                    } else {
                        setForegroundNotification(false)
                    }
                }
            }
        }
    }

    internal val shimejiViews = ArrayList<ShimejiView>(12)

    private val viewParams = HashMap<Int, LayoutParams>(12)

    private inner class PreferenceChangeListener() :
        OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
            if (isShimejiVisible && (key == AppConstants.ACTIVE_SHIMEJI_IDS || key == AppConstants.SIZE_MULTIPLIER)
            ) {
                removeMascotViews()
                loadMascotViews()
            } else if (key == AppConstants.ANIMATION_SPEED) {
                val speed = helper.getSpeedMultiplier(baseContext)
                for (view in shimejiViews) {
                    view.setSpeedMultiplier(speed)
                }
            } else if (key == AppConstants.SHOW_NOTIFICATION) {
                if (helper.getNotificationVisibility(baseContext)) {
                    setForegroundNotification(isShimejiVisible)
                    return
                }
                if (!isShimejiVisible) {
                    toggleShimejiStatus()
                }
                stopForeground(true)
            } else if (key == AppConstants.REAPPEAR_DELAY) {
                for (mascotView2 in shimejiViews) {
                    if (isShimejiVisible) {
                        mascotView2.cancelReappearTimer()
                        mascotView2.resumeAnimation()
                    }
                }
            }

        }
    }

    internal var handler: Handler = Handler()

    override fun onCreate() {
        super.onCreate()
        try {
            mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            handler = Handler(Handler.Callback {
                try {
                    onHandleMessage(it)
                } catch (e: Exception) {
                    return@Callback false
                }
            })
            prefs = getSharedPreferences((AppConstants.MY_PREFS), Context.MODE_MULTI_PROCESS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            val action = intent?.action
            Timber.e("ShimejiService onStartCommand action=%s", action)
            if (action == ACTION_TOGGLE) {
                toggleShimejiStatus()
            } else {
                setForegroundNotification(isShimejiVisible)
                removeMascotViews()
                loadMascotViews()
                if (this.prefs == null) {
                    this.prefListener = PreferenceChangeListener()
                    this.prefs = getSharedPreferences((AppConstants.MY_PREFS), Context.MODE_MULTI_PROCESS)
                    this.prefs!!.registerOnSharedPreferenceChangeListener(this.prefListener)
                }
                registerReceiver(this.screenStatusReceiver, IntentFilter("android.intent.action.SCREEN_OFF"))
                registerReceiver(this.screenStatusReceiver, IntentFilter("android.intent.action.SCREEN_ON"))
                registerReceiver(this.screenStatusReceiver, IntentFilter("android.intent.action.USER_PRESENT"))
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return START_STICKY
    }

    internal fun toggleShimejiStatus() {
        this.isShimejiVisible = !this.isShimejiVisible
        setForegroundNotification(isShimejiVisible)
        for (mascotView: ShimejiView in shimejiViews) {
            if (mascotView.isAttachedToWindow){
                if (isShimejiVisible) {
                    add(mascotView)
                } else {
                    mascotView.cancelReappearTimer()
                    remove(mascotView)
                }
            }
        }
        if (isShimejiVisible) {
            handler.sendEmptyMessage(1)
        }
    }

    fun add(mascotView: ShimejiView) {
        handler.sendEmptyMessage(1)
        mascotView.resumeAnimation()
    }


    fun remove(mascotView: ShimejiView?) {
        if (mascotView != null && mascotView.isShown) {
            //handler.removeMessages(1)
            mascotView.pauseAnimation()
            mascotView.isHidden = true
            mWindowManager.removeViewImmediate(mascotView)
        }
    }

    fun loadMascotViews() {
        try {
            lateinit var params: LayoutParams
            val mascots: List<Int> = helper.getActiveTeamMembers()
            Timber.e("Loading mascots: %s", mascots)
            spritesService.loadSpritesForMascots(mascots)
            loop@ for (intValue in mascots) {
                if (intValue != -1) {
                    val spritesById: Sprites = spritesService.getSpritesById(intValue)
                    if (spritesById == null || spritesById.isEmpty()) {
                        Timber.e("ERROR: Frames for mascot $intValue were empty. Skipping")
                    } else {
                        params = LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.TYPE_APPLICATION_OVERLAY,
                            520,
                            PixelFormat.TRANSLUCENT
                        )

                        params.gravity = Gravity.START or Gravity.TOP
                        var view: ShimejiView? = null

                        view = ShimejiView(this, intValue, paidenable = true, flinging = true)
                        view.setMascotEventsListener(object : MascotEventNotifier {
                            override fun hideMascot() {
                                this@ShimejiService.remove(view)
                            }

                            override fun showMascot() {
                                this@ShimejiService.add(view)
                            }
                        })
                        view.let { this.shimejiViews.add(it) }
                        params.width = view.width1
                        params.height = (view.height1 ?: return)

                        viewParams[view.getUniqueId().toInt()] = params
                        mWindowManager.addView(view, params)
                    }

                }
            }
            handler.sendEmptyMessage(1)


        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    internal fun removeMascotViews() {
        handler.removeMessages(1)
        for (view in shimejiViews) {
            if (view.isShown) {
                view.pauseAnimation()
                mWindowManager.removeViewImmediate(view)
            }
        }
        shimejiViews.clear()
        viewParams.clear()
    }

    private fun onHandleMessage(msg: Message): Boolean {
        if (msg.what != 1) {
            return false
        }
        this.handler.removeMessages(1)
        shimejiViews.forEach { view ->
            if (view.isAttachedToWindow){
                if (!view.isHidden && view.isVisible) {
                    try {
                        //isAttachedToWindow
                        val paramsShimeji = viewParams[view.getUniqueId().toInt()]
                        if (view.parent == null) {
                            try {
                                mWindowManager.addView(view, paramsShimeji)
                                view.isHidden = false
                            } catch (e: Exception) {
                                Timber.e(e)
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
                if (view.isVisible && view.height1 != null) {
                    val params = viewParams[view.getUniqueId().toInt()]
                    if (params != null) {
                        try {
                            params.height = view.height1!!
                            params.width = view.width1
                            params.x = view.x.toInt()
                            params.y = view.y.toInt()
                            mWindowManager.updateViewLayout(view, params)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            }
        }
        handler.sendEmptyMessageDelayed(1, 16)
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            prefs?.unregisterOnSharedPreferenceChangeListener(this.prefListener)
            unregisterReceiver(this.screenStatusReceiver)
        } catch (e: Exception) {
            Timber.e("Prevented unregister Receiver crash")
        }
        removeMascotViews()
    }

    internal fun onScreenOff() {
        handler.removeMessages(1)
        for (v in this.shimejiViews) {
            if (!v.isHidden) {
                v.pauseAnimation()
            }
        }
    }

    internal fun onScreenOn() {
        handler.removeMessages(1)
        for (v in this.shimejiViews) {
            if (!v.isHidden) {
                v.resumeAnimation()
            }
        }
        handler.sendEmptyMessage(1)
    }

    internal fun setForegroundNotification(start: Boolean, islockscreen: Boolean = false) {
        if (helper.getNotificationVisibility(this)) {
            //Title
            var text: CharSequence = if (start) {
                if (islockscreen) {
                    getText(R.string.shimeji_notif_visible_lockscreen)
                } else {
                    getText(R.string.shimeji_notif_visible) //Los Shimejis son visibles
                }
            } else {
                getText(R.string.shimeji_notif_hidden)
            }
            val intent =
                PendingIntent.getService(
                    this,
                    0,
                    Intent(this, ShimejiService::class.java).setAction(ACTION_TOGGLE),
                    FLAG_IMMUTABLE
                )
            var builder = Builder(this)
            builder = builder.setContentTitle(text)
            //Sub-Title
            text = if (start) {
                if (islockscreen) {
                    getText(R.string.shimeji_notif_visible_lockscreen_subtitle)
                } else {
                    getText(R.string.shimeji_notif_disable)
                }
            } else {
                getText(R.string.shimeji_notif_enable)
            }
            val largeIcon = try {
                val d = ResourcesCompat.getDrawable(resources, R.drawable.ic_anise_candy, theme)
                helper.drawableToBitmap(d!!)
            } catch (e: Exception) {
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            }
            val builder2 = builder.setContentText(text).setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_LOW)
                .setColor(Color.RED)
                .setLargeIcon(largeIcon)
                .setOngoing(true).setContentIntent(intent)
            if (VERSION.SDK_INT >= 26) {
                setupNotificationChannel()
                builder2.setChannelId("shimeji_channel").setOngoing(true).setContentIntent(intent)
            }
            val notification = builder2.build()
            if (start) {
                startForeground(99, notification)
                return
            }
            val noti: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            noti.notify(99, notification)

        }
    }

    @RequiresApi(api = 26)
    private fun setupNotificationChannel() {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("shimeji_channel", "Shimeji notifications", NotificationManager.IMPORTANCE_LOW)
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.setShowBadge(false)
        mNotificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        try {
            for (view in shimejiViews) {
                if (!view.isHidden) {
                    view.notifyLayoutChange(this)
                }
            }
        } catch (e: Throwable) {

        }
    }

    companion object {
        const val ACTION_TOGGLE = "com.redbox.shimeji.live.shimejilife.TOGGLE_SHIMEJI"
    }
}
