## Model

1. Product
```json
{
  "name": "string, length > 0, required: true",
  "code": "string, length=13, required: true"
}
```

2. Order
```json
{
  "seller": "string, length=9, required: true",
  "customer": "string, length=9, required: true",
  "products": [
    /* list of products, non-empty, required: true*/
  ] 
}
```

## API Contract

Request: 
```
POST  /v1/order/
Headers: Content-Type: application/json
Body: 
{
    "seller" : "123456789",
    "customer" : "123456789",
    "products" : [
        {"code": "0000000000001", "name": "milk"}
    ]
}
```
Response codes: 

- **200** (+ order returned in response) - successfully passed
- **400** - validation error, invalid document format
- **500** - unexpected internal error

### Example:
```shell
curl --request POST 'http://localhost:8080/{context-path}/v1/order' \
             --header 'Content-Type: application/json' \
             --data-raw '{"customer": "123456787", "seller": "123456789", "products": [ {"code": "1234567891111", "name" : "milk"}]}'
```