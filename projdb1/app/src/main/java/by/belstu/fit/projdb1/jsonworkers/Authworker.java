package by.belstu.fit.projdb1.jsonworkers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import by.belstu.fit.projdb1.models.User.Auth;

public class Authworker {
    private Auth dataset=new Auth();

    public Authworker(String email,String password){
        dataset.email=email;
        dataset.password=password;
    }
    private String json;
    private GsonBuilder builder = new GsonBuilder();
    private Gson gson = builder.serializeNulls().create();

    public void CreateJson() {
        json = gson.toJson(dataset);
    }
    public String returnjson(){
        return json;
    }
    public String registerInfo(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        return jsonObj.getString("account");
    }

    public String signInfo(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        return jsonObj.getString("token");
    }

}
