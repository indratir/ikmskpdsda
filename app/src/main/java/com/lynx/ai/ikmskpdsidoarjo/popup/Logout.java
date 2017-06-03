package com.lynx.ai.ikmskpdsidoarjo.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lynx.ai.ikmskpdsidoarjo.LoginPetugas;
import com.lynx.ai.ikmskpdsidoarjo.R;

/**
 * Created by indratir on 16/08/2016.
 */

public class Logout extends DialogFragment {
    String text;
    TextView textWarning;
    Button btnBackWarning, btnOkWarning;

    public static Logout newInstance(String text) {
        Bundle args = new Bundle();

        Logout fragment = new Logout();
        fragment.setArguments(args);
        args.putString("text", text);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        text = getArguments().getString("text");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_warning, null);
        builder.setView(view);
        builder.setCancelable(true);

        textWarning = (TextView) view.findViewById(R.id.textWarning);
        btnBackWarning = (Button) view.findViewById(R.id.btnBackWarning);
        btnOkWarning = (Button) view.findViewById(R.id.btnOkWarning);

        textWarning.setText(text);
        btnBackWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOkWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent close = new Intent(getActivity(), LoginPetugas.class);
                close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(close);
            }
        });
        return builder.create();
    }
}
