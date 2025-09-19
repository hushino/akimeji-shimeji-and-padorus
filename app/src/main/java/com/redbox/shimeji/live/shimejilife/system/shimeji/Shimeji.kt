package com.redbox.shimeji.live.shimejilife.system.shimeji

import android.graphics.Bitmap
import com.redbox.shimeji.live.shimejilife.system.shimeji.animations.Animation
import com.redbox.shimeji.live.shimejilife.system.shimeji.animations.Dragging
import com.redbox.shimeji.live.shimejilife.system.shimeji.animations.Falling
import com.redbox.shimeji.live.shimejilife.system.shimeji.animations.Flinging
import timber.log.Timber
import java.util.concurrent.ThreadLocalRandom

class Shimeji(var shimejiId: Int, var paidenable: Boolean, var flinging: Boolean) {
    //@Volatile
    var isFacingLeft = false
    var name: String? = null
    private lateinit var animation: Animation
    private var dx = 0
    private var dy = 0
    private var flingVelocityX = 0
    private var flingVelocityY = 0
    private lateinit var frames: Sprites
    internal var height = 0
    private var isBeingDragged = false
    var isBeingFlung = false
        private set
    private lateinit var margins: Playground
    private var speedMultiplier = 0.0
    private lateinit var thread: MascotThread

    //private val touch = MotionAttributes()
    internal var width = 0
    private var xOffset = 0
    var x: Int
        get() = try {
            dx
        } catch (e: NullPointerException) {
            500.also { dx = it }
        }
        private set(x) {
            dx = when {
                x < margins.left -> {
                    margins.left
                }
                x > margins.right - width -> {
                    margins.right - width
                }
                else -> {
                    x
                }
            }
        }//Log.e(Logger.TAG, "getY: " + this.dy);//solo se le ve hasta la mitad si quitamos la resta

    // sumar es ir abajo
    // lo que retorna es el recorrido actual en el eje y
    // increiblemente retorna cada posicion de cada shimeji.
    // pero no sabemos a que mascota pertenece cada uno.
    var y: Int
        get() = try {
            //Log.e(Logger.TAG, "getY: " + this.dy);
            dy
        } catch (e: NullPointerException) {
            500.also { dy = it }
        }
        private set(y) {
            dy = when {
                y > margins.bottom - height -> { //solo se le ve hasta la mitad si quitamos la resta
                    margins.bottom - height
                }
                y < margins.top -> {
                    margins.top
                }
                else -> {
                    y
                }
            }
        }

    val frameBitmap: Bitmap?
        get() = frames[animation.spriteIdentifier]

    fun startAnimation() {
        thread = MascotThread()
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
    }

    //aqui se inicia y es donde se empieza a caer el shimeji hasta llegar a la parte mas baja de la pantalla
    fun initialize(
        animationFrames: Sprites,
        speedMultiplier: Double,
        space: Playground
    ) {
        loadBitmaps(animationFrames)
        setPlayground(
            Playground(
                space.top - xOffset,
                space.bottom,
                space.left - xOffset,
                space.right + xOffset
            )
        )
        setSpeedMultiplier(speedMultiplier)

        animation = Falling(null, shimejiId, paidenable = paidenable, flinging = flinging)
        //test animation = WalkRight(shimejiId)
        //x = Random().nextInt(margins.right - width)
        // val wit =  ThreadLocalRandom.current().nextInt(width)
        //xOffset

        x = ThreadLocalRandom.current().nextInt(margins.right) - width

    }

    fun setSpeedMultiplier(speedMultiplier: Double) {
        this.speedMultiplier = if (speedMultiplier <= 0.1) 1.0 else 0.9 + speedMultiplier
    }

    fun resetEnvironmentVariables(space: Playground) {
        setPlayground(
            Playground(
                space.top,
                space.bottom /*+300 funca*/,
                space.left - xOffset,
                space.right + xOffset
            )
        )
        animation = Falling(null, shimejiId, paidenable = paidenable, flinging = flinging)
    }

    private fun setPlayground(playground: Playground) {
        margins = playground
    }

/*    private fun moveManually(x: Int, y: Int) {
        this.x = x
        this.y = y
    }*/

