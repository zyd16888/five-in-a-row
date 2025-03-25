package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : ComponentActivity() {
    
    private lateinit var gomokuView: GomokuView
    private lateinit var gameStatusText: TextView
    private lateinit var resetButton: Button
    private lateinit var modeRadioGroup: RadioGroup
    private lateinit var winOverlay: FrameLayout
    private lateinit var winText: TextView
    private lateinit var winResetButton: Button
    
    // 当前游戏模式
    private var currentMode = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化视图
        gomokuView = findViewById(R.id.gomoku_view)
        gameStatusText = findViewById(R.id.game_status_text)
        resetButton = findViewById(R.id.reset_button)
        modeRadioGroup = findViewById(R.id.mode_radio_group)
        winOverlay = findViewById(R.id.win_overlay)
        winText = findViewById(R.id.win_text)
        winResetButton = findViewById(R.id.win_reset_button)
        
        // 设置游戏状态变化监听器
        gomokuView.onGameStatusChanged = { winner ->
            val statusText = if (winner == 1) "黑棋胜利!" else "白棋胜利!"
            gameStatusText.text = statusText
            showWinOverlay(winner)
        }
        
        // 设置重置按钮点击事件
        resetButton.setOnClickListener {
            resetGame()
        }
        
        // 设置胜利弹窗重置按钮点击事件
        winResetButton.setOnClickListener {
            hideWinOverlay()
            resetGame()
        }
        
        // 设置模式选择
        modeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.mode_two_player -> {
                    currentMode = 0
                    gomokuView.setGameMode(0)
                    updateStatusText("双人对战模式")
                }
                R.id.mode_player_first -> {
                    currentMode = 1
                    gomokuView.setGameMode(1)
                    updateStatusText("人机对战 - 玩家先手")
                }
                R.id.mode_ai_first -> {
                    currentMode = 2
                    gomokuView.setGameMode(2)
                    updateStatusText("人机对战 - AI先手")
                }
            }
        }
        
        // 默认设置为双人对战模式
        modeRadioGroup.check(R.id.mode_two_player)
    }
    
    private fun showWinOverlay(winner: Int) {
        val winMessage = if (winner == 1) "黑棋胜利！" else "白棋胜利！"
        winText.text = winMessage
        winOverlay.visibility = View.VISIBLE
    }
    
    private fun hideWinOverlay() {
        winOverlay.visibility = View.GONE
    }
    
    private fun resetGame() {
        gomokuView.resetGame()
        when (currentMode) {
            0 -> updateStatusText("双人对战模式")
            1 -> updateStatusText("人机对战 - 玩家先手")
            2 -> updateStatusText("人机对战 - AI先手")
        }
        hideWinOverlay()
    }
    
    private fun updateStatusText(text: String) {
        gameStatusText.text = text
    }
}