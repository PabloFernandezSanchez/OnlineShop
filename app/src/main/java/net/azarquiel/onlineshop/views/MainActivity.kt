package net.azarquiel.onlineshop.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.onlineshop.R
import net.azarquiel.onlineshop.adapters.CarritoAdapter
import net.azarquiel.onlineshop.adapters.CustomAdapter
import net.azarquiel.onlineshop.adapters.InicioAdapter
import net.azarquiel.onlineshop.model.*
import org.jetbrains.anko.*
import java.security.Provider

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "OnlineShop"
        private const val REQUESTADD=0
    }

    private lateinit var adapter: CustomAdapter
    private lateinit var adapterInicio: InicioAdapter
    private lateinit var adapterCarrito: CarritoAdapter
    private lateinit var db: FirebaseFirestore
    private var productos: ArrayList<Producto> = ArrayList()
    private var carrito: ArrayList<Carrito> = ArrayList()
    private var inicio: ArrayList<Inicio> = ArrayList()
    private lateinit var docRef: CollectionReference
    private var entrarlogin: Boolean = true
    private lateinit var spin : Spinner
    private lateinit var email : String
    private var dinero : String="0"
    private var dineroCarro : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db = FirebaseFirestore.getInstance()
        setListener(0)
        fabCompra.hide()


        fab.setOnClickListener {
            if(entrarlogin){
                showLogin()
            }else{
                alert("¿Seguro que quieres cerrar sesion con $email?"){
                    yesButton { entrarlogin= true
                        toast("Sesion cerrada")}
                    noButton {  }
                }.show()
            }
        }
        fabCompra.setOnClickListener { borrar() }
    }

    private fun showLogin(){
        val myintent= Intent(this,LogInActivity::class.java)
        startActivityForResult(myintent, REQUESTADD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUESTADD) {
                    email = data?.getSerializableExtra("email") as String
                    val logorreg = data.getSerializableExtra("logorreg") as Boolean
                    entrarlogin = false
                    if(logorreg){
                        toast("$email se ha registrado")
                    }else{
                        toast("Sesion iniciada con $email")
                    }
                }
            }
    }

    private fun borrar(){
        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialogo_compra, null)
        dialog.setView(view)
        val builder = dialog.create()
        builder.show()
        val btnOK = view.findViewById<Button>(R.id.buttonFin)
        val btnCerrar = view.findViewById<Button>(R.id.buttonCancel)
        var edDireccion = view.findViewById<EditText>(R.id.eddireccion)
        var edTelef = view.findViewById<EditText>(R.id.edTelef)
        btnCerrar.setOnClickListener { builder.cancel() }
        btnOK.setOnClickListener {
            if(edDireccion.text.isNotEmpty() && edTelef.text.isNotEmpty()){
                db.collection("carrito").document(email).collection(email)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                Log.d(TAG, document.id + " => " + document.data)
                                procesarData(document.data)
                                val dinero = document["precio"] as String
                                dineroCarro+=dinero.toInt()
                            }
                            Log.d("Pablo", dineroCarro.toString())
                            db.collection("users")
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (document in task.result!!) {
                                            if(document["email"]==email){
                                                dinero = document["dinero"] as String
                                            }
                                        }
                                            if (dinero.toInt()>dineroCarro){
                                                    db.collection("ids")
                                                        .get()
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                for (document in task.result!!) {
                                                                    Log.d(TAG, document.id + " => " + document.data)
                                                                    procesarData(document.data)
                                                                }
                                                                val result = task.result!!
                                                                result.forEach { d ->
                                                                    val id = d["id"] as String
                                                                    val idofthisdoc = d["idofthisdoc"] as String
                                                                    db.collection("carrito").document(email).collection(email).document(id)
                                                                        .delete()
                                                                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                                                                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                                                                    db.collection("ids").document(idofthisdoc)
                                                                        .delete()
                                                                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                                                                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                                                                }
                                                                val dineroReal = (dinero.toInt()-dineroCarro).toString()
                                                                Log.d("Peblo", dineroReal)
                                                                val dinero = hashMapOf(
                                                                    "dinero" to dineroReal,
                                                                    "email" to email
                                                                )
                                                                db.collection("users").document(email)
                                                                    .set(dinero as Map<String, Any>)
                                                                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                                                                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

                                                                dineroCarro = 0
                                                            }else {
                                                                Log.w(TAG,"Error getting documents.", task.exception)
                                                            }
                                                }
                                                setListener(0)
                                                fab.visibility = View.VISIBLE
                                                fabCompra.hide()
                                                longToast("Su pedido ha sido realizado.")
                                            }else{
                                                dineroCarro = 0
                                                toast("Saldo insuficiente.")
                                            }
                                    } else {
                                        Log.w(
                                            TAG, "Error getting documents.", task.exception
                                        )
                                    }
                                }
                            builder.cancel()
                        } else {
                            Log.w(
                                TAG,"Error getting documents.", task.exception
                            )
                        }
                    }
            } else{
                toast("Rellena los campos")
            }
        }

    }

    private fun putRV() {
        adapter = CustomAdapter(this, R.layout.row)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
    }

    private fun setListener(num: Int) {
        if(num == 0) {
            docRef = db.collection("inicio")
            changeAdapter()
        }else if(num == 1){
            docRef = db.collection("productos").document("chico").collection("parte_superior")
            putRV()
        }else if(num == 2){
            docRef = db.collection("productos").document("chico").collection("parte_inferior")
            putRV()
        }else if(num == 3){
            docRef = db.collection("productos").document("chico").collection("exclusivo")
            putRV()
        }else if(num == 4){
            docRef = db.collection("productos").document("chica").collection("parte_superior")
            putRV()
        }else if(num == 5){
            docRef = db.collection("productos").document("chica").collection("parte_inferior")
            putRV()
        }else if(num == 6){
            docRef = db.collection("productos").document("chica").collection("exclusivo")
            putRV()
        }else if(num == 7){
            docRef = db.collection("carrito").document(email).collection(email)
            putRV2()
        }
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                if (num==0){
                    documentToList2(snapshot.documents)
                    adapterInicio.setInicio(inicio)
                }else if(num==7){
                    documentToList3(snapshot.documents)
                    adapterCarrito.setCarritos(carrito)
                }else{
                    documentToList(snapshot.documents)
                    adapter.setProductos(productos)
                }
            } else {
                Log.d(TAG, "Current data: null")
                carrito= ArrayList()
                adapterCarrito.setCarritos(carrito)
            }
        }

    }

    private fun documentToList(documents: List<DocumentSnapshot>) {
        productos.clear()
        documents.forEach { d ->
            val imagen = d["imagen"] as String
            val precio = d["precio"] as String
            val titulo = d["titulo"] as String
            val desc = d["desc"] as String
            val imagen2 = d["imagen2"] as String
            productos.add(Producto(imagen = imagen,precio = precio, titulo = titulo, desc = desc, imagen2 = imagen2))
        }
    }

    private fun documentToList3(documents: List<DocumentSnapshot>) {
        carrito.clear()
        documents.forEach { d ->
            val imagen = d["imagen"] as String
            val precio = d["precio"] as String
            val titulo = d["titulo"] as String
            val talla = d["talla"] as String
            carrito.add(Carrito(imagen = imagen,precio = precio, titulo = titulo, talla = talla))
        }
    }

    private fun documentToList2(documents: List<DocumentSnapshot>) {
        inicio.clear()
        documents.forEach { d ->
            val imagen1 = d["imagen1"] as String
            val imagen2 = d["imagen2"] as String
            val imagen3 = d["imagen3"] as String
            val titulo1 = d["titulo1"] as String
            val titulo2 = d["titulo2"] as String
            val titulo3 = d["titulo3"] as String
            inicio.add(Inicio(imagen1 = imagen1,imagen2 = imagen2,imagen3 = imagen3, titulo1 = titulo1, titulo2 = titulo2, titulo3 = titulo3))
        }
    }

    private fun changeAdapter() {
        adapterInicio = InicioAdapter(this, R.layout.rowinicio)
        rv.adapter = adapterInicio
        rv.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.inicio ->{
                setListener(0)
                fab.visibility = View.VISIBLE
                fabCompra.hide()
                true
            }
            R.id.pshombre ->{
                setListener(1)
                fabs()
                true
            }
            R.id.pihombre ->{
                setListener(2)
                fabs()
                true
            }
            R.id.zapatoshombre ->{
                setListener(3)
                fabs()
                true
            }
            R.id.psmujer ->{
                setListener(4)
                fabs()
                true
            }
            R.id.pimujer ->{
                setListener(5)
                fabs()
                true
            }
            R.id.zapatosmujer ->{
                setListener(6)
                fabs()
                true
            }
            R.id.monedero ->{
                if(!entrarlogin) {
                    showMonedero()
                }else{
                    alert("Tienes que iniciar sesión con tu cuenta de OnlineShop  o registrarte en el caso de no tener. Para ello pulsa el botón inferior izquierdo de Inicio."){
                        title="Inicia Sesión"
                        yesButton { }
                    }.show()
                }
                true
            }
            R.id.carrito ->{
                if(!entrarlogin) {
                    var cont = 0
                    db.collection("carrito").document(email).collection(email)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for (document in task.result!!) {
                                    cont++
                                    Log.d(TAG, cont.toString())
                                }
                                if (cont != 0) {
                                    setListener(7)
                                    fab.visibility = View.INVISIBLE
                                    fabCompra.show()
                                } else {
                                    longToast("¡Tu cesta esta vacía!")
                                }
                            } else {
                                Log.w(
                                    TAG, "Error getting documents.", task.exception
                                )
                            }
                        }
                }else{
                    alert("Tienes que iniciar sesión con tu cuenta de OnlineShop  o registrarte en el caso de no tener. Para ello pulsa el botón inferior izquierdo de Inicio."){
                        title="Inicia Sesión"
                        yesButton { }
                    }.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMonedero(){
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if(document["email"]==email){
                        dinero = document["dinero"] as String
                    }
                }
                Log.d(TAG,dinero)
                val myintent= Intent(this,MonederoActivity::class.java)
                myintent.putExtra("dinero", dinero)
                myintent.putExtra("email", email)
                startActivity(myintent)
            } else {
                Log.w(
                    TAG, "Error getting documents.", task.exception
                )
            }
        }
    }

    private fun fabs(){
        fab.visibility = View.INVISIBLE
        fabCompra.hide()
    }

    fun clickDetalle(v: View){
        val productopulsado=v.tag as Producto
        val myintent= Intent(this,DetalleActivity::class.java)
        myintent.putExtra("productopulsado", productopulsado)
        startActivity(myintent)
    }

    fun clickAñadir(v: View){
        if(!entrarlogin) {
            var esta = false
            val productopulsado=v.tag as Producto
            spin = findViewById(R.id.spinner)
            val tallas: List<String> = arrayListOf("XS", "S", "M", "L", "XL", "XXL")
            val pos = spin.selectedItemPosition
    //        Log.d(TAG, pos.toString())
            var cont = 0
            tallas.forEach{
                if(cont==pos){
                    productopulsado.talla=it
                }
                cont++
            }
            db.collection("carrito").document(email).collection(email)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Log.d(TAG,document.id + " => " + document.data)
                            procesarData(document.data)
                        }
                        val result = task.result!!
                        result.forEach { d ->
                            val titulo = d["titulo"] as String
                            if(titulo==productopulsado.titulo){
                                esta=true
                            }
                        }
                        if(!esta){
                            db.collection("carrito").document(email).collection(email)
                                .add(productopulsado)
                                .addOnSuccessListener { documentReference ->
                                    Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
                                    val data = hashMapOf(
                                        "id" to documentReference.id
                                    )
                                    db.collection("ids")
                                        .add(data as Map<String, Any>)
                                        .addOnSuccessListener { documentReference2 ->
                                            Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference2.id}")
                                            val data2 = hashMapOf(
                                                "id" to documentReference.id,
                                                "idofthisdoc" to documentReference2.id,
                                                "titulo" to productopulsado.titulo
                                            )
                                            db.collection("ids").document(documentReference2.id)
                                                .set(data2 as Map<String, Any>, SetOptions.merge())
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding document", e)
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                            toast("Artículo añadido al carro.")
                        }else{
                            longToast("El artículo ya se encuentra en el carro.")
                        }
                    } else {
                        Log.w(
                            TAG,"Error getting documents.", task.exception
                        )
                    }
                }
        }else{
            alert("Tienes que iniciar sesión con tu cuenta de OnlineShop  o registrarte en el caso de no tener. Para ello pulsa el botón inferior izquierdo de Inicio."){
                title="Inicia Sesión"
                yesButton { }
            }.show()
        }
    }

    private fun putRV2() {
        adapterCarrito = CarritoAdapter(this, R.layout.rowcarrito)
        rv.adapter = adapterCarrito
        rv.layoutManager = LinearLayoutManager(this)
    }

    private fun procesarData(data: Map<String, Any>) {
        for ((k, v) in data){
            Log.d(TAG, "$k => $v")
        }
    }

    fun clickBorrar(v: View){
        val carritopulsado=v.tag as Carrito
        db.collection("carrito").document(email).collection(email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        procesarData(document.data)
                    }
                    var result = task.result!!
                    result.forEach { d ->
                        val titulo = d["titulo"] as String
                        if (titulo == carritopulsado.titulo) {
                            db.collection("ids")
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (document in task.result!!) {
                                            Log.d(TAG, document.id + " => " + document.data)
                                            procesarData(document.data)
                                        }
                                        val result = task.result!!
                                        result.forEach { d ->
                                            val id = d["id"] as String
                                            val idofthisdoc = d["idofthisdoc"] as String
                                            val titulo2 = d["titulo"] as String
                                            if (titulo2==titulo) {
                                                db.collection("carrito").document(email).collection(email).document(id)
                                                    .delete()
                                                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                                                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                                                db.collection("ids").document(idofthisdoc)
                                                    .delete()
                                                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                                                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                                                toast("Artículo eliminado del carro.")
                                            }
                                        }
                                    }else {
                                        Log.w(
                                            TAG,"Error getting documents.", task.exception
                                        )
                                    }
                                }
                        }
                    }
                } else {
                    Log.w(
                        TAG,"Error getting documents.", task.exception
                    )
                }
            }
    }

}
