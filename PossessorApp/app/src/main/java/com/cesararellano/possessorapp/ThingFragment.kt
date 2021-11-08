package com.cesararellano.possessorapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ThingFragment"
class ThingFragment : Fragment() {
    // Variable de referencia a la cosa, que recibe de ThingTableFragment.
    private lateinit var thing: Thing

    // Variables de referencia a elementos del UI
    private lateinit var nameField: EditText
    private lateinit var priceField: EditText
    private lateinit var serialNumberField: EditText
    private lateinit var dateLabel: TextView
    private lateinit var modifyDateButton: Button
    private lateinit var viewToPhoto: ImageView
    private lateinit var cameraButton: ImageButton
    private lateinit var photoFile: File
    private var cameraResp = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if( result.resultCode == Activity.RESULT_OK ) {
            // val data = result.data
            // viewToPhoto.setImageBitmap(data?.extras?.get("data") as Bitmap)
            viewToPhoto.setImageBitmap( BitmapFactory.decodeFile( photoFile.absolutePath ) )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thing = arguments?.getParcelable("RECEIVED_THING") ?: Thing() // Recibe la cosa seleccionada de ThingTableFragment
    }

    // En onStart instanciamos un TextWatcher para validar en tiempo real, los 3 EditText que tenemos (nombre, precio y número de serie).
    override fun onStart() {
        super.onStart()

        val onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback( viewLifecycleOwner, onBackPressedCallback )

        val textObservable = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Dependiendo del número de hash hará la validación al EditText correspondiente.
                when {
                    s.hashCode() == nameField.text.hashCode() -> {
                        if( s.toString().isNotEmpty() ) {
                            onBackPressedCallback.isEnabled = false
                            thing.thingName = s.toString()
                        } else {
                            onBackPressedCallback.isEnabled = true
                            Toast.makeText(requireContext(), "Este campo no puede estar vacío", Toast.LENGTH_LONG).show()
                        }
                    }
                    s.hashCode() == priceField.text.hashCode() -> {
                        // Validando el precio de la cosa.
                        if(s != null) {
                            when {
                                s.isEmpty() -> {
                                    thing.pesosValue = 0
                                }
                                else -> {
                                    thing.pesosValue = s.toString().toInt()
                                }
                            }
                        }
                    }
                    else -> {
                        if(s.toString().isNotEmpty()) {
                            thing.serialNumber = s.toString()
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // Agregamos el listener para cada EditText
        nameField.addTextChangedListener(textObservable)
        priceField.addTextChangedListener(textObservable)
        serialNumberField.addTextChangedListener(textObservable)

        val appbar = activity as AppCompatActivity
        appbar.supportActionBar?.title = "Detalle cosa"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.thing_fragment, container, false)
        // Hacemos referencia a los elementos de la UI.
        nameField = view.findViewById(R.id.nameEditText)
        priceField = view.findViewById(R.id.priceEditText)
        serialNumberField = view.findViewById(R.id.serialNumberEditText)
        dateLabel = view.findViewById(R.id.dateLabel)
        modifyDateButton = view.findViewById(R.id.modifyDateButton)
        viewToPhoto = view.findViewById(R.id.thingImage)
        cameraButton = view.findViewById(R.id.imageButton)
        photoFile = File( context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${ thing.thingId }.jpg")
        viewToPhoto.setImageBitmap( BitmapFactory.decodeFile( photoFile.absolutePath ) )
        // Seteamos los valores de thing para que sean pintados en pantalla.
        nameField.setText( thing.thingName )
        priceField.setText( thing.pesosValue.toString() )
        serialNumberField.setText( thing.serialNumber )
        dateLabel.text = thing.creationDate

        modifyDateButton.setOnClickListener {
            // Se obtiene año, mes y día actual como fecha inicial del DatePickerDialog.
            val formatDate = SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault() ).format( Date() )
            val splitDate = formatDate.split("-")
            val year = splitDate[2].toInt()
            val month = splitDate[1].toInt()
            val day = splitDate[0].toInt()
            showDatePickerDialog(year, month, day) // Crea el DatePickerDialog y lo muestra.
        }

        cameraButton.apply {
            setOnClickListener {
                val takePhotoIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = getPhotoFile("${ thing.thingId }.jpg")
                val fileProvider = FileProvider.getUriForFile( context, "${ BuildConfig.APPLICATION_ID }.fileprovider", photoFile)
                takePhotoIntent.putExtra( MediaStore.EXTRA_OUTPUT, fileProvider )

                try {
                    cameraResp.launch(takePhotoIntent)
                } catch (e: Exception) {
                    Log.d(TAG, "No se encontró la cámara.")
                }
            }
        }

        return view
    }

    private fun getPhotoFile(filename: String): File {
        val photoPath = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(photoPath, filename)
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int) {
        val datePickerDialog = activity?.let { it1 ->
            DatePickerDialog(it1, { _, yearN, monthOfYear, dayOfMonth ->
                val newDate = "${ dayOfMonth }-${ monthOfYear + 1 }-${ yearN }"
                dateLabel.text = newDate
                thing.creationDate = newDate
            }, year, month, day) // Se crea el DatePicker con la fecha actual y tras seleccionar una fecha le establecemos el formato a la misma, para almacenarla y pintarla en la vista.
        }

        datePickerDialog?.datePicker?.maxDate = Date().time // Establecemos la fecha máxima a usar en el calendario.
        datePickerDialog?.show()
    }

    // Este companion object nos ayuda a crear una instancia de este fragmento e inyectarle la cosa a través del Parcelable.
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