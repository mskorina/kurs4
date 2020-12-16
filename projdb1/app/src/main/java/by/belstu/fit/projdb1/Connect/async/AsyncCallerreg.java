package by.belstu.fit.projdb1.Connect.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import by.belstu.fit.projdb1.Connect.Register;

public class AsyncCallerreg extends AsyncTask<Void, Void, String>
{
    public String signup(EditText login,EditText password) throws IOException, JSONException {

        Register register = new Register(login.getText().toString(),password.getText().toString());
        register.Connect();
//        if (result!=null)
//            return result;
//        return "error connection";
        return register.Info();
//        Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
    }
    EditText login;
    EditText password;
    Context ctx;
    ProgressDialog pdLoading;

    public AsyncCallerreg(EditText login, EditText password,Context ctx) {
        this.login=login;
        this.password=password;
        this.ctx=ctx;
        pdLoading=new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        pdLoading.setMessage("\tЗапрос на регистрацию...");
        pdLoading.show();
    }
    @Override
    protected String doInBackground(Void... params) {
        String result="";
        try {
            result=signup(login,password);
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