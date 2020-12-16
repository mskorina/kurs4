package by.belstu.fit.projdb1.Connect.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class AsyncCallersoapcheck extends AsyncTask<Void, Void, String>
{
    String envelope="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
            " <soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+

    " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+

    " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"> "+
        "<soap:Body>1"+
        "</soap:Body>"+
        "</soap:Envelope>";


    public String Connect() throws IOException {
        StringBuilder response = new StringBuilder();
        String path="https://api.belcraft.ru/v1/soapcheck";
        URL url=new URL(path);
        HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
        c.setRequestMethod("POST");
        c.setConnectTimeout(2500);
        c.setReadTimeout(5000);
        c.setRequestProperty("Content-Type", "txt/xml; charset=utf-8");
        c.setRequestProperty("Accept", "txt/xml");
        c.setDoOutput(true);
        c.setDoInput(true);
        c.getOutputStream().write(envelope.getBytes(StandardCharsets.UTF_8));
        c.getOutputStream().close();
        c.connect();
        if (c.getResponseCode() != 200) {
            return "Нет подключения к серверу";
        }
        String responseLine = null;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream(), "utf-8"))) {
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
        }
        return "Подключение есть";
    }


    Context ctx;
    ProgressDialog pdLoading;

    public AsyncCallersoapcheck(Context ctx) {
        this.ctx=ctx;
        pdLoading=new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        pdLoading.setMessage("\tПроверка...");
        pdLoading.show();
    }
    @Override
    protected String doInBackground(Void... params) {
        String result="";
        try {
            result=Connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //this method will be running on UI thread
        Toast.makeText(ctx,result,Toast.LENGTH_SHORT).show();
        pdLoading.dismiss();
    }

}