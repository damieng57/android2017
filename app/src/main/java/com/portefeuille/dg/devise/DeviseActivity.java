package com.portefeuille.dg.devise;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.portefeuille.dg.devise.exceptions.DeviseDevientNulleException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeviseActivity extends AppCompatActivity {

    private TextView ma_enpoche;
    private EditText ma_montant;
    private Devise devise;

    public static final int OK=1;
    public static final int CANCEL=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devise);

        Bundle bundle = this.getIntent().getExtras();

        if (savedInstanceState != null) {
            devise = (Devise) savedInstanceState.getSerializable("devise");
        } else if (bundle != null){
            this.devise = (Devise) bundle.get("devise");
        } else {
                try {
                    ObjectInputStream ois = new ObjectInputStream(this.openFileInput("devise.ser"));
                    devise = (Devise) ois.readObject();
                    ois.close();
                } catch (IOException | ClassNotFoundException ex) {
                    // Texte uniquement pour le déboggage
                    CharSequence text = "Fichier inaccessible en lecture!"+ex.toString();
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(this, text, duration);
                    toast.show();
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Le cast n'est plus nécessaire
        ma_enpoche = findViewById(R.id.tv_affichage);
        ma_montant = findViewById(R.id.et_nom);

        updateTextView(devise.getNom(), devise.getMontant());
    }

    public void ajouter(View view) {

        try {
            // Récupération de la valeur dans la textView et conversion en Float
            Float montant = Float.valueOf(ma_montant.getText().toString());
            // Puis ajout
            devise.ajout(montant);
            updateTextView(devise.getNom(), devise.getMontant());
        } catch (NumberFormatException ex){
            afficheMessage(getString(R.string.entreNombre), Toast.LENGTH_LONG);
        }
    }

    public void retirer(View view) {
        String montant = ma_montant.getText().toString();

        try{
            devise.retrait(Float.valueOf(montant));
            updateTextView(devise.getNom(), devise.getMontant());

        } catch (DeviseDevientNulleException ex){
            afficheMessage(getString(R.string.compteazero), Toast.LENGTH_SHORT);
            updateTextView(devise.getNom(), 0.0f);
        } catch (IllegalArgumentException ex){
            afficheMessage(getString(R.string.decouvert), Toast.LENGTH_SHORT);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("devise", devise);
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("devise.ser", Context.MODE_PRIVATE));
            oos.writeObject(this.devise);
            oos.close();
        } catch (IOException ex) {
            // Texte uniquement pour le déboggage
            //afficheMessage("Fichier inaccessible en écriture!", Toast.LENGTH_LONG);
        }

    }

    private void afficheMessage(String text, int duration){
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private void updateTextView(String devise, Float montant){
        Resources resources = getResources();
        String texte = resources.getString(R.string.affichage, devise, montant);
        ma_enpoche.setText(texte);
    }

    @Override
    public void onBackPressed(){
        Intent appelActivite = new Intent();
        appelActivite.putExtra("devise", this.devise);
        this.setResult(OK, appelActivite);
        this.finish();
    }

    public void retour(View view) {
        onBackPressed();
    }
}

