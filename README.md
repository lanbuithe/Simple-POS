README for coffee
==========================

## Get token
curl -v -H 'Content-Type: application/x-www-form-urlencoded' -u coffeeapp:mySecretOAuthSecret -X POST http://localhost:8080/oauth/token -d 'username=admin&password=adm in&grant_type=password&client_id=coffeeapp&client_secret=mySecretOAuthSecret'

## Get hold order
curl -v -H 'Content-Type: application/json' -X GET 'http://localhost:8080/api/orders?status=HOLD' -H 'Authorization: Bearer 9f79de77-bbad-47a5-838a-664a0cbefce6'