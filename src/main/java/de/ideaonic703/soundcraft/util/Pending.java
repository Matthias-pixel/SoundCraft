package de.ideaonic703.soundcraft.util;

import org.apache.commons.compress.harmony.pack200.NewAttributeBands;

public class Pending<T> {
    private T data;
    private boolean done;
    private Callback<T> callback = null;

    public Pending(boolean done, T data) {
        this.data = data;
        this.done = done;
    }
    public boolean isDone() {
        return done;
    }
    public void complete() {
        this.complete(this.data);
    }
    public void complete(T data) {
        if(this.done)
            return;
        this.data = data;
        this.done = true;
        if(this.callback != null)
            this.callback.callback(this.data);
    }
    public T get() {
        return this.data;
    }
    public void setCallback(Callback<T> callback) {
        this.callback = callback;
    }
    public void set(T data) {
        if(!this.done)
            this.data = data;
    }
}
