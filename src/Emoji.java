import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

// Класс элементов, находящихся в стеке
class Element {
    private int value;
    private boolean flag;

    public Element() {
        value = 0;
        flag = false;
    }

    public Element(int value) {
        this.value = value;
        if (value != 0) flag = true;
        else flag = false;
    }

    int getValue() {
        return this.value;
    }

    boolean isFlag() {
        return this.flag;
    }
}

public class Emoji {
    private int maxEmojiInLine = 0;
    private int countOfLine = 0;
    private Cursor cursor;
    private int[] matrix;
    private String currentString = "";
    private Stack<Element> stack = new Stack<>();
    private Stack<Cursor> function = new Stack<>();
    private ArrayList<Referred> variables = new ArrayList<>();

    // Проверка - является ли данная строка числом
    private boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка - существует ли такая переменная
    private ReferredType isVariable(String s) {
        if (variables.stream().filter(x -> x.getName().compareTo(s) == 0).count() == 0) {
            return ReferredType.ANOTHER;
        }
        return variables.stream().filter(x -> x.getName().compareTo(s) == 0).findFirst().get().getType();
    }

    // Метод передвижения
    private void moving(int codeOfOperation) {
        switch (codeOfOperation) {
            case Operator.MOVE_UP:
                cursor.setUp();
                break;
            case Operator.MOVE_RIGHT:
                cursor.setRight();
                break;
            case Operator.MOVE_DOWN:
                cursor.setDown();
                break;
            case Operator.MOVE_LEFT:
                cursor.setLeft();
                break;
            case Operator.MOVE_RIGHT_IF_TRUE:
                if (stack.pop().isFlag()) cursor.setRight();
                else cursor.setLeft();
                break;
            case Operator.MOVE_DOWN_IF_TRUE:
                if (stack.pop().isFlag()) cursor.setDown();
                else cursor.setUp();
                break;
        }
    }

    // Метод арифметических операций
    private void arithmeticOperating(int codeOfOperation) {
        int secondElement = stack.pop().getValue(), firstElement = stack.pop().getValue();
        switch (codeOfOperation) {
            case Operator.ADDITION:
                stack.push(new Element(firstElement + secondElement));
                break;
            case Operator.SUBTRACTION:
                stack.push(new Element(firstElement - secondElement));
                break;
            case Operator.MULTIPLICATION:
                stack.push(new Element(firstElement * secondElement));
                break;
            case Operator.DIVISION:
                stack.push(new Element(firstElement / secondElement));
                break;
            case Operator.MODULO:
                stack.push(new Element(firstElement % secondElement));
                break;
        }
    }

    // Метод операторов сравнения
    private void comparing(int codeOfOperation) {
        int secondElement = stack.pop().getValue(), firstElement = stack.pop().getValue();
        switch (codeOfOperation) {
            case Operator.EQUALLY:
                stack.push(new Element(firstElement == secondElement ? 1 : 0));
                break;
            case Operator.MORE:
                stack.push(new Element(firstElement > secondElement ? 1 : 0));
                break;
            case Operator.LESS:
                stack.push(new Element(firstElement < secondElement ? 1 : 0));
                break;
        }
    }

    // Метод логических операций
    private void logisting(int codeOfOperation) {
        boolean firstElement = stack.pop().isFlag();
        switch (codeOfOperation) {
            case Operator.NOT:
                stack.push(new Element(firstElement ? 0 : 1));
                break;
            case Operator.AND:
                stack.push(new Element((firstElement && stack.pop().isFlag()) ? 1 : 0));
                break;
            case Operator.OR:
                stack.push(new Element((firstElement || stack.pop().isFlag()) ? 1 : 0));
                break;
        }
    }

