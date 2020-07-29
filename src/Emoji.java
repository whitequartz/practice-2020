import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

// Класс элементов, находящихся в стеке
class Element {
    public int value;
    public boolean flag;

    public Element() {
        value = 0;
        flag = false;
    }

    public Element(int value) {
        this.value = value;
        if (value != 0) flag = true;
        else flag = false;
    }
}

// Класс объявленных переменных
class Variable {
    public String name;
    public int value;

    public Variable() {
        name = "";
        value = 0;
    }

    public Variable(String name, int value) {
        this.name = name;
        this.value = value;
    }
}

public class Emoji {
    protected static int maxEmojiInLine = 0, countOfLine = 0, line = 0, column = 0,
            lineInc = 0, columnInc = 1, point = 0;
    protected static int[] matrix;
    protected static String currentString = "";
    protected static Stack<Element> stack = new Stack<>();
    protected static ArrayList<Variable> variables = new ArrayList<>();
    protected static final int
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

            // EMPTY
            EMPTY = 95;

    // Выполняет следующий шаг в текущем направлении
    protected static void nextStep() {
        line += lineInc;
        column += columnInc;
    }

    // Проверка - является ли данная строка числом
    protected static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка - существует ли такая переменная
    protected static boolean isVariable(String s) {
        return variables.stream().filter(x -> x.name.compareTo(s) == 0).count() != 0;
    }

    // Метод передвижения
    protected static void moving(int codeOfOperation) {
        switch (codeOfOperation) {
            case MOVE_UP:
                lineInc = -1;
                columnInc = 0;
                break;
            case MOVE_RIGHT:
                lineInc = 0;
                columnInc = 1;
                break;
            case MOVE_DOWN:
                lineInc = 1;
                columnInc = 0;
                break;
            case MOVE_LEFT:
                lineInc = 0;
                columnInc = -1;
                break;
            case MOVE_RIGHT_IF_TRUE:
                if (stack.pop().flag) columnInc = 1;
                else columnInc = -1;
                lineInc = 0;
                break;
            case MOVE_DOWN_IF_TRUE:
                if (stack.pop().flag) lineInc = 1;
                else lineInc = -1;
                columnInc = 0;
                break;
        }
    }

    // Метод арифметических операций
    protected static void arithmeticOperating(int codeOfOperation) {
        int secondElement = stack.pop().value, firstElement = stack.pop().value;
        switch (codeOfOperation) {
            case ADDITION:
                stack.push(new Element(firstElement + secondElement));
                break;
            case SUBTRACTION:
                stack.push(new Element(firstElement - secondElement));
                break;
            case MULTIPLICATION:
                stack.push(new Element(firstElement * secondElement));
                break;
            case DIVISION:
                stack.push(new Element(firstElement / secondElement));
                break;
            case MODULO:
                stack.push(new Element(firstElement % secondElement));
                break;
        }
    }

    // Метод операторов сравнения
    protected static void comparing(int codeOfOperation) {
        int secondElement = stack.pop().value, firstElement = stack.pop().value;
        switch (codeOfOperation) {
            case EQUALLY:
                stack.push(new Element(firstElement == secondElement ? 1 : 0));
                break;
            case MORE:
                stack.push(new Element(firstElement > secondElement ? 1 : 0));
                break;
            case LESS:
                stack.push(new Element(firstElement < secondElement ? 1 : 0));
                break;
        }
    }

    // Метод логических операций
    protected static void logisting(int codeOfOperation) {
        boolean firstElement = stack.pop().flag;
        switch (codeOfOperation) {
            case NOT:
                stack.push(new Element(firstElement ? 0 : 1));
                break;
            case AND:
                stack.push(new Element((firstElement && stack.pop().flag) ? 1 : 0));
                break;
            case OR:
                stack.push(new Element((firstElement || stack.pop().flag) ? 1 : 0));
                break;
        }
    }

