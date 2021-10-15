package com.cesararellano.possessorapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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

private const val TAG = "ThingsTableFragment"
class ThingsTableFragment: Fragment() {
    private lateinit var thingRecyclerView: RecyclerView
    private var adapter: ThingAdapter? = null
    private var interfaceCallback:ThingTableInterface? = null


    private val thingTableViewModel: ThingsTableViewModel by lazy {
        ViewModelProvider(this).get(ThingsTableViewModel::class.java)
    }

    interface ThingTableInterface {
        fun onSelectedThing( thing: Thing )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interfaceCallback = context as ThingTableInterface?
    }

    override fun onDetach() {
        super.onDetach()
        interfaceCallback = null
    }

    private fun updateUI() {
        val inventary = thingTableViewModel.inventory
        adapter = ThingAdapter(inventary)
        thingRecyclerView.adapter = adapter

        val swipegestures = object : RecyclerViewGestures() { //  Genero los listeners para cuando se detecte el swipe del usuario
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.LEFT) {
                    adapter?.deleteItem(viewHolder.absoluteAdapterPosition) // Si se hace swipe hacia la izquierda, entonces elimino el item
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Al moverse verticalmente, se mueve la posicion del item seleccionado
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                Collections.swap(inventary, fromPosition, toPosition)
                adapter?.notifyItemMoved(fromPosition, toPosition)

                return false
            }
        }
        val touchHelper = ItemTouchHelper(swipegestures)
        touchHelper.attachToRecyclerView(thingRecyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Things total: ${ thingTableViewModel.inventory.size }")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        @SuppressLint("SetTextI18n")
        fun binding(thing:Thing) {

            this.thing = thing
            nameTextView.text = this.thing.thingName
            priceTextView.text = "$${ thing.pesosValue }"
            serialNumberTextView.text = this.thing.serialNumber

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

            thingItemLayout.setBackgroundColor( Color.parseColor(itemBackgroundColor) )
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            interfaceCallback?.onSelectedThing(thing)
        }
    }

    private inner class ThingAdapter(var inventary: ArrayList<Thing>):RecyclerView.Adapter<ThingHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingHolder {
            val holder = layoutInflater.inflate(R.layout.thing_layout,parent,false)
            return ThingHolder(holder)
        }

        override fun getItemCount(): Int {
            return inventary.size
        }

        override fun onBindViewHolder(holder: ThingHolder, position: Int) {
            holder.binding(inventary[position])
        }

        @SuppressLint("NotifyDataSetChanged")
        fun deleteItem(position: Int) { // Funcion para eliminar un item
            val builder = AlertDialog.Builder(activity) // Genero al alert para confirmar que se quiere eliminar el item
            builder.setTitle("¡Atención!")
            builder.setMessage("¿Desea eliminar esta posesión?")
            builder.setPositiveButton("Confirmar") { dialog, _ -> // En caso de que la respuesta sea positiva, eliminamos el item
                inventary.removeAt(position)
                notifyDataSetChanged()
                dialog.cancel()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ -> // En caso negativo, se cierra el dialogo
                notifyDataSetChanged()
                dialog.cancel()
            }

            val alert: AlertDialog = builder.create()
            alert.show() // Muestro el alert
        }
    }

}