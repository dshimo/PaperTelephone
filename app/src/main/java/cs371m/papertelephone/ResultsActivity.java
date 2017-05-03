package cs371m.papertelephone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        currentImageIndex = 0;

        /**
         * Method based on instructions provided here:
         * https://www.tutorialspoint.com/android/android_imageswitcher.htm
         */
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        prevButton.setEnabled(false);
        if (MainActivity.rounds <= 2)
            nextButton.setEnabled(false);
        Log.d("numRounds", String.valueOf(MainActivity.rounds));

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
                nextButton.setEnabled(true);
                saveButton.setEnabled(true);
                if (currentImageIndex >= 1) {
                    --currentImageIndex;
                    imageSwitcher.setVisibility(View.VISIBLE);
                    byte[] byteArray = getTelephone().pictures.get(currentImageIndex);
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
                    updateGuess();
                }
                if (currentImageIndex == 0) {
                    prevButton.setEnabled(false);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevButton.setEnabled(true);
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
                // disable button if currently viewing last image
                if ((getTelephone().pictures.size() > getTelephone().guesses.size() && currentImageIndex == getTelephone().pictures.size() - 1) || currentImageIndex == getTelephone().guesses.size() - 1) {
                    nextButton.setEnabled(false);
                }
                // manage disabling save button if no image to save
                if (currentImageIndex >= getTelephone().pictures.size()) {
                    saveButton.setEnabled(false);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImageIndex >= getTelephone().pictures.size()) {
                    Toast.makeText(getApplicationContext(), "No image available to " +
                            "save for this round.", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(ResultsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
//                saveButton.setEnabled(false);
            }
        });

        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), bmp));
        updateGuess();
    }

    public void returnToMainActivity(View v) {
        finish();
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

    public void startNewGame(View view) {
        final Intent intent = new Intent(this, DrawingActivity.class);
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        Log.d("sharedPref", MainActivity.difficulty);
        if (MainActivity.difficulty.equals("Choose your own!")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Write your prompt");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.requestFocus();
            //the snippet doesn't automatically bring up keyboard unfortunately
/*            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);*/
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DrawingActivity.word = input.getText().toString();
                    if (!DrawingActivity.word.equals("")) {
                        ((TelephoneCounter) getApplicationContext()).counter = 1;
                        ((TelephoneCounter) getApplicationContext()).guesses.clear();
                        ((TelephoneCounter) getApplicationContext()).pictures.clear();
                        startActivity(intent);
                        finish();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "You didn't write anything!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            ((TelephoneCounter) getApplicationContext()).counter = 1;
            ((TelephoneCounter) getApplicationContext()).guesses.clear();
            ((TelephoneCounter) getApplicationContext()).pictures.clear();
            startActivity(intent);
            finish();
        }
    }
}
