package com.portefeuille.dg.devise.exceptions;

import java.io.Serializable;

/**
 * Exception "maison" permettant de lancer la suppression effective
 * d'une devise si elle est tombée à 0
 * 
 * Voir la méthode retrait dans la classe Portefeuille
 * 
 */
public class DeviseDevientNulleException extends RuntimeException {

	public DeviseDevientNulleException(String message) {
	
		super(message);
	}

}
