package com.example.eventhubkampus

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventhubkampus.databinding.ActivityTambahEditBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class TambahEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahEditBinding
    private val acaraViewModel: AcaraViewModel by viewModels()

    // Variabel untuk menyimpan data acara yang akan diedit
    private var acaraEdit: Acara? = null

    // --- (BLOK BARU 1: Persiapan Gambar) ---

    // Variabel untuk menyimpan path FOTO yang akan disimpan ke database
    private var pathFotoTerpilih: String? = null

    /**
     * Menyiapkan "Peluncur Galeri" (Activity Result Launcher).
     * Ini adalah cara modern untuk mengambil konten (seperti gambar)
     * yang menggantikan onActivityResult()
     */
    private val peluncurGaleri = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Callback ini akan dipanggil setelah pengguna memilih gambar
        uri?.let {
            // Pengguna berhasil memilih gambar, URI-nya adalah 'it'

            // 1. Salin gambar dari URI galeri ke penyimpanan internal aplikasi kita
            //    Fungsi ini akan mengembalikan path absolut (String) dari file baru
            pathFotoTerpilih = simpanGambarKeInternal(it)

            // 2. Tampilkan gambar yang baru dipilih sebagai preview
            Glide.with(this)
                .load(pathFotoTerpilih) // Muat gambar dari path file
                .into(binding.idImgPreview)

            // 3. Tampilkan ImageView-nya
            binding.idImgPreview.visibility = View.VISIBLE
        }
    }
    // --- (AKHIR BLOK BARU 1) ---


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memeriksa apakah kita dalam mode Edit atau Tambah
        cekModeEdit()

        // --- (BLOK BARU 2: Listener Tombol Gambar) ---
        binding.idBtnPilihGambar.setOnClickListener {
            // Saat tombol "Pilih Gambar" diklik, luncurkan galeri
            // "image/*" berarti kita hanya mencari file gambar
            peluncurGaleri.launch("image/*")
        }
        // --- (AKHIR BLOK BARU 2) ---

        // Listener untuk tombol simpan (tetap sama)
        binding.idButtonSimpan.setOnClickListener {
            simpanAtauUpdateAcara()
        }
    }

    /**
     * Fungsi baru untuk menyalin file gambar dari URI (galeri)
     * ke penyimpanan internal aplikasi yang aman.
     * Mengembalikan String path absolut dari file yang baru disimpan.
     */
    private fun simpanGambarKeInternal(uri: Uri): String? {
        try {
            // 1. Dapatkan InputStream dari URI yang diberikan ContentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            // 2. Buat file tujuan di direktori internal aplikasi (/data/data/nama.package/files)
            val namaFile = "acara_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, namaFile) // 'filesDir' adalah direktori internal

            // 3. Buat OutputStream untuk menulis ke file baru
            val outputStream = FileOutputStream(file)

            // 4. Salin data byte dari inputStream ke outputStream
            inputStream?.copyTo(outputStream)

            // 5. Tutup kedua stream
            inputStream?.close()
            outputStream.close()

            // 6. Kembalikan path absolut dari file yang baru kita buat
            return file.absolutePath

        } catch (e: Exception) {
            // Tangani jika ada error (misal: I/O error)
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    /**
     * Memeriksa apakah Activity ini dibuka dalam mode Edit (jika ada data Acara)
     * atau mode Tambah Baru (jika tidak ada data).
     */
    private fun cekModeEdit() {
        if (intent.hasExtra(EXTRA_ACARA)) {
            // Ambil data Acara yang dikirim
            acaraEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_ACARA, Acara::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_ACARA)
            }
        }

        if (acaraEdit != null) {
            // --- MODE EDIT ---
            supportActionBar?.title = "Edit Acara"
            binding.idButtonSimpan.text = "Update Acara"

            // Isi form dengan data yang ada
            binding.idEditNama.setText(acaraEdit?.namaAcara)
            binding.idEditTanggal.setText(acaraEdit?.tanggal)
            binding.idEditLokasi.setText(acaraEdit?.lokasi)
            binding.idEditPenyelenggara.setText(acaraEdit?.penyelenggara)
            binding.idEditDeskripsi.setText(acaraEdit?.deskripsi)

            // --- (BLOK BARU 3: Tampilkan Gambar Lama) ---

            // Simpan path foto yang lama
            pathFotoTerpilih = acaraEdit?.fotoPath

            // Jika path-nya ada, tampilkan gambar yang lama
            if (!pathFotoTerpilih.isNullOrEmpty()) {
                Glide.with(this)
                    .load(pathFotoTerpilih)
                    .into(binding.idImgPreview)
                binding.idImgPreview.visibility = View.VISIBLE
            }
            // --- (AKHIR BLOK BARU 3) ---

        } else {
            // --- MODE TAMBAH BARU ---
            supportActionBar?.title = "Tambah Acara Baru"
            binding.idButtonSimpan.text = "Simpan Acara"
            // Tidak ada gambar untuk ditampilkan
        }
    }

    /**
     * Menyimpan atau Mengupdate data Acara ke database.
     */
    private fun simpanAtauUpdateAcara() {
        val nama = binding.idEditNama.text.toString()
        val tanggal = binding.idEditTanggal.text.toString()
        val lokasi = binding.idEditLokasi.text.toString()
        val penyelenggara = binding.idEditPenyelenggara.text.toString()
        val deskripsi = binding.idEditDeskripsi.text.toString()

        if (nama.isEmpty() || tanggal.isEmpty() || lokasi.isEmpty()) {
            Toast.makeText(this, "Nama, Tanggal, dan Lokasi wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        if (acaraEdit == null) {
            // --- MODE TAMBAH ---
            val acaraBaru = Acara(
                namaAcara = nama,
                tanggal = tanggal,
                lokasi = lokasi,
                penyelenggara = penyelenggara,
                deskripsi = deskripsi,
                fotoPath = pathFotoTerpilih // <-- SIMPAN PATH FOTO
            )
            acaraViewModel.tambahAcara(acaraBaru)
            Toast.makeText(this, "Acara berhasil disimpan!", Toast.LENGTH_SHORT).show()

        } else {
            // --- MODE EDIT ---
            val acaraUpdate = Acara(
                id = acaraEdit!!.id, // Pakai ID yang lama
                namaAcara = nama,
                tanggal = tanggal,
                lokasi = lokasi,
                penyelenggara = penyelenggara,
                deskripsi = deskripsi,
                fotoPath = pathFotoTerpilih // <-- SIMPAN PATH FOTO
            )
            acaraViewModel.perbaruiAcara(acaraUpdate)
            Toast.makeText(this, "Acara berhasil di-update!", Toast.LENGTH_SHORT).show()
        }

        finish() // Tutup activity dan kembali
    }

    // 'Kunci' untuk mengirim data antar Activity
    companion object {
        const val EXTRA_ACARA = "extra_acara"
    }
}