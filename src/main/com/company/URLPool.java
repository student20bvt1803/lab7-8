package com.company;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class stores crawler info
 * (max depth, unhandled and
 * handled pages). Can be
 * processed by multiple
 * threads.
 *
 * @author Stoyalov Arseny BVT1803
 */
public class URLPool {

    private final Set<WebPage> unhandled = new HashSet<>();

    private final Set<WebPage> handled = new HashSet<>();

    private final int maxDepth;

    private volatile int waitingThreads;

    private volatile boolean done;

    public URLPool(URL url, int maxDepth) {
        this.maxDepth = maxDepth;
        unhandled.add(new WebPage(url, 0));
    }

    public int getWaitingThreads() {
        return waitingThreads;
    }

    public boolean isNotDone() {
        return !done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public synchronized Set<WebPage> getHandled() {
        return Collections.unmodifiableSet(handled);
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public synchronized void addUnhandledPage(WebPage page) {

        if (waitingThreads > 0) {
            waitingThreads--;
        }
        unhandled.add(page);
        notify();
    }

    public synchronized WebPage getUnhandledPage() {

        try {
            while (unhandled.isEmpty()) {
                waitingThreads++;
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Iterator<WebPage> i = unhandled.iterator();
        WebPage page = i.next();
        i.remove();

        return page;
    }

    public synchronized void addHandledPage(WebPage page) {
        handled.add(page);
    }

}
