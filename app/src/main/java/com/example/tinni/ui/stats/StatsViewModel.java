package com.example.tinni.ui.stats;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.R;
import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.models.Rating;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;
import com.example.tinni.models.Sound;
import com.example.tinni.models.SoundStat;
import com.example.tinni.models.Stat;
import com.example.tinni.ui.sounds.SoundsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * <h1>Stats ViewModel</h1>
 * ViewModel for the stats ui
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   09.07.2020
 */

public class StatsViewModel extends ViewModel
{
    public ObservableBoolean loading = new ObservableBoolean(true);
    public MutableLiveData<List<Rating>> ratings = new MutableLiveData<>();
    public MutableLiveData<List<SelectedProgram>> programs = new MutableLiveData<>();
    public List<SelectedProgram> allPrograms = new ArrayList<>();
    public MutableLiveData<List<Stat>> stats = new MutableLiveData<>();

    public MutableLiveData<List<Rating>> getRatings()
    {
        return ratings;
    }

    public MutableLiveData<List<SelectedProgram>> getPrograms()
    {
        return programs;
    }

    public MutableLiveData<List<Stat>> getStats()
    {
        return stats;
    }

    /**
     * <h2>Prepare</h2>
     *
     * Sets up the data for the StatsFragment
     *
     * Source 1: https://stackoverflow.com/a/37659716/2700965
     * Source 2: https://stackoverflow.com/a/29671501/2700965
     * Source 3: https://stackoverflow.com/a/31021873/2700965
     *
     */

    public void prepare (Context context)
    {
        boolean update = false;
        if ((ratings.getValue() == null || ratings.getValue().size() == 0 || Constants.getInstance().ratings.size() > ratings.getValue().size()) && Constants.getInstance().ratings.size() > 0)
        {
            List<Rating> newRatings = Constants.getInstance().ratings.stream().limit(Constants.getInstance().ratingsLimit).collect(Collectors.toList());
            Collections.reverse(newRatings);
            ratings.setValue(newRatings);
            update = true;
        }
        if ((programs.getValue() == null || programs.getValue().size() == 0 || Constants.getInstance().pastPrograms.size() > allPrograms.size()) && Constants.getInstance().pastPrograms.size() > 0)
        {
            List<SelectedProgram> newPrograms = Constants.getInstance().pastPrograms.stream().limit(Constants.getInstance().limit).collect(Collectors.toList());
            Collections.reverse(newPrograms);
            allPrograms = new ArrayList<>(Constants.getInstance().pastPrograms);
            programs.setValue(newPrograms);
            update = true;
        }

        if (stats.getValue() == null || stats.getValue().size() == 0 || update)
        {
            List<Stat> newStats = new ArrayList<>();
            long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Constants.getInstance().installed);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
            String date = formatter.format(new Date( Constants.getInstance().installed));
            newStats.add(new Stat(days, context.getResources().getString(R.string.days_installed), String.format(context.getResources().getString(R.string.since_date), date)));

            long listened = Constants.getInstance().listened.size();
            List<SoundStat> different = Constants.getInstance().listened.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(SoundStat::getId))), ArrayList::new));
            newStats.add(new Stat(listened, context.getResources().getString(R.string.sounds_listened), String.format(context.getResources().getString(R.string.different), different.size())));

            long programsDone = Constants.getInstance().pastPrograms.size();
            List<SelectedProgram> differentPrograms = Constants.getInstance().pastPrograms.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(x -> x.getProgram().getId()))), ArrayList::new));
            newStats.add(new Stat(programsDone, context.getResources().getString(R.string.programs_completed), String.format(context.getResources().getString(R.string.different), differentPrograms.size())));

            long ownPrograms = Constants.getInstance().programs.stream().filter(Program::isCustom).count();
            long ownCompleted = Constants.getInstance().pastPrograms.stream().filter(x -> x.getProgram().isCustom()).count();
            newStats.add(new Stat(ownPrograms, context.getResources().getString(R.string.own_programs), String.format(context.getResources().getString(R.string.completed), ownCompleted)));

            List<Sound> ownSounds = Constants.getInstance().sounds.stream().filter(Sound::isCustom).collect(Collectors.toList());
            long ownListened = 0;

            for (SoundStat ss : Constants.getInstance().listened)
            {
                Sound s = ownSounds.stream().filter(x -> x.getId() == ss.getId()).findFirst().orElse(null);
                if (s != null)
                {
                    ownListened++;
                }
            }
            newStats.add(new Stat(ownSounds.size(), context.getResources().getString(R.string.own_sounds), String.format(context.getResources().getString(R.string.listened), ownListened)));

            long ratingsDone = Constants.getInstance().ratings.size();
            OptionalDouble average = Constants.getInstance().ratings.stream().mapToDouble(Rating::getRating).average();
            double avgRating = 0;
            if(average.isPresent())
            {
                avgRating = average.getAsDouble();
            }
            newStats.add(new Stat(ratingsDone, context.getResources().getString(R.string.ratings_done), String.format(context.getResources().getString(R.string.ratings_average), avgRating)));

            long minutesSum  = Constants.getInstance().listened.stream().mapToLong(SoundStat::getTime).sum();
            OptionalDouble averageMinutes = Constants.getInstance().listened.stream().mapToDouble(SoundStat::getTime).average();
            double avgMinutes = 0;
            if(averageMinutes.isPresent())
            {
                avgMinutes = averageMinutes.getAsDouble();

            }
            newStats.add(new Stat(minutesSum, context.getResources().getString(R.string.listened_minutes), String.format(context.getResources().getString(R.string.minutes_daily), (int)avgMinutes)));

            stats.setValue(newStats);
        }
        loading.set(false);
    }
}
