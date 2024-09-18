package sence.kate.diary.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import sence.kate.diary.R;
import sence.kate.diary.adapters.NotesAdapter;
import sence.kate.diary.dao.NotesDatabase;
import sence.kate.diary.entities.Note;
import sence.kate.diary.listerens.NotesListener;

public class MainActivity extends AppCompatActivity implements NotesListener {
    //идентификации запроса добавления новой заметки
 public static final int REQUEST_CODE_ADD_NOTE=1;
 //дентификация запроса обновления заметки.
    public static final int REQUEST_CODE_UPDATE=2;
    //для идентификация запроса отображения заметок.
    public static final int REQUEST_CODE_SHOW_NOTES=3;

    //ля работы с RecyclerView, отображающим список заметок.
 private RecyclerView notesRecyclerView;
 //бъявление списка noteList, который будет содержать объекты класса Note
 private List<Note> noteList;
 private NotesAdapter notesAdapter;
 public MediaPlayer mediaPlayer;
 public ImageView music;

 private  int noteClicedPosition= -1;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddMain);//добавление заметки
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNodeActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );

            }
        });

        notesRecyclerView = findViewById(R.id.notesRecuclerView);//список заметок
        //Установка менеджера компоновки для RecyclerView.
        // В данном случае используется StaggeredGridLayoutManager с двумя столбцами.
        notesRecyclerView.setLayoutManager(
               new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );


        noteList = new ArrayList<>();//для хранения заметок.
        //нициализация переменной notesAdapter, передавая список заметок и текущий контекст.
        notesAdapter = new NotesAdapter(noteList, this);
        //становка адаптера для RecyclerView.
        notesRecyclerView.setAdapter(notesAdapter);
        //получение списка заметок с запросом REQUEST_CODE_SHOW_NOTES.
        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        EditText inputSearch = findViewById(R.id.input_Search);

//для отслеживания изменений текста в inputSearch.
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
              //Вызывается для уведомления о том, что текст изменился.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             notesAdapter.cenclelTimer();
            }
            //Вызывается после того, как текст был изменен.
            @Override
            public void afterTextChanged(Editable s) {
             if(noteList.size() != 0 ){
                 notesAdapter.seachNote(s.toString());
             }
            }
        });

        findViewById(R.id.music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    pause();
                }else{
                   play();
                }
            }
        });
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.m1);

    }

public  void  play(){
        mediaPlayer.start();
}
    public  void  pause(){
        mediaPlayer.pause();
    }

    //Реализация метода интерфейса NotesListener. Вызывается при клике на заметку.
    @Override
    public void onNoteCliced(Note note, int position) {
        //Устанавливает значение переменной noteClicedPosition
        // равным позиции заметки, на которую был клик.
       noteClicedPosition = position;
       //оздает новый Intent для перехода к CreateNodeActivity
       Intent intent = new Intent(getApplicationContext(), CreateNodeActivity.class);
       //Помещает в Intent дополнительную информацию о том, что это представление или обновление заметки.
       intent.putExtra("isViewOrUpdate",true);
       intent.putExtra("note", note);
       //Запускает активность CreateNodeActivity с ожиданием результата,  startActivityForResult
       startActivityForResult(intent,REQUEST_CODE_UPDATE);
    }

    //Метод для выполнения асинхронной задачи получения заметок из базы данных.
    //Метод для выполнения асинхронной задачи получения заметок из базы данных.
    private void getNotes(final int requestCode, final  boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
      // GetNoteTask  возвращает список заметок из базы данных.
        class GetNoteTask extends AsyncTask<Void,Void, List<Note>>{
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                // Обработка результатов после выполнения фоновой за
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    // // Если это запрос для отображения заметок,
                    // добавляем их в список и обновляем адаптер
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    //// Если это запрос на добавление новой заметки,
                    // добавляем в начало списка и обновляем адаптер
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE) {
                    /// Если это запрос на обновление существующей заметки
                    noteList.remove(noteClicedPosition);

                    if (isNoteDeleted) {
                        //// Если заметка удалена, уведомляем адаптер об удалении
                        notesAdapter.notifyItemRemoved(noteClicedPosition);
                    } else {
                        // Если заметка изменена, добавляем обновленную заметку на место и уведомляем адаптер
                        noteList.add(noteClicedPosition, notes.get(noteClicedPosition));
                        notesAdapter.notifyItemChanged(noteClicedPosition);
                    }

                }
            }
        }
        new GetNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //// Обработка результатов, возвращаемых из других активностей
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            // Если добавлена новая заметка, обновляем список
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        }else if(requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK){
            // Если заметка обновлена или удалена, обновляем список
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE,data.getBooleanExtra("isNoteDeleted",false));
            }
        }
    }
}