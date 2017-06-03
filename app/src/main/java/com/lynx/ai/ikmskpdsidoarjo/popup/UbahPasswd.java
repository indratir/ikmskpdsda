package com.lynx.ai.ikmskpdsidoarjo.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.lynx.ai.ikmskpdsidoarjo.LoginPetugas;
import com.lynx.ai.ikmskpdsidoarjo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by indratir on 04/08/2016.
 */

public class UbahPasswd extends DialogFragment {
    ScrollView layoutMenuPetugas;
    EditText textPasswdLama;
    EditText textPasswdBaru;
    Button btnBackUbahPasswd;
    Button btnOkUbahPasswd;
    ProgressDialog pDialog;

    String passwdLama, passwdBaru, nipPetugas, url;
	
	/*
	* Set URL folder yang berisi file webservice.
	* */
	String urlOri="http://ailynx.hol.es/ikm/webservice/";
	
    public static UbahPasswd newInstance(String nipPetugas) {
        UbahPasswd fragment = new UbahPasswd();

        Bundle args = new Bundle();
        args.putString("nipPetugas", nipPetugas);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        nipPetugas = getArguments().getString("nipPetugas");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.popup_ubahpasswd, null);
        builder.setView(view);
        builder.setCancelable(true);

        layoutMenuPetugas = (ScrollView) getActivity().findViewById(R.id.layoutMenuPetugas);
        textPasswdLama = (EditText) view.findViewById(R.id.textHeadPasswdLama);
        textPasswdBaru = (EditText) view.findViewById(R.id.textHeadPasswdBaru);
        btnBackUbahPasswd = (Button) view.findViewById(R.id.btnBackUbahPasswd);
        btnOkUbahPasswd = (Button) view.findViewById(R.id.btnOkUbahPasswd);

        btnBackUbahPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOkUbahPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textPasswdLama.getText().toString().equals("") && textPasswdBaru.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Silahkan diisi terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else if (textPasswdLama.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Masukkan password lama", Toast.LENGTH_SHORT).show();
                } else if (textPasswdBaru.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Masukkan password baru", Toast.LENGTH_SHORT).show();
                } else{
                    passwdLama = textPasswdLama.getText().toString();
                    passwdBaru = textPasswdBaru.getText().toString();

                    /*
                    * Set URL untuk mengubah password petugas.
                    * */
                    url = urlOri+"ubah_password.php?nip_petugas="+nipPetugas+"&password_lama="+passwdLama+"&password_baru="+passwdBaru;
                    new doUbahPasswd().execute(url);
                }
            }
        });
        return builder.create();
    }

    class doUbahPasswd extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            JSONParser jparse = new JSONParser();
            String value;
            try{
                value = jparse.run(params[0]);
                Log.e("Do In Background", "jparse run");
            } catch (IOException e) {
                value = "99";
                e.printStackTrace();
                Log.e("Do In Background", e.getMessage());
            }
            return value;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Proses Ubah Password...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("1")){
                Toast.makeText(getActivity(), "Sukses Ubah Password\nSilahkan Login Kembali dengan Password Baru", Toast.LENGTH_LONG).show();
                Intent close = new Intent(getActivity(), LoginPetugas.class);
                close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(close);
            } else if (s.equals("0")){
                Toast.makeText(getActivity(), "Error: Password Lama Anda Salah", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error: Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            }
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
            Log.e("JSON Parser", "jparse run");
            return result;
        }

        public String verifyJSON(String data){
            JSONObject temp;
            String resultTemp;
            try{
                temp = new JSONObject(data);
                resultTemp = temp.getString("ubah_password");
                Log.e("verify JSON", resultTemp);
            } catch (JSONException e){
                temp = null;
                resultTemp = null;
                e.printStackTrace();
                Log.e("verify JSON", e.getMessage());
            }
            return resultTemp;
        }
    }
}
