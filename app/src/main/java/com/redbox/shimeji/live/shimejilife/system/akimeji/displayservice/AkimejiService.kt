package com.redbox.shimeji.live.shimejilife.system.akimeji.displayservice

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.*
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.redbox.shimeji.live.shimejilife.MainActivity
import com.redbox.shimeji.live.shimejilife.R
import com.redbox.shimeji.live.shimejilife.common.constants.AkimejiT2Constants
import com.redbox.shimeji.live.shimejilife.data.repoT2.HelperT2
import com.redbox.shimeji.live.shimejilife.data.repoT2.SpritesServiceAkimejiT2
import com.redbox.shimeji.live.shimejilife.system.akimeji.AkimejiView
import timber.log.Timber
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator

class AkimejiService : Service() {

    val helper: HelperT2 by lazy { ServiceLocator.helperT2 }
    val spritesServiceAkimejiT2: SpritesServiceAkimejiT2 by lazy { ServiceLocator.spritesServiceAkimejiT2 }

    internal var isShimejiVisible = true

    lateinit var mWindowManager: WindowManager

    private var prefListener: PreferenceChangeListener? = null
    private var prefs: SharedPreferences? = null

    internal val shimejiViews = ArrayList<AkimejiView>(12)

    internal val viewParams = HashMap<Int, WindowManager.LayoutParams>(12)


