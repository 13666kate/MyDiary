package sence.kate.diary.listerens;

import sence.kate.diary.entities.Note;

public interface NotesListener {
    void onNoteCliced(Note note, int position);


}
