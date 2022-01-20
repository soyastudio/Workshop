# Design
## Flow Chart
``` mermaid
graph LR
  subgraph SRC [OSMS]
    ST
  end
    
    ST --> AKS
    AKS --> KIT

  subgraph EDIS [EDIS - DeliverySlot]
    KIT --> |<Json Data>|IIB
    IIB --> |<CMM XML>|KOT
  end

    KOT --> AKT
    AKT --> END

  subgraph TGT [EDM]
    END
  end

  ST((Slot Service))
  AKS[(Azure Kafka SRC)]

  KIT[(Kafka Consumer Topic)]
  IIB[IIB Transformer]
  KOT[(Kafka Producer Topic)]

  AKT[(Azure Kafka TGT)]
  END((Consumer))
            

```


# Kafka Test

## DEV
### 1. ssh dgv012efa
```
    su - esd02ed
    password: #Today@321!
    cd /appl/esed/kafka
    cd /appl/esed/kafka/kafkaFL

```

### 2. Publish Message
```
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer OSMS_Slots_C02.slots.slots AnjanaAccount.txt '{"slotId":"eee8b3e0-afac-416c-95fd-09c38763d2d7","storeId":"68","dayOfWeek":"SATURDAY","slotStartTS":"2020-09-18T17:02:00:000Z","slotEndTS":"2020-09-18T18:00:00:000Z","slotExpiryTS":"2020-09-18T14:02:00:000Z","maxDeliveries":10,"serviceType":"DELIVERY","deliveryType":["ATTENDED","UNATTENDED"],"b2bCharge":{"userType":"BUSINESS","deliveryCharge":"5","minimumBasketSize":"150","alertBasketSize":"9999","reducedDeliveryCharge":"3.99"},"b2cCharge":{"userType":"RESIDENTIAL","deliveryCharge":"5.99","minimumBasketSize":"150","alertBasketSize":"9999","reducedDeliveryCharge":"8.99"},"slotType":"ONEHR","slotPlan":"STANDARD","bookings":[{"userGuid":"556-178-1581630549317","storeId":"1739","deliveryType":"ATTENDED","expiryTime":"2020-03-21T21:41:05:000Z","slotCategory":"ABC"},{"userGuid":"556-178-1581630549317","storeId":"1739","deliveryType":"ATTENDED","expiryTime":"2020-03-22T21:41:05:000Z","slotCategory":"XYZ"}],"createdDate":"2019-10-23T22:19:02.525-07:00","lastModifiedDate":"2019-10-23T22:19:02.525-07:00"}'

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
    cd /appl/esed/kafka/admin/bin

```

