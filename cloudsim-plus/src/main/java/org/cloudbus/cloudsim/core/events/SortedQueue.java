package org.cloudbus.cloudsim.core.events;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A {@link EventQueue} that orders {@link SimEvent}s based on their time attribute.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public class SortedQueue implements EventQueue {
    /**
     * Despite the events are sorted by time and there are
     * sorted collections such as {@link java.util.SortedSet},
     * since the time of a new event is usually higher than the previous
     * one, in such a case, the {@link LinkedList#add(Object)} provides
     * better performance, which is O(1).
     */
    private final List<SimEvent> list = new LinkedList<>();

    /**
     * The max time that an added event is scheduled.
     */
    private double maxTime = -1;

    /**
     * Adds a new event to the queue, preserving the temporal order
     * of the events.
     *
     * @param newEvent the event to be added to the queue.
     */
    public void addEvent(final SimEvent newEvent) {
        // The event has to be inserted as the last of all events
        // with the same event_time(). Yes, this matters.
        final double eventTime = newEvent.getTime();
        if (eventTime >= maxTime) {
            list.add(newEvent);
            maxTime = eventTime;
            return;
        }

        /*If the event time is smaller than the maxTime, traverses the list
         * to find the place to insert the event.
         * It uses a reverse iterator because usually in such cases,
         * the time of the new event is close to the last events.
         * Starting from the tail of the list will ensure the lowest number
         * of iterations of the best cases.*/
        final ListIterator<SimEvent> reverseIterator = list.listIterator(list.size() - 1);
        while (reverseIterator.hasPrevious()) {
            if (reverseIterator.previous().getTime() <= eventTime) {
                reverseIterator.next();
                reverseIterator.add(newEvent);
                return;
            }
        }

        list.add(newEvent);
    }

    /**
     * Adds a new event to the head of the queue.
     *
     * @param newEvent The event to be put in the queue.
     */
    protected void addEventFirst(final SimEvent newEvent) {
        newEvent.setSerial(0);
        list.add(0, newEvent);
    }

    /**
     * Returns an iterator to the events in the queue.
     *
     * @return the iterator
     */
    public Iterator<SimEvent> iterator() {
        return list.iterator();
    }

    /**
     * Returns a stream to the elements into the queue.
     *
     * @return the stream
     */
    public Stream<SimEvent> stream() {
        return list.stream();
    }

    /**
     * Returns the size of this event queue.
     *
     * @return the number of events in the queue.
     */
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Removes the event from the queue.
     *
     * @param event the event
     * @return true, if successful
     */
    public boolean remove(final SimEvent event) {
        return list.remove(event);
    }

    /**
     * Removes all the events from the queue.
     *
     * @param events the events
     * @return true, if successful
     */
    public boolean removeAll(final Collection<SimEvent> events) {
        return list.removeAll(events);
    }

    public boolean removeIf(final Predicate<SimEvent> predicate){
        return list.removeIf(predicate);
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        list.clear();
    }

    @Override
    public SimEvent first() throws NoSuchElementException {
        if (list.isEmpty()) {
            throw new NoSuchElementException("The Deferred Queue is empty.");
        }

        return list.get(0);
    }
}