package linda.test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.shm.CentralizedLinda;

/*
 * Ceci est une simple application de test de CentralizedLinda, elle permet aussi de simuler un environnement client-serveur
 */
public class Test4 {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
		
		CentralizedLinda linda = new CentralizedLinda(executorService);
		
		Random rand = new Random();
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[BEGIN-TAKE#" + Thread.currentThread().getId() + "] ");
				
				Tuple motif = new Tuple(Integer.class, 'c', String.class);
				
				System.out.println("Contains1: " + new Tuple(3, 'b', "foo").matches(new Tuple(Integer.class, Character.class, "foo")));
				
				Tuple tuple = linda.tryRead(new Tuple(Integer.class, 1, "amine"));
				
				System.out.println("[END-TAKE#" + Thread.currentThread().getId() + "] Result: " + tuple);
				

			}
		} , 4, 5, TimeUnit.SECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[BEGIN-READ#" + Thread.currentThread().getId() + "] ");
				
				Tuple motif = new Tuple(Integer.class, Character.class, "ghr00");
				
				System.out.println("Matches: " + new Tuple(rand.nextInt(), 'b', "ghr00").matches(motif));
				
				Tuple tuple = linda.take(motif);
				
				System.out.println("[END-READ#" + Thread.currentThread().getId() + "] Result: " + tuple);
			}
		} , 4, 5, TimeUnit.SECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[BEGIN-READ#" + Thread.currentThread().getId() + "] ");
				
				Tuple motif = new Tuple(Integer.class, Character.class, "ghr00");
				
				Tuple tuple = linda.read(motif);
				
				System.out.println("[END-READ#" + Thread.currentThread().getId() + "] Result: " + tuple);
			}
		} , 4, 5, TimeUnit.SECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[WRITE#" + Thread.currentThread().getId() + "] ");
				
				linda.write(new Tuple(rand.nextInt(), 'c', "amine"));
				

			}
		} , 3, 5, TimeUnit.SECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[REGISTER#" + Thread.currentThread().getId() + "] ");

				linda.eventRegister(eventMode.READ, eventTiming.FUTURE, new Tuple(Integer.class, Character.class, String.class), new Callback() {
					
					@Override
					public void call(Tuple t) {
						System.out.println("Les callbacks de type FUTURE marchent!");
					}
				});
				

			}
		} , 1, 30, TimeUnit.SECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[REGISTER#" + Thread.currentThread().getId() + "] ");

				linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, new Tuple(Integer.class, Character.class, String.class), new Callback() {
					
					@Override
					public void call(Tuple t) {
						System.out.println("Les callbacks de type IMMEDIATE marchent!");
						
					}
				});
				

			}
		} , 5, 2, TimeUnit.SECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[WRITE#" + Thread.currentThread().getId() + "] ");

				
				linda.write(new Tuple(rand.nextInt(), 'b', "ghr00"));
				

			}
		} , 5, 5, TimeUnit.SECONDS);
		
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {

				System.out.println("[TRYREAD#" + Thread.currentThread().getId() + "] ");

				Tuple tuple = linda.tryTake(new Tuple(Integer.class, Character.class, String.class));
				
				System.out.println("[TRYREAD#" + Thread.currentThread().getId() + "] " + tuple) ;
			}
		} , 5, 5, TimeUnit.SECONDS);
		
	}

}
