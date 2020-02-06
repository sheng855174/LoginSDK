package tw.playground.sheng;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import tw.playground.sheng.http.post.HttpsPostThread;
import tw.playground.sheng.http.post.ILoginHttpsPostAdapter;

public class MainActivity extends AppCompatActivity {
    private Button login_button;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playground_activity_main);

        //設定隱藏標題
        getSupportActionBar().hide();

        //綁定layout和程式邏輯變數
        login_button = findViewById(R.id.LoginButton);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);

        //設定監聽器
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILoginHttpsPostAdapter iLoginHttpsPostAdapter = new HttpsPostThread();
                iLoginHttpsPostAdapter.login(username.getText().toString(), password.getText().toString());
                finish();
            }
        });

    }
}
