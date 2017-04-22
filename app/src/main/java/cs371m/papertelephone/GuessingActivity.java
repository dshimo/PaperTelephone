package cs371m.papertelephone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.rd.PageIndicatorView;

public class GuessingActivity extends AppCompatActivity {
    private EditText mGuessEditTextView;
    private Button mGuessSubmitButton;
    private CountDownTimer timer;
    private TextView mTimerTextView;
    private int numRounds;
    private PageIndicatorView pageIndicator;
    private TextView pageNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessing);

        mTimerTextView = (TextView) findViewById(R.id.timerText);
        mGuessEditTextView = (EditText) findViewById(R.id.guess_input);
        mGuessEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mGuessEditTextView.clearFocus();
                    mGuessSubmitButton.requestFocus();
                }
                return false;
            }
        });
        mGuessSubmitButton = (Button) findViewById(R.id.guess_button);

        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        pageIndicator = (PageIndicatorView) findViewById(R.id.guess_page_indicator);
        pageIndicator.setCount(numRounds);
        pageIndicator.setSelection(getTelephone().counter-1);
        pageIndicator.setViewPager(null);
        pageIndicator.setSelectedColor(Color.BLACK);
        pageIndicator.setUnselectedColor(Color.GRAY);
        pageNumberView = (TextView) findViewById(R.id.guess_page_number);
        pageNumberView.setText(getString(R.string.page_number,getTelephone().counter,numRounds));
        int countDownSeconds = MainActivity.guessCountdown == 0 ? 15 : MainActivity.guessCountdown;
        getTelephone().secondsRemaining = countDownSeconds;
        loadImage();
    }

    private void loadImage() {
        byte[] byteArray = getTelephone().pictures.get(getTelephone().pictures.size() - 1);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.guess_image_view);

        image.setImageBitmap(bmp);
        if(numRounds > 8) {
            pageIndicator.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.guess_page_number);
        } else
            pageNumberView.setVisibility(View.GONE);
    }

    public void guessButtonClicked(View view) {
        timer.cancel();
        String guessInput = mGuessEditTextView.getText().toString();
        getTelephone().guesses.add(guessInput);
        getTelephone().counter += 1;

        Intent intent;
        Log.d("TAG", "Counter = " + getTelephone().counter);
        if (getTelephone().counter == 4)
            Log.d("STACKTRACE", Log.getStackTraceString(new Exception()));
        if (getTelephone().counter > numRounds) {
            intent = new Intent(this, ResultsActivity.class);
        } else {
            intent = new Intent(this, DrawingActivity.class);
        }

        startActivity(intent);
        finish();
    }

    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void onResume() {
        super.onResume();
        timer = new CountDownTimer(getTelephone().secondsRemaining * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(getString(R.string._60, (int) (millisUntilFinished / 1000)));
                getTelephone().secondsRemaining = (int) millisUntilFinished/1000;
            }

            @Override
            public void onFinish() {
                mTimerTextView.setText(getString(R.string._60, 0));
                guessButtonClicked(null);
            }
        }.start();
    }

    public TelephoneCounter getTelephone() {
        return ((TelephoneCounter) getApplicationContext());
    }
}
