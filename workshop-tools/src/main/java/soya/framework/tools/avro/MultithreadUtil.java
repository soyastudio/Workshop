package soya.framework.tools.avro;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

public class MultithreadUtil {

	private Collection<Runnable> callables = new ArrayList<>();

	private ListenableFuture<List<Object>> _run() {

		final ListeningExecutorService service = MoreExecutors

				.listeningDecorator(Executors.newFixedThreadPool(10));

		try {

			ArrayList<Runnable> newCallables;

			synchronized (this) {

				newCallables = new ArrayList<>(callables);

				callables.clear();

			}

			return Futures.allAsList(CollectionUtil.map(newCallables, service::submit));

		} finally {

			service.shutdown();

		}

	}

	public void run() {

		if (!callables.isEmpty()) {

			_run();

		}

	}

	public void runAndWait() {

		if (!callables.isEmpty()) {

			try {

				_run().get();

			} catch (Exception e) {

			}

		}

	}

	public void add(RunnableWithException<?>... callables) {

		for (RunnableWithException<?> callable : callables) {

			this.callables.add(() -> {

				try {

					callable.run();

				} catch (Throwable e) {

				}

			});

		}

	}

	public boolean isEmpty() {

		return callables.isEmpty();

	}

	public static void run(RunnableWithException<?>... callables) {

		MultithreadUtil util = new MultithreadUtil();

		util.add(callables);

		util.run();

	}

}