package br.com.assessments.pubsub.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;

import br.com.assessments.pubsub.api.Publisher;
import br.com.assessments.pubsub.api.Subscriber;

/**
 * 
 * Default implementation of Publisher
 *
 */
public class DefaultPublisher implements Publisher {
	private HashMap<Class<?>, HashSet<Subscriber>> subscribers = new HashMap<>();

	public void publish(Object event) {
		if(subscribers.containsKey(event.getClass())) {
			LinkedList<Subscriber> subs = new LinkedList<>(subscribers.get(event.getClass()));
			handle(subs, event);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Subscriber subscribe(Class<T> eventClass, Consumer<T> consumer) {
		DefaultSubscriber subscriber = new DefaultSubscriber((Consumer<Object>) consumer);
		subscribers.computeIfAbsent(eventClass, key -> new HashSet<>()).add(subscriber);
		return subscriber;
	}

	@Override
	public <T> void unsubscribe(Class<T> eventClass, Subscriber subscriber) {
		if(subscribers.containsKey(eventClass)) {
			HashSet<Subscriber> subs = subscribers.get(eventClass);
			subs.remove(subscriber);

			if(subs.isEmpty()) {
				subscribers.remove(eventClass);
			}
		}
	}

	private void handle(LinkedList<Subscriber> subs, Object event) {
		if(!subs.isEmpty()) {
			subs.pop().execute(event);
			handle(subs, event);
		}
	}
}
