/**
 * Asaf Shtrul
 * ID: 203378039
 */

/**
 * A synchronized bounded-size queue for multithreaded producer-consumer applications.
 * 
 * @param <T> Type of data items
 */


public class SynchronizedQueue<T> {

	private T[] buffer;
	private int producers;
	private Object L;
	private int firstelement; //the oldest element that will be to dequeue first.
	private int lastelement; //the newest element-the next place that we can enqueue element.
	

	
	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
		this.buffer = (T[])(new Object[capacity]);
		this.producers = 0;
		this.L = new Object();
		this.firstelement = 0;
		this.lastelement = 0;
	}
	
	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue, 
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this 
	 * queue (because no producers are registered), this method returns null.
	 * 
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public T dequeue() {
		// Realization of the consumer with monitor
		// boolean bool = true;
		T dequeueelement;
		while (true){
			if (this.getSize() > 0) {
				synchronized (this.L) {
					dequeueelement = buffer[firstelement];
					buffer[firstelement] = null;
					this.firstelement++;
					//this.firstelement = this.firstelement % buffer.length;
					this.firstelement %= buffer.length;
					this.L.notifyAll(); // notify from wait-set.
					return dequeueelement;
				}
			}else if (this.producers > 0) {
				synchronized (this.L) {
					try {
						
						this.L.wait();
					} catch (InterruptedException e) {
						System.out.println("some thread can't wait");
						//e.printStackTrace();
					}
				}
			}else return null;
		}
	}

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this 
	 * method blocks until some space becomes available.
	 * 
	 * @param item Item to enqueue
	 */
	public void enqueue(T item) {
		// Realization of the producer with monitor
		
		while (true){
			if (this.getSize() < buffer.length) {
				synchronized (this.L) {
					buffer[lastelement] = item;
					this.lastelement ++;
					//this.lastelement = this.lastelement % this.buffer.length;
					this.lastelement %= buffer.length;
					this.L.notifyAll(); // notify from wait-set.
					return;
				}
			}else{
				try {
					System.out.println("1");
					this.L.wait();
				} catch (InterruptedException e) {
					System.out.println("some thread can't wait");
					//e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity() {
		return buffer.length;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public int getSize() {
		return (this.buffer.length + this.lastelement - this.firstelement) % this.buffer.length;
	}
	
	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to 
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 * 
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	public void registerProducer() {
		synchronized (this) {
			this.producers++;
		}
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 * 
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public void unregisterProducer() {
		synchronized (this.L) {
			this.producers--;
			if (this.producers == 0) {
				this.L.notifyAll();
			}
		}
	}
}