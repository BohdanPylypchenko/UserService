# User microservice (my Grid Dynamics Internship project)

## What is it?

### General story
During my Grid Dynamics internship (June, 4, 2022 - December, 30, 2022) I "worked" in an intern team. Our team was 
doing a **Car sharing** project. The project included 4 microservices - each microservice for each intern.

I was assigned to User microservice.

**NOTICE!!! This was a learning project, thus it did not have any commercial usage, so I am allowed 
to share my part of the project.**

### What exactly is User microservice?
The architecture included 4 microservices:
1. User service (my service)
2. Car service
3. Backoffice service
4. Trip service

While other services were responsible for different car sharing features, my service acted as a gateway for all 
requests from public internet. The service implemented user-related and security features: 
accounts, email confirmation, security (authentication/authorization), money balance etc.

### Service requirements

1. User account: email, password, first name, last name

   User type (ADMIN, USER, CAR_OWNER). *User can have more than one role

2. User authentication with email and password (passwords should be stored encoded in DB)

3. User registration confirmation via email

4. Users can get profile info and manage it (GET, PUT)
 
5. As a ADMIN i can disable or enable user profiles

6. As a ADMIN I can manage user balance

7. As a CAR_OWNER I can add my car to rent

8. As a CAR_OWNER I can disable/enable car(*connect with Car Service)

9. As a CAR_OWNER I want to see statistics of my car(*connect with Car Service)

10. As a USER I want to see my statistics(number of trips, trip data etc.)(*connect with car service and Backoffice service)

11. As a USER Link card or put some money on virtual balance (*add 3rd party card library)

*Authentication/Authorization should be done with JWT

