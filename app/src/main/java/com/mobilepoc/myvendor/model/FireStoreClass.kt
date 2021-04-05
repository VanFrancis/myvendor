package com.mobilepoc.myvendor.model

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.view.activities.*
import com.mobilepoc.myvendor.view.fragments.DashboardFragment
import com.mobilepoc.myvendor.view.fragments.ProductsFragment

class FireStoreClass {
    // Acessar a instância na nuvem - Firestore .
    val mFireStore = FirebaseFirestore.getInstance()

    fun onCreate(savedInstanceState: Bundle?) {
        //aumentar o tamanho de armazenamento offiline
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(
                200 * 1024 * 1024 // 200 MB
            )
            .build()
        mFireStore?.firestoreSettings = settings

    }

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
                //TODO ESCONDER O BARRA PROGRESSO
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
                when (activity) {
                    is LoginActivity -> {
                        //TODO ESCONDER O BARRA PROGRESSO
                    }
                    is SettingsActivity -> {
                        //TODO ESCONDER O BARRA PROGRESSO
                    }
                }
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

                when (activity) {
                    is UserProfileActivity -> {
                        //TODO ESCONDER O BARRA PROGRESSO
                    }
                }
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
                when (activity) {
                    is UserProfileActivity -> {
                        //TODO ESCONDER O BARRA PROGRESSO
                    }

                    is AddProductActivity -> {
                        //TODO ESCONDER O BARRA PROGRESSO
                    }
                }
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
                    //TODO ESCONDER O BARRA PROGRESSO
                    Log.e(
                        activity.javaClass.simpleName,
                        "Erro ao enviar os detalhes do produto",
                        e
                    )
                }
    }

    /**
     * Uma função para obter a lista de produtos do Firestore.
     *
     * @param fragment O fragmento é passado como parâmetro conforme a função é chamada a partir do fragmento e precisa para o resultado de sucesso.
     */
    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getUserIDAtual())
                .get() // Obterá os snapshots dos documentos.
                .addOnSuccessListener { document ->

                    // Log com a lista de produtos
                    Log.e("Products List", document.documents.toString())

                    // Cria um arraylist.
                    val productsList: ArrayList<Product> = ArrayList()

                    // Um loop  que percorre a lista de documentos convertendo produtos em um ArrayList  .
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
                    when (fragment) {
                        is ProductsFragment -> {
                            //TODO ESCONDER O BARRA PROGRESSO
                        }
                    }
                    Log.e("Get Product List", "Erro ao obter lista de produtos.", e)
                }
    }
    /**
     * Uma função para obter a lista de itens do painel. A lista será uma lista geral de itens, não baseada na id do usuário
     */
    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->

                    // Log com a lista de produtos
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    // Cria um ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // Um loop  que percorre a lista de documentos convertendo produtos em um ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productsList.add(product)
                    }

                    // Passe o resultado do sucesso para base fragment.
                    fragment.successDashboardItemsList(productsList)
                }
                .addOnFailureListener { e ->
                    //TODO ESCONDER O BARRA PROGRESSO
                    Log.e(
                        fragment.javaClass.simpleName,
                        "Erro ao obter a lista de itens do painel.",
                        e
                    )
                }
    }
    /**
     * Uma função para excluir o produto  - Firestore
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()

                }
                .addOnFailureListener { e ->
                    //TODO ESCONDER O BARRA PROGRESSO
                    Log.e(
                        fragment.requireActivity().javaClass.simpleName,
                        "Erro ao excluir o produto.",
                        e
                    )
                }
    }

    fun getProdructDetails(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.toString())
                    val product = document.toObject(Product::class.java) //cria o objeto Produto
                    if (product != null){
                        activity.productDetailsSuccess(product)
                    }

                }
                .addOnFailureListener{ e ->
                    //TODO ESCONDER O BARRA PROGRESSO
                    Log.e(activity.javaClass.simpleName, "Erro enquanto carregava dados do produto", e)
                }
    }
    /**
     * Uma função para adicionar o item ao carrinho no firestore.
     * @param activity
     * @param addToCart
     */
    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {

        mFireStore.collection(Constants.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->
                    //TODO ESCONDER O BARRA PROGRESSO
                    Log.e(
                            activity.javaClass.simpleName,
                            "Erro ao criar uma coleção para o item do carrinho.",
                            e
                    )
                }
    }

    /**
     * Função que verifica se existe o item no carrinho
     */
    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID,getUserIDAtual())
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener{ document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    //Se o tamanho do documento for maior que 1, significa que o produto já foi adicionado ao carrinho.
                    if (document.documents.size > 0) {
                        activity.productExistsInCart()
                    } else {
                        //TODO ESCONDER O BARRA PROGRESSO
                    }

                }.addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName, "Erro enquanto checava se o item existe no carrinho")
                }
    }




}