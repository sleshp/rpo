package ru.bmstu.pos.fclient1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class PinpadActivity extends AppCompatActivity {

    private TextView tvAmount, tvPin, tvPtc;
    private String pin = "";
    private int remainingAttempts = 3;
    private final int MAX_KEYS = 10;
    private static final String CORRECT_PIN = "1234"; // Замените на ваш реальный PIN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);

        initViews();
        processIntentData();
        setupButtons();
        ShuffleKeys();
    }

    private void initViews() {
        tvAmount = findViewById(R.id.txtAmount);
        tvPin = findViewById(R.id.txtPin);
        tvPtc = findViewById(R.id.txtPtc);
    }

    private void processIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Обработка суммы
            String amountStr = intent.getStringExtra("amount");
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    formatAndDisplayAmount(amountStr);
                } catch (NumberFormatException e) {
                    tvAmount.setText("Сумма: ошибка формата");
                }
            }

            // Получаем начальное количество попыток из Intent
            remainingAttempts = intent.getIntExtra("ptc", 3);
            updateAttemptsDisplay();
        }
    }

    private void formatAndDisplayAmount(String amountStr) {
        long amount = Long.parseLong(amountStr);
        double amountRub = amount / 100.0;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        tvAmount.setText(String.format("Сумма: %s ₽", nf.format(amountRub)));
    }

    private void setupButtons() {
        // Цифровые кнопки 0-9
        for (int i = 0; i <= 9; i++) {
            int resId = getResources().getIdentifier("btnKey" + i, "id", getPackageName());
            findViewById(resId).setOnClickListener(this::onDigitButtonClick);
        }

        // Кнопка сброса
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            pin = "";
            updatePinDisplay();
        });

        // Кнопка OK
        findViewById(R.id.btnOK).setOnClickListener(this::onOkButtonClick);
    }

    private void onDigitButtonClick(View v) {
        if (pin.length() < 4) {
            Button btn = (Button) v;
            pin += btn.getText().toString();
            updatePinDisplay();
        }
    }

    private void onOkButtonClick(View v) {
        if (pin.length() != 4) {
            Toast.makeText(this, "Введите 4 цифры", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.equals(CORRECT_PIN)) {
            returnSuccess();
        } else {
            handleWrongPin();
        }
    }

    private void returnSuccess() {
        Intent result = new Intent();
        result.putExtra("pin", pin);
        Toast.makeText(this, "PIN-код введен верно", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, result);
        finish();
    }

    private void handleWrongPin() {
        remainingAttempts--;
        updateAttemptsDisplay();

        if (remainingAttempts <= 0) {
            Toast.makeText(this, "Попытки закончились", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else {
            pin = "";
            updatePinDisplay();
            Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePinDisplay() {
        tvPin.setText("•".repeat(pin.length()));
    }

    private void updateAttemptsDisplay() {
        String text;
        if (remainingAttempts >= 3) {
            text = "Осталось 3 попытки";
        } else if (remainingAttempts == 2) {
            text = "Осталось 2 попытки";
        } else if (remainingAttempts == 1) {
            text = "Осталась 1 попытка";
        } else {
            text = "Попытки закончились";
        }
        tvPtc.setText(text);
    }

    protected void ShuffleKeys() {
        Button[] keys = {
                findViewById(R.id.btnKey0),
                findViewById(R.id.btnKey1),
                findViewById(R.id.btnKey2),
                findViewById(R.id.btnKey3),
                findViewById(R.id.btnKey4),
                findViewById(R.id.btnKey5),
                findViewById(R.id.btnKey6),
                findViewById(R.id.btnKey7),
                findViewById(R.id.btnKey8),
                findViewById(R.id.btnKey9)
        };

        byte[] rnd = MainActivity.randomBytes(MAX_KEYS);
        for (int i = 0; i < MAX_KEYS; i++) {
            int idx = (rnd[i] & 0xFF) % 10;
            CharSequence txt = keys[idx].getText();
            keys[idx].setText(keys[i].getText());
            keys[i].setText(txt);
        }
    }
}