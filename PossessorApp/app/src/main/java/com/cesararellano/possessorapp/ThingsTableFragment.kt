package com.cesararellano.possessorapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "ThingsTableFragment"
class ThingsTableFragment: Fragment() {
    private lateinit var  thingRecyclerView: RecyclerView
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

    companion object {
        fun newInstance():ThingsTableFragment {
            return ThingsTableFragment()
        }
    }

    private inner class ThingHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var thing: Thing

        val nameTextView: TextView = itemView.findViewById(R.id.nameLabel)
        val priceTextView: TextView = itemView.findViewById(R.id.priceLabel)

        @SuppressLint("SetTextI18n")
        fun binding(thing:Thing) {
            this.thing = thing
            nameTextView.text = this.thing.thingName
            priceTextView.text = "$${ thing.pesosValue }"
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            interfaceCallback?.onSelectedThing(thing)
        }
    }

    private inner class ThingAdapter(var inventary: List<Thing>):RecyclerView.Adapter<ThingHolder>() {
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
    }
}