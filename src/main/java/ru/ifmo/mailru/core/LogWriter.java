package ru.ifmo.mailru.core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @author Anastasia Lebedeva
 */

public class LogWriter {
    private PrintWriter failedPagePrintWriter;
    private PrintWriter crawledPrintWriter;
    private String queueLogFile;

    public void setCrawledLogging(PrintWriter crawledPrintWriter) {
        this.crawledPrintWriter = crawledPrintWriter;
    }

    public void setQueueLogFile(String queueLogFile) {
        this.queueLogFile = queueLogFile;
    }

    public void setFailedLogging(PrintWriter failed) throws FileNotFoundException {
        this.failedPagePrintWriter = failed;
    }

    public void setFailedPage(WebURL url, String exception) {
        if (failedPagePrintWriter == null) {
            return;
        }
        synchronized (failedPagePrintWriter) {
            failedPagePrintWriter.println(url.getUri().toString() + " " + exception);
            failedPagePrintWriter.flush();
        }
    }

	public void setCrawledURL(WebURL url) {
        if (crawledPrintWriter == null) {
            return;
        }
        synchronized (crawledPrintWriter) {
            crawledPrintWriter.println(url.getUri());
            crawledPrintWriter.flush();
        }
	}

    public void setCrawledURL(Page page) {
        if (crawledPrintWriter == null) {
            return;
        }
        synchronized (crawledPrintWriter) {
            crawledPrintWriter.println(page.getUrl().getUri() + " " + page.getOutLinks().size() + " " +
                    page.getContent().hashCode() + " " + System.currentTimeMillis());
            crawledPrintWriter.flush();
        }
    }

    /*void makeSnapshot() {
        try {
            File newQueue = new File(queueLogFile + "tmp");
            PrintWriter printWriter = new PrintWriter(newQueue);
            synchronized (toCrawl) {
                for (WebURL webURL : toCrawl) {
                    printWriter.println(webURL.getUri().toString() + " " + webURL.getQualityRank());
                }
            }
            printWriter.close();
            File queueLog = new File(queueLogFile);
            queueLog.delete();
            newQueue.renameTo(queueLog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    } */
}
