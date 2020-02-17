package tw.playground.sheng;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import tw.playground.sheng.http.post.IRegHttpsPostAdapter;

public class MainActivity extends AppCompatActivity {
    private Button login_button;
    private ImageView imageView_twitter;
    private ImageView imageView_instagram;
    private ImageView imageView_facebook;
    private EditText username;
    private EditText password;
    private TextView register;
    private SharedPreferences sharedPreferences;
    private FloatingView floatingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playground_activity_main);

        //綁定layout和程式邏輯變數
        login_button = findViewById(R.id.LoginButton);
        register = findViewById(R.id.register);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        imageView_twitter = findViewById(R.id.imageView_twitter);
        imageView_instagram = findViewById(R.id.imageView_instagram);
        imageView_facebook = findViewById(R.id.imageView_facebook);

        //導入帳號密碼
        sharedPreferences = getApplicationContext().getSharedPreferences("tw.playground.sheng.LoginSDK", 0); // 0 - for private mode
        username.setText(sharedPreferences.getString("username", null));
        password.setText(sharedPreferences.getString("password", null));

        //處理圖形和版面
        getSupportActionBar().hide(); //設定隱藏標題
        register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//設定register底線


        //設定監聽器
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登入
                new Loginer(MainActivity.this, username.getText().toString(),password.getText().toString()).login();
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //註冊 | 快速註冊
                new Register(MainActivity.this).register();
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
    protected void onPause() {
        super.onPause();
        if (floatingView != null) {
            floatingView.hide();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (floatingView != null) {
            floatingView.showOverlayActivity();
        }
    }

}
