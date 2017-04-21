package cs371m.papertelephone;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class DrawingActivity extends AppCompatActivity implements ColorPickerDialogListener {

    public DrawingView dView;
    private static final int DIALOG_ID = 0;
    private CountDownTimer timer;
    private TextView tView;
    private TextView guessButton;
    private boolean isErase;
    private int oldPaintColor;
    private int oldBrushWidth;
    private int numRounds;
    private boolean roundStart;
    private int countDownSeconds;
    private boolean calledPause;
    private FloatingActionsMenu fabMenu;
    private com.github.clans.fab.FloatingActionButton timer_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        dView = (DrawingView) findViewById(R.id.drawingcanvas);
        tView = (TextView) findViewById(R.id.timerText);
        guessButton = (TextView) findViewById(R.id.guessbutton);
        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        countDownSeconds = MainActivity.drawCountdown == 0 ? 60 : MainActivity.drawCountdown;
        getTelephone().secondsRemaining = countDownSeconds;
        calledPause = false;
        fabMenu = (FloatingActionsMenu) findViewById(R.id.drawing_buttons);
        final FloatingActionButton colorPicker = (FloatingActionButton) findViewById(R.id.color_picker_button);
        final FloatingActionButton brushSize = (FloatingActionButton) findViewById(R.id.brush_size_button);
        final FloatingActionButton erase = (FloatingActionButton) findViewById(R.id.erase_button);
        timer_button = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.time_button);
        if (getTelephone().counter == 1) {
            Random rand = new Random();
            String[] easy = getResources().getStringArray(R.array.easywords);
            String[] medium = getResources().getStringArray(R.array.mediumwords);
            String[] hard = getResources().getStringArray(R.array.hardwords);
            int index = rand.nextInt(easy.length + medium.length + hard.length);
            String word;
            roundStart = false;
            if (index < easy.length)
                word = easy[index];
            else if (index < (easy.length + medium.length))
                word = medium[index - easy.length];
            else
                word = hard[index - easy.length - medium.length];
            guessButton.setText(getString(R.string.draw_button, word));
            getTelephone().guesses.add(word);
        } else {
            guessButton.setText(R.string.start_guessing);
            guessButton.setEnabled(true);
            dView.setTimeLeft(false);
            roundStart = true;
            tView.setText(getString(R.string._60, countDownSeconds));
            timer_button.setImageResource(R.drawable.check);
            timer_button.setColorNormalResId(R.color.green);
            timer_button.setLabelText("Press to Continue");
            timer_button.setShowProgressBackground(false);
            timer_button.hideProgress();
        }
        timer_button.setMax(countDownSeconds);

        if(dView.getTimeLeft())
            fabMenu.setVisibility(View.VISIBLE);
        else
            fabMenu.setVisibility(View.GONE);
        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.colorOn)
                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                            .setAllowPresets(true)
                            .setAllowCustom(true)
                            .setShowColorShades(true)
                            .setDialogId(DIALOG_ID)
                            .setColor(dView.getPaintColor())
                            .setShowAlphaSlider(true)
                            .setPresets(new int[]{Color.BLACK,Color.RED,getResources().getColor(R.color.orange),Color.YELLOW,
                                        Color.GREEN, Color.BLUE,getResources().getColor(R.color.indigo),getResources().getColor(R.color.violet),
                                        Color.WHITE})
                            .show(DrawingActivity.this);
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
                            .show(DrawingActivity.this);
                fabMenu.collapse();
            }
        });
        brushSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrushSizeDialog bSize = new BrushSizeDialog(DrawingActivity.this);
                bSize.setTitle(R.string.brush_size);
                bSize.show();
                fabMenu.collapse();

            }
        });
        isErase = false;
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isErase) {
                    oldPaintColor = dView.getPaintColor();
                    oldBrushWidth = dView.getBrushWidth();
                    dView.setPaintColor(Color.WHITE);
                    dView.setBrushWidth(130);
                    isErase = true;
                    colorPicker.setVisibility(View.GONE);
                    erase.setIcon(R.drawable.brush_icon);
                    erase.setTitle(getString(R.string.brush));
                    brushSize.setTitle(getString(R.string.eraser_size));
                } else {
                    dView.setPaintColor(oldPaintColor);
                    dView.setBrushWidth(oldBrushWidth);
                    isErase = false;
                    colorPicker.setVisibility(View.VISIBLE);
                    erase.setIcon(R.drawable.eraser);
                    erase.setTitle(getString(R.string.erase));
                    brushSize.setTitle(getString(R.string.brush_size));
                }
                fabMenu.collapse();
            }
        });
        timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roundStart) {
                    roundStart = false;
                    timer_button.setShowProgressBackground(true);
                    timer_button.setColorNormalResId(R.color.red);
                    timer_button.setImageResource(R.drawable.timer_off);
                    timer.start();
                    dView.setTimeLeft(true);
                    fabMenu.setVisibility(View.VISIBLE);
                    Log.d("DrawingActivity", "Counter = " + getTelephone().counter);
                    guessButton.setText(getString(R.string.draw_button,getTelephone().guesses.get(getTelephone().counter/2)));
                }
                else if(dView.getTimeLeft()) {
                    timer_button.setImageResource(R.drawable.check);
                    timer_button.setColorNormalResId(R.color.green);
                    timer_button.setLabelText("Press to Continue");
                    timer_button.hideProgress();
                    timer.cancel();
                    guessButton.setText(R.string.start_guessing);
                    guessButton.setEnabled(true);
                    dView.setTimeLeft(false);
                    dView.setVisibility(View.GONE);
                    fabMenu.setVisibility(View.GONE);
                } else {
                    getTelephone().counter += 1;
                    Log.d("DrawingActivity", "False,Counter = " + getTelephone().counter);

                    // convert bitmap into byte array
                    dView.setDrawingCacheEnabled(true);
                    Bitmap bmp = dView.getDrawingCache();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    getTelephone().pictures.add(byteArray);
                    Intent intent;
                    if (getTelephone().counter > numRounds) {
                        intent = new Intent(DrawingActivity.this, ResultsActivity.class);
                    } else {
                        intent = new Intent(DrawingActivity.this, GuessingActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.drawing_bar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.colorpicker).setEnabled(dView.getTimeLeft());
//        menu.findItem(R.id.brushsize).setEnabled(dView.getTimeLeft());
//        menu.findItem(R.id.stopdrawing).setEnabled(dView.getTimeLeft());
//        if (isErase)
//            menu.findItem(R.id.erase).setIcon(R.drawable.nonerase);
//        else
//            menu.findItem(R.id.erase).setIcon(R.drawable.eraser);
//        if (isErase && dView.getTimeLeft())
//            menu.findItem(R.id.colorpicker).setEnabled(false);
//        menu.findItem(R.id.erase).setEnabled(dView.getTimeLeft());
//        return true;
//    }

    public void onPause() {
        super.onPause();
        calledPause = true;
        if(timer != null)
            timer.cancel();
    }

    public void onResume() {
        super.onResume();
        if(dView.getTimeLeft() || roundStart) {
            Log.d("DrawingActivity", "ONRESUME");
            timer = new CountDownTimer(getTelephone().secondsRemaining * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tView.setText(getString(R.string._60, (int) (millisUntilFinished / 1000)));
                    getTelephone().secondsRemaining = (int) millisUntilFinished / 1000;
                    timer_button.setProgress(getTelephone().secondsRemaining, true);
                }

                @Override
                public void onFinish() {
                    tView.setText(getString(R.string._60, 0));
                    dView.setTimeLeft(false);
                    fabMenu.setVisibility(View.GONE);
                    timer_button.hideProgress();
                    timer_button.setImageResource(R.drawable.check);
                    timer_button.setColorNormalResId(R.color.green);
                    guessButton.setText(R.string.start_guessing);
                    dView.setVisibility(View.GONE);
                    guessButton.setEnabled(true);

                }
            };
            if ((getTelephone().counter == 1 || calledPause) && !roundStart)
                timer.start();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        FragmentManager fm = getFragmentManager();
//        switch (item.getItemId()) {
//            case R.id.colorpicker:
//                if (MainActivity.colorOn)
//                    ColorPickerDialog.newBuilder()
//                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
//                            .setAllowPresets(false)
//                            .setDialogId(DIALOG_ID)
//                            .setColor(dView.getPaintColor())
//                            .setShowAlphaSlider(true)
//                            .show(this);
//                else
//                    ColorPickerDialog.newBuilder()
//                            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
//                            .setAllowPresets(false)
//                            .setAllowCustom(false)
//                            .setDialogId(DIALOG_ID)
//                            .setColor(dView.getPaintColor())
//                            .setShowAlphaSlider(true)
//                            .setPresets(new int[]{Color.BLACK, Color.GRAY, Color.WHITE})
//                            .setShowColorShades(true)
//                            .show(this);
//                return true;
//            case R.id.brushsize:
//                BrushSizeDialog bSize = new BrushSizeDialog(this);
//                bSize.setTitle(R.string.brush_size);
//                bSize.show();
//                return true;
//            case R.id.stopdrawing:
//                timer.cancel();
//                if (getTelephone().counter == numRounds)
//                    guessButton.setText(R.string.show_results);
//                else
//                    guessButton.setText(R.string.start_guessing);
//                guessButton.setEnabled(true);
//                dView.setTimeLeft(false);
//                dView.setVisibility(View.GONE);
//                invalidateOptionsMenu();
//                return true;
//            case R.id.erase:
//                if (!isErase) {
//                    oldPaintColor = dView.getPaintColor();
//                    oldBrushWidth = dView.getBrushWidth();
//                    dView.setPaintColor(Color.WHITE);
//                    dView.setBrushWidth(130);
//                    isErase = true;
//                    invalidateOptionsMenu();
//                } else {
//                    dView.setPaintColor(oldPaintColor);
//                    dView.setBrushWidth(oldBrushWidth);
//                    isErase = false;
//                    invalidateOptionsMenu();
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

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
