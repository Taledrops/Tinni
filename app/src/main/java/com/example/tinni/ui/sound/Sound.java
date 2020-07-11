package com.example.tinni.ui.sound;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.tinni.R;
import com.example.tinni.databinding.ActivitySoundBinding;
import com.example.tinni.helpers.AppBarStateChangeListener;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.ui.notification.NotificationDummy;
import com.google.android.material.appbar.AppBarLayout;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * <h1>Sound Activity</h1>
 * Everything related to the sound ui
 *
 * Variables:
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   25.06.2020
 */

public class Sound extends AppCompatActivity
{
    private static final float MAX_VOLUME = 100;
    private float lastVolume = Constants.getInstance().volume;
    private boolean isMuted = false;
    private SoundViewModel viewModel;
    private com.example.tinni.models.Sound sound;
    private static final Functions func = new Functions();
    private ActivitySoundBinding binding;
    private static Animation appear;
    private static Animation disappear;
    private static Animation tap;
    private static Animation fadeIn;
    private static Animation fadeOut;
    private Runnable runnable;
    private Handler handler = new Handler();
    private boolean collapsed = false;
    private boolean closing = false;
    private int appbarOffset = 0;
    private boolean setup = false;
    private MediaPlayer mediaPlayer;
    private boolean isLiking = false;
    private boolean isDeleting = false;
    private boolean preparing = false;
    private boolean animateEnd = true;
    private int session = 0;
    private int currentTime = 0;
    private LocalDateTime startTime = null;
    private boolean finished = false;
    private int startDate = (int)(System.currentTimeMillis()) / 1000;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;

    public interface onLikeSoundResult
    {
        void result(boolean liking);
    }

    public interface onDeleteSoundResult
    {
        void result(boolean success);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewModel = new ViewModelProvider(this).get(SoundViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sound);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
        tap = AnimationUtils.loadAnimation(this, R.anim.tap);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        appear = AnimationUtils.loadAnimation(this, R.anim.appear);
        disappear = AnimationUtils.loadAnimation(this, R.anim.disappear);

        binding.bottomPlay.post(() -> binding.scrollContent.setPadding(0, 0, 0, binding.bottomPlayer.getHeight()));

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
            sound = Constants.getInstance().sounds.stream().filter(x -> x.getId() == getIntent().getIntExtra("sound", 0)).findFirst().orElse(null);

