package com.example.myapplication

import kotlin.random.Random

class GomokuAI(private val boardSize: Int) {

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

    // 设置棋盘状态
    fun setBoard(boardState: Array<IntArray>) {
        board = boardState
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

        // 评估每个空位的分数
        var bestScore = -1
        var bestMove = Pair(-1, -1)

        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (board[i][j] == 0) { // 空位
                    val score = evaluatePosition(i, j)
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = Pair(i, j)
                    }
                }
            }
        }

        return bestMove
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

        // 优先防守，如果玩家快要赢了，则权重更高
        return if (humanScore > aiScore * 1.5) {
            humanScore * 2
        } else {
            aiScore
        }
    }

    // 评估特定方向上的得分
    private fun evaluateDirection(row: Int, col: Int, rowDir: Int, colDir: Int, piece: Int): Int {
        val consecutive = countConsecutive(row, col, rowDir, colDir, piece)
        
        return when(consecutive) {
            5 -> winScore
            4 -> fourScore
            3 -> threeScore
            2 -> twoScore
            1 -> oneScore
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