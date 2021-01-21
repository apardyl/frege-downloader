# frege-downloader

docker image can be found under the name "jagiellonian/frege-downloader"

## Docker environment variables

   * `RMQ_HOST` - RabbitMQ host
   
   * `RMQ_PORT` - RabbitMQ port 

   * `DB_HOST` - PostgreSQL server host

   * `DB_PORT` - PostgreSQL server host 

   *  `DB_DATABASE` - Database name

   *  `DB_USERNAME` - Database username

   *  `DB_PASSWORD` - Database password
   
   *  `RMQ_REJECTED_PUBLISH_DELAY` - After recieving NACK service will wait as many seconds as the value of this variable is before trying to publish message again

**All the environment variables mentioned above are required to run the container**