    // Метод вывода текста в консоль
    private void outputting() throws Exception {
        String currentText = "", text = "";
        cursor.nextStep();

        // Считываем всё, что нужно вывести
        while (cursor.getCursor() != Operator.TEXT_OUTPUT) {
            if (cursor.getCursor() == Operator.STOP_SYMBOL) {
                throw new Exception("It expected a variable name or operator, but the cursor is outside the program.");
            }

            // Считываем текст
            if (cursor.getCursor() == Operator.WORK_WITH_TEXT) {
                if (!text.isEmpty()) {
                    if (isVariable(text) == ReferredType.VARIABLE) {
                        currentText += getVariableValue(text);
                    }
                    else if (isVariable(text) == ReferredType.FUNCTION) {
                        throw new Exception("A variable was expected, but a function was encountered.");
                    }
                    else {
                        throw new Exception("There is no such variable name, or the variable is not specified.");
                    }
                    text = "";
                }
                cursor.nextStep();
                while (cursor.getCursor() != Operator.WORK_WITH_TEXT) {
                    if (cursor.getCursor() == Operator.STOP_SYMBOL) {
                        throw new Exception("It expected a variable name, but the cursor is outside the program.");
                    }
                    currentText += Character.valueOf(Character.toChars(cursor.getCursor())[0]);
                    cursor.nextStep();
                }
            }
            else {
                // Считывание переменного
                text += Character.valueOf(Character.toChars(cursor.getCursor())[0]);
            }
            cursor.nextStep();
        }
        if (!text.isEmpty()) {
            if (isVariable(text) == ReferredType.VARIABLE) {
                currentText += getVariableValue(text);
            }
            else if (isVariable(text) == ReferredType.FUNCTION) {
                throw new Exception("The function cannot be called during console output.");
            }
            else {
                throw new Exception("There is no such variable name.");
            }
        }
        System.out.println(currentText);
    }

    // Метод получения значения переменного
    private int getVariableValue(String s) {
        return ((Variable) variables.stream().filter(x -> x.getName().compareTo(s) == 0).findFirst().get()).getValue();
    }

    // Метод получения указателя функции
    private Cursor getFunctionCursor(String s) {
        return ((Function) variables.stream().filter(x -> x.getName().compareTo(s) == 0).findFirst().get()).getCursor();
    }

