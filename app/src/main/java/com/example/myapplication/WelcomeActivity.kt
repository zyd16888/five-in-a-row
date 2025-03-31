package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat

class WelcomeActivity : ComponentActivity() {
    
    private lateinit var modeRadioGroup: RadioGroup
    private lateinit var difficultyRadioGroup: RadioGroup
    private lateinit var boardSizeRadioGroup: RadioGroup
    private lateinit var boardColorRadioGroup: RadioGroup
    private lateinit var customColorButton: ImageButton
    private lateinit var startGameButton: Button
    
    // 游戏选项
    private var gameMode = 0
    private var difficulty = 1
    private var boardSizeRows = 15
    private var boardSizeCols = 15
    private var boardColor = "#E3B448" // 默认颜色
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        // 初始化视图
        modeRadioGroup = findViewById(R.id.mode_radio_group)
        difficultyRadioGroup = findViewById(R.id.difficulty_radio_group)
        boardSizeRadioGroup = findViewById(R.id.board_size_radio_group)
        boardColorRadioGroup = findViewById(R.id.board_color_radio_group)
        customColorButton = findViewById(R.id.custom_color_button)
        startGameButton = findViewById(R.id.start_game_button)
        
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
        modeRadioGroup.check(R.id.mode_player_first)
        difficultyRadioGroup.check(R.id.difficulty_medium)
        boardSizeRadioGroup.check(R.id.size_15)
        boardColorRadioGroup.check(R.id.color_default)
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
                // 设置自定义颜色按钮的背景色
                try {
                    customColorButton.setBackgroundColor(Color.parseColor(boardColor))
                } catch (e: Exception) {
                    Toast.makeText(this, "无效的颜色代码", Toast.LENGTH_SHORT).show()
                }
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