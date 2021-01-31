# Design
## Flow Chart
``` mermaid
graph LR
  subgraph SRC [UCA]
    UCA
  end

    UCA --> AKS
    AKS --> KIT

  subgraph EDIS [EDIS - Customer Preference Management]
    KIT --> |<Json Data>|IIB
    IIB --> |<CMM XML>|KOT
  end

    KOT --> AKT
    AKT --> END

  subgraph TGT [EDM]
    END
  end

  UCA((Source))
  AKS[(Azure Kafka SRC Topic)]
  
  KIT[(Kafka Consumer Topic)]
  IIB[ESED_Customer_Preference_Management_IH_Publisher]

  KOT[(Kafka Producer Topic)]

  AKT[(Azure Kafka TGT)]
  END((Consumer))         
            
```

## Sequence Chart
``` mermaid
sequenceDiagram
    SRC(UCA)->>Kafka Inbound Topic: publish
    Kafka Inbound Topic-->>IIB Transformer: Json Message
    IIB Transformer-->>Kafka Outbound Topic: CMM XML
    Kafka Outbound Topic->>Consumer(EDM): subscribe 

```



# RIT

## DEV:
RITM0701516

## QA:
RITM0702864

## PROD:
RITM0713755


# DEV
## 1. ssh dgv012efa

    su - esd02ed
    password: #Today@321!

    cd /appl/esed/kafka


