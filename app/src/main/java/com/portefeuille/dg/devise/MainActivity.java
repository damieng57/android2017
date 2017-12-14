package com.portefeuille.dg.devise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView maListe;
    private Portefeuille monPorteFeuille;
    private ArrayAdapter<Devise> adaptateur;
    private Devise devise;

    private static final int GESTION_DEVISE=0;
    private static final int CREATION_DEVISE=1;
    private static final int MODIFICATION_NOM_DEVISE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.monPorteFeuille = new Portefeuille();

        if (savedInstanceState != null) {
            this.monPorteFeuille = (Portefeuille) savedInstanceState.getSerializable("portefeuille");
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStream(this.openFileInput("portefeuille.ser"));
                this.monPorteFeuille = (Portefeuille) ois.readObject();
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
    public void onStart(){
        super.onStart();

        this.maListe = this.findViewById(R.id.ma_liste);

        this.adaptateur = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                this.monPorteFeuille.getValeurs());
        this.maListe.setAdapter(adaptateur);

        this.maListe.setOnItemClickListener(this);
        this.maListe.setOnItemLongClickListener(this);
    }

    public void creeDevise(View view) {
        Intent appelActivite = new Intent(this, UpdateActivity.class);
        startActivityForResult(appelActivite, CREATION_DEVISE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int indexListe = position;

        this.devise = (Devise) this.maListe.getItemAtPosition(indexListe);
        Intent appelActivite = new Intent(this, DeviseActivity.class);
        appelActivite.putExtra("devise", this.devise);
        startActivityForResult(appelActivite, GESTION_DEVISE);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        int indexListe = position;

        this.devise = (Devise) this.maListe.getItemAtPosition(indexListe);
        Intent appelActivite = new Intent(this, UpdateActivity.class);
        appelActivite.putExtra("nom", this.devise.getNom());
        startActivityForResult(appelActivite, MODIFICATION_NOM_DEVISE);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == UpdateActivity.OK || resultCode == DeviseActivity.OK){
            if (requestCode==CREATION_DEVISE){
                String nomDevise = data.getExtras().getString("nom");
                Devise devise = new Devise(nomDevise, 0.0f);
                this.monPorteFeuille.ajout(devise);

            } else if (requestCode==MODIFICATION_NOM_DEVISE){
                String nomDevise = data.getExtras().getString("nom");
                this.devise.setNom(nomDevise);

            } else if (requestCode==GESTION_DEVISE){
                // Mettre à jour le portefeuille pour avoir la dernière valeur
                Bundle bundle = data.getExtras();
                Devise devise = (Devise) bundle.get("devise");
                // On tente l'ajout au portefeuille
                try {
                    // Au retour de l'activité DeviseActivity, on l'ajoute au portefeuille
                    this.devise.setMontant(devise.getMontant());
                    // On mets à jour la liste view
                    this.adaptateur.notifyDataSetChanged();
                } catch (NullPointerException ex) {
                    // Texte uniquement pour le déboggage
                    //afficheMessage(ex, Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("portefeuille", this.monPorteFeuille);
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("portefeuille.ser", Context.MODE_PRIVATE));
            oos.writeObject(this.monPorteFeuille);
            oos.close();
        } catch (IOException ex) {
            // Texte uniquement pour le déboggage
            // afficheMessage("Fichier inaccessible en écriture!", Toast.LENGTH_LONG);
        }
    }

    private void afficheMessage(String text, int duration){
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}
