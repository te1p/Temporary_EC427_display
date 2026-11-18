package edu.bu.queue.data;

public interface DataStoreQueueService {

  void updateRequestQueue(String message);

  String PollRequestQueue();
}
