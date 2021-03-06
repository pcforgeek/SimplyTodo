package com.pcforgeek.simplytodo.data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.pcforgeek.simplytodo.data.dao.LabelDAO
import com.pcforgeek.simplytodo.data.dao.NoteDAO
import com.pcforgeek.simplytodo.data.database.NotesDatabase
import com.pcforgeek.simplytodo.data.entity.Label
import com.pcforgeek.simplytodo.data.entity.Notes
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class NotesRepository(application: Application) {
    private val notesDAO: NoteDAO
    private val labelDAO: LabelDAO
    private val notesList: LiveData<List<Notes>>
    private val labelList: LiveData<List<Label>>

    init {
        val notesdb = NotesDatabase.getInstance(application)
        notesDAO = notesdb!!.notesDataDao()
        labelDAO = notesdb.labelDao()
        notesList = notesDAO.getAllNotes()
        labelList = labelDAO.getAllLabel()
    }

    fun getAllNotes(): LiveData<List<Notes>> {
        return notesList
    }

    fun deleteNote(notesData: Notes) {
        deleteAsyncTask(notesDAO).execute(notesData)
    }

    fun insertNote(notesData: Notes) {
        insertAsyncTask(notesDAO).execute(notesData)
    }

    fun updateNote(notesData: Notes) {
        updateAsyncTask(notesDAO).execute(notesData)
    }

    fun getNotes(uuid: Long): Flowable<Notes> {
        val notes = Flowable.fromCallable<Notes>
        { notesDAO.getNote(uuid) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        return notes
    }

    fun getNotesWithLabelId(labelId: Int): Flowable<List<Notes>> {
        return Flowable.fromCallable<List<Notes>> {
            notesDAO.getNotesWithLabelId(labelId)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllLabel(): LiveData<List<Label>> {
        return labelList
    }

    fun addLabel(label: Label) {
        addLabelAsyncTask(labelDAO).execute(label)
    }

    class updateAsyncTask(private val notesDAO: NoteDAO) : AsyncTask<Notes, Unit, Unit>() {
        override fun doInBackground(vararg params: Notes) {
            notesDAO.updateNote(params[0])
        }
    }

    class insertAsyncTask(private val notesDAO: NoteDAO) : AsyncTask<Notes, Unit, Unit>() {
        override fun doInBackground(vararg params: Notes) {
            notesDAO.insertNote(params[0])
        }
    }

    class deleteAsyncTask(private val notesDAO: NoteDAO) : AsyncTask<Notes, Unit, Unit>() {
        override fun doInBackground(vararg params: Notes) {
            notesDAO.deleteNote(params[0])
        }
    }

    class addLabelAsyncTask(private val labelDAO: LabelDAO) : AsyncTask<Label, Unit, Unit>() {
        override fun doInBackground(vararg params: Label) {
            labelDAO.addLabel(params[0])
        }
    }

}