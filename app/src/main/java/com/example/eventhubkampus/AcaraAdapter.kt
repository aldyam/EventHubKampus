package com.example.eventhubkampus // Pastikan ini package-mu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhubkampus.databinding.ItemAcaraBinding

/**
 * Adapter untuk RecyclerView.
 */
class AcaraAdapter : ListAdapter<Acara, AcaraAdapter.AcaraViewHolder>(AcaraDiffCallback()) {

    // Variabel untuk menyimpan fungsi callback saat item diklik
    private var onItemClickCallback: ((Acara) -> Unit)? = null

    // Fungsi untuk diatur dari luar (misal: dari MainActivity)
    fun setOnItemClickListener(callback: (Acara) -> Unit) {
        this.onItemClickCallback = callback
    }

    /**
     * ViewHolder berisi logika untuk mengikat data (Acara)
     * ke tampilan (item_acara.xml).
     */
    class AcaraViewHolder(private val binding: ItemAcaraBinding) : RecyclerView.ViewHolder(binding.root) {

        // --- FUNGSI BIND (INI YANG KITA UBAH) ---
        fun bind(acara: Acara) {

            // 1. Mengisi Teks
            binding.idItemNamaAcara.text = acara.namaAcara
            binding.idItemTanggal.text = acara.tanggal
            binding.idItemPenyelenggara.text = acara.penyelenggara // <-- Teks baru

            // 2. Mengisi Gambar
            Glide.with(itemView.context)
                .load(acara.fotoPath) // Muat path foto
                .placeholder(R.drawable.ic_image_placeholder) // Gambar default saat loading
                .error(R.drawable.ic_image_placeholder) // Gambar jika acara tidak punya foto
                .centerCrop() // Potong gambar agar pas
                .into(binding.idItemGambar) // Masukkan ke ImageView
        }
    }

    /**
     * Dipanggil saat RecyclerView perlu membuat ViewHolder baru
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaraViewHolder {
        // Buat binding untuk item_acara.xml
        val binding = ItemAcaraBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AcaraViewHolder(binding)
    }

    /**
     * Dipanggil saat RecyclerView ingin menampilkan data
     * di posisi tertentu.
     */
    override fun onBindViewHolder(holder: AcaraViewHolder, position: Int) {
        val acaraSaatIni = getItem(position)
        holder.bind(acaraSaatIni) // Panggil fungsi bind kita yang sudah di-update

        // Atur OnClickListener untuk seluruh tampilan item
        holder.itemView.setOnClickListener {
            // Panggil fungsi callback jika ada
            onItemClickCallback?.invoke(acaraSaatIni)
        }
    }

    /**
     * DiffCallback membantu ListAdapter menentukan
     * item mana yang berubah, ditambah, atau dihapus.
     */
    class AcaraDiffCallback : DiffUtil.ItemCallback<Acara>() {
        override fun areItemsTheSame(oldItem: Acara, newItem: Acara): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Acara, newItem: Acara): Boolean {
            return oldItem == newItem
        }
    }
}