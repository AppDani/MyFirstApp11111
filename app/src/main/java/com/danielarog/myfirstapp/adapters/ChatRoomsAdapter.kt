package com.danielarog.myfirstapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.databinding.ChatRoomBinding
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ChatRoomReference
import com.squareup.picasso.Picasso

class ChatRoomsAdapter(
    val currentUser: AppUser,
    val chatRoomRefs: List<ChatRoomReference>,
    val onChatRoomSelected : (ChatRoomReference) -> Unit
) : RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomsViewHolder>() {


    class ChatRoomsViewHolder(
        val binding: ChatRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentUser: AppUser,
            chatRoomRef: ChatRoomReference,
            onChatRoomSelected : (ChatRoomReference) -> Unit
        ) {
            if (chatRoomRef.userId1 == currentUser.uid) {
                binding.chatRoomTv.text = "Chat with ${chatRoomRef.userName2}"
                if (chatRoomRef.userImage2.isNotEmpty()) {
                    Picasso.get().load(chatRoomRef.userImage2)
                        .into(binding.chatImageChatRoom)
                }
            } else {
                binding.chatRoomTv.text = "Chat with ${chatRoomRef.userName1}"
                if (chatRoomRef.userImage1.isNotEmpty()) {
                    Picasso.get().load(chatRoomRef.userImage1)
                        .into(binding.chatImageChatRoom)
                }
                binding.chatRoomTv.text = "Chat with ${chatRoomRef.userName1}"
            }
            binding.root.setOnClickListener {
                onChatRoomSelected.invoke(chatRoomRef)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomsViewHolder {
        val binding = ChatRoomBinding.inflate(LayoutInflater.from(parent.context))
        return ChatRoomsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomsViewHolder, position: Int) {
        val room = chatRoomRefs[position]
        holder.bind(currentUser,room, onChatRoomSelected)
    }

    override fun getItemCount(): Int {
        return chatRoomRefs.size
    }
}