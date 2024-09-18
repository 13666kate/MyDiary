package sence.kate.diary.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sence.kate.diary.R;
import sence.kate.diary.dao.NotesDatabase;
import sence.kate.diary.entities.Note;

public class CreateNodeActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle,inputNoteText;
    private TextView textDateTime ;
    private String selectNoteColor;
    private View viewSubtitleIndicator;
    private static final int  REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
    private ImageView imageNote;
    private String imagePath;

    private Note alredyNote;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_node);

        ImageView imageBack = findViewById(R.id.imageBack);// кнопка назад
        imageBack.setOnClickListener(new View.OnClickListener() {//обработчик кнопки назад
            // Обработчик клика на кнопку "назад" - вызывает метод onBackPressed()
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        inputNoteTitle = (EditText) findViewById(R.id.inputNodeTitle);//едит текст для заголовка
        inputNoteSubtitle = (EditText) findViewById(R.id.inputNodeSubtitle);// текст для подзоголовка
       inputNoteText = (EditText) findViewById(R.id.inputNode);// текст для текста
       textDateTime = findViewById(R.id.textDateTime);// время
       viewSubtitleIndicator = findViewById(R.id.textSubtitleIndicator);//для цвета
       imageNote = findViewById(R.id.imageNote);// картинка для добавления в записи



       textDateTime.setText(new SimpleDateFormat(
               "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
               .format(new Date())
       );
       ImageView imageSave = findViewById(R.id.imageSave);
       imageSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               saveNote();
           }
       });

        selectNoteColor  = "#0F0730";
        imagePath = "";


        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alredyNote  = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemove).setVisibility(View.GONE);
                imagePath =" ";
            }
        });

       initColor();
       setSubtitleIndicatorColor();





    }

private void  setViewOrUpdateNote(){
    inputNoteTitle.setText(alredyNote.getTitle());
    inputNoteSubtitle.setText(alredyNote.getSubtitle());
    inputNoteText.setText(alredyNote.getNoteText());
    textDateTime.setText(alredyNote.getDateTime());

    if(alredyNote.getImagePath() != null && !alredyNote.getImagePath().trim().isEmpty()){
        imageNote.setImageBitmap(BitmapFactory.decodeFile(alredyNote.getImagePath()));
        imageNote.setVisibility(View.VISIBLE);
        findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
        imagePath = alredyNote.getImagePath();
    }

}

//aveNote() выполняет сохранение заметки в базе данных.
    private void  saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){// есшли заголовок заметки  пуст
            //говорим пользователю что так не пойдет
            Toast.makeText(this, "Note title cant be empty", Toast.LENGTH_SHORT).show();
            return;// возвращаем это значение
            // если подзаголовок пуст  и  текст пуст
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty()
        && inputNoteText.getText().toString().trim().isEmpty()){
            //возмущаемся что так нельзя
            Toast.makeText(this, "Note title cant be empty", Toast.LENGTH_SHORT).show();
            return;// продолжаем возмущатся
        }
        // обьект класса Note- там наши поля
        final Note note = new Note();
        //устанавливаем текст в заголовок
        note.setTitle(inputNoteTitle.getText().toString());
        //устанавливаем текст в подзаголовок
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        //устанавливаем текст в текст
        note.setNoteText(inputNoteText.getText().toString());
        //устанавливаем текст дату в поле дата
        note.setDateTime(textDateTime.getText().toString());
        // устанавливаем цвет в поле цвета
        note.setColor(selectNoteColor);
        // картинку в поле для картинки
        note.setImagePath(imagePath);


        // проверка существующей заметки на обновление
        if(alredyNote != null){// если эта заметка есть
            note.setId(alredyNote.getId());//установка идентификаторв на новую
        }
        //вставка заметки в базу данных
        //происходит асинхронно
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }


