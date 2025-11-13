package com.example.eventhubkampus // Pastikan package ini sesuai dengan proyekmu

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO (Data Access Object)
 * Interface ini berisi fungsi-fungsi untuk berinteraksi dengan database (CRUD).
 * Kita menggunakan 'suspend' untuk fungsi yang berjalan lama agar tidak
 * memblokir UI (dijalankan dengan Coroutines).
 */
@Dao
interface AcaraDao {

    // --- CREATE ---
    // @Insert: Perintah untuk menambah data baru
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Abaikan jika ada data duplikat
    suspend fun tambahAcara(acara: Acara)

    // --- READ ---
    // @Query: Perintah kustom menggunakan bahasa SQL
    // Mengambil semua data dari 'tabel_acara'
    // Diurutkan berdasarkan 'id' secara menurun (DESC) = data terbaru di atas
    @Query("SELECT * FROM tabel_acara ORDER BY id DESC")
    fun semuaAcara(): LiveData<List<Acara>>
    // Kita menggunakan LiveData agar data di UI bisa ter-update otomatis
    // setiap kali ada perubahan di database.

    // --- UPDATE ---
    // @Update: Perintah untuk memperbarui data yang sudah ada
    @Update
    suspend fun perbaruiAcara(acara: Acara)

    // --- DELETE ---
    // @Delete: Perintah untuk menghapus data
    @Delete
    suspend fun hapusAcara(acara: Acara)
}