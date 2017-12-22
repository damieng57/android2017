package com.portefeuille.dg.devise.modele;

import com.portefeuille.dg.devise.exceptions.DeviseDevientNulleException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;



public class Portefeuille implements Serializable {

	/**
	 * Attribut de la classe
	 */
	private ArrayList<Devise> valeurs;


	/**
	 * Constructeur par défaut
	 */
	public Portefeuille() {

		this.valeurs = new ArrayList<Devise>();
	}

	/**
	 * Méthode d'accès : accesseur
	 * @return la liste des devises
	 */
	public ArrayList<Devise> getValeurs() {
		return this.valeurs;
	}

	/**
	 * Méthode d'accès : modificateur
	 * @param valeurs : une nouvelle liste de devises
	 */
	public void setValeurs(ArrayList<Devise> valeurs) {
		this.valeurs = valeurs;
	}

	/**
	 * Ajout d'une devise au portefeuille
	 * @param d la devise à ajouter
	 */
	public void ajout(Devise d) {

		int i = this.chercheMonnaie(d.getNom());

		// Pas encore de cette monnaie
		if (i == -1) {
			this.valeurs.add(new Devise(d));
		}
		// La monnaie existe déjà, on ajoute simplement le montant
		else {
			this.valeurs.get(i).ajout(d.getMontant());
		}

	}

	/**
	 * Sortie d'argent du portefeuille
	 * @param d la devise dont on sort de l'argent
	 */
	public void retrait(Devise d) {

		int i = this.chercheMonnaie(d.getNom());

		if (i != -1) {
			try {
				this.valeurs.get(i).retrait(d.getMontant());
			} catch (DeviseDevientNulleException ddne) {
				System.out.println(ddne.getMessage());
				this.valeurs.remove(i);
			}
		}
		else  {
			throw new IllegalArgumentException("Retrait : la devise "+ 
								d.getNom() + " n'existe pas !");
		}
	}

	/**
	 * Permet de savoir si une monnaie existe dans le portefeuille
	 * (renvoie -1 si la monnaie n'existe pas, l'indice sinon)
	 * 
	 * Attention : nécessite qu'on implémente equals(Object) dans Devise 
	 */ 
	public int chercheMonnaie(String nom) {
		
		return this.valeurs.indexOf(new Devise(nom, 0));
	}


	/**
	 * Tri de la liste de devises
	 */
	public void tri() {
		Collections.sort(this.valeurs);
	}


	/**
	 * Très utile pour l'affichage tous médias
	 */
	@Override
	public String toString() {
	
		StringBuffer sB = new StringBuffer(512);
		sB.append("Contenu du portefeuille :\n");
		
		for (Devise d : this.valeurs) {
			sB.append(d+"\n");
		}
		
		return sB.toString();
	
	}
	
	/**
	 * Méthode d'affichage des devises
	 */
	public void affiche() {

		System.out.println(this);
	}

}
