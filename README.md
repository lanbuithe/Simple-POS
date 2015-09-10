README for Simple POS
==========================

### Deploy to OpenShift

* Installing the OpenShift Client Tools https://developers.openshift.com/en/managing-client-tools.html

* Login to OpenShift rhc ssh < app name >
* Go to repo directory cd app-root/repo
* git clone < git url >
* Go to < git directory >
* git remote add upstream < git url > 
* git pull -s recursive -X theirs upstream master
* git update-index --chmod=+x .openshift/action_hooks/*
* git stash
* Package application sh .openshift/action_hooks/build
* Start application sh .openshift/action_hooks/start
* Stop application sh .openshift/action_hooks/stop

### Get Token
curl -v -H 'Content-Type: application/x-www-form-urlencoded' -u posapp:mySecretOAuthSecret -X POST http://localhost:8080/oauth/token -d 'username=admin&password=admin&grant_type=password&client_id=posapp&client_secret=mySecretOAuthSecret'

### Get Hold Order
curl -v -H 'Content-Type: application/json' -X GET 'http://localhost:8080/api/orders?status=HOLD' -H 'Authorization: Bearer 9f79de77-bbad-47a5-838a-664a0cbefce6'

### Create order
curl -v -H 'Content-Type: application/json' -X POST 'http://localhost:8080/api/orders' -d '{"quantity":1,"amount":12000,"discount":0,"tax":0,"discountAmount":0,"taxAmount":0,"receivableAmount":12000,"details":[{"item":{"createdBy":"admin","createdDate":"2015-09-02T17:38:57Z","id":1,"name":"Cafe Đá","price":12000,"description":null,"category":{"createdBy":"admin","createdDate":"2015-09-02T17:38:46Z","id":1,"name":"Cafe","description":null},"categoryName":"Cafe"},"quantity":1,"amount":12000,"itemId":1}],"tableNo":{"createdBy":"admin","createdDate":"2015-09-02T17:39:08Z","id":1,"name":"Bàn Số 1","description":null},"status":"PAYMENT"}' -H 'Authorization: Bearer '


