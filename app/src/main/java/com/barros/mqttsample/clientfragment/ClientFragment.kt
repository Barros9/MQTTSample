package com.barros.mqttsample.clientfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.barros.mqttsample.databinding.FragmentClientBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel: ClientViewModel by viewModels()
        val binding = FragmentClientBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.disconnect()
                }
            })

        viewModel.showToast.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.navigateToConnect.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(ClientFragmentDirections.actionClientFragmentToConnectFragment())
            }
        })

        binding.subscribe.setOnClickListener {
            viewModel.subscribe(binding.subtopic.text.toString())
        }

        binding.unsubscribe.setOnClickListener {
            viewModel.unsubscribe(binding.subtopic.text.toString())
        }

        binding.publish.setOnClickListener {
            viewModel.publish(binding.pubtopic.text.toString(), binding.pubmsg.text.toString())
        }

        return binding.root
    }
}
