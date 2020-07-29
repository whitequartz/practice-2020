import java.io.File
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.FileReader
import java.io.FilenameFilter
import java.util.Scanner
import kotlin.math.round
import kotlin.text.Charsets.UTF_32


class Unittest {
    companion object {
        // companion == static
        @JvmStatic
        fun main(args: Array<String>) {
            val dir = File("src/units")
            val filesEmj = dir.list(MyFileNameFilter("emj"))
            val filesAns = dir.list(MyFileNameFilter("ans"))
            val standartOut = System.out
            var count = 0
            filesEmj!!.forEach { name ->
                print(name)
                val rawNameAns = name.split(".").toMutableList()
                rawNameAns[rawNameAns.lastIndex] = "ans"
                val nameAns = rawNameAns.joinToString(".")
                if (filesAns?.contains(nameAns) == false) {
                    // Если файл с ответом не существует
                    throw Exception("File result $nameAns for file $name not found.")
                }
                try {
                    // Запускаем компилятор
                    val baos = ByteArrayOutputStream()
                    val ps = PrintStream(baos, true, UTF_32)
                    System.setOut(ps)
                    Emoji.main(arrayOf("${dir.absolutePath}\\$name"))
                    // Проверка правильности выполнения
                    val result = baos.toString(UTF_32)
                    System.setOut(standartOut)
                    val scan1 = Scanner(result)
                    val scan2 = Scanner(FileReader("${dir.absolutePath}\\$nameAns"))
                    while (scan1.hasNextLine() == scan2.hasNextLine()) {
                        if (scan1.hasNextLine()) {
                            val line1 = scan1.nextLine()
                            val line2 = scan2.nextLine()
                            if (line1 != line2) {
                                println(" -> bad")
                                return@forEach
                            }
                        } else {
                            // Строки закончились и они все совпадают
                            count++
                            val status = round(1000.0 * count / filesEmj.size) / 10.0
                            println(" -> good $status%")
                            return@forEach
                        }
                    }
                    // Количество строк вывода не совпадает
                    println(" -> bad")
                }
                catch (exception: Exception) {
                    System.setOut(standartOut)
                    println(" -> error")
                    println(exception.message)
                }
            }
            val status = round(1000.0 * count / filesEmj.size) / 10.0
            println("\nResult: number of successful tests $count out of ${filesEmj.size} ($status%)")
        }
    }
}


class MyFileNameFilter(ext: String) : FilenameFilter {
    private val ext: String

    override fun accept(dir: File, name: String): Boolean {
        return name.toLowerCase().endsWith(ext)
    }

    init {
        this.ext = ext.toLowerCase()
    }
}
