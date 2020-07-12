package com.example.tinni.ui.pastprogram;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.transition.Transition;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.BuildConfig;
import com.example.tinni.R;
import com.example.tinni.adapters.ProgramListAdapter;
import com.example.tinni.adapters.QuestionnaireAdapter;
import com.example.tinni.adapters.SessionAdapter;
import com.example.tinni.adapters.SessionProgressAdapter;
import com.example.tinni.custom.BottomDialogQuestions;
import com.example.tinni.custom.BottomDialogRating;
import com.example.tinni.databinding.ActivityPastProgramBinding;
import com.example.tinni.databinding.ActivityProgramBinding;
import com.example.tinni.helpers.CircleTransform;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Question;
import com.example.tinni.models.Questionnaire;
import com.example.tinni.models.Session;
import com.example.tinni.ui.program.Program;
import com.example.tinni.ui.program.ProgramViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * <h1>Past Program Activity</h1>
 * Everything related to the past program ui
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

public class PastProgram extends AppCompatActivity
{
    private PastProgramViewModel viewModel;
    private ActivityPastProgramBinding binding;
    private int appbarOffset = 0;
    private com.example.tinni.models.SelectedProgram selectedProgram;
    private boolean animateEnd = true;
    private static Animation appear;
    private static Animation moveIn;
    private SessionProgressAdapter sessionAdapter;
    private boolean sessionsLoaded = false;
    private boolean closing = false;
    private boolean questionnaireLoaded = false;
    private QuestionnaireAdapter questionnaireAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Create</h2>
     * Connecting the Activity with the ViewModel
     * Setting up animations and placeholder
     * Setting up the toolbar and handle Shared Element Transition
     * Call appearing() once the transition is done
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewModel = new ViewModelProvider(this).get(PastProgramViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_past_program);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        appear = AnimationUtils.loadAnimation(this, R.anim.appear);
        moveIn = AnimationUtils.loadAnimation(this, R.anim.movein);

        binding.placeholder.blinking(true);
        binding.sessionsPlaceholder.blinking(true);

        setSupportActionBar(binding.toolbar);
        binding.appBar.bringToFront();
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            selectedProgram = Constants.getInstance().pastPrograms.stream().filter(x -> x.getId() == getIntent().getIntExtra("selectedprogram", 0)).findFirst().orElse(null);

