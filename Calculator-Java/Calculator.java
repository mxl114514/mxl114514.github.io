import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

public class Calculator extends JFrame {

    private JTextField display;
    private double result = 0;
    private String lastOperator = "=";
    private boolean startNewNumber = true;
    private StringBuilder currentInput = new StringBuilder("0");

    // Color scheme
    private static final Color BG_MAIN      = new Color(0x1e1e2e);
    private static final Color BG_BUTTONS   = new Color(0x2a2a3a);
    private static final Color BTN_DIGIT    = new Color(0x3a3a4a);
    private static final Color BTN_OP       = new Color(0xf5a97f);
    private static final Color BTN_EQUALS   = new Color(0xa6e3a1);
    private static final Color BTN_CLEAR    = new Color(0xf38ba8);
    private static final Color TEXT_WHITE   = Color.WHITE;
    private static final Color TEXT_OP      = new Color(0x1e1e2e);
    private static final Font  DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 28);
    private static final Font  BTN_FONT     = new Font("Segoe UI", Font.BOLD, 16);

    public Calculator() {
        setTitle("梦珞计算器 Java版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        // Main panel with rounded corners via custom painting
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_MAIN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(mainPanel);

        // Display
        display = new JTextField("0");
        display.setFont(DISPLAY_FONT);
        display.setForeground(TEXT_WHITE);
        display.setBackground(BG_MAIN);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x45475a), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        display.setOpaque(true);
        mainPanel.add(display, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_BUTTONS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(6, 4, 6, 6));
        buttonPanel.setBorder(new EmptyBorder(8, 4, 4, 4));

        // Buttons in order
        String[][] layout = {
            {"%",  "CE", "C",  "⌫"},
            {"1/x","x²", "√",  "÷"},
            {"7",  "8",  "9",  "×"},
            {"4",  "5",  "6",  "−"},
            {"1",  "2",  "3",  "+"},
            {"+/-", "0", ".",  "="}
        };

        for (String[] row : layout) {
            for (String label : row) {
                JButton btn = createButton(label);
                buttonPanel.add(btn);
            }
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Keyboard bindings
        setupKeyboardBindings();
    }

    private JButton createButton(String text) {
        JButton btn = new RoundedButton(text);
        btn.setFont(BTN_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);

        Color bg, fg = TEXT_WHITE;

        switch (text) {
            case "=":
                bg = BTN_EQUALS;
                fg = TEXT_OP;
                break;
            case "CE": case "C":
                bg = BTN_CLEAR;
                break;
            case "+": case "−": case "×": case "÷":
            case "%": case "1/x": case "x²": case "√":
                bg = BTN_OP;
                fg = TEXT_OP;
                break;
            default:
                bg = BTN_DIGIT;
                break;
        }

        btn.setBackground(bg);
        btn.setForeground(fg);

        btn.addActionListener(e -> handleButton(text));
        return btn;
    }

    private void setupKeyboardBindings() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // Digits
        for (int i = 0; i <= 9; i++) {
            final String digit = String.valueOf(i);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), "digit" + i);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), "digit" + i);
            am.put("digit" + i, new AbstractAction() {
                public void actionPerformed(ActionEvent e) { inputDigit(digit); }
            });
        }

        // Operators
        bindKey(im, am, KeyEvent.VK_ADD,      "op+",     () -> handleOperator("+"));
        bindKey(im, am, KeyEvent.VK_SUBTRACT,  "op-",     () -> handleOperator("−"));
        bindKey(im, am, KeyEvent.VK_MULTIPLY,  "op*",     () -> handleOperator("×"));
        bindKey(im, am, KeyEvent.VK_DIVIDE,    "op/",     () -> handleOperator("÷"));
        bindKey(im, am, KeyEvent.VK_SLASH,     "op/2",    () -> handleOperator("÷"));
        bindKey(im, am, KeyEvent.VK_ENTER,     "equals",  () -> handleButton("="));
        bindKey(im, am, KeyEvent.VK_ESCAPE,    "clear",   () -> handleButton("CE"));
        bindKey(im, am, KeyEvent.VK_BACK_SPACE,"bksp",    () -> handleButton("⌫"));
        bindKey(im, am, KeyEvent.VK_PERIOD,    "dot",     () -> handleButton("."));
        bindKey(im, am, KeyEvent.VK_DECIMAL,   "dot2",    () -> handleButton("."));
        bindKey(im, am, KeyEvent.VK_DELETE,    "del",     () -> handleButton("C"));

        // Shift+5 => %
        KeyStroke pctStroke = KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.SHIFT_DOWN_MASK);
        im.put(pctStroke, "pctShift");
        am.put("pctShift", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { handleButton("%"); }
        });

        // Shift+8 => *
        KeyStroke starStroke = KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.SHIFT_DOWN_MASK);
        im.put(starStroke, "starShift");
        am.put("starShift", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { handleOperator("×"); }
        });

        // Shift+= => +
        KeyStroke plusStroke = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.SHIFT_DOWN_MASK);
        im.put(plusStroke, "plusShift");
        am.put("plusShift", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { handleOperator("+"); }
        });
    }

    private void bindKey(InputMap im, ActionMap am, int keyCode, String name, Runnable action) {
        im.put(KeyStroke.getKeyStroke(keyCode, 0), name);
        am.put(name, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    private void handleButton(String text) {
        switch (text) {
            case "CE":
                result = 0;
                lastOperator = "=";
                startNewNumber = true;
                currentInput = new StringBuilder("0");
                display.setText("0");
                break;
            case "C":
                // Clear current entry only
                currentInput = new StringBuilder("0");
                startNewNumber = true;
                display.setText("0");
                break;
            case "⌫":
                if (currentInput.length() > 1) {
                    currentInput.deleteCharAt(currentInput.length() - 1);
                } else {
                    currentInput = new StringBuilder("0");
                    startNewNumber = true;
                }
                display.setText(formatDisplay(currentInput.toString()));
                break;
            case "+": case "−": case "×": case "÷":
                handleOperator(text);
                break;
            case "=":
                calculate();
                lastOperator = "=";
                startNewNumber = true;
                break;
            case "%":
                try {
                    double val = Double.parseDouble(currentInput.toString());
                    val = val / 100.0;
                    currentInput = new StringBuilder(formatNumber(val));
                    display.setText(currentInput.toString());
                } catch (NumberFormatException ignored) {}
                break;
            case "+/-":
                if (currentInput.charAt(0) == '-') {
                    currentInput.deleteCharAt(0);
                } else if (!currentInput.toString().equals("0")) {
                    currentInput.insert(0, '-');
                }
                display.setText(formatDisplay(currentInput.toString()));
                break;
            case ".":
                if (startNewNumber) {
                    currentInput = new StringBuilder("0.");
                    startNewNumber = false;
                } else if (currentInput.indexOf(".") < 0) {
                    currentInput.append(".");
                }
                display.setText(currentInput.toString());
                break;
            case "1/x":
                try {
                    double val = Double.parseDouble(currentInput.toString());
                    if (val != 0) {
                        val = 1.0 / val;
                        currentInput = new StringBuilder(formatNumber(val));
                        display.setText(currentInput.toString());
                    } else {
                        display.setText("错误");
                        currentInput = new StringBuilder("0");
                        startNewNumber = true;
                    }
                } catch (NumberFormatException ignored) {}
                break;
            case "x²":
                try {
                    double val = Double.parseDouble(currentInput.toString());
                    val = val * val;
                    currentInput = new StringBuilder(formatNumber(val));
                    display.setText(currentInput.toString());
                } catch (NumberFormatException ignored) {}
                break;
            case "√":
                try {
                    double val = Double.parseDouble(currentInput.toString());
                    if (val >= 0) {
                        val = Math.sqrt(val);
                        currentInput = new StringBuilder(formatNumber(val));
                        display.setText(currentInput.toString());
                    } else {
                        display.setText("错误");
                        currentInput = new StringBuilder("0");
                        startNewNumber = true;
                    }
                } catch (NumberFormatException ignored) {}
                break;
            default:
                // Digits
                inputDigit(text);
                break;
        }
    }

    private void inputDigit(String digit) {
        if (startNewNumber) {
            currentInput = new StringBuilder(digit);
            startNewNumber = false;
        } else {
            if (currentInput.toString().equals("0")) {
                currentInput = new StringBuilder(digit);
            } else {
                currentInput.append(digit);
            }
        }
        display.setText(formatDisplay(currentInput.toString()));
    }

    private void handleOperator(String op) {
        if (!startNewNumber) {
            calculate();
        }
        lastOperator = op;
        startNewNumber = true;
    }

    private void calculate() {
        try {
            double current = Double.parseDouble(currentInput.toString());
            switch (lastOperator) {
                case "=":
                    result = current;
                    break;
                case "+":
                    result += current;
                    break;
                case "−":
                    result -= current;
                    break;
                case "×":
                    result *= current;
                    break;
                case "÷":
                    if (current != 0) {
                        result /= current;
                    } else {
                        display.setText("错误");
                        result = 0;
                        lastOperator = "=";
                        currentInput = new StringBuilder("0");
                        startNewNumber = true;
                        return;
                    }
                    break;
            }
            currentInput = new StringBuilder(formatNumber(result));
            display.setText(currentInput.toString());
        } catch (NumberFormatException e) {
            display.setText("错误");
            currentInput = new StringBuilder("0");
            startNewNumber = true;
        }
    }

    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        // Limit decimal places to avoid display overflow
        String s = String.valueOf(value);
        if (s.length() > 14) {
            return String.format("%.10g", value);
        }
        return s;
    }

    private String formatDisplay(String s) {
        // Truncate for display
        if (s.length() > 14) {
            return s.substring(0, 14);
        }
        return s;
    }

    // Custom rounded button
    static class RoundedButton extends JButton {
        RoundedButton(String text) { super(text); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();

            // Center text manually
            FontMetrics fm = g.getFontMetrics();
            Rectangle r = getBounds();
            int x = (r.width - fm.stringWidth(getText())) / 2;
            int y = (r.height - fm.getHeight()) / 2 + fm.getAscent();
            g.setColor(getForeground());
            g.setFont(getFont());
            g.drawString(getText(), x, y);
        }

        @Override
        protected void paintBorder(Graphics g) { /* flat, no border */ }

        @Override
        public boolean isOpaque() { return false; }
    }

    public static void main(String[] args) {
        // FlatLaf look for modern feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Calculator calc = new Calculator();
            calc.setVisible(true);
        });
    }
}
