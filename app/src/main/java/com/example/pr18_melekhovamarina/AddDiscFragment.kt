package com.example.pr18_melekhovamarina

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class AddDiscFragment : Fragment() {

    interface Callbacks {
        fun onDiscSaved()
    }

    private var callbacks: Callbacks? = null
    private lateinit var storage: Storage

    private lateinit var tilTitle: TextInputLayout
    private lateinit var tilArtist: TextInputLayout
    private lateinit var tilYear: TextInputLayout
    private lateinit var tilTracks: TextInputLayout
    private lateinit var spinGenre: Spinner
    private lateinit var cbFavorite: CheckBox
    private lateinit var btnSave: MaterialButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_disc, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Storage(requireContext())

        tilTitle = view.findViewById(R.id.til_disc_title)
        tilArtist = view.findViewById(R.id.til_artist)
        tilYear = view.findViewById(R.id.til_year)
        tilTracks = view.findViewById(R.id.til_tracks)
        spinGenre = view.findViewById(R.id.spin_genre)
        cbFavorite = view.findViewById(R.id.cb_favorite)
        btnSave = view.findViewById(R.id.btn_save)

        val genres = resources.getStringArray(R.array.genres)
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, genres)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinGenre.adapter = adapter

        btnSave.setOnClickListener { saveDisc() }
    }

    private fun TextInputLayout.value(): String =
        editText?.text?.toString()?.trim().orEmpty()

    private fun saveDisc() {
        tilTitle.error = null
        tilArtist.error = null
        tilYear.error = null
        tilTracks.error = null

        val title = tilTitle.value()
        val artist = tilArtist.value()
        val year = tilYear.value().toIntOrNull()
        val tracks = tilTracks.value().toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        var ok = true

        if (title.isEmpty()) {
            tilTitle.error = "Введите название диска"
            ok = false
        }
        if (artist.isEmpty()) {
            tilArtist.error = "Введите исполнителя"
            ok = false
        }
        if (tilYear.value().isEmpty()) {
            tilYear.error = "Введите год выпуска"
            ok = false
        } else if (year == null || year < 1900 || year > currentYear) {
            tilYear.error = "Год от 1900 до $currentYear"
            ok = false
        }
        if (tilTracks.value().isEmpty()) {
            tilTracks.error = "Введите количество треков"
            ok = false
        } else if (tracks == null || tracks < 1 || tracks > 99) {
            tilTracks.error = "От 1 до 99 треков"
            ok = false
        }
        if (!ok) return

        val disc = Disc(
            id = System.currentTimeMillis(),
            title = title,
            artist = artist,
            year = year!!,
            genre = spinGenre.selectedItem.toString(),
            tracks = tracks!!,
            favorite = cbFavorite.isChecked
        )
        storage.addDisc(disc)
        Toast.makeText(requireContext(), "Диск добавлен в плей-лист", Toast.LENGTH_SHORT).show()
        clearForm()
        callbacks?.onDiscSaved()
    }

    private fun clearForm() {
        tilTitle.editText?.text?.clear()
        tilArtist.editText?.text?.clear()
        tilYear.editText?.text?.clear()
        tilTracks.editText?.text?.clear()
        spinGenre.setSelection(0)
        cbFavorite.isChecked = false
    }
}