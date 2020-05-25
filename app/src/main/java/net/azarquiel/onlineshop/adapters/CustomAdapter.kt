package net.azarquiel.onlineshop.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.row.view.*
import net.azarquiel.onlineshop.R
import net.azarquiel.onlineshop.model.Producto
import net.azarquiel.onlineshop.views.MainActivity
import org.jetbrains.anko.spinner

class CustomAdapter(val context: Context,
                    val layout: Int
                    ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<Producto> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setProductos(productos: List<Producto>) {
        this.dataList = productos
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Producto){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.tvprecio.text = dataItem.precio
            itemView.tvtitulo.text = dataItem.titulo
            Picasso.get().load("${dataItem.imagen}").into(itemView.ivproducto)
            var adapter :ArrayAdapter<CharSequence> = ArrayAdapter <CharSequence> (context, android.R.layout.simple_spinner_item )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            var tallas: List<String> = arrayListOf("XS", "S", "M", "L", "XL", "XXL")
            tallas.forEach{
                adapter.add(it)
            }
            Log.d("Pabloo", dataItem.imagen2)
            itemView.spinner.adapter = adapter
            itemView.btnAñadir.tag = dataItem
            itemView.ivproducto.tag = dataItem
            itemView.tag = dataItem

        }

    }
}