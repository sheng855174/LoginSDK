package tw.playground.sheng;

import android.content.Context;
import android.content.SharedPreferences;

import tw.playground.sheng.http.post.HttpsPostThread;
import tw.playground.sheng.http.post.ILoginHttpsPostAdapter;

public class Loginer {
    private String username;
    private String password;
    private Context context;

    public Loginer(Context context, String username, String password)
    {
        this.context = context;
        this.username = username;
        this.password = password;
    }
    public int login(){
        ILoginHttpsPostAdapter iLoginHttpsPostAdapter = new HttpsPostThread();
        iLoginHttpsPostAdapter.login(username, password);
        //如果登入成功，儲存資料
        if(true)
        {
            SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("tw.playground.sheng.LoginSDK", 0); // 0 - for private mode
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.commit();
        }
        return 0;
    }
}
