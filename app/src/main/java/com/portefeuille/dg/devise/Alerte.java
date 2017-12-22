package com.portefeuille.dg.devise;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * Created by user on 19/12/2017.
 */

public class Alerte extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        Activity activite = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activite);

        builder.setMessage(R.string.da_confirme_suppression).setTitle(R.string.da_devise_nulle);

        DialogInterface.OnClickListener ecouteur = (DialogInterface.OnClickListener) activite;
        builder.setPositiveButton(R.string.da_oui, ecouteur);
        builder.setNegativeButton(R.string.da_non, ecouteur);

        return builder.create();

    }



}
