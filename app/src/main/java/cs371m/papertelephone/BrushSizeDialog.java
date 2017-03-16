package cs371m.papertelephone;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class BrushSizeDialog extends Dialog{

    private SeekBar sBar;
    private CircleView cView;
    private DrawingActivity act;
    private Button ok, cancel;
    private TextView brushText;

    public BrushSizeDialog(@NonNull Context context) {
        super(context);
        act = (DrawingActivity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.dialog_brush_size);
        sBar = (SeekBar) findViewById(R.id.brushSeekBar);
        cView = (CircleView) findViewById(R.id.circleview);
        brushText = (TextView) findViewById(R.id.brushseektext);
        ok = (Button) findViewById(R.id.okbutton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.dView.setBrushWidth(cView.getRadius()*2);
                dismiss();
            }
        });
        cancel = (Button) findViewById(R.id.cancelbutton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sBar.setProgress(0);
        sBar.setMax(150);
        sBar.setProgress(cView.getRadius()*2);
        brushText.setText(act.getString(R.string.brush_size_seek, cView.getRadius()*2));
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cView.setRadius(progress/2);
                brushText.setText(act.getString(R.string.brush_size_seek, cView.getRadius()*2));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

}
