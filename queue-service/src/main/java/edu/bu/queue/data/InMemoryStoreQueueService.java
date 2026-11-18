package edu.bu.queue.data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryStoreQueueService implements DataStoreQueueService {

  final Queue<String> requestQueue = new ConcurrentLinkedQueue<>();

  @Override
  public void updateRequestQueue(String message) {
    requestQueue.add(message);
  }

  @Override
  public String PollRequestQueue() {
    return requestQueue.poll();
  }
}
