package cs371m.papertelephone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

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
                if (currentImageIndex < getTelephone().pictures.size() - 1) {
                    ++currentImageIndex;
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
                if (currentImageIndex > getTelephone().pictures.size()) {
                    Toast.makeText(getApplicationContext(), "No image available to " +
                            "save for this round.", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(ResultsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            }
        });

        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
        updateGuess();
    }

    private String saveImageToStorage() {
        byte[] byteArray = getTelephone().pictures.get(currentImageIndex);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Save image to gallery
        return MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                "PaperTelephone Image", "PaperTelephone");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    String savedImageURL = saveImageToStorage();
                    Toast.makeText(getApplicationContext(), "Image saved successfully! Path: " +
                            savedImageURL, Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void updateGuess() {
        TextView guessDisplay = (TextView) findViewById(R.id.guess_display);
        guessDisplay.setText(getTelephone().guesses.get(currentImageIndex));
    }

    public TelephoneCounter getTelephone() {
        return ((TelephoneCounter) getApplicationContext());
    }

}