## 2. Publish Message
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer {TOPIC_NAME} {key}.txt '{MSG}'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer IAUC_C02_DEV_CFMS_AGGREGATE AnjanaAccount.txt '{"_id":"CustomerPreferences_28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","state":{"_class":"PreferenceAggregateDomain_v1","createTimestamp":1611875712045,"lastUpdateTimestamp":1611875712045,"createClientId":"UMA","lastUpdateClientId":"UMA","createHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","lastUpdateHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","sequenceNumber":1,"timestamp":1571994962492,"aggregateRevision":1,"version":1,"eventId":"98d5779b-24a6-4aac-9c1c-4e5a4a3e677f","guid":"456-042-1611875708495","preferences":[{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":1,"type":"EMAIL_SAVE","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":2,"type":"J4U","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":3,"type":"GROCERY_DEL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":4,"type":"PROD_RECALL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]}]}}'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS-EMOM_C02_ORDER AnjanaAccount.txt '{"orderNumber":"20027908","versionNumber":1,"orderStatus":"CREATED","companyId":"1","banner":"Safeway","storeNumber":"1502","orderCreatedDate":"2020-07-27T12:54:58.114Z","sourceInfo":{"source":"ECOMMERCE","enteredBy":"CUSTOMER","deviceType":"WEB"},"orderTotal":{"amount":"107.09","currency":"USD"},"customer":{"customerId":"Qmr-n1L-0FaAnstCAw2nY","clubCardNumber":"rB2MWhv3PEr","isSubscription":false,"name":{"firstName":"Y3PR","lastName":"irM"},"address":[{"addressType":"epgj-qg","addressLine1":"wqqz GLuLB 4I2j cV","city":"tMpZJg6gSq","state":"Hpz","zipCode":"csQIO","country":"USA"}],"contact":[{"number":"5zvece3V9h","type":"MOBILE"}],"email":[{"id":"jGxJ0E4.xk6sKQ8@bQTSN.vix","type":"PERSONAL"}],"preference":{"optIn":[{"type":"TEXT","id":"8612345455","isOptin":true}]}},"paymentDetails":[{"paymentType":"CREDITCARD","paymentSubType":"VISA","tokenNumber":"KPHL1Xh1ydMgpzI9","cardExpiryMonth":"qPbj","cardExpiryYear":"23","zipcode":"s9P0y","reqAuthorizationAmount":"107.09","cardHolderName":"j7yxG7D M b","address":{"zipCode":"HBbOs"},"paymentStatus":{"status":"NOTAUTHORIZED","authorizationCode":"x7VLYvdF","authorizationDate":"2020-07-27T12:54:58.147Z"}}],"subOrders":[{"subOrderNumber":1,"fulfillmentType":"DUG","customerService":{"contact":[{"number":"6987079608","type":"MOBILE","subType":"DUG_VIRTUAL_PHONE"},{"number":"7987875877","type":"MOBILE","subType":"OPERATION_PHONE_MOBILE1"},{"number":"6987097659","type":"PHONE","subType":"OPERATION_PHONE_MOBILE2"},{"number":"6876329847","type":"PHONE","subType":"OPERATION_PHONE_MOBILE3"},{"number":"6873698739","type":"PHONE","subType":"OPERATION_PHONE_MOBILE4"},{"number":"6873269368","type":"MOBILE","subType":"OPERATION_PHONE_MOBILE5"},{"number":"7327498732","type":"PHONE","subType":"OPERATION_PHONE_MOBILE6"},{"number":"6876987769","type":"MOBILE","subType":"CUST_SERV_PHONE"},{"number":"6869869868","type":"MOBILE","subType":"STORE_PHONE"}]},"deliveryInfo":{"slotInfo":{"slotPlan":"STANDARD","slotType":"ONEHR","slotId":"65ad7565-da54-438a-ab7f-975a38bba712","timeZone":"America/Los_Angeles","startTime":"2020-07-27T17:00:00.000Z","endTime":"2020-07-27T18:00:00.000Z","editingCutoffDate":"2020-07-27T14:00:00.000Z","lastPickupTime":"2020-07-28T03:00:00.000Z"},"instructions":"","pickupInfo":{"locationType":"STORE","locationId":"1502"},"stageByDateTime":"2020-07-27T18:00:00.000Z"},"charges":[{"id":"0000000029103","name":"BagFee","category":"ServiceFee","chargeAmount":{"amount":"0.1","currency":"USD"}}],"promoCodes":[],"orderLines":[{"itemId":"960304350","itemDescription":"Value Corner Eggs Large Family Pack - 60 Count","orderedQuantity":2,"unitOfMeasure":"CT","unitPrice":{"amount":"10.29","currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false},{"itemId":"148020081","itemDescription":"Marie Callenders Entree Pot Pie Chicken - 4-10 Oz","orderedQuantity":2,"unitOfMeasure":"OZ","unitPrice":{"amount":"6.99","currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false},{"itemId":"960031028","itemDescription":"Signature Farms Boneless Skinless Chicken Breasts Value Pack - 3 Lbs.","orderedQuantity":1,"unitOfMeasure":"LB","unitPrice":{"amount":"12.57","currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false},{"itemId":"960105958","itemDescription":"Tide Plus Laundry Detergent Liquid HE Turbo Clean Downy April Fresh - 92 Fl. Oz.","orderedQuantity":4,"unitOfMeasure":"FZ","unitPrice":{"amount":"14.99","currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false}]}],"storeInfo":[{"key":"isHybridStore","value":"true"},{"key":"isMFC","value":"false"},{"key":"isErumsEnabled","value":"true"},{"key":"isPremiumStore","value":"true"},{"key":"is3PLStore","value":"true"},{"key":"isDUGArrivalEnabled","value":"true"}]}'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS-EMOM_C02_ORDER AnjanaAccount.txt '{"orderNumber":"37310876","versionNumber":1,"orderStatus":"COMPLETED","companyId":"1","banner":"Safeway","storeNumber":"1490","orderCreatedDate":"2020-07-15T19:33:21.043Z","sourceInfo":{"source":"ECHO","enteredBy":"CUSTOMER","deviceType":"WEB","affiliate":{"affiliateName":"IBOTTA","affiliateOrderRef":"A123544334-123"}},"tender":[{"tenderType":"REFUND","tenderSubType":"credit_card_refund","chargeAmount":{"amount":3.95,"currency":"USD"}},{"tenderType":"CANCELLATION","tenderSubType":"credit_card_refund","chargeAmount":{"amount":3.95,"currency":"USD"}}],"orderTotal":{"amount":79.87,"currency":"USD"},"customer":{"customerId":"300-368-1000020461","clubCardNumber":"41032675319","isSubscription":false,"name":{"firstName":"THEODORE","lastName":"CHIAO"},"address":[{"addressType":"SHIP-TO","addressLine1":"75 Folsom St Apt 901","addressLine2":" ","city":"San Francisco","state":"CA","zipCode":"94105","country":"USA"}],"contact":[{"number":"4155336055","type":"MOBILE"}],"email":[{"id":"tedhchiao@gmail.com","type":"PERSONAL"}],"preference":{"optIn":[{"type":"TEXT","id":"4155336055","isOptin":true}]}},"paymentDetails":[{"paymentType":"CREDITCARD","paymentSubType":"American Express","tokenNumber":"933418729074015","cardExpiryMonth":"07","cardExpiryYear":"20","zipcode":"94105","reqAuthorizationAmount":"79.87","cardHolderName":"Theodore Chiao","address":{"zipCode":"94105"},"paymentStatus":{"status":"AUTHORIZED","authorizationCode":"150569","authorizationDate":"2020-07-16T06:33:21.079Z"}},{"paymentType":"CREDITONACCOUNT","paymentSubType":"COA","reqAuthorizationAmount":"10","paymentStatus":{"status":"AUTHORIZED","authorizationDate":"2020-07-07T18:18:04.476Z"}},{"paymentType":"EBT","paymentSubType":"EBT","reqAuthorizationAmount":"20","paymentStatus":{"status":"SUSPENDED"}}],"subOrders":[{"subOrderNumber":1,"fulfillmentType":"DELIVERY","customerService":{"contact":[{"number":"8775054040","type":"PHONE","subType":"CUST_SERV_PHONE"}]},"deliveryInfo":{"deliverySubType":"RESIDENTIAL","slotInfo":{"slotPlan":"STANDARD","slotType":"FOURHR","slotId":"7e4232d0-dc62-4156-8844-3040468f788f","timeZone":"America/Los_Angeles","startTime":"2020-07-16T15:01:00.000Z","endTime":"2020-07-16T19:00:00.000Z","editingCutoffDate":"2020-07-16T08:00:00.000Z"},"deliveryServiceType":"ATTENDED","instructions":"","stageByDateTime":"2020-07-16T19:00:00.000Z"},"charges":[{"id":"0000000029103","name":"BagFee","category":"ServiceFee","chargeAmount":{"amount":0.1,"currency":"USD"}},{"id":"0000000022155","name":"DeliveryFee","category":"DeliveryFee","chargeAmount":{"amount":3.95,"currency":"USD"}}],"promoCodes":[],"orderLines":[{"itemId":"05050000022","itemDescription":"FJ LIVERWURST","orderedQuantity":2,"unitOfMeasure":"UNIT","unitPrice":{"amount":4.99,"currency":"USD"},"suppliedQuantity":2,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":4.99,"currency":"USD"},"itemTotalTax":"0.0","discountsApplied":"-0.99","isRegulatedItem":false}],"refundedOrderLines":[{"itemDescription":"FJ LIVERWURST","refundedQuantity":2,"unitOfMeasure":"","unitPrice":{"amount":4.99,"currency":"USD"}}],"subOrderStatus":"COMPLETED"}],"storeInfo":[{"key":"isHybridStore","value":"true"},{"key":"isMFC","value":"false"},{"key":"isErumsEnabled","value":"true"},{"key":"isPremiumStore","value":"true"},{"key":"is3PLStore","value":"true"}]}'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS-EMOM_C02_ORDER AnjanaAccount.txt '{"orderNumber":"20030881","versionNumber":1,"companyId":"1","banner":"Safeway","storeNumber":"1502","orderStatus":"COMPLETED","orderCreatedDate":"2020-08-13T18:04:33.320Z","sourceInfo":{"source":"ECHO","enteredBy":"CUSTOMER","deviceType":"MOBILE","affiliate":{"affiliateName":"IBOTTA","affiliateOrderRef":"fakesrctok-1b3b12345f7f1056"}},"orderTotal":{"amount":24.12,"currency":"USD"},"customer":{"customerId":"557-220-1596564497706","clubCardNumber":"49372831113","isSubscription":true,"name":{"firstName":"raja","lastName":"ram"},"address":[{"addressType":"SHIP-TO","addressLine1":"1701 Santa Rita Rd","city":"Pleasanton","state":"CA","zipCode":"94566","country":"USA"}],"contact":[{"number":"4085055665","type":"MOBILE"}],"email":[{"id":"4aug@example.com","type":"PERSONAL"}],"preference":{"optIn":[{"type":"TEXT","id":"4085055665","isOptin":true}]}},"paymentDetails":[{"paymentType":"CREDITCARD","paymentSubType":"Visa","tokenNumber":"9495846215171111","cardExpiryMonth":"08","cardExpiryYear":"29","zipcode":"94566","reqAuthorizationAmount":"24.12","cardHolderName":"ss","address":{"zipCode":"94566"},"paymentStatus":{"status":"Approved","authorizationCode":"ET127395","authorizationDate":"2020-08-14T16:33:22.000Z"}}],"subOrders":[{"subOrderNumber":1,"subOrderStatus":"COMPLETED","fulfillmentType":"DUG","deliveryInfo":{"slotInfo":{"slotType":"ONEHR","slotId":"1cf9124f-0179-4e93-bb2e-dbea1d7ae8c6","timeZone":"America/Los_Angeles","startTime":"2020-08-19T15:00:00.000Z","endTime":"2020-08-19T16:00:00.000Z","editingCutoffDate":"2020-08-19T12:00:00.000Z","slotPlan":"STANDARD","lastPickupTime":"2020-08-20T03:00:00.000Z"},"instructions":"","pickupInfo":{"locationType":"STORE","locationId":"1502"},"stageByDateTime":"2020-08-19T16:00:00.000Z"},"charges":[{"id":"00000029103","name":"BAG FEE           ","chargeAmount":{"amount":0.3,"currency":"USD"}}],"promoCodes":[],"orderLines":[{"itemId":"329","itemDescription":"DEBI LILLY FRAGRANT ROSE BOUQUET","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"Same Brand, Different Size","isRegulatedItem":false,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":5.99,"currency":"USD"},"itemTotalTax":0,"discountsApplied":0},{"itemId":"02113007028","itemDescription":"LUCERNE MILK REDUCED FAT 2%","orderedQuantity":5,"unitOfMeasure":"UNIT","substitutionValue":"Same Brand, Different Size","isRegulatedItem":false,"suppliedQuantity":5,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":3.49,"currency":"USD"},"itemTotalTax":0,"discountsApplied":0}],"refundedOrderLines":[{"itemDescription":"2/10 MILK #290","unitPrice":{"amount":3.49,"currency":"USD"},"refundedQuantity":5}],"messageAction":"COMPLETED","customerService":{"contact":[{"number":"6987079608","type":"MOBILE","subType":"DUG_VIRTUAL_PHONE"},{"number":"7987875877","type":"MOBILE","subType":"OPERATION_PHONE_MOBILE1"},{"number":"6987097659","type":"PHONE","subType":"OPERATION_PHONE_MOBILE2"},{"number":"6876329847","type":"PHONE","subType":"OPERATION_PHONE_MOBILE3"},{"number":"6873698739","type":"PHONE","subType":"OPERATION_PHONE_MOBILE4"},{"number":"6873269368","type":"MOBILE","subType":"OPERATION_PHONE_MOBILE5"},{"number":"7327498732","type":"PHONE","subType":"OPERATION_PHONE_MOBILE6"},{"number":"6876987769","type":"MOBILE","subType":"CUST_SERV_PHONE"},{"number":"6869869868","type":"MOBILE","subType":"STORE_PHONE"}]}}],"tender":[{"tenderType":"REFUND","tenderSubType":"credit_card_refund","chargeAmount":{"amount":17.45,"currency":"USD"}}],"storeInfo":[{"key":"isHybridStore","value":"true"},{"key":"isMFC","value":"false"},{"key":"isErumsEnabled","value":"true"},{"key":"isPremiumStore","value":"true"},{"key":"is3PLStore","value":"true"}]}'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS-EMOM_C02_ORDER AnjanaAccount.txt '{"orderNumber":"20443910","versionNumber":1,"companyId":"1","banner":"Vons","storeNumber":"2206","orderStatus":"COMPLETED","orderCreatedDate":"2020-10-29T20:57:52.963Z","sourceInfo":{"source":"ECHO","enteredBy":"CUSTOMER","deviceType":"WEB"},"orderTotal":{"amount":56.87,"currency":"USD"},"customer":{"customerId":"300-091-1431800392795","clubCardNumber":"60031566002196","isSubscription":false,"name":{"firstName":"Victoria","lastName":"Schmid"},"address":[{"addressType":"SHIP-TO","addressLine1":"133 E 16th St","addressLine2":"Spc 56","city":"Costa Mesa","state":"CA","zipCode":"92627","country":"USA"}],"contact":[{"number":"9492939079","type":"MOBILE"}],"email":[{"id":"victoria_kaye@att.net","type":"PERSONAL"}],"preference":{"optIn":[{"type":"TEXT","id":"9492939079","isOptin":true}]}},"paymentDetails":[{"paymentType":"CREDITCARD","paymentSubType":"Visa","tokenNumber":"9719357035215736","cardExpiryMonth":"1121","cardExpiryYear":"21","zipcode":"92627","reqAuthorizationAmount":"56.87","cardHolderName":"VICKI K SCHMID","address":{"zipCode":"92627"},"paymentStatus":{"status":"Accepted by E-xact","authorizationCode":"035759","authorizationDate":"2020-10-30T06:14:04.000Z"}}],"subOrders":[{"subOrderNumber":1,"subOrderStatus":"COMPLETED","fulfillmentType":"DELIVERY","deliveryInfo":{"deliverySubType":"RESIDENTIAL","deliveryServiceType":"ATTENDED","slotInfo":{"slotType":"TWOHR","slotId":"ef414ac3-55b6-4bc5-bd44-eaa4cd07cabc","timeZone":"America/Los_Angeles","startTime":"2020-10-30T01:00:00.000Z","endTime":"2020-10-30T03:00:00.000Z","shiftNumber":"4","editingCutoffDate":"2020-10-29T21:00:00.000Z","slotPlan":"STANDARD"},"instructions":"Mobile home park.  First left.  Center home.","stageByDateTime":"2020-10-30T03:00:00.000Z"},"charges":[{"id":"00000022151","name":"DOT COM DELIVERY C","chargeAmount":{"amount":5.95,"currency":"USD"}},{"id":"00000029103","name":"BAG FEE           ","chargeAmount":{"amount":0.2,"currency":"USD"}}],"promoCodes":[],"routeInfo":{"vanNumber":"B4","stopNumber":"003"},"tote":{"toteDetails":[{"toteId":"55079335","item":[],"toteTempZone":"FZ"},{"toteId":"55079337","item":[],"toteTempZone":"CH"},{"toteId":"55079339","item":[],"toteTempZone":"AM"}]},"orderLines":[{"itemId":"04132210912","itemDescription":"SEAPAK SHRIMP BUTTERFLY OVEN CRUNCH","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":10.99,"currency":"USD"},"itemTotalTax":0,"discountsApplied":0},{"itemId":"63671111504","itemDescription":"TEDDYS CREAM SODA","orderedQuantity":4,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":4,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":1.59,"currency":"USD"},"itemTotalTax":0.4,"discountsApplied":-2.36},{"itemId":"22762210599","itemDescription":"PEACH MUFFINS 4 COUNT","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":5.99,"currency":"USD"},"itemTotalTax":0,"discountsApplied":0},{"itemId":"07910090237","itemDescription":"MILKBONE DOG TREATS FLAVOR SNACKS SMALL / MEDIUM DOGS","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":5.99,"currency":"USD"},"itemTotalTax":0,"discountsApplied":-1},{"itemId":"76394622507","itemDescription":"ALFAROS ARTESANO GOLDEN WHEAT","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":4.29,"currency":"USD"},"itemTotalTax":0,"discountsApplied":-1.3},{"itemId":"83522900230","itemDescription":"ABSOLUT MANDARIN VODKA","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":true,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":26.99,"currency":"USD"},"itemTotalTax":0,"discountsApplied":-10.33},{"itemId":"03151","itemDescription":"TOMATOES VINE RIPE LARGE","orderedQuantity":2,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":1.1,"suppliedQuantityType":"WEIGHTED","suppliedUnitPrice":{"amount":2.99,"currency":"USD"},"itemTotalTax":0,"discountsApplied":0},{"itemId":"02113003225","itemDescription":"LUCERNE LARGE GRADE AA EGGS","orderedQuantity":1,"unitOfMeasure":"UNIT","substitutionValue":"No Substitution","isRegulatedItem":false,"suppliedQuantity":1,"suppliedQuantityType":"UNIT","suppliedUnitPrice":{"amount":3.49,"currency":"USD"},"itemTotalTax":0,"discountsApplied":-0.5}],"refundedOrderLines":[],"messageAction":"COMPLETED","customerService":{"contact":[{"number":"8775054040","type":"PHONE","subType":"CUST_SERV_PHONE"}]}}],"storeInfo":null,"modifiedDate":"2020-10-30T00:06:40.153","ttl":-1}'
    
    
          
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSCO_ESED_C02_ORDER_DEV AnjanaAccount.txt '{"orderNumber":"18182998","versionNumber":2,"orderStatus":"PAYMENT_REQUESTED","orderStatusReasonCode":"CUS_RESCEDULE_CANCEL","messageAction":"UPDATE","messageActionReason":"","companyId":"1","banner":"JewelOsco","isActive":true,"storeNumber":"0607","orderCreatedDate":"2020-08-13T07:51:11.179Z","fulfillmentSystem":"MANHATTAN","sourceInfo":{"source":"ECOMMERCE","enteredBy":"CUSTOMER","deviceType":"MOBILE","affiliate":{"affiliateName":"IBOTTA","affiliateOrderRef":"A123544334-123"}},"orderTotal":{"amount":"263.56","currency":"USD","totalCardSavings":15.51,"cardSavings":[{"savingsCategoryId":123,"savingsCategoryName":"String","savingsAmount":12.23}]},"customer":{"customerId":"556-020-1586122641346","clubCardNumber":"49130429968","isSubscription":false,"memberId":"1231321323","name":{"firstName":"JASON","lastName":"GRESS"},"address":[{"addressType":"SHIPTO","addressLine1":"14448 Donna Ln","addressLine2":" ","city":"Saratoga","state":"CA","zipCode":"95070","country":"USA"}],"contact":[{"number":"4088577000","type":"MOBILE"}],"email":[{"id":"gressholdings@gmail.com","type":"PERSONAL"}],"preference":{"termsCheckedVersionId":"0","optIn":[{"id":"4088577000","type":"TEXT","isOptin":true}]}},"paymentDetails":[{"paymentType":"CREDITCARD","paymentSubType":"AMEX","tokenNumber":"846787175413009","cardExpiryMonth":"10","cardExpiryYear":"24","zipcode":"95070","reqAuthorizationAmount":"263.56","cardHolderName":"Jason Gress","address":{"zipCode":"95070"},"paymentStatus":{"status":"NOTAUTHORIZED","authorizationCode":"201946","authorizationDate":"2020-08-13T07:51:11.233Z"}}],"subOrders":[{"subOrderNumber":1,"subOrderStatusReasonCode":"RESCHEDULE_SLOT","messageAction":"UPDATE","messageActionReason":"RESCHEDULE_SLOT","fulfillmentType":"DELIVERY","customerService":{"contact":[{"number":"8775054040","type":"PHONE","subType":"CUST_SERV_PHONE"}]},"deliveryInfo":{"deliverySubType":"RESIDENTIAL","slotInfo":{"slotPlan":"STANDARD","slotType":"FOURHR","slotId":"95e9bd9c-bedd-41ac-b0ad-04bebd4d7eb9","timeZone":"America/Los_Angeles","startTime":"2020-08-13T15:01:00.000Z","endTime":"2020-08-13T19:00:00.000Z","shiftNumber":"7","lastPickupTime":"2020-03-25T20:00:00.000Z","editingCutoffDate":"2020-08-13T08:00:00.000Z"},"deliveryServiceType":"ATTENDED","instructions":"","stageByDateTime":"2020-08-13T14:21:00.000Z","pickupInfo":{"locationType":"STORE","locationId":"1211","shortOrderNumber":4}},"charges":[{"id":"0000000029103","name":"BagFee","category":"ServiceFee","chargeAmount":{"amount":"0.1","currency":"USD"}},{"id":"0000000022155","name":"DeliveryFee","category":"DeliveryFee","chargeAmount":{"amount":"3.95","currency":"USD"}}],"promoCodes":[{"code":"SAVE20","description":"$20 Off Orders Over $75","pluCode":"00001234"}],"routeInfo":{"vanNumber":"DDS","stopNumber":"377"},"tote":{"toteEstimate":{"chilled":6,"frozen":2,"ambient":7},"toteDetails":[{"toteId":"","item":[]},{"toteId":"99800400215899","item":[{"itemId":"960027187","fulfilledUpc":[{"upcId":"003320009471","upcQuantity":1,"scannedUpc":"033200094715","pickedBy":"SYSTEM","pickedDate":"2020-08-13T09:38:01.782Z","pickType":"REGULAR"}]}]}],"totalPickedToteCount":4,"totalNumberOfBagsUsed":5},"orderLines":[{"itemId":"196011495","itemDescription":"San Luis Sourdough Bread Round - 24 Oz","orderedQuantity":1,"shortedQuantity":0,"fulfilledQuantity":1,"unitOfMeasure":"OZ","unitPrice":{"amount":"4.99","currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"comments":"","fulfilledUpc":[{"upcId":"001853724157","entryId":100,"upcQuantity":1,"pickedBy":"SYSTEM","pickedDate":"2020-08-13T09:38:01.782Z","pickType":"REGULAR","scanPrice":2.33,"isSubstituted":false,"itemPrice":{"itemCode":"UPC","entryId":100,"department":30,"unitPrice":20.5,"extendedPrice":5.5,"quantityType":"LB","quantityValue":4.45,"discountAllowed":true,"linkPluNumber":"promo PLU","startDate":"2020-08-13T09:38:01.782Z","endDate":"2020-08-14T09:38:01.782Z","itemPluNumber":"embedded item PLU","pointsApplyItem":true,"wic":false,"substituted":false,"netPromotionAmount":205.49,"savings":[{"offerId":"463272","externalOfferId":"463274","category":1,"source":"CPE","linkpluNumber":"promo PLU","programCode":"SC","startDate":"2020-08-13T07:00:00.000+0000","endDate":"2020-08-14T07:00:00.000+0000","discountAmount":14.94,"discountType":"Free","description":"Default Description","discountMessage":"discount","discountLevel":"Item Level","promotionPrice":2.59,"netPromotionAmount":8.43,"points":[{"programName":"String","earn":1,"burn":1}]}]}},{"upcId":"001853724157","entryId":200,"upcQuantity":1,"pickedBy":"SYSTEM","pickedDate":"2020-08-13T09:38:01.782Z","pickType":"REGULAR","scanWeight":3.98,"isSubstituted":true,"itemPrice":{"itemCode":"UPC","entryId":100,"department":30,"unitPrice":20.5,"extendedPrice":5.5,"quantityType":"LB","quantityValue":4.45,"discountAllowed":true,"linkPluNumber":"promo PLU","startDate":"2020-08-13T09:38:01.782Z","endDate":"2020-08-14T09:38:01.782Z","itemPluNumber":"embedded item PLU","pointsApplyItem":true,"wic":false,"substituted":false,"netPromotionAmount":205.49,"savings":[{"offerId":"463272","externalOfferId":"463274","category":1,"source":"CPE","linkpluNumber":"promo PLU","programCode":"SC","startDate":"2020-08-13T07:00:00.000+0000","endDate":"2020-08-14T07:00:00.000+0000","discountAmount":14.94,"discountType":"Free","description":"Default Description","discountMessage":"discount","discountLevel":"Item Level","promotionPrice":2.59,"netPromotionAmount":8.43,"points":[{"programName":"String","earn":1,"burn":1}]}]}}],"itemPrice":{"itemCode":"UPC","entryId":100,"department":30,"unitPrice":20.5,"extendedPrice":5.5,"quantityType":"LB","quantityValue":4.45,"discountAllowed":true,"linkPluNumber":"promo PLU","startDate":"2020-08-13T09:38:01.782Z","endDate":"2020-08-14T09:38:01.782Z","itemPluNumber":"embedded item PLU","pointsApplyItem":true,"wic":false,"substituted":false,"netPromotionAmount":205.49,"savings":[{"offerId":"463272","externalOfferId":"463274","category":1,"source":"CPE","linkpluNumber":"promo PLU","programCode":"SC","startDate":"2020-08-13T07:00:00.000+0000","endDate":"2020-08-14T07:00:00.000+0000","discountAmount":14.94,"discountType":"Free","description":"Default Description","discountMessage":"discount","discountLevel":"Item Level","promotionPrice":2.59,"netPromotionAmount":8.43,"points":[{"programName":"String","earn":1,"burn":1}]}]}}]}],"storeInfo":[{"key":"isHybridStore","value":"true"},{"key":"isMFC","value":"false"},{"key":"isErumsEnabled","value":"true"},{"key":"isPremiumStore","value":"true"},{"key":"is3PLStore","value":"true"},{"key":"isDUGArrivalEnabled","value":"false"},{"key":"storeTimeZone","value":"PST"},{"key":"terminalNumber","value":"99"},{"key":"isWYSIWYGEnabled","value":"true"}],"orderInfo":{"stageByDateTime":"2020-08-13T14:21:00.000Z","data":[{"key":"key","value":"value"}]},"audit":{"createDate":"2020-08-13T12:27:32.039Z","modifiedDate":"2020-08-13T12:27:32.039Z","createdBy":"OSCO-SERVICES"}}'
      
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSCO_ESED_C02_ORDER_DEV AnjanaAccount.txt '{"audit":{"createDate":"2020-10-28T15:59:38.808Z","modifiedDate":"2020-10-28T19:11:00.917Z","createdBy":"OSCO-Processor","modifiedBy":"APS_FEED"},"orderNumber":"20050999","versionNumber":1,"orderStatus":"PAYMENT_REQUESTED","companyId":"1","banner":"Safeway","isActive":true,"storeNumber":"2941","modifiedReasonCode":"PACKED","orderCreatedDate":"2020-10-28T15:59:38.470Z","fulfillmentSystem":"APS","sourceInfo":{"source":"ECOMMERCE","enteredBy":"CUSTOMER","deviceType":"WEB"},"orderTotal":{"amount":86.9,"currency":"USD","totalCardSavings":0},"customer":{"customerId":"557-145-1597354036238","clubCardNumber":"49291215654","isSubscription":false,"name":{"firstName":"adithya","lastName":"patha"},"address":[{"addressType":"SHIP-TO","addressLine1":"707 Contra Costa Blvd","city":"Pleasant Hill","state":"CA","zipCode":"94523","country":"USA"}],"contact":[{"number":"6513097229","type":"MOBILE"}],"email":[{"id":"apath08@safeway.com","type":"PERSONAL"}],"preference":{"optIn":[{"type":"TEXT","id":"6513097229","isOptin":false}]}},"paymentDetails":[{"paymentType":"CREDITCARD","paymentSubType":"DISCOVER","tokenNumber":"7021994581719716","cardExpiryMonth":"12","cardExpiryYear":"21","zipcode":"94538","reqAuthorizationAmount":"86.90","cardHolderName":"adithya patha","address":{"zipCode":"94538"},"paymentStatus":{"status":"NOTAUTHORIZED","authorizationCode":"ET104063","authorizationDate":"2020-10-28T15:59:38.499Z"}}],"subOrders":[{"fulfillmentOrderNumber":"20050939011","subOrderNumber":1,"fulfillmentType":"DUG","customerService":{"contact":[{"number":"7298374987","type":"PHONE","subType":"DUG_VIRTUAL_PHONE"},{"number":"7932874987","type":"PHONE","subType":"CUST_SERV_PHONE"}]},"deliveryInfo":{"slotInfo":{"slotPlan":"STANDARD","slotType":"ONEHR","slotId":"968f67b2-bffe-46b8-92d0-c984600788a5","timeZone":"America/Los_Angeles","startTime":"2020-10-28T19:00:00.000Z","endTime":"2020-10-28T20:00:00.000Z","shiftNumber":"2","editingCutoffDate":"2020-10-28T16:00:00.000Z","lastPickupTime":"2020-10-29T03:00:00.000Z"},"instructions":"","pickupInfo":{"locationType":"STORE","locationId":"2941","shortOrderNumber":129},"stageByDateTime":"2020-10-28T17:00:00.000Z"},"charges":[{"id":"0000000029103","name":"BagFee","category":"ServiceFee","chargeAmount":{"amount":0.1,"currency":"USD"}}],"promoCodes":[],"tote":{"totalNumberOfBagsUsed":5},"orderLines":[{"itemId":"960549767","itemDescription":"Axe Body Spray Cool Ocean - 4 Oz","orderedQuantity":5,"shortedQuantity":0,"fulfilledQuantity":4,"unitOfMeasure":"OZ","unitPrice":{"amount":6.99,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"005210000256","upcQuantity":2,"fulfilledQuantity":2,"scannedUpc":"052100002569","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:08:37.869","pickType":"REGULAR","upcDescription":"McCormick Ground Nutmeg - 1.1 Oz"},{"upcId":"001300000218","upcQuantity":1,"fulfilledQuantity":1,"scannedUpc":"013000002189","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:08:46.943","pickType":"REGULAR","upcDescription":"Heinz Mustard Yellow - 20 Oz"},{"upcId":"001300000218","upcQuantity":1,"fulfilledQuantity":1,"scannedUpc":"013000002189","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:09:00.472","pickType":"REGULAR","upcDescription":"Heinz Mustard Yellow - 20 Oz"}],"department":{"name":"Breakfast & Cereal"},"aisle":{"id":"1_7_2_2","name":"Cereal"},"shelf":{},"itemPrice":{"itemCode":"0007940046978"}},{"itemId":"960134449","itemDescription":"Heinz Mustard Yellow - 20 Oz","orderedQuantity":5,"shortedQuantity":5,"fulfilledQuantity":0,"unitOfMeasure":"OZ","unitPrice":{"amount":2.99,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[],"department":{"name":"Condiments, Spice & Bake"},"aisle":{"id":"1_20_1_2","name":"Jam,Spreads & Condiments"},"shelf":{},"itemPrice":{"itemCode":"0001300000218"}},{"itemId":"184040158","itemDescription":"Avocados Hass Large","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":9,"unitOfMeasure":"EA","unitPrice":{"amount":2.5,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"000000004070","upcQuantity":4,"fulfilledQuantity":4,"scannedUpc":"000000040709","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T18:58:54.188","pickType":"REGULAR","upcDescription":"Celery - 1 Bunch"},{"upcId":"000000004225","upcQuantity":5,"fulfilledQuantity":5,"scannedUpc":"000000042253","pickedBy":"vgunj00","isSubstituted":false,"pickedDate":"2020-10-28T18:59:21.58","pickType":"REGULAR"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_1_2","name":"Fresh Fruits"},"shelf":{},"itemPrice":{"itemCode":"0048404007501"}},{"itemId":"184410069","itemDescription":"Cilantro","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":9,"unitOfMeasure":"EA","unitPrice":{"amount":0.99,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"000000004070","upcQuantity":9,"fulfilledQuantity":9,"scannedUpc":"000000040709","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T18:55:22.012","pickType":"REGULAR","upcDescription":"Celery - 1 Bunch"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_2_11","name":"Fresh Vegetables & Herbs"},"shelf":{},"itemPrice":{"itemCode":"0048441002701"}},{"itemId":"184710053","itemDescription":"Cilantro Organic","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":5,"unitOfMeasure":"EA","unitPrice":{"amount":1.29,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"000000004889","upcQuantity":5,"fulfilledQuantity":5,"scannedUpc":"000000048897","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T18:56:56.975","pickType":"REGULAR","upcDescription":"Cilantro - 1 Bunch"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_2_11","name":"Fresh Vegetables & Herbs"},"shelf":{},"itemPrice":{"itemCode":"0003338390419"}},{"itemId":"184020066","itemDescription":"Fuji Large Apple","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":4,"unitOfMeasure":"LB","unitPrice":{"amount":1.25,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"040345200282","upcQuantity":3,"fulfilledQuantity":4,"scannedUpc":"0403452002828","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:03:14.43","pickType":"REGULAR","upcDescription":"Green Seedless Grapes - 2 Lb"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_1_1","name":"Fresh Fruits"},"shelf":{},"itemPrice":{"itemCode":"0048402006601"}},{"itemId":"184700063","itemDescription":"Grapes Red Seedless Organic","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":2,"unitOfMeasure":"LB","unitPrice":{"amount":7.98,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"040345200282","upcQuantity":1,"fulfilledQuantity":2,"scannedUpc":"0403452002828","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:04:41.837","pickType":"REGULAR","upcDescription":"Green Seedless Grapes - 2 Lb"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_1_6","name":"Fresh Fruits"},"shelf":{},"itemPrice":{"itemCode":"0048470006301"}},{"itemId":"184700039","itemDescription":"Organic Fuji Apples","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":3,"unitOfMeasure":"LB","unitPrice":{"amount":1.5,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"040413100282","upcQuantity":1,"fulfilledQuantity":2,"scannedUpc":"0404131002825","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:00:02.957","pickType":"REGULAR","upcDescription":"Fuji Large Apple"},{"upcId":"040402300282","upcQuantity":1,"fulfilledQuantity":1,"scannedUpc":"0404023002827","pickedBy":"vgunj00","isSubstituted":true,"pickedDate":"2020-10-28T19:06:30.405","pickType":"REGULAR","upcDescription":"Red Seedless Grapes - 2 Lb"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_1_1","name":"Fresh Fruits"},"shelf":{},"itemPrice":{"itemCode":"0048470003901"}},{"itemId":"184700157","itemDescription":"Organic Hass Avocados","orderedQuantity":2,"shortedQuantity":0,"fulfilledQuantity":2,"unitOfMeasure":"EA","unitPrice":{"amount":2.99,"currency":"USD"},"substitutionCode":"2","substitutionValue":"Same Brand Diff Size","isRegulatedItem":false,"fulfilledUpc":[{"upcId":"000000004225","upcQuantity":2,"fulfilledQuantity":2,"scannedUpc":"000000042253","pickedBy":"vgunj00","isSubstituted":false,"pickedDate":"2020-10-28T18:57:48.229","pickType":"REGULAR"}],"department":{"name":"Fruits & Vegetables"},"aisle":{"id":"1_23_1_2","name":"Fresh Fruits"},"shelf":{},"itemPrice":{"itemCode":"0048470008301"}}],"subOrderStatus":"PACKED","modifiedReasonCode":"PACKED"}],"storeInfo":[{"key":"isHybridStore","value":"true"},{"key":"isMFC","value":"false"},{"key":"isErumsEnabled","value":"true"},{"key":"isPremiumStore","value":"true"},{"key":"is3PLStore","value":"true"},{"key":"isDUGArrivalEnabled","value":"false"},{"key":"isWYSIWYGEnabled","value":"false"},{"key":"storeTimeZone","value":"America/Los_Angeles"},{"key":"terminalNumber","value":"99"},{"key":"SENT_TO_VPOS","value":"Y"}],"orderInfo":{"stageByDateTime":"2020-10-28T17:00:00.000Z"},"id":"5f99956aca38ca0019418ed9","version":5,"modifiedBy":"OSCO-Processor","modifiedDate":"2020-10-28T19:11:01.939","ttl":-1}'


