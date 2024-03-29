# Practical Assignment #1 - The Acme Electronic Supermarket  

By:
- Bruno Mendes [up201906166](mailto:up201906166@edu.fe.up.pt)
- Fernando Rego [up201905951](mailto:up201905951@edu.fe.up.pt)
- Gustavo Santos [up201907397](mailto:up201907397@edu.fe.up.pt)
- José Costa [up201907216](mailto:up201907216@edu.fe.up.pt)

## How to run

You may use the server by getting its dependencies and then firing up the Go application, with the MongoDB database up:

Inside the `server` folder, run:
```bash
    docker-compose up -d
    go get .
    go run .
```

If you plan on using the admin functionalities of the application you might also want to add admin users to add products and register purchases. To do so, inside the `server` directory, run:
```bash
    ./add_admin.sh <nickname> <password>
```

Then, fire up the Android application using your preferred method (we recommend sideloading with `Android Studio`). Since we did not deploy the web server in the cloud, make sure to change the server IP address in `res/values/string_config.xml` to your machine's.

## Overview

![architecture diagram](docs/architecture_diagram.drawio.png)

We have developed two artifacts: an Android application and a web server to support its operations. The Android app, upon login, disambiguates to client or admin mode accordingly to the user type that is registered on the server.

### Customer App

The customer app starts by greeting the user with their accumulated balance, available to be used in purchases, and display their earned coupons, also to be used in purchases. If they want to, they can review their transaction history, which includes paid price and product list, on the `Receipts` tab of the bottom navigation bar. This was made possible by creating a nested `RecyclerView`.

|       |        |
| -------------- | -------------- |
| ![client home](docs/img/client_home.jpg) | ![client home](docs/img/client_receipts.jpg)

Tapping `New Purchase`, the user may initiate a purchase and start scanning product QR codes, printed on the physical supermarket. When they are about to leave the supermarket, they should tap `Checkout`, choose whether to use available balance and coupons, and then conclude the operation by checking out on a terminal, with QR code or NFC.

|       |        |        |        |
| -------------- | -------------- | -------------- | -------------- |
| ![client purchase](docs/img/client_purchase.jpg)   | ![client purchase checkout](docs/img/client_purchase_checkout.jpg "title-1")   | ![client purchase qr](docs/img/client_purchase_qr.jpg) | ![client purchase nfc](docs/img/client_purchase_nfc.jpg)|



### Terminal App

The terminal app is a more pragmatic one, allowing at the home page the generation of new supermarket products, with name, image and price, and displaying the respective QR codes.

|       |        |        |        |
| -------------- | -------------- | -------------- | -------------- |
| ![client purchase nfc](docs/img/terminal_products.jpg) | ![client purchase nfc](docs/img/terminal_new_product.jpg) | ![client purchase nfc](docs/img/terminal_new_product_img.jpg) | ![client purchase nfc](docs/img/terminal_product_qr.jpg) |

On the `Checkout` tab, the terminal is able to listen for a QR code or NFC purchase. After receiving the purchase payload from the client, it validates it with the server and displays a result message.

|       |        |
| -------------- | -------------- |
| ![client purchase nfc](docs/img/terminal_checkout.jpg) | ![client purchase nfc](docs/img/terminal_purchase_check.jpg)

### Web Service
To support the several operations of the application, we developed a `RESTful` service using the *Go* language and also a non-relational database using *MongoDB*.

The server provides several endpoints that the application can use to add or retrieve information. These endpoints are divided by groups, each group with the right middlware to verify that the authentication used is valid to the correspondent request.

All the information needed for the application is stored into three database collections that can be represented by the following UML:
 
![mongodb collections](docs/mongodb.svg)

## Main Features

### Registration
Registering the user means registering them on the database, and allow their operations on the store to be persisted, which is great for both parties: the store has data ready to be used to predict customer behavior and the customer gets a glance of their past activity and current account status.
Due to the way we have implemented session with JWT (see below), this also allows the user to exchange phones and maintain their history.

### Adding to the shopping basket
In this digital future, it's the user's responsability to scan their products' references while picking them from the shelves. They can do so by scanning the QR codes printed next to the products in the market.

### Checkout
When they are ready to leave, the user must complete their purchase by using NFC or a QR code (containing transaction data) to validate it in the market terminals. The money will then be billed to their account (of course, this was not implemented, actually) and then the doors will open.

### Payment and Result
The terminal validates the purchase with the web server and displays a success message, with total amount paid (already discounting the coupons and balance), or an error message. In case of the latter, the customer should ask for the assitance of a human operator.

### Past Transactions and Coupons
The registered transactions in the server associated with the logged in user are available at all times. They include total amount paid and product list with quantity. The same can be said for available coupons.

### Coupons
Coupons are a way to incentivize the user to keep using the store. They are generated by the server for every 100€ spent and can be used to discount 15%  of another purchase from the total purchase price to account balance, that can be used in the next purchases. Coupons are identified by a single UUID and are associated with the user and the server checks this association when the user tries to use a coupon.

### Cryptography
In addition to the HMAC encryption used in the JWT's, that already identify and encode the user ids and roles, 512-bit RSA keys were used for encrypting or signing messages, further preventing ill intended actors of interfering with normal operation.

