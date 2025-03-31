package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.ComponentActivity

class WelcomeActivity : ComponentActivity() {
    
    private lateinit var modeRadioGroup: RadioGroup
    private lateinit var difficultyRadioGroup: RadioGroup
    private lateinit var startGameButton: Button
    
    // 游戏选项
    private var gameMode = 0
    private var difficulty = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        // 初始化视图
        modeRadioGroup = findViewById(R.id.mode_radio_group)
        difficultyRadioGroup = findViewById(R.id.difficulty_radio_group)
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
        
        // 设置开始游戏按钮点击事件
        startGameButton.setOnClickListener {
            startGame()
        }
        
        // 默认设置
        modeRadioGroup.check(R.id.mode_two_player)
        difficultyRadioGroup.check(R.id.difficulty_medium)
    }
    
    private fun startGame() {
        // 创建Intent，传递游戏模式和难度参数到游戏页面
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("GAME_MODE", gameMode)
            putExtra("DIFFICULTY", difficulty)
        }
        startActivity(intent)
    }
}