## 3. Consume Message
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer {TOPIC_NAME} latest {DEST_FILE_NAME}.text
     
      java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_GroceryOrder latest GroceryOrder13.text &

# QA
## 1. ssh qgv012efb

    su - esq03ed
    password: #Today@321!

    cd /appl/esed/kafka

## 2. Publish Message
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer {TOPIC_NAME} per.txt 'MSG'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer IAUC_C02_QA_CFMS_AGGREGATE per.txt '{"_id":"CustomerPreferences_28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","state":{"_class":"PreferenceAggregateDomain_v1","createTimestamp":1611875712045,"lastUpdateTimestamp":1611875712045,"createClientId":"UMA","lastUpdateClientId":"UMA","createHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","lastUpdateHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","sequenceNumber":1,"timestamp":1571994962492,"aggregateRevision":1,"version":1,"eventId":"98d5779b-24a6-4aac-9c1c-4e5a4a3e677f","guid":"456-042-1611875708495","preferences":[{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":1,"type":"EMAIL_SAVE","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":2,"type":"J4U","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":3,"type":"GROCERY_DEL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":4,"type":"PROD_RECALL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]}]}}'


## 3. Consume Message
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer {TOPIC_NAME} latest {DEST_FILE_NAME}.text
     
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_GroceryOrder latest GroceryOrder_1_4.text
     
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer OSCO_ESED_C02_ORDER_QA latest GroceryOrder_OMS.text
     
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_GroceryOrder latest GroceryOrder_1_6.text


# Build
## ERUMS
mqsiapplybaroverride -k ESED_GroceryOrder_eRUMS_IH_Publisher -b ESED_GroceryOrder_eRUMS_IH_Publisher.bar -p ESED_GroceryOrder_eRUMS_IH_Publisher.BASE.override.properties

mqsireadbar -b ESED_GroceryOrder_eRUMS_IH_Publisher.bar -r

## OMS
mqsiapplybaroverride -k ESED_GroceryOrder_OMS_IH_Publisher -b ESED_GroceryOrder_OMS_IH_Publisher.bar -p ESED_GroceryOrder_OMS_IH_Publisher.BASE.override.properties

  