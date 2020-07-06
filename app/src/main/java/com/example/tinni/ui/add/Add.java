package com.example.tinni.ui.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.tinni.R;
import com.example.tinni.adapters.CategoryAdapter;
import com.example.tinni.databinding.ActivityAddBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Category;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Add Activity</h1>
 * Everything related to the add ui
 *
 * Variables:
 * int PICK_AUDIO_REQUEST: A constant value to determine a pick audio request
 * int PICK_IMAGE_REQUEST: A constant value to determine a pick image request
 * ActivityAddBinding binding: The binding to the layout file
 * AddViewModel viewModel: The ViewModel to this page
 * Animation pulse: A reference to the pulse animation
 * MediaPlayer mediaPlayer: A reference to the media player
 * Handler handler: Handler to update the current progress of the sound file
 * Runnable runnable: Reference to the runnable connected to the handler
 * boolean isUploading: Indicator if user is currently picking an audio file
 * boolean isPlaying: Indicator if audio file is currently playing
 * Animation tap: The tap animation for the submit button
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   21.06.2020
 */

public class Add extends AppCompatActivity
{
    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private AddViewModel viewModel;
    private ActivityAddBinding binding;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isUploading = false;
    private boolean isPlaying = false;
    private static Animation tap;
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
        viewModel = new ViewModelProvider(this).get(AddViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        binding.uploadContainer.startAnimation(pulse);

        tap = AnimationUtils.loadAnimation(this, R.anim.tap);

        binding.upload.setOnClickListener(v ->
        {
            if (!isUploading)
            {
                openUploadActivity();
            }
        });

        binding.reupload.setOnClickListener(v ->
        {
            if (!isUploading)
            {
                openUploadActivity();
            }
        });

        binding.imageContainer.setOnClickListener(v ->
        {
            if (!isUploading)
            {
                openImageActivity();
            }
        });

        binding.play.setOnClickListener(v ->
        {
            if (!isUploading && mediaPlayer != null)
            {
                if (!isPlaying)
                {
                    startSound();
                }
                else
                {
                    stopSound(false);
                }
            }
        });

        binding.submit.Init(getResources().getString(R.string.upload));

        binding.submit.setOnClickListener(v ->
        {
            if (!viewModel.loading.get())
            {
                submit();
            }
        });

        setupCategories();
    }

    /**
     * <h2>Setup Categories</h2>
     * Fill the categories gathered in the SoundsViewModel and stored inside Constants
     * Android´s FlexboxLayoutManger is used to have them fill the entire viewport
     */

