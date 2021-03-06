package dae.mob123.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dae.mob123.model.util.CoordinateConverter;
import dae.mob123.model.util.MuralTypeConverter;

/*
Author: EB
Class is made abstract so that override methods mustn't be added manually
*/
@Database(entities = {Mural.class}, version = 1, exportSchema = false)
@TypeConverters({CoordinateConverter.class, MuralTypeConverter.class})
public abstract class MuralDatabase extends RoomDatabase {

    /*Singleton: one static intance so there is only one connection to the database*/
    private static MuralDatabase instance;
    public static final String DATABASE_NAME = "murals.db";
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    /*Static instance must be called from anywhere in the app*/
    public static MuralDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context,
                    MuralDatabase.class,
                    DATABASE_NAME).build();
        }
        return instance;
    }
    /*Connection with DAO, which provides the queries that can be made to the database*/
    public abstract MuralDAO getRepoDao();
}
