package by.belstu.fit.projdb1.Connect.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import by.belstu.fit.projdb1.Connect.Drop;

public class AsyncCallerdrop extends AsyncTask<Void, Void, String>
{
    public String dropf(String token) throws IOException, JSONException {

        Drop drop=new Drop(token);
        drop.Connect();
//        if (result!=null)
//            return result;
//        return "error connection";
        return drop.Info();
//        Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
    }
    String token;
    Context ctx;
    ProgressDialog pdLoading;

    public AsyncCallerdrop(String token,Context ctx) {
        this.ctx=ctx;
        this.token=token;
        pdLoading=new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        pdLoading.setMessage("\tУдаление с сервера...");
        pdLoading.show();
    }
    @Override
    protected String doInBackground(Void... params) {
        String result="";
        try {
            result=dropf(token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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