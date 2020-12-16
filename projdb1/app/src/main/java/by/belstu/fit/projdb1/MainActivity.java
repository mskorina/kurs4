package by.belstu.fit.projdb1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.WindowManager;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


import by.belstu.fit.projdb1.Connect.async.AsyncCallerdrop;
import by.belstu.fit.projdb1.jsonworkers.Jsonexport;


public class MainActivity extends AppCompatActivity {
    Uri uri;
    int FILE_REQUEST_CODE;
    String fileName;
    boolean imports=false;
    public boolean auth=false;
    SharedPreferences settings;
    boolean check=false;
    boolean checkget=false;
    private AppBarConfiguration mAppBarConfiguration;
    static boolean orintationChanged=false;
    private ArrayList docPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FILE_REQUEST_CODE=111;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_Payment, R.id.nav_Card, R.id.nav_statistic,
                R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        settings = getSharedPreferences("passwordset", MODE_PRIVATE);

        if(Build.VERSION.SDK_INT >= 23) {
            ExternalStoragePermissions.verifyStoragePermissions(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
            builder2.setTitle("Внимание").setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!DBHelper.password.isEmpty()) {
                        SQLiteDatabase db = DBHelper.getInstance(getApplicationContext()).getWritableDatabase(DBHelper.password);
                        db.execSQL("PRAGMA foreign_keys=ON");
                        db.execSQL("DELETE FROM CARDS");
                        db.close();
                        SharedPreferences settings2=getSharedPreferences("syncset", MODE_PRIVATE);
                        if (settings2.contains("token")) {
                            String token = settings2.getString("token","none");
                            new AsyncCallerdrop(token, MainActivity.this).execute();
                        }
                    }
                }
            }).setNegativeButton("Не надо",null).setMessage("Все данные будут удалены без возратно!!!");
            AlertDialog dialog2 = builder2.create();
            dialog2.show();
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (!auth && !imports) {
            if (!checkget && !DBHelper.savepassword) {
                Intent intent = new Intent(this, PasswordActivity.class);
                startActivityForResult(intent, 1);
            } else {
                checkget = false;
                DBHelper.savepassword = false;
                orintationChanged = false;
            }
        }
        imports=false;
        auth=false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode== FILE_REQUEST_CODE) {
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    docPaths = new ArrayList<Uri>();
                    ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                    String dbp=files.get(0).getPath();
                    try {
                        if(importDatabase(dbp))         Toast.makeText(this, "Файл найден, выполняется импорт", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "Откройте приложение заново", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        else if (resultCode!=11) {
            uri = data.getData();
            if (uri.toString().startsWith("file:")) {
                fileName = uri.getPath();
            }
            else { // uri.startsWith("content:")
                uri = data.getData();
                Cursor c = getContentResolver().query(uri, null, null, null, null);

                if (c != null && c.moveToFirst()) {

                    int id = c.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (id != -1) {
                        fileName = c.getString(id);
                    }
                }
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
        if(!DBHelper.savepassword) {
        if (data == null) {finish(); return;}
        DBHelper.password = data.getStringExtra("password");
        if (!check) {
            if (!settings.contains("set")) {
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString("set", "1");
                prefEditor.apply();
            }
        }
        check=true;
        checkget=true;
        }
    }

    public void onPause() {
        if(!imports && !auth) {
            check = false;
            if (!DBHelper.savepassword)
                DBHelper.password = "";
            super.onPause();
        }
        super.onPause();
    }

    public void ExportDb(View view) throws IOException {
        Toast.makeText(this, "Сохранение...", Toast.LENGTH_SHORT).show();
        File root = new File(Environment.getExternalStorageDirectory().getPath()
                + "/WalletLite/");
        if(!root.exists()) root.mkdir();
        File dbsave = new File(Environment.getExternalStorageDirectory().getPath()
                + "/WalletLite/"+DBHelper.DATABASE_NAME);
        File dbsrc=new File(getDatabasePath(DBHelper.DATABASE_NAME).getPath());
        copy(dbsrc,dbsave);
        Toast.makeText(this, "Сохранено в sdcard/WalletLite", Toast.LENGTH_SHORT).show();
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public void Importdb(View view) throws IOException {
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .setShowFiles(true)
                .setSuffixes("db","DB")
                .build());
        imports=true;
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    public boolean importDatabase(String dbPath) throws IOException {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.close();
        File newDb = new File(dbPath);
        File oldDb = new File(DBHelper.DB_FILEPATH);
        if (newDb.exists()) {
            copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            dbHelper.getWritableDatabase(DBHelper.password).close();
            return true;
        }
        return false;
    }

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public void expjsondb(View view) {
    Jsonexport jsonexporter=new Jsonexport();
    jsonexporter.CreateJson(getApplicationContext());
    jsonsave(jsonexporter.json);
    }

    private void jsonsave(String json) {
        Toast.makeText(this, "Сохранение...", Toast.LENGTH_SHORT).show();
        File root = new File(Environment.getExternalStorageDirectory().getPath()
                + "/WalletLite/");
        if(!root.exists()) root.mkdir();
        File filej = new File(Environment.getExternalStorageDirectory().getPath()
                + "/WalletLite/exportjson.txt");
        try
        {
            filej.createNewFile();
            FileOutputStream fOut = new FileOutputStream(filej);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(json);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
            Toast.makeText(this, "Сохранено. sdcard/WalletLite/exportjson.txt", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}