package com.percolate.foam;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class that allows us to store and load exception data to the device.
 *
 * This is done so that crashes can be sent the next time the application launches.
 *
 * Data is stored using androids <code>context.openFileOutput</code>, so no extra permissions are
 * required since it uses internal storage.  See http://developer.android.com/reference/android/content/Context.html#openFileOutput(java.lang.String, int)
 */
class ExceptionPersister {

    private Context context;

    public ExceptionPersister(Context context){
        this.context = context;
    }

    /**
     * Load and return all StoredException data.  These represent crashes that have not yet
     * been reported.
     *
     * @return Map containing &lt;file name, exception data&gt;.
     */
    public Map<String, StoredException> loadAll(){
        Map<String, StoredException> storedExceptions = new HashMap<String, StoredException>();

        String[] fileNames = context.fileList();
        if(fileNames != null) {
            for (String fileName : fileNames) {
                if(Utils.isNotBlank(fileName) && fileName.startsWith("FoamStoredException")) {
                    StoredException storedException = loadStoredExceptionData(fileName);
                    if(storedException != null) {
                        storedExceptions.put(fileName, storedException);
                    }
                }
            }
        }
        return storedExceptions;
    }

    /**
     * Load file by <code>fileName</code>.  This file is expected to contain a StoredException
     * object that has been converted to json using <code>Gson</code>.
     *
     * Load the file and convert if from json back to an StoredException object.
     *
     * @param fileName File to load
     * @return Re-serialized StoredException object.  Will be null in the event of an error.
     */
    private @Nullable StoredException loadStoredExceptionData(String fileName) {
        StoredException storedException = null;
        FileInputStream in = null;
        try {
            in = context.openFileInput(fileName);
            if (in != null) {
                final InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                final Gson gson = new Gson();
                storedException = gson.fromJson(reader, StoredException.class);
            }
        } catch (Exception ex) {
            Utils.logIssue("Could not load file [" + fileName + "]", ex);
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Utils.logIssue("Could not close exception storage file.", ex);
            }
        }
        return storedException;
    }

    /**
     * Write given StoredException to a new file.
     * We store data using androids <code>context.openFileOutput</code> so that no extra permissions
     * are required.
     *
     * The given StoredException is converted to JSON and then stored as a String.
     *
     * @param storedException Exception data to persist.
     */
    public void store(StoredException storedException){
        String fileName = "FoamStoredException-" +
                storedException.platform.toString() +
                System.currentTimeMillis() +
                new Random().nextInt();

        OutputStream out = null;
        try {
            String json = new Gson().toJson(storedException);
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            final OutputStreamWriter writer = new OutputStreamWriter(out, "ISO8859_1"); //$NON-NLS-1$
            writer.write(json);
            writer.flush();
        } catch (Exception ex) {
            Utils.logIssue("Could not write exception to a file.", ex);
        } finally {
            try {
                if(out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                Utils.logIssue("Could not close exception storage file.", ex);
            }
        }
    }

}
