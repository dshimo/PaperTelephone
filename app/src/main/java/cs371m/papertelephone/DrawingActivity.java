package cs371m.papertelephone;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

public class DrawingActivity extends AppCompatActivity implements ColorPickerDialogListener{

    private DrawingView dView;
    private static final int DIALOG_ID = 0;
    private CountDownTimer timer;
    private TextView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        dView = (DrawingView) findViewById(R.id.drawingcanvas);
        tView = (TextView) findViewById(R.id.timerText);
        int countDownSeconds;
        if(MainActivity.drawCountdown == 0)
            countDownSeconds = 60;
        else
            countDownSeconds = MainActivity.drawCountdown;
        timer = new CountDownTimer(countDownSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tView.setText(getString(R.string._60, (int)(millisUntilFinished/1000)));
            }

            @Override
            public void onFinish() {
                tView.setText(getString(R.string._60, 0));
                dView.setTimeLeft(false);
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawing_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.colorpicker:
                if(MainActivity.colorOn)
                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                            .setAllowPresets(false)
                            .setDialogId(DIALOG_ID)
                            .setColor(Color.BLACK)
                            .setShowAlphaSlider(true)
                            .show(this);
                else
                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                            .setAllowPresets(false)
                            .setAllowCustom(false)
                            .setDialogId(DIALOG_ID)
                            .setColor(Color.BLACK)
                            .setShowAlphaSlider(true)
                            .setPresets(new int[]{Color.BLACK, Color.GRAY, Color.WHITE})
                            .setShowColorShades(true)
                            .show(this);
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
}
