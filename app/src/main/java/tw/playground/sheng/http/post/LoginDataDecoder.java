package tw.playground.sheng.http.post;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginDataDecoder {
    private JSONObject jsonObject;

    public LoginDataDecoder(String string)
    {
        try {
            jsonObject = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String decoder(String string){
        try {
            JSONObject quizJSON = jsonObject.getJSONObject("quiz");
            Log.d("sheng050", quizJSON.toString());
            JSONObject sportJSON = quizJSON.getJSONObject("sport");
            Log.d("sheng050", sportJSON.toString());

            JSONObject postJSON = jsonObject.getJSONObject("post");
            Log.d("sheng050", postJSON.toString());
            Log.d("sheng050", postJSON.getString("username"));
            Log.d("sheng050", postJSON.getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }
}
