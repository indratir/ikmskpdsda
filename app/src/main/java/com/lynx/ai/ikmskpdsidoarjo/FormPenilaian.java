package com.lynx.ai.ikmskpdsidoarjo;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lynx.ai.ikmskpdsidoarjo.popup.Keterangan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by indratir on 12/08/2016.
 */

public class FormPenilaian extends AppCompatActivity {
    String idPenilaian = "0",
            nipPegawai = "0",
            url;
	
	/*
		Ganti url dibawah ini dengan alamat website.
	*/
	String urlOri="http://ailynx.hol.es/ikm/webservice/";
	
    ScrollView layoutPenilaian;
    LinearLayout boxWrapPenilaian;
    ProgressDialog pDialog;
    Button btnOk;
    LinearLayout[] boxUnsur;
    TextView[] textUnsur;
    Spinner[] spinnerUnsur;
    String[] unsurTerpilih;
    String[] pertanyaanUnsur;
    String[][] jawabUnsur;

    ArrayAdapter<String> adapterUnsur;
    int jumlahUnsur = 0;
    int jumlahJawaban = 0;
    int statusKirim = 0;
	
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penilaian);

        nipPegawai = getIntent().getExtras().getString("nipPegawai");
        idPenilaian = getIntent().getExtras().getString("idPenilaian");
        layoutPenilaian = (ScrollView) findViewById(R.id.layoutPenilaian);
        boxWrapPenilaian = (LinearLayout) findViewById(R.id.boxWrapPenilaian);

        /*
        * Set URL untuk cek jumlah pertanyaan.
        * */
        url = urlOri+"jumlah_pertanyaan.php";
        new doGetJumlahUnsur().execute(url);
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(layoutPenilaian, "Silahkan Mengisi Form Penilaian dan Tekan Tombol Selesai", Snackbar.LENGTH_LONG).show();
    }

    public void Inisialisasi(int jumlah){
        this.jumlahUnsur = jumlah;
        boxUnsur = new LinearLayout[jumlah];
        textUnsur = new TextView[jumlah];
        spinnerUnsur = new Spinner[jumlah];
        unsurTerpilih = new String[jumlah];
        pertanyaanUnsur = new String[jumlah];
        jawabUnsur = new String[jumlah][jumlahJawaban];

        /*
        * Set URL untuk mengambil data pertanyaan.
        * */
        url = urlOri+"data_pertanyaan.php";
        new doGetPertanyaan().execute(url);
    }

    public void SusunTampilan(){
        for (int i = 0; i<jumlahUnsur; i++){
            boxUnsur[i] = new LinearLayout(FormPenilaian.this);
            textUnsur[i] = new TextView(FormPenilaian.this);
            spinnerUnsur[i] = new Spinner(FormPenilaian.this);

            LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            boxParams.setMargins(0, 45, 0, 0);
            boxUnsur[i].setBackgroundColor(Color.rgb(255,255,255));
            boxUnsur[i].setOrientation(LinearLayout.VERTICAL);
            boxUnsur[i].setLayoutParams(boxParams);
            boxUnsur[i].setPadding(15, 15, 15, 15);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textUnsur[i].setText(pertanyaanUnsur[i]);
            textUnsur[i].setLayoutParams(textParams);
            textUnsur[i].setTextColor(Color.rgb(33,33,33));
            boxUnsur[i].addView(textUnsur[i]);

            /*
            * Set URL untuk mengambil data jawaban dari setiap pertanyaan.
            * */
            url = urlOri+"data_jawaban.php?id_pertanyaan="+(i+1)+"";
            new doGetJawaban().execute(url, ""+i+"");
            adapterUnsur = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, jawabUnsur[i]);

            LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            spinnerParams.setMargins(0, 15, 0, 0);
            spinnerUnsur[i].setLayoutParams(spinnerParams);
            spinnerUnsur[i].setBackgroundColor(Color.rgb(20,167,81));
            spinnerUnsur[i].setAdapter(adapterUnsur);
            final int counter = i;
            spinnerUnsur[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    unsurTerpilih[counter] = ""+position+"";
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    unsurTerpilih[counter] = "0";
                }
            });

            boxUnsur[i].addView(spinnerUnsur[i]);

            boxWrapPenilaian.addView(boxUnsur[i]);
            pDialog.dismiss();
        }
        btnOk = new Button(FormPenilaian.this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 30, 0, 0);
        btnOk.setLayoutParams(btnParams);
        btnOk.setText("SELESAI");
        btnOk.setBackgroundColor(Color.rgb(20,167,81));
        btnOk.setPadding(15,0,15,0);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOk.setText("Tunggu Sebentar..");
                int counter = 0;
                for (int i = 0; i<jumlahUnsur; i++){
                    if (unsurTerpilih[i].equals("0")){
                        Snackbar.make(layoutPenilaian, "Pastikan Anda Memberi Penilaian Pada Setiap Pertanyaan", Snackbar.LENGTH_LONG).show();
                        break;
                    } else {
                        counter++;
                    }
                }
                if (counter == jumlahUnsur){
                    for (int i = 0; i<jumlahUnsur; i++){
                        /*
                        * Set URL untuk mengirim nilai atau jawaban yang telah dipilih.
                        * */
                        url = urlOri+"tambah_nilai.php?nip_pegawai="+nipPegawai+"&id_penilaian="+idPenilaian+"&id_pertanyaan="+(i+1)+"&id_jawaban="+unsurTerpilih[i];
                        new doTambahNilai().execute(url);
                    }
                }
            }
        });
        boxWrapPenilaian.addView(btnOk);
    }

    public void InisialisasiJawaban(){
        for (int i = 0; i<jumlahUnsur; i++){
            jawabUnsur[i][0] = "---";
        }
        SusunTampilan();
    }

    class doGetJumlahUnsur extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            ParseJumlahUnsur jparse = new ParseJumlahUnsur();
            String value;
            try{
                value = jparse.run(params[0]);
            } catch (IOException e) {
                value = "0";
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(FormPenilaian.this);
            pDialog.setMessage("Tunggu Sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            jumlahUnsur = Integer.parseInt(s);
            /*
            * Set URL untuk cek jumlah jawaban.php
            * */
            url = urlOri+"jumlah_jawaban.php";
            new doGetJumlahJawaban().execute(url);
        }
    }

    class ParseJumlahUnsur {
        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException{
            client.cache();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = getJumlahUnsur(response.body().string());

            return result;
        }
        public String getJumlahUnsur(String data){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString("jumlah_pertanyaan");
            } catch (JSONException e){
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }

    class doGetJumlahJawaban extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            jumlahJawaban = Integer.parseInt(s)+1;
            Inisialisasi(jumlahUnsur);
        }

        @Override
        protected String doInBackground(String... params) {
            ParseJumlahJawaban jparse = new ParseJumlahJawaban();
            String value;
            try{
                value = jparse.run(params[0]);
            } catch (IOException e) {
                value = "0";
                e.printStackTrace();
            }
            return value;
        }
    }

    class ParseJumlahJawaban {
        OkHttpClient client = new OkHttpClient();
        String run(String url) throws IOException{
            client.cache();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String result = getJumlahJawaban(response.body().string());

            return result;
        }
        public String getJumlahJawaban(String data){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString("jumlah_jawaban");
            } catch (JSONException e) {
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }

    class doGetPertanyaan extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            InisialisasiJawaban();
        }

        @Override
        protected String doInBackground(String... params) {
            ParsePertanyaan jparse = new ParsePertanyaan();
            String[] value = new String[jumlahUnsur];
            for (int i=0; i<jumlahUnsur; i++){
                try{
                    value[i] = jparse.run(params[0], i+1);
                } catch (IOException e) {
                    value[i] = "";
                    e.printStackTrace();
                }
                pertanyaanUnsur[i] = value[i];
            }
            return null;
        }
    }

    class ParsePertanyaan{
        OkHttpClient client = new OkHttpClient();
        String run(String url, int position) throws IOException{
            client.cache();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = getPertanyaan(response.body().string(), position);

            return result;
        }
        public String getPertanyaan(String data, int position){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString(""+position+"");
            }catch (JSONException e){
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }

    class doGetJawaban extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            int number = Integer.parseInt(params[1]);
            ParseJawaban jparse = new ParseJawaban();
            String[] value = new String[jumlahJawaban-1];
            for (int i=0; i<jumlahJawaban-1; i++){
                try {
                    value[i] = jparse.run(params[0], i+1);
                } catch (IOException e) {
                    value[i] = "";
                    e.printStackTrace();
                }
                jawabUnsur[number][i+1] = value[i];
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }


    }

    class ParseJawaban {
        OkHttpClient client = new OkHttpClient();

        String run(String url, int position) throws IOException{
            client.cache();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String result = getJawaban(response.body().string(), position);
            return result;
        }

        public String getJawaban(String data, int position){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString(""+position+"");
            } catch (JSONException e) {
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }

    class doTambahNilai extends AsyncTask<String, String, String>{
        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("1")){
                statusKirim++;
            } else if(s.equals("0")){
                Snackbar.make(layoutPenilaian, "Error: Gagal Menambah Data ke Server", Snackbar.LENGTH_SHORT).show();
            } else if(s.equals("99")){
                Snackbar.make(layoutPenilaian, "Error: Cek Koneksi Internet Anda", Snackbar.LENGTH_SHORT).show();
            }
            if (statusKirim == jumlahUnsur){
                DialogFragment ket = new Keterangan();
                ket.show(getFragmentManager(), "keterangan");
            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(FormPenilaian.this);
            pDialog.setMessage("Tunggu Sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            ParseTambahNilai jparse = new ParseTambahNilai();
            String value;
            try{
                value = jparse.run(params[0]);
            } catch (IOException e) {
                value = "99";
                e.printStackTrace();
            }
            return value;
        }
    }

    class ParseTambahNilai {
        OkHttpClient client = new OkHttpClient();
        String run(String url) throws IOException{
            client.cache();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String result = tambahNilai(response.body().string());

            return result;
        }
        public String tambahNilai(String data){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString("tambah_nilai");
            } catch (JSONException e) {
                temp = null;
                resultTemp = null;
                e.printStackTrace();
            }
            return resultTemp;
        }
    }
}
