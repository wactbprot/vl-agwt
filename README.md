# An agwt to vl REST interface

Set

```shell
H="Content-Type: application/json"
URL=http://localhost:9099
```

and use it this way:

```shell
curl -H "$H" -X POST $URL
```

## post calibration request object [POST]


```shell
D ='{"Customer":{
        #AGWT format
    }, 
     "ToDo":{
        #VL fotmat
    }
}'

curl -X POST -d "$D" -H "$H" $URL/cal-req
```

