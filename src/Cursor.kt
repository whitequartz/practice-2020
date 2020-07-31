data class Cursor(private val matrix: IntArray,
                  private val countOfLine: Int,
                  private val maxEmojiInLine: Int,
                  private var line: Int,
                  private var column: Int,
                  private var lineInc: Int,
                  private var columnInc: Int)
{
    constructor(matrix: IntArray, countOfLine: Int, maxEmojiInLine: Int) :
            this(matrix, countOfLine, maxEmojiInLine, 0, 0, 0, 1)

    // Выполняет следующий шаг в текущем направлении
    fun nextStep() {
        line += lineInc
        column += columnInc
    }

    // Метод для получения значения элемента
    fun getCursor(): Int {
        return if (column < 0 || column >= maxEmojiInLine || line < 0 || line >= countOfLine) {
            Operator.STOP_SYMBOL
        } else {
            matrix[line * maxEmojiInLine + column]
        }
    }

    fun setUp() {
        lineInc = -1
        columnInc = 0
    }

    fun setRight() {
        lineInc = 0
        columnInc = 1
    }

    fun setDown() {
        lineInc = 1
        columnInc = 0
    }

    fun setLeft() {
        lineInc = 0
        columnInc = -1
    }

    fun copy(): Cursor = Cursor(matrix, countOfLine, maxEmojiInLine, line, column, lineInc, columnInc)
}
