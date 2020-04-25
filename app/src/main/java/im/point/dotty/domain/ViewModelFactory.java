package im.point.dotty.domain;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public ViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(application);
        } else if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return  (T) new AuthViewModel(application);
        } else {
            throw new IllegalArgumentException(modelClass.getSimpleName() + " can not be constructed by this factory");
        }
    }
}
