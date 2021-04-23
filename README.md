# An agwt to vl REST interface



Set:

```shell
H="Content-Type: application/json"
URL=http://localhost:9099
```
Define the calibration request:

```shell
D="{\"CalibrationRequest\":{\"Comment\":\"\",
    \"PostReminder\":true,
	\"PreReminder\":true,
	\"MailTo\":\"thomas.bock@ptb.de\",
	\"RequestId\":\"34330b0e9f16c21007b37f2d562082e4\",
	\"Customer\":{},
	\"Date\":[{\"Type\":\"request\",\"Value\":\"2021-04-23\"},{\"Type\":\"desired\",\"Value\":\"2022-01-01\"}],
	\"Type\":\"agwt\",
	\"Device\":[{\"ToDo\":{},\"Amount\":1}]}}"
```

Send the request:

```shell
curl -H "$H" -d -X POST $URL/request
```

## Installation

Download latest standalone `jar` from http://a75438.berlin.ptb.de/vlagwt/

## Start server

```shell
java -jar vlagwt-x.y.z-standalone.jar
```
