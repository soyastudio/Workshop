# Kafka Test

## DEV
### 1. ssh dgv012efa
```
    su - esd02ed
    password: #Today@321!
    cd /appl/esed/kafka

```

### 2. Publish Message
```
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS_Slots_C02.slots.slots AnjanaAccount.txt '{"slotId":"0dc86063-99e0-479d-a1f0-d7f5019b8018","storeId":"1623","dayOfWeek":"THURSDAY","slotStartTS":"2019-10-31T09:02:00.000-07:00","slotEndTS":"2019-10-31T10:00:00.000-07:00","slotExpiryTS":"2019-10-31T01:00:00.000-07:00","maxDeliveries":3,"serviceType":"DELIVERY","deliveryType":["ATTENDED","UNATTENDED"],"b2bCharge":{"userType":"BUSINESS","deliveryCharge":5.95,"minimumBasketSize":150,"alertBasketSize":9999,"reducedDeliveryCharge":5.95,"deliveryChargeUPC":"0000000022151","reducedDeliveryChargeUPC":"0000000022151"},"b2cCharge":{"userType":"RESIDENTIAL","deliveryCharge":5.95,"minimumBasketSize":150,"alertBasketSize":9999,"reducedDeliveryCharge":5.95,"deliveryChargeUPC":"0000000022151","reducedDeliveryChargeUPC":"0000000022151"},"slotType":"ONEHR","slotPlan":"STANDARD","reservedByCurrentUser":false,"bookings":[{"userGuid":"300-090-1404680433134","storeId":"1623","orderId":"4860379","versionNumber":"1","deliveryType":"ATTENDED","expiryTime":"2019-10-31T01:00:00.000-07:00"},{"userGuid":"520-000-0290087547378","storeId":"1623","orderId":"12016946","cartId":"3060470","deliveryType":"ATTENDED","expiryTime":"2019-10-29T11:00:23.000-07:00","bookingCategory":"Rebooking"},{"userGuid":"300-090-1546398103054","storeId":"1623","cartId":"3260544","deliveryType":"ATTENDED","expiryTime":"2019-10-30T15:06:59.000-07:00","bookingCategory":"Subscribed"}],"isMigrated":true,"migrationDate":"2019-10-23T22:19:02.525-07:00"}'
 
```
  
### 3. Consume Message
```
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_DeliverySlot latest DeliverySlot.text
 
```

## QA
### 1. ssh qgv012efb
```
    su - esq03ed
    password: #Today@321!
    cd /appl/esed/kafka

```

### 2. Publish Message 
```
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS_Slots_C02.slots.slots per.txt '{"slotId":"0dc86063-99e0-479d-a1f0-d7f5019b8018","storeId":"1623","dayOfWeek":"THURSDAY","slotStartTS":"2019-10-31T09:02:00.000-07:00","slotEndTS":"2019-10-31T10:00:00.000-07:00","slotExpiryTS":"2019-10-31T01:00:00.000-07:00","maxDeliveries":3,"serviceType":"DELIVERY","deliveryType":["ATTENDED","UNATTENDED"],"b2bCharge":{"userType":"BUSINESS","deliveryCharge":5.95,"minimumBasketSize":150,"alertBasketSize":9999,"reducedDeliveryCharge":5.95,"deliveryChargeUPC":"0000000022151","reducedDeliveryChargeUPC":"0000000022151"},"b2cCharge":{"userType":"RESIDENTIAL","deliveryCharge":5.95,"minimumBasketSize":150,"alertBasketSize":9999,"reducedDeliveryCharge":5.95,"deliveryChargeUPC":"0000000022151","reducedDeliveryChargeUPC":"0000000022151"},"slotType":"ONEHR","slotPlan":"STANDARD","reservedByCurrentUser":false,"bookings":[{"userGuid":"300-090-1404680433134","storeId":"1623","orderId":"4860379","versionNumber":"1","deliveryType":"ATTENDED","expiryTime":"2019-10-31T01:00:00.000-07:00"},{"userGuid":"520-000-0290087547378","storeId":"1623","orderId":"12016946","cartId":"3060470","deliveryType":"ATTENDED","expiryTime":"2019-10-29T11:00:23.000-07:00","bookingCategory":"Rebooking"},{"userGuid":"300-090-1546398103054","storeId":"1623","cartId":"3260544","deliveryType":"ATTENDED","expiryTime":"2019-10-30T15:06:59.000-07:00","bookingCategory":"Subscribed"}],"isMigrated":true,"migrationDate":"2019-10-23T22:19:02.525-07:00"}'
 
```
    
### 3. Consume Message
```
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_DeliverySlot latest DeliverySlot.text
 
```
