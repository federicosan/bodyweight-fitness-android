package io.mazur.fit.presenter;

import org.joda.time.DateTime;

import java.util.Locale;

import io.mazur.fit.R;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.stream.CalendarStream;
import io.mazur.fit.stream.DrawerStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.DateUtils;
import io.mazur.fit.view.ToolbarView;

public class ToolbarPresenter extends IPresenter<ToolbarView> {
    private Integer mId = R.id.action_menu_home;

    @Override
    public void onRestoreInstanceState(ToolbarView view) {
        super.onRestoreInstanceState(view);

        setToolbarContent(mId);
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(RoutineStream.getInstance()
                .getExerciseObservable()
                .filter(exercise -> mId.equals(R.id.action_menu_home))
                .subscribe(this::setToolbarForHome));

        subscribe(CalendarStream.getInstance()
                .getCalendarDayChangedObservable()
                .filter(exercise -> mId.equals(R.id.action_menu_workout_log))
                .subscribe(this::setToolbarForCalendar));

        subscribe(DrawerStream.getInstance()
                .getMenuObservable()
                .filter(id -> id.equals(R.id.action_menu_home) || id.equals(R.id.action_menu_workout_log))
                .subscribe(id -> {
                    mId = id;

                    setToolbarContent(mId);
                }));
    }

    private void setToolbarContent(Integer id) {
        if (id.equals(R.id.action_menu_home)) {
            Exercise exercise = RoutineStream.getInstance().getExercise();

            setToolbarForHome(exercise);
        } else if (id.equals(R.id.action_menu_workout_log)) {
            CalendarDayChanged calendarDayChanged = CalendarStream.getInstance().getCalendarDayChanged();

            setToolbarForCalendar(calendarDayChanged);
        }
    }

    private void setToolbarForHome(Exercise exercise) {
        if (exercise == null) {
            exercise = RoutineStream.getInstance().getExercise();
        }

        mView.invalidateOptionsMenu();

        mView.setTitle(exercise.getTitle());
        mView.setSubtitle(exercise.getSection().getTitle());
        mView.setDescription(exercise.getDescription());
    }

    private void setToolbarForCalendar(CalendarDayChanged calendarDayChanged) {
        DateTime dateTime;

        if (calendarDayChanged == null) {
            dateTime = new DateTime();
        } else {
            calendarDayChanged = CalendarStream.getInstance().getCalendarDayChanged();

            if (calendarDayChanged == null) {
                dateTime = new DateTime();
            } else {
                dateTime = DateUtils.getDate(
                        calendarDayChanged.presenterSelected,
                        calendarDayChanged.daySelected
                );
            }
        }

        mView.inflateCalendarMenu();
        mView.setTitle(dateTime.toString("MMMM", Locale.ENGLISH));
        mView.setSubtitle(dateTime.toString("YYYY", Locale.ENGLISH));
        mView.setDescription("");
    }
}
