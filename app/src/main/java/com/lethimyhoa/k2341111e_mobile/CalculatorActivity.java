package com.lethimyhoa.k2341111e_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculatorActivity extends AppCompatActivity {

    Button btnCalculate, btnBackspace, btnChange;
    EditText edtFormula;
    TextView txtMC, txtMR, txtMPlus, txtMMinus, txtMS, txtM;
    View.OnClickListener click_m_listener;
    double memoryValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        //anonymous listeners
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processFormula();
            }
        });


//MC (Memory Clear): Xóa trắng giá trị trong bộ nhớ (đưa về 0).
//•
//MR (Memory Recall): Lấy giá trị đang lưu trong bộ nhớ hiển thị lên màn hình.
//•
//M+ (Memory Plus): Cộng dồn giá trị đang có trên màn hình vào bộ nhớ.
//•
//M- (Memory Minus): Trừ giá trị trên màn hình khỏi bộ nhớ.
//•
//MS (Memory Store): Lưu giá trị trên màn hình vào bộ nhớ (thay thế giá trị cũ).


        click_m_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double currentValue = 0;
                try {
                    String s = edtFormula.getText().toString();
                    if (!s.isEmpty()) currentValue = Double.parseDouble(s);
                } catch (Exception e) {}

                //process later
                if (view.equals(txtM)) {
                    // Khi nhấn vào chữ M, thực hiện gọi lại giá trị bộ nhớ (giống MR)
                    edtFormula.setText(formatResult(memoryValue));
                    edtFormula.setSelection(edtFormula.getText().length());
                } else if (view.equals(txtMC)) {
                    memoryValue = 0;
                } else if (view.equals(txtMR)) {
                    edtFormula.setText(formatResult(memoryValue));
                    edtFormula.setSelection(edtFormula.getText().length());
                } else if (view.equals(txtMPlus)) {
                    memoryValue += currentValue;
                } else if (view.equals(txtMMinus)) {
                    memoryValue -= currentValue;
                } else if (view.equals(txtMS)) {
                    memoryValue = currentValue;
                }
                //k đc if view == txtMC vì nó đang so sánh ô nhớ trên RAM
            }
        };
        //các biến cùng dùng chung sự kiện, và phải biết ai đang dùng
        txtMC.setOnClickListener(click_m_listener);
        txtMR.setOnClickListener(click_m_listener);
        txtMPlus.setOnClickListener(click_m_listener);
        txtMMinus.setOnClickListener(click_m_listener);
        txtMS.setOnClickListener(click_m_listener);
        txtM.setOnClickListener(click_m_listener);
    }

    private void processFormula() {
        // Step 1: get formula
        // EditText edtFormula = findViewById(R.id.edtFormula);
        String formula = edtFormula.getText().toString();

        if (formula.isEmpty()) return;

        try {
            // Step 2: Basic Calculation Logic (Linear)
            // Split by operators but keep delimiters for identifying actions
            // This regex splits the string into numbers and operators
            String[] tokens = formula.split("(?<=[-+*/])|(?=[-+*/])");

            if (tokens.length < 3) return; // Need at least: number, operator, number

            double resultValue = Double.parseDouble(tokens[0].trim());

            for (int i = 1; i < tokens.length; i += 2) {
                String op = tokens[i].trim();
                if (i + 1 >= tokens.length) break;
                double nextValue = Double.parseDouble(tokens[i + 1].trim());

                switch (op) {
                    case "+": resultValue += nextValue; break;
                    case "-": resultValue -= nextValue; break;
                    case "*": resultValue *= nextValue; break;
                    case "/":
                        if (nextValue != 0) resultValue /= nextValue;
                        else {
                            edtFormula.setError("Divide by zero");
                            return;
                        }
                        break;
                }
            }

            // Step 3: show result
            edtFormula.setText(formatResult(resultValue));
            edtFormula.setSelection(edtFormula.getText().length());

        } catch (Exception e) {
            edtFormula.setError("Invalid format");
        }
    }

    private void addViews() {
        btnBackspace = findViewById(R.id.btnBackspace);
        btnChange = findViewById(R.id.btnChange);
        btnCalculate = findViewById(R.id.btnCalculate);
        edtFormula = findViewById(R.id.edtFormula);
        txtMC = findViewById(R.id.txtMC);
        txtMR = findViewById(R.id.txtMR);
        txtMPlus = findViewById(R.id.txtMPlus);
        txtMMinus = findViewById(R.id.txtMMinus);
        txtMS = findViewById(R.id.txtMS);
        txtM = findViewById(R.id.txtM);
    }

    public void processChooseValue(View view) {
        // get current view clicked
        Button btn = (Button) view;
        //get edtFormula
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = btn.getText().toString();
        edtFormula.setText(old_value + new_value);
        edtFormula.setSelection(edtFormula.getText().length());
    }

    public void processBackspace(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = "";
        if (old_value.length() > 1) {
            //remove the last character:
            new_value = old_value.substring(0, old_value.length() - 1);
        }
        edtFormula.setText(new_value);
        edtFormula.setSelection(edtFormula.getText().length());
    }

    public void processPercentage(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = old_value;

        if (!old_value.isEmpty()) {
            try {
                // convert string to double and divide by 100
                double number = Double.parseDouble(old_value);
                new_value = formatResult(number / 100.0);
            } catch (Exception e) {
                // handle if formula is complex or invalid
                edtFormula.setError("Invalid format");
            }
        }
        edtFormula.setText(new_value);
        edtFormula.setSelection(edtFormula.getText().length());
    }

    public void processCE(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = "";

        if (!old_value.isEmpty()) {
            // Tìm vị trí của toán tử cuối cùng (+, -, *, /)
            int lastOpIndex = -1;
            for (int i = old_value.length() - 1; i >= 0; i--) {
                char c = old_value.charAt(i);
                if (c == '+' || c == '-' || c == '*' || c == '/') {
                    lastOpIndex = i;
                    break;
                }
            }

            if (lastOpIndex != -1) {
                // Giữ lại phần trước và bao gồm cả toán tử
                new_value = old_value.substring(0, lastOpIndex + 1);
            } else {
                // Nếu không có toán tử, xóa sạch (Clear Entry duy nhất)
                new_value = "";
            }
        }
        edtFormula.setText(new_value);
    }

    public void processC(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        // clear error if any
        edtFormula.setError(null);
        // clear all
        edtFormula.setText("");
    }

    public void processInverse(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = old_value;

        if (!old_value.isEmpty()) {
            try {
                // Convert to double
                double number = Double.parseDouble(old_value);

                // Check if number is not zero to avoid division by zero error
                if (number != 0) {
                    new_value = formatResult(1.0 / number);
                } else {
                    edtFormula.setError("Cannot divide by zero");
                }
            } catch (Exception e) {
                // Handle complex formulas or invalid input
                edtFormula.setError("Invalid format");
            }
        }
        // Show result
        edtFormula.setText(new_value);
        edtFormula.setSelection(edtFormula.getText().length());
    }

    public void processSquare(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = old_value;

        if (!old_value.isEmpty()) {
            try {
                // Convert to double
                double number = Double.parseDouble(old_value);

                // Calculate square (x * x)
                new_value = formatResult(number * number);
            } catch (Exception e) {
                // Handle if formula is complex or invalid input
                edtFormula.setError("Invalid format");
            }
        }
        // Show result
        edtFormula.setText(new_value);
        edtFormula.setSelection(edtFormula.getText().length());

    }

    public void processSqrt(View view) {
        Button btn = (Button) view;
        //EditText edtFormula = findViewById(R.id.edtFormula);
        String old_value = edtFormula.getText().toString();
        String new_value = old_value;

        if (!old_value.isEmpty()) {
            try {
                // Chuyển đổi sang kiểu double
                double number = Double.parseDouble(old_value);

                // Kiểm tra điều kiện số không âm
                if (number >= 0) {
                    new_value = formatResult(Math.sqrt(number));
                } else {
                    edtFormula.setError("Invalid input (negative number)");
                }
            } catch (Exception e) {
                // Xử lý nếu định dạng không hợp lệ (ví dụ chuỗi có toán tử)
                edtFormula.setError("Invalid format");
            }
        }
        // Hiển thị kết quả
        edtFormula.setText(new_value);
        edtFormula.setSelection(edtFormula.getText().length());
    }

    public void processChange(View view) {
        String old_value = edtFormula.getText().toString();
        if (old_value.isEmpty()) return;

        try {
            double value = Double.parseDouble(old_value);
            value = -value; // Đảo dấu
            edtFormula.setText(formatResult(value));
            edtFormula.setSelection(edtFormula.getText().length());
        } catch (Exception e) {
            edtFormula.setError("Can't change sign of formula");
        }
    }

    private String formatResult(double value) {
        String result = String.valueOf(value);
        if (result.endsWith(".0")) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }
}