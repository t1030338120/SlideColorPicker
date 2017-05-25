package tang.com.slide.colorpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import tang.slide.colorpicker.HorizontalSlideColorPicker;
import tang.slide.colorpicker.OnColorChangeListener;
import tang.slide.colorpicker.VerticalSlideColorPicker;

public class MainActivity extends AppCompatActivity {

    private VerticalSlideColorPicker vertSlideColorpicker;
    private android.widget.TextView text;
    private HorizontalSlideColorPicker horSlideColorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.text = (TextView) findViewById(R.id.text);
        vertSlideColorpicker = (VerticalSlideColorPicker) findViewById(R.id.color_picker);
        horSlideColorPicker = (HorizontalSlideColorPicker) findViewById(R.id.color_picker1);

        OnColorChangeListener onColorChangeListener = new OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                text.setBackgroundColor(selectedColor);
            }
        };
        vertSlideColorpicker.setOnColorChangeListener(onColorChangeListener);
        horSlideColorPicker.setOnColorChangeListener(onColorChangeListener);
    }



}
