package com.portefeuille.dg.devise;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.portefeuille.dg.devise.exceptions.DeviseDevientNulleException;
import com.portefeuille.dg.devise.modele.Devise;

public class DeviseActivity extends AppCompatActivity {

    private TextView ma_enpoche;
    private EditText ma_montant;
    private Devise devise;
    private Bundle bundle;

    public static final int OK=1;
    public static final int CANCEL=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devise);

        bundle = this.getIntent().getExtras();
        // Cas typique de la rotation d'écran
        if (savedInstanceState != null) {
            this.devise = (Devise) savedInstanceState.getSerializable("devise");
            // Cas après appel depuis MainActivity
        } else if (bundle != null) {
            this.devise = (Devise) bundle.get("devise");
        } else {
            // Message de débogage, ne doit pas apparaître en situation normale
            // afficheMessage("N'arrive pas charger la devise", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Le cast n'est plus nécessaire
        ma_enpoche = findViewById(R.id.tv_affichage);
        ma_montant = findViewById(R.id.et_nom);

        // Mise à jour de la textView
        updateTextView(this.devise.getNom(), this.devise.getMontant());
    }

    // Ajouter à la devise en cours
    public void ajouter(View view) {

        try {
            // Récupération de la valeur dans la textView et conversion en Float
            Float montant = Float.valueOf(ma_montant.getText().toString());
            // Puis ajout
            this.devise.ajout(montant);
            updateTextView(this.devise.getNom(), this.devise.getMontant());
        } catch (NumberFormatException ex){
            afficheMessage(getString(R.string.entreNombre), Toast.LENGTH_LONG);
        }

        // On masque le clavier
        cacheClavier();
    }

    // Retirer de la devise en cours
    public void retirer(View view) {

        String montant = ma_montant.getText().toString();

        try{
            this.devise.retrait(Float.valueOf(montant));
            updateTextView(devise.getNom(), devise.getMontant());
        } catch (DeviseDevientNulleException ex){
            afficheMessage(getString(R.string.compteazero), Toast.LENGTH_SHORT);
            updateTextView(this.devise.getNom(), 0.0f);
        } catch (IllegalArgumentException ex){
            afficheMessage(getString(R.string.decouvert), Toast.LENGTH_SHORT);
        }

        // On masque le clavier
        cacheClavier();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("devise", this.devise);
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
        this.setResult(CANCEL, appelActivite);
        this.finish();
    }

    public void retour(View view) {
        Intent appelActivite = new Intent();
        appelActivite.putExtra("devise", this.devise);
        this.setResult(OK, appelActivite);
        this.finish();
    }

    private void cacheClavier() {
        InputMethodManager mgr = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.ma_montant.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        this.ma_montant.setText("");
    }
}

