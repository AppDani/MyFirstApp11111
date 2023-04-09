package com.danielarog.myfirstapp.fragments.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.activities.MainActivity
import com.danielarog.myfirstapp.adapters.ChatMessagesAdapter
import com.danielarog.myfirstapp.databinding.FragmentChatBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ChatRoom
import com.danielarog.myfirstapp.viewmodels.ChatViewModel
import com.google.android.material.textfield.TextInputEditText

class ChatFragment() : BaseFragment() {

    var adapter: ChatMessagesAdapter? = null
    var chatViewModel: ChatViewModel? = null

    var _binding : FragmentChatBinding? = null
    val binding : FragmentChatBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        val chatMessagesRv = binding.chatMessagesRv
        val sendMessageBtn = binding.etSendMessage
        val etMessage = binding.etMessage

        val user = (requireActivity() as MainActivity).viewModel.userLive.value!!
        chatMessagesRv.layoutManager = LinearLayoutManager(context)

        chatViewModel?.chatRoomLiveData?.observe(viewLifecycleOwner) { room ->
            if (room == null) return@observe
            if(adapter != null) {
                adapter!!.update(room)
                chatMessagesRv.smoothScrollToPosition(adapter!!.itemCount - 1);
                return@observe
            }
            // listen for incoming messages
            adapter = ChatMessagesAdapter(user, room)
            chatMessagesRv.adapter = adapter
        }
        requireArguments().getString("chatRoomId")?.let { chatRoomId ->
            chatViewModel?.enterChatRoom(chatRoomId)
        }
        // send a message
        sendMessageBtn.setOnClickListener {
            val message = etMessage.text.toString()
            if (message.isEmpty()) {
                snackBar("No message entered", view)
                return@setOnClickListener
            }
            chatViewModel?.sendMessage(chatViewModel!!.chatRoomLiveData.value!!, user, message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatViewModel = null
        _binding = null
    }

}