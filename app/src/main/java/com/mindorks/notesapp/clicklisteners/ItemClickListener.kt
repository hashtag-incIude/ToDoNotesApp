package com.mindorks.notesapp.clicklisteners

import com.mindorks.notesapp.db.Notes

interface ItemClickListener {

    fun onClick(notes: Notes)

    fun onUpdate(notes: Notes)

}
