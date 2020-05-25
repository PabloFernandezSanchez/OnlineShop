package net.azarquiel.onlineshop.views

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_log_in.*
import net.azarquiel.onlineshop.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class LogInActivity : AppCompatActivity() {

    private var logorreg: Boolean = false
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        db = FirebaseFirestore.getInstance()

        registerbutton.setOnClickListener {
            if(edEmail.text.isNotEmpty() && edPassword.text.isNotEmpty()){
                val int = Intent(this, MainActivity::class.java)
                val email = edEmail.text.toString()
                val pass = edPassword.text.toString()
                val dinero = hashMapOf(
                    "dinero" to "0",
                    "email" to email
                )
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        logorreg = true
                        db.collection("users").document(email)
                            .set(dinero as Map<String, Any>)
                            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
                        showMainActivity(int, email)
                    }else{
                        showAlert()
                    }
                }
            }

        }
       loginbutton.setOnClickListener {
            if(edEmail.text.isNotEmpty() && edPassword.text.isNotEmpty()){
                val int = Intent(this, MainActivity::class.java)
                val email = edEmail.text.toString()
                val pass = edPassword.text.toString()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        showMainActivity(int, email)
                    }else{
                        showAlert1()
                    }
                }
            }

        }
    }

    private fun showAlert(){
        alert("Ya existe una cuenta con este Email."){
            title = "Error"
            yesButton { }
        }.show()
    }

    private fun showAlert1(){
        alert("Email o Password incorrectos."){
            title = "Error"
            yesButton { }
        }.show()
    }

    private fun showMainActivity(int: Intent, email: String) {
        int.putExtra("email",email)
        int.putExtra("logorreg", logorreg)
        setResult(Activity.RESULT_OK,int)
        finish()
    }
}
