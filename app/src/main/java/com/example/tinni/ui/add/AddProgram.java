package com.example.tinni.ui.add;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.AddQuestionsAdapter;
import com.example.tinni.adapters.SelectedSessionAdapter;
import com.example.tinni.custom.BottomDialogQuestion;
import com.example.tinni.custom.BottomDialogSessions;
import com.example.tinni.databinding.ActivityAddProgramBinding;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.ItemMoveCallback;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Question;

import java.io.IOException;
import java.util.Objects;

/**
 * <h1>Add Program Activity</h1>
 * Everything related to the add ui
 *
 * Variables:
 * int PICK_IMAGE_REQUEST: A constant value to determine a pick image request
 * Animation tap: The tap animation for the submit button
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   03.07.2020
 */

public class AddProgram extends AppCompatActivity
{
    private static final int PICK_IMAGE_REQUEST = 1;
    private AddProgramViewModel viewModel;
    private ActivityAddProgramBinding binding;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isUploading = false;
    private boolean isPlaying = false;
    private static Animation tap;
    private static FragmentManager fragmentManager;
    private boolean setup = false;
    private SelectedSessionAdapter selectedSessionAdapter;
    private AddQuestionsAdapter addQuestionsAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Submit result</h2>
     * Interface to get the submit result
     */

    public interface onSubmitResult
    {
        void result(boolean success);
    }

    /**
     * <h2>On Create</h2>
     * Connecting the activity with the ViewModel and inflating its layout view
     * Setting up the toolbar
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddProgramViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_program);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tap = AnimationUtils.loadAnimation(this, R.anim.tap);

        fragmentManager = getSupportFragmentManager();

        binding.imageContainer.setOnClickListener(v ->
        {
            if (!isUploading)
            {
                openImageActivity();
            }
        });

        binding.submit.Init(getResources().getString(R.string.create_program));

        binding.submit.setOnClickListener(v ->
        {
            if (!viewModel.loading.get())
            {
                submit();
            }
        });

        binding.emptySessionsContainer.emptySessions.setOnClickListener(v -> openSessions());

        binding.emptyQuestionsContainer.emptySessions.setOnClickListener(v -> openQuestion(null));

        binding.addQuestion.setOnClickListener(v -> openQuestion(null));

        ItemClickSupport.addTo(binding.selectedSounds)
                .setOnItemClickListener((recyclerView, position, v) -> openSessions());

        ItemClickSupport.addTo(binding.questions)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Question q = addQuestionsAdapter.getItem(position);
                    if (q != null)
                    {
                        openQuestion(q);
                    }
                });

        binding.title.setOnClickListener(v -> selectedSessionAdapter.reloadList());

        viewModel.prepare();
        setupRecyclers();
    }

    /**
     * <h2>Setup Recylcers</h2>
     *
     * Setting up the RecyclerViews for the selected sessions and questions
     *
     */

    private void setupRecyclers ()
    {
        if (!setup)
        {
            setup = true;

            binding.selectedSounds.setItemViewCacheSize(20);
            binding.selectedSounds.setDrawingCacheEnabled(true);
            binding.selectedSounds.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.selectedSounds.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(binding.selectedSounds.getContext(), getResources().getInteger(R.integer.default_margin))));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            binding.selectedSounds.setLayoutManager(layoutManager);
            selectedSessionAdapter = new SelectedSessionAdapter(viewModel.selectedSessions);
            binding.selectedSounds.setAdapter(selectedSessionAdapter);

            binding.questions.setItemViewCacheSize(20);
            binding.questions.setDrawingCacheEnabled(true);
            binding.questions.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.questions.setLayoutManager(layoutManager);
            addQuestionsAdapter = new AddQuestionsAdapter(viewModel.questions);
            binding.questions.setAdapter(addQuestionsAdapter);

