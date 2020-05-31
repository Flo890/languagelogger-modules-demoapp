package de.lmu.ifi.justanothermobilesensingapp;

import android.content.Context;
import android.content.Intent;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import de.lmu.ifi.justanothermobilesensingapp.sync.ContentAbstractionSyncJobService;
import de.lmu.ifi.researchime.contentabstraction.RIMECallback;
import de.lmu.ifi.researchime.contentabstraction.RIMEInputContentProcessingController;
import de.lmu.ifi.researchime.contentabstraction.model.InputContent;
import de.lmu.ifi.researchime.contentabstraction.model.MessageStatistics;
import de.lmu.ifi.researchime.contentabstraction.model.abstractedcontent.AbstractedAction;
import de.lmu.ifi.researchime.contentabstraction.model.abstractedcontent.WordFrequency;
import de.lmu.ifi.researchime.contentabstraction.model.config.RIMEContentAbstractionConfig;
import de.lmu.ifi.researchime.contentabstraction.setup.SetupCompletionActivity;
import de.lmu.ifi.researchime.contentextraction.FullMessageWordExtractor;
import de.lmu.ifi.researchime.contentextraction.KeyboardMessageStatisticsGenerator;
import de.lmu.ifi.researchime.contentextraction.MessageDiffWordEventExtractor;
import de.lmu.ifi.researchime.contentextraction.model.AbstractActionEventJson;
import de.lmu.ifi.researchime.contentextraction.model.MessageStatisticsJson;
import de.lmu.ifi.researchime.contentextraction.model.WordFrequencyJson;
import de.lmu.ifi.researchime.contentextraction.model.event.Event;

/**
 * TODO this is a demo class
 */
public class LanuageLoggerCaller {

    private static final String username = "participant's username goes here";

    /**
     * a minimal example. The language logger logic is applied, without any further integration
     * (No fetching of remote configuration, no sync of result data)
     * @param context
     * @param contentChangeEvents
     * @param initialContent
     */
    public static void runLanguageLogger(final Context context, List<Event> contentChangeEvents, String initialContent) {

        // An InputContent object is used bundle all data of this processing
        InputContent inputContent = new InputContent();
         inputContent.save();

        // -------- generate message statistics --------
        KeyboardMessageStatisticsGenerator keyboardMessageStatisticsGenerator = new KeyboardMessageStatisticsGenerator();
        MessageStatistics messageStatistics = keyboardMessageStatisticsGenerator.createMessageStatistics(contentChangeEvents);
        inputContent.setMessageStatistics(messageStatistics);
        // TODO do something with the resulting message statistics data

        // Optional: the following code block saves the message statistics into the app's db and syncs them to the backend:
        MessageStatisticsJson.fromMessageStatistics(messageStatistics, username).save();
        ContentAbstractionSyncJobService.launchService(context);


        // --------- extract change events 1 -----------
        MessageDiffWordEventExtractor messageDiffWordEventExtractor = new MessageDiffWordEventExtractor();
        List<de.lmu.ifi.researchime.contentabstraction.model.rawcontent.ContentChangeEvent> highLevelEvents = messageDiffWordEventExtractor.extractHigherLevelContentChangeEvents(inputContent, contentChangeEvents, initialContent);
        inputContent.setContentChangeEvents(highLevelEvents);
        // the resulting data will be used in the abstraction step

        // --------- extract change events 2 ---------
        FullMessageWordExtractor fullMessageWordExtractor = new FullMessageWordExtractor();
        List<de.lmu.ifi.researchime.contentabstraction.model.rawcontent.ContentChangeEvent> highLevelEventsFullMsg = fullMessageWordExtractor.extractHigherLevelContentChangeEvents(inputContent, contentChangeEvents, initialContent);
        inputContent.getContentChangeEvents().addAll(highLevelEventsFullMsg);
        // the resulting data will be used in the abstraction step

        // --------- call content abstraction module ---------
        // the following call assumes that the ResearchIME config has been downloaded and saved to DB!
        // to simplify testing, we changed the preconditon of an idle device to a charging device for this demo app
        RIMEContentAbstractionConfig rimeContentAbstractionConfig = SQLite.select().from(RIMEContentAbstractionConfig.class).querySingle();
        RIMEInputContentProcessingController rimeInputContentProcessingController = new RIMEInputContentProcessingController(context, rimeContentAbstractionConfig);
        rimeInputContentProcessingController.processContentChangeEvents(context, inputContent.getContentChangeEvents(), new RIMECallback() {
            @Override
            public boolean onAbstractedActionEventsReady(List<AbstractedAction> abstractedActionEvents) {
                // TODO do something with the resulting abstracted data

                // optional: save into local db and sync to backend
                for (AbstractedAction abstractedAction : abstractedActionEvents) {
                    AbstractActionEventJson.fromAbstractActionEvent(abstractedAction, username).save();
                }
                ContentAbstractionSyncJobService.launchService(context);

                return true; // ... if your app could successfully accomodate the data. False, if not (yielding a retry later).
            }

            @Override
            public boolean onWordFrequenciesChanged(List<WordFrequency> wordFrequencies) {
                // TODO do something with the resulting abstracted data

                // optional: save into local db and sync to backend
                for (WordFrequency wordFrequency : wordFrequencies) {
                    WordFrequencyJson.fromWordFrequency(wordFrequency, username).save();
                }
                ContentAbstractionSyncJobService.launchService(context);

                return true; // ... if your app could successfully accomodate the data. False, if not (yielding a retry later).
            }
        });

        // optional: clean local database (remove already synced objects)
        rimeInputContentProcessingController.cleanupOldHLCCEs(context, rimeContentAbstractionConfig);
    }

    public static void setupLanguageLogger(Context context){
        // the easiest way is to use this activity:
        Intent intent = new Intent(context, SetupCompletionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
