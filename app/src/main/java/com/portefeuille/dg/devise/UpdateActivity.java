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
    private Bundle bundle;
    private String nomDevise;

    public static final int OK=1;
    public static final int CANCEL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        bundle = this.getIntent().getExtras();
        // Cas typique de la rotation d'écran
        if (savedInstanceState != null) {
            this.nomDevise = savedInstanceState.getString("devise");
        // Cas après appel depuis MainActivity
        } else if (bundle != null){
            this.nomDevise = bundle.getString("nom");}
        else {
            // Message de débogage, ne doit pas apparaître en situation normale
            //afficheMessage("N'arrive pas charger le nom de la devise", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // NOTA : Le cast n'est plus nécessaire
        etNom = findViewById(R.id.et_nom);
        tvAffichage = findViewById(R.id.tv_affichage);

        // On teste si extra est non null
        if (this.getIntent().getExtras() == null){
            // Si null, c'est que l'on veut créer une devise
            // On configure la textView pour afficher le bon message
            tvAffichage.setText(R.string.addDevise);
        } else {
            // Sinon, c'est une modification
            // On configure la textView pour afficher le bon message
            tvAffichage.setText(R.string.majDevise);
            etNom.setText(this.getIntent().getStringExtra("nom"));
        }
    }

    // on clique sur le bouton annuler - On revient à l'activité appelante sans rien faire
    public void cancel(View view) {
        this.setResult(CANCEL);
        this.finish();
    }

    // on clique sur le bouton ok
    public void ok(View view) {
        // On récupère la valeur dans l'EditText
        String nomDevise = this.etNom.getText().toString().trim();
        // On vérifie que ce n'est pas une chaîne vide
        if (nomDevise.length()==0){
            // Sinon, on affiche un message d'erreur
            afficheMessage("Le nom est vide !", Toast.LENGTH_LONG);
        } else {
            // Si c'est bon, on prépare un intent et retour à l'activité principale
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
