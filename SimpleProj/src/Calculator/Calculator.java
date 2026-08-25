import javax.swing.JFrame;//frame
import javax.swing.JTextField;//textfields
import javax.swing.SwingConstants;//Alignment
import java.awt.Font;//fontssize
import javax.swing.JPanel;//container for buttons
import java.awt.GridLayout;//arrange our calc buttons
import javax.swing.JButton;//Calc Buttons
import java.awt.BorderLayout;//layouts
import java.util.ArrayList;//used for parsing the expression
import java.util.List;//used for parsing the expression 

public class Calculator {
    //static = shared by the class and accessible without creating an object.
    static String expression = "";

    public static void main(String[] args) throws Exception {

        JFrame window = new JFrame("Calculator");

        JTextField display = new JTextField();

        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setPreferredSize(new java.awt.Dimension(400, 80));//textfield size
        display.setFont(new Font("Arial", Font.PLAIN, 30)); //font plus font size
        display.setEditable(false);//user cannot input through keyboards

        JPanel buttonPanel = new JPanel();//container
        buttonPanel.setLayout(new GridLayout(0, 4));//grid rows x cols

        // creating and giving variable for button
        JButton buttonDelete = new JButton("⌫");
        buttonDelete.addActionListener(e -> {
            if (!expression.isEmpty()) {
                expression = expression.substring(0, expression.length() - 1);
                display.setText(expression);
            }
        });

        JButton buttonClear = new JButton("AC");
        buttonClear.addActionListener(e -> {
            expression = "";
            display.setText(expression);
        });

        JButton buttonPercentage = new JButton("%");
        buttonPercentage.addActionListener(e -> {
            int idx = lastOperatorIndex(expression);
            String segment = expression.substring(idx + 1);
            if (!segment.isEmpty()) {
                try {
                    double value = Double.parseDouble(segment) / 100.0;
                    expression = expression.substring(0, idx + 1) + formatResult(value);
                    display.setText(expression);
                } catch (NumberFormatException ex) {
                    // ignore invalid segment
                }
            }
        });

        JButton buttonDivide = new JButton("÷");
        buttonDivide.addActionListener(e -> {
            expression = expression + "÷";
            display.setText(expression);
        });

        JButton button7 = new JButton("7");
        button7.addActionListener(e -> {
            expression = expression + "7";
            display.setText(expression);
        });

        JButton button8 = new JButton("8");
        button8.addActionListener(e -> {
            expression = expression + "8";
            display.setText(expression);
        });

        JButton button9 = new JButton("9");
        button9.addActionListener(e -> {
            expression = expression + "9";
            display.setText(expression);
        });

        JButton buttonMultiplication = new JButton("×");
        buttonMultiplication.addActionListener(e -> {
            expression = expression + "×";
            display.setText(expression);
        });

        JButton button6 = new JButton("6");
        button6.addActionListener(e -> {
            expression = expression + "6";
            display.setText(expression);
        });

        JButton button5 = new JButton("5");
        button5.addActionListener(e -> {
            expression = expression + "5";
            display.setText(expression);
        });

        JButton button4 = new JButton("4");
        button4.addActionListener(e -> {
            expression = expression + "4";
            display.setText(expression);
        });

        JButton buttonSubtraction = new JButton("-");
        buttonSubtraction.addActionListener(e -> {
            expression = expression + "-";
            display.setText(expression);
        });

        JButton button3 = new JButton("3");
        button3.addActionListener(e -> {
            expression = expression + "3";
            display.setText(expression);
        });

        JButton button2 = new JButton("2");
        button2.addActionListener(e -> {
            expression = expression + "2";
            display.setText(expression);
        });

        JButton button1 = new JButton("1");
        button1.addActionListener(e -> {
            expression = expression + "1";
            display.setText(expression);
        });

        JButton buttonAddition = new JButton("+");
        buttonAddition.addActionListener(e -> {
            expression = expression + "+";
            display.setText(expression);
        });

        JButton buttonPlusMinus = new JButton("+/-");
        buttonPlusMinus.addActionListener(e -> {
            int idx = lastOperatorIndex(expression);
            String before = expression.substring(0, idx + 1);
            String segment = expression.substring(idx + 1);
            if (segment.startsWith("-")) {
                segment = segment.substring(1);
            } else if (!segment.isEmpty()) {
                segment = "-" + segment;
            }
            expression = before + segment;
            display.setText(expression);
        });

        JButton button0 = new JButton("0");
        button0.addActionListener(e -> {
            expression = expression + "0";
            display.setText(expression);
        });

        JButton buttonPoint = new JButton(".");
        buttonPoint.addActionListener(e -> {
            int idx = lastOperatorIndex(expression);
            String segment = expression.substring(idx + 1);
            if (!segment.contains(".")) {
                expression = expression + (segment.isEmpty() ? "0." : ".");
                display.setText(expression);
            }
        });

        JButton buttonEquals = new JButton("=");
        buttonEquals.addActionListener(e -> {
            if (!expression.isEmpty()) {
                try {
                    double result = calculateExpression(expression);
                    expression = formatResult(result);
                    display.setText(expression);
                } catch (Exception ex) {
                    display.setText("Error");
                    expression = "";
                }
            }
        });

        // put inside button panels
        buttonPanel.add(buttonDelete);
        buttonPanel.add(buttonClear);
        buttonPanel.add(buttonPercentage);
        buttonPanel.add(buttonDivide);

        buttonPanel.add(button7);
        buttonPanel.add(button8);
        buttonPanel.add(button9);
        buttonPanel.add(buttonMultiplication);

        buttonPanel.add(button4);
        buttonPanel.add(button5);
        buttonPanel.add(button6);
        buttonPanel.add(buttonSubtraction);

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(buttonAddition);

        buttonPanel.add(buttonPlusMinus);
        buttonPanel.add(button0);
        buttonPanel.add(buttonPoint);
        buttonPanel.add(buttonEquals);

        window.setLayout(new BorderLayout());
        window.add(display, BorderLayout.NORTH);//textfield
        window.add(buttonPanel, BorderLayout.CENTER);//center button panel

        window.setSize(400, 500); //framesize
        window.setResizable(false); //make frame not resizeable
        window.setLocationRelativeTo(null); //pop up in the middle
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //close program when window closes
        window.setVisible(true); //make the window visible
    }

