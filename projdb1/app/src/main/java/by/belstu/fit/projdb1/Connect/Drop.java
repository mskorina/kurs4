package by.belstu.fit.projdb1.Connect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import by.belstu.fit.projdb1.models.sync.tokenjson;

public class Drop {
    public String path="https://api.belcraft.ru/v1/syncdrop";
    private GsonBuilder builder = new GsonBuilder();
    private Gson gson = builder.serializeNulls().create();
    private tokenjson tokenjson=new tokenjson();

    public Drop(String token) {
        tokenjson.token=token;
    }
    private StringBuilder response = new StringBuilder();
    public void Connect() throws IOException, JSONException {
        String json = gson.toJson(tokenjson);
        URL url=new URL(path);
        HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
        c.setRequestMethod("POST");
        c.setConnectTimeout(2500);
        c.setReadTimeout(5000);
        c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        c.setRequestProperty("Accept", "application/json");
        c.setDoOutput(true);
        c.setDoInput(true);
        c.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        c.getOutputStream().close();
        c.connect();
        if (c.getResponseCode() != 200) {
            return;
        }
        String responseLine = null;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream(), "utf-8"))) {
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
        }
    }

    public String Info() throws JSONException {
        JSONObject jsonObj = null;
        if (!response.toString().isEmpty()) {
            jsonObj = new JSONObject(response.toString());
            if (jsonObj.getString("status").equals("OK"))
                return "Синхронизация завершена";
            return "Ошибка синхронизации";
        }
        return "connection failed";
    }
}

