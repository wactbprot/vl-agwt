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

## convert a calibration request

The `/convert` endpoint returns the planning document generated from
the calibration request.

```shell
curl -H "$H" -d @resources/calibration-request.json -X POST $URL/convert
```

## request a calibration

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

## planning request

The `/planning/<RequestId>` endpoint returns the planning document 
belonging to the `<RequestId>`. 

```shell
curl -H "$H" $URL/planning/<RequestId>
```

Example:

```shell
curl -H "$H" $URL/planning/34330b0e9f16c21007b37f2d56bf1804
```
returns

```json
{"_id":"pla-2021-05-29-zenit","_rev":"1-7b647b22286b4de0dafeb36c023094f3","Planning":{"RequestId":"34330b0e9f16c21007b37f2d56bf1804","Date":[{"Type":"desired","Value":"2021-05-29"},{"Type":"CustomerRef","Value":"2021-05-12"},{"Type":"schedule","Value":"2021-05-29","Duration":5}],"Customer":{"Sign":"ZENIT","Lang":"de","Comment":"","Invoice":{},"AddName":"Zentrum für Innovation und Technik in NRW","Address":{"Town":"Mülheim an der Ruhr","Street":"Bismarckstr. 28","Zipcode":"45470","Land":"DE","District":"NW","Category":""},"Shipping":{},"Name":"ZENIT GmbH","Type":"Verwender","DebitorenNr":null,"Contact":{"Name":"ZENIT GmbH","Gender":"other","Email":"samuel.eickelberg@ptb.de","Phone":null,"Fax":null}},"Device":[{"ToDo":{"Name":"CDG-INF-120_0.13","DeviceClass":"CDG","Standard":"SE3","Type":"error","Cmc":true,"TimeBasedFee":true,"Gas":"N2","Values":{"Temperature":{"Type":"target","Unit":"C","Value":["23.0"]},"Pressure":{"Type":"target","Unit":"Pa","Value":[0.13,0.667,1.33,6.67,13.3,26.7,40,53.3,66.7,80,93.3,107,120],"N":[3,1,1,1,1,1,1,1,1,1,1,1,1]}}},"Type":"CDG","Amount":5}],"Comment":"Test","PostReminder":true,"PreReminder":true}}
```

```shell
curl -H "$H" $URL/planning/foo
```
returns

```json
{"error":"Not found","ok":false}
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
