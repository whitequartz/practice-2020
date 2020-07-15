import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Emoji {
    protected static int maxEmojiInLine = 0, countOfLine = 0;
    protected static double[] matrix;
    protected static final double
        // MOVEMENT
        MOVE_UP = 1382,
        MOVE_RIGHT = 1153,
        MOVE_DOWN = 1383,
        MOVE_LEFT = 1381,
        MOVE_RIGHT_IF_TRUE = 1110,
        MOVE_DOWN_IF_TRUE = 733,
        END_OF_PROGRAM = 545,

        // ARITHMETIC OPERATION
        ADDITION = 1141,
        SUBTRACTION = 1142,
        MULTIPLICATION = 1270,
        DIVISION = 555,
        MODULO = 1143,

        // COMPARATORS
        EQUALLY = 721,
        MORE = 917,
        LESS = 918,

        // LOGISTIC OPERATORS
        NOT = 837,
        AND = 830,
        OR = 1260,

        // COMMANDS
        VARIABLE = 610,
        FUNCTION = 584,
        WORK_WITH_TEXT = 708,
        TEXT_OUTPUT = 556,
        IF = 820,

        // EMPTY
        EMPTY = 32;

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
                    matrix[i * maxEmojiInLine + j] = EMPTY;
                }
            }
            scan.close();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}