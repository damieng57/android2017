package com.portefeuille.dg.devise.modele;

import com.portefeuille.dg.devise.exceptions.DeviseDevientNulleException;

import java.io.Serializable;

public class Devise implements Comparable<Devise>, Serializable {

	/**
	 * Attributs de la classe
	 */
	private float montant;

	private String nom;

	/**
	 * Constructeurs
	 */
	public Devise() {

		this("EUR", 0);
	}

	public Devise(float m) {

		this("EUR", m);
	}

	public Devise(String d, float m) {

		this.setMontant(m);
		this.setNom(d);
	}

	// Constructeur par copie
	public Devise(Devise d) {

		this.montant = d.montant;
		this.nom = d.nom;
	}

	/**
	 * Méthodes d'accès
	 */
	public void setMontant(float m) {

		if (m >= 0) {
			this.montant = m;
		} else {
			throw new IllegalArgumentException("setMontant : somme négative : "
					+ m);
		}
	}

	public void setNom(String nom) {
		
		if (nom==null || nom.trim().length()==0) {
			throw new IllegalArgumentException("Le nom de la devise est vide !");
		}
		this.nom = nom;
	}
	
	public String getNom() {

		return this.nom;
	}

	public float getMontant() {

		return this.montant;
	}

	/**
	 * Autres méthodes
	 */
	public void ajout(float mnt) {

		if (mnt >= 0) {
			this.montant += mnt;
		} else {
			throw new IllegalArgumentException("ajout : somme négative : "
					+ mnt);
		}
	}

	public void retrait(float mnt) {

		if (mnt < 0) {
			throw new IllegalArgumentException("retrait : somme négative : "
					+ mnt);
		}
		else if (mnt > this.getMontant()) {
			throw new IllegalArgumentException("retrait : pas assez dans le portefeuille : "
					+ mnt + " > " + this.montant);
		}
		
		this.montant -= mnt;
	
		if (this.montant==0) {
			throw new DeviseDevientNulleException("Il n'y a plus de liquidité dans cette devise");
		}
	}

	public String toString() {

		return this.montant + " " + this.nom;
	}

	public void affiche() {

		System.out.println(this);
	}
	
	/**
	 * Méthode nécessaire pour que contains et indexOf de ArrayList fonctionnent
	 * Attention : equals(Object) et non equals(Devise)
	 */
	public boolean equals(Object d) {
		
		if (d instanceof Devise) {
			return ((Devise)d).nom.equals(this.nom);
		}
		
		return false;
	}

	/**
	 * Méthode de l'interface Comparable à implementer pour
	 * pouvoir utiliser Collections.sort(liste)
	 * 
	 * @param o la devise à comparer
	 * @return un entier permettant de trier
	 */
	public int compareTo(Devise o) {
		
		return this.nom.compareTo(o.nom);
	}
	
}
