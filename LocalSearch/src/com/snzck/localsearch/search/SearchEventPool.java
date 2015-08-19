package com.snzck.localsearch.search;

import java.util.Deque;
import java.util.LinkedList;

import com.znzck.localsearch.system.SystemConfigField;
import com.znzck.localsearch.system.Config;

public class SearchEventPool {

	private Deque<SearchEvent> queue;
	private int maxSize;

	public SearchEventPool(Config config) {
		queue = new LinkedList<SearchEvent>();
		maxSize = Integer.parseInt(config.getProperty(SystemConfigField.EVENT_QUEUE_SIZE.name()));
	}
	
	public void add(SearchEvent event){
		if(queue.size() > maxSize){
			queue.remove();
		}
		queue.add(event);
	}
	
	public SearchEvent[] listEvents(){
		SearchEvent[] events = new SearchEvent[queue.size()];
		for(int i = 0; i < events.length; i++){
			events[i] = queue.remove();
		}
		return events;
	}
}
