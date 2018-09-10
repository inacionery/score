# Score

## Building

Run:

	./mvnw clean package

## Running

Run:

	java -jar target/score-0.0.1-SNAPSHOT.jar

## Example

Request:

	http://localhost:8080/estimate?keyword=linux

Response:

	{
	  "keyword": "linux",
	  "score": 40
	}