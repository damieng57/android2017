package com.portefeuille.dg.devise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private EditText etNom;
    private TextView tvAffichage;
    private String nomDevise = "";

    public static final int OK=1;
    public static final int CANCEL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        this.nomDevise = this.getIntent().getStringExtra("nom");

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Le cast n'est plus nécessaire
        etNom = findViewById(R.id.et_nom);
        tvAffichage = findViewById(R.id.tv_affichage);

        // On teste si extra est non null
        if (this.getIntent().getExtras() == null){
            // Si null, c'est que l'on veut créer une devise
            tvAffichage.setText(R.string.addDevise);
        } else {
            // Sinon, c'est une modification
            tvAffichage.setText(R.string.majDevise);
            etNom.setText(this.getIntent().getStringExtra("nom"));
        }
    }

    public void cancel(View view) {
        this.setResult(CANCEL);
        this.finish();
    }

    public void ok(View view) {

        String nomDevise = this.etNom.getText().toString().trim();
        if (nomDevise.length()==0){
            afficheMessage("Le nom est vide !", Toast.LENGTH_LONG);
        } else {
            Intent donnees = new Intent();
            donnees.putExtra("nom", nomDevise);
            this.setResult(OK, donnees);
            this.finish();
        }
    }

    @Override
    public void onBackPressed(){
        this.cancel(null);
    }

    private void afficheMessage(String text, int duration){
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    public void retour(View view) {
        this.cancel(null);
    }
}
