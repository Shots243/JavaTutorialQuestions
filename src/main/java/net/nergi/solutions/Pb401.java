package net.nergi.solutions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.nergi.Solution;
import net.nergi.util.Utils;
import net.nergi.solutions.P735a.GenericCollection;
import net.nergi.solutions.P735a.GenericIterator;

/** Solution for b401. */
@SuppressWarnings("unused")
public class Pb401 implements Solution {

  /** Returns the header for the solution, which is the problem's name. */
  @Override
  public String getName() {
    return "b401: Generic sets";
  }

  /** Runs the solution to the problem. */
  @Override
  public void exec() {
    // Create sets
    final GenericSet<String> speedStrings = new SpeedEfficientGenericSet<>();
    final GenericSet<String> memoryStrings = new MemoryEfficientGenericSet<>();

    speedStrings.add("no thank you");
    speedStrings.add("yeah, i guess");

    memoryStrings.add("oh s***!");
    memoryStrings.add("what the f***!");

    // Print combos
    for (String s : speedStrings) {
      System.out.println(s);
    }

    Utils.printSeparator();

    for (String s : memoryStrings) {
      System.out.println(s);
    }

    Utils.printSeparator();

    speedStrings.addAll(memoryStrings);

    for (String s : speedStrings) {
      System.out.println(s);
    }
  }

  /**
   * Generic set implementation for reference types.
   *
   * @param <E> Type to store
   */
  // Modified for 735a
  public interface GenericSet<E> extends GenericCollection<E> {

    // Adds the item to the set
    void add(E item);

    // If the item belongs to the set, it is removed and 'true'
    // is returned.  Otherwise 'false' is returned
    boolean remove(E item);

    // Returns true iff the set is empty
    boolean isEmpty();

    // Returns true iff the set contains item
    boolean contains(E item);

    // Add to the set each element in 'other'
    default void addAll(GenericSet<E> other) {
      for (E e : other) {
        if (!contains(e)) {
          add(e);
        }
      }
    }

    // Remove from the set each element in 'other'
    default void removeAll(GenericSet<E> other) {
      for (E e : other) {
        if (contains(e)) {
          remove(e);
        }
      }
    }

    // Return true iff the set contains every element of 'other'
    default boolean contains(GenericSet<E> other) {
      boolean conts = true;

      for (E e : other) {
        conts = contains(e);

        if (!conts) {
          break;
        }
      }

      return conts;
    }

    // Extension for 336b
    default GenericSet<E> asUnmodifiableSet() {
      return new GenericSet<>() {

        private final GenericSet<E> backingSet = this;

        @Override
        public void add(E item) {
          throw new UnsupportedOperationException("This set is read-only!");
        }

        @Override
        public boolean remove(E item) {
          throw new UnsupportedOperationException("This set is read-only!");
        }

        @Override
        public boolean isEmpty() {
          return backingSet.isEmpty();
        }

        @Override
        public boolean contains(E item) {
          return backingSet.contains(item);
        }

        @Override
        public GenericIterator<E> iterator() {
          return new GenericIterator<>() {

            private final Iterator<E> backingIterator = backingSet.iterator();

            @Override
            public boolean hasNext() {
              return backingIterator.hasNext();
            }

            @Override
            public E next() {
              return backingIterator.next();
            }
          };
        }
      };
    }
  }

  public abstract static class AbstractGenericSet<E> implements GenericSet<E> {

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("[");

      for (E e : this) {
        if (sb.length() > 1) {
          sb.append(", ");
        }

        sb.append(e);
      }

      sb.append("]");

      return sb.toString();
    }
  }

  public static class MemoryEfficientGenericSet<E> extends AbstractGenericSet<E> {

    private final Set<E> backingSet = new HashSet<>();

    @Override
    public void add(E item) {
      backingSet.add(item);
    }

    @Override
    public boolean remove(E item) {
      return backingSet.remove(item);
    }

    @Override
    public boolean isEmpty() {
      return backingSet.isEmpty();
    }

    @Override
    public boolean contains(E item) {
      return backingSet.contains(item);
    }

    @Override
    public GenericIterator<E> iterator() {
      return new GenericIterator<>() {

        private final Iterator<E> internalIterator = backingSet.iterator();

        @Override
        public boolean hasNext() {
          return internalIterator.hasNext();
        }

        @Override
        public E next() {
          return internalIterator.next();
        }
      };
    }
  }

  public static class SpeedEfficientGenericSet<E> extends AbstractGenericSet<E> {

    private final Map<E, Boolean> backingMap = new HashMap<>();

    @Override
    public void add(E item) {
      backingMap.put(item, true);
    }

    @Override
    public boolean remove(E item) {
      return backingMap.remove(item) != null;
    }

    @Override
    public boolean isEmpty() {
      return backingMap.isEmpty();
    }

    @Override
    public boolean contains(E item) {
      final Boolean val = backingMap.get(item);
      return val != null ? val : false;
    }

    @Override
    public GenericIterator<E> iterator() {
      return new GenericIterator<>() {

        private final Iterator<E> backingIterator = backingMap.keySet().iterator();

        @Override
        public boolean hasNext() {
          return backingIterator.hasNext();
        }

        @Override
        public E next() {
          return backingIterator.next();
        }
      };
    }
  }
}
