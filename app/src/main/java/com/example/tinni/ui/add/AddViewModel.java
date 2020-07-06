package com.example.tinni.ui.add;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Category;
import com.example.tinni.models.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Add ViewModel</h1>
 * ViewModel for the add ui
 *
 * Variables:
 * ObservableField<Uri> uri: The uri of the selected audio file
 * ObservableInt length: The length of the selected audio file
 * ObservableField<String> title: The title of the selected audio file
 * List<Category> categories: A list of all categories
 * ObservableField<String> The description of the selected audio file
 * ObservableField<Uri> image: The image of the selected audio file
 * ObservableBoolean loading: Loading indicator while uploading
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   21.06.2020
 */

public class AddViewModel extends ViewModel
{
    public ObservableField<String> uri = new ObservableField<>();
    public ObservableInt length = new ObservableInt();
    public ObservableField<String> title = new ObservableField<>();
    public List<Category> categories = new ArrayList<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> image = new ObservableField<>();
    public ObservableBoolean loading = new ObservableBoolean();

    /**
     * <h2>Save Async Task</h2>
     * Async Task to save new sound file and store them in the SharedPreferences
     * Sound might be saved to a server in the future hence the Async Task
     *
     * Arguments:
     * List<Category> categories: The list of all categories
     * ObservableField<String> title: The title of the uploaded sound file
     * ObservableField<String> description: The description of the uploaded sound file
     * ObservableField<Uri> uri: The uri of the uploaded sound file
     * ObservableField<Bitmap> bitmap: The bitmap of the uploaded sound file
     * Add.onSubmitResult delegate: The delegate catching the result
     *
     */

    public static class saveAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        List<Category> categories;
        ObservableField<String> title;
        ObservableField<String> description;
        ObservableField<String> uri;
        ObservableField<String> image;
        ObservableInt length;
        Add.onSubmitResult delegate;

        public saveAsyncTask(List<Category> _categories, ObservableField<String> _title, ObservableField<String> _description, ObservableField<String> _uri, ObservableField<String> _image, ObservableInt _length, Add.onSubmitResult _onSubmitResult)
        {
            categories = _categories;
            title = _title;
            description = _description;
            uri = _uri;
            image = _image;
            length = _length;
            delegate = _onSubmitResult;
        }
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            List<Category> chosenCategories = categories.stream().filter(p -> p.active.get()).collect(Collectors.toList());
            Sound newSound = new Sound((int)(System.currentTimeMillis()/1000), true, title.get(), description.get(), length.get(), 0, chosenCategories, 0, uri.get(), image.get());
            Constants.getInstance().addCustomSound(newSound);
            return true;
        }

        protected void onPostExecute(Boolean res)
        {
            delegate.result(res);
        }
    }

    /**
     * <h2>Clear Text</h2>
     * Clears the given Observable String
     *
     * Arguments:
     * @param txt: The text to clear

     */

    public void clearText (ObservableField<String> txt)
    {
        txt.set("");
    }
}
