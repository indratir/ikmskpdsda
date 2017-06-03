package com.lynx.ai.ikmskpdsidoarjo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by indratir on 04/08/2016.
 */

public class LoginPetugas extends AppCompatActivity {
    ScrollView layoutLoginPetugas;
    MaskedEditText textNipPetugas;
    EditText textPasswordPetugas;
    Button btnLoginPetugas;
    TextView textInfo;
    ProgressDialog pDialog;

    String nipPetugas,
		passwdPetugas, 
		url;

    /*
    * Ganti url dibawah ini dengan alamat website.
    * ex: "http://www.xyz.com/"
    * */
	String urlOri="http://ailynx.hol.es/ikm/webservice/";
	
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpetugas);

        layoutLoginPetugas = (ScrollView) findViewById(R.id.layoutLoginPetugas);
        textNipPetugas = (MaskedEditText) findViewById(R.id.textNipPetugas);
        textPasswordPetugas = (EditText) findViewById(R.id.textPasswordPetugas);
        btnLoginPetugas = (Button) findViewById(R.id.btnLoginPetugas);
        textInfo = (TextView) findViewById(R.id.textInfo);

        btnLoginPetugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nipPetugas = textNipPetugas.getUnmaskedText();
                passwdPetugas = textPasswordPetugas.getText().toString();
                if (nipPetugas.equals("")){
                    Snackbar.make(layoutLoginPetugas, "Masukkan NIP Anda", Snackbar.LENGTH_SHORT).show();
                } else if(passwdPetugas.equals("")){
                    Snackbar.make(layoutLoginPetugas, "Masukkan Password Anda", Snackbar.LENGTH_SHORT).show();
                } else {
                    /*
                    * Set URL untuk login petugas
                    * */
                    url = urlOri+"login_petugas.php?nip_petugas="+nipPetugas+"&password_petugas="+passwdPetugas;

                    new CekLoginPetugas().execute(url);
                }
            }
        });

        textInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(LoginPetugas.this, InfoAplikasi.class);
                startActivity(info);
            }
        });
    }

    protected void StartIntent(){
        Intent petugas = new Intent(LoginPetugas.this, MenuPetugas.class);
        petugas.putExtra("nipPetugas", nipPetugas);

        startActivity(petugas);
    }

    class CekLoginPetugas extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginPetugas.this);
            pDialog.setMessage("Proses Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("1")){
                StartIntent();
            } else if (s.equals("0")){
                Snackbar.make(layoutLoginPetugas, "Login Error:\nKombinasi NIP dan Password Salah", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(layoutLoginPetugas, "Login Error:\nCek Koneksi Internet Anda", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            JSONParser jparse = new JSONParser();
            String value;
            try {
                value = jparse.run(params[0]);
            } catch (IOException e) {
                value = "99";
                e.printStackTrace();
            }
            return value;
        }
    }

    class JSONParser {
        OkHttpClient client = new OkHttpClient();
        String run(String url) throws IOException{
            client.cache();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = verifyJSON(response.body().string());
            return result;
        }

        public String verifyJSON(String data){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString("login_petugas");
            } catch (JSONException e){
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }
}
