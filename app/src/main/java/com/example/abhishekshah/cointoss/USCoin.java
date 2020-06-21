package com.example.abhishekshah.cointoss;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class USCoin extends AppCompatActivity {

    private static final String COIN_SIDE = "coin side";
    private ImageView coinImage;
    private Button flipButton;
    private TextView rotationDisplay;
    private TextView info;
    private int coinSide;
    private SeekBar seekBar;
    private TextView seekValue;
    private Uri uri;
    private EditText coinInfoUS;
    private ImageButton previousActivity;
    private MediaPlayer mp;
    private int curSide = R.drawable.us_heads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.us_coin);
        coinImage = (ImageView) findViewById(R.id.coin);
        flipButton = (Button) findViewById(R.id.flipButton);
        rotationDisplay=(TextView) findViewById(R.id.rotation);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekValue = (TextView) findViewById(R.id.seekvalue);
        info = (TextView) findViewById(R.id.info);
        coinInfoUS= (EditText) findViewById(R.id.coinInfoIndia);
        previousActivity= (ImageButton)findViewById(R.id.nextActivity);

        previousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(USCoin.this, IndiaCoin.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekValue.setText("Force: "+String.format("%.4f",(float)(seekBar.getProgress()/8333.33))+" Newton");
            }
        });


        flipButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent pressureValue) {
                if (pressureValue.getAction() == pressureValue.ACTION_DOWN ) {
                   // float pressure = pressureValue.getPressure();
                    float pressure = (float)(seekBar.getProgress());
                    float j= (float)(pressure/8333.33);
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

        if (curSide == R.drawable.us_heads) {
            animation = new Rotate3dAnimation(coinImage, R.drawable.us_heads, R.drawable.us_tails, 0, 180, 0, 0, 0, 0);

        } else {
            animation = new Rotate3dAnimation(coinImage, R.drawable.us_tails, R.drawable.us_heads, 0, 180, 0, 0, 0, 0);
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

            boolean stayTheSame = (curSide == R.drawable.us_tails);
            long timeOfAnimation = animateCoin(stayTheSame,rotations);
            curSide = R.drawable.us_tails;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setButtonsEnabled(true);
                }
            }, timeOfAnimation + 10);

        } else {

            boolean stayTheSame = (curSide == R.drawable.us_heads);
            long timeOfAnimation = animateCoin(stayTheSame,rotations);
            curSide = R.drawable.us_heads;

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
