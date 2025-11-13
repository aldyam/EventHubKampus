package com.example.eventhubkampus // Pastikan ini package-mu

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tabel_acara")
data class Acara(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val namaAcara: String,
    val tanggal: String,
    val lokasi: String,
    val penyelenggara: String,
    val deskripsi: String,

    // --- INI ADALAH BARIS YANG KITA TAMBAHKAN ---
    val fotoPath: String? = null // Path ke file gambar di internal storage

) : Parcelable