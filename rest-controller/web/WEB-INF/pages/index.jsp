<body>
    <h3>Use case:</h3>

    <pre>
        curl --request POST 'http://localhost:8080/{context-path}/v1/order' \
             --header 'Content-Type: application/json' \
             --data-raw '{"customer": "123456787", "seller": "123456789", "products": [ {"code": "1234567891111", "name" : "milk"}]}'
    </pre>
</body>