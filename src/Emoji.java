import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Emoji {
    protected static int maxEmojiInLine = 0, countOfLine = 0;
    protected static double[] matrix;

    public static void main(String[] args) {
        // Считываем все эмоджи в одномерную матрицу из файла
        try
        {
            // Поиск количества строк, максимального числа эмоджи в строке
            Scanner scan = new Scanner(new FileReader("src/emoji.txt"));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.getBytes("UTF-32").length > maxEmojiInLine) {
                    maxEmojiInLine = line.getBytes("UTF-32").length;
                }
                countOfLine++;
            }
            maxEmojiInLine = maxEmojiInLine / 4;
            matrix = new double[countOfLine * maxEmojiInLine];
            scan.close();

            // Считывание эмоджи в матрицу
            scan = new Scanner(new FileReader("src/emoji.txt"));
            for (int i = 0; i < countOfLine; i++){
                String line = scan.nextLine();
                int j;
                for (j = 0; j < (line.getBytes("UTF-32").length / 4); j++) {
                    byte[] array = line.substring(j, j+1).getBytes("UTF-32");
                    matrix[i * maxEmojiInLine + j] = array[0] * Math.pow(32, 3) + array[1] * Math.pow(32, 2) +
                            array[2] * 32 + array[3];
                }
                for (; j < maxEmojiInLine; j++) {
                    matrix[i * maxEmojiInLine + j] = Operator.EMPTY.getSymbolCode();
                }
            }
            scan.close();

            // синтаксический анализатор
            int posX = 0, posY = 0;
            Operator[] operators = Operator.values();
            while (true) {
                for (int i = 0; i < operators.length; i++) {
                    if (matrix[posY * maxEmojiInLine + posX] == operators[i].getSymbolCode()) {
                        // TODO
                    }
                }
            }

        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
