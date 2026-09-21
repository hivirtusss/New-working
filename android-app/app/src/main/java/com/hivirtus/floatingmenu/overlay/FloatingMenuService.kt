package com.hivirtus.floatingmenu.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.data.AppState
import com.hivirtus.floatingmenu.data.MenuConfig
import com.hivirtus.floatingmenu.util.FileHelper

class FloatingMenuService : Service() {
    private lateinit var windowManager: WindowManager
    private var bubbleView: View? = null
    private var panelView: View? = null
    private var config = MenuConfig.load()
    private var bubbleParams: WindowManager.LayoutParams? = null
    private var panelParams: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        config.menuOpen = true
        startAsForeground()
        showBubble()
        showPanel()
    }

    override fun onDestroy() {
        config.menuOpen = false
        config.save()
        FileHelper.writeRootFile(config.toIniString(), MenuConfig.CONFIG_PATH)
        removeViews()
        super.onDestroy()
    }

    private fun startAsForeground() {
        val channelId = "floating_menu"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Floating Menu", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.overlay_running))
            .setSmallIcon(R.drawable.virtus_bubble)
            .build()
        startForeground(101, notification)
    }

    private fun layoutFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun showBubble() {
        if (bubbleView != null) return
        val view = LayoutInflater.from(this).inflate(R.layout.overlay_bubble, null)
        val bubble = view.findViewById<ImageView>(R.id.bubbleButton)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = config.menuX
        params.y = config.menuY

        bubble.setOnClickListener {
            config.menuOpen = !config.menuOpen
            if (config.menuOpen) showPanel() else hidePanel()
            persist()
        }

        view.setOnTouchListener(DragTouchListener(params) {
            config.menuX = params.x
            config.menuY = params.y
            panelParams?.let {
                it.x = params.x
                it.y = params.y + 120
                panelView?.let { panel -> windowManager.updateViewLayout(panel, it) }
            }
            persist()
        })

        windowManager.addView(view, params)
        bubbleView = view
        bubbleParams = params
    }

    private fun showPanel() {
        if (panelView != null) {
            panelView?.visibility = View.VISIBLE
            return
        }
        val view = LayoutInflater.from(this).inflate(R.layout.overlay_menu_panel, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = config.menuX
        params.y = config.menuY + 120

        bindPanelToggles(view)
        view.findViewById<TextView>(R.id.closePanel).setOnClickListener {
            config.menuOpen = false
            hidePanel()
            persist()
        }

        windowManager.addView(view, params)
        panelView = view
        panelParams = params
    }

    private fun hidePanel() {
        panelView?.let { windowManager.removeView(it) }
        panelView = null
        panelParams = null
    }

    private fun bindPanelToggles(root: View) {
        val map = mapOf(
            R.id.toggle_esp_box to { v: Boolean ->
                config.toggleEsp = v
                AppState.enableVisual = v
            },
            R.id.toggle_esp_line to { v: Boolean -> config.espLine = v },
            R.id.toggle_esp_distance to { v: Boolean -> config.espDistance = v },
            R.id.toggle_aim_assist to { v: Boolean -> config.aimAssist = v },
            R.id.toggle_recoil to { v: Boolean -> config.recoilControl = v },
            R.id.toggle_hide_esp to { v: Boolean ->
                config.hideEsp = v
                AppState.hideEsp = v
            }
        )
        map.forEach { (id, apply) ->
            val box = root.findViewById<CheckBox>(id)
            when (id) {
                R.id.toggle_esp_box -> box.isChecked = config.toggleEsp
                R.id.toggle_esp_line -> box.isChecked = config.espLine
                R.id.toggle_esp_distance -> box.isChecked = config.espDistance
                R.id.toggle_aim_assist -> box.isChecked = config.aimAssist
                R.id.toggle_recoil -> box.isChecked = config.recoilControl
                R.id.toggle_hide_esp -> box.isChecked = config.hideEsp
            }
            box.setOnCheckedChangeListener { _, checked ->
                apply(checked)
                persist()
            }
        }
    }

    private fun persist() {
        config.save()
        FileHelper.writeRootFile(config.toIniString(), MenuConfig.CONFIG_PATH)
    }

    private fun removeViews() {
        bubbleView?.let { windowManager.removeView(it) }
        panelView?.let { windowManager.removeView(it) }
        bubbleView = null
        panelView = null
    }

    private class DragTouchListener(
        private val params: WindowManager.LayoutParams,
        private val onMoved: () -> Unit
    ) : View.OnTouchListener {
        private var lastX = 0
        private var lastY = 0
        private var dragging = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                    dragging = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX.toInt() - lastX
                    val dy = event.rawY.toInt() - lastY
                    if (dx != 0 || dy != 0) dragging = true
                    params.x += dx
                    params.y += dy
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                    (v.context.getSystemService(WINDOW_SERVICE) as WindowManager).updateViewLayout(v, params)
                    onMoved()
                }
                MotionEvent.ACTION_UP -> if (dragging) return true
            }
            return false
        }
    }
}
