# TP Android — ListView de contacts modifiable

## Ouvrir le projet
1. Dans Android Studio : **File > Open** et choisir le dossier `ContactsListView`.
2. Laisser Gradle se synchroniser, puis cliquer sur **Run**.

(Ou bien créer le projet « Empty Views Activity » comme indiqué dans le sujet et remplacer
les fichiers par ceux de ce dossier.)

## Fichiers
| Fichier | Rôle |
|---|---|
| `app/build.gradle.kts` | dépendances (core-ktx, appcompat + activity-ktx et lifecycle pour le ViewModel) |
| `res/values/strings.xml` | tous les textes de l'application |
| `res/layout/activity_main.xml` | EditText + Modifier, bouton Ajouter, ListView |
| `ContactsViewModel.kt` | la liste des contacts et la position sélectionnée |
| `MainActivity.kt` | adaptateur, clics, modification, ajout, suppression, tri |
| `AndroidManifest.xml` | déclaration de l'activité de lancement |

## Partie principale (sections 1 à 5)
- `mutableListOf` → la liste peut être modifiée (`contacts[i] = ...`), ce qui serait impossible avec `listOf`.
- `ArrayAdapter` + `simple_list_item_activated_1` → affichage avec surbrillance de la ligne choisie.
- `setOnItemClickListener` → copie le contact dans l'EditText et mémorise la position.
- Bouton **Modifier** → contrôles (aucune sélection → Toast, texte vide → `etContact.error`),
  puis `contacts[position] = nouveauNom` et **`adapter.notifyDataSetChanged()`**.
  Sans cet appel, la donnée change mais l'écran n'est pas redessiné.

## Exercices pour aller plus loin
1. **Ajouter** : bouton « Ajouter » → `contacts.add(nouveauNom)` puis `notifyDataSetChanged()`.
2. **Supprimer** : `setOnItemLongClickListener` ouvre un `AlertDialog` ; si l'utilisateur confirme,
   `contacts.removeAt(position)`. Le listener renvoie `true` pour que le clic simple ne se déclenche pas.
   La sélection est annulée car les positions des éléments suivants ont changé.
3. **Doublons** : fonction `existeDeja()` (comparaison sans tenir compte des majuscules).
   En modification, on ignore l'élément lui-même pour pouvoir, par exemple, juste corriger la casse.
4. **Tri** : après chaque modification ou ajout, `contacts.sortWith(String.CASE_INSENSITIVE_ORDER)`.
   Comme le contact a pu changer de place, on recalcule sa position avec `indexOf()` et on remet la surbrillance.
5. **Réflexion — rotation de l'écran** :
   Quand on tourne l'écran, Android **détruit puis recrée l'activité** (`onDestroy()` → `onCreate()`).
   La liste étant une propriété de l'activité, elle est recréée avec ses valeurs initiales :
   les modifications sont perdues. (Le texte de l'EditText est conservé, car Android sauvegarde
   automatiquement l'état des vues qui ont un id.)
   **Solution** : placer les données dans un **ViewModel** (`ContactsViewModel`), récupéré avec
   `by viewModels()`. Le ViewModel survit aux changements de configuration et n'est détruit que
   lorsque l'écran est fermé définitivement. Les modifications restent donc après rotation.
   Remarque : elles sont tout de même perdues si l'application est fermée ; pour une sauvegarde
   permanente il faudrait une base de données (Room) ou des SharedPreferences.

## Tests réalisés (section 5 du sujet)
1. Clic sur « Sarra Trabelsi » → ligne surlignée, nom affiché en haut.
2. Remplacer par « Sarra Ben Salah » → **Modifier** → liste mise à jour (et triée).
3. Champ vide → **Modifier** → icône et message d'erreur dans le champ.
4. Au lancement, sans sélection → **Modifier** → Toast « Sélectionnez d'abord un contact ».
5. Saisir « Ines Jaziri » sur un autre contact → **Modifier** → erreur « Ce nom existe déjà ».
6. Appui long sur un contact → **Supprimer** → contact retiré.
7. Modifier un contact puis tourner l'écran → la modification est conservée.