            if (selectedProgram != null)
            {
                String imageTransitionName = extras.getString("program_transition_name");
                if (imageTransitionName != null && selectedProgram.getProgram().getBitmap() != null)
                {
                    binding.programImg.setTransitionName(imageTransitionName);
                    binding.programImg.setImageBitmap(selectedProgram.getProgram().getBitmap());
                }
                else
                {
                    if (Constants.getInstance().picasso != null && selectedProgram.getProgram().getImg() > 0)
                    {
                        Constants.getInstance().picasso.load(selectedProgram.getProgram().getImg()).fit().centerCrop().into(binding.programImg);
                    }
                    else if (Constants.getInstance().picasso != null && selectedProgram.getProgram().getImgUri() != null && selectedProgram.getProgram().getImgUri().length() > 0)
                    {
                        Constants.getInstance().picasso.load(selectedProgram.getProgram().getImgUri()).fit().centerCrop().into(binding.programImg);
                    }
                    animateEnd = false;
                    appearing();
                }
                viewModel.prepare(this, selectedProgram);
            }
            else
            {
                finish();
            }
        }
        else
        {
            finish();
        }

        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener()
        {
            @Override
            public void onTransitionStart(Transition transition)
            {

            }

            @Override
            public void onTransitionEnd(Transition transition)
            {
                appearing();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> appbarOffset = verticalOffset);

        binding.more.setOnClickListener(v -> openMore());

        binding.export.setOnClickListener(v -> generatePdf());

        ItemClickSupport.addTo(binding.sessions)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Session s = sessionAdapter.getItem(position);
                    if (s != null)
                    {
                        openSound(s);
                    }
                });
    }

    /**
     * <h2>Open Sound</h2>
     * Opens the selected sound in the Sound Activity
     *
     * @param session the selected session
     *
     */

    private void openSound (Session session)
    {
        if (session.getSound() != null)
        {
            Intent intent = new Intent(this, com.example.tinni.ui.sound.Sound.class);
            intent.putExtra("sound", session.getSound().getId());
            startActivity(intent);
        }
    }

    /**
     * <h2>Generate PDF</h2>
     * Generates a pdf file of the program
     *
     * Source 1: https://stackoverflow.com/a/23392246/2700965
     * Source 2: https://stackoverflow.com/a/52789871/2700965
     * Source 3: UNITI app, "Shades of noise"
     *
     */

    private void generatePdf()
    {
        // Create document
        PdfDocument document = new PdfDocument();

        final File sharedFolder = new File(getFilesDir(), "report");
        if(!sharedFolder.exists())
        {
            sharedFolder.mkdirs();
        }

        try
        {
            int pageWidth = 595;
            int pageHeight = 842;
            int pageNumber = 1;
            int y = 100;
            final File file = File.createTempFile("report" + (int)(System.currentTimeMillis()) / 1000, ".pdf", sharedFolder);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            Paint titlePaint = new Paint();
            Paint italicPaint = new Paint();
            Paint greenPaint = new Paint();
            Paint redPaint = new Paint();
            greenPaint.setColor(Color.GREEN);
            redPaint.setColor(Color.RED);
            italicPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
            titlePaint.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            canvas.drawText(String.format(getResources().getString(R.string.report_for), selectedProgram.getProgram().getTitle()), 10, 20, paint);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
            String fromDate = formatter.format(new Date(selectedProgram.getStart()));
            String toDate = formatter.format(new Date(selectedProgram.getEnd()));
            String date;
            if (!fromDate.equals(toDate))
            {
                date = String.format(getResources().getString(R.string.from_to), fromDate, toDate);
            }
            else
            {
                date = toDate;
            }

            long total = Constants.getInstance().pastPrograms.stream().filter(s -> s.getProgram().getId() == selectedProgram.getProgram().getId()).count();
            String totalText = String.format(getResources().getString(R.string.total_completed), total);
            canvas.drawText(String.format(getResources().getString(R.string.past_program_text), date, totalText), 10, 40, paint);

            int size = 0;
            if (viewModel.questionnaire.getValue() != null)
            {
                size = viewModel.questionnaire.getValue().size();
            }
            canvas.drawText(String.format(getResources().getString(R.string.questionnaire), size), 10, 70, titlePaint);

            if (viewModel.questionnaire.getValue() != null && viewModel.questionnaire.getValue().size() > 0)
            {
                List<Questionnaire> list = new ArrayList<>(viewModel.questionnaire.getValue());
                int sum = 160;
                for (Questionnaire q : list)
                {
                    if (q.getBefore().indexOf(System.lineSeparator()) > 0)
                    {
                        String[] split = q.getBefore().split(System.lineSeparator());
                        sum = sum + (30 * split.length);
                    }
                    else
                    {
                        sum += 30;
                    }
                    if (q.getAfter().indexOf(System.lineSeparator()) > 0)
                    {
                        String[] split = q.getAfter().split(System.lineSeparator());
                        sum = sum + (30 * split.length);
                    }
                    else
                    {
                        sum += 30;
                    }

                    if ((y + sum) > pageHeight)
                    {
                        pageNumber ++;
                        sum = 160;
                        document.finishPage(page);
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();

                        canvas.drawText(String.format(getResources().getString(R.string.report_for_page), selectedProgram.getProgram().getTitle(), pageNumber), 10, 20, paint);
                        canvas.drawText(String.format(getResources().getString(R.string.past_program_text), date, totalText), 10, 40, paint);
                        y = 70;
                    }
                    canvas.drawText(String.format(getResources().getString(R.string.separator), q.getId(), q.getQuestion()), 20, y, paint);
                    canvas.drawLine(20f, y + 10f, 575f, y + 10f, titlePaint);
                    y += 30;
                    canvas.drawText(getResources().getString(R.string.before_program), 20, y, italicPaint);
                    y += 30;
                    if (q.getBefore().indexOf(System.lineSeparator()) > 0)
                    {
                        String[] split = q.getBefore().split(System.lineSeparator());
                        for (String s : split)
                        {
                            canvas.drawText(s, 20, y, paint);
                            y += 30;
                        }
                    }
                    else
                    {
                        canvas.drawText(q.getBefore(), 20, y, paint);
                        y += 30;
                    }
                    canvas.drawText(getResources().getString(R.string.after_program), 20, y, italicPaint);
                    y += 30;
                    if (q.getAfter().indexOf(System.lineSeparator()) > 0)
                    {
                        String[] split = q.getAfter().split(System.lineSeparator());
                        for (String s : split)
                        {
                            canvas.drawText(s, 20, y, paint);
                            y += 30;
                        }
                    }
                    else
                    {
                        canvas.drawText(q.getAfter(), 20, y, paint);
                        y += 30;
                    }
                }
            }
            else
            {
                canvas.drawText(getResources().getString(R.string.no_entries), 10, y, paint);
            }

            document.finishPage(page);
            pageNumber++;

            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();

            size = 0;
            y = 100;
            if (viewModel.sessions.getValue() != null)
            {
                size = viewModel.sessions.getValue().size();
            }
            canvas.drawText(String.format(getResources().getString(R.string.report_for_page), selectedProgram.getProgram().getTitle(), pageNumber), 10, 20, paint);
            canvas.drawText(String.format(getResources().getString(R.string.past_program_text), date, totalText), 10, 40, paint);
            canvas.drawText(String.format(getResources().getString(R.string.progress), size), 10, 70, titlePaint);

            if (viewModel.sessions.getValue() != null && viewModel.sessions.getValue().size() > 0)
            {
                List<Session> list = new ArrayList<>(viewModel.sessions.getValue());
                int sum = 130;
                for (Session s : list)
                {
                    if ((y + sum) > pageHeight)
                    {
                        pageNumber ++;
                        sum = 130;
                        document.finishPage(page);
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();

                        canvas.drawText(String.format(getResources().getString(R.string.report_for_page), selectedProgram.getProgram().getTitle(), pageNumber), 10, 20, paint);
                        canvas.drawText(String.format(getResources().getString(R.string.past_program_text), date, totalText), 10, 40, paint);
                        y = 70;
                    }
                    canvas.drawText(String.format(getResources().getString(R.string.session_number), s.getId()), 20, y, paint);
                    canvas.drawLine(20f, y + 10f, 575f, y + 10f, titlePaint);
                    y += 30;
                    String sessionCount = String.format(getResources().getString(R.string.session_number), s.getId());
                    String timeText = func.getTotalTime(this, s.getTime());
                    String dateString = formatter.format(new Date(s.getDate()));
                    canvas.drawText(String.format(getResources().getString(R.string.session_text_progress), s.getSound().getTitle(), timeText, dateString), 20, y, paint);
                    y += 30;
                    String ratingText;
                    Paint ratingPaint;
                    switch (s.getRating())
                    {
                        case 5:
                            ratingText = getResources().getString(R.string.very_good);
                            ratingPaint = greenPaint;
                            break;
                        case 4:
                            ratingText = getResources().getString(R.string.good);
                            ratingPaint = greenPaint;
                            break;
                        case 3:
                            ratingText = getResources().getString(R.string.neutral);
                            ratingPaint = paint;
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
                }
            }
            else
            {
                canvas.drawText(getResources().getString(R.string.no_entries), 10, y, paint);
            }

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

            if(file.exists())
            {
                Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                System.out.println("create pdf uri path==>" + path);

                try
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e)
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
/*
    private void generatePdf ()
    {
        try {
            File file1 = new File(getApplicationContext().getFilesDir().getPath());
            if(!file1.exists())
            {
                file1.mkdirs();
            }

            File file = new File(getApplicationContext().getFilesDir().getPath(), "report"+System.currentTimeMillis()+".pdf");
            PrintAttributes printAttrs = new PrintAttributes.Builder().
                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                    setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 450, 700)).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                    build();
            PdfDocument document = new PrintedPdfDocument(this, printAttrs);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(450, 700, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            if (page != null)
            {
                int currentLine = 2;
                int textSizeHeaderTop = 12;
                int textSizeHeader = 8;
                int textSize = 6;
                int textSizeSmall = 4;
                int marginLeft = 2;
                int marginTop = marginLeft + textSizeHeaderTop;

                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setTextSize(textSizeHeaderTop);
                canvas.drawText("Bericht zu " + selectedProgram.getProgram().getTitle(), marginLeft, marginTop, paint);

                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
                String fromDate = formatter.format(new Date(selectedProgram.getStart()));
                String toDate = formatter.format(new Date(selectedProgram.getEnd()));
                String date;
                if (!fromDate.equals(toDate))
                {
                    date = String.format(getResources().getString(R.string.from_to), fromDate, toDate);
                }
                else
                {
                    date = toDate;
                }

                long total = Constants.getInstance().pastPrograms.stream().filter(s -> s.getProgram().getId() == selectedProgram.getProgram().getId()).count();
                String totalText = String.format(getResources().getString(R.string.total_completed), total);

                paint.setTextSize(textSize);
                canvas.drawText(String.format(getResources().getString(R.string.past_program_text), date, totalText), marginLeft, marginTop * currentLine, paint);
                currentLine = currentLine + 2;

                if (viewModel.questionnaire.getValue() != null && viewModel.questionnaire.getValue().size() > 0)
                {
                    paint.setTextSize(textSizeHeader);
                    canvas.drawText(String.format(getResources().getString(R.string.questionnaire), viewModel.questionnaire.getValue().size()), marginLeft, marginTop * currentLine, paint);

                    currentLine++;

                    for (Questionnaire q : viewModel.questionnaire.getValue())
                    {
                        paint.setTextSize(textSizeHeader);
                        canvas.drawText(String.format(getResources().getString(R.string.separator), q.getId(), q.getQuestion()), marginLeft, marginTop * currentLine, paint);
                        currentLine ++;
                        paint.setTextSize(textSizeSmall);
                        canvas.drawText(getResources().getString(R.string.before_program), marginLeft, marginTop * currentLine, paint);
                        currentLine++;
                        paint.setTextSize(textSize);
                        canvas.drawText(q.getBefore(), marginLeft, marginTop * currentLine, paint);
                        currentLine++;
                        paint.setTextSize(textSizeSmall);
                        canvas.drawText(getResources().getString(R.string.after_program), marginLeft, marginTop * currentLine, paint);
                        currentLine++;
                        paint.setTextSize(textSize);
                        canvas.drawText(q.getAfter(), marginLeft, marginTop * currentLine, paint);
                        currentLine++;
                    }
                }
            }
            document.finishPage(page);

            if (viewModel.sessions.getValue() != null && viewModel.sessions.getValue().size() > 0)
            {
                pageInfo = new PdfDocument.PageInfo.Builder(450, 700, 2).create();
                PdfDocument.Page pageTwo = document.startPage(pageInfo);
                int currentLine = 2;
                int textSizeHeader = 8;
                int textSize = 6;
                int textSizeSmall = 4;
                int marginLeft = 2;
                int marginTop = marginLeft + textSize;

                if (pageTwo != null)
                {
                    Canvas canvas = pageTwo.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    if (viewModel.sessions.getValue() != null && viewModel.sessions.getValue().size() > 0)
                    {
                        paint.setTextSize(textSizeHeader);
                        canvas.drawText(String.format(getResources().getString(R.string.progress), viewModel.sessions.getValue().size()), marginLeft, marginTop * currentLine, paint);

                        currentLine = currentLine + 2;

                        for (Session s : viewModel.sessions.getValue())
                        {
                            String sessionCount = String.format(getResources().getString(R.string.session_number), s.getId());
                            String timeText = func.getTotalTime(this, s.getTime());
                            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
                            String dateString = formatter.format(new Date(s.getDate()));
                            paint.setColor(Color.BLACK);
                            paint.setTextSize(textSize);
                            canvas.drawText(String.format(getResources().getString(R.string.session_text_progress), sessionCount, timeText, dateString), marginLeft, marginTop * currentLine, paint);
                            currentLine++;

                            String ratingText;
                            switch (s.getRating())
                            {
                                case 5:
                                    ratingText = getResources().getString(R.string.very_good);
                                    paint.setColor(Color.GREEN);
                                    break;
                                case 4:
                                    ratingText = getResources().getString(R.string.good);
                                    paint.setColor(Color.GREEN);
                                    break;
                                case 3:
                                    ratingText = getResources().getString(R.string.neutral);
                                    paint.setColor(Color.BLACK);
                                    break;
                                case 2:
                                    ratingText = getResources().getString(R.string.bad);
                                    paint.setColor(Color.RED);
                                    break;
                                case 1:
                                    ratingText = getResources().getString(R.string.miserable);
                                    paint.setColor(Color.RED);
                                    break;
                                default:
                                    ratingText = getResources().getString(R.string.neutral);
                                    paint.setColor(Color.BLACK);
                                    break;
                            }
                            paint.setTextSize(textSize);
                            canvas.drawText(String.format(getResources().getString(R.string.rating_arrow), ratingText), marginLeft, marginTop * currentLine, paint);
                            currentLine = currentLine + 2;
                        }
                    }
                    document.finishPage(pageTwo);
                }
            }
            FileOutputStream os = new FileOutputStream(file);
            document.writeTo(os);
            document.close();
            os.close();
            System.out.println("done: "  + file.getAbsolutePath());

            if(file.exists())
            {
                Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                System.out.println("create pdf uri path==>" + path);

                try
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(),
                            "There is no PDF Viewer",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException("Error generating file", e);
        }

    }

 */

    /**
     * <h2>Open More</h2>
     * Opens the more popup
     *
     * Source: https://stackoverflow.com/a/19253868/2700965
     */

    private void openMore ()
    {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupTheme);
        PopupMenu popup = new PopupMenu(wrapper, binding.more);

        popup.inflate(R.menu.program_menu);

        Menu menu = popup.getMenu();
        if (viewModel.selected.get() != null && Objects.requireNonNull(viewModel.selected.get()).getProgram() != null && Objects.requireNonNull(viewModel.selected.get()).getProgram().isCustom())
        {
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
        }

        popup.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.stats:
                    Toast.makeText(wrapper, "Stats", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.export:
                    com.example.tinni.models.Program p = new com.example.tinni.models.Program(Objects.requireNonNull(viewModel.selected.get()).getProgram());
                    p.active.set(false);
                    p.setDone(false);
                    for (Session s : p.getSessions())
                    {
                        s.active.set(true);
                        s.done.set(false);
                    }
                    String json = Constants.getInstance().gson.toJson(p);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(Objects.requireNonNull(viewModel.selected.get()).getProgram().getTitle(), json);
                    if (clipboard != null)
                    {
                        clipboard.setPrimaryClip(clip);
                    }
                    AlertDialog.Builder alertDialogBuilderExport = new AlertDialog.Builder(this, R.style.DialogTheme)
                            .setMessage(getResources().getString(R.string.export_text))
                            .setPositiveButton(getResources().getString(R.string.got_it), null)
                            .setNegativeButton(getResources().getString(R.string.cancel), null);
                    AlertDialog alertDialogExport = alertDialogBuilderExport.create();
                    Objects.requireNonNull(alertDialogExport.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialogExport.show();
                    Button nbuttonExport = alertDialogExport.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbuttonExport.setVisibility(View.GONE);
                    Button pbuttonExport = alertDialogExport.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbuttonExport.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }


    /**
     * <h2>Appearing</h2>
     * Animate controls once the transition is done
     * Set up the Sessions RecyclerView
     */

    private void appearing ()
    {
        binding.toolbar.setVisibility(View.VISIBLE);
        binding.toolbar.startAnimation(appear);
        binding.topContent.setVisibility(View.VISIBLE);
        binding.topContent.startAnimation(appear);
        binding.scroll.setVisibility(View.VISIBLE);
        binding.scroll.startAnimation(appear);
        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            viewModel.getQuestionnaire().observe(this, l ->
            {
                if (l != null && l.size() > 0)
                {
                    if (!questionnaireLoaded)
                    {
                        questionnaireLoaded = true;
                        binding.questionnaire.setItemViewCacheSize(20);
                        binding.questionnaire.setDrawingCacheEnabled(true);
                        binding.questionnaire.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        binding.questionnaire.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(this, getResources().getInteger(R.integer.default_margin))));
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        binding.questionnaire.setLayoutManager(layoutManager);
                    }

                    questionnaireAdapter = new QuestionnaireAdapter(l);
                    binding.questionnaire.setAdapter(questionnaireAdapter);

                    binding.placeholder.blinking(false);
                    binding.questionnaire.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.placeholder.blinking(false);
                    binding.questionnaireTitle.setVisibility(View.GONE);
                }
            });

            viewModel.getSessions().observe(this, l ->
            {
                System.out.println("#### HAB SESSION");
                if (l != null && l.size() > 0)
                {
                    System.out.println("#### HAB SESSION SIZE: " + l.size());
                    if (!sessionsLoaded)
                    {
                        System.out.println("#### SETUP SESSION");
                        binding.sessions.setItemViewCacheSize(20);
                        binding.sessions.setDrawingCacheEnabled(true);
                        binding.sessions.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        binding.sessions.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(this, getResources().getInteger(R.integer.default_margin))));
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        binding.sessions.setLayoutManager(layoutManager);
                    }

                    sessionAdapter = new SessionProgressAdapter(l);
                    binding.sessions.setAdapter(sessionAdapter);

                    binding.sessionsPlaceholder.blinking(false);
                    binding.sessions.setVisibility(View.VISIBLE);
                    System.out.println("#### MACH VISIBLE");
                }
                else
                {
                    System.out.println("#### LEER ALSO VERSTECKE");
                    binding.sessionsPlaceholder.blinking(false);
                    binding.sessionsTitle.setVisibility(View.GONE);
                }
            });
            handler.removeCallbacksAndMessages(null);
        }, getResources().getInteger(R.integer.anim_alpha));
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

    /**
     * <h2>On Back Pressed</h2>
     * Override
     * Handle the click on the back button
     * Animate controls and finish the Activity with supportFinishAfterTransition
     */

    @Override
    public void onBackPressed()
    {
        close();
    }

    private void close ()
    {
        if (!closing)
        {
            closing = true;
            if (animateEnd)
            {
                binding.appBar.setExpanded(true, true);
                binding.scroll.smoothScrollTo(0, 0);
                Handler waitHandler = new Handler();

                if (appbarOffset < 0)
                {
                    binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                    {
                        if (verticalOffset >= 0)
                        {
                            waitHandler.removeCallbacksAndMessages(null);
                            supportFinishAfterTransition();
                        }
                    });
                    waitHandler.postDelayed(this::supportFinishAfterTransition, getResources().getInteger(R.integer.anim_length_slow));
                }
                else
                {
                    supportFinishAfterTransition();
                }
            }
            else
            {
                finish();
            }
        }
    }
}
