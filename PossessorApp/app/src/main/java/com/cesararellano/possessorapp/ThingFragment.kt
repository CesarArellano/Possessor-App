package com.cesararellano.possessorapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class ThingFragment : Fragment() {

    private lateinit var thing: Thing
    private lateinit var nameField: EditText
    private lateinit var priceField: EditText
    private lateinit var serialNumberField: EditText
    private lateinit var dateLabel: TextView
    private lateinit var modifyDateButton: Button

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
                        if( s.toString().isNotEmpty() ) {
                            thing.thingName = s.toString()
                        }
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

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Este campo no puede estar vacío", Toast.LENGTH_LONG).show()
                    nameField.setText(thing.thingName)
                }
            }
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
        modifyDateButton = view.findViewById(R.id.modifyDateButton)

        nameField.setText( thing.thingName )
        priceField.setText( thing.pesosValue.toString() )
        serialNumberField.setText( thing.serialNumber )
        dateLabel.text = thing.creationDate

        modifyDateButton.setOnClickListener {
            // Se obtiene (año, mes y día) actual como fecha inicial del DatePickerDialog
            val formatDate = SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault() ).format( Date() )
            val splitDate = formatDate.split("-")
            val year = splitDate[2].toInt()
            val month = splitDate[1].toInt()
            val day = splitDate[0].toInt()
            showDatePickerDialog(year, month, day)
        }

        return view
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int) {
        val datePickerDialog = activity?.let { it1 ->
            DatePickerDialog(it1, { _, yearN, monthOfYear, dayOfMonth ->
                val newDate = "${ dayOfMonth }-${ monthOfYear + 1 }-${ yearN }"
                dateLabel.text = newDate
                thing.creationDate = newDate
            }, year, month, day)
        }

        datePickerDialog?.datePicker?.maxDate = Date().time
        datePickerDialog?.show()
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