    // Метод вывода текста в консоль
    protected static void outputting() throws Exception {
        String currentText = "", text = "";
        nextStep();

        // Считываем всё, что нужно вывести
        while (matrix[line * maxEmojiInLine + column] != TEXT_OUTPUT) {

            // Считываем текст
            if (matrix[line * maxEmojiInLine + column] == WORK_WITH_TEXT) {
                if (text != "") {
                    if (isVariable(text)) {
                        currentText += getVariableValue(text);
                    }
                    else {
                        throw new Exception("There is no such variable.");
                    }
                    text = "";
                }
                nextStep();
                while (matrix[line * maxEmojiInLine + column] != WORK_WITH_TEXT) {
                    currentText += Character.valueOf(Character.toChars(matrix[line * maxEmojiInLine + column])[0]);
                    nextStep();
                }
            }
            else {
                // Считывание переменного
                text += Character.valueOf(Character.toChars(matrix[line * maxEmojiInLine + column])[0]);
            }
            nextStep();
        }
        if (isVariable(text)) {
            currentText += getVariableValue(text);
        }
        System.out.println(currentText);
    }

    // Метод получения значения переменного
    protected static int getVariableValue(String s) {
        return variables.stream().filter(x -> x.name.compareTo(s) == 0).findFirst().get().value;
    }

    public static void main(String[] args) throws Exception {
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
            matrix = new int[countOfLine * maxEmojiInLine];
            scan.close();

            // Считывание эмоджи в матрицу
            scan = new Scanner(new FileReader("src/emoji.txt"));
            for (int i = 0; i < countOfLine; i++){
                String line = scan.nextLine();
                int j = 0, k = 0;
                for (; j < (line.getBytes("UTF-32").length / 4); j++) {
                    byte[] array = line.substring(k, k + 1).getBytes("UTF-32");
                    if ((array[0] == 0) && (array[1] == 0) && (array[2] == -1) && (array[3] == -3)) {
                        array = line.substring(k, k + 2).getBytes("UTF-32");
                        k++;
                    }
                    matrix[i * maxEmojiInLine + j] = (int) (array[0] * Math.pow(32, 3) + array[1] * Math.pow(32, 2) +
                            array[2] * 32 + array[3]);
                    k++;
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

        // Перебор вариантов эмоджи, которые содержит point
        point = matrix[line * maxEmojiInLine + column];
        while (point != END_OF_PROGRAM) {
            switch (point) {
                case MOVE_UP:
                case MOVE_RIGHT:
                case MOVE_DOWN:
                case MOVE_LEFT:
                case MOVE_RIGHT_IF_TRUE:
                case MOVE_DOWN_IF_TRUE:
                    moving(point);
                    break;
                case ADDITION:
                case SUBTRACTION:
                case MULTIPLICATION:
                case DIVISION:
                case MODULO:
                    arithmeticOperating(point);
                    break;
                case EQUALLY:
                case MORE:
                case LESS:
                    comparing(point);
                    break;
                case NOT:
                case AND:
                case OR:
                    logisting(point);
                    break;
                case VARIABLE:
                    nextStep();

                    // Получаем название переменного
                    while (matrix[line * maxEmojiInLine + column] != EMPTY) {
                        currentString += Character.valueOf(Character.toChars(matrix[line * maxEmojiInLine + column])[0]);
                        nextStep();
                    }
                    variables.add(new Variable(currentString, 0));
                    currentString = "";
                    nextStep();

                    // Получаем значение переменного
                    while (matrix[line * maxEmojiInLine + column] != EMPTY) {
                        currentString += Character.valueOf(Character.toChars(matrix[line * maxEmojiInLine + column])[0]);
                        nextStep();
                    }
                    if (isDigit(currentString)) {
                        try {
                            variables.get(variables.size() - 1).value = Integer.parseInt(currentString);
                        } catch (NumberFormatException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    else {
                        throw new Exception("A variable can only contain a number.");
                    }
                    currentString = "";
                    break;
                case FUNCTION:
                    break;
                case TEXT_OUTPUT:
                    outputting();
                    break;
                case EMPTY:
                    if (currentString.length() != 0) {
                        if (!isDigit(currentString) && isVariable(currentString)) {
                            stack.push(new Element(getVariableValue(currentString)));
                        }
                        else {
                            try {
                                stack.push(new Element(Integer.parseInt(currentString)));
                            } catch (NumberFormatException ex) {
                                throw new Exception("There is no such variable.");
                            }
                        }
                        currentString = "";
                    }
                    break;
                default:
                    currentString += Character.valueOf(Character.toChars(point)[0]);
                    break;
            }
            nextStep();
            point = matrix[line * maxEmojiInLine + column];
        }
    }
}