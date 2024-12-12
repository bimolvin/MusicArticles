package com.example.musicarticles.editor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicarticles.R


class EditorSubmitFragment(fragmentSendDataListener : OnFragmentSendDataListener) : Fragment() {

    private var sendDataListener : OnFragmentSendDataListener? = null
    init {
        this.sendDataListener = fragmentSendDataListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor_submit, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** attach listener from parent fragment */
        try {
            sendDataListener = context as OnFragmentSendDataListener
        } catch (_: ClassCastException) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = System.currentTimeMillis()
        val uri = "articles/$id"
        view.findViewById<TextView>(R.id.formed_uri).text = uri
        view.findViewById<Button>(R.id.post_article).setOnClickListener {
            sendData(id)
        }
        view.findViewById<Button>(R.id.button_back).setOnClickListener {
            back()
        }
    }

    private fun sendData(id: Long) {
        sendDataListener?.onDataReceived(id)
    }

    private fun back() {
        sendDataListener?.onButtonBack()
    }


    interface OnFragmentSendDataListener {
        fun onDataReceived(id: Long)
        fun onButtonBack()
    }
}