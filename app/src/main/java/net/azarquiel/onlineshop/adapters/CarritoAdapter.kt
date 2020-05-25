package net.azarquiel.onlineshop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.rowcarrito.view.*
import net.azarquiel.onlineshop.model.Carrito
import net.azarquiel.onlineshop.views.MainActivity

class CarritoAdapter(val context: Context,
                     val layout: Int
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    private var dataList: List<Carrito> = emptyList()

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

    internal fun setCarritos(carritos: List<Carrito>) {
        this.dataList = carritos
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Carrito){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.tvpreciocarrito.text = dataItem.precio
            itemView.tvtitulocarrito.text = dataItem.titulo
            Picasso.get().load(dataItem.imagen).into(itemView.ivcarrito)
            itemView.tvtallacarro.text = dataItem.talla
            itemView.imageButton.tag = dataItem
            itemView.tag = dataItem
        }

    }
}