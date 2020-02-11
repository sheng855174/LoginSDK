package tw.playground.sheng;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tw.playground.sheng.FloatingView.FloatingView;
import tw.playground.sheng.FloatingView.FloatingViewConfig;
import tw.playground.sheng.http.post.HttpsPostThread;
import tw.playground.sheng.http.post.ILoginHttpsPostAdapter;

public class MainActivity extends AppCompatActivity {
    private Button login_button;
    private ImageView imageView_twitter;
    private ImageView imageView_instagram;
    private ImageView imageView_facebook;
    private EditText username;
    private EditText password;
    private FloatingView floatingView;
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
        imageView_twitter = findViewById(R.id.imageView_twitter);
        imageView_instagram = findViewById(R.id.imageView_instagram);
        imageView_facebook = findViewById(R.id.imageView_facebook);

        //設定監聽器
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILoginHttpsPostAdapter iLoginHttpsPostAdapter = new HttpsPostThread();
                iLoginHttpsPostAdapter.login(username.getText().toString(), password.getText().toString());
                finish();
            }
        });
        imageView_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"this is twitter",Toast.LENGTH_LONG).show();
            }
        });
        imageView_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"this is instagram",Toast.LENGTH_LONG).show();
            }
        });
        imageView_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"this is facebook",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FloatingViewConfig config = new FloatingViewConfig.Builder().build();
        floatingView = new FloatingView(this, config);
        floatingView.showOverlayActivity();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (floatingView != null) {
            floatingView.hide();
        }
    }
}
