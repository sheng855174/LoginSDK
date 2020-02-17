package tw.playground.sheng;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tw.playground.sheng.http.post.HttpsPostThread;
import tw.playground.sheng.http.post.IRegHttpsPostAdapter;

public class Register {
    private String TAG = "sheng050";
    private String username;
    private String password;
    private Context context;

    public Register(Context context){
        this.context = context;
    }

    public int register(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage("還沒有帳號，現在去註冊吧！");
        builder.setPositiveButton("現在就去", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //註冊
            }
        });
        builder.setNegativeButton("快速登入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //快速登入
                //1.註冊
                username = random(10);
                password = random(12);
                IRegHttpsPostAdapter iRegHttpsPostAdapter = new HttpsPostThread();
                //2.登入
                new Loginer(context, username,password).login();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return 0;
    }

    public String random(int digital){//可以選擇0~32位數，中英亂數產生
        String result;
        int head;
        String timestamp = System.currentTimeMillis() + "";
        long tmp = System.currentTimeMillis();
        result = timestamp + (int)(Math.random()*2147483647);//int max 2147483647
        result = md5(result);
        result += result;//避免隨機到最後不夠，直接抄兩遍
        head = (int)(Math.random()*32);
        Log.d(TAG, String.valueOf((System.currentTimeMillis()-tmp)));
        return result.substring(head,head+digital);
    }

    public String md5(String input) {
        String result = input;
        if(input != null) {
            MessageDigest md = null; //or "SHA-1"
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while(result.length() < 32) { //40 for SHA-1
                result = "0" + result;
            }
        }
        return result;
    }
}
