
**Build package**:

    mvn clean package
    
**Run application**:
   
    java -jar target/gr-payment-1.0-SNAPSHOT.jar 


###REST API

**Get all accounts**:

    GET http://localhost:8080/rest/v1/accounts/
response example:
```json
[
    {
        "uid": "111",
        "ownerName": "AAA",
        "balance": 0
    },
    {
        "uid": "222",
        "ownerName": "BBB",
        "balance": 300
    }
]
```

**Get account by uid**:

    GET http://localhost:8080/rest/v1/accounts/{uid}
    
response example:
```json
{
    "uid": "111",
    "ownerName": "AAA",
    "balance": 0
}
```

**Create account**:
    
    POST http://localhost:8080/rest/v1/accounts/
    
body example:
```json
{
    "uid": "999",
    "ownerName": "Ivan",
    "balance": 123
}
```

**Money transfer**:
    
    POST http://localhost:8080/rest/v1/accounts/transfer
    

body example:
```json
{
    "from": "111",
    "to": "222",
    "amount": 5
}
```