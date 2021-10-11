package com.cesararellano.possessorapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class ThingFragment : Fragment() {

    private lateinit var thing: Thing
    private lateinit var nameField: EditText
    private lateinit var priceField: EditText
    private lateinit var serialNumberField: EditText
    private lateinit var dateLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thing = Thing()
        thing = arguments?.getParcelable("RECEIVED_THING")!!
    }

    override fun onStart() {
        super.onStart()

        val textObservable = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.hashCode() == nameField.text.hashCode() -> {
                        thing.thingName = s.toString()
                    }
                    s.hashCode() == priceField.text.hashCode() -> {
                        if(s != null) {
                            if(s.isEmpty()) {
                                thing.pesosValue = 0
                            } else {
                                thing.pesosValue = s.toString().toInt()
                            }
                        }
                    }
                    else -> {
                        thing.serialNumber = s.toString()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        nameField.addTextChangedListener(textObservable)
        priceField.addTextChangedListener(textObservable)
        serialNumberField.addTextChangedListener(textObservable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.thing_fragment, container, false)

        nameField = view.findViewById(R.id.nameEditText)
        priceField = view.findViewById(R.id.priceEditText)
        serialNumberField = view.findViewById(R.id.serialNumberEditText)
        dateLabel = view.findViewById(R.id.dateLabel)

        nameField.setText( thing.thingName )
        priceField.setText( thing.pesosValue.toString() )
        serialNumberField.setText( thing.serialNumber )
        dateLabel.text = thing.creationDate.toString()

        return view
    }

    companion object {
        fun newInstance(thing: Thing): ThingFragment {
            val args = Bundle().apply {
                putParcelable("RECEIVED_THING", thing)
            }
            return ThingFragment().apply {
                arguments = args
            }
        }
    }
}