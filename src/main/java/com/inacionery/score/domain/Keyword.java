/**
 * 
 */

package com.inacionery.score.domain;

/**
 * @author Inácio Nery
 */
public class Keyword {

	private String keyword;
	private Integer score;

	public String getKeyword() {

		return keyword;
	}

	public Integer getScore() {

		return score;
	}

	public Keyword(String keyword, Integer score) {

		this.keyword = keyword;
		this.score = score;
	}

}