### 2. Publish Message 
```
    cd
    java -jar kafkaya.jar -c ESED_C01_DeliverySlot -p OSMS_Slots_C02.slots.slots -m ewogICJzbG90SWQiOiAiMDAwMDAwMDAtOTllMC00NzlkLWExZjAtZDdmNTAxOWI4MDE4IiwKICAic3RvcmVJZCI6ICIxNjIzIiwKICAiZGF5T2ZXZWVrIjogIlRIVVJTREFZIiwKICAic2xvdFN0YXJ0VFMiOiAiMjAxOS0xMC0zMVQwOTowMjowMC4wMDAtMDc6MDAiLAogICJzbG90RW5kVFMiOiAiMjAxOS0xMC0zMVQxMDowMDowMC4wMDAtMDc6MDAiLAogICJzbG90RXhwaXJ5VFMiOiAiMjAxOS0xMC0zMVQwMTowMDowMC4wMDAtMDc6MDAiLAogICJtYXhEZWxpdmVyaWVzIjogMywKICAic2VydmljZVR5cGUiOiAiREVMSVZFUlkiLAogICJkZWxpdmVyeVR5cGUiOiBbCiAgICAiQVRURU5ERUQiLAogICAgIlVOQVRURU5ERUQiCiAgXSwKICAiYjJiQ2hhcmdlIjogewogICAgInVzZXJUeXBlIjogIkJVU0lORVNTIiwKICAgICJkZWxpdmVyeUNoYXJnZSI6IDUuOTUsCiAgICAibWluaW11bUJhc2tldFNpemUiOiAxNTAsCiAgICAiYWxlcnRCYXNrZXRTaXplIjogOTk5OSwKICAgICJyZWR1Y2VkRGVsaXZlcnlDaGFyZ2UiOiA1Ljk1LAogICAgImRlbGl2ZXJ5Q2hhcmdlVVBDIjogIjAwMDAwMDAwMjIxNTEiLAogICAgInJlZHVjZWREZWxpdmVyeUNoYXJnZVVQQyI6ICIwMDAwMDAwMDIyMTUxIgogIH0sCiAgImIyY0NoYXJnZSI6IHsKICAgICJ1c2VyVHlwZSI6ICJSRVNJREVOVElBTCIsCiAgICAiZGVsaXZlcnlDaGFyZ2UiOiA1Ljk1LAogICAgIm1pbmltdW1CYXNrZXRTaXplIjogMTUwLAogICAgImFsZXJ0QmFza2V0U2l6ZSI6IDk5OTksCiAgICAicmVkdWNlZERlbGl2ZXJ5Q2hhcmdlIjogNS45NSwKICAgICJkZWxpdmVyeUNoYXJnZVVQQyI6ICIwMDAwMDAwMDIyMTUxIiwKICAgICJyZWR1Y2VkRGVsaXZlcnlDaGFyZ2VVUEMiOiAiMDAwMDAwMDAyMjE1MSIKICB9LAogICJzbG90VHlwZSI6ICJPTkVIUiIsCiAgInNsb3RQbGFuIjogIlNUQU5EQVJEIiwKICAicmVzZXJ2ZWRCeUN1cnJlbnRVc2VyIjogZmFsc2UsCiAgImJvb2tpbmdzIjogWwogICAgewogICAgICAidXNlckd1aWQiOiAiMzAwLTA5MC0xNDA0NjgwNDMzMTM0IiwKICAgICAgInN0b3JlSWQiOiAiMTYyMyIsCiAgICAgICJvcmRlcklkIjogIjQ4NjAzNzkiLAogICAgICAidmVyc2lvbk51bWJlciI6ICIxIiwKICAgICAgImRlbGl2ZXJ5VHlwZSI6ICJBVFRFTkRFRCIsCiAgICAgICJleHBpcnlUaW1lIjogIjIwMTktMTAtMzFUMDE6MDA6MDAuMDAwLTA3OjAwIgogICAgfSwKICAgIHsKICAgICAgInVzZXJHdWlkIjogIjUyMC0wMDAtMDI5MDA4NzU0NzM3OCIsCiAgICAgICJzdG9yZUlkIjogIjE2MjMiLAogICAgICAib3JkZXJJZCI6ICIxMjAxNjk0NiIsCiAgICAgICJjYXJ0SWQiOiAiMzA2MDQ3MCIsCiAgICAgICJkZWxpdmVyeVR5cGUiOiAiQVRURU5ERUQiLAogICAgICAiZXhwaXJ5VGltZSI6ICIyMDE5LTEwLTI5VDExOjAwOjIzLjAwMC0wNzowMCIsCiAgICAgICJzbG90Q2F0ZWdvcnkiOiAiUmVib29raW5nIgogICAgfSwKICAgIHsKICAgICAgInVzZXJHdWlkIjogIjMwMC0wOTAtMTU0NjM5ODEwMzA1NCIsCiAgICAgICJzdG9yZUlkIjogIjE2MjMiLAogICAgICAiY2FydElkIjogIjMyNjA1NDQiLAogICAgICAiZGVsaXZlcnlUeXBlIjogIkFUVEVOREVEIiwKICAgICAgImV4cGlyeVRpbWUiOiAiMjAxOS0xMC0zMFQxNTowNjo1OS4wMDAtMDc6MDAiLAogICAgICAic2xvdENhdGVnb3J5IjogIlN1YnNjcmliZWQiCiAgICB9CiAgXSwKICAiY3JlYXRlZERhdGUiOiAiMjAxOS0xMC0yM1QyMjoxOTowMi41MjUtMDc6MDAiLAogICJsYXN0TW9kaWZpZWREYXRlIjogIjIwMTktMTAtMjNUMjI6MTk6MDIuNTI1LTA3OjAwIgp9Cg==
    
```
    
### 3. Consume Message
```
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer OSMS_Slots_C02.slots.slots latest DeliverySlot-SRC.text
    
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_DeliverySlot latest DeliverySlot.text
 
```


mqsiapplybaroverride -k ESED_GroceryOrder_eRUMS_IH_Publisher -b ESED_GroceryOrder_eRUMS_IH_Publisher.bar -p ESED_GroceryOrder_eRUMS_IH_Publisher.BASE.override.properties

mqsireadbar -b ESED_GroceryOrder_eRUMS_IH_Publisher.bar -r

mqsiapplybaroverride -k ESED_GroceryOrder_OMS_IH_Publisher -b ESED_GroceryOrder_OMS_IH_Publisher.bar -p ESED_GroceryOrder_OMS_IH_Publisher.BASE.override.properties

mqsireadbar -b ESED_GroceryOrder_OMS_IH_Publisher.bar -r





mqsiapplybaroverride -k ESED_AirMilePoints_OCRP_IH_Publisher -b ESED_AirMilePoints_OCRP_IH_Publisher.bar -p ESED_AirMilePoints_OCRP_IH_Publisher.BASE.override.properties

mqsireadbar -b ESED_AirMilePoints_OCRP_IH_Publisher.bar -r

