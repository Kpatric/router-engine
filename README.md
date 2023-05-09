# router-engine
Rest api to provide wallet withdrawals.

The api is expected to enable transfer of money  from wallet account to bank account.

// UML Sequence Diagram

User -initiate request-> router Engine

router Engine -Check if the funds are available-> Wallet

Wallet -. funds available-> router Engine

router Engine -. verify user and funds -> User

router Engine - persist transaction -> router Engine

router Engine -deposit to recipient-> Bank Engine

Bank Engine -. successful deposit -> router Engine

router Engine - update transaction status -> router Engine

router Engine - update wallet balance -> Wallet

Wallet -. update successful -> router Engine

router Engine -. transaction successful -> User

![gleek-bZ19SucTj-E-d3tPwyLQPg(1)](https://user-images.githubusercontent.com/19501425/236657647-d1095f38-ab91-484b-99f4-fed6a459b8c2.png)

High level Architectural diagram.

![Github Actions-Otop Solution drawio(1)](https://user-images.githubusercontent.com/19501425/236657638-518a6734-8fd5-4480-af91-be581430f1d4.png)

How to run the application
1. Have docker on your machine.
2. Run docker-compose build
3. Run docker-compose up
4. Import postman collection file under postman_collection folder
5. Execute the post

Expectation
1.Check wallet balance to be done and compared with the amount to be transferred.
2.If balance is less than the amount return.
3.Conitune if balance higher. Calculate charges and do the bank transfer.
4.If transfer successful, update the wallet balance
5.Persist the transaction.

What I could have done better

1.I could have included security module with spring security 

2.I could have have included cloud architecture in my design

3.I could have used k8s for orchestration
