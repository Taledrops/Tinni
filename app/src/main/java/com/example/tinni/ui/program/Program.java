package com.example.tinni.ui.program;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.SessionAdapter;
import com.example.tinni.custom.BottomDialogQuestions;
import com.example.tinni.custom.BottomDialogRating;
import com.example.tinni.databinding.ActivityProgramBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

public class Program extends AppCompatActivity
{
    private ProgramViewModel viewModel;
    private ActivityProgramBinding binding;
    private int appbarOffset = 0;
    private com.example.tinni.models.Program program;
    private boolean animateEnd = true;
    private static Animation appear;
    private static Animation moveIn;
    private boolean closing = false;
    private SessionAdapter sessionAdapter;
    private Session currentSession = null;
    private boolean isDeleting = false;
    private static FragmentManager fragmentManager;
    private static final Functions func = new Functions();

    public interface onDeleteProgramResult
    {
        void result(boolean success);
    }

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
        viewModel = new ViewModelProvider(this).get(ProgramViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_program);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        appear = AnimationUtils.loadAnimation(this, R.anim.appear);
        moveIn = AnimationUtils.loadAnimation(this, R.anim.movein);

        fragmentManager = getSupportFragmentManager();

        binding.placeholder.blinking(true);

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
            program = Constants.getInstance().programs.stream().filter(x -> x.getId() == getIntent().getIntExtra("program", 0)).findFirst().orElse(null);

