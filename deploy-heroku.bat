echo "Maven clean"
call mvn clean

echo "Maven package"
call mvn package -Pprod -DskipTests

rem echo "Copy Procfile"
rem copy Procfile target\Procfile

rem "Update databasechangeloglock"
rem heroku pg:psql -c "update databasechangeloglock set locked=false;" -a coffeepos

echo "Deploy to Heroku"
call heroku deploy:jar -j target/*.war -i Procfile -a coffeepos

echo "Restart application"
rem call heroku restart --app coffeepos

echo "View log"
rem call heroku logs -t --app coffeepos