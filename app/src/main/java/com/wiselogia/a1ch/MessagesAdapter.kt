package com.wiselogia.a1ch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiselogia.a1ch.databinding.SendedMessageBinding
import java.sql.Timestamp

class MessagesAdapter(private val onClick: (UsefulMessageModel) -> Unit) :
    RecyclerView.Adapter<MessagesAdapter.MessagesHolder>() {

    private var showables = listOf<UsefulMessageModel>()
        set(value) {
            val res = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = field.size

                override fun getNewListSize() = value.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    field[oldItemPosition] == value[newItemPosition]

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ) = field[oldItemPosition] == value[newItemPosition]

            })
            field = value
            res.dispatchUpdatesTo(this)
        }

    inner class MessagesHolder(private val binding: SendedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val sender = binding.sender
        private val sendedValue = binding.sendedValue
        private val timestamp = binding.timestamp
        private val imageView = binding.imageView
        fun bind(message: UsefulMessageModel) {
            sender.text = message.from
            imageView.isVisible = message.image != null
            sendedValue.isVisible = message.text != null
            sendedValue.text = message.text
            message.image?.let { imageView.glide("/thumb/$it") }
            binding.root.setOnClickListener{
                onClick(message)
            }
            timestamp.text = Timestamp(message.time).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesHolder {
        return MessagesHolder(
            SendedMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    fun addItems(newItems: List<UsefulMessageModel>) {
        showables = newItems
    }


    override fun onBindViewHolder(holder: MessagesHolder, position: Int) =
        holder.bind(showables[position])

    override fun getItemCount() = showables.size

}