package com.mindorks.notesapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mindorks.notesapp.NotesApp
import com.mindorks.notesapp.R
import com.mindorks.notesapp.adapter.NotesAdapter
import com.mindorks.notesapp.clicklisteners.ItemClickListener
import com.mindorks.notesapp.db.Notes
import com.mindorks.notesapp.util.AppConstant
import com.mindorks.notesapp.util.PrefConstant
import com.mindorks.notesapp.workmanager.MyWorker
import java.util.*
import java.util.concurrent.TimeUnit


class MyNotesActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MyNotesActivity"
        const val ADD_NOTES_CODE = 100
    }

    var fullName: String = ""

    private lateinit var buttonAddNotes: FloatingActionButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerViewNotes: RecyclerView
    private var listNotes = ArrayList<Notes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notes)
        setupSharedPreference()
        bindViews()
        getIntentData()
        getDataFromDataBase()
        setupToolbarText()
        clickListeners()
        setupRecyclerView()
        setupWorkManager()
    }

    private fun getIntentData() {
        val intent = intent
        if (intent.hasExtra(AppConstant.FULL_NAME)) {
            fullName = intent.getStringExtra(AppConstant.FULL_NAME)
        }
        if (fullName.isEmpty()) {
            fullName = sharedPreferences.getString(PrefConstant.FULL_NAME, "")
        }


    }

    private fun setupWorkManager() {
        val constraint = Constraints.Builder()
                .build()
        val request = PeriodicWorkRequest
                .Builder(MyWorker::class.java, 1, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()
        WorkManager.getInstance().enqueue(request)
    }

    private fun getDataFromDataBase() {
        val notesApp = applicationContext as NotesApp
        val notesDao = notesApp.getNotesDb().notesDao()
        Log.d(TAG, notesDao.getAll().size.toString())
        listNotes.addAll(notesDao.getAll())
    }

    private fun setupSharedPreference() {
        sharedPreferences = getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private fun bindViews() {
        buttonAddNotes = findViewById(R.id.buttonAddNotes)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
    }

    private fun clickListeners() {
        buttonAddNotes.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivityForResult(Intent(this@MyNotesActivity, AddNotesActivity::class.java), ADD_NOTES_CODE)
            }
        })
    }

    private fun setupToolbarText() {
        if (supportActionBar != null) {
            supportActionBar?.title = fullName
        }

    }


    private fun setupRecyclerView() {
        val itemClickListener = object : ItemClickListener {
            override fun onUpdate(notes: Notes) {
                // update the value
                Log.d(TAG, notes.isTaskCompleted.toString())
                val notesApp = applicationContext as NotesApp
                val notesDao = notesApp.getNotesDb().notesDao()
                notesDao.updateNotes(notes)
            }

            override fun onClick(notes: Notes) {
                val intent = Intent(this@MyNotesActivity, DetailActivity::class.java)
                intent.putExtra(AppConstant.TITLE, notes.title)
                intent.putExtra(AppConstant.DESCRIPTION, notes.description)
                startActivity(intent)
            }

        }
        val notesAdapter = NotesAdapter(listNotes, itemClickListener)
        val linearLayoutManager = LinearLayoutManager(this@MyNotesActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = notesAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NOTES_CODE && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra(AppConstant.TITLE);
            val description = data?.getStringExtra(AppConstant.DESCRIPTION)
            val imagePath = data?.getStringExtra(AppConstant.IMAGE_PATH)

            val note = Notes(title = title!!, description = description!!, imagePath = imagePath!!, isTaskCompleted = false)
            addNotesToDb(note)
            listNotes.add(note)
            recyclerViewNotes.adapter?.notifyItemChanged(listNotes.size - 1)

        }
    }

    private fun addNotesToDb(notes: Notes) {
        //insert notes in DB
        val notesApp = applicationContext as NotesApp
        val notesDao = notesApp.getNotesDb().notesDao()
        notesDao.insert(notes)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.blog -> startActivity(Intent(this, BlogActivity::class.java))
        }
        return super.onOptionsItemSelected(item);

    }
}
