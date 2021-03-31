package com.mobilepoc.myvendor.model

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.ImageHeaderParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.view.activities.*
import com.mobilepoc.myvendor.view.fragments.DashboardFragment
import com.mobilepoc.myvendor.view.fragments.ProductsFragment

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
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //obtendo a referência de armazenamento
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "."
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
                            is AddProductActivity -> {
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

    /**
     * Função para fazer uma entrada do produto do usuário - Firestore.
     */
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        //Rotina parecida com a do Usuário, cria a coleção e configura os campos e dps envia
        mFireStore.collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {

                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(
                            activity.javaClass.simpleName,
                            "Erro ao enviar os detalhes do produto",
                            e
                    )
                }
    }

    /**
     * A function to get the products list from cloud firestore.
     *
     * @param fragment The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getUserIDAtual())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e("Products List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productsList.add(product)
                    }

                    when (fragment) {
                        is ProductsFragment -> {
                            fragment.successProductsListFromFireStore(productsList)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is ProductsFragment -> {
                           // fragment.hideProgressDialog()
                        }
                    }
                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }
    /**
     * A function to get the dashboard items list. The list will be an overall items list, not based on the user's id.
     */
    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productsList.add(product)
                    }

                    // Pass the success result to the base fragment.
                    fragment.successDashboardItemsList(productsList)
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error which getting the dashboard items list.
                   // fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
                }
    }
    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()

                }
                .addOnFailureListener { e ->
                    Log.e(
                            fragment.requireActivity().javaClass.simpleName,
                            "Error while deleting the product.",
                            e
                    )
                }
    }



}