package cs371m.papertelephone;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class DrawingActivity extends AppCompatActivity implements ColorPickerDialogListener {

    public DrawingView dView;
    private static final int DIALOG_ID = 0;
    private CountDownTimer timer;
    private TextView tView;
    private Button guessButton;
    private boolean isErase;
    private int oldPaintColor;
    private int oldBrushWidth;
    private int numRounds;
    private boolean calledPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        dView = (DrawingView) findViewById(R.id.drawingcanvas);
        tView = (TextView) findViewById(R.id.timerText);
        guessButton = (Button) findViewById(R.id.guessbutton);
        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        int countDownSeconds = MainActivity.drawCountdown == 0 ? 60 : MainActivity.drawCountdown;
        getTelephone().secondsRemaining = countDownSeconds;
        calledPause = false;
        if (getTelephone().counter == 1) {
            Random rand = new Random();
            String[] easy = getResources().getStringArray(R.array.easywords);
            String[] medium = getResources().getStringArray(R.array.mediumwords);
            String[] hard = getResources().getStringArray(R.array.hardwords);
            int index = rand.nextInt(easy.length + medium.length + hard.length);
            String word;
            if (index < easy.length)
                word = easy[index];
            else if (index < (easy.length + medium.length))
                word = medium[index - easy.length];
            else
                word = hard[index - easy.length - medium.length];
            guessButton.setText(getString(R.string.draw_button, word));
            getTelephone().guesses.add(word);
        } else {
            guessButton.setText(R.string.start_drawing);
            guessButton.setEnabled(true);
            dView.setTimeLeft(false);
            tView.setText(getString(R.string._60, countDownSeconds));
        }

        isErase = false;
    }

    public void guessButtonClick(View view) {
        String str = String.valueOf(guessButton.getText());
        if (str.contains("Start Drawing")) {
            guessButton.setText(getString(R.string.draw_button,
                    getTelephone().guesses.get(getTelephone().guesses.size()-1)));
            guessButton.setEnabled(false);
            dView.setTimeLeft(true);
            invalidateOptionsMenu();
            timer.start();
        } else {
            getTelephone().counter += 1;


            // convert bitmap into byte array
            dView.setDrawingCacheEnabled(true);
            Bitmap bmp = dView.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            getTelephone().pictures.add(byteArray);
            Intent intent;
            if (getTelephone().counter > numRounds) {
                intent = new Intent(this, ResultsActivity.class);
            } else {
                intent = new Intent(this, GuessingActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawing_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.colorpicker).setEnabled(dView.getTimeLeft());
        menu.findItem(R.id.brushsize).setEnabled(dView.getTimeLeft());
        menu.findItem(R.id.stopdrawing).setEnabled(dView.getTimeLeft());
        if (isErase)
            menu.findItem(R.id.erase).setIcon(R.drawable.nonerase);
        else
            menu.findItem(R.id.erase).setIcon(R.drawable.erasure);
        if (isErase && dView.getTimeLeft())
            menu.findItem(R.id.colorpicker).setEnabled(false);
        menu.findItem(R.id.erase).setEnabled(dView.getTimeLeft());
        return true;
    }

    public void onPause() {
        super.onPause();
        calledPause = true;
        timer.cancel();
    }

    public void onResume() {
        super.onResume();
        timer = new CountDownTimer(getTelephone().secondsRemaining * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tView.setText(getString(R.string._60, (int) (millisUntilFinished / 1000)));
                getTelephone().secondsRemaining = (int) millisUntilFinished/1000;
            }

            @Override
            public void onFinish() {
                tView.setText(getString(R.string._60, 0));
                dView.setTimeLeft(false);
                if (getTelephone().counter == numRounds)
                    guessButton.setText(R.string.show_results);
                else
                    guessButton.setText(R.string.start_guessing);
                dView.setVisibility(View.GONE);
                guessButton.setEnabled(true);
                invalidateOptionsMenu();
            }
        };
        if (getTelephone().counter == 1 || calledPause)
            timer.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.colorpicker:
                if (MainActivity.colorOn)
                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                            .setAllowPresets(false)
                            .setDialogId(DIALOG_ID)
                            .setColor(dView.getPaintColor())
                            .setShowAlphaSlider(true)
                            .show(this);
                else
                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                            .setAllowPresets(false)
                            .setAllowCustom(false)
                            .setDialogId(DIALOG_ID)
                            .setColor(dView.getPaintColor())
                            .setShowAlphaSlider(true)
                            .setPresets(new int[]{Color.BLACK, Color.GRAY, Color.WHITE})
                            .setShowColorShades(true)
                            .show(this);
                return true;
            case R.id.brushsize:
                BrushSizeDialog bSize = new BrushSizeDialog(this);
                bSize.setTitle(R.string.brush_size);
                bSize.show();
                return true;
            case R.id.stopdrawing:
                timer.cancel();
                if (getTelephone().counter == numRounds)
                    guessButton.setText(R.string.show_results);
                else
                    guessButton.setText(R.string.start_guessing);
                guessButton.setEnabled(true);
                dView.setTimeLeft(false);
                dView.setVisibility(View.GONE);
                invalidateOptionsMenu();
                return true;
            case R.id.erase:
                if (!isErase) {
                    oldPaintColor = dView.getPaintColor();
                    oldBrushWidth = dView.getBrushWidth();
                    dView.setPaintColor(Color.WHITE);
                    dView.setBrushWidth(130);
                    isErase = true;
                    invalidateOptionsMenu();
                } else {
                    dView.setPaintColor(oldPaintColor);
                    dView.setBrushWidth(oldBrushWidth);
                    isErase = false;
                    invalidateOptionsMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        if (dialogId == DIALOG_ID)
            dView.setPaintColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public TelephoneCounter getTelephone() {
        return ((TelephoneCounter) getApplicationContext());
    }
}
