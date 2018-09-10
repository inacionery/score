/**
 * 
 */

package com.inacionery.score.web.rest;

import com.inacionery.score.domain.Keyword;
import com.inacionery.score.service.EstimateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Inácio Nery
 */
@RestController
@RequestMapping("/")
public class EstimateResource {

	@Autowired
	private EstimateService estimateService;

	@GetMapping("/estimate")
	public ResponseEntity<Keyword> getEstimate(
		@RequestParam(value = "keyword") String keyword) {

		Integer score = estimateService.getKeywordScore(keyword);

		return ResponseEntity.ok(new Keyword(keyword, score));
	}

}
