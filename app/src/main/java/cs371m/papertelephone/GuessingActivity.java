package cs371m.papertelephone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.PageIndicatorView;

import mbanje.kurt.fabbutton.FabButton;

public class GuessingActivity extends AppCompatActivity {
    private EditText mGuessEditTextView;
    private CountDownTimer timer;
    private int numRounds;
    private PageIndicatorView pageIndicator;
    private TextView pageNumberView;
    private boolean roundStart;
    private int countDownSeconds;
    private boolean timeLeft;
    private float[] greenHSL, redHSL, outHSL;

    private FabButton timer_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessing);

        mGuessEditTextView = (EditText) findViewById(R.id.guess_input);
        mGuessEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mGuessEditTextView.clearFocus();
                }
                return false;
            }
        });

        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        pageIndicator = (PageIndicatorView) findViewById(R.id.guess_page_indicator);
        pageIndicator.setCount(numRounds);
        pageIndicator.setSelection(getTelephone().counter - 1);
        pageIndicator.setViewPager(null);
        pageIndicator.setSelectedColor(Color.BLACK);
        pageIndicator.setUnselectedColor(Color.GRAY);
        pageNumberView = (TextView) findViewById(R.id.guess_page_number);
        pageNumberView.setText(getString(R.string.page_number, getTelephone().counter, numRounds));

        greenHSL = new float[3];
        redHSL = new float[3];
        outHSL = new float[3];
        ColorUtils.colorToHSL(getResources().getColor(R.color.green), greenHSL);
        ColorUtils.colorToHSL(getResources().getColor(R.color.red), redHSL);

        timer_button = (FabButton) findViewById(R.id.time_button);
        timer_button.bringToFront();
        roundStart = true;

        roundStart = false;
        timer_button.showProgress(true);
        timer_button.setIcon(getResources().getDrawable(R.drawable.timer_off), getResources().getDrawable(R.drawable.timer_off));
        timer_button.showShadow(true);
        timeLeft = true;
        if (numRounds <= 8)
            pageIndicator.setVisibility(View.VISIBLE);
        else
            pageNumberView.setVisibility(View.VISIBLE);
        Log.d("GuessingAct", "Counter = " + getTelephone().counter);

        timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guessButtonClicked();

            }
        });

        countDownSeconds = MainActivity.guessCountdown == 0 ? 15 : MainActivity.guessCountdown;
        getTelephone().secondsRemaining = countDownSeconds;
        loadImage();
    }

    private void loadImage() {
        byte[] byteArray = getTelephone().pictures.get(getTelephone().pictures.size() - 1);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.guess_image_view);

        image.setImageBitmap(bmp);
        if (numRounds > 8) {
            pageIndicator.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.guess_page_number);
        } else
            pageNumberView.setVisibility(View.GONE);
    }

    public void guessButtonClicked() {
        String guessInput = mGuessEditTextView.getText().toString();
        if (guessInput.length() == 0) {
            Toast.makeText(this, "Please enter a guess.", Toast.LENGTH_SHORT).show();
        } else {
            timer.cancel();
            getTelephone().guesses.add(guessInput);
            getTelephone().counter += 1;

            Intent intent;
            Log.d("TAG", "Counter = " + getTelephone().counter);
            if (getTelephone().counter > numRounds) {
                intent = new Intent(this, ResultsActivity.class);
            } else {
                intent = new Intent(this, DrawingActivity.class);
            }

            startActivity(intent);
            finish();
        }
    }

    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    public void onResume() {
        super.onResume();
        if (timeLeft || roundStart) {
            Log.d("DrawingActivity", "ONRESUME");
            timer = new CountDownTimer(getTelephone().secondsRemaining * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    getTelephone().secondsRemaining = (int) millisUntilFinished / 1000;
                    int newProgress = (int) ((((double) getTelephone().secondsRemaining) / countDownSeconds) * 100);
                    timer_button.setProgress(newProgress);
                    ColorUtils.blendHSL(redHSL, greenHSL,
                            ((float) getTelephone().secondsRemaining) / countDownSeconds, outHSL);

                    int newcolor = ColorUtils.HSLToColor(outHSL);
                    timer_button.setColor(newcolor);
                }

                @Override
                public void onFinish() {
                    guessButtonClicked();
                }
            };
            timer.start();
        }
    }

    public TelephoneCounter getTelephone() {
        return ((TelephoneCounter) getApplicationContext());
    }
}
