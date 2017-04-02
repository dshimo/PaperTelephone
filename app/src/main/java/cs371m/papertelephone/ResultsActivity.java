package cs371m.papertelephone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by siuau_000 on 4/1/2017.
 */

public class ResultsActivity extends AppCompatActivity {
    private int numRounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
//        loadImageViews(numRounds);

        // test
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        for (int x = 0; x < numRounds; x++) {
            ImageView image = new ImageView(ResultsActivity.this);
            image = loadImageToView(image);

            linearLayout1.addView(image);
        }

    }

    private void loadImageViews(int numRounds) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout1);
        for (int i = 0; i < numRounds; i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80, 60));
            image.setMaxHeight(20);
            image.setMaxWidth(20);
            image = loadImageToView(image);

            layout.addView(image);

        }
    }

    private ImageView loadImageToView(View view) {
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");  // TODO change to multiple, currently repeating most recent
        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            ImageView image = (ImageView) view;
            image.setImageBitmap(bmp);

            return image;
        }
        return (ImageView) view;
    }

}
