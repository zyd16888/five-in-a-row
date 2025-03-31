package com.example.myapplication

import kotlin.random.Random

class GomokuAI(private var boardSize: Int) {

    // 难度级别：0=简单，1=中等，2=困难
    private var difficultyLevel = 1
    
    // 棋盘状态的引用
    private lateinit var board: Array<IntArray>
    
    // AI的棋子代码（通常为2，即白棋）
    private val aiPiece = 2
    
    // 人类玩家的棋子代码（通常为1，即黑棋）
    private val humanPiece = 1

    // 棋盘得分权重
    private val winScore = 100000
    private val fourScore = 10000
    private val threeScore = 1000
    private val twoScore = 100
    private val oneScore = 10

    // 方向数组：水平、垂直、正对角线、反对角线
    private val directions = arrayOf(
        intArrayOf(0, 1),  // 水平
        intArrayOf(1, 0),  // 垂直
        intArrayOf(1, 1),  // 正对角线
        intArrayOf(1, -1)  // 反对角线
    )

    // 设置难度级别
    fun setDifficultyLevel(level: Int) {
        difficultyLevel = level
    }
    
    // 获取当前难度级别
    fun getDifficultyLevel(): Int {
        return difficultyLevel
    }
    
    // 设置棋盘状态
    fun setBoard(boardState: Array<IntArray>) {
        board = boardState
    }
    
    // 设置棋盘大小
    fun setBoardSize(size: Int) {
        if (size > 0) {
            boardSize = size
        }
    }

    // 获取AI的下一步落子位置
    fun getNextMove(): Pair<Int, Int> {
        // 如果是空棋盘，随机选择靠近中心的位置
        if (isEmptyBoard()) {
            val center = boardSize / 2
            val range = 2
            return Pair(
                Random.nextInt(center - range, center + range + 1),
                Random.nextInt(center - range, center + range + 1)
            )
        }

        // 简单难度时，有一定概率随机下棋
        if (difficultyLevel == 0 && Random.nextDouble() < 0.4) {
            return getRandomValidMove()
        }

        // 评估每个空位的分数
        var bestScore = -1
        var bestMoves = mutableListOf<Pair<Int, Int>>()

        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (board[i][j] == 0) { // 空位
                    val score = evaluatePosition(i, j)
                    
                    // 根据难度调整分数比较逻辑
                    when {
                        score > bestScore -> {
                            bestScore = score
                            bestMoves.clear()
                            bestMoves.add(Pair(i, j))
                        }
                        score == bestScore -> {
                            bestMoves.add(Pair(i, j))
                        }
                    }
                }
            }
        }

        // 随机选择一个最佳移动（简单难度有更多随机性）
        return if (bestMoves.isNotEmpty()) {
            // 简单难度不总是选择最佳移动
            if (difficultyLevel == 0 && bestMoves.size > 1 && Random.nextDouble() < 0.3) {
                bestMoves[Random.nextInt(1, bestMoves.size)]
            } else {
                bestMoves[0]
            }
        } else {
            getRandomValidMove()
        }
    }

    // 获取随机有效移动
    private fun getRandomValidMove(): Pair<Int, Int> {
        val validMoves = mutableListOf<Pair<Int, Int>>()
        
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (board[i][j] == 0) {
                    validMoves.add(Pair(i, j))
                }
            }
        }
        
        return if (validMoves.isNotEmpty()) {
            validMoves[Random.nextInt(validMoves.size)]
        } else {
            Pair(-1, -1)
        }
    }
    
    // 检查是否是空棋盘
    private fun isEmptyBoard(): Boolean {
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (board[i][j] != 0) {
                    return false
                }
            }
        }
        return true
    }

    // 评估位置价值
    private fun evaluatePosition(row: Int, col: Int): Int {
        var aiScore = 0
        var humanScore = 0

        // 检查每个方向
        for (dir in directions) {
            // 评估AI的得分
            aiScore += evaluateDirection(row, col, dir[0], dir[1], aiPiece)
            
            // 评估玩家的得分
            humanScore += evaluateDirection(row, col, dir[0], dir[1], humanPiece)
        }

        // 根据难度级别调整防守和进攻的权重
        return when (difficultyLevel) {
            0 -> { // 简单难度：防守意识弱，进攻随机
                if (humanScore > aiScore * 2) humanScore else aiScore
            }
            1 -> { // 中等难度：平衡防守和进攻
                if (humanScore > aiScore * 1.5) (humanScore * 2).toInt() else aiScore
            }
            2 -> { // 困难难度：强化防守和进攻
                if (humanScore > aiScore * 1.2) (humanScore * 2.5).toInt() else (aiScore * 1.2).toInt()
            }
            else -> if (humanScore > aiScore * 1.5) (humanScore * 2).toInt() else aiScore
        }
    }

    // 评估特定方向上的得分
    private fun evaluateDirection(row: Int, col: Int, rowDir: Int, colDir: Int, piece: Int): Int {
        val consecutive = countConsecutive(row, col, rowDir, colDir, piece)
        
        // 困难模式下提高评分
        val difficultyMultiplier = if (difficultyLevel == 2 && piece == aiPiece) 1.2 else 1.0
        
        return when(consecutive) {
            5 -> (winScore * difficultyMultiplier).toInt()
            4 -> (fourScore * difficultyMultiplier).toInt()
            3 -> (threeScore * difficultyMultiplier).toInt()
            2 -> (twoScore * difficultyMultiplier).toInt()
            1 -> (oneScore * difficultyMultiplier).toInt()
            else -> 0
        }
    }

    // 计算在特定方向上连续棋子的数量
    private fun countConsecutive(row: Int, col: Int, rowDir: Int, colDir: Int, piece: Int): Int {
        var count = 0
        var maxCount = 0
        var blocked = 0
        
        // 假设在该位置放置一个棋子
        board[row][col] = piece
        
        // 正向检查
        var r = row
        var c = col
        
        // 最多检查5个位置
        for (i in 0 until 5) {
            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize) {
                if (board[r][c] == piece) {
                    count++
                } else if (board[r][c] != 0) {
                    blocked++
                    break
                } else {
                    break
                }
            } else {
                blocked++
                break
            }
            r += rowDir
            c += colDir
        }
        
        // 反向检查
        r = row - rowDir
        c = col - colDir
        
        // 最多检查4个位置（因为起始点已经计算过了）
        for (i in 0 until 4) {
            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize) {
                if (board[r][c] == piece) {
                    count++
                } else if (board[r][c] != 0) {
                    blocked++
                    break
                } else {
                    break
                }
            } else {
                blocked++
                break
            }
            r -= rowDir
            c -= colDir
        }
        
        // 恢复空位
        board[row][col] = 0
        
        // 如果两端都被阻挡，则价值较低
        return if (blocked == 2 && count < 5) count/2 else count
    }
} 