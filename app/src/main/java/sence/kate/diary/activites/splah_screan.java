package sence.kate.diary.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import sence.kate.diary.R;

public class splah_screan extends AppCompatActivity {
    AnimationDrawable Tail;
   int progresint=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah_screan);


        ImageView tail = (ImageView) findViewById(R.id.imageTail);
        tail.setBackgroundResource(R.drawable.tail);//Устанавливаем анимацию для заднего фона
        Tail = (AnimationDrawable) tail.getBackground();//Присваиваем

        progressBar();



    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Tail.start();
    }


    public void progressBar() {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);// создаем нашу переменную
        //  int progresint = 0; // счетчик для подсчета прогресса
        Intent intent = new Intent(splah_screan.this,MainActivity.class);
        //Отслеживаем прогрессс

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progresint++; //увеличиывем прогресс на 1
                progressBar.setProgress(progresint); // отслеживаем прогресс

                if(progresint>=100){ // если прогресс дошел ло 100 или привысил 100 то останавливаемся
                    timer.cancel();
                    startActivity(intent); // переход на другую  а5ктивность
                }
            }
        };

        timer.schedule(timerTask,20,35); // задержка и период

    }
}