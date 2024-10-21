package com.example.drawing;

import static com.example.drawing.R.id.clear_button;
import static com.example.drawing.R.id.color_picker_button;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Spinner brushSizeSpinner;
    private Button colorPickerButton, clearButton;
    private int currentColor = Color.BLACK;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout drawingFrame = findViewById(R.id.drawing_frame);
        drawingView = new DrawingView(this, null);
        drawingFrame.addView(drawingView);

        brushSizeSpinner = findViewById(R.id.brush_size_spinner);
        colorPickerButton = findViewById(color_picker_button);
        clearButton = findViewById(clear_button);

        // Set up the spinner for brush size selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.brush_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brushSizeSpinner.setAdapter(adapter);

        // Set the brush size based on the selected item
        brushSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSize = parent.getItemAtPosition(position).toString();
                switch (selectedSize) {
                    case "Small":
                        drawingView.setBrushSize(10);
                        break;
                    case "Medium":
                        drawingView.setBrushSize(30);
                        break;
                    case "Large":
                        drawingView.setBrushSize(50);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        colorPickerButton.setOnClickListener(v -> openColorPicker());
        clearButton.setOnClickListener(v -> drawingView.clearCanvas());

        setUpColorPalette();
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                drawingView.setColor(currentColor);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        colorPicker.show();
    }

    private void setUpColorPalette() {
        findViewById(R.id.colorBrown).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#8B4513")));
        findViewById(R.id.colorRed).setOnClickListener(v -> drawingView.setColor(Color.RED));
        findViewById(R.id.colorOrange).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#FFA500")));
        findViewById(R.id.colorYellow).setOnClickListener(v -> drawingView.setColor(Color.YELLOW));
        findViewById(R.id.colorGreen).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#008000")));
        findViewById(R.id.colorTeal).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#008080")));
        findViewById(R.id.colorBlue).setOnClickListener(v -> drawingView.setColor(Color.BLUE));
        findViewById(R.id.colorPurple).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#800080")));
        findViewById(R.id.colorPink).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#FF69B4")));
        findViewById(R.id.colorWhite).setOnClickListener(v -> drawingView.setColor(Color.WHITE));
        findViewById(R.id.colorGray).setOnClickListener(v -> drawingView.setColor(Color.GRAY));
        findViewById(R.id.colorBlack).setOnClickListener(v -> drawingView.setColor(Color.BLACK));
    }

    public class DrawingView extends View {

        private Path drawPath;
        private Paint drawPaint, canvasPaint;
        private Canvas drawCanvas;
        private Bitmap canvasBitmap;

        public DrawingView(MainActivity context, AttributeSet attrs) {
            super(context, attrs);
            setupDrawing();
        }

        private void setupDrawing() {
            drawPath = new Path();
            drawPaint = new Paint();
            drawPaint.setColor(currentColor);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(10);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
            canvasPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldW, int oldH) {
            super.onSizeChanged(w, h, oldW, oldH);
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }

        public void setColor(int newColor) {
            drawPaint.setColor(newColor);
        }

        public void setBrushSize(float newSize) {
            drawPaint.setStrokeWidth(newSize);
        }

        public void clearCanvas() {
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }
    }
}