    // Finds the index of the last *binary* operator in the expression, so the
    // digit segment being typed right now can be isolated (used by +/-, %, and .).
    // A '-' right at the start, or right after another operator, is treated as a
    // sign rather than a binary operator.
    static int lastOperatorIndex(String expr) {
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == '+' || c == '×' || c == '÷') {
                return i;
            }
            if (c == '-') {
                boolean isSign = (i == 0) || isOperator(expr.charAt(i - 1));
                if (!isSign) {
                    return i;
                }
            }
        }
        return -1;
    }

    static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }

    // Formats a double for display: whole numbers are shown without a
    // trailing ".0".
    static String formatResult(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    static double calculateExpression(String expression) {
        // 1) Tokenize into numbers and operators, treating a leading '-' or a
        //    '-' that follows another operator as part of the number (unary sign).
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        StringBuilder currentNumber = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else if (c == '-' && (currentNumber.length() == 0)
                    && (numbers.isEmpty() || !operators.isEmpty() && operators.size() == numbers.size())) {
                // unary minus: starts a new (negative) number
                currentNumber.append(c);
            } else if (isOperator(c)) {
                if (currentNumber.length() > 0) {
                    numbers.add(Double.parseDouble(currentNumber.toString()));
                    currentNumber.setLength(0);
                }
                operators.add(c);
            }
        }
        if (currentNumber.length() > 0) {
            numbers.add(Double.parseDouble(currentNumber.toString()));
        }

        if (numbers.isEmpty()) {
            return 0;
        }

        // 2) First pass: collapse × and ÷ left-to-right, since they have
        //    higher precedence than + and -.
        List<Double> reducedNumbers = new ArrayList<>();
        List<Character> reducedOperators = new ArrayList<>();

        reducedNumbers.add(numbers.get(0));
        for (int i = 0; i < operators.size() && i + 1 < numbers.size(); i++) {
            char op = operators.get(i);
            double nextNumber = numbers.get(i + 1);
            int lastIndex = reducedNumbers.size() - 1;

            if (op == '×') {
                reducedNumbers.set(lastIndex, reducedNumbers.get(lastIndex) * nextNumber);
            } else if (op == '÷') {
                reducedNumbers.set(lastIndex, reducedNumbers.get(lastIndex) / nextNumber);
            } else {
                reducedOperators.add(op);
                reducedNumbers.add(nextNumber);
            }
        }

        // 3) Second pass: sum everything left-to-right using + and -.
        double result = reducedNumbers.get(0);
        for (int i = 0; i < reducedOperators.size(); i++) {
            char op = reducedOperators.get(i);
            double nextNumber = reducedNumbers.get(i + 1);
            if (op == '+') {
                result += nextNumber;
            } else if (op == '-') {
                result -= nextNumber;
            }
        }

        return result;
    }
}