package com.percolate.foam;

import android.content.Context;

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
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class ExceptionPersister {

    private Context context;

    public ExceptionPersister(Context context){
        this.context = context;
    }

    public Map<String, StoredException> loadAll(){
        Map<String, StoredException> storedExceptions = new HashMap<String, StoredException>();
        final Gson gson = new Gson();
        String[] fileNames = context.fileList();
        if(fileNames != null) {
            for (String fileName : fileNames) {
                if(Utils.isNotBlank(fileName) && fileName.startsWith("FoamStoredException")) {
                    FileInputStream in = null;
                    try {
                        in = context.openFileInput(fileName);
                        if (in != null) {
                            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                            StoredException storedException = gson.fromJson(reader, StoredException.class);
                            storedExceptions.put(fileName, storedException);
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
                }
            }
        }
        return storedExceptions;
    }

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
