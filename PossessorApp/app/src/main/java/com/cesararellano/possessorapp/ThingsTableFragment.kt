package com.cesararellano.possessorapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ThingsTableFragment: Fragment() {
    // Propiedades para generar el RecyclerView.
    private lateinit var sectionsRecyclerView: RecyclerView
    private var sectionsAdapter: SectionsAdapter ? = null
    // Dos TextView para el total de cosas y la suma total del precio de las cosas.
    private lateinit var totalThings: TextView
    private lateinit var thingPriceSum: TextView
    private var interfaceCallback:ThingTableInterface? = null
    private val thingTableViewModel: ThingsTableViewModel by lazy {
        ViewModelProvider(this).get(ThingsTableViewModel::class.java) // Obtenemos los datos de ThingsTableViewModel
    }

    // Creamos nuestra interfaz para cuando el usuario seleccione una cosa.
    interface ThingTableInterface {
        fun onSelectedThing( thing: Thing, thingTable: ThingsTableViewModel)
    }

    // Utilizamos este método para setear el callback de la interfaz.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        interfaceCallback = context as ThingTableInterface?
    }

    // Utilizamos este método para quitar la referencia del callback.
    override fun onDetach() {
        super.onDetach()
        interfaceCallback = null
    }

    // Este método nos permite actualizar la UI con el inventario recibido, al igual de manejar las acciones de Drag and Drop y el swipe delete.
    private fun updateUI() {
        val inventory = thingTableViewModel.inventory
        sectionsAdapter = SectionsAdapter(inventory)
        sectionsRecyclerView.adapter = sectionsAdapter
    }

    // Con esta función actualizamos el pie de página del total de cosas y la suma total que hay en el inventario.
    @SuppressLint("SetTextI18n")
    fun updateFooter() {
        val numberOfThingsViewModel = thingTableViewModel.getTotalThings()
        val thingPriceSumViewModel: Int = thingTableViewModel.getTotalPriceSum()
        totalThings.text = "Total de cosas: $numberOfThingsViewModel"
        thingPriceSum.text = "Suma total de precios: $$thingPriceSumViewModel"
    }

    // Si se pasa por onResume, se actualizará el pie de página.
    override fun onResume() {
        super.onResume()
        updateFooter()
    }

    // Se utiliza para cambiar el título del actionBar y ponerle su menú de opciones (agregar nueva cosa).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appbar = activity as AppCompatActivity
        appbar.supportActionBar?.title = "Possesor App"
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Creamos RecyclerView
        val view = inflater.inflate(R.layout.thing_list_fragment, container, false)
        sectionsRecyclerView = view.findViewById(R.id.thingRecyclerView)
        sectionsRecyclerView.layoutManager = LinearLayoutManager(context)
        // Hacemos referencia a los TextView.
        totalThings = view.findViewById(R.id.totalThings)
        thingPriceSum = view.findViewById(R.id.totalPriceSum)
        updateUI() // Creamos el RecyclerView de las secciones.
        updateFooter()
        return view
    }

    // SectionsAdapter para el RecylerView principal.
    private inner class SectionsAdapter(var listOfSections: ArrayList<Sections>): RecyclerView.Adapter<SectionsAdapter.SectionHolder>() {

        // Section Holder para cada item de la sección.
        inner class SectionHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            // Se declaran los atributos que tendrá la sección.
            private val sectionName: TextView = itemView.findViewById(R.id.sectionName)
            private val thingsRecyclerView: RecyclerView = itemView.findViewById(R.id.thingsRecyclerView)
            private val numberOfThingsBySection: TextView = itemView.findViewById(R.id.numberOfThingsBySection)
            private val thingPriceSumBySection: TextView = itemView.findViewById(R.id.thingPriceSumBySection)
            private val orderByAlphaButton: ImageButton = itemView.findViewById(R.id.orderByAlphaButton)
            private val orderByAlphaLabel: TextView = itemView.findViewById(R.id.orderByAlphaLabel)
            private val orderByDateButton: ImageButton = itemView.findViewById(R.id.orderByDateButton)
            private val orderByDateLabel: TextView = itemView.findViewById(R.id.orderByDateLabel)
            private var isSortByAscAlpha:Boolean = true
            private var isSortByAscDate:Boolean = true

            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            fun binding(section: Sections, backgroundColor: Int) {
                // Se establecen los valores de la sección.
                sectionName.text = section.sectionName
                numberOfThingsBySection.text = "Número de cosas: ${ section.sectionList.size }"
                thingPriceSumBySection.text = "Suma de precios: $${ thingTableViewModel.getThingPriceSumBySection(section.sectionList) }"
                // Creamos el RecylerView anidado de las cosas.
                val thingAdapter = ThingAdapter(section.sectionList)
                thingsRecyclerView.layoutManager = LinearLayoutManager(context)
                thingsRecyclerView.adapter = thingAdapter
                thingsRecyclerView.setBackgroundColor(backgroundColor)

                // Habilitamos o deshabilitamos los botones dependiendo si hay cosas en la sección específica.
                orderByAlphaButton.isEnabled = section.sectionList.size > 0
                orderByDateButton.isEnabled = section.sectionList.size > 0

                orderByDateButton.setOnClickListener {
                    // Ordenamos de forma ASC o DESC dependiendo de la bandera actual.
                    if( isSortByAscDate ) {
                        section.sectionList.sortBy { it.originalCreationDate }
                        orderByDateLabel.text = "ASC"
                    } else {
                        section.sectionList.sortByDescending { it.originalCreationDate }
                        orderByDateLabel.text = "DESC"
                    }

                    isSortByAscDate = !isSortByAscDate // Invertimos la bandera.
                    notifyDataSetChanged() // Actualizamos el RecyclerView de las secciones.
                }

                orderByAlphaButton.setOnClickListener {
                    // Ordenamos de forma ASC o DESC dependiendo de la bandera actual.
                    if( isSortByAscAlpha ) {
                        section.sectionList.sortBy { it.thingName }
                        orderByAlphaLabel.text = "ASC"
                    } else {
                        section.sectionList.sortByDescending { it.thingName }
                        orderByAlphaLabel.text = "DESC"
                    }

                    isSortByAscAlpha = !isSortByAscAlpha // Invertimos la bandera.
                    notifyDataSetChanged() // Actualizamos el RecyclerView de las secciones.
                }

                // Se generan los listeners dependiendo del gesto del usuario, se utiliza la clase abstracta RecyclerViewGestures.
                val swipegestures = object : RecyclerViewGestures() {

                    // Acciones de Swipe
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        if(direction == ItemTouchHelper.LEFT) { // Tras hacer un swipe a la izquierda ejecutará el método deleteItem.
                            thingAdapter.deleteItem(viewHolder.absoluteAdapterPosition)
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

                        Collections.swap(section.sectionList, fromPosition, toPosition)
                        thingAdapter.notifyItemMoved(fromPosition, toPosition)

                        return false
                    }
                }
                // Se adjunta el RecyclerView al touchHelper.
                val touchHelper = ItemTouchHelper(swipegestures)
                touchHelper.attachToRecyclerView(thingsRecyclerView)
            }

        }

        // Inflamos el SectionHolder.
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHolder {
            val holder = LayoutInflater.from(parent.context).inflate(R.layout.sections_ui, parent, false)
            return SectionHolder(holder)
        }

        // Ejecutamos el binding y le establecemos el backgroundColor a la sección.
        override fun onBindViewHolder(holder: SectionHolder, position: Int) {
            val backgroundColor = getSectionColor(position)
            holder.binding(listOfSections[position], backgroundColor)
            holder.itemView.setBackgroundColor(backgroundColor)
        }

        override fun getItemCount(): Int {
            return listOfSections.size
        }

        // Función utilizada para obtener el color de la sección.
        private fun getSectionColor(position: Int): Int {

            val priceColor = when(position) {
                in -1..0 -> "#E45050"
                in 0..1 -> "#E47D50"
                in 1..2 -> "#BD8138"
                in 2..3 -> "#35B046"
                in 3..4 -> "#4BAF96"
                in 4..5 -> "#47A8E7"
                in 5..6 -> "#476BE7"
                in 6..7 -> "#7747E7"
                in 7..8 -> "#5F4CB7"
                in 8..9 -> "#E747AD"
                else -> "#B74C70"
            }

            return Color.parseColor(priceColor)
        }
    }


    private inner class ThingHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var thing: Thing

        val photoThumbnail: ImageView = itemView.findViewById(R.id.photoThumbnail)
        val nameTextView: TextView = itemView.findViewById(R.id.nameLabel)
        val priceTextView: TextView = itemView.findViewById(R.id.priceLabel)
        val serialNumberTextView: TextView = itemView.findViewById(R.id.serialNumberLabel)

        // Seteamos los valores de la cosa en la tabla ( item del RecyclerView ).
        @SuppressLint("SetTextI18n")
        fun binding(thing:Thing) {
            this.thing = thing

            val photoFile = File( context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${ thing.thingId }.jpg")

            if( photoFile.exists() ) {
                photoThumbnail.setImageBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
            } else {
                photoThumbnail.setImageDrawable( ContextCompat.getDrawable(requireContext(), R.drawable.no_image) )
            }

            nameTextView.text = thing.thingName
            priceTextView.text = "$${ thing.pesosValue }"
            serialNumberTextView.text = thing.serialNumber
        }

        init {
            itemView.setOnClickListener(this) // ClickListener para el itemView.
        }

        override fun onClick(p0: View?) {
            interfaceCallback?.onSelectedThing(thing, thingTableViewModel) // Se ejecuta la función onSelectedThing de nuestra ThingTableInterface.
        }
    }

    // Creamos el adapter, el cual nos ayuda a crear en general el RecyclerView.
    private inner class ThingAdapter(var inventory: ArrayList<Thing>):RecyclerView.Adapter<ThingHolder>() {

        //Seteamos el layout del item
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingHolder {
            val holder = layoutInflater.inflate(R.layout.thing_layout,parent,false)
            return ThingHolder(holder)
        }

        // Seteamos el número de elementos que tendrá el Recycler View a través de la longitud de la lista del inventario.
        override fun getItemCount(): Int {
            return inventory.size
        }

        override fun onBindViewHolder(holder: ThingHolder, position: Int) {
            holder.binding(inventory[position]) // Ligamos la posesión (cosa) con la función binding del ThingHolder.
        }

        // Función para eliminar el item al que se le hizo swipe
        @SuppressLint("NotifyDataSetChanged")
        fun deleteItem(position: Int) {
            // Configurando el AlertDialog
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("¡Atención!")
            builder.setMessage("¿Desea eliminar esta posesión?")

            builder.setOnDismissListener { // Si cierra el Dialog, dando tap fuera del mismo o dando al botón de back del teléfono, actualizará la vista.
                notifyDataSetChanged()
            }

            builder.setPositiveButton("Confirmar") { dialog, _ -> // Confirma la eliminación de la cosa.
                deletePhotoFile("${ inventory[position].thingId }.jpg")
                inventory.removeAt(position)
                updateFooter()
                sectionsAdapter?.notifyDataSetChanged()
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
        private fun deletePhotoFile(filename: String) {
            val photoPath = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val photoFile = File(photoPath, filename)

            if( photoFile.exists() ) photoFile.delete()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_thing_table, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.newThingItem -> {
                val newThing = Thing()
                thingTableViewModel.addNewThing(newThing)
                interfaceCallback?.onSelectedThing(newThing, thingTableViewModel)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}