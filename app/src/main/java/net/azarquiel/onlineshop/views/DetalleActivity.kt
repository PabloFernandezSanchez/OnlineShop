package net.azarquiel.onlineshop.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle.*
import kotlinx.android.synthetic.main.row.view.*
import net.azarquiel.onlineshop.R
import net.azarquiel.onlineshop.model.Producto

class DetalleActivity : AppCompatActivity() {

    private lateinit var productopulsado:Producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)
        productopulsado = intent.getSerializableExtra("productopulsado") as Producto
        diseño()
    }

    private fun diseño() {
        tvDescDetalle.text = productopulsado.desc
        tvPercioDetalle.text = productopulsado.precio
        tvTituloDetalle.text = productopulsado.titulo
        Picasso.get().load(productopulsado.imagen2).into(ivDetalle)
    }
}
