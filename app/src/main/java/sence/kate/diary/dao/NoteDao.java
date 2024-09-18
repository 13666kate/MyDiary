package sence.kate.diary.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import sence.kate.diary.entities.Note;

@Dao//Эта аннотация указывает, что интерфейс NoteDao
// является объектом доступа к данным (DAO) для Room.
public interface NoteDao {

    //Этот метод выполняет SQL-запрос для
    // выборки всех записей из таблицы notes в порядке убывания идентификатора.
    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();

    //Возвращает список объектов Note, представляющих все записи в базе данных.


    //Этот метод выполняет вставку новой записи в таблицу notes.
    // При конфликте (если запись с таким же идентификатором уже существует),
    // существующая запись заменяется новой.
    //Принимает объект Note в качестве параметра.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    //тот метод удаляет запись из таблицы notes.
    //Принимает объект Note в качестве параметра.
    @Delete
    void deleteNote(Note note);

}