### User Service API 
```
1. User registration (POST: /registration)

Allows registration for a new user. 
Security: permitAll

Example request:

    Body:
    {
        "email":"bob@gmail.com",
        "password":"password",
        "repeatPassword":"password",
        "firstName":"Bob",
        "lastName":"Alister",
        "age":21,
        "driverLicence":"SF7234SLL",
        "roles":["USER"]
    }

Example response (HttpStatus 201):

    Body:
    {
        "id": 1,
        "email": "bob@gmail.com",
        "password": "$2a$10$hTlj76.onzhNMv/sh64KZ.NQl30XxR7lhbOIeAeP8hO7d6UTJyo/C",
        "firstName": "Bob",
        "lastName": "Alister",
        "age": 21,
        "driverLicence": "fdf234sdr",
        "roles": [
        "USER"
        ],
        "status": "ENABLE"
    }


2. Login (POST: /login)

User login endpoint.
Security: permitAll
    
Example request:

    Body:
    {
        "email" : "bob@gmail.com"
        "password" : "password"
    }
    
Example response (HttpStatus 200):

    Body:
    {
        "email": "bob@gmail.com",
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJib2JAZ21haWwuY29tIiwicm9sZXMiOlsiQURNSU4iXSwiaWF0IjoxNjU5NjAzMzQzLCJleHAiOjE2NTk2MDM3MDN9.RL6Ao3LEMlGqCnzKha4eEZbKyO_cKjW4GL8oKrYBZTU"
    }


3. User get profile info (GET: /users)

Allows user to see information about himself.
Security: hasAuthority(USER)
        
Example request: 
        
    Path variable: 1 (user id)
    Url: /users/1
    
Example response (HttpStatus 200):

    Body:
    {
        "id": 1,
        "email": "bob@gmail.com",
        "firstName": "Bob",
        "lastName": "Alister",
        "age": 21,
        "driverLicence": "fdf234sdr",
        "roles": [
        "USER"
        ],
        "status": "ENABLE"
    }


4. User update profile information (PUT: /users)
        
Allows user to update some information about himself.
Security: hasAuthority(USER)

Example request:

    Path variable: 1 (user id)
    Body:
    {
        "email": "bob@gmail.com",
        "firstName": "Bob",
        "lastName": "Alister",
        "age": 25,
        "driverLicence": "SF78SD88K",
    }

    Url: users/1

Example response (HttpStatus 200):

    Body:
    {
        "id": 1,
        "email": "bob@gmail.com",
        "firstName": "Bob",
        "lastName": "Alister",
        "age": 25,
        "driverLicence": "SF78SD88K",
        "roles": [
        "USER"
        ],
        "status": "ENABLE"
    }


5. Admin disable/enable a user (PUT: /users/status/)
        
Allows admin to update users status.
Security: hasAuthority(ADMIN)

Example request:

    Path variable: 1   ({userId})
    Request param: ?status=disable
    Url: /users/status/1?status=disable

Example response (HttpStatus 200):

    Body:
    {
        "id": 1,
        "email": "bob@gmail.com",
        "firstName": "Bob",
        "lastName": "Alister",
        “password” : “sfjsdjagjlksdhfu39h38rfh8rfh3r8f”
        "age": 21,
        "driverLicence": "fdf234sdr",
        "roles": [
        "USER"
        ],
        "status": "DISABLE"
    }


6. Put money to a user balance (PUT: /users/tobalance)

Allows user and admin to charge users balance.
Security: hasAuthority(ADMIN, USER)

Example request:
        
    Path variable: 1 (user id)
    Request param: ?value=100.50
    Url: /profile/tobalance/1?value=100.50

Example response (HttpStatus 204):

    “$100.50 has been credited to the balance of the user with id 1”


7. Admin takes money from a user balance (PUT: /users/frombalance)
        
Allows admin to take money from a users balance.
Security: hasAuthority(ADMIN)

Example request:

    Path variable: 1 (user id)
    Request param:  ?value=100.50
    Url: /users/frombalance/1?value=100.50

Example response (HttpStatus 204):
    “100.50 dollars was debited from users balance with id 1”
    

8. Car owner add car to a rent (POST: /cars/add)

Allows car owner to add a new car to a rent.
Security: hasAuthority(CAR_OWNER)

Example request: 
        
    Path variable: 1 (car_owner id)
    Url: /cars/add/1

    Body: 
    {
        “brand”:”BMW”,
        “model”:”540i”,
        “gearboxtype”:”automatic”,
        … 
    }

Example response (HttpStatus 200): 

    “Your car BMW 540i was added to a rent.”        


9. Car owner Disable/Enable car (PUT: /cars/changestatus)

Allows car owner to disable/enable a car.
Security: hasAuthority(CAR_OWNER)

Example request:

    Path variable: 1 (car id)
    Request param:  ?status=disable
    Url: /cars/changestatus/1?status=disable

Example response (HttpStatus 200):

    “Your car BMW 540i  was disabled”
    

10. Car owner see statistics of his car (GET: /cars/statistics)

Allows car owner to get statistics about a car.
Security: hasAuthority(CAR_OWNER)
        
Example request:
        
    Path variable: 1 (cat id)
    Url: /cars/statistics/1

Example response (HttpStatus 200):

    “Cars some statistics”
    

11. User gets his trip statistics (GET: /users/statistics)
        
Allows user to get statistics about himserf.
Security: hasAuthority(USER)

Example request: 

    Path variable: 1 (user id)
    Url: /users/statistics/1

Example response (HttpStatus 200): 

    “Users some statistics”

```

## Final result

### What was implemented?

Because of my preparation to AWS Certified Cloud Practitioner exam / internship-end technical interview, I did not
fully complete my service.

**7/11 endpoints were fully implemented, tested, reviewed; including registration, email confirmation, 
JWT authentication/authorization, user profile management, user balance management (no 3rd party library)**

Notice: there are some minor API changes in implementation. Those did occur as a result of my teammates requests. 

### And what wasn't?

The service lacks integration endpoints to interact with other services.

Maybe it is not that big concern after all - it is possible to test and run the service as-is, 
no other services needed.