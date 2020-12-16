package by.belstu.fit.projdb1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import  net.sqlcipher.database.SQLiteDatabase;
import  net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

public class PasswordActivity extends AppCompatActivity {
    SharedPreferences settings;
    EditText pass;
    TextView text;
    SQLiteDatabase db;
    File filedatabase;
    boolean check=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pass=(EditText) findViewById(R.id.passwordedit);
        text=(TextView) findViewById(R.id.passwordtext);
        filedatabase= getApplicationContext().getDatabasePath("WalletLite.db");
        settings = getSharedPreferences("passwordset", MODE_PRIVATE);
        if (settings.contains("set")) {
            text.setText("Введите текущий пароль");
            check=true;
        }
        SQLiteDatabase.loadLibs(getApplicationContext());
    }

    public void passwordclick(View view) {
        String password;
        password=pass.getText().toString();
        if(password.length()<6) {
            Toast.makeText(this, "Слишком короткий пароль", Toast.LENGTH_LONG).show();
        }
        else {
            if (check) {

                try {
                    db = DBHelper.getInstance(getApplicationContext()).getReadableDatabase(password);
                    db.close();
                }
                catch (Exception e) {
                    Toast.makeText(this, "Не верный пароль", Toast.LENGTH_LONG).show();
                    return;
                }

            }
                try {
                    db = DBHelper.getInstance(getApplicationContext()).getWritableDatabase(password);
                    db.close();
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
              //      Toast.makeText(this, "что-то не так", Toast.LENGTH_LONG).show();
                    return;
                }
            Intent intent = new Intent();
            intent.putExtra("password", password);
            setResult(11, intent);
            finish();
        }
    }

}
