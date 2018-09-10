/**
 * 
 */

package com.inacionery.score.service;

import com.inacionery.score.thread.AmazonSearchThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author Inácio Nery
 */
@Service
public class EstimateService {

	public Integer getKeywordScore(String keyword) {

		List<AmazonSearchThread> tasks = createAmazonSearchTasks(keyword);

		Map<String, Integer> keywordScores = new HashMap<>();

		ExecutorService executorService = Executors.newWorkStealingPool();

		try {
			for (Future<Map<String, Integer>> future : executorService.invokeAll(
				tasks, 9900, TimeUnit.MILLISECONDS)) {

				if (future.isCancelled()) {
					continue;
				}

				try {
					keywordScores = mergeResults(keywordScores, future.get());
				}
				catch (ExecutionException e) {
					logger.error(
						"It's not possible process the result of this task",
						future, e);
				}
			}
		}
		catch (InterruptedException e) {
			logger.error("It's not possible process the results", e);
		}

		executorService.shutdown();

		return Optional.ofNullable(keywordScores.get(keyword)).orElse(0);
	}

	private List<AmazonSearchThread> createAmazonSearchTasks(String keyword) {

		List<AmazonSearchThread> tasks = new ArrayList<>();

		Float decreaseScore = 10f / keyword.length();

		for (int i = 0; i < keyword.length(); i++) {
			String keywordPart = keyword.substring(0, i + 1);

			Float initialScore = 100f - (100 / keyword.length()) * i;

			AmazonSearchThread amazonSearchThread = context.getBean(
				AmazonSearchThread.class, keywordPart, initialScore,
				decreaseScore);

			if (logger.isDebugEnabled()) {
				logger.debug(
					"AmazonSearchThread created for keyword " + keywordPart +
						" with initial score " + initialScore +
						" with decrease score " + decreaseScore);
			}

			tasks.add(amazonSearchThread);
		}

		return tasks;
	}

	private Map<String, Integer> mergeResults(
		Map<String, Integer> keywordScores, Map<String, Integer> future) {

		return Stream.concat(
			keywordScores.entrySet().stream(),
			future.entrySet().stream()).collect(
				Collectors.toMap(
					entry -> entry.getKey(), entry -> entry.getValue(),
					(s1, s2) -> s1 > s2 ? s1 : s2));
	}

	@Autowired
	private ApplicationContext context;

	private Logger logger = LoggerFactory.getLogger(EstimateService.class);
}
