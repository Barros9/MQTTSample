package com.barros.mqttsample.connectfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.barros.mqttsample.R
import com.barros.mqttsample.databinding.FragmentConnectBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel: ConnectViewModel by viewModels()
        val binding = FragmentConnectBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.connect.setOnClickListener {
            val serverURI = binding.serverURI.text.toString()
            val clientID = binding.clientID.text.toString()
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (serverURI.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.connect(
                    serverURI = serverURI,
                    clientID = clientID,
                    username = username,
                    password = password
                )
            } else {
                Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.navigateToClient.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(
                    ConnectFragmentDirections.actionConnectFragmentToClientFragment(
                        viewModel.connectInfo.value!!
                    )
                )
                viewModel.navigationCompleted()
            }
        })

        viewModel.showNoConnectionToast.observe(viewLifecycleOwner, {
            if (it) {
                Log.d(this.javaClass.name, getString(R.string.no_internet_connection))
                Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}
