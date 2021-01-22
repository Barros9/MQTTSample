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

        return FragmentClientBinding.inflate(inflater).apply {
            clientViewModel = viewModel
            lifecycleOwner = this@ClientFragment

            subscribe.setOnClickListener {
                viewModel.subscribe(subtopic.text.toString())
            }

            unsubscribe.setOnClickListener {
                viewModel.unsubscribe(subtopic.text.toString())
            }

            publish.setOnClickListener {
                viewModel.publish(pubtopic.text.toString(), pubmsg.text.toString())
            }
        }.root
    }
}
