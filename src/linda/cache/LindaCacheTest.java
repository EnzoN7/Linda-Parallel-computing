package linda.cache;

import linda.Tuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

public class LindaCacheTest {

    private ILindaCache cache;

    private Tuple intAndCharTemplate = new Tuple(Integer.class, Character.class);

    @Before
    public void setup() {
        cache = new LRULindaCache(10);
    }

    @Test
    public void basicPutOneTupleAndGet() {

        Tuple basicTuple = new Tuple(1, 'c');

        cache.put(intAndCharTemplate, basicTuple );

        Optional<Tuple> tuple = cache.get(intAndCharTemplate);

        Assert.assertTrue(tuple.isPresent());
        Assert.assertEquals(tuple.get(), basicTuple);
    }

    @Test
    public void basicPutTuplesAndGet() {
        Tuple expectedTuple = new Tuple(100, '~');

        cache.put(intAndCharTemplate, new Tuple(1, 'c') );
        cache.put(intAndCharTemplate, new Tuple(2, 'a'));
        cache.put(intAndCharTemplate, new Tuple(10, 'z'));
        cache.put(intAndCharTemplate, new Tuple(9, '9'));
        cache.put(intAndCharTemplate, expectedTuple);

        Optional<Tuple> tuple = cache.get(intAndCharTemplate);

        Assert.assertTrue(tuple.isPresent());
        Assert.assertEquals(tuple.get(), expectedTuple);
    }

    @Test
    public void basicPutSameTuples() {
        Tuple basicTuple = new Tuple(1, 'c');

        cache.put(intAndCharTemplate, basicTuple );
        cache.put(intAndCharTemplate, basicTuple );
        cache.put(intAndCharTemplate, basicTuple );
        cache.put(intAndCharTemplate, basicTuple );
        cache.put(intAndCharTemplate, basicTuple );
        cache.put(intAndCharTemplate, basicTuple );

        Optional<Tuple> tuple = cache.get(intAndCharTemplate);

        Assert.assertTrue(tuple.isPresent());
        Assert.assertEquals(tuple.get(), basicTuple);
    }

    @Test
    public void putOverCapacity() {

        Tuple expectedTuple = new Tuple(100, 'x');

        cache.put(intAndCharTemplate, new Tuple(1, 'c') );
        cache.put(intAndCharTemplate, new Tuple(2, 'a'));
        cache.put(intAndCharTemplate, new Tuple(10, 'z'));
        cache.put(intAndCharTemplate, new Tuple(9, '9'));
        cache.put(intAndCharTemplate, new Tuple(100, 'c'));
        cache.put(intAndCharTemplate, new Tuple(100, 'b'));
        cache.put(intAndCharTemplate, new Tuple(100, 'a'));
        cache.put(intAndCharTemplate, new Tuple(214, '2'));
        cache.put(intAndCharTemplate, new Tuple(111, 'e'));
        cache.put(intAndCharTemplate, new Tuple(1362, 'w'));
        cache.put(intAndCharTemplate, new Tuple(9999, 't'));
        cache.put(intAndCharTemplate, new Tuple(5, 'y'));
        cache.put(intAndCharTemplate, expectedTuple);

        Optional<Tuple> tuple = cache.get(intAndCharTemplate);

        Assert.assertTrue(tuple.isPresent());
        Assert.assertEquals(tuple.get(), expectedTuple);
        Assert.assertEquals(cache.size(), 10);
    }

    @Test
    public void getUnderLimit() {
        Optional<Tuple> tuple = cache.get(intAndCharTemplate);

        Assert.assertFalse(tuple.isPresent());
        Assert.assertEquals(cache.size(), 0);
    }
}