// после завершения фоновой задачи
            //идет сохранение или обновление
            //RESULT_OK - это код удачного завершения
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
               Intent intent = new Intent();
               setResult(RESULT_OK,intent);
               finish();
            }
        }
        //execute() для запуска асинхронной задачи.
        // Этот метод запускает фоновый поток, в котором
        // выполняется метод doInBackground(),
        // определенный внутри класса SaveNoteTask
        new SaveNoteTask().execute();//вызов асинхронной задачи
    }

    private  void initColor(){
        //BottomSheetBehavior - это часть библиотеки
        // Android Design Support Library, предоставляющей
        // функциональность для реализации визуальных элементов
        // в виде "листа" (bottom sheet) — области, которая может
        // выдвигаться или сворачиваться внизу экрана.
        final LinearLayout layoutMiscell = findViewById(R.id.layoutMickel);//контейнер цветов
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscell);


         //если мы кликнем на элемент сворачивания
        layoutMiscell.findViewById(R.id.textMiscell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если он не развернут то разворачиваем
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    // иначе сворачиваем
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        //обьявление цвето в
       final  ImageView imageColor1 = layoutMiscell.findViewById(R.id.imageColor1);
       final  ImageView imageColor2 = layoutMiscell.findViewById(R.id.imageColor2);
       final  ImageView imageColor3 = layoutMiscell.findViewById(R.id.imageColor3);
       final  ImageView imageColor4 = layoutMiscell.findViewById(R.id.imageColor4);
       final  ImageView imageColor5 = layoutMiscell.findViewById(R.id.imageColor5);

       //при нажатии на нужный нам цвет мы помечаем его отмеченной галочкой
       layoutMiscell.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            selectNoteColor = "#0F0730";//устанавливаем цвет
            imageColor1.setImageResource(R.drawable.baseline_done);
            imageColor2.setImageResource(0);// остальные все цвета не отмечаем
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
           }
       });
        layoutMiscell.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#3E23B3";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.baseline_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscell.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#A4B1EF";
                imageColor1.setImageResource(R.drawable.baseline_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.baseline_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscell.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#A698E4";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.baseline_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscell.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#6C7083";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.baseline_done);
                setSubtitleIndicatorColor();
            }
        });


        // проверяем какой цвет отмечен
        try {

         // проверяем установлен ли цвет и строка цвета не пуста
        if(alredyNote != null && alredyNote.getColor() != null && !alredyNote.getColor().trim().isEmpty()) {
            switch (alredyNote.getColor()) {// передаем какой цвет выбрал пользователь
                case "#3E23B3":// если втрой 2-фиксируем и тд. первый цвет по умолчанию
                    layoutMiscell.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#A4B1EF":
                    layoutMiscell.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#A698E4":
                    layoutMiscell.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#6C7083":
                    layoutMiscell.findViewById(R.id.viewColor5).performClick();
                    break;


            }
        }
        }catch (Exception e ){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // при нажатии на кнопку добавления картинки
       layoutMiscell.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //если сверток открыт мы его закрываем
             bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
             //есть  у нас нет разарешение на использование галерии
             if(ContextCompat.checkSelfPermission(getApplicationContext(),
                     Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

              // то мы его запрашиваем
                 ActivityCompat.requestPermissions(
                         CreateNodeActivity.this,
                         new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                         REQUEST_CODE_STORAGE_PERMISSION
                 );
             }else{
                 selectImage();// если разрешение есть то выюираем изображение
             }
           }
       });

        // для видимости удаления этой заметки
        if(alredyNote != null){ // если заметка не пуста
          layoutMiscell.findViewById(R.id.layoutdeleteNote).setVisibility(View.VISIBLE);
          layoutMiscell.findViewById(R.id.layoutdeleteNote).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  // пр нажатии список сварачивается
                  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                 ShoedeleteNodeDialog();// вызываем диалоговое окно
              }
          });
        }


    }

    private void ShoedeleteNodeDialog(){
        if(dialog == null){ // если диалога нет
          AlertDialog.Builder builder =new AlertDialog.Builder(CreateNodeActivity.this);
          //создаем его
          View view = LayoutInflater.from(this).inflate(
                  //это хранение диалогового окна на удаление
                  R.layout.layout_delete_node,
                  (ViewGroup) findViewById(R.id.layoutSeleteNoteConteiner)

          );
          builder.setView(view);//устанавливаем этот диалог в билдер
          dialog = builder.create(); // создаем диалог из билдера
          if(dialog.getWindow() != null){
              dialog.getWindow().setBackgroundDrawable( new ColorDrawable(0));
              //установка фона диалогового окна на прозрачный
          }

        //при нажатии на кнопку удалить в диалоговом окне
          view.findViewById(R.id.textDeleteNode).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  @SuppressLint("StaticFieldLeak")
                  //Это асинхронная задача, которая выполняется в фоновом потоке.
                  class DeleteNoteTask extends AsyncTask<Void,Void,Void> {


                      //doInBackground(Void... voids) - это метод, который принадлежит классу
                      // , расширяющему AsyncTask. AsyncTask предоставляет простой способ
                      // выполнения задачи в фоновом потоке,
                      // а doInBackground является методом, где выполняется фактическая
                      // работа в фоновом режиме.

                      //В этом методе выполняется удаление заметки из базы данных в фоновом режиме,
                      // чтобы не блокировать пользовательский интерфейс во время выполнения длительной
                      // операции.
                      @Override
                      protected Void doInBackground(Void... voids) {
                          NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                  .deleteNote(alredyNote);//удаляем заметку с юазы данных

                          return null;
                      }
                 //onPostExecute(Void aVoid) - это метод, который также принадлежит
                 // классу, расширяющему AsyncTask. Этот метод вызывается после завершения
                 // выполнения метода doInBackground в фоновом потоке. Он выполняется в основном
                 // потоке, что// позволяет вам взаимодействовать с элементами пользовательского интерфейса.
                      @Override
                      protected void onPostExecute(Void aVoid) {
                          super.onPostExecute(aVoid);
                          Intent intent = new Intent();
                          // устанавливается флаг "isNoteDeleted" со значением true,
                          // чтобы указать, что заметка была удалена.
                          intent.putExtra("isNoteDeleted", true);
                          setResult(RESULT_OK, intent);
                          finish();//авершаем текущую активность
                      }
                  }

                  new DeleteNoteTask().execute();
              }
          });

          view.findViewById(R.id.textCencle).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 dialog.dismiss();
              }
          });
        }
        dialog.show();// отображаем диалоговое окно
    }


    //установкв цвета фона подзаголовка
    private void setSubtitleIndicatorColor(){
        //GradientDrawable - это класс в Android, который позволяет создавать
        // фигуры с градиентами, цветами и другими эффектами.
        GradientDrawable gradientDrawable =  (GradientDrawable) viewSubtitleIndicator.getBackground();
        //станавливает цвет фона для GradientDrawable. Метод Color.parseColor(selectNoteColor)
        // используется для преобразования строки, представляющей цвет в формате "#RRGGBB"
        gradientDrawable.setColor(Color.parseColor(selectNoteColor));
    }


    private void selectImage(){
        //запрс на выбор двнных изображения
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //запускаем активность на выбор изображения
        //REQUEST_CODE_SELECT_IMAGE - это код по котоому определяется выцбор картинки
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);

    }
    //метод принятия или околения разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==  REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            //если разрешение есть на внешнее хранилище
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //доступ
               selectImage(); 
            }else {
                //иначе мы жалуемся что его нет
                Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //был ли запрос на выбор изображения и успешно ли пользователь выбрал изображение
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            //есть ли двнные о изображении
            if(data !=null){
                //Получаем Uri изображения из данных,
                // которые возвращаются после выбора изображения из галереи.
                Uri imageUri = data.getData();
                //если Uri не пустой
                 if(imageUri != null){
                     try {
                         //Открываем InputStream для чтения данных изображения из Uri
                         InputStream inputStream = getContentResolver().openInputStream(imageUri);
                         //оздаем Bitmap из данных, считанных из InputStream. В данном
                         //случае, это изображение, которое будет отображаться в ImageView (imageNote).//
                         Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                         //Устанавливает созданный Bitmap в ImageView для отображения выбранного изображения.
                         imageNote.setImageBitmap(bitmap);
                         //Делаем ImageView видимым, таким образом, отображая выбранное изображение.
                         imageNote.setVisibility(View.VISIBLE);
                         //кнопка используется для удаления выбранного изображения.видима
                         findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
                         //Получает путь к выбранному изображению из его Uri с использованием метода getPathFromUri
                         imagePath = getPathFromUri(imageUri);
                     }catch (Exception e){
                         //обработка ошибки
                         Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
            }
        }
    }
    private String getPathFromUri(Uri contextUri){
        //Объявление переменной filePath, которая будет использоваться для хранения пути к файлу.
        String filePath ;
        //Создание Cursor для запроса информации о файле
        Cursor cursor = getContentResolver().query(
                contextUri,null,null,null,null);
           if(cursor == null){
               //Если Cursor равен null, то просто используется путь из Uri, вызывая метод getPath()
               filePath = contextUri.getPath();
           }else{
               //еремещение курсора на первую строку результата запроса.
               // Это нужно, чтобы получить данные о файле.
               cursor.moveToFirst();
               //Получение индекса столбца "_data" в результирующем наборе данных
               int index = cursor.getColumnIndex("_data");
               //Получение строки из столбца "_data", которая представляет путь к файлу
               filePath = cursor.getString(index);
               //Возврат строки, представляющей путь к файлу.
               cursor.close();
           }

      return filePath;
    }
}