            if (sound != null)
            {
                session = getIntent().getIntExtra("session", 0);

                String imageTransitionName = extras.getString("sound_transition_name");
                if (imageTransitionName != null && sound.getBitmap() != null)
                {
                    binding.soundImg.setTransitionName(imageTransitionName);
                    binding.soundImg.setImageBitmap(sound.getBitmap());
                }
                else
                {
                    animateEnd = false;
                    appearing();
                }
                viewModel.prepare(sound, session);
                setup();
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

        binding.appBar.addOnOffsetChangedListener(new AppBarStateChangeListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state)
            {
                if (state.equals(State.COLLAPSED) && !collapsed)
                {
                    //collapsed
                    collapsed = true;
                    binding.title.setVisibility(View.VISIBLE);
                    binding.title.startAnimation(fadeIn);
                }
                else if (state.equals(State.IDLE) && collapsed)
                {
                    //Expanded
                    collapsed = false;
                    binding.title.startAnimation(fadeOut);
                    binding.title.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.scrollDown.setOnClickListener(v -> binding.appBar.setExpanded(false, true));

        binding.up.setOnClickListener(v ->
        {
            binding.appBar.setExpanded(true, true);
            binding.scroll.smoothScrollTo(0, 0);
        });

        binding.play.setOnClickListener(v ->
        {
            binding.playContainer.startAnimation(tap);
            play();
        });

        binding.bottomPlay.setOnClickListener(v -> play());

        binding.forward.setOnClickListener(v -> skip(true));

        binding.rewind.setOnClickListener(v -> skip(false));

        binding.fav.setOnClickListener(v ->
        {
            if (!isLiking)
            {
                likeSound();
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.wait), Toast.LENGTH_SHORT).show();
            }
        });

        binding.volumeButton.setOnClickListener(v ->
        {
            if (mediaPlayer != null)
            {
                if (Constants.getInstance().volume > 0f)
                {
                    lastVolume = Constants.getInstance().volume;
                    volume(0, true);
                }
                else
                {
                    if (lastVolume == 0)
                    {
                        lastVolume = getResources().getInteger(R.integer.default_volume);
                    }
                    volume(lastVolume, true);
                }
            }
        });

        binding.volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (mediaPlayer != null)
                {
                    volume((float)progress, false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        binding.more.setOnClickListener(v -> openMore());
    }

    /**
     * <h2>Like Sound</h2>
     * Handles the liking of this sound
     */

    private void likeSound ()
    {
        isLiking = true;

        SoundViewModel.likeSoundAsyncTask likeStoryAsyncTask = new SoundViewModel.likeSoundAsyncTask(viewModel.current.getValue(), viewModel.liked, result ->
        {
            if (result)
            {
                Toast.makeText(this, getResources().getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
            }
            isLiking = false;
        });

        likeStoryAsyncTask.execute();
    }

    /**
     * <h2>Delete Sound</h2>
     * Calls the AsyncTask to delete this sound
     */

    private void deleteSound ()
    {
        isDeleting = true;

        SoundViewModel.deleteSoundAsyncTask deleteSoundAsyncTask = new SoundViewModel.deleteSoundAsyncTask(viewModel.current.getValue(), result ->
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

        deleteSoundAsyncTask.execute();
    }

    /**
     * <h2>Open More</h2>
     * Opens the more popup
     */

    private void openMore ()
    {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupTheme);
        PopupMenu popup = new PopupMenu(wrapper, binding.more);

        popup.inflate(R.menu.sound_menu);

        Menu menu = popup.getMenu();
        if (viewModel.current.getValue() != null && Objects.requireNonNull(viewModel.current.getValue()).isCustom())
        {
            menu.getItem(1).setVisible(true);
        }

        popup.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.stats:
                    Toast.makeText(wrapper, "Stats", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.delete:
                    if (!isDeleting)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme)
                                .setMessage(getResources().getString(R.string.delete_verification))
                                .setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> deleteSound())
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

    private void appearing ()
    {
        Animation anim = new AlphaAnimation(0f, 0.8f);
        anim.setFillAfter(true);
        anim.setDuration(getResources().getInteger(R.integer.anim_alpha));
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.toolbar.startAnimation(appear);
                binding.headContent.setVisibility(View.VISIBLE);
                binding.headContent.startAnimation(appear);
                binding.scrollDown.setVisibility(View.VISIBLE);
                binding.scrollDown.startAnimation(appear);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                binding.kenBurns.setVisibility(View.VISIBLE);
                if (!setup)
                {
                    setup = true;

                    notificationManagerCompat = NotificationManagerCompat.from(Sound.this);

                    String contentTitle = String.format(getResources().getString(R.string.sound_playing), sound.getTitle());
                    notificationBuilder = func.createNotificationBuilder(Sound.this, "play_channel", NotificationManager.IMPORTANCE_LOW);
                    Intent in = new Intent(Sound.this, NotificationDummy.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(Sound.this,0, in, 0);
                    notificationBuilder.setContentIntent(pendingIntent).setAutoCancel(true);
                    notificationBuilder.setTicker(String.format(getResources().getString(R.string.sound_playing), sound.getTitle()));
                    notificationBuilder.setOngoing(false);
                    notificationBuilder.setSmallIcon(R.drawable.circle_accent);
                    notificationBuilder.setContentTitle(contentTitle);
                    notificationBuilder.setProgress(sound.getLength(), 0, false);
                    notificationBuilder.setContentText(String.format(getResources().getString(R.string.sound_playing), sound.getTitle()));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        binding.layer.startAnimation(anim);
    }

    private boolean isHeadphonesPluggedIn ()
    {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo deviceInfo : audioDevices)
            {
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET)
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    private void setup ()
    {
        Toast.makeText(this, sound.getUri(), Toast.LENGTH_SHORT).show();
        preparing = true;

        if (mediaPlayer != null)
        {
            mediaPlayer.reset();
        }

        Uri uri = Uri.parse(sound.getUri());

        try
        {
            mediaPlayer = new MediaPlayer();
            System.out.println("#### URI SOUND: " + uri);
            mediaPlayer.setDataSource(Sound.this, uri);
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> binding.buffer.setProgress(percent));
            mediaPlayer.setOnPreparedListener(mp ->
            {
                if (!closing)
                {
                    preparing = false;
                    binding.progress.setMax(viewModel.length.get());
                    binding.bottomProgress.setMax(viewModel.length.get());
                    binding.bottomProgress.setProgress(0);
                    binding.progress.setProgress(0);
                    volume(Constants.getInstance().volume, true);
                    sound.playing.set(0);

                    binding.progress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()
                    {

                        @Override
                        public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser)
                        {
                            if (mediaPlayer != null && fromUser)
                            {
                                change(progress);
                            }
                        }

                        @Override
                        public void onStopTrackingTouch(CircularSeekBar seekBar)
                        {

                        }

                        @Override
                        public void onStartTrackingTouch(CircularSeekBar seekBar)
                        {

                        }

                    });

                    binding.bottomProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                    {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                        {
                            if (mediaPlayer != null && fromUser)
                            {
                                change(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar)
                        {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar)
                        {

                        }
                    });
                }
                else if (mediaPlayer != null)
                {
                    mediaPlayer.release();
                }
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) ->
            {
                Toast.makeText(Sound.this, getResources().getString(R.string.error_play), Toast.LENGTH_SHORT).show();
                sound.playing.set(3);
                preparing = false;
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> stop(false));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(Sound.this, getResources().getString(R.string.error_play), Toast.LENGTH_SHORT).show();
            sound.playing.set(3);
            preparing = false;
        }
    }

    private void play ()
    {
        if (sound.playing.get() != 2)
        {
            switch (sound.playing.get())
            {
                case 0:
                    mediaPlayer.start();
                    sound.playing.set(1);
                    if (notificationManagerCompat != null && notificationBuilder != null)
                    {
                        Toast.makeText(this, "NOT 1", Toast.LENGTH_SHORT).show();
                        notificationManagerCompat.notify(sound.getId(), notificationBuilder.build());
                    }

                    if (session > 0)
                    {
                        startTime = LocalDateTime.now().minusSeconds(currentTime);
                    }
                    runnable = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (mediaPlayer != null && mediaPlayer.isPlaying())
                            {
                                if (session == 0)
                                {
                                    sound.time.set(mediaPlayer.getCurrentPosition());
                                }
                                else
                                {
                                    currentTime = (int) ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
                                    sound.time.set(currentTime * 1000);
                                    if (currentTime >= viewModel.length.get())
                                    {
                                        finished = true;
                                        stop(false);
                                    }
                                }
                                try
                                {
                                    if (notificationBuilder != null)
                                    {
                                        int time = mediaPlayer.getCurrentPosition() / 1000;
                                        String displayTime = func.millisecondsToTimer(mediaPlayer.getCurrentPosition());
                                        if (session > 0)
                                        {
                                            time = currentTime;
                                            displayTime = func.millisecondsToTimer(time * 1000);
                                        }
                                        notificationBuilder.setContentText(displayTime);
                                        notificationBuilder.setProgress(viewModel.length.get(), time, false);
                                        notificationBuilder.setContentTitle(sound.getTitle());
                                        if (!finished)
                                        {
                                            notificationManagerCompat.notify(sound.getId(), notificationBuilder.build());
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                try
                                {
                                    handler.postDelayed(this, getResources().getInteger(R.integer.player_repeat));
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    runOnUiThread(runnable);
                    binding.kenBurns.resume();

                    if (!isHeadphonesPluggedIn())
                    {
                        Toast.makeText(this, getResources().getString(R.string.error_headphone), Toast.LENGTH_LONG).show();
                    }
                    break;
                case 1:
                    mediaPlayer.pause();
                    sound.playing.set(0);
                    if (handler != null && runnable != null)
                    {
                        handler.removeCallbacks(runnable);
                    }
                    binding.kenBurns.pause();
                    if (notificationManagerCompat != null && notificationBuilder != null)
                    {
                        notificationManagerCompat.cancel(sound.getId());
                    }
                    break;
                case 3:
                    setup();
                    break;
            }
        }
    }

    /**
     * <h2>Stop</h2>
     * Stops the playback
     * Checks for repeat on the viewModel and replays the sound if true
     * Stops callbacks and handlers and resets the MedidPlayer and seekbar
     *
     * @param close whether the Activity is being closed
     *
     */

    private void stop (boolean close)
    {
        Toast.makeText(this, "stop 1 ", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null && sound.playing.get() == 1)
        {
            Toast.makeText(this, "stop 2 ", Toast.LENGTH_SHORT).show();
            if (session == 0 || currentTime >= viewModel.length.get() || close)
            {
                Toast.makeText(this, "stop 3", Toast.LENGTH_SHORT).show();
                if (handler != null && runnable != null)
                {
                    handler.removeCallbacks(runnable);
                }
                startTime = null;
                currentTime = 0;
                sound.playing.set(0);
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                sound.time.set(0);
                if (close)
                {
                    mediaPlayer.release();
                }
                binding.kenBurns.pause();

                if (notificationManagerCompat != null && notificationBuilder != null)
                {
                    notificationManagerCompat.cancel(sound.getId());
                }

                if (session > 0)
                {
                    Toast.makeText(this, "stop 4 ", Toast.LENGTH_SHORT).show();
                    if (!Objects.requireNonNull(viewModel.session.get()).done.get())
                    {
                        Toast.makeText(this, "stop 6 ", Toast.LENGTH_SHORT).show();
                        Constants.getInstance().updateSession = viewModel.session.get();
                    }
                    close();
                }
            }
            else
            {
                Toast.makeText(this, "stop 5 ", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            }
        }

        if (!close && viewModel.repeat.get())
        {
            play();
        }
    }

    private void volume (float volume, boolean bar)
    {
        if (mediaPlayer != null)
        {
            Constants.getInstance().volume = volume;
            float mpVolume = (float) (1 - (Math.log(MAX_VOLUME - volume) / Math.log(MAX_VOLUME)));
            if (Float.isInfinite(mpVolume))
            {
                mpVolume = 1;
            }
            mediaPlayer.setVolume(mpVolume, mpVolume);
            if (volume <= 0 && !isMuted)
            {
                binding.volumeButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.volume_off));
                isMuted = true;
            }
            else if (volume > 0 && isMuted)
            {
                isMuted = false;
                binding.volumeButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.volume));
            }
            if (bar)
            {
                binding.volumeBar.setProgress((int) volume);
            }
        }
    }

    /**
     * <h3>Change</h3>
     * Handles the manual change of the seekbar
     *
     * if session is 0:
     * seek to the current progress * 1000 (for milliseconds)
     * set the time of the current sound to the current position of the mediaPlayer
     *
     * if session is > 0
     * seek to the current progress if it does not exceed the length of the sound
     * if it exceeds the length of the sound divide the progress with the sound length
     * the resulting fraction gives us the relative value to seek to in the sound file
     *
     * also set the currentTime to the current progress and the startTime to now minus currentTime
     *
     *
     * @param progress The current progress
     */

    private void change (float progress)
    {
        if (session == 0)
        {
            mediaPlayer.seekTo((int) progress * 1000);
            sound.time.set(mediaPlayer.getCurrentPosition());
        }
        else
        {
            float soundLength = sound.getLength();

            if (progress <= soundLength)
            {
                mediaPlayer.seekTo((int)progress * 1000);
            }
            else
            {
                float fraction = progress / soundLength;
                fraction = fraction - (float)Math.floor(fraction);
                float newProgress = soundLength * fraction;
                mediaPlayer.seekTo((int)newProgress * 1000);
            }

            currentTime = (int)progress;
            startTime = LocalDateTime.now().minusSeconds(currentTime);
            sound.time.set(currentTime * 1000);
        }
    }

    private void skip (boolean forward)
    {
        if (mediaPlayer != null && !preparing)
        {
            int newValue = sound.time.get();
            int full = viewModel.length.get() * 1000;
            if (forward)
            {
                newValue = newValue + getResources().getInteger(R.integer.skip_seconds);
                if (newValue > full)
                {
                    newValue = full;
                }
            }
            else
            {
                newValue = newValue - getResources().getInteger(R.integer.skip_seconds);
                if (newValue < 0)
                {
                    newValue = 0;
                }
            }

            if (session == 0)
            {
                mediaPlayer.seekTo(newValue);
                sound.time.set(newValue);
            }
            else
            {
                float progress = newValue / 1000f;
                change(progress);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

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
            stop(true);
            binding.appBar.addOnOffsetChangedListener(null);
            int now = (int)(System.currentTimeMillis()) / 1000;
            int timeListened = now - startDate;
            if (timeListened == 0)
            {
                timeListened = 1;
            }
            Constants.getInstance().addToListened(sound, timeListened);
            if (animateEnd)
            {
                binding.appBar.setExpanded(true, true);
                binding.scroll.smoothScrollTo(0, 0);
                Animation anim = new AlphaAnimation(0.8f, 0f);
                anim.setFillAfter(true);
                anim.setDuration(getResources().getInteger(R.integer.anim_alpha));
                binding.toolbar.startAnimation(disappear);
                binding.toolbar.setVisibility(View.INVISIBLE);
                binding.headContent.startAnimation(disappear);
                binding.headContent.setVisibility(View.INVISIBLE);
                binding.scrollDown.startAnimation(disappear);
                binding.scrollDown.setVisibility(View.INVISIBLE);
                binding.layer.startAnimation(anim);
                binding.kenBurns.startAnimation(disappear);
                binding.kenBurns.setVisibility(View.INVISIBLE);
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
                    waitHandler.postDelayed(this::supportFinishAfterTransition, getResources().getInteger(R.integer.anim_alpha));
                }
            }
            else
            {
                finish();
            }
        }
    }
}
