package com.example.smartlight_final;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.smartlight_final.databinding.ActivityMainBinding;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private CoordinatorLayout rootLayout;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.root_layout);
    }

    public void turnLightOn(View view) {
        sendRequest("http://20.93.3.161:3001/set-light-on");
        changeBackground(true);
    }

    public void turnLightOff(View view) {
        sendRequest("http://20.93.3.161:3001/set-light-off");
        changeBackground(false);
    }

    private void sendRequest(String urlString) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode(); // Trigger the request
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void changeBackground(boolean isLightOn) {
        if (isLightOn) {

            rootLayout.setBackgroundResource(R.drawable.background);
        } else {

            rootLayout.setBackgroundResource(R.drawable.background_off);
        }
    }
}

