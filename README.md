# balance
Spring Boot application for account balance changing and reading. Uses pessimistic lock for updating balance in database, cache for reading balance, and its evicting when balance has changed. App can work with parrallel requests, and always return actual account balance.

Contains balance app itself, client app for testing balance app (runs requests to the app in infinite loop, contains parameters to change testing requests count).
Balance and Client apps could be started in IDEA after starting database, or using enclosed docker-compose file




Client's application.yml contains params for changing tests:

  thread-count - to choose count of threads used to send requests to balance app
  
  read-quota - number to set the weight of read requests in all sending requests
  
  write-quota - number to set the weight of write requests (changing balance) in all sending requests
  
  id-to-index - client uses list of all account ids to send requests, one can choose using only the part of the list up to the index
