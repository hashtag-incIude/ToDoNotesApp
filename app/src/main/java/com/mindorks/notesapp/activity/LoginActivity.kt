package com.mindorks.notesapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.mindorks.notesapp.R
import com.mindorks.notesapp.util.AppConstant
import com.mindorks.notesapp.util.PrefConstant
import com.mindorks.notesapp.util.StoreSession

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private lateinit var editTextFullName: EditText
    private lateinit var editTextUserName: EditText
    private lateinit var buttonLogin: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupSharedPreference()
        bindViews()
    }

    private fun setupSharedPreference() {
        StoreSession.init(this)
    }

    private fun bindViews() {
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextUserName = findViewById(R.id.editTextUserName)
        buttonLogin = findViewById(R.id.buttonLogin)
        val clickAction = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val fullName = editTextFullName.text.toString()
                val userName = editTextUserName.text.toString()
                if (fullName.isNotEmpty() && userName.isNotEmpty()) {
                    val intent = Intent(this@LoginActivity, MyNotesActivity::class.java)
                    intent.putExtra(AppConstant.FULL_NAME, fullName)
                    startActivity(intent)
                    saveFullName(fullName)
                    saveLoginState()
                }

            }
        }
        buttonLogin.setOnClickListener(clickAction)
    }


    private fun saveLoginState() {
        StoreSession.write(PrefConstant.IS_LOGGED_IN, true)
    }

    private fun saveFullName(fullName: String) {
        StoreSession.write(PrefConstant.FULL_NAME, fullName)

    }

}
