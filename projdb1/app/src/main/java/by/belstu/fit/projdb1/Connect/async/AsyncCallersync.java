package by.belstu.fit.projdb1.Connect.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import by.belstu.fit.projdb1.Connect.Sync;

public class AsyncCallersync extends AsyncTask<Void, Void, Boolean>
{
    Context ctx;
    String token;
    ProgressDialog pdLoading;

    public AsyncCallersync(Context ctx, String token) {
        this.ctx=ctx;
        this.token=token;
        pdLoading= new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        pdLoading.setMessage("\tСинхронизация...");
        pdLoading.show();
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result=false;
        Sync sync = new Sync(ctx,token);
        try {
            result=sync.Connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result){
            Toast.makeText(ctx,"Синхронизация выполнена",Toast.LENGTH_SHORT).show();}
        else{
            Toast.makeText(ctx, "Ошибка, проверьте интернет или перезайдите", Toast.LENGTH_SHORT).show();
            //this method will be running on UI thread
        }
        pdLoading.dismiss();
    }

}