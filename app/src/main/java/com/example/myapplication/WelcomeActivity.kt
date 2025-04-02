package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton

class WelcomeActivity : ComponentActivity() {
    
    private lateinit var modeRadioGroup: RadioGroup
    private lateinit var difficultyRadioGroup: RadioGroup
    private lateinit var boardSizeRadioGroup: RadioGroup
    private lateinit var boardColorRadioGroup: RadioGroup
    private lateinit var customColorButton: ImageButton
    private lateinit var startGameButton: MaterialButton
    
    // 游戏选项
    private var gameMode = 0
    private var difficulty = 1
    private var boardSizeRows = 15
    private var boardSizeCols = 15
    private var boardColor = "#E3B448" // 默认颜色
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        // 设置沉浸式状态栏（使用新API替换已弃用的API）
        setupImmersiveMode()
        
        // 初始化视图
        modeRadioGroup = findViewById(R.id.mode_radio_group)
        difficultyRadioGroup = findViewById(R.id.difficulty_radio_group)
        boardSizeRadioGroup = findViewById(R.id.board_size_radio_group)
        boardColorRadioGroup = findViewById(R.id.board_color_radio_group)
        customColorButton = findViewById(R.id.custom_color_button)
        startGameButton = findViewById(R.id.start_game_button)
        
        // 更新颜色按钮初始颜色
        updateColorButtonBackground(boardColor)
        
        // 设置游戏模式选择监听
        modeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            gameMode = when (checkedId) {
                R.id.mode_two_player -> 0
                R.id.mode_player_first -> 1
                R.id.mode_ai_first -> 2
                else -> 0
            }
        }
        
        // 设置难度选择监听
        difficultyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            difficulty = when (checkedId) {
                R.id.difficulty_easy -> 0
                R.id.difficulty_medium -> 1
                R.id.difficulty_hard -> 2
                else -> 1
            }
        }
        
        // 设置棋盘大小选择监听
        boardSizeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.size_15 -> {
                    boardSizeRows = 15
                    boardSizeCols = 15
                }
                R.id.size_20 -> {
                    boardSizeRows = 20
                    boardSizeCols = 20
                }
                R.id.size_30 -> {
                    boardSizeRows = 30
                    boardSizeCols = 30
                }
                R.id.size_20_40 -> {
                    boardSizeRows = 40
                    boardSizeCols = 20
                }
            }
        }
        
        // 设置棋盘颜色选择监听
        boardColorRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            boardColor = when (checkedId) {
                R.id.color_default -> "#E3B448" // 默认木色
                R.id.color_green -> "#4CAF50"    // 绿色
                else -> boardColor // 保持自定义颜色不变
            }
            updateColorButtonBackground(boardColor)
        }
        
        // 设置自定义颜色按钮点击事件
        customColorButton.setOnClickListener {
            showColorPickerDialog()
        }
        
        // 设置开始游戏按钮点击事件
        startGameButton.setOnClickListener {
            startGame()
        }
        
        // 默认设置
        modeRadioGroup.check(R.id.mode_two_player)
        difficultyRadioGroup.check(R.id.difficulty_medium)
        boardSizeRadioGroup.check(R.id.size_15)
        boardColorRadioGroup.check(R.id.color_default)
    }
    
    private fun setupImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上版本使用新API
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // 已弃用的方法调用，使用上面的WindowCompat替代
            // window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // 针对Android 11以下版本使用旧API
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
    
    private fun updateColorButtonBackground(colorHex: String) {
        try {
            val drawable = customColorButton.background as GradientDrawable
            drawable.setColor(Color.parseColor(colorHex))
        } catch (e: Exception) {
            // 如果出错，使用默认颜色
            Toast.makeText(this, "颜色设置出错，使用默认颜色", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showColorPickerDialog() {
        // 这里使用一个简单的颜色选择对话框
        // 实际应用中可以使用更丰富的颜色选择器库
        val colors = arrayOf(
            "#E3B448", "#4CAF50", "#2196F3", "#FF5722", 
            "#9C27B0", "#F44336", "#3F51B5", "#795548"
        )
        val colorNames = arrayOf(
            "温润原木", "护眼绿", "湖水蓝", "暖阳橙", 
            "典雅紫", "中国红", "深邃蓝", "古朴棕"
        )
        
        AlertDialog.Builder(this)
            .setTitle("选择棋盘颜色")
            .setItems(colorNames) { _, which ->
                boardColor = colors[which]
                // 选择自定义颜色后，选中自定义选项
                boardColorRadioGroup.check(R.id.color_custom)
                // 更新自定义颜色按钮的背景色
                updateColorButtonBackground(boardColor)
            }
            .show()
    }
    
    private fun startGame() {
        // 创建Intent，传递游戏模式和难度参数到游戏页面
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("GAME_MODE", gameMode)
            putExtra("DIFFICULTY", difficulty)
            putExtra("BOARD_SIZE_ROWS", boardSizeRows)
            putExtra("BOARD_SIZE_COLS", boardSizeCols)
            putExtra("BOARD_COLOR", boardColor)
        }
        startActivity(intent)
    }
}