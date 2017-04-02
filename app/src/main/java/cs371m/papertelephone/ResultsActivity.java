package cs371m.papertelephone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageSwitcher;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Created by siuau_000 on 4/1/2017.
 */

public class ResultsActivity extends AppCompatActivity {
    private int numRounds;
    private int currentImageIndex;
    private ImageSwitcher imageSwitcher;
    private Button prevButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        currentImageIndex = 0;

/*        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        TextView initalText = new TextView(this);
        initalText.setText(getTelephone().guesses.get(0));
        linearLayout1.addView(initalText);
        for (int x = 0; x < numRounds; x++) {
            if (x % 2 == 0) {
                ImageView image = new ImageView(ResultsActivity.this);
                byte[] byteArray = getTelephone().pictures.get(x / 2);
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                image.setImageBitmap(bmp);
                linearLayout1.addView(image);
            } else {
                TextView text = new TextView(this);
                text.setText(getTelephone().guesses.get(x / 2 + 1));
                linearLayout1.addView(text);
            }
        }*/

        /**
         * Method based on instructions provided here:
         * https://www.tutorialspoint.com/android/android_imageswitcher.htm
         */
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);

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
//                    Toast.makeText(getApplicationContext(), "Previous Image",
//                            Toast.LENGTH_LONG).show();
                    byte[] byteArray = getTelephone().pictures.get(--currentImageIndex);
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImageIndex < getTelephone().pictures.size() - 1) {
//                    Toast.makeText(getApplicationContext(), "Next Image",
//                            Toast.LENGTH_LONG).show();
                    byte[] byteArray = getTelephone().pictures.get(++currentImageIndex);
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
                }
            }
        });

        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));

    }

    public TelephoneCounter getTelephone() {
        return ((TelephoneCounter) getApplicationContext());
    }

}
