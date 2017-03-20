package cs371m.papertelephone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.InputStream;

public class GuessingActivity extends AppCompatActivity {
    private EditText mGuessEditTextView;
    private Button mGuessSubmitButton;
    private ImageView mGuessImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessing);

        mGuessEditTextView = (EditText) findViewById(R.id.guess_input);
        mGuessSubmitButton = (Button) findViewById(R.id.guess_button);
        mGuessImageView = (ImageView) findViewById(R.id.guess_image_view);

        loadImage();


    }

    private void loadImage() {
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.guess_image_view);

        image.setImageBitmap(bmp);
    }
}
