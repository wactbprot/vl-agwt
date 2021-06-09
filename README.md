# An agwt to vl REST interface

Set:

```shell
URL=http://localhost:9099
```
if the server runs at `localhost`. For `PUT` or `POST` of json data set the content type by:

```shell
H="Content-Type: application/json"
```

## Installation

Download latest standalone `jar` from http://a75438.berlin.ptb.de/vlagwt/

## Start server

```shell
java -jar vlagwt-x.y.z-standalone.jar
```

## API
### available calibration procedures (ToDo)

```shell
curl $URL/todo
```

### convert a calibration request

The `/convert` endpoint returns the planning document generated from
the calibration request.

```shell
curl -H "$H" -d @resources/calibration-request.json -X POST $URL/convert
```

### request a calibration

Use the json file under `resources/calibration-request.json` and send the request:

```shell
curl -H "$H" -d @resources/calibration-request.json -X POST $URL/request
```

which leads to the following steps:

* input data (`inquiry` short `inq`) checks
* convert `inq` to a planning document (short `pla`)
* sends a notification email
* stores the document (`pla`) in the vl database

If everything goes right the reply is:

```json
{"ok":true,"error":null}
```

in case of an error:

```json
{"error":"<description of reason for error>", "ok":false}
```

### planning request

The `/planning/<RequestId>` endpoint returns the planning document 
belonging to the `<RequestId>`. 

```shell
curl $URL/planning/<RequestId>
```

Example:

```shell
curl $URL/planning/34330b0e9f16c21007b37f2d56bf1804
```
returns

```json
{"_id":"pla-2021-05-29-zenit","_rev":"1-7b647b22286b4de0dafeb36c023094f3","Planning":{"RequestId":"34330b0e9f16c21007b37f2d56bf1804" ...}
```

```shell
curl $URL/planning/foo
```
returns

```json
{"error":"Not found","ok":false}
```

### request the dcc(s)

```shell
curl $URL/dcc/<RequestId>
```
