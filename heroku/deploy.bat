echo "Maven clean"
call mvn clean

echo "Maven package"
call mvn package -Pprod -DskipTests

rem echo "Copy Procfile"
rem copy heroku\Procfile target\Procfile

echo "Deploy to Heroku"
call heroku deploy:jar -j target/*.war -i heroku/Procfile -a coffeepos

rem echo "Restart application"
rem call heroku restart --app coffeepos

echo "View log"
rem call heroku logs -t --app coffeepos