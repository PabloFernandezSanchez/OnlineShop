package net.azarquiel.onlineshop.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_monedero.*
import net.azarquiel.onlineshop.R

class MonederoActivity : AppCompatActivity() {

    private lateinit var dinero:String
    private lateinit var dinero2:String
    private lateinit var email:String
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monedero)

        db = FirebaseFirestore.getInstance()
        dinero = intent.getSerializableExtra("dinero") as String
        email = intent.getSerializableExtra("email") as String
        tvDinero.text = "$dinero €"
    }

    fun clickAñadirDinero(view: View) {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        if (document["email"] == email) {
                            dinero2 = document["dinero"] as String
                        }
                    }
                        val aSumar = edDinero.text.toString().toInt()
                        val dineroFinal = dinero2.toInt() + aSumar
                        tvDinero.text = "$dineroFinal €"
                        val dinero = hashMapOf(
                            "dinero" to dineroFinal.toString(),
                            "email" to email
                        )
                        db.collection("users").document(email)
                            .set(dinero as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.d(
                                    "TAG",
                                    "DocumentSnapshot successfully written!"
                                )
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

                    } else {
                         Log.w(MainActivity.TAG, "Error getting documents.", task.exception)
                    }
                }
            }
}