    @SuppressLint("WrongConstant")
    void setupCategories ()
    {
        if (Constants.getInstance().categories.size() > 0)
        {
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);

            binding.categories.addItemDecoration(new MarginDecorator("Bottom", func.pxFromDp(this, getResources().getInteger(R.integer.medium_margin))));
            binding.categories.setLayoutManager(layoutManager);

            List<Category> categories = new ArrayList<>();
            for (Category c : Constants.getInstance().categories)
            {
                categories.add(new Category(c));
            }
            viewModel.categories = categories;

            CategoryAdapter categoryAdapter = new CategoryAdapter(viewModel.categories);
            binding.categories.setAdapter(categoryAdapter);

            ItemClickSupport.addTo(binding.categories).setOnItemClickListener((recyclerView, position, v) ->
            {
                System.out.println("1");
                Category c = categoryAdapter.getItem(position);
                if (c != null)
                {
                    System.out.println(c.getTitle());
                    System.out.println(c.active.get());
                    c.active.set(!c.active.get());
                }
            });
        }
    }

    /**
     * <h2>Submit</h2>
     * Submitting the new sound file
     */

    void submit ()
    {
        viewModel.loading.set(true);
        binding.submit.startAnimation(tap);

        Handler handler = new Handler();
        handler.postDelayed(() -> binding.submit.Animate(1, getResources().getInteger(R.integer.anim_length), 0, done ->
        {
            boolean ok = true;
            if (viewModel.uri.get() == null || Objects.requireNonNull(viewModel.uri.get()).length() == 0)
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_filetype), Toast.LENGTH_LONG).show();
            }
            else if (viewModel.title.get() == null || Objects.requireNonNull(viewModel.title.get()).length() == 0)
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_title), Toast.LENGTH_LONG).show();
            }
            else if (viewModel.categories.stream().noneMatch(c -> c.active.get()))
            {
                ok = false;
                Toast.makeText(this, getResources().getString(R.string.error_category), Toast.LENGTH_LONG).show();
            }
            else
            {
                String type = getContentResolver().getType(Objects.requireNonNull(Uri.parse(viewModel.uri.get())));
                if (type == null || !Constants.getInstance().allowedFileTypes.contains(type))
                {
                    ok = false;
                    Toast.makeText(this, String.format(getResources().getString(R.string.error_file), Constants.getInstance().allowedFileTypesString), Toast.LENGTH_LONG).show();
                }
            }

            if (ok)
            {
                binding.submit.Animate(2, getResources().getInteger(R.integer.anim_length), getResources().getInteger(R.integer.duration_button), successDone ->
                {
                    AddViewModel.saveAsyncTask saveAsyncTask = new AddViewModel.saveAsyncTask(viewModel.categories, viewModel.title, viewModel.description, viewModel.uri, viewModel.image, viewModel.length, result ->
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
     * <h2>Open Image Activity</h2>
     * Click listener for opening the Android Intent.ACTION_GET_CONTENT for selecting image files
     * Checks permission to read external storage first
     */

    void openImageActivity ()
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
     * <h2>Open Upload Activity</h2>
     * Click listener for opening the Android Intent.ACTION_GET_CONTENT for selecting audio files
     * Checks permission to read external storage first
     */

    void openUploadActivity ()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_AUDIO_REQUEST);
        }
        else
        {
            isUploading = true;
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(intent_upload, PICK_AUDIO_REQUEST);
        }
    }

    /**
     * <h2>Start Sound</h2>
     * Starting or pausing the audio playback
     *
     *
     */

    public void startSound ()
    {
        mediaPlayer.start();
        binding.play.setImageResource(R.drawable.pause);
        isPlaying = true;

        runnable = new Runnable()
        {

            @Override
            public void run()
            {
                if (mediaPlayer != null)
                {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    binding.audioBar.setProgress(currentPosition);
                    binding.currentTime.setText(func.millisecondsToTimer(mediaPlayer.getCurrentPosition()));
                    try
                    {
                        handler.postDelayed(this, getResources().getInteger(R.integer.player_repeat));
                    }
                    catch (Exception e)
                    {
                        stopSound(true);
                    }
                }
            }
        };

        runOnUiThread(runnable);
    }

    /**
     * <h2>Stop Sound</h2>
     * Stopping the audio playback
     *
     * Arguments:
     * boolean reset: Indicator if musicPlayer should be reset to 0
     *
     */

    public void stopSound (boolean reset)
    {
        if (mediaPlayer != null && isPlaying)
        {
            handler.removeCallbacks(runnable);
            isPlaying = false;
            mediaPlayer.pause();
            binding.play.setImageResource(R.drawable.play);
            if (reset)
            {
                mediaPlayer.seekTo(0);
                binding.audioBar.setProgress(0);
                binding.currentTime.setText(func.millisecondsToTimer(0));
            }
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
        if (requestCode == PICK_AUDIO_REQUEST)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (!isUploading)
                {
                    isUploading = true;
                    Intent intent_upload = new Intent();
                    intent_upload.setType("audio/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, PICK_AUDIO_REQUEST);
                }
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.error_write_permission), Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PICK_IMAGE_REQUEST)
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
     * Checking the result of the pick audio request
     * Setting up the media player to preview selected sound file
     *
     * Source: https://stackoverflow.com/a/17945257/2700965
     */

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if (requestCode == PICK_AUDIO_REQUEST)
        {
            if (resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                Uri uri = data.getData();

                if (uri != null)
                {
                    mediaPlayer = new MediaPlayer();

                    try
                    {
                        viewModel.uri.set(uri.toString());
                        mediaPlayer.setDataSource(this, uri);
                        System.out.println("#### URI ADD: " + uri);
                        try
                        {
                            mediaPlayer.prepare();

                            mediaPlayer.setOnPreparedListener(mp ->
                            {
                                binding.audioBar.setMax(mediaPlayer.getDuration() / 1000);
                                viewModel.length.set(mediaPlayer.getDuration() / 1000);
                                mediaPlayer.setOnCompletionListener(mepl -> stopSound(true));

                                binding.audioBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                                {

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar)
                                    {

                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar)
                                    {

                                    }

                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                                    {
                                        if (mediaPlayer != null && fromUser)
                                        {
                                            mediaPlayer.seekTo(progress * 1000);
                                            binding.currentTime.setText(func.millisecondsToTimer(mediaPlayer.getCurrentPosition()));
                                        }
                                    }
                                });
                            });
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(this, getResources().getString(R.string.error_filetype), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(this, getResources().getString(R.string.error_filetype), Toast.LENGTH_SHORT).show();
                    }


                    if (binding.top.getVisibility() == View.VISIBLE)
                    {
                        binding.top.setVisibility(View.GONE);
                    }

                    if (binding.form.getVisibility() == View.GONE)
                    {
                        binding.form.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(this, getResources().getString(R.string.error_filetype), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.error_filetype), Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PICK_IMAGE_REQUEST)
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
