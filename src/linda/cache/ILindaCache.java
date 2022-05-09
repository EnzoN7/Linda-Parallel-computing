package linda.cache;

import linda.Tuple;

import java.util.Optional;

//
public interface ILindaCache {

    /**
     *
     * @param key : A template
     * @param value : Un tuple obtenu par l'opération "read" parametré avec "key"
     * @return true if ... , false otherwise.
     */
    boolean put(Tuple key, Tuple value);

    /**
     *
     * @param key
     * @return
     */
    Optional<Tuple> get(Tuple key);

    /**
     * Return the number of elements stored in the cache
     * @return
     */
    int size();

    int size(Tuple template);

    /**
     * Check if there is at least one element in the cache
     * @return
     */
    boolean isEmpty();

    /**
     * Clear the cache by removing all elements
     */
    void clear();
}
