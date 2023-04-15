#! /bin/bash

# Add a new admin user
# Print usage 
if [ $# -ne 2 ]; then
    echo "Usage: $0 <nickname> <admin_password>"
    exit 1
fi

curl --location '127.0.0.1:8000/register' \
--header 'Content-Type: application/json' \
--data '{
    "card": {
        "card_cvv":"123",
        "card_date":"12/12",
        "card_number":"1234123412341434"
    },
   "name" : "Admin Test",
   "nickname" : "'$1'",
   "password" : "'$2'",
   "public_key":"QUJDZGVmZ2hp",
   "is_admin": true,
   "admin_key": "admin"
}'