    public void main(String[] args) throws Exception {
        // Считываем все эмоджи в одномерную матрицу из файла
        try
        {
            // Проверка на наличие названия файла с программой в аргументах
            if (args.length == 0) {
                throw new Exception("The file with the program is not specified.");
            }
            String filename = args[0];
            // Поиск количества строк, максимального числа эмоджи в строке
            Scanner scan = new Scanner(new FileReader(filename));
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
            scan = new Scanner(new FileReader(filename));
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
                    matrix[i * maxEmojiInLine + j] = Operator.EMPTY;
                }
            }
            scan.close();

        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }

        cursor = new Cursor(matrix, countOfLine, maxEmojiInLine);
        // Перебор вариантов эмоджи, которые содержит point
        int point = cursor.getCursor();
        while (point != Operator.END_OF_PROGRAM) {
            switch (point) {
                case Operator.MOVE_UP:
                case Operator.MOVE_RIGHT:
                case Operator.MOVE_DOWN:
                case Operator.MOVE_LEFT:
                case Operator.MOVE_RIGHT_IF_TRUE:
                case Operator.MOVE_DOWN_IF_TRUE:
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    moving(point);
                    break;
                case Operator.ADDITION:
                case Operator.SUBTRACTION:
                case Operator.MULTIPLICATION:
                case Operator.DIVISION:
                case Operator.MODULO:
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    arithmeticOperating(point);
                    break;
                case Operator.EQUALLY:
                case Operator.MORE:
                case Operator.LESS:
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    comparing(point);
                    break;
                case Operator.NOT:
                case Operator.AND:
                case Operator.OR:
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    logisting(point);
                    break;
                case Operator.VARIABLE:
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    cursor.nextStep();
                    cursor.nextStep();

                    // Получаем название переменного
                    while (cursor.getCursor() != Operator.EMPTY) {
                        if (cursor.getCursor() == Operator.STOP_SYMBOL) {
                            throw new Exception("It expected a variable name, but the cursor is outside the program.");
                        }
                        currentString += Character.valueOf(Character.toChars(cursor.getCursor())[0]);
                        cursor.nextStep();
                    }
                    if (currentString.isEmpty()) {
                        throw new Exception("Variable name not specified.");
                    }
                    if (isDigit(currentString)) {
                        throw new Exception("The variable name cannot be a number.");
                    }
                    if (isVariable(currentString) == ReferredType.FUNCTION) {
                        throw new Exception("A function with the same name has already been initialized.");
                    }
                    if (isVariable(currentString) != ReferredType.VARIABLE) {
                        variables.add(new Variable(currentString, 0));
                    }
                    currentString = "";
                    cursor.nextStep();

                    // Получаем значение переменного
                    if (cursor.getCursor() == Operator.STACK_PEEK) {
                        if (!stack.isEmpty()) {
                            currentString = Integer.toString(stack.peek().getValue());
                        }
                        else {
                            throw new Exception("Stack is empty.");
                        }
                    }
                    else while (cursor.getCursor() != Operator.EMPTY) {
                        if (cursor.getCursor() == Operator.STOP_SYMBOL) {
                            throw new Exception("It expected a variable name, but the cursor is outside the program.");
                        }
                        currentString += Character.valueOf(Character.toChars(cursor.getCursor())[0]);
                        cursor.nextStep();
                    }
                    if (isDigit(currentString)) {
                        try {
                            ((Variable) variables.get(variables.size() - 1)).setValue(Integer.parseInt(currentString));
                        } catch (NumberFormatException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    else {
                        throw new Exception("A variable can only contain a number.");
                    }
                    currentString = "";
                    break;
                case Operator.FUNCTION:
                    // Если указатель уже выполняет функцию, то при встрече оператора считать, что это выход из функции
                    if (!function.empty()) {
                        cursor = function.pop();
                        break;
                    }
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    cursor.nextStep();

                    // Получаем название функции
                    while (cursor.getCursor() != Operator.EMPTY) {
                        if (cursor.getCursor() == Operator.STOP_SYMBOL) {
                            throw new Exception("It expected a function name, but the cursor is outside the program.");
                        }
                        currentString += Character.valueOf(Character.toChars(cursor.getCursor())[0]);
                        cursor.nextStep();
                    }
                    if (currentString.isEmpty()) {
                        throw new Exception("Function name not specified.");
                    }
                    if (isDigit(currentString)) {
                        throw new Exception("The function name cannot be a number.");
                    }
                    if (isVariable(currentString) != ReferredType.ANOTHER) {
                        throw new Exception("A variable or function with the same name has already been initialized.");
                    }
                    variables.add(new Function(currentString, cursor));
                    currentString = "";
                    cursor.nextStep();
                    while (cursor.getCursor() != Operator.FUNCTION) {
                        if (cursor.getCursor() == Operator.STOP_SYMBOL) {
                            throw new Exception("It expected an operator, but the cursor is outside the program.");
                        }
                        switch (cursor.getCursor()) {
                            case Operator.MOVE_UP:
                                cursor.setUp();
                                break;
                            case Operator.MOVE_RIGHT:
                            case Operator.MOVE_RIGHT_IF_TRUE:
                                cursor.setRight();
                                break;
                            case Operator.MOVE_DOWN:
                            case Operator.MOVE_DOWN_IF_TRUE:
                                cursor.setDown();
                                break;
                            case Operator.MOVE_LEFT:
                                cursor.setLeft();
                                break;
                        }
                        cursor.nextStep();
                    }
                    break;
                case Operator.TEXT_OUTPUT:
                    if (!currentString.isEmpty()) {
                        throw new Exception("Was expected empty after '" + currentString + "' but met operator.");
                    }
                    outputting();
                    break;
                case Operator.STACK_PEEK:
                    if (!stack.isEmpty()) {
                        currentString = Integer.toString(stack.peek().getValue());
                    }
                    else {
                        throw new Exception("Stack is empty.");
                    }
                    break;
                case Operator.STACK_DELETE_LAST:
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                    else {
                        throw new Exception("Stack is empty.");
                    }
                    break;
                case Operator.EMPTY:
                    if (!currentString.isEmpty()) {
                        if (isVariable(currentString) == ReferredType.VARIABLE) {
                            stack.push(new Element(getVariableValue(currentString)));
                        }
                        else if (isVariable(currentString) == ReferredType.FUNCTION) {
                            // Функция вызвана - начинаем выполнение
                            function.push(cursor);
                            cursor = getFunctionCursor(currentString);
                        }
                        else if (isDigit(currentString)) {
                            try {
                                stack.push(new Element(Integer.parseInt(currentString)));
                            } catch (NumberFormatException ex) {
                                throw new Exception("There is no such variable.");
                            }
                        }
                        else {
                            throw new Exception("Variable or function name expected but met '" + currentString + "'.");
                        }
                        currentString = "";
                    }
                    break;
                case Operator.STOP_SYMBOL:
                    throw new Exception("The operator is expected, but the cursor is outside the program.");
                default:
                    currentString += Character.valueOf(Character.toChars(point)[0]);
                    break;
            }
            cursor.nextStep();
            point = cursor.getCursor();
        }
    }
}
