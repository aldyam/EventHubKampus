package com.example.eventhubkampus

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhubkampus.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    // Siapkan View Binding
    private lateinit var binding: ActivityMainBinding

    // Dapatkan ViewModel menggunakan 'by viewModels()'
    private val acaraViewModel: AcaraViewModel by viewModels()

    // Buat variabel untuk Adapter
    private lateinit var acaraAdapter: AcaraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hapus judul default dari ActionBar (karena kita sudah punya judul di layout)
        supportActionBar?.hide()

        // 1. Inisialisasi Adapter
        acaraAdapter = AcaraAdapter()

        // 2. Setup RecyclerView
        binding.idRecyclerview.apply {
            // Tentukan adapter yang akan digunakan
            adapter = acaraAdapter
            // Tentukan layout manager (misal: LinearLayout)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // 3. (UPDATE) Atur apa yang terjadi saat item di adapter diklik
        acaraAdapter.setOnItemClickListener { acaraYangDiklik ->
            // Buat Intent untuk pindah ke TambahEditActivity
            val intent = Intent(this@MainActivity, DetailAcaraActivity::class.java)

            // Kirim data acara yang diklik ke activity berikutnya
            intent.putExtra(TambahEditActivity.EXTRA_ACARA, acaraYangDiklik)

            startActivity(intent)
        }

        // 4. (DELETE) Logika untuk SWIPE-TO-DELETE (Menghapus)
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // dragDirs (0 = tidak aktifkan drag-and-drop)
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // swipeDirs (Geser Kiri dan Kanan)
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Kita tidak pakai drag-and-drop
            }

            // Fungsi ini dipanggil saat item di-SWIPE
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Ambil posisi dan objek acara yang dihapus
                val position = viewHolder.adapterPosition
                val acaraYangDihapus = acaraAdapter.currentList[position]

                // Panggil fungsi HAPUS dari ViewModel
                acaraViewModel.hapusAcara(acaraYangDihapus)

                // Tampilkan Snackbar dengan tombol "Batal" (Undo)
                Snackbar.make(
                    binding.root,
                    "Acara dihapus!",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Batal") {
                        // Jika "Batal" diklik, panggil fungsi TAMBAH lagi
                        acaraViewModel.tambahAcara(acaraYangDihapus)
                    }
                    show()
                }
            }
        }

        // 'Tempelkan' helper swipe ini ke RecyclerView kita
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.idRecyclerview)


        // 5. (READ) Mengamati (Observe) data dari ViewModel
        // --- BLOK INI SUDAH DIMODIFIKASI UNTUK EMPTY STATE ---
        acaraViewModel.semuaAcara.observe(this) { daftarAcara ->

            // Cek apakah daftar acara kosong atau null
            if (daftarAcara.isNullOrEmpty()) {
                // Jika daftar kosong, tampilkan empty view
                binding.idRecyclerview.visibility = View.GONE
                binding.idEmptyView.visibility = View.VISIBLE
            } else {
                // Jika ada data, tampilkan recyclerview
                binding.idRecyclerview.visibility = View.VISIBLE
                binding.idEmptyView.visibility = View.GONE

                // Kirim data ke Adapter
                acaraAdapter.submitList(daftarAcara)
            }
        }
        // --- AKHIR BLOK MODIFIKASI ---

        // 6. (CREATE) Atur listener untuk Tombol Tambah (FAB)
        binding.idFabTambah.setOnClickListener {
            // Buat Intent untuk pindah ke TambahEditActivity
            val intent = Intent(this@MainActivity, TambahEditActivity::class.java)
            // Mulai Activity baru (tanpa mengirim data, jadi ini mode Tambah)
            startActivity(intent)
        }
    }
}