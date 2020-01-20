/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author cyrilmongis
 */
public class QueryFileJob implements FileJob {

    private final QueryJob job;

    private final File file;

    private final FileOutputStream fileOutputStream;

    public QueryFileJob(QueryJob job) throws IOException {
        this.job = job;
        this.file = File.createTempFile(job.getId(), "");
        fileOutputStream = new FileOutputStream(file);
        job.setOutputStream(fileOutputStream);
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setId(String id) {
        job.setId(id);
    }

    @Override
    public String getId() {
        return job.getId();
    }

    @Override
    public void setObserver(QueryObserver observer) {
        job.setObserver(observer);
    }

    @Override
    public void configure(String sequence, String allel, String len, String... servers) {
        job.configure(sequence, allel, len, servers);
    }

    @Override
    public void setOutputStream(OutputStream stream) {

    }

    @Override
    public long getTimeCreation() {
        return job.getTimeCreation();
    }

    @Override
    public boolean isFinished() {
        return job.isFinished();
    }

    @Override
    public boolean hasSucceeded() {
        return job.hasSucceeded();
    }

    @Override
    public int getProgress() {
        return job.getProgress();
    }

    @Override
    public int getTotal() {
        return job.getTotal();
    }

    @Override
    public void run() {
        job.run();
        try {
            fileOutputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void clean() {
        file.delete();
        job.clean();
    }

    public Throwable getError() {
        return job.getError();
    }

    @Override
    public String getFileName() {
        return job.getFileName();
    }

    @Override
    public void setFileName(String filename) {
        job.setFileName(filename);
    }

}