            if (program != null)
            {
                String imageTransitionName = extras.getString("program_transition_name");
                if (imageTransitionName != null && program.getBitmap() != null)
                {
                    binding.programImg.setTransitionName(imageTransitionName);
                    binding.programImg.setImageBitmap(program.getBitmap());
                }
                else
                {
                    animateEnd = false;
                    appearing();
                }
                viewModel.prepare(program);
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

        ItemClickSupport.addTo(binding.sessions)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Session s = sessionAdapter.getItem(position);
                    if (s != null)
                    {
                        openSound(s);
                    }
                });

        binding.start.setOnClickListener(v -> handleStart());

        binding.startTop.setOnClickListener(v -> handleStart());

        binding.more.setOnClickListener(v -> openMore());
    }

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
        if (viewModel.current.getValue() != null && Objects.requireNonNull(viewModel.current.getValue()).isCustom())
        {
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
        }

        popup.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.stats:
                    openPastProgram();
                    return true;
                case R.id.export:
                    com.example.tinni.models.Program p = new com.example.tinni.models.Program(viewModel.current.getValue());
                    p.active.set(false);
                    p.setDone(false);
                    p.getSessions().removeIf(x -> x.getSound().isCustom());
                    for (Session s : p.getSessions())
                    {
                        s.active.set(true);
                        s.done.set(false);
                    }
                    String json = Constants.getInstance().gson.toJson(p);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(viewModel.current.getValue().getTitle(), json);
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
                case R.id.delete:
                    if (!isDeleting)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme)
                                .setMessage(getResources().getString(R.string.delete_program_verification))
                                .setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> deleteProgram())
                                .setNegativeButton(getResources().getString(R.string.cancel), null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();
                        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    }
                    else
                    {
                        Toast.makeText(this, getResources().getString(R.string.wait), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    /**
     * <h2>Open Past Program</h2>
     * Opens the last completed program of this program
     *
     * Source: https://stackoverflow.com/a/13821611/2700965
     */

    private void openPastProgram ()
    {
        List<SelectedProgram> list = Constants.getInstance().pastPrograms.stream().filter(x -> x.getProgram().getId() == program.getId()).collect(Collectors.toList());
        if (list.size() > 0)
        {
            Collections.sort(list, (obj1, obj2) -> Long.compare(obj2.getEnd(), obj1.getEnd()));
            SelectedProgram sp = list.get(0);
            com.example.tinni.models.Program p = sp.getProgram();
            if (p != null)
            {
                Intent intent = new Intent(this, com.example.tinni.ui.pastprogram.PastProgram.class);
                intent.putExtra("selectedprogram", sp.getId());
                startActivity(intent);
            }
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.error_history), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * <h2>Delete Program</h2>
     * Calls the AsyncTask to delete this program
     */

    private void deleteProgram ()
    {
        isDeleting = true;

        ProgramViewModel.deleteProgramAsyncTask deleteProgramAsyncTask = new ProgramViewModel.deleteProgramAsyncTask(viewModel.current.getValue(), result ->
        {
            if (result)
            {
                finish();
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.error_simple), Toast.LENGTH_SHORT).show();
            }
            isDeleting = false;
        });

        deleteProgramAsyncTask.execute();
    }

    /**
     * <h2>Handle Start</h2>
     * Handles the tap on the start program button
     *
     *
     */

    private void handleStart ()
    {
        if (viewModel.active.get() == null)
        {
            if (Constants.getInstance().selectedProgram == null)
            {
                BottomDialogQuestions bottomDialogQuestions = new BottomDialogQuestions();
                bottomDialogQuestions.newInstance(viewModel, true, fragmentManager);
                bottomDialogQuestions.show(fragmentManager, "program");
            }
            else
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setMessage(getResources().getString(R.string.error_program))
                        .setPositiveButton(getResources().getString(R.string.start), (dialog, which) ->
                        {
                            Constants.getInstance().deleteSelectedProgram();
                            BottomDialogQuestions bottomDialogQuestions = new BottomDialogQuestions();
                            bottomDialogQuestions.newInstance(viewModel, true, fragmentManager);
                            bottomDialogQuestions.show(fragmentManager, "program");
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (alertDialog.getWindow() != null)
                {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                }
            }
        }
        else
        {
            if (sessionAdapter.sessionList.size() >= Objects.requireNonNull(viewModel.active.get()).sessions.size())
            {
               Session s = sessionAdapter.sessionList.get(Objects.requireNonNull(viewModel.active.get()).sessions.size() - 1);
               if (s != null)
               {
                   openSound(s);
               }
            }
        }
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
        if (session.getSound() != null && session.active.get())
        {
            Intent intent = new Intent(this, com.example.tinni.ui.sound.Sound.class);
            intent.putExtra("sound", session.getSound().getId());

            if (viewModel.active.get() != null && Objects.requireNonNull(viewModel.active.get()).getProgram() != null)
            {
                intent.putExtra("session", session.getId());
                intent.putExtra("program", Objects.requireNonNull(viewModel.active.get()).getProgram().getId());
                currentSession = session;
            }
            startActivity(intent);
        }
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
            viewModel.getCurrent().observe(this, result ->
            {
                if (result != null)
                {
                    if (sessionAdapter == null)
                    {
                        binding.sessions.setItemViewCacheSize(20);
                        binding.sessions.setDrawingCacheEnabled(true);
                        binding.sessions.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        binding.sessions.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(Program.this, getResources().getInteger(R.integer.default_margin))));
                        LinearLayoutManager layoutManager = new LinearLayoutManager(Program.this, LinearLayoutManager.VERTICAL, false);
                        binding.sessions.setLayoutManager(layoutManager);

                        sessionAdapter = new SessionAdapter(result.getSessions());
                        binding.sessions.setAdapter(sessionAdapter);

                        binding.placeholder.blinking(false);
                        binding.sessions.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        sessionAdapter.reloadList(result.getSessions());
                    }
                }
            });
            handler.removeCallbacksAndMessages(null);
            binding.start.setVisibility(View.VISIBLE);
            binding.start.startAnimation(moveIn);
        }, getResources().getInteger(R.integer.anim_alpha));
    }

    /**
     * <h2>On Resume</h2>
     * Called when fragment becomes visible
     * Checks whether to update the program and switch to the next session
     *
     */

    @Override
    public void onResume()
    {
        super.onResume();
        if (Constants.getInstance().updateSession != null)
        {
            BottomDialogRating bottomDialogRating = new BottomDialogRating();
            bottomDialogRating.setCancelable(false);
            bottomDialogRating.newInstance(viewModel, currentSession, fragmentManager);
            bottomDialogRating.show(fragmentManager, "rating");
        }
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
                            binding.container.removeView(binding.start);
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
