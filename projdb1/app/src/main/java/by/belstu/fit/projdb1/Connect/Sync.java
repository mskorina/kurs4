package by.belstu.fit.projdb1.Connect;

import android.content.Context;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import by.belstu.fit.projdb1.jsonworkers.Syncworker;

public class Sync {
    public String path="https://api.belcraft.ru/v1/syncstep1";
    public String path2="https://api.belcraft.ru/v1/syncstep2";
    Context ctx;
    String token;
    public Syncworker syncworker=new Syncworker();
    public Sync(Context ctx,String token) {
        this.ctx=ctx;
        this.token=token;
    }
    public StringBuilder responsestep1 = new StringBuilder();
    public StringBuilder responsestep2 = new StringBuilder();
    public boolean Connect() throws IOException, JSONException {
        String clientsync1=syncworker.CreateJsonClientsync1(ctx,token);
        String clientsync2;
        String responseLine = null;
        URL url=new URL(path);
        HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
        c.setRequestMethod("POST");
        c.setConnectTimeout(2500);
        c.setReadTimeout(5000);
        c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        c.setRequestProperty("Accept", "application/json");
        c.setDoOutput(true);
        c.setDoInput(true);
        c.getOutputStream().write(clientsync1.getBytes(StandardCharsets.UTF_8));
        c.getOutputStream().close();
        c.connect();
        if (c.getResponseCode() != 200) {
            return false;
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream(), "utf-8"))) {
            while ((responseLine = br.readLine()) != null) {
                responsestep1.append(responseLine);
            }
        }
        if (!syncworker.checkresponse(responsestep1.toString())) return false;
        if (!syncworker.ParseJsonServersync1(ctx,responsestep1.toString())) return false;
        clientsync2=syncworker.CreateJsonClientsync2(ctx,token,responsestep1.toString());
        URL url2=new URL(path2);
        HttpsURLConnection c2=(HttpsURLConnection)url2.openConnection();
        c2.setRequestMethod("POST");
        c2.setConnectTimeout(2500);
        c2.setReadTimeout(5000);
        c2.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        c2.setRequestProperty("Accept", "application/json");
        c2.setDoOutput(true);
        c2.setDoInput(true);
        c2.getOutputStream().write(clientsync2.getBytes(StandardCharsets.UTF_8));
        c2.getOutputStream().close();
        c2.connect();
        if (c2.getResponseCode() != 200) {
            return false;
        }
        responseLine = null;

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(c2.getInputStream(), "utf-8"))) {
            while ((responseLine = br.readLine()) != null) {
                responsestep2.append(responseLine);
            }
        }
        if (!syncworker.checkresponse(responsestep2.toString())) return false;
        return true;
    }
}