    private var screenStatusReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val kgMgr = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val action = intent.action
            when {
                "android.intent.action.SCREEN_OFF" == action -> {
                    this@AkimejiService.onScreenOff()
                }
                kgMgr.isKeyguardLocked -> {
                    setForegroundNotification(true, true)
                }
                "android.intent.action.USER_PRESENT" == action -> {
                    if (isShimejiVisible) {
                        setForegroundNotification(true)
                        this@AkimejiService.onScreenOn()
                    } else {
                        setForegroundNotification(false)
                    }
                }
            }
        }
    }


    fun add(mascotView: AkimejiView) {
        handler.sendEmptyMessage(1)
        mascotView.resumeAnimation()
    }


    fun remove(mascotView: AkimejiView?) {
        //Timber.e("remove1")
        if (mascotView != null && mascotView.isShown) {
            //handler.removeMessages(1)
            //Timber.e("remove2")
            mascotView.pauseAnimation()
            mascotView.isHidden = true
            mWindowManager.removeViewImmediate(mascotView)
        }
    }


    private inner class PreferenceChangeListener internal constructor() :
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
            if (isShimejiVisible && (key == AkimejiT2Constants.ACTIVE_SHIMEJI_IDS || key == AkimejiT2Constants.SIZE_MULTIPLIER)
            ) {
                removeMascotViews()
                loadMascotViews()
            } else if (key == AkimejiT2Constants.ANIMATION_SPEED) {
                val speed = helper.getSpeedMultiplier(baseContext)
                for (view in shimejiViews) {
                    view.setSpeedMultiplier(speed)
                }

            } else if (key == AkimejiT2Constants.SHOW_NOTIFICATION) {
                if (helper.getNotificationVisibility(baseContext)) {
                    setForegroundNotification(isShimejiVisible)
                    return
                }
                if (!isShimejiVisible) {
                    toggleShimejiStatus()
                }
                stopForeground(true)
            } else if (key == AkimejiT2Constants.REAPPEAR_DELAY) {
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
            Timber.e("AkimejiServiceT2 onCreate")
            mWindowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
            handler = Handler(Handler.Callback {
                onHandleMessage(it)
            })
            prefs = getSharedPreferences((AkimejiT2Constants.MY_PREFS), Service.MODE_MULTI_PROCESS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent == null || ACTION_TOGGLE != intent.action) {
            setForegroundNotification(isShimejiVisible)
            loadMascotViews()
            this.prefListener = PreferenceChangeListener()
            this.prefs!!.registerOnSharedPreferenceChangeListener(this.prefListener)
            registerReceiver(
                this.screenStatusReceiver,
                IntentFilter("android.intent.action.SCREEN_OFF")
            )
            registerReceiver(
                this.screenStatusReceiver,
                IntentFilter("android.intent.action.SCREEN_ON")
            )
            registerReceiver(
                this.screenStatusReceiver,
                IntentFilter("android.intent.action.USER_PRESENT")
            )
        } else if (ACTION_TOGGLE == intent.action) {
            toggleShimejiStatus()
        }
        return Service.START_STICKY
    }

    internal fun toggleShimejiStatus() {
        this.isShimejiVisible = !this.isShimejiVisible
        setForegroundNotification(isShimejiVisible)
        for (mascotView: AkimejiView in shimejiViews) {
            if (isShimejiVisible) {
                add(mascotView)
            } else {
                mascotView.cancelReappearTimer()
                remove(mascotView)
            }
        }
        if (isShimejiVisible) {
            handler.sendEmptyMessage(1)
        }
    }

    fun loadMascotViews() {
        try {
            lateinit var params: WindowManager.LayoutParams
            val mascots: List<Int> = helper.getActiveTeamMembers()
            spritesServiceAkimejiT2.loadSpritesForMascots(mascots)
            loop@ for (intValue in mascots) {
                if (intValue != -1) {
                    params = WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        520,
                        PixelFormat.TRANSLUCENT
                    )

                    params.gravity = Gravity.START or Gravity.TOP
                    val view = AkimejiView(this, intValue)
                    view.setMascotEventsListener(object : AkimejiView.MascotEventNotifier {
                        override fun hideMascot() {
                            this@AkimejiService.remove(view)
                        }

                        override fun showMascot() {
                            this@AkimejiService.add(view)
                        }
                    })
                    this.shimejiViews.add(view)
                    params.width = (view.width1 ?: return)
                    params.height = (view.height1 ?: return)

                    viewParams[view.getUniqueId().toInt()] = params
                    mWindowManager.addView(view, params)
                }
            }
            handler.sendEmptyMessage(1)
        } catch (e: Throwable) {
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
        Timber.e("removeMascotViews")
        shimejiViews.clear()
        viewParams.clear()
    }

    private fun onHandleMessage(msg: Message): Boolean {
        if (msg.what != 1) {
            return false
        }
        this.handler.removeMessages(1)
        for (view in shimejiViews) {
            try {
                val params = viewParams[view.getUniqueId().toInt()]
                if (params != null) {
                    // Keep window sized to current frame, and move it to logical x/y
                    view.height1?.let { params.height = it }
                    params.width = view.width1
                    params.x = view.x.toInt()
                    params.y = view.y.toInt()
                    if (view.parent == null) {
                        mWindowManager.addView(view, params)
                        view.isHidden = false
                    } else {
                        mWindowManager.updateViewLayout(view, params)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        handler.sendEmptyMessageDelayed(1, 16)
        return true
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(this.screenStatusReceiver)
        } catch (e: Exception) {
        }
        stopForeground(true)
        removeMascotViews()
        super.onDestroy()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    internal fun setForegroundNotification(show: Boolean, force: Boolean = false) {
        try {
            if (!show) {
                stopForeground(true)
                return
            }
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
            val channelId = "akimeji_service"
            val channelName = "Akimeji Service"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
                chan.lightColor = Color.BLUE
                chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                service.createNotificationChannel(chan)
                val notification = Notification.Builder(this, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build()
                startForeground(124, notification)
            } else {
                val notification = Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build()
                startForeground(124, notification)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    internal fun onScreenOff() {
        try {
            for (view in shimejiViews) {
                view.pauseAnimation()
            }
            setForegroundNotification(true, true)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    internal fun onScreenOn() {
        try {
            for (view in shimejiViews) {
                view.resumeAnimation()
            }
        } catch (e: Throwable) {
        }
    }

    companion object {
        const val ACTION_TOGGLE = "com.redbox.shimeji.live.shimejilife.TOGGLE_SHIMEJI"
    }
}