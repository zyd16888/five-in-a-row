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
    private lateinit var boardSizeRadioGroup: RadioGroup
    private lateinit var winOverlay: FrameLayout
    private lateinit var winText: TextView
    private lateinit var winResetButton: Button
    
    // 当前游戏模式
    private var currentMode = 0
    
    // 当前棋盘大小
    private var currentBoardSize = 15
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化视图
        gomokuView = findViewById(R.id.gomoku_view)
        gameStatusText = findViewById(R.id.game_status_text)
        resetButton = findViewById(R.id.reset_button)
        modeRadioGroup = findViewById(R.id.mode_radio_group)
        boardSizeRadioGroup = findViewById(R.id.board_size_radio_group)
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
                R.id.mode_two_player -> {
                    currentMode = 0
                    gomokuView.setGameMode(0)
                    updateStatusText("双人对战模式")
                }
            }
        }
        
        // 设置棋盘大小选择
        boardSizeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_15 -> {
                    currentBoardSize = 15
                    gomokuView.setBoardSize(15)
                    updateBoardSizeStatus()
                }
                R.id.size_20 -> {
                    currentBoardSize = 20
                    gomokuView.setBoardSize(20)
                    updateBoardSizeStatus()
                }
                R.id.size_30 -> {
                    currentBoardSize = 30
                    gomokuView.setBoardSize(30)
                    updateBoardSizeStatus()
                }
                R.id.size_20_40 -> {
                    currentBoardSize = 20
                    gomokuView.setBoardSize(20, 40) // 使用非正方形棋盘
                    updateBoardSizeStatus()
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
    
    private fun updateBoardSizeStatus() {
        // 可以在这里更新棋盘大小相关的状态显示
        val modeText = when (currentMode) {
            0 -> "双人对战模式"
            1 -> "人机对战 - 玩家先手"
            2 -> "人机对战 - AI先手"
            else -> "未知模式"
        }
        
        val sizeText = when (boardSizeRadioGroup.checkedRadioButtonId) {
            R.id.size_15 -> "15x15"
            R.id.size_20 -> "20x20"
            R.id.size_30 -> "30x30"
            R.id.size_20_40 -> "20x40"
            else -> "15x15"
        }
        
        updateStatusText("$modeText - 棋盘$sizeText")
    }
}