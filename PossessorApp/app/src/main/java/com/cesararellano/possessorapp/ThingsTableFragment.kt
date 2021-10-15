package com.cesararellano.possessorapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class ThingsTableFragment: Fragment() {
    // Propiedades para generar el RecyclerView
    private lateinit var thingRecyclerView: RecyclerView
    private var adapter: ThingAdapter? = null
    private var interfaceCallback:ThingTableInterface? = null
    private val thingTableViewModel: ThingsTableViewModel by lazy {
        ViewModelProvider(this).get(ThingsTableViewModel::class.java) // Obtenemos los datos de ThingsTableViewModel
    }

    // Creamos nuestra interfaz para cuando el usuario seleccione una cosa.
    interface ThingTableInterface {
        fun onSelectedThing( thing: Thing )
    }

    // Utilizamos este método para setear el callback de la interfaz.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        interfaceCallback = context as ThingTableInterface?
    }

    // Utilizamos este método para quitar la referencias del callback.
    override fun onDetach() {
        super.onDetach()
        interfaceCallback = null
    }

    // Este método nos permite actualizar el UI con el inventario recibido, al igual de manejar las acciones de Drag and Drop y el swipe delete.
    private fun updateUI() {
        val inventary = thingTableViewModel.inventory
        adapter = ThingAdapter(inventary)
        thingRecyclerView.adapter = adapter

        // Se generan los listeners dependiendo del gesto del usuario, se utiliza la clase abstracta RecyclerViewGestures.
        val swipegestures = object : RecyclerViewGestures() {

            // Acciones de Swipe
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.LEFT) { // Tras hacer un swipe a la izquierda ejecutará el método deleteItem.
                    adapter?.deleteItem(viewHolder.absoluteAdapterPosition)
                }
            }

            // Acciones de Drag and Drop.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Se obtienen las posiciones para actualizar los índices de la lista correctamente.
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                Collections.swap(inventary, fromPosition, toPosition)
                adapter?.notifyItemMoved(fromPosition, toPosition)

                return false
            }
        }
        // Se adjunta el RecyclerView al touchHelper.
        val touchHelper = ItemTouchHelper(swipegestures)
        touchHelper.attachToRecyclerView(thingRecyclerView)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Creamos RecyclerView
        val view = inflater.inflate(R.layout.thing_list_fragment, container, false)
        thingRecyclerView = view.findViewById(R.id.thingRecyclerView)
        thingRecyclerView.layoutManager = LinearLayoutManager(context)
        updateUI()
        return view
    }

    private inner class ThingHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        var itemBackgroundColor = "#FF8B00"

        private lateinit var thing: Thing

        val nameTextView: TextView = itemView.findViewById(R.id.nameLabel)
        val priceTextView: TextView = itemView.findViewById(R.id.priceLabel)
        val serialNumberTextView: TextView = itemView.findViewById(R.id.serialNumberLabel)
        val thingItemLayout: ConstraintLayout = itemView.findViewById(R.id.thingItemLayout)

        // Seteamos los valores de la cosa en la tabla ( item del RecyclerView ).
        @SuppressLint("SetTextI18n")
        fun binding(thing:Thing) {

            this.thing = thing
            nameTextView.text = this.thing.thingName
            priceTextView.text = "$${ thing.pesosValue }"
            serialNumberTextView.text = this.thing.serialNumber

            // Se decide por el color de fondo del item dependiendo de su precio.
            itemBackgroundColor = when( thing.pesosValue ) {
                in 0..99 -> "#E45050"
                in 100..199 -> "#E47D50"
                in 200..299 -> "#BD8138"
                in 300..399 -> "#35B046"
                in 400..499 -> "#4BAF96"
                in 500..599 -> "#47A8E7"
                in 600..699 -> "#476BE7"
                in 700..799 -> "#7747E7"
                in 800..899 -> "#5F4CB7"
                in 900..999 -> "#E747AD"
                else -> "#B74C70"
            }
            // Seteamos el color de fondo.
            thingItemLayout.setBackgroundColor( Color.parseColor(itemBackgroundColor) )
        }

        init {
            itemView.setOnClickListener(this) // ClickListener para el itemView.
        }

        override fun onClick(p0: View?) {
            interfaceCallback?.onSelectedThing(thing) // Se ejecuta la función onSelectedThing de nuestra ThingTableInterface.
        }
    }

    // Creamos el adapter, el cual nos ayuda a crear en general el RecyclerView.
    private inner class ThingAdapter(var inventary: ArrayList<Thing>):RecyclerView.Adapter<ThingHolder>() {

        //Seteamos el layout del item
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingHolder {
            val holder = layoutInflater.inflate(R.layout.thing_layout,parent,false)
            return ThingHolder(holder)
        }

        // Seteamos el número de elementos que tendrá el Recycler View a través de la longitud de la lista del inventario.
        override fun getItemCount(): Int {
            return inventary.size
        }

        override fun onBindViewHolder(holder: ThingHolder, position: Int) {
            holder.binding(inventary[position]) // Ligamos la posesión (cosa) con la función binding del ThingHolder.
        }

        // Función para eliminar el item al que se le hizo swipe
        @SuppressLint("NotifyDataSetChanged")
        fun deleteItem(position: Int) {
            // Configurando el AlertDialog
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("¡Atención!")
            builder.setMessage("¿Desea eliminar esta posesión?")
            builder.setOnDismissListener { // Si cierra el Dialog, dando el botón de back de teléfono o dando tap fuera del mismo, actualizará la vista.
                notifyDataSetChanged()
            }
            builder.setPositiveButton("Confirmar") { dialog, _ -> // Confirma la eliminación de la cosa.
                inventary.removeAt(position)
                notifyDataSetChanged()
                dialog.cancel()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ -> // Cancela la acción de eliminar.
                notifyDataSetChanged()
                dialog.cancel()
            }

            val alert: AlertDialog = builder.create()
            alert.show() // Despliega en pantalla el AlertDialog.
        }
    }

}