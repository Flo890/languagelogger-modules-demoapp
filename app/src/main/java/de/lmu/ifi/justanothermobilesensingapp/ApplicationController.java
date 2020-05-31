package de.lmu.ifi.justanothermobilesensingapp;

import android.app.Application;

import de.lmu.ifi.researchime.contentabstraction.RIMEInputContentProcessingController;
import de.lmu.ifi.researchime.contentextraction.DbInitializer;

/**
 * We need an ApplicationController to init the database on app (re)boot
 * Mind to register this controller in the Manifest!
 */
public class ApplicationController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RIMEInputContentProcessingController.initDatabase(getApplicationContext());
        DbInitializer.initRimeContentExtractionDb(getApplicationContext());
    }
}
