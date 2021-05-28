package com.company;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Changes {
    // we use a CopyOnWriteArrayList to ensure the iteration is thread-safe
    private CopyOnWriteArrayList<WatchEvent<Path>> events = new CopyOnWriteArrayList<>();
    public volatile String fullFilePath;

    public boolean addEvents(List<WatchEvent<Path>> events) {
        return this.events.addAll(events);
    }

    public boolean addEvent(WatchEvent<Path> event) {
        return this.events.add(event);
    }

    public void setEvents(CopyOnWriteArrayList<WatchEvent<Path>> events) {
        this.events = events;
    }

    public CopyOnWriteArrayList<WatchEvent<Path>> getEvents() {
        return this.events;
    }

    public void clear() {
        this.events.clear();
    }

    /**
     * Remove all events for a file and event if given
     *
     * @param kind       Kind of events that should be deleted. If null all event will be deleted
     * @param path       Name of the File we want events removed for
     * @param oldChanges Only delete an event if it is in present in this list. If null clear entire list
     */
    public void deleteEventsByContext(WatchEvent.Kind<Path> kind, Path path, CopyOnWriteArrayList<WatchEvent<Path>> oldChanges) {
        this.events.removeAll(this.events.stream()
                .filter(e -> e.context().equals(path)
                        && (kind == null || e.kind().equals(kind))
                        && (oldChanges == null || oldChanges.contains(e)))
                .collect(Collectors.toList()));
    }
}
