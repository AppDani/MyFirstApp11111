package com.danielarog.myfirstapp.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.databinding.ChatMessageBinding
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ChatMessage
import com.danielarog.myfirstapp.models.ChatRoom
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ChatMessagesAdapter(
    val currentUser: AppUser,
    var chatRoom: ChatRoom
) : RecyclerView.Adapter<ChatMessagesAdapter.ChatMessagesViewHolder>() {



    class ChatMessagesViewHolder(
        val binding: ChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private fun getTime(message: ChatMessage) : String {
            val date = message.date
            return DateTimeFormatter.ofPattern("hh:mm:ss")
                .format(
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(date),
                        ZoneId.systemDefault()
                    )
                )
        }
        fun bind(
            currentUser: AppUser,
            chatMessage: ChatMessage
        ) {
            if (chatMessage.senderId == currentUser.uid) {
                binding.messageLayout.gravity = Gravity.START
                binding.messageLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
            } else {
                binding.messageLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                binding.messageLayout.gravity = Gravity.END
            }

            binding.chatMessageTimeTv.text = getTime(chatMessage)

            binding.chatMessageTv.text = chatMessage.content
            binding.chatSenderNameTag.text = chatMessage.senderName[0] + ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessagesViewHolder {
        val binding = ChatMessageBinding.inflate(LayoutInflater.from(parent.context))
        return ChatMessagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessagesViewHolder, position: Int) {
        val message = chatRoom.messages[position]
        holder.bind(currentUser, message)
    }

    override fun getItemCount(): Int {
        return chatRoom.messages.size
    }

    fun update(chatRoom: ChatRoom?) {
        chatRoom?.let { room ->
            this.chatRoom = room
            notifyDataSetChanged()
        }
    }
}