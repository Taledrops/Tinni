package com.example.tinni.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.format.DateUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.databinding.ObservableArrayList;

import com.example.tinni.R;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Category;
import com.example.tinni.models.Note;
import com.example.tinni.models.Program;
import com.example.tinni.models.Question;
import com.example.tinni.models.Rating;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;
import com.example.tinni.models.Sound;
import com.example.tinni.models.SoundStat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * <h1>Constants</h1>
 * Constant variables to use through the whole app
 *
 * Variables:
 * List<Integer> favorites: A list of all favorited sound IDs
 * Picasso picasso: The main instance of the picasso library
 * List<Category> categories: A list of all categories loaded inside the SoundViewModel
 * List<Category> categories: A list of all selected categories in the SoundsFragment (Shared Preferences)
 * List<String> allowedFileTypes: Allowed file types for user uploaded sound files
 * List<SelectedProgram> pastPrograms: History of programs completed
 * List<Integer> listened: A list of all the sounds listened to
 * List<Sound> customSounds: A list of all custom sounds uploaded by the user (Shared Preferences)
 * List<Program> customPrograms: A list of all custom programs uploaded by the user (Shared Preferences)
 * List<Sound> sounds: A list containing all sounds
 * List<Program> programs: A list containing all programs
 * String allowedFileTypesString: Allowed file types to display in Toast
 * SharedPreferences preferences: The Shared Preferences instance
 * Sound soundToAdd: Temporary storage of recently added file to add to the SoundsFragment Recyclerview
 * Program programToAdd: Temporary storage of recently added file to add to the ProgramsFragment Recyclerview
 * Sound soundToRemove: Temporary storage of recently deleted file to remove from the SoundsFragment Recyclerview
 * Program programToRemove: Temporary storage of recently deleted file to remove from the ProgramsFragment Recyclerview
 * Sound currentSound: The recently selected Sound object
 * Program currentProgram: The recently selected Program object
 * Session updateSession: The session to update after completion
 * Program selectedProgram: The Program object currently done by the user
 * List<Rating> ratings: The list of daily ratings
 * List<Notes> notes: The list of personal notes
 * int changedProgram: The ID of the program recently changed to update its active indicator
 * int limit: Limit for retrieving list items (preview)
 * int ratingsLimit: Limit for retrieving list items (ratings, 14 days)
 * long installed: Date of the first install of the app
 * Gson gson: The instance of the Gson Library
 * float volume: The saved volume setting
 * Constants instance: The current instance of this class
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   19.06.2020
 */

public class Constants
{
    public Picasso picasso;
    public List<Integer> favorites = new ArrayList<>();
    public List<Category> categories = new ArrayList<>();
    public List<Integer> selectedCategories = new ArrayList<>();
    public List<String> allowedFileTypes = new ArrayList<>();
    public List<SelectedProgram> pastPrograms = new ArrayList<>();
    public List<SoundStat> listened = new ArrayList<>();
    public List<Sound> customSounds = new ArrayList<>();
    public List<Program> customPrograms = new ArrayList<>();
    public List<Sound> sounds = new ArrayList<>();
    public List<Program> programs = new ArrayList<>();
    public String allowedFileTypesString = ".mp3, .mp4, .wav, .ogg, .3gp";
    public SharedPreferences preferences;
    public Sound soundToAdd = null;
    public Program programToAdd = null;
    public Sound soundToRemove = null;
    public Program programToRemove = null;
    public Session updateSession = null;
    public SelectedProgram selectedProgram = null;
    public List<Rating> ratings = new ArrayList<>();
    public List<Note> notes = new ArrayList<>();
    public int changedProgram = 0;
    public int limit = 10;
    public int ratingsLimit = 14;
    public long installed = System.currentTimeMillis();
    public final Gson gson = new Gson();
    public float volume;
    public static final Constants instance = new Constants();
    private boolean init = false;

    /**
     * <h2>Get Instance</h2>
     * Get the current instance of this class
     */

    public static Constants getInstance()
    {
        return instance;
    }

    /**
     * <h2>Init</h2>
     * Initialize default values
     * Retrieve custom sounds from Shared Preferences
     * Retrieve selected categories from Shared Preferences
     * Fill sounds list
     * Fill categories list
     *
     * Source 1: https://stackoverflow.com/a/28107791/2700965
     * Source 2: https://stackoverflow.com/a/5311917/2700965
     */

