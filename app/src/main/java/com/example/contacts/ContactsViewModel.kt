package com.example.contacts

import androidx.lifecycle.ViewModel

/**
 * Exercice 5 : le ViewModel survit à la rotation de l'écran.
 *
 * Lors d'une rotation, Android détruit puis recrée l'activité : toutes ses
 * propriétés (dont la liste) sont réinitialisées. Le ViewModel, lui, est conservé
 * tant que l'écran n'est pas définitivement fermé : on y stocke donc les données.
 */
class ContactsViewModel : ViewModel() {

    // Les données : une liste MODIFIABLE (mutableListOf) de chaînes simples
    val contacts = mutableListOf(
        "Ahmed Ben Ali",
        "Sarra Trabelsi",
        "Mohamed Gharbi",
        "Ines Jaziri",
        "Youssef Mansouri",
        "Amira Chaabane",
        "Karim Bouazizi",
        "Nour Hammami"
    )

    // Position de l'élément sélectionné (-1 = aucune sélection)
    var positionSelectionnee = -1
}
