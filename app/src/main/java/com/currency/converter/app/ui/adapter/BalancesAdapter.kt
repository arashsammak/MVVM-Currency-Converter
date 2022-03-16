package com.currency.converter.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.currency.converter.app.data.db.WalletEntity
import com.currency.converter.app.databinding.BalanceItemBinding
import java.text.DecimalFormat

class BalancesAdapter : RecyclerView.Adapter<BalancesAdapter.ViewHolder>() {

    private var wallets = emptyList<WalletEntity>()

    inner class ViewHolder(private val binding: BalanceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: WalletEntity) {
            binding.walletEntity = wallet
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BalanceItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMovie = wallets[position]
        holder.bind(currentMovie)
    }

    override fun getItemCount(): Int {
        return wallets.size
    }

    fun setData(newData: List<WalletEntity>) {
        wallets = newData
        notifyDataSetChanged()
    }

    interface OnItemClick {
        fun invoke(id: Int)
    }
}