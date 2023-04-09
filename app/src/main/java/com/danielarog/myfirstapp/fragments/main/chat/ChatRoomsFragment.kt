package com.danielarog.myfirstapp.fragments.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.activities.MainActivity
import com.danielarog.myfirstapp.adapters.ChatRoomsAdapter
import com.danielarog.myfirstapp.databinding.FragmentChatRoomsBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.ChatRoom
import com.danielarog.myfirstapp.viewmodels.AuthViewModel
import com.danielarog.myfirstapp.viewmodels.ChatViewModel
import com.google.gson.Gson

class ChatRoomsFragment : BaseFragment() {

    private lateinit var viewModel: ChatViewModel
    private var _binding: FragmentChatRoomsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        val user = (requireActivity() as MainActivity).viewModel.userLive.value
        binding.chatRoomsRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.chatRoomsLiveData.observe(viewLifecycleOwner) { chatRooms ->
            user?.let { currentUser ->
                val adapter = ChatRoomsAdapter(currentUser, chatRooms) { selectedRoom ->
                    val bundle = bundleOf(Pair("chatRoomId",selectedRoom.roomId))
                    findNavController()
                        .navigate(R.id.action_chatRoomsFragment_to_chatFragment,bundle)
                }
                binding.chatRoomsRv.adapter = adapter
            }
        }

    }

}