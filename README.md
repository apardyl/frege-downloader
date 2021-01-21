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

You should also  mount volume that contains directory with repositories. 
You can do this using -v flag. i.e. `-v "path to repositories directory":/repositories/` .
Make sure the directory `/repositories/` is on the right side of the command, because that is where the repositories inside the container are stored

Sample container run:
`docker run --env  RMQ_HOST=172.17.0.3 --env RMQ_PORT=5672 --env DB_HOST=172.17.0.2 --env DB_PORT=5432 --env DB_DATABASE=frege --env DB_USERNAME=postgres --env DB_PASSWORD=password --env RMQ_REJECTED_PUBLISH_DELAY=5 -v /home/rep:/repositories/ jagiellonian/frege-downloader`

Service listens for RabbitMQ messages from queue `downloader` in the following format: 

```
{
    "repo_id": string,
    "git_url": string,
    "languages": [
     ...,
      language id: int,
     ...,
    ]
}
```
Sample message:

```
{
  "repo_id": "some_id",
  "git_url": "https://github.com/Software-Engineering-Jagiellonian/frege-downloader.git",
  "languages": [
    5,
    4,
    3
  ]
}

```
The languages field is **optional**
