package by.belstu.fit.projdb1.ui.send;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;

import by.belstu.fit.projdb1.Connect.Register;
import by.belstu.fit.projdb1.Connect.Sign;
import by.belstu.fit.projdb1.Connect.Sync;
import by.belstu.fit.projdb1.Connect.async.AsyncCallerreg;
import by.belstu.fit.projdb1.Connect.async.AsyncCallersign;
import by.belstu.fit.projdb1.Connect.async.AsyncCallersoapcheck;
import by.belstu.fit.projdb1.Connect.async.AsyncCallersync;
import by.belstu.fit.projdb1.MainActivity;
import by.belstu.fit.projdb1.R;

import static android.content.Context.MODE_PRIVATE;

public class SendFragment extends Fragment {
    public EditText login;
    public EditText password;
    FloatingActionButton fab;
    SharedPreferences settings;
    Button registerbut;
    Button signbut;
    Button logoutbut;
    Button syncbut;
    Button checkbut;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_send, container, false);
        fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        settings = getActivity().getSharedPreferences("syncset", MODE_PRIVATE);
        login=getActivity().findViewById(R.id.loginedit);
        password=getActivity().findViewById(R.id.passedit);
        registerbut = root.findViewById(R.id.signupbutton);
        signbut = root.findViewById(R.id.signbutton);
        logoutbut = root.findViewById(R.id.logoutbutton);
        syncbut = root.findViewById(R.id.syncbutton);
        checkbut = root.findViewById(R.id.synccheckbutton);

        registerbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checkperm();
                login=getView().findViewById(R.id.loginedit);
                password=getView().findViewById(R.id.passedit);
                FragmentActivity mainActivity=getActivity();
                MainActivity mainActivity1=(MainActivity)mainActivity;
                mainActivity1.auth=true;
                new AsyncCallerreg(login,password,getActivity()).execute();

            }
        });
        signbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checkperm();
                login=getView().findViewById(R.id.loginedit);
                password=getView().findViewById(R.id.passedit);
                FragmentActivity mainActivity=getActivity();
                MainActivity mainActivity1=(MainActivity)mainActivity;
                mainActivity1.auth=true;
                new AsyncCallersign(login,password,getActivity(),getView(),settings).execute();

                if (settings.contains("token")) {
                    registerbut.setVisibility(View.GONE);
                    signbut.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    login.setVisibility(View.GONE);
                    logoutbut.setVisibility(View.VISIBLE);
                    syncbut.setVisibility(View.VISIBLE);
                    checkbut.setVisibility(View.VISIBLE);
                }

            }
        });
        logoutbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (settings.contains("token")) {
                    SharedPreferences.Editor prefEditor = settings.edit();
                    prefEditor.remove("token");
                    prefEditor.apply();
                }
                login.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                signbut.setVisibility(View.VISIBLE);
                registerbut.setVisibility(View.VISIBLE);
                logoutbut.setVisibility(View.INVISIBLE);
                syncbut.setVisibility(View.INVISIBLE);
                checkbut.setVisibility(View.INVISIBLE);
            }
        });

        syncbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (settings.contains("token")) {
                    String token=settings.getString("token","none");
                    new AsyncCallersync(getActivity(),token).execute();
                }

            }
        });
        checkbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (settings.contains("token")) {
                    new AsyncCallersoapcheck(getActivity()).execute();
                }

            }
        });

        return root;
    }






    @Override
    public void onResume() {
        fab.hide();
        login=getView().findViewById(R.id.loginedit);
        password=getView().findViewById(R.id.passedit);
        if (settings.contains("token")) {
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
            if(!checkbut.isShown())
            checkbut.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    public void checkperm() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.INTERNET},
                1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    onDestroyView();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }













}