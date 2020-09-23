package com.mindorks.notesapp.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mindorks.notesapp.R
import com.mindorks.notesapp.util.AppConstant

class DetailActivity : AppCompatActivity() {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var title: String
    private lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        bindViews()
        getIntentData()
        setupText()
    }

    private fun bindViews() {
        textViewDescription = findViewById(R.id.textViewDescription)
        textViewTitle = findViewById(R.id.textViewTitle)
    }

    private fun getIntentData() {
        val intent = intent
        if (intent != null) {
            title = intent.getStringExtra(AppConstant.TITLE)
            description = intent.getStringExtra(AppConstant.DESCRIPTION)
        }
    }

    private fun setupText() {
        textViewTitle.text = title
        textViewDescription.text = description
    }

}
