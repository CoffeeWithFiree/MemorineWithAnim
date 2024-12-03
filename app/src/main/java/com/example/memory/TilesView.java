package com.example.memory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;


class Card {
    Bitmap frontImage;
    Bitmap backImage;

    boolean isOpen = false;
    float x, y, width, height;
    float rotationY;

    public Card(float x, float y, float width, float height, Bitmap frontImage, Bitmap backImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.frontImage = frontImage;
        this.backImage = backImage;
    }

    public void setRotationY(float rotationY)
    {
        this.rotationY = rotationY;
    }

//    boolean isOpen = false; // цвет карты
//    float x, y, width, height;

    public void flipWithAnim(View parentView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "rotationY", 0f, 180f);
        animator.setDuration(150);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOpen = !isOpen;
                parentView.invalidate((int) x, (int) y, (int) (x + width), (int) (y + height));

                ObjectAnimator animator2 = ObjectAnimator.ofFloat(Card.this, "rotationY", 180f, 360f);
                animator2.setDuration(150);
                animator2.addUpdateListener(anim -> { // Изменено имя переменной
                    rotationY = (float) anim.getAnimatedValue();
                    parentView.invalidate((int) x, (int) y, (int) (x + width), (int) (y + height));
                });
                animator2.start();
            }
        });

        animator.addUpdateListener(anim -> { // Изменено имя переменной
            rotationY = (float) anim.getAnimatedValue();
            parentView.invalidate((int) x, (int) y, (int) (x + width), (int) (y + height));
        });

        animator.start();
    }

    public void draw(Canvas c) {
        c.save();
        c.translate(x + width / 2, y + height / 2);
        c.rotate(rotationY, 0, 0);
        c.translate(-(x + width / 2), -(y + height / 2));

        Bitmap toDraw = isOpen ? frontImage : backImage;
        c.drawBitmap(toDraw, x, y, null);

        c.restore();

        //        if (isOpen) {


//        if (isOpen) {

    }
    public boolean flip(float touch_x, float touch_y) {
        return touch_x >= x && touch_x <= x + width && touch_y >= y && touch_y <= y + height;
    }

}

public class TilesView extends View {
    // пауза для запоминания карт
    final int PAUSE_LENGTH = 1; // в секундах
    boolean isOnPauseNow = false;

    // число открытых карт
    int openedCard = 0;
    ArrayList<Card> cards = new ArrayList<>();

    int width, height; // ширина и высота канвы

    Card firstCard = null;
    Card secondCard = null;

    Bitmap backImage;
    ArrayList<Bitmap> frontImages = new ArrayList<>();

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadImages(context);
        newGame();
//        // 1) заполнить массив tiles случайными цветами
//        // сгенерировать поле 2*n карт, при этом
//        // должно быть ровно n пар карт разных цветов
//        cards.add(new Card(0,0, 200, 150, Color.YELLOW));
//        cards.add(new Card(200+50, 0, 200 + 200 + 50, 150, Color.YELLOW));
//
//        cards.add(new Card(0,200, 200, 150 + 200, Color.RED));
//        cards.add(new Card(200+50, 200, 200 + 200 + 50, 150+200, Color.RED));
    }

    private void loadImages(Context context)
    {
        backImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.i9);


        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i1));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i2));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i3));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i4));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i5));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i6));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i7));
        frontImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.i8));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
        // 2) отрисовка плиток
        // задать цвет можно, используя кисть
        //Paint p = new Paint();
        for (Card c: cards) {
            c.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPauseNow)
        {
            // 3) получить координаты касания
//            int x = (int) event.getX();
//            int y = (int) event.getY();
            float x = event.getX();
            float y = event.getY();
            // 4) определить тип события
//            if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPauseNow)


            // палец коснулся экрана

            for (Card c: cards) {

                if (!c.isOpen && c.flip(x, y)) {

                    c.flipWithAnim(this);

                    if (openedCard == 0) {
                        firstCard = c;
                        openedCard ++;

//                        if (c.flip(x, y)) {
//                            Log.d("mytag", "card flipped: " + openedCard);
//                            openedCard++;
//                            invalidate();
//                            return true;
//
                    } else if (openedCard == 1) {

                        secondCard = c;
                        openedCard++;
                        isOnPauseNow = true;

                        PauseTask task = new PauseTask();
                        task.execute(PAUSE_LENGTH);

//                        // перевернуть карту с задержкой
//                        if (c.flip(x, y)) {
//                            openedCard++;
//                            // 1) если открылис карты одинакового цвета, удалить их из списка
//                            // например написать функцию, checkOpenCardsEqual
//
//                            // 2) проверить, остались ли ещё карты
//                            // иначе сообщить об окончании игры
//
//                            // если карты открыты разного цвета - запустить задержку
//                            invalidate();
//                            PauseTask task = new PauseTask();
//                            task.execute(PAUSE_LENGTH);
//                            isOnPauseNow = true;
//                            return true;
                    }
                    // заставляет экран перерисоваться
//        return true;
                    invalidate();
                    return true;
                }
            }

        }
        return true;
    }


    public void newGame() {
        // запуск новой игры

        cards.clear();
        openedCard = 0;
        isOnPauseNow = false;

        ArrayList<Bitmap> images = new ArrayList<>();

        for (Bitmap img: frontImages)
        {
            images.add(img);
            images.add(img);
        }

        Collections.shuffle(images);

//        int numPairs = 8;
//        ArrayList<Integer> colors = new ArrayList<>();
//        Random random = new Random();
//
//        for (int i = 0; i < numPairs; i++)
//        {
//            colors.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
//            colors.add(colors.get(i));
//        }
//        Collections.shuffle(colors);

        int numCols = 4;
        int numRows = 4;
        int tileWidts = width / numCols;
        int tileHeight = height / numRows;

        for (int i = 0; i < images.size(); i++)
        {
            int col = i % numCols;
            int row = i / numCols;
            cards.add(new Card(col * tileWidts, row * tileHeight, tileWidts - 10, tileHeight - 10, images.get(i), backImage));

        }
        invalidate();
    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
//            Log.d("mytag", "Pause started");
            try {
                Thread.sleep(integers[0] * 1000); // передаём число секунд ожидания
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
//            Log.d("mytag", "Pause finished");
            return null;
        }

        // после паузы, перевернуть все карты обратно


        @Override
        protected void onPostExecute(Void aVoid) {

            if (firstCard != null && secondCard != null)
            {
                if (firstCard.frontImage == secondCard.frontImage)
                {
                    cards.remove(firstCard);
                    cards.remove(secondCard);
                } else {
                    firstCard.isOpen = false;
                    secondCard.isOpen = false;
                }
            }

            firstCard = null;
            secondCard = null;
            openedCard = 0;
            isOnPauseNow = false;
            invalidate();

            if (cards.isEmpty())
            {
                Toast.makeText(getContext(), "Win", Toast.LENGTH_SHORT).show();
            }



//            for (Card c: cards) {
//                if (c.isOpen) {
//                    c.isOpen = false;
//                }
//            }
//            openedCard = 0;
//            isOnPauseNow = false;
//            invalidate();
        }
    }
}
