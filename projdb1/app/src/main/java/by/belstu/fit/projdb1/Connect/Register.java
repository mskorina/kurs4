package by.belstu.fit.projdb1.Connect;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import by.belstu.fit.projdb1.jsonworkers.Authworker;

public class Register {
    public String path="https://api.belcraft.ru/v1/register";
    public Authworker authworker;
    public Register(String user,String password) {
        this.user=user;
        this.password=password;
        authworker=new Authworker(user,password);
    }
    private String user;
    private String password;
    private String json;
    public StringBuilder response = new StringBuilder();
    public void Connect() throws IOException, JSONException {
        authworker.CreateJson();
        json=authworker.returnjson();

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
        return;
    }
    public String Info() throws JSONException {
        if (!response.toString().isEmpty()) return authworker.registerInfo(response.toString());
        return "connection failed";
    }
}