            ItemTouchHelper.Callback callback = new ItemMoveCallback("questions", addQuestionsAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(binding.questions);
        }
    }

    /**
     * <h2>Submit</h2>
     * Submitting the new sound file
     */

    private void submit ()
    {
        viewModel.loading.set(true);
        binding.submit.startAnimation(tap);

        Handler handler = new Handler();
        handler.postDelayed(() -> binding.submit.Animate(1, getResources().getInteger(R.integer.anim_length), 0, done ->
        {
            boolean ok = true;
            if (viewModel.title.get() == null || Objects.requireNonNull(viewModel.title.get()).length() == 0)
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_title), Toast.LENGTH_LONG).show();
            }
            else if (viewModel.interval.get() <= 0)
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_interval), Toast.LENGTH_LONG).show();
            }
            else if (viewModel.selectedSessions == null || viewModel.selectedSessions.size() == 0)
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_session), Toast.LENGTH_LONG).show();
            }
            else if (viewModel.questions == null || viewModel.questions.size() == 0)
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_no_questions), Toast.LENGTH_LONG).show();
            }

            if (ok)
            {
                binding.submit.Animate(2, getResources().getInteger(R.integer.anim_length), getResources().getInteger(R.integer.duration_button), successDone ->
                {
                    AddProgramViewModel.saveAsyncTask saveAsyncTask = new AddProgramViewModel.saveAsyncTask(viewModel.title, viewModel.description, viewModel.image, viewModel.selectedSessions, viewModel.questions, viewModel.interval, result ->
                    {
                        if (result)
                        {
                            finish();
                        }
                        else
                        {
                            Toast.makeText(this, getResources().getString(R.string.error_simple), Toast.LENGTH_LONG).show();
                        }
                        viewModel.loading.set(false);
                    });
                    saveAsyncTask.execute();
                });
            }
            else
            {
                binding.submit.Animate(3, getResources().getInteger(R.integer.anim_length), getResources().getInteger(R.integer.duration_button), errorDone -> viewModel.loading.set(false));
            }

        }), getResources().getInteger(R.integer.anim_length_medium));
    }

    /**
     * <h2>Update Adapter</h2>
     * Refreshes the RecyclerView by refreshing its adapter
     *
     * @param session Indicator whether the selectedSessionAdapter should be updated or the other
     *
     */

    public void updateAdapter (boolean session)
    {
        if (session)
        {
            if (selectedSessionAdapter != null)
            {
                selectedSessionAdapter.reloadList();
            }
        }
        else
        {
            if (addQuestionsAdapter != null)
            {
                addQuestionsAdapter.reloadList();
            }
        }
    }

    /**
     * <h2>Open Question</h2>
     * Opens the question creator
     *
     */

    private void openQuestion (Question q)
    {
        BottomDialogQuestion bottomDialogQuestion = new BottomDialogQuestion();
        bottomDialogQuestion.newInstance(viewModel, addQuestionsAdapter, this, fragmentManager, q);
        bottomDialogQuestion.show(fragmentManager, "question");
    }

    /**
     * <h2>Open Sessions</h2>
     * Opens the sessions add popup
     *
     */

    private void openSessions ()
    {
        if (selectedSessionAdapter != null)
        {
            BottomDialogSessions bottomDialogSessions = new BottomDialogSessions();
            bottomDialogSessions.newInstance(viewModel, selectedSessionAdapter, this);
            bottomDialogSessions.show(fragmentManager, "sessions");
        }
    }

    /**
     * <h2>Open Image Activity</h2>
     * Click listener for opening the Android Intent.ACTION_GET_CONTENT for selecting image files
     * Checks permission to read external storage first
     */

    private void openImageActivity ()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }
        else
        {
            isUploading = true;
            Intent intent_upload = new Intent();
            intent_upload.setType("image/*");
            intent_upload.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(intent_upload, PICK_IMAGE_REQUEST);
        }
    }

    /**
     * <h2>On Request permissions result</h2>
     * Checking permission for uploading sound and image files and error handling
     *
     * Source: https://developer.android.com/training/permissions/requesting
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PICK_IMAGE_REQUEST)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (!isUploading)
                {
                    isUploading = true;
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, PICK_IMAGE_REQUEST);
                }
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.error_write_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * <h2>On activity result</h2>
     *
     * Source: https://stackoverflow.com/a/17945257/2700965
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE_REQUEST)
        {
            if(resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                Uri uri = data.getData();
                Bitmap bitmap;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    if (bitmap != null)
                    {
                        //viewModel.bitmap.set(func.rotateBitmap(this, uri, bitmap));
                        viewModel.image.set(uri.toString());
                    }
                    else
                    {
                        Toast.makeText(this, getResources().getString(R.string.error_filetype_image), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.error_filetype_image), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.error_filetype_image), Toast.LENGTH_SHORT).show();
            }
        }

        isUploading = false;

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * <h2>On Support Navigate Up</h2>
     * Closing the activity by clicking the back arrow in the toolbar
     */

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
