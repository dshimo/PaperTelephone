package cs371m.papertelephone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageSwitcher;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ResultsActivity extends AppCompatActivity {
    private int numRounds;
    private int currentImageIndex;
    private ImageSwitcher imageSwitcher;
    private Button prevButton, nextButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        currentImageIndex = 0;

        /**
         * Method based on instructions provided here:
         * https://www.tutorialspoint.com/android/android_imageswitcher.htm
         */
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        byte[] byteArray = getTelephone().pictures.get(currentImageIndex);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImageIndex >= 1) {
                    --currentImageIndex;
                    imageSwitcher.setVisibility(View.VISIBLE);
//                   Toast.makeText(getApplicationContext(), "Round " + currentImageIndex + 1,
//                           Toast.LENGTH_LONG).show();
                    byte[] byteArray = getTelephone().pictures.get(currentImageIndex);
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
                    updateGuess();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Round " + currentImageIndex,
//                        Toast.LENGTH_SHORT).show();
                if (currentImageIndex < getTelephone().pictures.size() - 1) {
                    ++currentImageIndex;
//                   Toast.makeText(getApplicationContext(), "Round " + currentImageIndex + 1,
//                           Toast.LENGTH_LONG).show();
                    byte[] byteArray = getTelephone().pictures.get(currentImageIndex);
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
                    updateGuess();
                } else if (currentImageIndex < getTelephone().guesses.size() - 1) {
                    ++currentImageIndex;
                    imageSwitcher.setVisibility(View.GONE);
                    updateGuess();
                }

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                byte[] byteArray = getTelephone().pictures.get(currentImageIndex);
//                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "" + (Math.random() * 1001), "");
            }
        });

        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
        updateGuess();
    }

    private void updateGuess() {
        TextView guessDisplay = (TextView) findViewById(R.id.guess_display);
        guessDisplay.setText(getTelephone().guesses.get(currentImageIndex));
    }

    public TelephoneCounter getTelephone() {
        return ((TelephoneCounter) getApplicationContext());
    }

}