    public void init (Context context)
    {
        if (!init)
        {
            init = true;
            picasso = new Picasso.Builder(context).build();

            try
            {
                installed = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }

            allowedFileTypes.add("audio/mpeg");
            allowedFileTypes.add("audio/mp4");
            allowedFileTypes.add("video/mp4");
            allowedFileTypes.add("audio/mp3");
            allowedFileTypes.add("audio/wav");
            allowedFileTypes.add("audio/ogg");

            volume = context.getResources().getInteger(R.integer.default_volume);

            preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

            //preferences.edit().clear().apply();
            //preferences.edit().remove("program").apply();
            //preferences.edit().remove("pastprograms").apply();
            //preferences.edit().remove("ratings").apply();

            String serializedRatings = preferences.getString("ratings", null);
            if (serializedRatings != null)
            {
                Type type = new TypeToken<List<Rating>>()
                {
                }.getType();
                ratings = gson.fromJson(serializedRatings, type);
            }

            String serializedCustom = preferences.getString("custom", null);
            if (serializedCustom != null)
            {
                Type type = new TypeToken<List<Sound>>()
                {
                }.getType();
                customSounds = gson.fromJson(serializedCustom, type);
            }

            String serializedListened = preferences.getString("listened", null);
            if (serializedListened != null)
            {
                Type type = new TypeToken<List<SoundStat>>()
                {
                }.getType();
                listened = gson.fromJson(serializedListened, type);
            }

            String serializedFavorites = preferences.getString("favorites", null);
            if (serializedFavorites != null)
            {
                Type type = new TypeToken<List<Integer>>()
                {
                }.getType();
                favorites = gson.fromJson(serializedFavorites, type);
            }

            String serializedCategories = preferences.getString("categories", null);
            if (serializedCategories != null)
            {
                Type type = new TypeToken<List<Integer>>()
                {
                }.getType();
                selectedCategories = gson.fromJson(serializedCategories, type);
            }

            String serializedProgram = preferences.getString("program", null);
            if (serializedProgram != null)
            {
                Type type = new TypeToken<SelectedProgram>()
                {
                }.getType();
                selectedProgram = gson.fromJson(serializedProgram, type);
            }

            String serializedPastPrograms = preferences.getString("pastprograms", null);
            if (serializedPastPrograms != null)
            {
                Type type = new TypeToken<List<SelectedProgram>>()
                {
                }.getType();
                pastPrograms = gson.fromJson(serializedPastPrograms, type);
            }

            String serializedCustomPrograms = preferences.getString("customprograms", null);
            if (serializedCustomPrograms != null)
            {
                Type type = new TypeToken<List<Program>>()
                {
                }.getType();
                customPrograms = gson.fromJson(serializedCustomPrograms, type);
            }

            String serializedNotes = preferences.getString("notes", null);
            if (serializedNotes != null)
            {
                Type type = new TypeToken<List<Note>>()
                {
                }.getType();
                notes = gson.fromJson(serializedNotes, type);
            }

            categories.add(new Category(4, context.getString(R.string.nature), false, false, false, Constants.getInstance().selectedCategories.contains(4)));
            categories.add(new Category(5, context.getString(R.string.white_noise), false, false, false, Constants.getInstance().selectedCategories.contains(5)));
            categories.add(new Category(6, context.getString(R.string.calm), false, false, false, Constants.getInstance().selectedCategories.contains(6)));
            categories.add(new Category(7, context.getString(R.string.urban), false, false, false, Constants.getInstance().selectedCategories.contains(7)));
            categories.add(new Category(8, context.getString(R.string.loud), false, false, false, Constants.getInstance().selectedCategories.contains(8)));
            categories.add(new Category(9, context.getString(R.string.quiet), false, false, false, Constants.getInstance().selectedCategories.contains(9)));
            categories.add(new Category(10, context.getString(R.string.exciting), false, false, false, Constants.getInstance().selectedCategories.contains(10)));
            categories.add(new Category(11, context.getString(R.string.melodic), false, false, false, Constants.getInstance().selectedCategories.contains(13)));

            if (customSounds.size() > 0)
            {
                sounds.addAll(Constants.getInstance().customSounds);
            }

            List<Category> soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(0));
            soundCategories.add(categories.get(2));
            soundCategories.add(categories.get(5));

            sounds.add(new Sound(2131755008, false, "Beach Waves", "A beautiful loop of beach waves", 60, R.drawable.beach, soundCategories, "android.resource://com.example.tinni/" + R.raw.beachwavesloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(1));
            soundCategories.add(categories.get(4));

            sounds.add(new Sound(2131755016, false, "Broken Record", "The sound of a broken record", 74, R.drawable.record, soundCategories, "android.resource://com.example.tinni/" + R.raw.record, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(3));
            soundCategories.add(categories.get(4));

            sounds.add(new Sound(2131755009, false, "Church Bell", "Can you hear the bell ringing?", 60, R.drawable.churchbell, soundCategories, "android.resource://com.example.tinni/" + R.raw.churchbellsloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(0));
            soundCategories.add(categories.get(1));
            soundCategories.add(categories.get(2));
            soundCategories.add(categories.get(5));

            sounds.add(new Sound(2131755010, false, "Cicada", "A hot summer night melody", 60, R.drawable.cicada, soundCategories, "android.resource://com.example.tinni/" + R.raw.cicadaloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(3));
            soundCategories.add(categories.get(4));
            soundCategories.add(categories.get(6));

            sounds.add(new Sound(2131755011, false, "Crowd Babble", "A lot of people talking for you to listen", 60, R.drawable.crowd, soundCategories, "android.resource://com.example.tinni/" + R.raw.crowdbabble2loop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(2));

            sounds.add(new Sound(2131755012, false, "Didgeridoo", "A traditional calming instrument", 60, R.drawable.didgeridoo, soundCategories, "android.resource://com.example.tinni/" + R.raw.didgeridooloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(3));
            soundCategories.add(categories.get(4));
            soundCategories.add(categories.get(6));

            sounds.add(new Sound(2131755013, false, "Laughing Kids", "The sound of kids laughing", 60, R.drawable.kidslaughing, soundCategories, "android.resource://com.example.tinni/" + R.raw.laughingkidsloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(3));
            soundCategories.add(categories.get(5));
            soundCategories.add(categories.get(7));

            sounds.add(new Sound(2131755014, false, "Notched Meditation", "A melodic meditation music", 60, R.drawable.notchedmeditation, soundCategories, "android.resource://com.example.tinni/" + R.raw.notchedmeditationloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(0));
            soundCategories.add(categories.get(2));
            soundCategories.add(categories.get(5));

            sounds.add(new Sound(2131755016, false, "Rainy Mood", "A beautiful rain loop", 60, R.drawable.rain, soundCategories, "android.resource://com.example.tinni/" + R.raw.rainloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(3));
            soundCategories.add(categories.get(4));
            soundCategories.add(categories.get(6));

            sounds.add(new Sound(2131755017, false, "Screaming Kids", "The sound of kids screaming", 60, R.drawable.screamingkids, soundCategories, "android.resource://com.example.tinni/" + R.raw.screamingkidsloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(0));
            soundCategories.add(categories.get(2));

            sounds.add(new Sound(2131755018, false, "Seagulls", "The sound of seagulls by the sea", 60, R.drawable.seagull, soundCategories, "android.resource://com.example.tinni/" + R.raw.seagullsloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(2));
            soundCategories.add(categories.get(5));

            sounds.add(new Sound(2131755019, false, "Hot Shower", "The sound of a hot and cozy shower", 60, R.drawable.shower, soundCategories, "android.resource://com.example.tinni/" + R.raw.showerloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(0));
            soundCategories.add(categories.get(2));

            sounds.add(new Sound(2131755020, false, "Sparrows", "The sound of sparrows in the woods", 60, R.drawable.sparrows, soundCategories, "android.resource://com.example.tinni/" + R.raw.sparrowsloop, null));

            soundCategories = new ArrayList<>();
            soundCategories.add(categories.get(0));
            soundCategories.add(categories.get(2));

            sounds.add(new Sound(2131755021, false, "Waterfall", "The sound of a waterfall in the mountains", 60, R.drawable.waterfall, soundCategories, "android.resource://com.example.tinni/" + R.raw.waterfallloop, null));

            Collections.sort(sounds, (obj1, obj2) -> obj1.getTitle().compareToIgnoreCase(obj2.getTitle()));

            if (customPrograms.size() > 0)
            {
                programs.addAll(Constants.getInstance().customPrograms);
            }

            List<Session> programSessions = new ArrayList<>();
            programSessions.add(new Session(1, true, sounds.get(0), 180, false));
            programSessions.add(new Session(2, true, sounds.get(2), 70, false));
            programSessions.add(new Session(3, true, sounds.get(5), 240, false));
            programSessions.add(new Session(4, true, sounds.get(3), 120, false));
            programSessions.add(new Session(5, true, sounds.get(7), 300, false));
            programSessions.add(new Session(6, true, sounds.get(1), 360, false));
            programSessions.add(new Session(7, true, sounds.get(2), 60, false));
            programSessions.add(new Session(8, true, sounds.get(0), 180, false));
            programSessions.add(new Session(9, true, sounds.get(4), 180, false));

            List<Question> programQuestions = new ArrayList<>();
            ObservableArrayList<Answer> answers = new ObservableArrayList<>();

            answers.add(new Answer(1, "Daily or almost daily", false));
            answers.add(new Answer(2, "Almost weekly", false));
            answers.add(new Answer(3, "Almost monthly", false));
            answers.add(new Answer(4, "Every few months", false));
            answers.add(new Answer(5, "Yearly", false));
            programQuestions.add(new Question(1, "How often do you have tinnitus on average?", answers, false));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(6, "Constant: You can always or usually hear it in a quiet room", false));
            answers.add(new Answer(7, "Intermittent: 'comes and goes', cannot always be heart in a quiet room", false));
            programQuestions.add(new Question(2, "What describes your tinnitus best during the day?", answers, false));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(8, "Tonal", false));
            answers.add(new Answer(9, "Noise-like", false));
            answers.add(new Answer(10, "Music-like", false));
            answers.add(new Answer(11, "Crickets-like", false));
            answers.add(new Answer(12, "Other", false));
            programQuestions.add(new Question(3, "What does your tinnitus sound like?", answers, true));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(13, "Stable", false));
            answers.add(new Answer(14, "Sometimes fluctuating", false));
            answers.add(new Answer(15, "Always fluctuating", false));
            answers.add(new Answer(16, "I don't know", false));
            programQuestions.add(new Question(4, "Is the volume of your tinnitus stable over time or does it fluctuate during the day?", answers, false));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(17, "Right ear", false));
            answers.add(new Answer(18, "Left ear", false));
            answers.add(new Answer(19, "Both ears", false));
            answers.add(new Answer(20, "Inside the head", false));
            answers.add(new Answer(21, "Always different", false));
            programQuestions.add(new Question(5, "Where do you perceive your tinnitus?", answers, true));

            programs.add(new Program(1, false, "Natural stimulation", "Natural background noise can help with mild symptoms. This program is aimed at patients with mild to moderate symptoms.", R.drawable.nature, programSessions, programQuestions, 7, false, null));

            programQuestions = new ArrayList<>();

            answers = new ObservableArrayList<>();
            answers.add(new Answer(1, "No", false));
            answers.add(new Answer(2, "Yes, following my heart beat", false));
            answers.add(new Answer(3, "Yes, following my breathing", false));
            answers.add(new Answer(4, "I don't know", false));
            programQuestions.add(new Question(1, "Is your tinnitus rhythmic?", answers, false));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(5, "Very quiet environment", false));
            answers.add(new Answer(6, "Low intensity sounds", false));
            answers.add(new Answer(7, "High intensity sounds", false));
            answers.add(new Answer(8, "Head movements", false));
            answers.add(new Answer(9, "Taking a nap", false));
            answers.add(new Answer(10, "Driving", false));
            answers.add(new Answer(11, "I don't know", false));
            programQuestions.add(new Question(2, "What reduces the symptoms of your tinnitus?", answers, true));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(12, "Tonal", false));
            answers.add(new Answer(13, "Noise-like", false));
            answers.add(new Answer(14, "Music-like", false));
            answers.add(new Answer(15, "Crickets-like", false));
            answers.add(new Answer(16, "Other", false));
            programQuestions.add(new Question(3, "What does your tinnitus sound like?", answers, true));

            programSessions = new ArrayList<>();
            programSessions.add(new Session(1, true, sounds.get(3), 120, false));
            programSessions.add(new Session(2, true, sounds.get(7), 300, false));
            programSessions.add(new Session(3, true, sounds.get(1), 360, false));
            programSessions.add(new Session(4, true, sounds.get(0), 180, false));
            programSessions.add(new Session(5, true, sounds.get(4), 180, false));

            programs.add(new Program(2, false, "After work stimulation", "Studies show that tinnitus patients experience particularly strong symptoms after work. This program is intended to help alleviate these symptoms and thus enable a relaxing time at home.", R.drawable.work, programSessions, programQuestions, 3, false, null));

            programQuestions = new ArrayList<>();

            answers = new ObservableArrayList<>();
            answers.add(new Answer(1, "Yes", false));
            answers.add(new Answer(2, "No", false));
            answers.add(new Answer(3, "I don't know", false));
            programQuestions.add(new Question(1, "Have you seen a doctor over the past year?", answers, false));

            answers = new ObservableArrayList<>();
            answers.add(new Answer(4, "Very quiet environment", false));
            answers.add(new Answer(5, "Low intensity sounds", false));
            answers.add(new Answer(6, "High intensity sounds", false));
            answers.add(new Answer(7, "Head movements", false));
            answers.add(new Answer(8, "Taking a nap", false));
            answers.add(new Answer(9, "Driving", false));
            answers.add(new Answer(10, "I don't know", false));
            programQuestions.add(new Question(2, "What increases the symptoms of your tinnitus?", answers, true));

            programSessions = new ArrayList<>();
            programSessions.add(new Session(1, true, sounds.get(7), 1200, false));
            programSessions.add(new Session(2, true, sounds.get(1), 800, false));

            programs.add(new Program(3, false, "Late night stimulation", "The night's rest is very important for people. People with tinnitus often have the problem that their symptoms are particularly noticeable at night. This program is designed to counteract exactly this effect.", R.drawable.night, programSessions, programQuestions, 1, false, null));

            if (selectedProgram != null && selectedProgram.getProgram() != null)
            {
                for (Program p : programs)
                {
                    if (p.getId() == selectedProgram.getProgram().getId())
                    {
                        p.active.set(true);
                    }
                    else
                    {
                        p.active.set(false);
                    }
                }

                System.out.println();
            }

            Collections.sort(programs, (obj1, obj2) -> obj1.getTitle().compareToIgnoreCase(obj2.getTitle()));

            /*
            long day = 86400000;
            for (int i = 10; i >= 1; i--)
            {
                ratings.add(new Rating(ThreadLocalRandom.current().nextInt(1, 5 + 1), System.currentTimeMillis() - (i * day), "Hello " + i));
            }
            SharedPreferences.Editor editor = preferences.edit();
            String json = gson.toJson(ratings);
            editor.putString("ratings", json);
            editor.apply();

             */
        }
    }

    /**
     * <h2>Add Rating</h2>
     * Add daily rating
     *
     * @param rating The rating from 1 to 5
     *
     * Source: https://stackoverflow.com/a/28107791/2700965
     */

    public void addRating (int rating, String text)
    {
        Rating lastRating = wasLastRatingToday();
        if (lastRating != null)
        {
            lastRating.setRating(rating);
            lastRating.setText(text);
        }
        else
        {
            ratings.add(new Rating(rating, System.currentTimeMillis(), text));
        }

        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(ratings);
        editor.putString("ratings", json);
        editor.apply();
    }

    /**
     * <h2>Was Last Rating Today</h2>
     * Checks if the last element of the ratings is from today
     *
     * Source: https://stackoverflow.com/a/16146263/2700965
     *
     */

    public Rating wasLastRatingToday ()
    {
        if (ratings.size() > 0)
        {
            Rating lastRating = ratings.get(ratings.size() - 1);
            if (lastRating != null)
            {
                if (DateUtils.isToday(lastRating.getDate()))
                {
                    return lastRating;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
           return null;
        }
    }

    /**
     * <h2>Add Note</h2>
     * Add custom note to SharedPreferences and list
     *
     * @param note The note to add
     *
     * Source: https://stackoverflow.com/a/28107791/2700965
     */

    public void addNote (Note note, boolean edit)
    {
        if (!edit)
        {
            notes.add(note);
        }
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(notes);
        editor.putString("notes", json);
        editor.apply();
    }

    /**
     * <h2>Add Custom Sound</h2>
     * Add custom sound to SharedPreferences and list
     *
     * @param sound The sound to add
     *
     * Source: https://stackoverflow.com/a/28107791/2700965
     */

    public void addCustomSound (Sound sound)
    {
        soundToAdd = sound;
        customSounds.add(sound);
        sounds.add(sound);
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(customSounds);
        editor.putString("custom", json);
        editor.apply();
    }

    /**
     * <h2>Remove Custom Sound</h2>
     * Remove custom sound from SharedPreferences and list
     *
     * @param sound The sound to add
     *
     */

    public void removeCustomSound (Sound sound)
    {
        soundToRemove = sound;
        Sound sc = customSounds.stream().filter(x -> x.getId() == sound.getId()).findFirst().orElse(null);
        if (sc != null)
        {
            customSounds.remove(sc);
            if (customSounds.size() > 0)
            {
                SharedPreferences.Editor editor = preferences.edit();
                String json = gson.toJson(customSounds);
                editor.putString("custom", json);
                editor.apply();
            }
            else
            {
                preferences.edit().remove("custom").apply();
            }
        }

        sounds.stream().filter(x -> x.getId() == sound.getId()).findFirst().ifPresent(s -> sounds.remove(s));

        for (Program p : customPrograms)
        {
            if (p.getSessions() != null && p.getSessions().size() > 0)
            {
                List<Session> sessions = p.getSessions().stream().filter(x -> x.getSound().getId() == sound.getId()).collect(Collectors.toList());
                for (Session s : sessions)
                {
                    p.getSessions().remove(s);
                }
            }
        }

        for (Program p : programs)
        {
            if (p.getSessions() != null && p.getSessions().size() > 0)
            {
                List<Session> sessions = p.getSessions().stream().filter(x -> x.getSound().getId() == sound.getId()).collect(Collectors.toList());
                for (Session s : sessions)
                {
                    p.getSessions().remove(s);
                }
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(customPrograms);
        editor.putString("customprograms", json);
        editor.apply();

        for (SelectedProgram sp : pastPrograms)
        {
            if (sp.getProgram() != null && sp.getProgram().getSessions() != null && sp.getProgram().getSessions().size() > 0)
            {
                List<Session> sessions = sp.getProgram().getSessions().stream().filter(x -> x.getSound().getId() == sound.getId()).collect(Collectors.toList());
                for (Session s : sessions)
                {
                    sp.getProgram().getSessions().remove(s);
                }
            }
        }

        editor = preferences.edit();
        json = gson.toJson(pastPrograms);
        editor.putString("pastprograms", json);
        editor.apply();
    }

    /**
     * <h2>Add Custom Program</h2>
     * Add custom program to SharedPreferences and list
     *
     * @param program The program to add
     *
     * Source: https://stackoverflow.com/a/28107791/2700965
     */

    public void addCustomProgram (Program program, boolean add)
    {
        if (add)
        {
            programToAdd = program;
        }
        customPrograms.add(program);
        programs.add(program);
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(customPrograms);
        editor.putString("customprograms", json);
        editor.apply();
    }

    /**
     * <h2>Remove Custom Program</h2>
     * Remove custom program from SharedPreferences and list
     *
     * @param program The program to add
     *
     */

    public void removeCustomProgram (Program program)
    {
        programToRemove = program;
        Program pc = customPrograms.stream().filter(x -> x.getId() == program.getId()).findFirst().orElse(null);
        if (pc != null)
        {
            customPrograms.remove(pc);
            if (customPrograms.size() > 0)
            {
                SharedPreferences.Editor editor = preferences.edit();
                String json = gson.toJson(customPrograms);
                editor.putString("customprograms", json);
                editor.apply();
            }
            else
            {
                preferences.edit().remove("customprograms").apply();
            }

            if (selectedProgram != null && selectedProgram.getProgram() != null)
            {
                if (selectedProgram.getProgram().getId() == program.getId())
                {
                    deleteSelectedProgram();
                }
            }

            pastPrograms.removeIf(x -> x.getProgram().getId() == program.getId());

            if (pastPrograms.size() > 0)
            {
                SharedPreferences.Editor editor = preferences.edit();
                String json = gson.toJson(pastPrograms);
                editor.putString("pastprograms", json);
                editor.apply();
            }
            else
            {
                preferences.edit().remove("pastprograms").apply();
            }
        }

        programs.stream().filter(x -> x.getId() == program.getId()).findFirst().ifPresent(p -> programs.remove(p));
    }

    /**
     * <h2>Add To listened</h2>
     * Add sound to listened list
     *
     * @param sound The sound to add
     */

    public void addToListened (Sound sound, int time)
    {
        listened.add(0, new SoundStat(sound.getId(), time, System.currentTimeMillis()));
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(listened);
        editor.putString("listened", json);
        editor.apply();
    }

    /**
     * <h2>Add To favorites</h2>
     * Add or remove sound to/from favorites list
     *
     * @param sound The sound to add
     * @return return if added or removed
     */

    public boolean addToFavorites (Sound sound)
    {
        boolean returnValue = true;
        if (!favorites.contains(sound.getId()))
        {
            favorites.add(sound.getId());
        }
        else
        {
            favorites.remove(Integer.valueOf(sound.getId()));
            returnValue = false;
        }

        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(favorites);
        editor.putString("favorites", json);
        editor.apply();

        return returnValue;
    }

    /**
     * <h2>Handle Selected Categories</h2>
     * Generate Shared Preferences for the selected categories
     *
     * Source: https://stackoverflow.com/a/28107791/2700965
     */

    public void handleSelectedCategories ()
    {
        if (selectedCategories.size() > 0)
        {
            SharedPreferences.Editor editor = preferences.edit();
            String json = gson.toJson(selectedCategories);
            editor.putString("categories", json);
            editor.apply();
        }
        else
        {
            preferences.edit().remove("categories").apply();
        }
    }

    /**
     * <h2>Handle Selected Program</h2>
     * Generate Shared Preferences for the selected program
     * Handle adding and deleting the currently selected program
     *
     */

    public SelectedProgram handleSelectedProgram (Program program)
    {
        if (selectedProgram != null && program.getId() == selectedProgram.getProgram().getId())
        {
            pastPrograms.add(selectedProgram);
            SharedPreferences.Editor editor = preferences.edit();
            String json = gson.toJson(pastPrograms);
            editor.putString("pastprograms", json);
            editor.apply();
            changedProgram = selectedProgram.getProgram().getId();
            selectedProgram.getProgram().active.set(false);
            selectedProgram = null;
            preferences.edit().remove("program").apply();
            return null;
        }
        else
        {
            program.active.set(true);
            selectedProgram = new SelectedProgram((int)(System.currentTimeMillis()) / 1000, new Program(program), System.currentTimeMillis());
            selectedProgram.sessions.add(program.getSessions().get(0));
            SharedPreferences.Editor editor = preferences.edit();
            String json = gson.toJson(selectedProgram);
            editor.putString("program", json);
            editor.apply();
            return selectedProgram;
        }
    }

    /**
     * <h2>Update Selected Program</h2>
     * Update Shared Preferences for the selected program
     *
     */

    public void updateSelectedProgram (SelectedProgram selectedProgram)
    {
        if (selectedProgram != null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            String json = gson.toJson(selectedProgram);
            editor.putString("program", json);
            editor.apply();
        }
    }

    /**
     * <h2>Delete Selected Program</h2>
     * Delete Shared Preferences for the selected program
     *
     */

    public void deleteSelectedProgram ()
    {
        if (selectedProgram != null && selectedProgram.getProgram() != null)
        {
            changedProgram = selectedProgram.getProgram().getId();
            selectedProgram.getProgram().active.set(false);
            selectedProgram = null;
            preferences.edit().remove("program").apply();
        }
    }
}