Product QR codes contain a message that is encrypted with the public key of the store. The store's private key is shared with the application upon registration or login, so the application can decrypt the QR code and add the product to the cart. This is put in place to prevent the user from adding products that are not in the store, or that are not in the store's database, or that have conflicting information. As encrypting with a 512 bit key and PKCS1 v1.5 padding only allows for 53 bytes of data, we had to split the message in fields. Each product field contains a base64 encoded string with the encrypted field value. The message reads as follows:

```json
{
    "uuid": "base64 encoded encrypted product uuid",
    "name": "base64 encoded encrypted product name",
    "price": "base64 encoded encrypted product price",
}
```

To validate purchases are being made by the user, the purchase messages shared between client and server, through the terminal (either by QR code of NFC), are signed with the private key, also with 512 bit, of the user using PKCS1 v1.5. The server then validates the signature with the public key of the user, shared in the login process, and if it is valid, it proceeds to validate the purchase. The message shared with the terminal has the following format: 
```json
{
    "b64MessageString": {
        "user_uuid" : "user uuid",
        "discount" : true | false, // whether the user is using available balance
        "coupon" : true | false, // whether the user is using a coupon
        "cart" : [
            {
                "uuid" : "product uuid",
                "quantity" : "product quantity"
            },
            ...
        ],
    },
    
    "Signature": "base64 encoded signature"
}
```

The format of signed messages shared with the server is as follows:

Signature HTTP header set with the base64 encoded signature of the message body.

- For HTTP request that allow body:
```json
{
    "b64MessageString" : "base 64 encoded message string",
}
``` 

- For HTTP request that do not allow body:

Signed-Message HTTP header set with the base64 encoded signature of the message body. Here the message is a small string with throwaway data, as the signature is what matters.

To validate product creation/deletion on the admin mode, this method is also employed, preventing non-admin users from creating or deleting products.

## Additional Features

### NFC
Besides the mandatory primary means of performing a purchase checkout (using the QR code), we allow the user to checkout via NFC if they prefer so. In that mode, the terminal app enters reader mode, while the client app enters card emulation mode. The data exchange protocol, due to the small maximum size of a data packet, allows dividing the data in chunks (at the end of each packet transfer, the client appends `OK` or `MORE` so that the reader knows whether to consider the next packets as part of the purchase model or dispatch to the server immediately) and is resilient to errors (if the connection is broken, the next sent packet will be the first one again).

### JWT
In addition to the required encryption, we use JSON Web Tokens, that defines a self-contained way for securely transmitting information between parties. This is an additional layer of security on top of the encryption, but also allows us to uninstall the app and still use the same user with their history (we generate new keys upon login, not registration). Our JWT implementation includes an expiration after 1 week, after which the user is forced to do a relogin. Possible improvements could include adding a refresh token to the equation, which was considered overkill for the current scale of the project.

### Swipe to refresh
The feature swipe to refresh, using `SwipeRefreshLayout`, allows both client and admin to refresh the application and obtain the more recent information from the database. The client has the option to refresh the user information and the list of active coupons by making a vertical swipe in the `HomeFragment`. The admin can update the list of the products also by making a vertical swipe in the `ProductsFragment` 

### Product Generation
While not mandatory, we decided it was best to include on-demand product generation in the admin app. This means that the supermarket products exist in the server, which knows how to generate product QR code payloads, and checks if the products of a purchase match the registry. The admin app is so capable of generating new products, display their QR code, and export its image to the device external (scoped) storage.

### Product Images
The products may be identified with an image, sent to the server as a byte payload, and chosen through the admin application with the camera or the file picker. These images will be shown to users while on a purchase to confirm they are validating the right product.

### Persistency
The admin products, client balance, receipts and coupons are saved in the local database and easily accessed even without an Internet connection. Of course, in that case, server-dependant features such as a checkout will yield a connection error.

### Internationalization
Every string in the application, including programatic ones (such as dialog titles) are both in Portuguese and English, so that the application can be used in both Portuguese and British setups across the world. Not only that but since we made it compatible with Android's App Languages feature, the user can also change the language just for our app directly from their system settings.

### App Shortcuts
Besides launching the app normally, the user also has two shortcuts to choose from which can be used straight from the system launcher by long pressing the app's icon. One to open the receipts tab and another to start a new purchase.

### Attention to Detail
We maintained a general attention to add simple features which add value to a user like a dark mode that automatically follows system settings, an animated splash screen when opening the app (notice the rotation in the e letter), a themed icon compatible with Android 13+, full compatibility with Android's predictive back gesture and textfields that rise with the keyboard so no important view (like the login button) is hidden by the input method.

## Performed Tests
We have tested the app thorougly with manual acceptance tests and across several devices, with and without NFC (the main hardware differentiator that influences our app's operation), as well as different Android API levels (26, 29, 30, 31, 32, 33, Android 14(Beta) ). This could be improved with unit testing and acceptance testing, in the future.

## References

- [Android API Reference](https://developer.android.com/reference)
- [Class Slides](https://moodle.up.pt/course/view.php?id=2244)
