package com.example.tinni.ui.ratings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.BuildConfig;
import com.example.tinni.R;
import com.example.tinni.adapters.RatingsAdapter;
import com.example.tinni.databinding.ActivityRatingsBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Rating;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <h1>Program Activity</h1>
 * Everything related to the program ui
 *
 * Variables:
 *
 * Session currentSession: The currently opened session
 * FragmentManager fragmentManager: The FragmentManager instance to open BottomDialogQuestions
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   28.06.2020
 */

public class Ratings extends AppCompatActivity
{
    private RatingsViewModel viewModel;
    private ActivityRatingsBinding binding;
    private boolean ratingsLoaded = false;
    private RatingsAdapter ratingsAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Create</h2>
     * Connecting the Activity with the ViewModel
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RatingsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ratings);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewModel.getRatings().observe(this, l ->
        {
            if (l != null && l.size() > 0)
            {
                if (!ratingsLoaded)
                {
                    ratingsLoaded = true;
                    binding.ratings.setItemViewCacheSize(20);
                    binding.ratings.setDrawingCacheEnabled(true);
                    binding.ratings.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.ratings.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(this, getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    binding.ratings.setLayoutManager(layoutManager);
                }

                ratingsAdapter = new RatingsAdapter(l);
                binding.ratings.setAdapter(ratingsAdapter);
            }
        });

        binding.fab.setOnClickListener(v -> generatePdf());
    }

    /**
     * <h2>Generate PDF</h2>
     * Generates a pdf file of the ratings
     *
     * Source 1: https://stackoverflow.com/a/23392246/2700965
     * Source 2: https://stackoverflow.com/a/52789871/2700965
     * Source 3: UNITI app, "Shades of noise"
     * Source 4: https://medium.com/over-engineering/drawing-multiline-text-to-canvas-on-android-9b98f0bfa16a
     *
     */

    private void generatePdf()
    {
        List<Rating> list = new ArrayList<>(Constants.getInstance().ratings);
        Collections.reverse(list);
        if (list.size() > 0)
        {
            // Create document
            PdfDocument document = new PdfDocument();

            final File sharedFolder = new File(getFilesDir(), "ratings");
            boolean isCreated = sharedFolder.exists() || sharedFolder.mkdirs();

            if (isCreated)
            {
                try
                {
                    int pageWidth = 595;
                    int pageHeight = 842;
                    int pageNumber = 1;
                    int y = 100;
                    final File file = File.createTempFile("ratings" + (int) (System.currentTimeMillis()) / 1000, ".pdf", sharedFolder);
                    FileOutputStream fOut = new FileOutputStream(file);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    Paint titlePaint = new Paint();
                    Paint greenPaint = new Paint();
                    Paint redPaint = new Paint();
                    greenPaint.setColor(Color.GREEN);
                    redPaint.setColor(Color.RED);
                    titlePaint.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                    canvas.drawText(getResources().getString(R.string.daily_tinnitus_progress), 10, 20, paint);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
                    String date = formatter.format(new Date(System.currentTimeMillis()));

                    canvas.drawText(date, 10, 40, paint);

                    canvas.drawText(getResources().getString(R.string.summary), 10, 70, titlePaint);

                    for (Rating r : list)
                    {
                        if (y > pageHeight)
                        {
                            pageNumber ++;
                            document.finishPage(page);
                            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                            page = document.startPage(pageInfo);
                            canvas = page.getCanvas();

                            canvas.drawText(String.format(getResources().getString(R.string.daily_tinnitus_progress_page), pageNumber), 10, 20, paint);
                            canvas.drawText(date, 10, 40, paint);

                            canvas.drawText(getResources().getString(R.string.summary), 10, 70, titlePaint);
                            y = 100;
                        }

                        canvas.drawText(formatter.format(new Date(r.getDate())), 20, y, titlePaint);
                        canvas.drawLine(20f, (float)y + 5 , 575f, (float)y + 5, titlePaint);
                        y += 30;
                        String ratingText;
                        Paint ratingPaint;
                        switch (r.getRating())
                        {
                            case 5:
                                ratingText = getResources().getString(R.string.very_good);
                                ratingPaint = greenPaint;
                                break;
                            case 4:
                                ratingText = getResources().getString(R.string.good);
                                ratingPaint = greenPaint;
                                break;
                            case 2:
                                ratingText = getResources().getString(R.string.bad);
                                ratingPaint = redPaint;
                                break;
                            case 1:
                                ratingText = getResources().getString(R.string.miserable);
                                ratingPaint = redPaint;
                                break;
                            default:
                                ratingText = getResources().getString(R.string.neutral);
                                ratingPaint = paint;
                                break;
                        }
                        canvas.drawText(ratingText, 20, y, ratingPaint);
                        y += 30;
                        if (r.getText() != null && r.getText().length() > 0)
                        {
                            y -= 15;
                            TextPaint textPaint = new TextPaint();
                            textPaint.setAntiAlias(true);
                            textPaint.setColor(Color.BLACK);

                            StaticLayout staticLayout = StaticLayout.Builder.obtain(r.getText(), 0, r.getText().length(), textPaint, 575).build();
                            canvas.save();
                            canvas.translate(20, y);
                            staticLayout.draw(canvas);
                            canvas.restore();
                            y += staticLayout.getHeight();
                            y += 30;
                        }
                    }

                    document.finishPage(page);
                    document.writeTo(fOut);
                    document.close();

                    if (file.exists())
                    {
                        Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);

                        try
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(path, "application/pdf");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_pdf), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.error_simple), Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.error_simple), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.error_ratings), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * <h2>On Resume</h2>
     * Called when Activity becomes visible
     * Calls the prepare function in the viewModel
     *
     */

    @Override
    public void onResume()
    {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            viewModel.prepare();
            handler.removeCallbacksAndMessages(null);
        }, getResources().getInteger(R.integer.start_delay));
    }

    /**
     * <h2>On Support Navigate Up</h2>
     * Override
     * Handle the click on the back arrow and call onBackPressed
     */

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
