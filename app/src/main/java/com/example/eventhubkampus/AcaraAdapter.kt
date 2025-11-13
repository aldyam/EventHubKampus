package com.example.eventhubkampus // Pastikan ini package-mu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhubkampus.databinding.ItemAcaraBinding

class AcaraAdapter : ListAdapter<Acara, AcaraAdapter.AcaraViewHolder>(AcaraDiffCallback()) {

    private var onItemClickCallback: ((Acara) -> Unit)? = null

    fun setOnItemClickListener(callback: (Acara) -> Unit) {
        this.onItemClickCallback = callback
    }

    class AcaraViewHolder(private val binding: ItemAcaraBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(acara: Acara) {

            // 1. Mengisi Teks
            binding.idItemNamaAcara.text = acara.namaAcara
            binding.idItemLokasi.text = acara.lokasi
            binding.idItemTanggal.text = acara.tanggal

            // Tampilkan penyelenggara
            if (acara.penyelenggara.isNullOrEmpty()) {
                binding.idItemPenyelenggara.visibility = View.GONE
            } else {
                binding.idItemPenyelenggara.visibility = View.VISIBLE
                binding.idItemPenyelenggara.text = acara.penyelenggara
            }

            // ---
            // 2. (BARU) Mengisi Teks Deskripsi
            // ---
            if (acara.deskripsi.isNullOrEmpty()) {
                // Sembunyikan jika tidak ada deskripsi
                binding.idItemDeskripsi.visibility = View.GONE
            } else {
                binding.idItemDeskripsi.visibility = View.VISIBLE
                binding.idItemDeskripsi.text = acara.deskripsi
            }

            // 3. Mengisi Gambar Header
            Glide.with(itemView.context)
                .load(acara.fotoPath) // Muat path foto
                .placeholder(R.drawable.ic_image_placeholder) // Gambar default
                .error(R.drawable.ic_image_placeholder) // Gambar jika error/kosong
                .centerCrop()
                .into(binding.idItemGambarHeader)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaraViewHolder {
        val binding = ItemAcaraBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AcaraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AcaraViewHolder, position: Int) {
        val acaraSaatIni = getItem(position)
        holder.bind(acaraSaatIni)

        holder.itemView.setOnClickListener {
            onItemClickCallback?.invoke(acaraSaatIni)
        }
    }

    class AcaraDiffCallback : DiffUtil.ItemCallback<Acara>() {
        override fun areItemsTheSame(oldItem: Acara, newItem: Acara): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Acara, newItem: Acara): Boolean {
            return oldItem == newItem
        }
    }
}