package com.example.eventhubkampus // Pastikan ini package-mu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventhubkampus.databinding.ActivityDetailBinding
import com.google.android.material.appbar.AppBarLayout

class DetailAcaraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var acara: Acara? = null // Untuk menyimpan acara yang diterima

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup View Binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Atur Toolbar sebagai ActionBar
        setSupportActionBar(binding.idToolbarDetail)
        // Tampilkan tombol "Kembali" (panah kiri)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ambil data Acara yang dikirim dari MainActivity
        acara = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TambahEditActivity.EXTRA_ACARA, Acara::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TambahEditActivity.EXTRA_ACARA)
        }

        // Jika datanya ada, panggil fungsi untuk mengisi tampilan
        acara?.let {
            isiData(it)
        }

        // Atur listener untuk tombol "Edit" (FAB)
        binding.idFabEdit.setOnClickListener {
            // Buat Intent untuk pindah ke TambahEditActivity

            // --- INI ADALAH BARIS YANG DIPERBAIKI ---
            // 'this@DetailAcaraxActivity' diubah menjadi 'this@DetailAcaraActivity'
            val intent = Intent(this@DetailAcaraActivity, TambahEditActivity::class.java)

            // Kirim data acara ini ke TambahEditActivity
            intent.putExtra(TambahEditActivity.EXTRA_ACARA, acara)
            startActivity(intent)
        }
    }

    /**
     * Fungsi untuk mengisi semua TextView DAN ImageView dengan data
     */
    private fun isiData(acara: Acara) {

        // --- (BAGIAN 1: Mengisi Teks) ---
        binding.idDetailNamaAcara.text = acara.namaAcara
        binding.idDetailTanggal.text = acara.tanggal
        binding.idDetailLokasi.text = acara.lokasi
        binding.idDetailPenyelenggara.text = acara.penyelenggara
        binding.idDetailDeskripsi.text = acara.deskripsi

        // --- (BAGIAN 2: Mengisi Gambar & Judul) ---

        if (!acara.fotoPath.isNullOrEmpty()) {
            // --- CASE 1: ADA GAMBAR ---

            binding.appBar.visibility = View.VISIBLE

            Glide.with(this)
                .load(acara.fotoPath)
                .into(binding.idDetailGambar)

            // Judul di Toolbar (collapsing) akan tetap "Detail Acara" (dari XML)

            // Tampilkan TextView nama acara di konten
            binding.idDetailNamaAcara.visibility = View.VISIBLE

        } else {
            // --- CASE 2: TIDAK ADA GAMBAR ---

            binding.idDetailGambar.visibility = View.GONE

            val params = binding.appBar.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.appBar.layoutParams = params

            val ctParams = binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
            ctParams.scrollFlags = 0
            binding.collapsingToolbar.layoutParams = ctParams

            // Judul di Toolbar akan tetap "Detail Acara" (dari XML)
            binding.collapsingToolbar.isTitleEnabled = false

            // Tampilkan TextView judul di konten
            binding.idDetailNamaAcara.visibility = View.VISIBLE
        }
    }

    /**
     * Fungsi ini dipanggil saat tombol "Kembali" (panah kiri)
     * di Toolbar diklik.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}