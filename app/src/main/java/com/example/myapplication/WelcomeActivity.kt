package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
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
import android.util.Log

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
        
        // 设置各个卡片的点击事件，以触发RadioButton选择
        setupCardClickListeners()
        
        // 直接设置每个RadioButton的点击监听器
        setupDirectRadioButtonListeners()
        
        // 设置游戏模式选择监听
        modeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 根据选中的RadioButton设置游戏模式
            gameMode = when (checkedId) {
                R.id.mode_two_player -> 0
                R.id.mode_player_first -> 1
                R.id.mode_ai_first -> 2
                else -> 0
            }
            Log.d("WelcomeActivity", "游戏模式选择: $gameMode (ID: $checkedId)")
            updateModeCardsState(checkedId)
        }
        
        // 设置难度选择监听
        difficultyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 根据选中的RadioButton设置游戏难度
            difficulty = when (checkedId) {
                R.id.difficulty_easy -> 0
                R.id.difficulty_medium -> 1
                R.id.difficulty_hard -> 2
                else -> 1
            }
            Log.d("WelcomeActivity", "难度选择: $difficulty (ID: $checkedId)")
            updateDifficultyCardsState(checkedId)
        }
        
        // 设置棋盘大小选择监听
        boardSizeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 根据选中的RadioButton设置棋盘大小
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
            Log.d("WelcomeActivity", "棋盘大小选择: ${boardSizeRows}x${boardSizeCols} (ID: $checkedId)")
            updateSizeCardsState(checkedId)
        }
        
        // 设置棋盘颜色选择监听
        boardColorRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 根据选中的RadioButton设置棋盘颜色
            boardColor = when (checkedId) {
                R.id.color_default -> "#E3B448" // 默认木色
                R.id.color_green -> "#4CAF50"    // 绿色
                else -> boardColor // 保持自定义颜色不变
            }
            Log.d("WelcomeActivity", "棋盘颜色选择: $boardColor (ID: $checkedId)")
            updateColorButtonBackground(boardColor)
            updateColorCardsState(checkedId)
        }
        
        // 设置自定义颜色按钮点击事件
        customColorButton.setOnClickListener {
            showColorPickerDialog()
        }
        
        // 设置开始游戏按钮点击事件
        startGameButton.setOnClickListener {
            startGame()
        }
        
        // 初始选中默认值并更新所有卡片的状态
        modeRadioGroup.check(R.id.mode_player_first)
        difficultyRadioGroup.check(R.id.difficulty_medium)
        boardSizeRadioGroup.check(R.id.size_15)
        boardColorRadioGroup.check(R.id.color_default)
        
        // 手动更新所有卡片的初始状态
        updateAllCardsInitialState()
    }
    
    private fun updateAllCardsInitialState() {
        // 更新所有卡片的初始状态
        updateModeCardsState(modeRadioGroup.checkedRadioButtonId)
        updateDifficultyCardsState(difficultyRadioGroup.checkedRadioButtonId)
        updateSizeCardsState(boardSizeRadioGroup.checkedRadioButtonId)
        updateColorCardsState(boardColorRadioGroup.checkedRadioButtonId)
    }
    
    private fun updateModeCardsState(checkedId: Int) {
        updateCardState(R.id.card_two_player, R.id.mode_two_player, checkedId)
        updateCardState(R.id.card_player_first, R.id.mode_player_first, checkedId)
        updateCardState(R.id.card_ai_first, R.id.mode_ai_first, checkedId)
    }
    
    private fun updateDifficultyCardsState(checkedId: Int) {
        updateCardState(R.id.card_easy, R.id.difficulty_easy, checkedId)
        updateCardState(R.id.card_medium, R.id.difficulty_medium, checkedId)
        updateCardState(R.id.card_hard, R.id.difficulty_hard, checkedId)
    }
    
    private fun updateSizeCardsState(checkedId: Int) {
        updateCardState(R.id.card_size_15, R.id.size_15, checkedId)
        updateCardState(R.id.card_size_20, R.id.size_20, checkedId)
        updateCardState(R.id.card_size_30, R.id.size_30, checkedId)
        updateCardState(R.id.card_size_20_40, R.id.size_20_40, checkedId)
    }
    
    private fun updateColorCardsState(checkedId: Int) {
        updateCardState(R.id.card_color_default, R.id.color_default, checkedId)
        updateCardState(R.id.card_color_green, R.id.color_green, checkedId)
        updateCardState(R.id.card_color_custom, R.id.color_custom, checkedId)
    }
    
    private fun updateCardState(cardId: Int, radioButtonId: Int, checkedId: Int) {
        val cardView = findViewById<com.google.android.material.card.MaterialCardView>(cardId) ?: return
        updateCardAppearance(cardView, radioButtonId == checkedId)
    }
    
    private fun setupCardClickListeners() {
        // 游戏模式卡片
        setupCardForRadioButton(R.id.card_two_player, R.id.mode_two_player)
        setupCardForRadioButton(R.id.card_player_first, R.id.mode_player_first)
        setupCardForRadioButton(R.id.card_ai_first, R.id.mode_ai_first)
        
        // 难度级别卡片
        setupCardForRadioButton(R.id.card_easy, R.id.difficulty_easy)
        setupCardForRadioButton(R.id.card_medium, R.id.difficulty_medium)
        setupCardForRadioButton(R.id.card_hard, R.id.difficulty_hard)
        
        // 棋盘大小卡片
        setupCardForRadioButton(R.id.card_size_15, R.id.size_15)
        setupCardForRadioButton(R.id.card_size_20, R.id.size_20)
        setupCardForRadioButton(R.id.card_size_30, R.id.size_30)
        setupCardForRadioButton(R.id.card_size_20_40, R.id.size_20_40)
        
        // 棋盘颜色卡片
        setupCardForRadioButton(R.id.card_color_default, R.id.color_default)
        setupCardForRadioButton(R.id.card_color_green, R.id.color_green)
        setupCardForRadioButton(R.id.card_color_custom, R.id.color_custom)
    }
    
    private fun setupCardForRadioButton(cardId: Int, radioButtonId: Int) {
        val cardView = findViewById<com.google.android.material.card.MaterialCardView>(cardId)
        val radioButton = findViewById<RadioButton>(radioButtonId)
        
        if (cardView == null) {
            Log.e("WelcomeActivity", "找不到卡片视图: ${resources.getResourceEntryName(cardId)}")
            return
        }
        
        if (radioButton == null) {
            Log.e("WelcomeActivity", "找不到单选按钮: ${resources.getResourceEntryName(radioButtonId)}")
            return
        }
        
        // 设置点击事件
        cardView.setOnClickListener {
            // 获取对应的RadioGroup
            val radioGroup = when (radioButtonId) {
                R.id.mode_two_player, R.id.mode_player_first, R.id.mode_ai_first -> modeRadioGroup
                R.id.difficulty_easy, R.id.difficulty_medium, R.id.difficulty_hard -> difficultyRadioGroup
                R.id.size_15, R.id.size_20, R.id.size_30, R.id.size_20_40 -> boardSizeRadioGroup
                R.id.color_default, R.id.color_green, R.id.color_custom -> boardColorRadioGroup
                else -> null
            }
            
            // 直接在这里更新游戏模式、难度或棋盘大小
            when (radioButtonId) {
                // 游戏模式
                R.id.mode_two_player -> gameMode = 0
                R.id.mode_player_first -> gameMode = 1
                R.id.mode_ai_first -> gameMode = 2
                
                // 难度级别
                R.id.difficulty_easy -> difficulty = 0
                R.id.difficulty_medium -> difficulty = 1
                R.id.difficulty_hard -> difficulty = 2
                
                // 棋盘大小
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
                
                // 棋盘颜色
                R.id.color_default -> boardColor = "#E3B448"
                R.id.color_green -> boardColor = "#4CAF50"
            }
            
            // 更新卡片外观
            when (radioButtonId) {
                R.id.mode_two_player, R.id.mode_player_first, R.id.mode_ai_first -> updateModeCardsState(radioButtonId)
                R.id.difficulty_easy, R.id.difficulty_medium, R.id.difficulty_hard -> updateDifficultyCardsState(radioButtonId)
                R.id.size_15, R.id.size_20, R.id.size_30, R.id.size_20_40 -> updateSizeCardsState(radioButtonId)
                R.id.color_default, R.id.color_green, R.id.color_custom -> {
                    updateColorCardsState(radioButtonId)
                    if (radioButtonId == R.id.color_default || radioButtonId == R.id.color_green) {
                        updateColorButtonBackground(boardColor)
                    }
                }
            }
            
            // 设置RadioButton的选中状态
            radioButton.isChecked = true
            
            // 补充使用RadioGroup的check方法（可能会再次触发监听器，但不影响结果）
            radioGroup?.check(radioButtonId)
            
            Log.d("WelcomeActivity", "卡片点击: ${resources.getResourceEntryName(cardId)}, 选中: ${resources.getResourceEntryName(radioButtonId)}, 游戏模式: $gameMode, 难度: $difficulty, 棋盘: ${boardSizeRows}x${boardSizeCols}, 颜色: $boardColor")
        }
    }
    
    private fun updateCardAppearance(cardView: com.google.android.material.card.MaterialCardView, isChecked: Boolean) {
        if (isChecked) {
            // 选中状态
            cardView.strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_selected)
            cardView.strokeColor = ContextCompat.getColor(this, R.color.card_stroke_selected)
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_selected))
            cardView.elevation = resources.getDimension(R.dimen.card_elevation_selected)
        } else {
            // 未选中状态
            cardView.strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_normal)
            cardView.strokeColor = ContextCompat.getColor(this, R.color.card_stroke_normal)
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background_normal))
            cardView.elevation = resources.getDimension(R.dimen.card_elevation_normal)
        }
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
        // 打印日志，确认当前选中的选项
        Log.d("WelcomeActivity", "开始游戏 - 游戏模式: $gameMode, 难度: $difficulty, 棋盘大小: ${boardSizeRows}x${boardSizeCols}, 棋盘颜色: $boardColor")
        
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
    
    // 为每个RadioButton设置直接点击监听
    private fun setupDirectRadioButtonListeners() {
        // 游戏模式
        findViewById<RadioButton>(R.id.mode_two_player).setOnClickListener { 
            gameMode = 0
            modeRadioGroup.check(R.id.mode_two_player)
            updateModeCardsState(R.id.mode_two_player)
            Log.d("WelcomeActivity", "直接点击: RadioButton 双人对战, 游戏模式: $gameMode")
        }
        findViewById<RadioButton>(R.id.mode_player_first).setOnClickListener { 
            gameMode = 1
            modeRadioGroup.check(R.id.mode_player_first)
            updateModeCardsState(R.id.mode_player_first)
            Log.d("WelcomeActivity", "直接点击: RadioButton 玩家先手, 游戏模式: $gameMode")
        }
        findViewById<RadioButton>(R.id.mode_ai_first).setOnClickListener { 
            gameMode = 2
            modeRadioGroup.check(R.id.mode_ai_first)
            updateModeCardsState(R.id.mode_ai_first)
            Log.d("WelcomeActivity", "直接点击: RadioButton AI先手, 游戏模式: $gameMode")
        }
        
        // 难度级别
        findViewById<RadioButton>(R.id.difficulty_easy).setOnClickListener { 
            difficulty = 0
            difficultyRadioGroup.check(R.id.difficulty_easy)
            updateDifficultyCardsState(R.id.difficulty_easy)
            Log.d("WelcomeActivity", "直接点击: RadioButton 简单, 难度: $difficulty")
        }
        findViewById<RadioButton>(R.id.difficulty_medium).setOnClickListener { 
            difficulty = 1
            difficultyRadioGroup.check(R.id.difficulty_medium)
            updateDifficultyCardsState(R.id.difficulty_medium)
            Log.d("WelcomeActivity", "直接点击: RadioButton 中等, 难度: $difficulty")
        }
        findViewById<RadioButton>(R.id.difficulty_hard).setOnClickListener { 
            difficulty = 2
            difficultyRadioGroup.check(R.id.difficulty_hard)
            updateDifficultyCardsState(R.id.difficulty_hard)
            Log.d("WelcomeActivity", "直接点击: RadioButton 困难, 难度: $difficulty")
        }
        
        // 棋盘大小
        findViewById<RadioButton>(R.id.size_15).setOnClickListener { 
            boardSizeRows = 15
            boardSizeCols = 15
            boardSizeRadioGroup.check(R.id.size_15)
            updateSizeCardsState(R.id.size_15)
            Log.d("WelcomeActivity", "直接点击: RadioButton 15x15, 棋盘: ${boardSizeRows}x${boardSizeCols}")
        }
        findViewById<RadioButton>(R.id.size_20).setOnClickListener { 
            boardSizeRows = 20
            boardSizeCols = 20
            boardSizeRadioGroup.check(R.id.size_20)
            updateSizeCardsState(R.id.size_20)
            Log.d("WelcomeActivity", "直接点击: RadioButton 20x20, 棋盘: ${boardSizeRows}x${boardSizeCols}")
        }
        findViewById<RadioButton>(R.id.size_30).setOnClickListener { 
            boardSizeRows = 30
            boardSizeCols = 30
            boardSizeRadioGroup.check(R.id.size_30)
            updateSizeCardsState(R.id.size_30)
            Log.d("WelcomeActivity", "直接点击: RadioButton 30x30, 棋盘: ${boardSizeRows}x${boardSizeCols}")
        }
        findViewById<RadioButton>(R.id.size_20_40).setOnClickListener { 
            boardSizeRows = 40
            boardSizeCols = 20
            boardSizeRadioGroup.check(R.id.size_20_40)
            updateSizeCardsState(R.id.size_20_40)
            Log.d("WelcomeActivity", "直接点击: RadioButton 20x40, 棋盘: ${boardSizeRows}x${boardSizeCols}")
        }
        
        // 棋盘颜色
        findViewById<RadioButton>(R.id.color_default).setOnClickListener { 
            boardColor = "#E3B448"
            boardColorRadioGroup.check(R.id.color_default)
            updateColorCardsState(R.id.color_default)
            updateColorButtonBackground(boardColor)
            Log.d("WelcomeActivity", "直接点击: RadioButton 木色, 颜色: $boardColor")
        }
        findViewById<RadioButton>(R.id.color_green).setOnClickListener { 
            boardColor = "#4CAF50"
            boardColorRadioGroup.check(R.id.color_green)
            updateColorCardsState(R.id.color_green)
            updateColorButtonBackground(boardColor)
            Log.d("WelcomeActivity", "直接点击: RadioButton 绿色, 颜色: $boardColor")
        }
        findViewById<RadioButton>(R.id.color_custom).setOnClickListener { 
            boardColorRadioGroup.check(R.id.color_custom)
            updateColorCardsState(R.id.color_custom)
            Log.d("WelcomeActivity", "直接点击: RadioButton 自定义, 颜色: $boardColor")
        }
    }
}