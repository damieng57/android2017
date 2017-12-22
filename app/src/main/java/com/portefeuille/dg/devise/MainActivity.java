package com.portefeuille.dg.devise;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.portefeuille.dg.devise.modele.Devise;
import com.portefeuille.dg.devise.modele.Portefeuille;

import static com.portefeuille.dg.devise.dao.Serialisation.charge;
import static com.portefeuille.dg.devise.dao.Serialisation.sauvegarde;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, View.OnClickListener, View.OnLongClickListener, DialogInterface.OnClickListener {

    private ListView maListe;
    private Portefeuille monPorteFeuille;
    private ArrayAdapter<Devise> adaptateur;
    private Devise devise;
    private GridLayout grille;
    private TextView textePortefeuille;

    private static final int GESTION_DEVISE=0;
    private static final int CREATION_DEVISE=1;
    private static final int MODIFICATION_NOM_DEVISE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Le portefeuille sur lequel on travaille
        this.monPorteFeuille = new Portefeuille();
        // La devise courante
        this.devise = new Devise();

        // On recupère le portefeuille et la devise courante en cas de rotation
        if (savedInstanceState != null) {
            this.monPorteFeuille = (Portefeuille) savedInstanceState.getSerializable("portefeuille");
            this.devise = (Devise) savedInstanceState.getSerializable("devise");
        // On démarre l'appli, on récupère le portefeuille depuis un fichier sérialisé
        } else {
            this.monPorteFeuille = charge(this);
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        // Mode paysage (ListView)
        if (this.getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_90 ||
                this.getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_270) {
            this.maListe = this.findViewById(R.id.ma_liste);
            // Définition des écouteurs sur la liste
            this.maListe.setOnItemClickListener(this);
            this.maListe.setOnItemLongClickListener(this);

            // Adaptateur Portfeuille<Devise> -> ListView
            this.adaptateur = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1,
                    this.monPorteFeuille.getValeurs());
            this.maListe.setAdapter(adaptateur);
        // Mode portrait (Boutons dynamiques + TextView)
        } else {
            this.grille = this.findViewById(R.id.gl_grille);
            this.textePortefeuille = this.findViewById(R.id.tv_portefeuille);

            genereBoutons();
            }
        }


    // Création d'une devise
    // On appelle UpdateActivity sans paramètre
    public void creeDevise(View view) {
        Intent appelActivite = new Intent(this, UpdateActivity.class);
        startActivityForResult(appelActivite, CREATION_DEVISE);
    }

    // Simple clic sur la ListView, on appelle DeviseActivity et on passe en paramètre la devise
    // depuis la listView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int indexListe = position;

        this.devise = (Devise) this.maListe.getItemAtPosition(indexListe);
        Intent appelActivite = new Intent(this, DeviseActivity.class);
        appelActivite.putExtra("devise", this.devise);
        startActivityForResult(appelActivite, GESTION_DEVISE);
    }

    // Appui long sur la listView, on veut modifier le nom de la devise en cours
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int indexListe = position;

        this.devise = (Devise) this.maListe.getItemAtPosition(indexListe);
        Intent appelActivite = new Intent(this, UpdateActivity.class);
        appelActivite.putExtra("nom", this.devise.getNom());
        startActivityForResult(appelActivite, MODIFICATION_NOM_DEVISE);

        return true;
    }

    // Traitement des résultats reçues des activités filles (DeviseActivity et UpdateActivity)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == UpdateActivity.OK || resultCode == DeviseActivity.OK){
            if (requestCode==CREATION_DEVISE){
                String nomDevise = data.getExtras().getString("nom");

                // Si l'index est à -1, la devise n'est pas présente au portefeuille
                if (this.monPorteFeuille.chercheMonnaie(nomDevise)==-1){
                    Devise devise = new Devise(nomDevise, 0.0f);
                    this.monPorteFeuille.ajout(devise);
                // Si différent de -1 (et en toute logique positif car correspondant à un index, on affiche un message
                } else {
                    afficheMessage(getResources().getString(R.string.nomExistant), Toast.LENGTH_LONG);
                }


            } else if (requestCode==MODIFICATION_NOM_DEVISE){
                String nomDevise = data.getExtras().getString("nom");
                // Même principe pour la modification, si l'index est négatif, pas de conflit
                if (this.monPorteFeuille.chercheMonnaie(nomDevise)==-1){
                    this.devise.setNom(nomDevise);
                // Dans le cas contraire, on affiche un message
                } else {
                    afficheMessage(getResources().getString(R.string.nomExistant), Toast.LENGTH_LONG);
                }


            } else if (requestCode==GESTION_DEVISE){
                // Mettre à jour le portefeuille pour avoir la dernière valeur
                Bundle bundle = data.getExtras();
                Devise devise = (Devise) bundle.get("devise");
                // On tente l'ajout au portefeuille
                try {
                    // Au retour de l'activité DeviseActivity, on l'ajoute au portefeuille
                    this.devise.setMontant(devise.getMontant());

                    if (this.devise.getMontant()==0){
                        Alerte alerte = new Alerte();
                        alerte.show(getFragmentManager(), "suppression");
                    }

                    // On mets à jour la liste view si en mode paysage
                    if (this.getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_90 ||
                            this.getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_270) {
                        this.adaptateur.notifyDataSetChanged();
                    // Sinon c'est la textview du mode portrait
                    } else {
                        this.textePortefeuille.setText(this.monPorteFeuille.toString());
                    }
                } catch (NullPointerException ex) {
                    // Texte uniquement pour le déboggage
                    afficheMessage(ex.toString(), Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // On sauvegarde le portefeuille courant
        outState.putSerializable("portefeuille", this.monPorteFeuille);
        // On sauvegarde la devise courante! (Sinon, pas de mise à jour en cas de rotation.)
        outState.putSerializable("devise", this.devise);
    }

    // Enregistrement du portefeuille dans fichier sérialisé avant fermeture du programme
    @Override
    protected void onPause() {
        super.onPause();

        sauvegarde(this, this.monPorteFeuille);
    }

    private void afficheMessage(String text, int duration){
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        Button bouton = (Button) view;

        bouton.setTextColor(Color.BLUE);

        this.devise = (Devise) bouton.getTag();
        Intent appelActivite = new Intent(this, DeviseActivity.class);
        appelActivite.putExtra("devise", this.devise);
        startActivityForResult(appelActivite, GESTION_DEVISE);


    }

    @Override
    public boolean onLongClick(View view) {
        Button bouton = (Button) view;

        this.devise = (Devise) bouton.getTag();
        Intent appelActivite = new Intent(this, UpdateActivity.class);
        appelActivite.putExtra("nom", this.devise.getNom());
        startActivityForResult(appelActivite, MODIFICATION_NOM_DEVISE);

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which==Dialog.BUTTON_POSITIVE){
            this.monPorteFeuille.retrait(this.devise);

            // Mode paysage (ListView)
            if (this.getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_90 ||
                    this.getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_270) {
                this.adaptateur.notifyDataSetChanged();

                // Mode portrait (Boutons dynamiques + TextView)
            } else {
                genereBoutons();
                }
            }
        }

    private void genereBoutons() {
        // Nettoyage de la grille
        this.grille.removeAllViews();

        // Ajout des boutons
        for (int i = 0; i < this.monPorteFeuille.getValeurs().size(); i++) {
            Devise devise = this.monPorteFeuille.getValeurs().get(i);
            Button boutonDevise = new Button(this);
            boutonDevise.setText(devise.getNom());
            boutonDevise.setTag(devise);
            boutonDevise.setOnClickListener(this);
            boutonDevise.setOnLongClickListener(this);
            this.grille.addView(boutonDevise);

            this.textePortefeuille.setText(this.monPorteFeuille.toString());
        }
    }

}
