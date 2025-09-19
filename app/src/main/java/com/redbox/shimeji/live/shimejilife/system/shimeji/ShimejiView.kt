package com.redbox.shimeji.live.shimejilife.system.shimeji

import android.content.Context
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Scroller
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.timerTask


//@Suppress("DEPRECATION")
class ShimejiView(internal val mContext: Context, var mascotId: Int, var paidenable: Boolean, var flinging: Boolean) :
    SurfaceView(mContext), SurfaceHolder.Callback {

    // Use fully-qualified ServiceLocator references
    val spritesService = com.redbox.shimeji.live.shimejilife.di.ServiceLocator.spritesService
    val helper = com.redbox.shimeji.live.shimejilife.di.ServiceLocator.helper

    var isHidden = false

    var eventNotifier: MascotEventNotifier? = null

    interface MascotEventNotifier {
        fun hideMascot()
        fun showMascot()
    }

    fun setMascotEventsListener(mascotEventNotifier: MascotEventNotifier) {
        this.eventNotifier = mascotEventNotifier
    }

    var timer = Timer()
    var timerTask: TimerTask? = null
    fun cancelReappearTimer() {
        timerTask?.cancel()
    }

    internal val drawRunner = Runnable {
        try {
            draw()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    //https://www.baeldung.com/kotlin-jvm-synthetic
    internal var flipHorizontalMatrix = Matrix()
    internal val gestureDetector: GestureDetector
    internal var gestureListener: OnGestureListener = object : SimpleOnGestureListener() {
        internal var initialX: Int = 0
        internal var initialY: Int = 0

        override fun onDoubleTap(e: MotionEvent): Boolean {
            try {
                if (eventNotifier == null) {
                    return true
                }
                eventNotifier?.hideMascot()
                val unused: TimerTask = timerTask {
                    run {
                        eventNotifier?.showMascot()
                    }
                }
                if (helper.getReappearDelayMs(mContext).toLong() >= 1) {
                    timer.schedule(unused, helper.getReappearDelayMs(mContext).toLong())
                } else {
                    timer.schedule(unused, 60000L)//1 minute
                }
            } catch (e: Exception) {
                return true
            }
            return true
            //return super.onDoubleTap(e)
        }

        // esto podria solucionar el problema de getx null error https://stackoverflow.com/questions/4151385/android-simpleongesturelistener-onfling-getting-a-null-motionevent
        // lanzarlo en el canal beta
        // otra alternativa: https://stackoverflow.com/questions/17390873/onfling-motionevent-e1-null
        override fun onDown(e: MotionEvent): Boolean {
            this.initialX = shimeji.x
            this.initialY = shimeji.y
            /*toast.setGravity(Gravity.getAbsoluteGravity(1,2), 3, 5);
            toast.show();*/
            return false
        }

        /*override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            // Log.e(Logger.TAG, "Single tap on shimeji mascotId.id: " + mascotId.id + " mascotId.uniqueId: "+ mascotId.uniqueId)
            return super.onSingleTapConfirmed(e)
        }*/

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //if (Animation.paidEnabled or flinging) {
            shimeji.setFlingSpeed(velocityX.toInt(), velocityY.toInt())
            scroller.fling(
                shimeji.x,
                shimeji.y, velocityX.toInt(), velocityY.toInt(),
                playground.left - 100,
                playground.right + 100,
                playground.top - 100,
                playground.bottom + 100
            )
            //Log.v(Logger.TAG, "Fling on shimeji: " + this@ShimejiView.mascotId)
            //}
            return true
        }

        //e1 will always be the initial motion event for a touch event (The ACTION_DOWN event). e2 is the current motion event.
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            drag(
                this.initialX + (e2.rawX - e1!!.rawX).toInt(),
                this.initialY + (e2.rawY - e1!!.rawY).toInt()
            )
            return false
        }
    }

    internal val handler1 = Handler()
    var height1: Int?
    internal val id = NEXT_ID.getAndIncrement()
    internal var isVisible = true
    internal var shimeji: Shimeji
    internal val paint: Paint
    internal var playground: Playground
    internal val scroller: Scroller
    internal val sizeMultiplier: Double

    // private val sizeMultiplier: Double
    internal val speedMultiplier: Double

    //internal val spritesService: SpritesService
    var width1: Int


    init {
        //this.spritesService = spritesService
        this.paint = Paint()
        this.paint.isAntiAlias = true
        setBackgroundColor(0)
        setZOrderOnTop(true)
        this.speedMultiplier = helper.getSpeedMultiplier(this.mContext)
        this.sizeMultiplier = helper.getSizeMultiplier(this.mContext)
        spritesService.setSizeMultiplier(this.sizeMultiplier, mascotId)
        val frames = this.spritesService.getSpritesById(mascotId)
        this.height1 = frames.height
        this.width1 = frames.width


        this.shimeji = Shimeji(mascotId, paidenable, flinging)
        /*if (mascotId == 0) {
            Timber.e("Un akimeji: $mascotId")
            Animation.flingEnabled = true
            Animation.paidEnabled = true
        } else {
            Animation.flingEnabled = false
            Animation.paidEnabled = false
        }*/
        this.playground = Playground(this.mContext, false)
        this.shimeji.initialize(frames, this.speedMultiplier, this.playground)
        this.shimeji.startAnimation()
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
        this.gestureDetector = GestureDetector(this.mContext, this.gestureListener)
        this.scroller = Scroller(this.mContext)
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }


    fun pauseAnimation() {
        this.isVisible = false
    }

    fun resumeAnimation() {
        Timber.e("isvisible: true")
        this.isVisible = true
    }

    fun setSpeedMultiplier(speedMultiplier: Double) {
        this.shimeji.setSpeedMultiplier(speedMultiplier)
    }

    fun endDrag() {
        this.shimeji.endDragging()
    }

    fun drag(x: Int, y: Int) {
        this.shimeji.drag(x, y)
    }

    //invocado desde service cuando la pantalla rota
    fun notifyLayoutChange(context: Context) {
        this.playground = Playground(context, false)
        shimeji.resetEnvironmentVariables(this.playground)
    }

    override fun getX(): Float {
        try {
            if (this.shimeji.isBeingFlung) {
                if (this.scroller.computeScrollOffset()) {
                    this.shimeji.fling(this.scroller.currX, this.scroller.currY)
                } else {
                    this.shimeji.endFlinging()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this.shimeji.x.toFloat()
    }

    override fun getY(): Float {
        return this.shimeji.y.toFloat()
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            this.handler1.post(this.drawRunner)
        } catch (e: Exception) {
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // surfaceChanged: -2 256 256 to surfaceChanged: -2 256 540 (img con globo)
        // Log.e(Logger.TAG, "surfaceChanged: " + format+" "+width1+" "+height1);
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        try {
            this.handler1.removeCallbacks(this.drawRunner)
        } catch (e: Exception) {
        }
    }

    fun getUniqueId(): Long {
        return id
    }

    fun draw() {
        if (this.isVisible) {
            val canvas = holder.lockCanvas()
            if (canvas != null) {
                canvas.drawColor(0, Mode.CLEAR)
                val mascotBitmap = this.shimeji.frameBitmap
                this.height1 = (mascotBitmap ?: return).height
                this.width1 = mascotBitmap.width
                if (this.shimeji.isFacingLeft) {
                    canvas.drawBitmap(mascotBitmap, 0.0f, 0.0f, this.paint)
                } else {
                    this.flipHorizontalMatrix.setScale(-1.0f, 1.0f)
                    this.width1.toFloat()
                        .let { this.flipHorizontalMatrix.postTranslate(it, 0.0f) }
                    canvas.drawBitmap(mascotBitmap, this.flipHorizontalMatrix, this.paint)
                }
                holder.unlockCanvasAndPost(canvas)
            }
        }
        this.handler1.removeCallbacks(this.drawRunner)
        this.handler1.postDelayed(this.drawRunner, 16)
    }

    // sirve para desabilitar el touch o el drag del shimeji y activar otros eventos
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        super.dispatchTouchEvent(ev)
        try {
            performClick()
            if (ev.action == MotionEvent.ACTION_UP /*cuando suelto el dedo */) {
                endDrag()
            }
        } catch (e: Exception) {
            return false
        }
        return this.gestureDetector.onTouchEvent(ev)
    }

    companion object {
        internal val NEXT_ID = AtomicLong(0)
    }


}
