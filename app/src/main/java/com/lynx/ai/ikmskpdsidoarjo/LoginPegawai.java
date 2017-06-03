package com.lynx.ai.ikmskpdsidoarjo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

public class LoginPegawai extends AppCompatActivity {
    ScrollView layoutLoginPegawai;
    MaskedEditText textNipPegawai;
    EditText textNamaPegawai;
    Button btnLoginPegawai;
    TextView textInfo;
    ProgressDialog pDialog;

    String nipPetugas,
            nipPegawai,
            namaPegawai,
            idPenilaian,
            urlLoginPegawai,
            urlTambahPenilaian;

	/*
	* Ganti url dibawah ini dengan alamat website.
	* */
	String urlOri="http://ailynx.hol.es/ikm/webservice/";
	
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpegawai);

        nipPetugas = getIntent().getStringExtra("nipPetugas");

        layoutLoginPegawai = (ScrollView) findViewById(R.id.layoutLoginPegawai);
        textNipPegawai = (MaskedEditText) findViewById(R.id.textNipPegawai);
        textNamaPegawai = (EditText) findViewById(R.id.textNamaPegawai);
        btnLoginPegawai = (Button) findViewById(R.id.btnLoginPegawai);
        textInfo = (TextView) findViewById(R.id.textInfo);

        textNamaPegawai.setKeyListener(null);

        btnLoginPegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nipPegawai = textNipPegawai.getUnmaskedText();
                if(nipPegawai.equals("")){
                    Snackbar.make(layoutLoginPegawai, "Masukkan NIP Pegawai", Snackbar.LENGTH_SHORT).show();
                } else {
                    /*
                    * Set URL untuk login pegawai.
                    * */
                    urlLoginPegawai = urlOri+"login_pegawai.php?nip_pegawai="+nipPegawai;
                    new doLoginPegawai().execute(urlLoginPegawai);
                }
            }
        });
        textInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(LoginPegawai.this, InfoAplikasi.class);
                startActivity(info);
            }
        });
    }

    class doLoginPegawai extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            ParseLoginPegawai jparse = new ParseLoginPegawai();
            String value;
            try{
                value = jparse.run(params[0]);
            } catch (IOException e) {
                value = "99";
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginPegawai.this);
            pDialog.setMessage("Proses Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            namaPegawai = s;
            pDialog.dismiss();
            if (s.equals("99")){
                Snackbar.make(layoutLoginPegawai, "Login Error:\nCek Koneksi Internet Anda", Snackbar.LENGTH_SHORT).show();
            } else if(!s.equals("null")){
                textNamaPegawai.setText(namaPegawai);
                btnLoginPegawai.setText("Mulai");
                btnLoginPegawai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        * Set URL untuk menambah data form penilaian.
                        * */
                        urlTambahPenilaian = urlOri+"tambah_penilaian.php?nip_petugas="+nipPetugas;
//                        Snackbar.make(layoutLoginPegawai, "Mulai Survey", Snackbar.LENGTH_SHORT).show();
                        new doTambahPenilaian().execute(urlTambahPenilaian);
                    }
                });
                Snackbar.make(layoutLoginPegawai, "NIP Terdaftar", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(layoutLoginPegawai, "Login Error:\nNIP tidak terdaftar", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    class ParseLoginPegawai {
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
                resultTemp = temp.getString("nama_pegawai");
            } catch (JSONException e){
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }

            return resultTemp;
        }
    }

    class doTambahPenilaian extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginPegawai.this);
            pDialog.setMessage("Tunggu Sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            idPenilaian = s;
            pDialog.dismiss();
            if (!s.equals("null")){
                Intent pegawai = new Intent(LoginPegawai.this, FormPenilaian.class);
                Bundle data = new Bundle();
                data.putString("nipPegawai", nipPegawai);
                data.putString("idPenilaian", idPenilaian);
                pegawai.putExtras(data);
                startActivity(pegawai);
            } else if (s.equals("error")){
                Snackbar.make(layoutLoginPegawai, "Cek Koneksi Internet Anda", Snackbar.LENGTH_SHORT).show();
            } else if (s.equals("null")){
                Snackbar.make(layoutLoginPegawai, "Gagal Memulai Survey. Coba Lagi Beberapa Saat", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            ParseTambahPenilaian jparse = new ParseTambahPenilaian();
            String value;
            try{
                value = jparse.run(params[0]);
            } catch (IOException e) {
                value = "error";
                e.printStackTrace();
            }
            return value;
        }
    }

    class ParseTambahPenilaian{
        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException{
            client.cache();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String result = getIdPenilaian(response.body().string());

            return result;
        }

        public String getIdPenilaian(String data){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString("id_penilaian");
            } catch (JSONException e) {
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }
}
