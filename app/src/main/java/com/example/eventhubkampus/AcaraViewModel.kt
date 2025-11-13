package com.example.eventhubkampus // Pastikan ini package-mu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File // <-- IMPORT BARU UNTUK MENGELOLA FILE

class AcaraViewModel(application: Application) : AndroidViewModel(application) {

    // Siapkan variabel untuk Repository dan LiveData
    private val repository: AcaraRepository
    val semuaAcara: LiveData<List<Acara>>

    // Blok 'init' ini akan dieksekusi saat AcaraViewModel pertama kali dibuat
    init {
        // 1. Dapatkan akses ke DAO dari AcaraDatabase
        val acaraDao = AcaraDatabase.getDatabase(application).acaraDao()
        // 2. Buat Repository menggunakan DAO tersebut
        repository = AcaraRepository(acaraDao)
        // 3. Ambil daftar 'semuaAcara' dari Repository
        semuaAcara = repository.semuaAcara
    }

    // --- CREATE ---
    // Fungsi ini akan dipanggil oleh UI untuk menambah data.
    fun tambahAcara(acara: Acara) = viewModelScope.launch {
        repository.tambah(acara)
    }

    // --- UPDATE ---
    fun perbaruiAcara(acara: Acara) = viewModelScope.launch {
        repository.perbarui(acara)
    }

    // --- DELETE (INI YANG KITA MODIFIKASI) ---
    fun hapusAcara(acara: Acara) = viewModelScope.launch {

        // --- (BAGIAN BARU 1: HAPUS FILE GAMBAR) ---
        // Sebelum menghapus dari database,
        // kita hapus dulu file fisiknya dari penyimpanan.
        acara.fotoPath?.let { path ->
            // Pastikan path-nya tidak kosong
            if (path.isNotEmpty()) {
                try {
                    // Buat objek File dari path yang disimpan
                    File(path).delete()
                } catch (e: Exception) {
                    // Jika gagal hapus file (jarang terjadi),
                    // biarkan saja, tapi cetak error-nya
                    e.printStackTrace()
                }
            }
        }
        // --- (AKHIR BAGIAN BARU 1) ---

        // --- (BAGIAN 2: HAPUS DARI DATABASE) ---
        // Setelah file (mungkin) dihapus,
        // lanjutkan hapus data dari database Room.
        repository.hapus(acara)
    }
}