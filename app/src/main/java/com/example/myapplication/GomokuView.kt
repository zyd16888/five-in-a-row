package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.BlurMaskFilter
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.math.max

class GomokuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 棋盘尺寸 - 默认为15x15
    private var boardSizeRows = 15
    private var boardSizeCols = 15

    // 保存棋盘状态：0=空，1=黑棋，2=白棋
    private var board = Array(boardSizeRows) { IntArray(boardSizeCols) { 0 } }

    // 当前玩家：1=黑棋，2=白棋
    private var currentPlayer = 1

    // 游戏是否结束
    private var gameOver = false

    // 游戏模式: 0=双人对战, 1=人机对战(玩家先手), 2=人机对战(AI先手)
    private var gameMode = 1

    // AI难度: 0=简单, 1=中等, 2=困难
    private var difficulty = 1

    // AI实例
    private val ai = GomokuAI(boardSizeRows, boardSizeCols)

    // 格子大小和边距，在onMeasure中计算
    private var cellSize = 0f
    private var padding = 0f

    // 最后一个落子位置，用于高亮显示
    private var lastRow = -1
    private var lastCol = -1

    // 画笔
    private val boardPaint = Paint().apply {
        color = Color.parseColor("#E3B448") // 棋盘颜色
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val blackPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val whitePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    private val lastMovePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }
    
    private val highlightPaint = Paint().apply {
        color = Color.parseColor("#80FF0000") // 半透明红色
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val blackShadowPaint = Paint().apply {
        color = Color.BLACK
        alpha = 50
        isAntiAlias = true
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
    }

    private val whiteShadowPaint = Paint().apply {
        color = Color.DKGRAY
        alpha = 30
        isAntiAlias = true
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(4f, BlurMaskFilter.Blur.NORMAL)
    }

    // 游戏状态监听器
    var onGameStatusChanged: ((winner: Int) -> Unit)? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        
        // 保持视图原有尺寸，不强制为正方形
        setMeasuredDimension(width, height)

        // 计算格子尺寸和边距，考虑非正方形棋盘
        padding = min(width, height) * 0.05f
        
        // 根据棋盘列数和行数计算合适的格子大小
        val cellWidthAvailable = (width - 2 * padding) / (boardSizeCols - 1)
        val cellHeightAvailable = (height - 2 * padding) / (boardSizeRows - 1)
        
        // 取最小值确保格子是正方形
        cellSize = min(cellWidthAvailable, cellHeightAvailable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制棋盘背景
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), boardPaint)

        // 绘制棋盘线
        drawBoardLines(canvas)
        
        // 绘制星位点
        drawStarPoints(canvas)

        // 绘制棋子
        drawPieces(canvas)
    }

    private fun drawBoardLines(canvas: Canvas) {
        // 画横线
        for (i in 0 until boardSizeRows) {
            canvas.drawLine(
                padding,
                padding + i * cellSize,
                padding + (boardSizeCols - 1) * cellSize,
                padding + i * cellSize,
                linePaint
            )
        }

        // 画竖线
        for (i in 0 until boardSizeCols) {
            canvas.drawLine(
                padding + i * cellSize,
                padding,
                padding + i * cellSize,
                padding + (boardSizeRows - 1) * cellSize,
                linePaint
            )
        }
    }
    
    private fun drawStarPoints(canvas: Canvas) {
        // 根据棋盘大小动态计算星位点位置
        val starPoints = mutableListOf<Pair<Int, Int>>()
        
        // 小号棋盘（15x15及以下）
        if (boardSizeRows <= 15 && boardSizeCols <= 15) {
            // 标准五子棋星位点（3-3, 3-11, 7-7, 11-3, 11-11）
            if (boardSizeRows >= 15 && boardSizeCols >= 15) {
                starPoints.add(Pair(3, 3))
                starPoints.add(Pair(3, 11))
                starPoints.add(Pair(7, 7))
                starPoints.add(Pair(11, 3))
                starPoints.add(Pair(11, 11))
            }
        } else {
            // 大号棋盘，计算适当的星位点
            val rowStep = boardSizeRows / 6
            val colStep = boardSizeCols / 6
            
            // 添加四角的星位点
            starPoints.add(Pair(rowStep, colStep))
            starPoints.add(Pair(rowStep, boardSizeCols - colStep - 1))
            starPoints.add(Pair(boardSizeRows - rowStep - 1, colStep))
            starPoints.add(Pair(boardSizeRows - rowStep - 1, boardSizeCols - colStep - 1))
            
            // 添加中心星位点
            starPoints.add(Pair(boardSizeRows / 2, boardSizeCols / 2))
            
            // 如果棋盘足够大，添加更多的星位点
            if (boardSizeRows >= 20 && boardSizeCols >= 20) {
                starPoints.add(Pair(rowStep, boardSizeCols / 2))
                starPoints.add(Pair(boardSizeRows - rowStep - 1, boardSizeCols / 2))
                starPoints.add(Pair(boardSizeRows / 2, colStep))
                starPoints.add(Pair(boardSizeRows / 2, boardSizeCols - colStep - 1))
            }
        }
        
        // 绘制星位点
        for ((row, col) in starPoints) {
            if (row < boardSizeRows && col < boardSizeCols) {
                val cx = padding + col * cellSize
                val cy = padding + row * cellSize
                val radius = cellSize * 0.15f
                
                canvas.drawCircle(cx, cy, radius, blackPaint)
            }
        }
    }

    private fun drawPieces(canvas: Canvas) {
        for (i in 0 until boardSizeRows) {
            for (j in 0 until boardSizeCols) {
                if (board[i][j] != 0) {
                    val cx = padding + j * cellSize
                    val cy = padding + i * cellSize
                    val radius = cellSize * 0.42f
                    
                    // 绘制阴影
                    if (board[i][j] == 1) {
                        canvas.drawCircle(cx + 2f, cy + 2f, radius, blackShadowPaint)
                    } else {
                        canvas.drawCircle(cx + 1f, cy + 1f, radius, whiteShadowPaint)
                    }

                    // 绘制棋子
                    val paint = if (board[i][j] == 1) blackPaint else whitePaint
                    canvas.drawCircle(cx, cy, radius, paint)
                    
                    // 如果是最后一个落子位置，绘制红色标记
                    if (i == lastRow && j == lastCol) {
                        canvas.drawCircle(cx, cy, radius * 0.6f, lastMovePaint)
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && !gameOver) {
            // 如果是AI回合，不处理玩家点击
            if ((gameMode == 1 && currentPlayer == 2) || (gameMode == 2 && currentPlayer == 1)) {
                return true
            }

            val x = event.x
            val y = event.y

            // 转换为棋盘坐标
            val col = ((x - padding) / cellSize + 0.5f).toInt()
            val row = ((y - padding) / cellSize + 0.5f).toInt()

            // 判断位置是否有效
            if (row in 0 until boardSizeRows && col in 0 until boardSizeCols && board[row][col] == 0) {
                // 放置棋子
                placePiece(row, col)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // 放置棋子
    private fun placePiece(row: Int, col: Int) {
        // 放置棋子
        board[row][col] = currentPlayer
        
        // 记录最后落子位置
        lastRow = row
        lastCol = col

        // 检查是否获胜
        if (checkWin(row, col)) {
            gameOver = true
            onGameStatusChanged?.invoke(currentPlayer)
            invalidate()
            return
        }

        // 切换玩家
        currentPlayer = if (currentPlayer == 1) 2 else 1
        invalidate()

        // 如果是AI回合，让AI下棋
        if (!gameOver && ((gameMode == 1 && currentPlayer == 2) || (gameMode == 2 && currentPlayer == 1))) {
            postDelayed({ makeAIMove() }, 500)
        }
    }

    // AI下棋
    private fun makeAIMove() {
        if (gameOver) return

        // 更新AI的棋盘状态
        ai.setBoard(board)
        
        // 获取AI的落子位置
        val (row, col) = ai.getNextMove()
        
        if (row != -1 && col != -1) {
            // 放置棋子
            placePiece(row, col)
        }
    }

    // 检查是否获胜
    private fun checkWin(row: Int, col: Int): Boolean {
        val piece = board[row][col]

        // 横向
        if (checkDirection(row, col, 0, 1, piece)) return true

        // 纵向
        if (checkDirection(row, col, 1, 0, piece)) return true

        // 正对角线
        if (checkDirection(row, col, 1, 1, piece)) return true

        // 反对角线
        if (checkDirection(row, col, 1, -1, piece)) return true

        return false
    }

    private fun checkDirection(row: Int, col: Int, rowDir: Int, colDir: Int, piece: Int): Boolean {
        var count = 1

        // 正向检查
        var r = row + rowDir
        var c = col + colDir
        while (r in 0 until boardSizeRows && c in 0 until boardSizeCols && board[r][c] == piece) {
            count++
            r += rowDir
            c += colDir
        }

        // 反向检查
        r = row - rowDir
        c = col - colDir
        while (r in 0 until boardSizeRows && c in 0 until boardSizeCols && board[r][c] == piece) {
            count++
            r -= rowDir
            c -= colDir
        }

        return count >= 5
    }

    // 设置游戏模式
    fun setGameMode(mode: Int) {
        gameMode = mode
        resetGame()
        
        // 如果AI先手，让AI下棋
        if (gameMode == 2) {
            postDelayed({ makeAIMove() }, 500)
        }
    }

    // 设置AI难度
    fun setDifficulty(level: Int) {
        difficulty = level
        ai.setDifficultyLevel(level)
    }

    // 设置棋盘尺寸
    fun setBoardSize(size: Int) {
        setBoardSize(size, size)
    }
    
    // 设置非正方形棋盘尺寸
    fun setBoardSize(rows: Int, cols: Int) {
        if (rows <= 0 || cols <= 0) return
        
        boardSizeRows = rows
        boardSizeCols = cols
        
        // 重新初始化棋盘数组
        board = Array(boardSizeRows) { IntArray(boardSizeCols) { 0 } }
        
        // 更新AI的棋盘尺寸
        ai.setBoardSize(boardSizeRows, boardSizeCols)
        
        // 重置游戏
        resetGame()
    }

    // 获取当前棋盘尺寸
    fun getBoardSize(): Pair<Int, Int> {
        return Pair(boardSizeRows, boardSizeCols)
    }

    // 重置游戏
    fun resetGame() {
        for (i in 0 until boardSizeRows) {
            for (j in 0 until boardSizeCols) {
                board[i][j] = 0
            }
        }
        currentPlayer = 1
        gameOver = false
        lastRow = -1
        lastCol = -1
        invalidate()
        
        // 如果AI先手，让AI下棋
        if (gameMode == 2 && !gameOver) {
            postDelayed({ makeAIMove() }, 500)
        }
    }
}