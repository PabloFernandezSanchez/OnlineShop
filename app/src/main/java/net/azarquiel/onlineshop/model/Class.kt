package net.azarquiel.onlineshop.model

import java.io.Serializable

data class Producto(var imagen:String="", var precio:String="",var titulo:String="", var talla:String ="", var desc:String ="", var imagen2:String =""): Serializable
data class Inicio(var imagen1:String="",var imagen2:String="",var imagen3:String="",var titulo1:String="",var titulo2:String="",var titulo3:String="")
data class Carrito(var imagen:String="", var precio:String="",var titulo:String="", var talla:String ="")
data class User(var email:String="", var dinero:String="")