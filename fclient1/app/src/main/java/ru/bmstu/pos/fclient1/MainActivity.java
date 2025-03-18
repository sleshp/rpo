package ru.bmstu.pos.fclient1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import org.apache.commons.io.IOUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import ru.bmstu.pos.fclient1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TransactionEvents {

    static {
        System.loadLibrary("fclient1");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;
    private String pin;
    private Button button;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        pin = result.getData().getStringExtra("pin");
                        synchronized (MainActivity.this) {
                            MainActivity.this.notifyAll();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация UI элементов
        button = findViewById(R.id.sample_button); // Замените на ваш ID кнопки
        TextView ta = findViewById(R.id.txtAmount);
        TextView tp = findViewById(R.id.txtPtc);

        // Инициализация генератора случайных чисел
        int res = initRng();
        if (res != 0) {
            showError("RNG initialization failed");
            return;
        }

        // Тест шифрования
        testEncryption();

        // Обработка входящих данных
        processIntentData(ta, tp);

        // Обработчик долгого нажатия кнопки
        button.setOnLongClickListener(view -> {
            processTransaction();
            return true;
        });

        button.setOnClickListener(v -> {
            testHttpClient();
        });

    }

    private void testEncryption() {
        byte[] key = randomBytes(16);
        byte[] data = randomBytes(16);

        if (key == null || data == null) {
            showError("Failed to generate random bytes");
            return;
        }

        byte[] encrypted = encrypt(key, data);
        byte[] decrypted = decrypt(key, encrypted);

        if (encrypted == null || decrypted == null) {
            showError("Encryption/decryption failed");
            return;
        }

        showResult("Key: " + bytesToHex(key));
        showResult("Original: " + bytesToHex(data));
        showResult("Encrypted: " + bytesToHex(encrypted));
        showResult("Decrypted: " + bytesToHex(decrypted));
    }

    private void processIntentData(TextView amountView, TextView attemptsView) {
        Intent intent = getIntent();
        if (intent != null) {
            // Обработка суммы
            String amount = intent.getStringExtra("amount");
            if (amount != null && amountView != null) {
                try {
                    long f = Long.parseLong(amount);
                    DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
                    amountView.setText("Сумма: " + df.format(f));
                } catch (NumberFormatException e) {
                    amountView.setText("Сумма: неверный формат");
                }
            }

            // Обработка попыток
            int pts = intent.getIntExtra("ptc", 0);
            if (attemptsView != null) {
                if (pts == 2) {
                    attemptsView.setText("Осталось две попытки");
                } else if (pts == 1) {
                    attemptsView.setText("Осталась одна попытка");
                }
            }
        }
    }

    private void processTransaction() {
        new Thread(() -> {
            try {
                byte[] trd = stringToHex("9F0206000000000100");
                boolean result = transaction(trd);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Transaction " + (result ? "successful" : "failed"),
                        Toast.LENGTH_SHORT).show());
            } catch (Exception ex) {
                Log.e("MainActivity.transaction", ex.getMessage());
            }
        }).start();
    }

    public void onButtonClick(View v) {
        Intent it = new Intent(this, PinpadActivity.class);
        it.putExtra("amount", "10000"); // Здесь должна быть ваша реальная сумма
        it.putExtra("ptc", 3); // Количество попыток
        activityResultLauncher.launch(it);
    }


    private void showResult(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }

    private String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    public static byte[] stringToHex(String s) {
        if (s == null) return null;
        try {
            return Hex.decodeHex(s.toCharArray());
        } catch (DecoderException ex) {
            return null;
        }
    }

    @Override
    public String enterPin(int ptc, String amount) {
        pin = "";
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                Log.println(Log.ERROR, "MainActivity.enterPin", ex.getMessage());
            }
        }
        return pin;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    protected void testHttpClient()
    {
        new Thread(() -> {
            try {
                HttpURLConnection uc = (HttpURLConnection)
                        (new URL("http://10.0.2.2:8081/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);
                runOnUiThread(() ->
                {
                    Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                });

            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }

    protected String getPageTitle(String html)
    {
        Pattern pattern = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String p;
        if (matcher.find())
            p = matcher.group(1);
        else
            p = "Not found";
        return p;
    }

    // Нативные методы
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);
}