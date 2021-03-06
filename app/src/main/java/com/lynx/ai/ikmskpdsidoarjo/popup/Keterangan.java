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
 * Created by indratir on 05/08/2016.
 */

public class Keterangan extends DialogFragment {
    Button btnOkKet;
    TextView textBoxKet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_keterangan, null);
        builder.setView(view);
        builder.setCancelable(true);

        textBoxKet = (TextView) view.findViewById(R.id.textBoxKet);

        btnOkKet = (Button) view.findViewById(R.id.btnOkKet);
        btnOkKet.setOnClickListener(new View.OnClickListener() {
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
