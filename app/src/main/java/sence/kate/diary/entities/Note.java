package sence.kate.diary.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// это класс создания полей и таблицы для базы данных
@Entity (tableName = "notes") //название моей таблицы
//@Entity указывает, что объекты класса Note будут сохраняться в таблицу "notes" в базе данных.
public class Note implements Serializable {

  @PrimaryKey(autoGenerate = true)
  private int id;
  //@PrimaryKey(autoGenerate = true) указывает, что поле
    // id будет использоваться как первичный ключ. autoGenerate = true говорит ,
    // что значения ключа будут автоматически генерироваться.

  @ColumnInfo(name = "title")// указывает, что
  // поле title будет сохраняться в столбец "заголовка" базы данных.
    private String title;

  // указывает, что
    // поле date_time будет сохранять дату создания.
    @ColumnInfo(name = "date_time")
    private String dateTime;
    // указывает, что
    // поле subtitle будет сохранять подзоголовок.
    @ColumnInfo(name = "subtitle")
    private String subtitle;
    // указывает, что
    // поле subtitle будет сохранять текст записи.
    @ColumnInfo(name = "note_text")
    private String noteText;
    // указывает, что
    // поле subtitle будет сохранять путь к картинке.
    @ColumnInfo(name = "image_path")
    private String imagePath;
    // указывает, что
    // поле subtitle будет сохранять выбор цвета для заголовка.
    @ColumnInfo(name = "color")
    private String color;
//не использовалось
    @ColumnInfo(name = "web_link")
    private String webLink;

    //гетеры и сетеры для получения или изменения или установки в базу
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " : " + dateTime;
    }
}
