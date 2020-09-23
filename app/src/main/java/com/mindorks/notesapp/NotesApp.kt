package com.mindorks.notesapp

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.mindorks.notesapp.db.NotesDatabase

class NotesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
    }

    fun getNotesDb(): NotesDatabase {
        return NotesDatabase.getInstance(this)
    }

}