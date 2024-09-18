package sence.kate.diary.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sence.kate.diary.entities.Note;

//Эта аннотация указывает, что класс NotesDatabase является базой данных Room.
// Указывает версию базы данных. Если мы изменим структуру базы данных в
// будущем, вы должны увеличить версию, чтобы Room мог знать, что необходимо выполнить миграцию данных.

// Отключает экспорт схемы базы данных. Это может быть полезно для предотвращения случайного раскрытия схемы базы данных.
@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
//Этот метод является статическим и синхронизированным
// , что означает, что он будет использоваться для получения экземпляра базы данных.
    private static NotesDatabase notesDatabase;
    public static synchronized NotesDatabase getDatabase(Context context){
        if(notesDatabase == null){
            notesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase.class,//ласс базы данных.
                    "notes_db"//мя базы данных.

            ).build();
        }
        return notesDatabase;
    }
    //Этот абстрактный метод возвращает объект NoteDao, который предоставляет методы для
    // выполнения операций CRUD (Create, Read, Update, Delete) с объектами типа Note в базе данных.
    public abstract NoteDao noteDao();
}
