package com.example.abhishekshah.cointoss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishekshah.cointoss.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String COIN_SIDE = "coin side";

    private ImageView coinImage;
    private Button flipButton;

    private TextView pressureDisplay;
    private TextView rotationDisplay;
    private int coinSide;

    private MediaPlayer mp;
    private int curSide = R.drawable.heads;
    private MotionEvent pressureValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coinImage = (ImageView) findViewById(R.id.coin);
        flipButton = (Button) findViewById(R.id.flipButton);
        pressureDisplay = (TextView) findViewById(R.id.pressure);
        rotationDisplay=(TextView) findViewById(R.id.rotation);

        flipButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent pressureValue) {
                if (pressureValue.getAction() == pressureValue.ACTION_DOWN ) {
                    float pressure = pressureValue.getPressure();
                    pressureDisplay.setText("Screen Pressure: " + (float) pressure);
                    float j= (float)(pressure/25);
                    float rotations = (float)(j*j*509295.818);
                    rotationDisplay.setText("Rotations: " + (int) rotations);
                    flipCoin(rotations);
                    return true;
                }
                return false;
            }
        });
        // Restore all values and images after rotate

        if (savedInstanceState != null) {
            coinImage.setImageResource(Integer.parseInt(savedInstanceState.getCharSequence(COIN_SIDE).toString()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(COIN_SIDE, String.valueOf(curSide));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setButtonsEnabled(boolean enabled) {
        flipButton.setEnabled(enabled);
    }

    private long animateCoin(boolean stayTheSame, float rotations) {

        Rotate3dAnimation animation;

        if (curSide == R.drawable.heads) {
            animation = new Rotate3dAnimation(coinImage, R.drawable.heads, R.drawable.tails, 0, 180, 0, 0, 0, 0);
        } else {
            animation = new Rotate3dAnimation(coinImage, R.drawable.tails, R.drawable.heads, 0, 180, 0, 0, 0, 0);
        }
        if (stayTheSame) {
            animation.setRepeatCount((int) rotations); // must be odd (5+1 = 6 flips so the side will stay the same)
        } else {
            animation.setRepeatCount((int) rotations); // must be even (6+1 = 7 flips so the side will not stay the same)
        }

        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        coinImage.startAnimation(animation);
        setButtonsEnabled(false);
        return animation.getDuration() * (animation.getRepeatCount() + 1);
    }

    public void flipCoin(float rotations) {

        if(rotations-(int)rotations>=-0.25&&rotations-(int)rotations<=0.25) {
            coinSide = 1;
        }

        stopPlaying();
        mp = MediaPlayer.create(this, R.raw.coin_flip);
        mp.start();

        if (coinSide == 0) {

            boolean stayTheSame = (curSide == R.drawable.tails);
            long timeOfAnimation = animateCoin(stayTheSame,rotations);
            curSide = R.drawable.tails;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setButtonsEnabled(true);
                }
            }, timeOfAnimation + 10);

        } else {

            boolean stayTheSame = (curSide == R.drawable.heads);
            long timeOfAnimation = animateCoin(stayTheSame,rotations);
            curSide = R.drawable.heads;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setButtonsEnabled(true);
                }
            }, timeOfAnimation + 10);
        }
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
