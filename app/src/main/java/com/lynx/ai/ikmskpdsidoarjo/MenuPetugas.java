package com.lynx.ai.ikmskpdsidoarjo;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lynx.ai.ikmskpdsidoarjo.popup.UbahPasswd;
import com.lynx.ai.ikmskpdsidoarjo.popup.Logout;

/**
 * Created by indratir on 04/08/2016.
 */

public class MenuPetugas extends AppCompatActivity {
    ScrollView layoutMenuPetugas;
    Button btnMenu1;
    Button btnMenu2;
    Button btnMenu3;
    TextView textInfo;

    String nipPetugas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menupetugas);

        layoutMenuPetugas = (ScrollView) findViewById(R.id.layoutMenuPetugas);
        btnMenu1 = (Button) findViewById(R.id.btnMenu1);
        btnMenu2 = (Button) findViewById(R.id.btnMenu2);
        btnMenu3 = (Button) findViewById(R.id.btnMenu3);
        textInfo = (TextView) findViewById(R.id.textInfo);

        nipPetugas = getIntent().getStringExtra("nipPetugas");

        Snackbar.make(layoutMenuPetugas, "Login Sukses.\nNIP : "+nipPetugas+"", Snackbar.LENGTH_SHORT).show();

        btnMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ubahPasswd = new UbahPasswd().newInstance(nipPetugas);
                ubahPasswd.show(getFragmentManager(), "ubahPasswd");
            }
        });

        btnMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent survey = new Intent(MenuPetugas.this, LoginPegawai.class);
                survey.putExtra("nipPetugas", nipPetugas);
                startActivity(survey);
            }
        });

        btnMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment logout = new Logout().newInstance("Anda akan logout dari aplikasi. Apakah anda yakin?");
                logout.show(getFragmentManager(), "logout");
            }
        });

        textInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(MenuPetugas.this, InfoAplikasi.class);
                startActivity(info);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DialogFragment logout = new Logout().newInstance("Anda akan logout dari aplikasi. Apakah anda yakin?");
        logout.show(getFragmentManager(), "logout");
    }
}