    fun updateAnimation() {
        try {
            checkConditions()
            applyVelocityX(((animation.xVelocity?.toDouble() ?: return) * speedMultiplier).toInt())
            applyVelocityY(((animation.yVelocity?.toDouble() ?: return) * speedMultiplier).toInt())
            isFacingLeft = animation.isFacingLeft
            animation = (animation.frameTick() ?: return)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun kill() {
        thread.isRunning = false
        thread.interrupt()
    }

    private fun checkConditions() {
        if (isBeingDragged) {
            if (animation !is Dragging) {
                animation = Dragging(shimejiId, paidenable = paidenable, flinging = flinging)
            }
        } else if (isBeingFlung) {
            if (animation !is Flinging) {
                animation = Flinging(flingVelocityX, shimejiId, paidenable = paidenable, flinging = flinging)
            }
        } else if (animation is Dragging) {
            animation = ((animation as Dragging?) ?: return).drop()
        } else if (animation is Flinging) {
            animation = ((animation as Flinging?) ?: return).drop()
        }
        val animation2 =
            animation
        var z = false
        val z2 = dy <= margins.top
        val z3 = dy + height >= margins.bottom
        val z4 = dx <= margins.left
        if (dx + width >= margins.right) {
            z = true
        }
        animation2.checkBorders(z2, z3, z4, z)
    }

    fun drag(x: Int, y: Int) {
        this.x = x
        this.y = y // sumar es ir abajo
        isBeingDragged = true
    }

    fun setFlingSpeed(velocityX: Int, velocityY: Int) {
        flingVelocityX = velocityX
        flingVelocityY = velocityY
        isBeingFlung = true
    }

    fun fling(x: Int, y: Int) {
        this.x = x
        this.y = y
        isBeingDragged = false
    }

    fun endDragging() {
        isBeingDragged = false
    }

    fun endFlinging() {
        isBeingFlung = false
    }


    // getHeight1()+300 es el piso minimo el shimeji se movera 300px por encima, perfecto para no molestar en la barra inferior
    private fun loadBitmaps(bitmaps: Sprites) {
        frames = bitmaps
        height =
            frames.height // cuando sale una animacion de dialogo de piso sumar +300 getSpriteIdentifier 46
        width = frames.width
        xOffset =
            frames.xOffset //elimina los espacios en blanco entre el shimeji y el borde de la pantalla
        //Log.e(Logger.TAG, "this.frames.getHeight1(): " + this.frames.getHeight1()); //256
    }

    private fun applyVelocityX(increment: Int) {
        x = dx + increment
    }

    private fun applyVelocityY(increment: Int) {
        y = dy + increment
    }

    /*@get:Contract(pure = true) //30
    val animationDelay: Int
        get() = 3*/

    private inner class MascotThread() : Thread() {
        //val random = kotlin.random.Random.nextLong(25,35)
        //val random =  ThreadLocalRandom.current().nextLong(27, 35)
        //kotlin.random.Random.nextLong(27,35)
        //@Volatile
        var isRunning = true
        override fun run() {
            while (isRunning) {
                try {
                    updateAnimation()
                    if (Animation.classicmode) {
                        sleep(30)
                    } else {
                        sleep(16)
                    }
                } catch (e: InterruptedException) {
                    isRunning = false
                }
            }
        }

    }
}

/* fun handleTouchEvent(ev: MotionEvent): Boolean {
       var newPointerIndex = 1
       var pointerIndex: Int
       val x: Float
       val y: Float
       when (ev.actionMasked) {
           0 -> {
               pointerIndex = ev.actionIndex
               x = ev.getX(pointerIndex)
               y = ev.getY(pointerIndex)
               if (ev.x > (dx - 20).toFloat() && ev.y > (dy - 20).toFloat() && ev.x < (dx + width + 20).toFloat() && ev.y < (dy + height + 20).toFloat()) {
                   isBeingDragged = true
                   touch.mLastTouchX = x
                   touch.mLastTouchY = y
                   touch.mActivePointerId = ev.getPointerId(0)
                   break
               }
               if (isBeingDragged) {
                   touch.invalidate()
                   isBeingDragged = false
                   break
               }
           }
           1, 3 -> if (isBeingDragged) {
               touch.invalidate()
               isBeingDragged = false
               break
           }
           2 -> if (isBeingDragged) {
               pointerIndex = ev.findPointerIndex(touch.mActivePointerId)
               if (pointerIndex == -1) {
                   pointerIndex = 0
               }
               x = ev.getX(pointerIndex)
               y = ev.getY(pointerIndex)
               moveManually(
                   (x - touch.mLastTouchX).toInt() + dx,
                   (y - touch.mLastTouchY).toInt() + dy
               )
               touch.mLastTouchX = x
               touch.mLastTouchY = y
               break
           }
           6 -> if (isBeingDragged) {
               isBeingDragged = false
               pointerIndex = ev.actionIndex
               if (ev.getPointerId(pointerIndex) == touch.mActivePointerId) {
                   if (pointerIndex != 0) {
                       newPointerIndex = 0
                   }
                   touch.mLastTouchX = ev.getX(newPointerIndex)
                   touch.mLastTouchY = ev.getY(newPointerIndex)
                   touch.mActivePointerId = ev.getPointerId(newPointerIndex)
                   break
               }
           }
       }
       return isBeingDragged
   }*/
