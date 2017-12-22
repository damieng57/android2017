package com.portefeuille.dg.devise.dao;

import android.content.Context;

import com.portefeuille.dg.devise.modele.Portefeuille;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Damien on 22/12/2017.
 */

public class Serialisation {

    public static void sauvegarde(Context context, Portefeuille portefeuille){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput("portefeuille.ser", Context.MODE_PRIVATE));
            oos.writeObject(portefeuille);
            oos.close();
        } catch (IOException ex) {
            // Texte uniquement pour le déboggage
            // afficheMessage("Fichier inaccessible en écriture!", Toast.LENGTH_LONG);
        }
    }

    public static Portefeuille charge(Context context){
        Portefeuille portefeuille = new Portefeuille();

        try {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput("portefeuille.ser"));
            portefeuille = (Portefeuille) ois.readObject();
            ois.close();

        } catch (IOException | ClassNotFoundException ex) {
            // Texte uniquement pour le déboggage - Au premier lancement, le fichier n'existant pas
            // il lève une exception. Plus de problème ensuite.
                // CharSequence text = "Fichier inaccessible en lecture!"+ex.toString();
                // int duration = Toast.LENGTH_LONG;
                // Toast toast = Toast.makeText(this, text, duration);
                // toast.show();
        }

        // A minima, on renvoie un portefeuille vide pour éviter un nullPointerException dans MainActivity
        return portefeuille;
    }

}
