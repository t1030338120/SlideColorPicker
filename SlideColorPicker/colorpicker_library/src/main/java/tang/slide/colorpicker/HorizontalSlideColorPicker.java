package tang.slide.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 水平滑动的colorPicker
 */
public class HorizontalSlideColorPicker extends View {

	private Paint paint;
	private Paint trackerPaint;
	private Bitmap bitmap;
	private int viewWidth;
	private int viewHeight;
	private OnColorChangeListener onColorChangeListener;
	private RectF colorPickerRectF;
	private float selectorXPos;
	private float trackerOffsetX; //滚动球和颜色框的宽度察觉
	private int[] colors;
	private boolean cacheBitmap = true;
	private int selectedColor = 0xFFFFFFFF;
	private int trackerRadius; //滚动指示点的半径
	private int defaltMinTrackerOffsetX ;
	private int defaltMinWidth ;
	private int defaltMinHeight;

	public HorizontalSlideColorPicker(Context context) {
		super(context);
		init();
	}

	public HorizontalSlideColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalSlideColorPicker, 0, 0);
		try {
			selectedColor = a.getColor(R.styleable.VerticalSlideColorPicker_trackerDefColor, Color.RED);
			trackerOffsetX = a.getDimension(R.styleable.VerticalSlideColorPicker_trackerOffset, defaltMinTrackerOffsetX);
			int colorsResourceId = a.getResourceId(R.styleable.VerticalSlideColorPicker_colors, R.array.default_colors);
			colors = a.getResources().getIntArray(colorsResourceId);
		} finally {
			a.recycle();
		}

	}

	public HorizontalSlideColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}



	private void init() {
		setWillNotDraw(false);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		trackerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		trackerPaint.setStyle(Paint.Style.FILL);
		trackerPaint.setColor(selectedColor);


		defaltMinTrackerOffsetX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		defaltMinWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		defaltMinHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());

		setDrawingCacheEnabled(true);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
		int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);

		if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
			setMeasuredDimension(defaltMinWidth, defaltMinHeight);
		}else if(widthMode == MeasureSpec.AT_MOST ){
			setMeasuredDimension(defaltMinWidth, heightAllowed);
		}else if (heightMode == MeasureSpec.AT_MOST){
			setMeasuredDimension(widthAllowed, defaltMinHeight);
		}else {
			setMeasuredDimension(widthAllowed, heightAllowed);
		}
	}



	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		viewHeight = h;

		trackerOffsetX = Math.min(viewHeight/3, trackerOffsetX);//限制最大值
		trackerRadius = viewHeight/2;
		colorPickerRectF = new RectF(trackerOffsetX, trackerOffsetX, viewWidth-trackerOffsetX, viewHeight - trackerOffsetX);

		LinearGradient gradient = new LinearGradient(colorPickerRectF.right, 0, 0, colorPickerRectF.left, colors, null, Shader.TileMode.CLAMP);
		paint.setShader(gradient);
		resetToDefault();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRoundRect(colorPickerRectF, viewWidth/2, viewWidth/2, paint);

		if (cacheBitmap) {
			bitmap = getDrawingCache();
			cacheBitmap = false;
			invalidate();
		} else {
			int rx = (int) Math.min(selectorXPos, viewWidth-trackerRadius);
			rx = (int) Math.max(rx, trackerRadius);
			trackerPaint.setColor(selectedColor);
			canvas.drawCircle(rx, trackerRadius, trackerRadius, trackerPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float xPos = Math.min(event.getX(), colorPickerRectF.right-1);
		xPos = Math.max(colorPickerRectF.left, xPos);

		selectorXPos = xPos;
		selectedColor = getColorByPixel();

		if (onColorChangeListener != null) {
			onColorChangeListener.onColorChange(selectedColor);
		}

		invalidate();

		return true;
	}



	//获取颜色值
	private int getColorByPixel() {
		int currColor = bitmap.getPixel((int) selectorXPos, viewHeight/2);

		if(Color.alpha(currColor) < 128){
			float[] hsv = new float[3];
			Color.colorToHSV(currColor, hsv);
			currColor = Color.HSVToColor(128, new float[]{hsv[0], hsv[1], hsv[2]});
		}

		return currColor;
	}

	public void setTrackerOffsetX(float trackerOffsetX) {
		this.trackerOffsetX = trackerOffsetX;
		invalidate();
	}

	public void setColors(int[] colors) {
		this.colors = colors;
		cacheBitmap = true;
		invalidate();
	}

	public void resetToDefault() {
		selectorXPos = trackerOffsetX;

		if (onColorChangeListener != null) {
			onColorChangeListener.onColorChange(Color.TRANSPARENT);
		}

		invalidate();
	}

	public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
		this.onColorChangeListener = onColorChangeListener;
	}

}
