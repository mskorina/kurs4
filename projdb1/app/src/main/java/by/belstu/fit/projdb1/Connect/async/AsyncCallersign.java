package by.belstu.fit.projdb1.Connect.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import by.belstu.fit.projdb1.Connect.Sign;
import by.belstu.fit.projdb1.R;

public class AsyncCallersign extends AsyncTask<Void, Void, String>
{

    public String sign(EditText login,EditText password) throws IOException, JSONException {

        Sign sign = new Sign(login.getText().toString(),password.getText().toString());
        sign.Connect();
//        if (register.!=null)
//            return result;
//        return "error connection";
        return sign.Info();
//        Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("StaticFieldLeak")
    private EditText login;
    @SuppressLint("StaticFieldLeak")
    private EditText password;
    @SuppressLint("StaticFieldLeak")
    private Context ctx;
    private ProgressDialog pdLoading;
    @SuppressLint("StaticFieldLeak")
    private View root;
    private SharedPreferences settings;

    public AsyncCallersign(EditText login, EditText password,Context ctx,View root,SharedPreferences settings) {
        this.login=login;
        this.password=password;
        this.ctx=ctx;
        this.root=root;
        this.settings=settings;
        pdLoading=new ProgressDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        pdLoading.setMessage("\tЗапрос на вход...");
        pdLoading.show();
    }
    @Override
    protected String doInBackground(Void... params) {
        String result="";
        try {
            result=sign(login,password);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (!result.equals("No Connect") && !result.equals("error_password_or_user"))
            if (!settings.contains("token")) {
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString("token", result);
                prefEditor.apply();
                login=root.findViewById(R.id.loginedit);
                password=root.findViewById(R.id.passedit);
                Button registerbut=root.findViewById(R.id.signupbutton);
                Button signbut=root.findViewById(R.id.signbutton);
                Button logoutbut=root.findViewById(R.id.logoutbutton);
                Button syncbut=root.findViewById(R.id.syncbutton);
                Button checkbut=root.findViewById(R.id.synccheckbutton);
                if (registerbut.isShown())
                    registerbut.setVisibility(View.GONE);
                if (signbut.isShown())
                    signbut.setVisibility(View.GONE);
                if (password.isShown())
                    password.setVisibility(View.GONE);
                if (login.isShown())
                    login.setVisibility(View.GONE);
                if (!logoutbut.isShown())
                    logoutbut.setVisibility(View.VISIBLE);
                if (!syncbut.isShown())
                    syncbut.setVisibility(View.VISIBLE);
                if (!checkbut.isShown())
                    checkbut.setVisibility(View.VISIBLE);
                Toast.makeText(ctx,"Вход выполнен",Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("No Connect")) {
                Toast.makeText(ctx, "Проверьте подключение", Toast.LENGTH_SHORT).show();
                //this method will be running on UI thread
            }
            else {
                Toast.makeText(ctx, "Проверьте логин и пароль", Toast.LENGTH_SHORT).show();
            }
        pdLoading.dismiss();
    }

}