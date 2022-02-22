package linda.shm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linda.Tuple;

class TupleSpaceTest {

	TupleSpace space = new TupleSpace();
	
	@BeforeEach
	void setUp() throws Exception {

		space.add(new Tuple(1, 'c'));

		space.add(new Tuple(true, "ab"));
	}

	//@Test
	void testFindTuple() {
		fail("Not yet implemented");
	}

	@Test
	void testFindOneTuple() {
		Optional<Tuple> tuple;
		try {
			tuple = space.findOne(new Tuple(1, 'c'));
			
			System.out.println("exists:" + space.exists(new Tuple(1, 'c')));
			
			assertTrue(tuple.isPresent());
			assertTrue(tuple.get().matches(new Tuple(1, 'c')));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Test
	void testExistsTuple() {
		try {
			assertTrue(space.exists(new Tuple(1, 'c')));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Test
	void testTakeOneTuple() {
		fail("Not yet implemented");
	}

}
