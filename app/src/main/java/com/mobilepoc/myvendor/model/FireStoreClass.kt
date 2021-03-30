package com.mobilepoc.myvendor.model

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.view.activities.LoginActivity
import com.mobilepoc.myvendor.view.activities.RegisterActivity
import com.mobilepoc.myvendor.view.activities.SettingsActivity
import com.mobilepoc.myvendor.view.activities.UserProfileActivity

class FireStoreClass {

    // Acessar a instância na nuvem - Firestore .
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * Função que registra uma usuário na base de dados FireStore .
     */
    fun registraUsuario(activity: RegisterActivity, userInfo: User) {

        // "users" é o nome da coleção. Se a coleção já tiver sido criada, ela não criará a novamente .
        mFireStore.collection(Constants.USERS)
            // ID do usuário
            .document(userInfo.id)
            //userInfo são campos e SetOption está configurado para mesclar. Caso quisermos juntar mais tarde em vez de substituir os campos. .
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Transfere os resultados para a activity.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Falha ao tentar registrar um usuário.",
                    e
                )
            }
    }
    /**
     * Função que pega o ID do usuário logado
     */
    fun getUserIDAtual(): String {
        // Uma instância de Usuario Atual usando FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Aqui, passamos o nome da coleção da qual queremos os dados.
        mFireStore.collection(Constants.USERS)
            // O id do documento para obter os campos do usuário.
            .document(getUserIDAtual())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())
                //conversao do documento para objeto
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYVENDOR_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Crie uma instância do editor que nos ajude a editar o SharedPreference. .
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                       //Transfere os dados para uma activity
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Falha ao tentar carregar os detalhes do perfil.",
                    e
                )
            }
    }
    /**
     * Uma função para atualizar os dados do perfil do usuário no banco de dados.
     *
     * @param activity usada para identificar a Activity Base para a qual o resultado é passado.
     * @param userHashMap HashMap, quais campos devem ser atualizado.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Nome da Coleção = Users
        mFireStore.collection(Constants.USERS)
            // Relação ao qual os dados devem ser atualizados. Aqui, o ID do documento é o ID do usuário conectado no momento.
            .document(getUserIDAtual())
            // A HashMap campos devem ser atualizado.
            .update(userHashMap)
            .addOnSuccessListener {

                // Sucesso
                when (activity) {
                    is UserProfileActivity -> {
                        // Transferir o resultado para uma Activity
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Erro enquanto atualiza os detalhes do usuário",
                    e
                )
            }
    }

    /**
     * Uma função para fazer upload da imagem para o cloud storage.
     */
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        //obtendo a referência de armazenamento
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileURI)
        )

        //adicionando o arquivo para referência
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // url para download no instantâneo da tarefa
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        //Transfere os dados para a activity
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }

                    }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

}