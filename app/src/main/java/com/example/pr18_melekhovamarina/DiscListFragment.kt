package com.example.pr18_melekhovamarina

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DiscListFragment : Fragment() {

    interface Callbacks {
        fun onLogout()
    }

    private var callbacks: Callbacks? = null
    private lateinit var storage: Storage
    private lateinit var adapter: DiscAdapter

    private lateinit var tvGreeting: TextView
    private lateinit var tvSummary: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var btnLogout: MaterialButton

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
    ): View = inflater.inflate(R.layout.fragment_disc_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Storage(requireContext())

        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvSummary = view.findViewById(R.id.tv_summary)
        tvEmpty = view.findViewById(R.id.tv_empty)
        recycler = view.findViewById(R.id.recycler)
        btnLogout = view.findViewById(R.id.btn_logout)

        val userName = storage.getUser()?.login ?: "меломан"
        tvGreeting.text = "Привет, $userName!"

        adapter = DiscAdapter(storage.getDiscs()) { disc -> confirmDelete(disc) }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        btnLogout.setOnClickListener { callbacks?.onLogout() }

        refresh()
    }

    private fun refresh() {
        val discs = storage.getDiscs()
        adapter.submit(discs)

        val favorites = discs.count { it.favorite }
        tvSummary.text = "Дисков: ${discs.size}  •  Любимых: $favorites"

        if (discs.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }

    private fun confirmDelete(disc: Disc) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить диск?")
            .setMessage("«${disc.title}» – ${disc.artist}")
            .setPositiveButton("Удалить") { _, _ ->
                storage.removeDisc(disc.id)
                refresh()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}