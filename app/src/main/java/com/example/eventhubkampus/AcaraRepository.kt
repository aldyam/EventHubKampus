package com.example.eventhubkampus

import androidx.lifecycle.LiveData

/**
 * Repository adalah perantara antara sumber data (DAO) dan ViewModel.
 * Ia mengambil data dari DAO dan menyediakannya untuk ViewModel.
 */
class AcaraRepository(private val acaraDao: AcaraDao) {

    // --- READ ---
    // Ambil daftar semua acara dari DAO.
    // Karena DAO mengembalikan LiveData, kita tidak perlu
    // melakukan apa-apa lagi, datanya sudah 'live'.
    val semuaAcara: LiveData<List<Acara>> = acaraDao.semuaAcara()

    // --- CREATE ---
    // Fungsi 'suspend' ini memanggil fungsi 'suspend' di DAO
    // Ini harus dipanggil dari sebuah Coroutine (nanti di ViewModel)
    suspend fun tambah(acara: Acara) {
        acaraDao.tambahAcara(acara)
    }

    // --- UPDATE ---
    suspend fun perbarui(acara: Acara) {
        acaraDao.perbaruiAcara(acara)
    }

    // --- DELETE ---
    suspend fun hapus(acara: Acara) {
        acaraDao.hapusAcara(acara)
    }
}