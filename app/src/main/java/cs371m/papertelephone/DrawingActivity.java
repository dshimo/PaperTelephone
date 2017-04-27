package cs371m.papertelephone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import mbanje.kurt.fabbutton.FabButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.rd.PageIndicatorView;


import java.io.ByteArrayOutputStream;
import java.util.Random;

public class DrawingActivity extends AppCompatActivity implements ColorPickerDialogListener {
    public final String TAG = "DrawingActivity";

    public DrawingView dView;
    private static final int DIALOG_ID = 0;
    public static String word;
    private CountDownTimer timer;
    private TextView guessButton;
    private boolean isErase;
    private int oldPaintColor;
    private int oldBrushWidth;
    private int numRounds;
    private boolean roundStart;
    private int countDownSeconds;
    private boolean calledPause;
    private FloatingActionsMenu fabMenu;
    private FabButton timer_button;
    private float[] greenHSL, redHSL, outHSL;
    private PageIndicatorView pageIndicator;
    private TextView pageNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        greenHSL = new float[3];
        redHSL = new float[3];
        outHSL = new float[3];
        ColorUtils.colorToHSL(getResources().getColor(R.color.green), greenHSL);
        ColorUtils.colorToHSL(getResources().getColor(R.color.red), redHSL);
        dView = (DrawingView) findViewById(R.id.drawingcanvas);
        pageNumberView = (TextView) findViewById(R.id.page_number);
        guessButton = (TextView) findViewById(R.id.guessbutton);
        numRounds = MainActivity.rounds == 0 ? 3 : MainActivity.rounds;
        pageNumberView.setText(getString(R.string.page_number, getTelephone().counter, numRounds));
        pageIndicator = (PageIndicatorView) findViewById(R.id.page_indicator);
        pageIndicator.setCount(numRounds);
        pageIndicator.setSelection(getTelephone().counter - 1);
        pageIndicator.setViewPager(null);
        pageIndicator.setSelectedColor(Color.BLACK);
        pageIndicator.setUnselectedColor(Color.GRAY);
        if (numRounds > 8) {
            pageIndicator.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dView.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.page_number);
        } else
            pageNumberView.setVisibility(View.GONE);
        countDownSeconds = MainActivity.drawCountdown == 0 ? 60 : MainActivity.drawCountdown;
        getTelephone().secondsRemaining = countDownSeconds;
        calledPause = false;
        fabMenu = (FloatingActionsMenu) findViewById(R.id.drawing_buttons);
        final FloatingActionButton colorPicker = (FloatingActionButton) findViewById(R.id.color_picker_button);
        final FloatingActionButton brushSize = (FloatingActionButton) findViewById(R.id.brush_size_button);
        final FloatingActionButton erase = (FloatingActionButton) findViewById(R.id.erase_button);
        timer_button = (FabButton) findViewById(R.id.time_button);
        timer_button.setIcon(getResources().getDrawable(R.drawable.timer_off),getResources().getDrawable(R.drawable.timer_off));
        timer_button.bringToFront();
        timer_button.showProgress(true);
        if (getTelephone().counter == 1) {
            Random rand = new Random();
            String difficulty = MainActivity.difficulty == null ? "Easy Words" : MainActivity.difficulty;
            Log.d(TAG, "The difficulty is set to " + difficulty);

            String[] wordBank = {"Something has gone terribly wrong!"};
            switch (difficulty) {
                case "Easy Words":
                    wordBank = getResources().getStringArray(R.array.easywords);
                    word = wordBank[rand.nextInt(wordBank.length)];
                    break;
                case "Medium Words":
                    wordBank = getResources().getStringArray(R.array.mediumwords);
                    word = wordBank[rand.nextInt(wordBank.length)];
                    break;
                case "Hard Words":
                    wordBank = getResources().getStringArray(R.array.hardwords);
                    word = wordBank[rand.nextInt(wordBank.length)];
                    break;
                case "Choose your own!":
                    Log.d("DrawingActivity", "The word chosen is " + word);
                    break;
                default:
                    break;
            }

            guessButton.setText(getString(R.string.draw_button, word));
            getTelephone().guesses.add(word);
        } else {
            guessButton.setText(R.string.start_guessing);
            guessButton.setEnabled(true);
            dView.setTimeLeft(false);
            roundStart = true;
            timer_button.setIcon(getResources().getDrawable(R.drawable.check), getResources().getDrawable(R.drawable.check));
            timer_button.setColor(getResources().getColor(R.color.green));
            timer_button.showProgress(false);
            dView.setVisibility(View.GONE);
            pageIndicator.setVisibility(View.GONE);
            pageNumberView.setVisibility(View.GONE);
        }

        if (dView.getTimeLeft())
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
                            .setPresets(new int[]{Color.BLACK, Color.RED, getResources().getColor(R.color.orange),
                                    Color.YELLOW, Color.GREEN, Color.BLUE, getResources().getColor(R.color.indigo),
                                    getResources().getColor(R.color.violet), Color.WHITE})
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
                if (roundStart) {
                    roundStart = false;
                    timer_button.showProgress(true);
                    timer_button.setIcon(getResources().getDrawable(R.drawable.timer_off),getResources().getDrawable(R.drawable.timer_off));
                    timer.start();
                    dView.setTimeLeft(true);
                    fabMenu.setVisibility(View.VISIBLE);
                    dView.setVisibility(View.VISIBLE);
                    if (numRounds <= 8)
                        pageIndicator.setVisibility(View.VISIBLE);
                    else
                        pageNumberView.setVisibility(View.VISIBLE);
                    Log.d("DrawingActivity", "Counter = " + getTelephone().counter);
                    guessButton.setText(getString(R.string.draw_button, getTelephone().guesses.get(getTelephone().counter / 2)));
                } else if (dView.getTimeLeft()) {
                    timer_button.setIcon(getResources().getDrawable(R.drawable.check), getResources().getDrawable(R.drawable.check));
                    timer_button.setColor(getResources().getColor(R.color.green));
                    timer_button.showProgress(false);
                    timer.cancel();
                    guessButton.setText(R.string.start_guessing);
                    guessButton.setEnabled(true);
                    dView.setTimeLeft(false);
                    dView.setVisibility(View.GONE);
                    fabMenu.setVisibility(View.GONE);
                    pageIndicator.setVisibility(View.GONE);
                    pageNumberView.setVisibility(View.GONE);
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

    public void onPause() {
        super.onPause();
        calledPause = true;
        if (timer != null)
            timer.cancel();
    }

    public void onResume() {
        super.onResume();
        if (dView.getTimeLeft() || roundStart) {
            Log.d("DrawingActivity", "ONRESUME");
            timer = new CountDownTimer(getTelephone().secondsRemaining * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    getTelephone().secondsRemaining = (int) millisUntilFinished / 1000;
                    int newProgress = (int)((((double) getTelephone().secondsRemaining)/countDownSeconds)*100);
                    timer_button.setProgress(newProgress);
                    ColorUtils.blendHSL(redHSL, greenHSL,
                            ((float) getTelephone().secondsRemaining) / countDownSeconds, outHSL);

                    int newcolor = ColorUtils.HSLToColor(outHSL);
                    timer_button.setColor(newcolor);
                }

                @Override
                public void onFinish() {
                    timer_button.showProgress(false);
                    timer_button.setIcon(getResources().getDrawable(R.drawable.check),getResources().getDrawable(R.drawable.check));
                    timer_button.setColor(getResources().getColor(R.color.green));
                    guessButton.setText(R.string.start_guessing);
                    guessButton.setEnabled(true);
                    dView.setTimeLeft(false);
                    dView.setVisibility(View.GONE);
                    fabMenu.setVisibility(View.GONE);
                    pageIndicator.setVisibility(View.GONE);
                    pageNumberView.setVisibility(View.GONE);
                }
            };
            if ((getTelephone().counter == 1 || calledPause) && !roundStart)
                timer.start();
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
