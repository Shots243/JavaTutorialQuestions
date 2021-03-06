package net.nergi.solutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import net.nergi.Solution;
import net.nergi.util.Utils;

/** Solution for 5d30. */
@SuppressWarnings("unused")
public class P5d30 implements Solution {

  /** Returns the header for the solution, which is the problem's name. */
  @Override
  public String getName() {
    return "5d30: Unreliable buffered reader";
  }

  /** Runs the solution to the problem. */
  @Override
  public void exec() {
    // So uh, I already have a BufferedReader inside the utility class I've written.
    // We're going to have to perform some JVM black magic in order to get this to work.
    final UnreliableBufferedReader ubr =
        new UnreliableBufferedReader(new InputStreamReader(System.in), 0.5);

    // Are you ready?
    // (Fixed for post Java 12)
    try {
      // **************************************************************************
      // ************************* PLEASE DON'T DO THIS! **************************
      // **************************************************************************
      // *    THIS IS JUST AN EXAMPLE AND SHOULD NOT BE USED IF NOT NECESSARY!    *
      // * I AM ONLY DOING THIS SO THAT IT DOESN'T AFFECT OTHER SOLUTION CLASSES! *
      // **************************************************************************
      final Field brField = Utils.class.getDeclaredField("br");
      brField.setAccessible(true);
      brField.set(null, ubr);

      if (Utils.getBr() instanceof UnreliableBufferedReader) {
        System.out.println("Injection successful.");
      } else {
        throw new Exception("Failed to set br field.");
      }
    } catch (NoSuchFieldException e) {
      System.out.println("Wrong field!");
      e.printStackTrace();
      return;
    } catch (IllegalAccessException e) {
      System.out.println("There is a security manager among us.");
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    System.out.println("Please attempt to get 5 lines of input:");

    // Attempt... to get 5 strings
    final List<String> strings = Utils.getUserLines(5, false);
    System.out.println("----------");
    strings.forEach(System.out::println);

    // Attempts?
    System.out.printf("Attempts: %d, Lines: 5\n", ubr.getReads());
  }

  /**
   * A {@link BufferedReader} that is designed to fail randomly. This has real <code>
   * #define true (rand() % 100 &lt; 95)</code> vibes.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static class UnreliableBufferedReader extends BufferedReader {

    private final Random rnd = new Random();
    private final double probabilityOfError;
    private long reads = 0;

    /**
     * Create a new <code>UnreliableBufferedReader</code> with a given reader and a probability to
     * fail.
     *
     * @param in {@link Reader} to read from
     * @param probabilityOfError Probability of read methods throwing an {@link IOException}.
     */
    public UnreliableBufferedReader(Reader in, double probabilityOfError) {
      super(in);
      this.probabilityOfError = Math.min(1, Math.max(0, probabilityOfError));
    }

    /**
     * """Attempt""" to read a line from the reader.
     *
     * @return A line if the read succeeds.
     * @throws IOException Throws on a normal {@link IOException} or when it decides to based on
     *     random chance.
     */
    @Override
    public String readLine() throws IOException {
      ++reads;

      if (rnd.nextDouble() <= probabilityOfError) {
        super.readLine(); // Clear the buffer.
        throw new IOException("Error occurred on input stream.");
      } else {
        return super.readLine();
      }
    }

    /**
     * """Attempt""" to read a character from the reader.
     *
     * @return A character from 0x0000 to 0xffff if successful.
     * @throws IOException Throws on a normal {@link IOException} or when it decides to based on
     *     random chance.
     */
    @Override
    public int read() throws IOException {
      ++reads;

      if (rnd.nextDouble() <= probabilityOfError) {
        super.read();
        throw new IOException("Error occurred on input stream.");
      } else {
        return super.read();
      }
    }

    /**
     * """Attempt""" to read part of the reader into a given character array.
     *
     * @return Number of characters read, or -1 if the stream ended.
     * @throws IOException Throws on a normal {@link IOException} or when it decides to based on
     *     random chance.
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
      ++reads;

      if (rnd.nextDouble() <= probabilityOfError) {
        final char[] dummy = new char[len];
        super.read(dummy, 0, len);

        throw new IOException("Error occurred on input stream.");
      } else {
        return super.read(cbuf, off, len);
      }
    }

    /** Get the number of times a read method has been called. */
    public long getReads() {
      return reads;
    }
  }
}
