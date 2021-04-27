# An agwt to vl REST interface

Set:

```shell
H="Content-Type: application/json"
URL=http://localhost:9099
```

## available calibration procedures (ToDo)

```shell
curl -H "$H" $URL/todo
```

## request a calibration

Use the json file under `resources/calibration-request.json` and send the request:

```shell
curl -H "$H" -d @resources/calibration-request.json -X POST $URL/request
```

## request the dcc(s)


```shell
curl -H "$H"  $URL/dcc/<RequestId>
```


## Installation

Download latest standalone `jar` from http://a75438.berlin.ptb.de/vlagwt/

## Start server

```shell
java -jar vlagwt-x.y.z-standalone.jar
```
