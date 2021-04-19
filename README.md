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

## request cer-document [GET]

```shell
curl -H "$H"  $URL/cer/<pla-id>
```

example:


```shell
curl -H "$H"  $URL/cer/pla-2022-01-01-dummy_a
```


## generate cer-document [POST]

```shell
curl -X POST -d '{"save": true}' -H "$H"  $URL/cer/<pla-id>
```

