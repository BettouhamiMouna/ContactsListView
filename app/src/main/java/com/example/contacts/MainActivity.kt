package com.example.contacts

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Les données vivent dans le ViewModel (exercice 5) pour survivre à la rotation
    private val viewModel: ContactsViewModel by viewModels()
    private val contacts get() = viewModel.contacts

    // L'adaptateur fait le lien entre les données et la ListView
    private lateinit var adapter: ArrayAdapter<String>

    // Les vues
    private lateinit var etContact: EditText
    private lateinit var btnModifier: Button
    private lateinit var btnAjouter: Button
    private lateinit var lvContacts: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupération des vues déclarées dans activity_main.xml
        etContact = findViewById(R.id.etContact)
        btnModifier = findViewById(R.id.btnModifier)
        btnAjouter = findViewById(R.id.btnAjouter)
        lvContacts = findViewById(R.id.lvContacts)

        // Création de l'adaptateur avec un layout Android prédéfini
        // simple_list_item_activated_1 met en surbrillance l'élément sélectionné
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            contacts
        )
        lvContacts.adapter = adapter

        // Après une rotation, on remet la surbrillance sur l'élément sélectionné
        if (viewModel.positionSelectionnee != -1) {
            lvContacts.setItemChecked(viewModel.positionSelectionnee, true)
        }

        // Clic sur un élément de la liste → on copie son texte dans l'EditText
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            viewModel.positionSelectionnee = position
            lvContacts.setItemChecked(position, true)
            etContact.setText(contacts[position])
            etContact.setSelection(etContact.text.length) // curseur à la fin
            etContact.requestFocus()
        }

        // Exercice 2 : appui long → suppression après confirmation
        lvContacts.setOnItemLongClickListener { _, _, position, _ ->
            confirmerSuppression(position)
            true // l'événement est consommé : le clic simple ne se déclenche pas
        }

        // Clic sur le bouton "Modifier"
        btnModifier.setOnClickListener { modifierContact() }

        // Exercice 1 : clic sur le bouton "Ajouter"
        btnAjouter.setOnClickListener { ajouterContact() }

        // Bonus : la touche "Terminé" du clavier valide aussi la modification
        etContact.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifierContact()
                true
            } else {
                false
            }
        }
    }

    private fun modifierContact() {
        val position = viewModel.positionSelectionnee

        // Vérification 1 : un élément doit être sélectionné
        if (position == -1) {
            Toast.makeText(this, R.string.msg_aucune_selection, Toast.LENGTH_SHORT).show()
            return
        }

        // Vérification 2 : le texte ne doit pas être vide
        val nouveauNom = lireNomSaisi() ?: return

        // Vérification 3 (exercice 3) : pas de doublon
        // (on ignore l'élément en cours de modification lui-même)
        if (existeDeja(nouveauNom, positionIgnoree = position)) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        // Mise à jour de la donnée...
        contacts[position] = nouveauNom
        // Exercice 4 : tri alphabétique, puis on resélectionne le contact modifié
        trierEtSelectionner(nouveauNom)
        // ...puis on prévient l'adaptateur pour qu'il redessine la liste
        adapter.notifyDataSetChanged()

        Toast.makeText(
            this, getString(R.string.msg_contact_modifie, nouveauNom), Toast.LENGTH_SHORT
        ).show()
    }

    // Exercice 1 : ajout d'un nouveau contact
    private fun ajouterContact() {
        val nouveauNom = lireNomSaisi() ?: return

        if (existeDeja(nouveauNom)) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        contacts.add(nouveauNom)
        trierEtSelectionner(nouveauNom)
        adapter.notifyDataSetChanged()
        lvContacts.smoothScrollToPosition(viewModel.positionSelectionnee)

        Toast.makeText(
            this, getString(R.string.msg_contact_ajoute, nouveauNom), Toast.LENGTH_SHORT
        ).show()
    }

    // Exercice 2 : boîte de dialogue de confirmation puis suppression
    private fun confirmerSuppression(position: Int) {
        val nom = contacts[position]
        AlertDialog.Builder(this)
            .setTitle(R.string.titre_suppression)
            .setMessage(getString(R.string.msg_confirmer_suppression, nom))
            .setPositiveButton(R.string.btn_supprimer) { _, _ ->
                contacts.removeAt(position)
                // Les positions ont changé : on annule la sélection
                viewModel.positionSelectionnee = -1
                lvContacts.clearChoices()
                etContact.text.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(
                    this, getString(R.string.msg_contact_supprime, nom), Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.btn_annuler, null)
            .show()
    }

    /** Lit le texte saisi ; affiche une erreur et renvoie null s'il est vide. */
    private fun lireNomSaisi(): String? {
        val nom = etContact.text.toString().trim()
        if (nom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return null
        }
        return nom
    }

    /** Exercice 3 : vrai si le nom existe déjà (sans tenir compte des majuscules). */
    private fun existeDeja(nom: String, positionIgnoree: Int = -1): Boolean =
        contacts.withIndex().any { (i, c) ->
            i != positionIgnoree && c.equals(nom, ignoreCase = true)
        }

    /** Exercice 4 : trie la liste puis met à jour la position du contact sélectionné. */
    private fun trierEtSelectionner(nom: String) {
        contacts.sortWith(String.CASE_INSENSITIVE_ORDER)
        val nouvellePosition = contacts.indexOf(nom)
        viewModel.positionSelectionnee = nouvellePosition
        lvContacts.setItemChecked(nouvellePosition, true)
    }
}
