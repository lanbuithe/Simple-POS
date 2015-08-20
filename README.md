README for Simple POS
==========================

### Deploy to OpenShift

* Installing the OpenShift Client Tools https://developers.openshift.com/en/managing-client-tools.html

* Login to OpenShift rhc ssh '<'app name'>'

* Go to repo directory cd app-root/repo

* git clone <git url>

* Go to <git directory>

* git remote add upstream <git url> 
 
* git pull -s recursive -X theirs upstream master

* git update-index --chmod=+x .openshift/action_hooks/*

* git stash

### Get token
curl -v -H 'Content-Type: application/x-www-form-urlencoded' -u posapp:mySecretOAuthSecret -X POST http://localhost:8080/oauth/token -d 'username=admin&password=admin&grant_type=password&client_id=posapp&client_secret=mySecretOAuthSecret'

### Get hold order
curl -v -H 'Content-Type: application/json' -X GET 'http://localhost:8080/api/orders?status=HOLD' -H 'Authorization: Bearer 9f79de77-bbad-47a5-838a-664a0cbefce6'


