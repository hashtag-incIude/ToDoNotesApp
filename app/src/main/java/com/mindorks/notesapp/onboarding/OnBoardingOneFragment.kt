package com.mindorks.notesapp.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mindorks.notesapp.R


class OnBoardingOneFragment : Fragment() {

    lateinit var textViewNext: TextView
    lateinit var onNextClick: OnNextClick

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onNextClick = context as OnNextClick
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val next = view.findViewById<TextView>(R.id.textViewNext)
        next.setOnClickListener {
            onNextClick.onClick()
        }
    }

    interface OnNextClick {
        fun onClick()
    }
}
