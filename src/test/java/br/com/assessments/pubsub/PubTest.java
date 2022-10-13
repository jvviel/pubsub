package br.com.assessments.pubsub;

import br.com.assessments.pubsub.api.Subscriber;
import br.com.assessments.pubsub.core.DefaultPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Publisher Tester
 */
public class PubTest {
	private DefaultPublisher publisher;

	@BeforeEach
	public void init() {
		publisher = new DefaultPublisher();
	}

	@Test
	public void testPublisher() {
		ArrayList<Object> history = new ArrayList<>();
		Subscriber sub1 = publisher.subscribe(String.class, history::add);
		Subscriber sub2 = publisher.subscribe(String.class, history::add);
		publisher.publish("test1");

		assertEquals(history.size(), 2);
		history.clear();

		publisher.unsubscribe(String.class, sub1);
		publisher.publish("test");

		assertEquals(history.size(), 1);
		history.clear();

		publisher.unsubscribe(String.class, sub2);
		publisher.publish("test");

		assertEquals(history.size(), 0);
		history.clear();
	}

	@Test
	public void testEventTypes() {
		ArrayList<Object> history = new ArrayList<>();
		publisher.subscribe(String.class, history::add);
		publisher.subscribe(Integer.class, history::add);
		publisher.publish("test1");
		assertTrue(history.contains("test1"));

		publisher.publish(1);
		assertTrue(history.contains(1));
	}

	public void testCustomEvents() {
		ArrayList<Object> history = new ArrayList<>();
		publisher.subscribe(MyEvent.class, history::add);
		publisher.publish(new MyEvent("myvalue"));
		assertTrue(history.contains(new MyEvent("myvalue")));

		Set<Object> history2 = new HashSet<>();
		publisher.subscribe(MyEvent.class, history2::add);
		publisher.publish(new MyEvent("myvalue"));
		assertTrue(history2.contains(new MyEvent("myvalue")));
	}

	public void testScale() {
		for(int i=0; i < 1000000; i++) {
			publisher.subscribe(String.class, (e) -> {});
		}
		publisher.publish("test");
	}

	public static class MyEvent {
		private String content;

		public MyEvent(final String content) {
			super();
			this.content = content;
		}

		public String getContent() {
			return content;
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof MyEvent && ((MyEvent) obj).content.equals(content);
		}
	}
}
