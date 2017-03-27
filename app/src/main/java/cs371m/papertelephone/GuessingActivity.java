package cs371m.papertelephone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class GuessingActivity extends AppCompatActivity {
    private EditText mGuessEditTextView;
    private Button mGuessSubmitButton;
    private ImageView mGuessImageView;
    private CountDownTimer timer;
    private TextView mTimerTextView;
    private int rounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessing);

        mTimerTextView = (TextView) findViewById(R.id.timerText);
        mGuessEditTextView = (EditText) findViewById(R.id.guess_input);
        mGuessEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    mGuessEditTextView.clearFocus();
                    mGuessSubmitButton.requestFocus();
//                    guessButtonClicked(null);
                }
                return false;
            }
        });
        mGuessSubmitButton = (Button) findViewById(R.id.guess_button);
        mGuessImageView = (ImageView) findViewById(R.id.guess_image_view);

        rounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;

        loadImage();
        loadTimer();
    }

    private void loadImage() {
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.guess_image_view);

        image.setImageBitmap(bmp);
    }

    public void guessButtonClicked(View view) {
        String guessInput = mGuessEditTextView.getText().toString();
        getTelephone().guess = guessInput;
        getTelephone().counter += 1;
        if (getTelephone().counter > rounds) {
            // call up end-game activity (gallery?)
        } else {
            Intent intent = new Intent(this, DrawingActivity.class);
//                intent.putExtra("pictures", this.getIntent().getExtras().getIntArray("pictures"));    // pass around collection of pictures instead TODO
            intent.putExtra("picture", getIntent().getExtras().getByteArray("picture"));
            if (getIntent().getExtras().getStringArrayList("guesses") != null) {
                intent.putExtra("guesses", getIntent().getExtras().getStringArrayList("guesses").add(guessInput));
            }
            startActivity(intent);
            finish();
        }
    }

    private void loadTimer() {
        int countDownSeconds;

        if (MainActivity.guessCountdown == 0) {
            countDownSeconds = 15;
        } else {
            countDownSeconds = MainActivity.guessCountdown;
        }

        timer = new CountDownTimer(countDownSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(getString(R.string._60, (int) (millisUntilFinished / 1000)));
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
