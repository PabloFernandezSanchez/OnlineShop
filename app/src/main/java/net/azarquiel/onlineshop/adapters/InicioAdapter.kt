package net.azarquiel.onlineshop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rowinicio.view.*
import net.azarquiel.onlineshop.model.Inicio
import java.util.*

class InicioAdapter(val context: Context,
                    val layout: Int
) : RecyclerView.Adapter<InicioAdapter.ViewHolder>() {

    private var dataList: List<Inicio> = emptyList()

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

    internal fun setInicio(inicio: List<Inicio>) {
        this.dataList = inicio
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Inicio){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            var valor = valorRandom(1..4)
            when(valor){
                1 -> {
                    itemView.tvinicio.text = dataItem.titulo1
                    Picasso.get().load("${dataItem.imagen1}").into(itemView.ivinicio)
                }
                2 -> {
                    itemView.tvinicio.text = dataItem.titulo2
                    Picasso.get().load("${dataItem.imagen2}").into(itemView.ivinicio)
                }
                else -> {
                    itemView.tvinicio.text = dataItem.titulo3
                    Picasso.get().load("${dataItem.imagen3}").into(itemView.ivinicio)
                }
            }
            itemView.tag = dataItem

        }

        fun valorRandom(valores: IntRange) : Int {
            var r = Random()
            return r.nextInt(valores.last - valores.first) + valores.first
        }

    }
}