/**
 * 
 */

package com.inacionery.score.thread;

import com.inacionery.score.service.EstimateService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author Inácio Nery
 */
@Component
@Scope("prototype")
public class AmazonSearchThread implements Callable<Map<String, Integer>> {

	public AmazonSearchThread(
		String keyword, Float initialScore, Float decreaseScore) {

		this.keyword = keyword;
		this.initialScore = initialScore;
		this.decreaseScore = decreaseScore;
	}

	@Override
	public Map<String, Integer> call()
		throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("Searching the keyword " + keyword);
		}

		String json = restTemplate.getForObject(
			amazonCompletionURL + keyword, String.class);

		JSONArray jsonArray = new JSONArray(json);

		JSONArray keywords = jsonArray.getJSONArray(1);

		Map<String, Integer> keywordScores = new HashMap<>();

		for (int i = 0; i < keywords.length(); i++) {
			keywordScores.put(
				keywords.getString(i),
				(int) (initialScore - (decreaseScore * i)));

		}

		if (logger.isDebugEnabled()) {
			logger.debug("The result for the keyword " + keywordScores);
		}

		return keywordScores;
	}

	private final String amazonCompletionURL =
		"http://completion.amazon.com/search/complete?search-alias=aps&mkt=1&q=";
	private final Float decreaseScore;
	private final Float initialScore;
	private final String keyword;
	private final Logger logger =
		LoggerFactory.getLogger(EstimateService.class);
	private final RestTemplate restTemplate = new RestTemplate();

}
