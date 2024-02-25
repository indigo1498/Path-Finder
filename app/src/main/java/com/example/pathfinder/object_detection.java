package com.example.pathfinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.pathfinder.ml.SsdMobilenetV11Metadata1;

import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class object_detection extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;


    private Set<String> spokenClasses = new HashSet<>();

    private List<String> labels;
    private List<Integer> colors;
    private Paint paint;
    private ImageProcessor imageProcessor;
    private Bitmap bitmap;
    private ImageView imageView;
    private CameraDevice cameraDevice;
    private Handler handler;
    private CameraManager cameraManager;
    private TextureView textureView;
    private SsdMobilenetV11Metadata1 model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);
        getPermission();
        HashSet<String> spokenClasses = new HashSet<>();

        // Initialize Text-to-Speech engine
        textToSpeech = new TextToSpeech(this, this);
        try {
            labels = FileUtil.loadLabels(this, "labels.txt");

            imageProcessor = new ImageProcessor.Builder().add(new ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build();

            model = SsdMobilenetV11Metadata1.newInstance(this);
            HandlerThread handlerThread = new HandlerThread("videoThread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());

            imageView = findViewById(R.id.imageView);

            textureView = findViewById(R.id.textureView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                bitmap = textureView.getBitmap();
                TensorImage image = TensorImage.fromBitmap(bitmap);
                image = imageProcessor.process(image);

                SsdMobilenetV11Metadata1.Outputs outputs = model.process(image);
                float[] locations = outputs.getLocationsAsTensorBuffer().getFloatArray();
                float[] classes = outputs.getClassesAsTensorBuffer().getFloatArray();
                float[] scores = outputs.getScoresAsTensorBuffer().getFloatArray();

                Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutable);

                int h = mutable.getHeight();
                int w = mutable.getWidth();
                paint.setTextSize(h / 15f);
                paint.setStrokeWidth(h / 85f);
                int x;
                for (int index = 0; index < scores.length; index++) {
                    x = index * 4;
                    if (scores[index] > 0.6) {
                        paint.setColor(colors.get(index));
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(new RectF(locations[x + 1] * w, locations[x] * h, locations[x + 3] * w, locations[x + 2] * h), paint);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawText(labels.get((int) classes[index]) + " " + scores[index], locations[x + 1] * w, locations[x] * h, paint);
                        speakDetectedClass(labels.get((int) classes[index]) + " ");
                    }
                }

                imageView.setImageBitmap(mutable);
            }
        });

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.CYAN);
        colors.add(Color.GRAY);
        colors.add(Color.BLACK);
        colors.add(Color.DKGRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);

        paint = new Paint();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            model.close();
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            cameraManager.openCamera(cameraManager.getCameraIdList()[0], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                    Surface surface = new Surface(surfaceTexture);
                    try {
                        CaptureRequest.Builder captureRequestBuilder;
                        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        captureRequestBuilder.addTarget(surface);
                        cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                try {
                                    CaptureRequest captureRequest = captureRequestBuilder.build();
                                    session.setRepeatingRequest(captureRequest, null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            }
                        }, handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                }
            }, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS engine initialized successfully
        } else {
            // Initialization failed
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }



    // Method to speak the detected class label
    private void speakDetectedClass(String label) {
        // Check if the label has not been spoken recently
        if (!spokenClasses.contains(label)) {
            // Add the label to the spoken classes set
            spokenClasses.add(label);

            // Speak the label
            if (textToSpeech != null) {
                textToSpeech.speak(label, TextToSpeech.QUEUE_FLUSH, null, "labelUtteranceId");
            }

            // Post a delayed runnable to remove the label from the spoken classes set after 5 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    spokenClasses.remove(label);
                }
            }, 3000);
        }
    }

    }
