class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val str = "\uD83D\uDDA4\uD83D\uDC99fгgпв6\uD83D\uDE41"
            val a = str.toByteArray(charset("UTF-32"))
            val s = a.size / 4

            for (i in 0 until s) {
                print(i)
                print("  ")
                print(a[i * 4 + 0])
                print(" ")
                print(a[i * 4 + 1])
                print(" ")
                print(a[i * 4 + 2])
                print(" ")
                print(a[i * 4 + 3])
                print("  ")
                println(String(byteArrayOf(a[i * 4 + 0], a[i * 4 + 1], a[i * 4 + 2], a[i * 4 + 3])))

                val b = String(byteArrayOf(a[i * 4 + 0], a[i * 4 + 1], a[i * 4 + 2], a[i * 4 + 3])).toByteArray()
                print(b.size)
            }
            println(str)
            println(a.size)
        }
    }
}
