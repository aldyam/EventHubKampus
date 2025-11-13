package com.example.eventhubkampus // Pastikan ini package-mu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager // <-- IMPORT BARU
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhubkampus.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val acaraViewModel: AcaraViewModel by viewModels()
    private lateinit var acaraAdapter: AcaraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inisialisasi Adapter
        acaraAdapter = AcaraAdapter()

        // ---
        // 2. (INI YANG BERUBAH) Setup RecyclerView
        // ---
        binding.idRecyclerview.apply {
            adapter = acaraAdapter
            // Kita ganti dari LinearLayoutManager ke GridLayoutManager (2 kolom)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }

        // 3. (UPDATE) Atur klik
        acaraAdapter.setOnItemClickListener { acaraYangDiklik ->
            val intent = Intent(this@MainActivity, DetailAcaraActivity::class.java)
            intent.putExtra(TambahEditActivity.EXTRA_ACARA, acaraYangDiklik)
            startActivity(intent)
        }

        // 4. (DELETE) Logika Swipe-to-Delete
        // (Kita nonaktifkan swipe-to-delete untuk Grid, karena aneh)
        // Jika kamu mau, kamu bisa hapus seluruh blok "itemTouchHelperCallback" ini
        // agar lebih bersih. Untuk saat ini, kita biarkan tapi tidak akan berfungsi
        // sebaik di list.
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val acaraYangDihapus = acaraAdapter.currentList[position]
                acaraViewModel.hapusAcara(acaraYangDihapus)

                Snackbar.make(
                    binding.root,
                    "Acara dihapus!",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Batal") {
                        acaraViewModel.tambahAcara(acaraYangDihapus)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.idRecyclerview)


        // 5. (READ) Mengamati data
        acaraViewModel.semuaAcara.observe(this) { daftarAcara ->
            if (daftarAcara.isNullOrEmpty()) {
                binding.idRecyclerview.visibility = View.GONE
                binding.idEmptyView.visibility = View.VISIBLE
            } else {
                binding.idRecyclerview.visibility = View.VISIBLE
                binding.idEmptyView.visibility = View.GONE
                acaraAdapter.submitList(daftarAcara)
            }
        }

        // 6. (CREATE) Tombol Tambah
        binding.idFabTambah.setOnClickListener {
            val intent = Intent(this@MainActivity, TambahEditActivity::class.java)
            startActivity(intent)
        }
    }
}