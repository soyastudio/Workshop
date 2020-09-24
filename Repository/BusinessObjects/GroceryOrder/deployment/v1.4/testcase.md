# ERUMS
## Input
```
{
  "orderNumber": "20023743",
  "versionNumber": 2,
  "orderStatus": "CREATED",
  "messageAction": "CREATED",
  "messageActionReason ": "CUS_RESCHEDULE_CANCEL",
  "companyId": "1",
  "banner": "Safeway",
  "storeNumber": "3116",
  "orderCreatedDate": "2020-07-07T18:39:40.317Z",
  "sourceInfo": {
    "source": "ECOMMERCE",
    "enteredBy": "CUSTOMER",
    "deviceType": "WEB",
    "affiliate": {
      "affiliateName": "IBOTTA",
      "affiliateOrderRef": "A123544334-123"
    }
  },
  "orderTotal": {
    "amount": 44.38,
    "currency": "USD"
  },
  "customer": {
    "customerId": "556-030-1588631734xxx",
    "clubCardNumber": "49192969xxx",
    "isSubscription": false,
    "name": {
      "firstName": "xxx",
      "lastName": "xxx"
    },
    "address": [
      {
        "addressType": "SHIP-TO",
        "addressLine1": "xxx Hazelwood Dr",
        "addressLine2": "xxx Hazelwood Drive",
        "city": "South San Francisco",
        "state": "CA",
        "zipCode": "94080",
        "country": "USA"
      }
    ],
    "contact": [
      {
        "number": "636459xxxx",
        "type": "MOBILE"
      }
    ],
    "email": [
      {
        "id": "xxx@gmail.com",
        "type": "PERSONAL"
      }
    ],
    "preference": {
      "optIn": [
        {
          "type": "TEXT",
          "id": "6364590xxx",
          "isOptin": true
        }
      ]
    }
  },
  "paymentDetails": [
    {
      "paymentType": "CREDITCARD",
      "paymentSubType": "MASTERCARD",
      "tokenNumber": "1811400206461002",
      "cardExpiryMonth": "12",
      "cardExpiryYear": "25",
      "zipcode": "60015",
      "reqAuthorizationAmount": "44.38",
      "cardHolderName": "MASTERCARD",
      "address": {
        "zipCode": "60015"
      },
      "paymentStatus": {
        "status": "NOTAUTHORIZED",
        "authorizationCode": "ET154338",
        "authorizationDate": "2020-07-07T18:39:40.339Z"
      }
    }
  ],
  "subOrders": [
    {
      "subOrderNumber": 1,
      "messageAction": "UPDATE",
      "messageActionReason": "RESCHEDULE_SLOT",
      "fulfillmentType": "DELIVERY",
      "customerService": {
        "contact": [
          {
            "number": "7843793105",
            "type": "MOBILE",
            "subType": "CUST_SERV_PHONE"
          }
        ]
      },
      "deliveryInfo": {
        "deliverySubType": "RESIDENTIAL",
        "slotInfo": {
          "slotPlan": "STANDARD",
          "slotType": "TWOHR",
          "slotId": "1b9f3a65-2d2e-4bfa-9e3b-a27c454272d8",
          "timeZone": "America/Los_Angeles",
          "startTime": "2020-07-08T01:00:00.000Z",
          "endTime": "2020-07-08T03:00:00.000Z",
          "editingCutoffDate": "2020-07-07T21:00:00.000Z"
        },
        "deliveryServiceType": "ATTENDED",
        "instructions": "Leave at the door step",
        "stageByDateTime": "2020-07-08T03:00:00.000Z"
      },
      "charges": [
        {
          "id": "0000000029103",
          "name": "BagFee",
          "category": "ServiceFee",
          "chargeAmount": {
            "amount": 0.1,
            "currency": "USD"
          }
        },
        {
          "id": "0000000022151",
          "name": "DeliveryFee",
          "category": "DeliveryFee",
          "chargeAmount": {
            "amount": 5.95,
            "currency": "USD"
          }
        }
      ],
      "promoCodes": [],
      "orderLines": [
        {
          "itemId": "960037176",
          "itemDescription": "Signature SELECT/Refreshe Soda Cola Zero - 12-12 Fl. Oz.",
          "orderedQuantity": 2,
          "unitOfMeasure": "Fz ",
          "unitPrice": {
            "amount": 3.99,
            "currency": "USD"
          },
          "substitutionCode": "2",
          "substitutionValue": "Same Brand Diff Size",
          "isRegulatedItem": false
        },
        {
          "itemId": "960008285",
          "itemDescription": "Fresh Baked Garlic Parmesan Sourdough Bread",
          "orderedQuantity": 3,
          "unitOfMeasure": "Ea ",
          "unitPrice": {
            "amount": 3.49,
            "currency": "USD"
          },
          "substitutionCode": "2",
          "substitutionValue": "Same Brand Diff Size",
          "isRegulatedItem": false
        },
        {
          "itemId": "188100176",
          "itemDescription": "Ground Beef 93% Lean 7% Fat - 1.25 Lbs",
          "orderedQuantity": 2,
          "unitOfMeasure": "Lb ",
          "unitPrice": {
            "amount": 9.99,
            "currency": "USD"
          },
          "substitutionCode": "2",
          "substitutionValue": "Same Brand Diff Size",
          "isRegulatedItem": false
        }
      ]
    }
  ],
  "storeInfo": [
    {
      "key": "isHybridStore",
      "value": "true"
    },
    {
      "key": "isMFC",
      "value": "true"
    },
    {
      "key": "isErumsEnabled",
      "value": "true"
    },
    {
      "key": "isPremiumStore",
      "value": "true"
    },
    {
      "key": "is3PLStore",
      "value": "true"
    }
  ]
}
```

## Output
```
<?xml version="1.0" encoding="UTF-8"?>
<GetGroceryOrder xmlns:Abs="https://collab.safeway.com/it/architecture/info/default.aspx">
    <DocumentData>
        <Document SystemEnvironmentCd="PROD">
            <Abs:DocumentID>GROCERY_ORDER</Abs:DocumentID>
            <Abs:AlternateDocumentID>OSMS-EMOM_C02_ORDER-20200921093134975481</Abs:AlternateDocumentID>
            <Abs:DocumentNm>GroceryOrder</Abs:DocumentNm>
            <Abs:CreationDt>2020-09-21T09:31:34.975481-07:00</Abs:CreationDt>
            <Abs:Description>GroceryOrderDetail</Abs:Description>
            <Abs:SourceApplicationCd>eRUMS</Abs:SourceApplicationCd>
            <Abs:TargetApplicationCd>EDIS</Abs:TargetApplicationCd>
            <Abs:InternalFileTransferInd>Y</Abs:InternalFileTransferInd>
            <Abs:DataClassification>
                <Abs:DataClassificationLevel>
                    <Abs:Code>Internal</Abs:Code>
                </Abs:DataClassificationLevel>
                <Abs:BusinessSensitivityLevel>
                    <Abs:Code>Low</Abs:Code>
                </Abs:BusinessSensitivityLevel>
                <Abs:PHIdataInd>N</Abs:PHIdataInd>
                <Abs:PCIdataInd>Y</Abs:PCIdataInd>
                <Abs:PIIdataInd>Y</Abs:PIIdataInd>
            </Abs:DataClassification>
        </Document>
        <DocumentAction>
            <Abs:ActionTypeCd>UPDATE</Abs:ActionTypeCd>
            <Abs:RecordTypeCd>CHANGE</Abs:RecordTypeCd>
        </DocumentAction>
    </DocumentData>
    <GroceryOrderData>
        <Abs:GroceryOrderHeader>
            <Abs:OrderId>20023743</Abs:OrderId>
            <Abs:OrderCreateTS>2020-07-07T18:39:40.317Z</Abs:OrderCreateTS>
            <Abs:CompanyId>1</Abs:CompanyId>
            <Abs:VersionNbr>2</Abs:VersionNbr>
            <Abs:OrderStatus>
                <Abs:StatusTypeCd>CREATED</Abs:StatusTypeCd>
            </Abs:OrderStatus>
            <Abs:OrderActionStatus>
                <Abs:StatusTypeCd>CREATED</Abs:StatusTypeCd>
            </Abs:OrderActionStatus>
            <Abs:FulfillingFacility>
                <Abs:RetailStoreId>3116</Abs:RetailStoreId>
                <Abs:BannerCd>Safeway</Abs:BannerCd>
                <Abs:HybridStoreInd>Y</Abs:HybridStoreInd>
                <Abs:MFCInd>Y</Abs:MFCInd>
                <Abs:ERUMSEnabledInd>Y</Abs:ERUMSEnabledInd>
                <Abs:PremiumStoreInd>Y</Abs:PremiumStoreInd>
                <Abs:ThirdPartyDeliveryOnlyInd>Y</Abs:ThirdPartyDeliveryOnlyInd>
            </Abs:FulfillingFacility>
            <Abs:RetailCustomer>
                <Abs:CustomerId>556-030-1588631734xxx</Abs:CustomerId>
                <Abs:CustomerNm>
                    <Abs:GivenNm>xxx</Abs:GivenNm>
                    <Abs:FamilyNm>xxx</Abs:FamilyNm>
                </Abs:CustomerNm>
                <Abs:Contact>
                    <Abs:PhoneFaxContact TypeCode="MOBILE">
                        <Abs:PhoneNbr>636459xxxx</Abs:PhoneNbr>
                    </Abs:PhoneFaxContact>
                </Abs:Contact>
                <Abs:Contact>
                    <Abs:DigitalContact>
                        <Abs:DigitalAddress>xxx@gmail.com</Abs:DigitalAddress>
                        <Abs:EmailStatuses Abs:typeCode="PERSONAL"/>
                    </Abs:DigitalContact>
                </Abs:Contact>
                <Abs:Contact>
                    <Abs:Address>
                        <Abs:AddressUsageTypeCd>SHIP-TO</Abs:AddressUsageTypeCd>
                        <Abs:AddressLine1txt>xxx Hazelwood Dr</Abs:AddressLine1txt>
                        <Abs:AddressLine2txt>xxx Hazelwood Drive</Abs:AddressLine2txt>
                        <Abs:CityNm>South San Francisco</Abs:CityNm>
                        <Abs:StateCd>CA</Abs:StateCd>
                        <Abs:CountryCd>USA</Abs:CountryCd>
                    </Abs:Address>
                </Abs:Contact>
                <Abs:ClubCardNbr>49192969xxx</Abs:ClubCardNbr>
                <Abs:CustomerPreference>
                    <Abs:PreferenceType>
                        <Abs:Code>TEXT</Abs:Code>
                    </Abs:PreferenceType>
                    <Abs:OptInContactNbr>6364590xxx</Abs:OptInContactNbr>
                    <Abs:OptInInd>true</Abs:OptInInd>
                </Abs:CustomerPreference>
                <Abs:CustomerSubscription>
                    <Abs:OptInInd>false</Abs:OptInInd>
                </Abs:CustomerSubscription>
            </Abs:RetailCustomer>
            <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            <Abs:TotalAmt>44.38</Abs:TotalAmt>
            <Abs:CustomerPayment>
                <Abs:Tender>
                    <Abs:TenderId>1811400206461002</Abs:TenderId>
                    <Abs:TenderTypeCd>CREDITCARD</Abs:TenderTypeCd>
                    <Abs:TenderSubTypeCd>MASTERCARD</Abs:TenderSubTypeCd>
                    <Abs:HolderNm>MASTERCARD</Abs:HolderNm>
                    <Abs:ExpireMonthYearTxt>12</Abs:ExpireMonthYearTxt>
                    <Abs:BillingAddress>
                        <Abs:PostalZoneCd>60015</Abs:PostalZoneCd>
                    </Abs:BillingAddress>
                    <Abs:PostalCd>60015</Abs:PostalCd>
                    <Abs:Token>
                        <Abs:TokenId>1811400206461002</Abs:TokenId>
                        <Abs:TokenTypeCd>CREDITCARD</Abs:TokenTypeCd>
                    </Abs:Token>
                    <Abs:Status>
                        <Abs:StatusCd>NOTAUTHORIZED</Abs:StatusCd>
                    </Abs:Status>
                    <Abs:RequiredAuthAmt>44.38</Abs:RequiredAuthAmt>
                    <Abs:AuthCd>ET154338</Abs:AuthCd>
                    <Abs:AuthDt>2020-07-07T18:39:40.339Z</Abs:AuthDt>
                </Abs:Tender>
            </Abs:CustomerPayment>
            <Abs:CustomerSavings/>
            <Abs:OrderRecordDateInfo>
                <Abs:CreateUserId>CUSTOMER</Abs:CreateUserId>
            </Abs:OrderRecordDateInfo>
            <Abs:OrderCreatedDeviceType>
                <Abs:Code>WEB</Abs:Code>
            </Abs:OrderCreatedDeviceType>
            <Abs:AffiliatePartnerType>
                <Abs:AffiliatePartnerNm>IBOTTA</Abs:AffiliatePartnerNm>
                <Abs:OrderReferenceTxt>A123544334-123</Abs:OrderReferenceTxt>
            </Abs:AffiliatePartnerType>
            <Abs:OrderSourceSystemType>
                <Abs:Code>ECOMMERCE</Abs:Code>
            </Abs:OrderSourceSystemType>
        </Abs:GroceryOrderHeader>
        <Abs:GrocerySubOrder>
            <Abs:SubOrderNbr>1</Abs:SubOrderNbr>
            <Abs:SubOrderStatus/>
            <Abs:SubOrderActionStatus>
                <Abs:StatusTypeCd>UPDATE</Abs:StatusTypeCd>
                <Abs:Description>RESCHEDULE_SLOT</Abs:Description>
            </Abs:SubOrderActionStatus>
            <Abs:GroceryOrderDetail>
                <Abs:ItemId>
                    <Abs:SystemSpecificItemId>960037176</Abs:SystemSpecificItemId>
                    <Abs:BaseProductNbr>960037176</Abs:BaseProductNbr>
                    <Abs:ItemDescription>Signature SELECT/Refreshe Soda Cola Zero - 12-12 Fl. Oz.</Abs:ItemDescription>
                </Abs:ItemId>
                <Abs:UnitPriceAmt>3.9900</Abs:UnitPriceAmt>
                <Abs:Quantity>2.0000</Abs:Quantity>
                <Abs:UOM>
                    <Abs:UOMCd>Fz</Abs:UOMCd>
                </Abs:UOM>
                <Abs:SubstitutionType>
                    <Abs:Code>2</Abs:Code>
                    <Abs:Description>Same Brand Diff Size</Abs:Description>
                </Abs:SubstitutionType>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                <Abs:RegulatedItemInd>false</Abs:RegulatedItemInd>
                <Abs:Department/>
                <Abs:DeliveredItem>
                    <Abs:UOM/>
                </Abs:DeliveredItem>
            </Abs:GroceryOrderDetail>
            <Abs:GroceryOrderDetail>
                <Abs:ItemId>
                    <Abs:SystemSpecificItemId>960008285</Abs:SystemSpecificItemId>
                    <Abs:BaseProductNbr>960008285</Abs:BaseProductNbr>
                    <Abs:ItemDescription>Fresh Baked Garlic Parmesan Sourdough Bread</Abs:ItemDescription>
                </Abs:ItemId>
                <Abs:UnitPriceAmt>3.4900</Abs:UnitPriceAmt>
                <Abs:Quantity>3.0000</Abs:Quantity>
                <Abs:UOM>
                    <Abs:UOMCd>Ea</Abs:UOMCd>
                </Abs:UOM>
                <Abs:SubstitutionType>
                    <Abs:Code>2</Abs:Code>
                    <Abs:Description>Same Brand Diff Size</Abs:Description>
                </Abs:SubstitutionType>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                <Abs:RegulatedItemInd>false</Abs:RegulatedItemInd>
                <Abs:Department/>
                <Abs:DeliveredItem>
                    <Abs:UOM/>
                </Abs:DeliveredItem>
            </Abs:GroceryOrderDetail>
            <Abs:GroceryOrderDetail>
                <Abs:ItemId>
                    <Abs:SystemSpecificItemId>188100176</Abs:SystemSpecificItemId>
                    <Abs:BaseProductNbr>188100176</Abs:BaseProductNbr>
                    <Abs:ItemDescription>Ground Beef 93% Lean 7% Fat - 1.25 Lbs</Abs:ItemDescription>
                </Abs:ItemId>
                <Abs:UnitPriceAmt>9.9900</Abs:UnitPriceAmt>
                <Abs:Quantity>2.0000</Abs:Quantity>
                <Abs:UOM>
                    <Abs:UOMCd>Lb</Abs:UOMCd>
                </Abs:UOM>
                <Abs:SubstitutionType>
                    <Abs:Code>2</Abs:Code>
                    <Abs:Description>Same Brand Diff Size</Abs:Description>
                </Abs:SubstitutionType>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                <Abs:RegulatedItemInd>false</Abs:RegulatedItemInd>
                <Abs:Department/>
                <Abs:DeliveredItem>
                    <Abs:UOM/>
                </Abs:DeliveredItem>
            </Abs:GroceryOrderDetail>
            <Abs:FullFillmentType>
                <Abs:Code>DELIVERY</Abs:Code>
            </Abs:FullFillmentType>
            <Abs:PickupInfo>
                <Abs:PickupSlot/>
                <Abs:PickupSlotType/>
            </Abs:PickupInfo>
            <Abs:DeliveryInfo>
                <Abs:CustomerType>
                    <Abs:Code>RESIDENTIAL</Abs:Code>
                </Abs:CustomerType>
                <Abs:DeliverySlotId>1b9f3a65-2d2e-4bfa-9e3b-a27c454272d8</Abs:DeliverySlotId>
                <Abs:DeliverySlotType>
                    <Abs:Code>TWOHR</Abs:Code>
                </Abs:DeliverySlotType>
                <Abs:DeliveryServiceType>
                    <Abs:Code>ATTENDED</Abs:Code>
                </Abs:DeliveryServiceType>
                <Abs:SlotPlan>
                    <Abs:Code>STANDARD</Abs:Code>
                </Abs:SlotPlan>
                <Abs:StartDttm>2020-07-08T01:00:00.000Z</Abs:StartDttm>
                <Abs:EndDttm>2020-07-08T03:00:00.000Z</Abs:EndDttm>
                <Abs:SlotExpiryDttm>2020-07-08T03:00:00.000Z</Abs:SlotExpiryDttm>
                <Abs:StageByDttm>2020-07-08T03:00:00.000Z</Abs:StageByDttm>
                <Abs:EditCutoffDttm>2020-07-07T21:00:00.000Z</Abs:EditCutoffDttm>
                <Abs:CustomerInstructionTxt>Leave at the door step</Abs:CustomerInstructionTxt>
                <Abs:DeliveryTimeZoneCd>America/Los_Angeles</Abs:DeliveryTimeZoneCd>
            </Abs:DeliveryInfo>
            <Abs:ChargeInfo>
                <Abs:Charge>
                    <Abs:Code>0000000029103</Abs:Code>
                    <Abs:Description>BagFee</Abs:Description>
                </Abs:Charge>
                <Abs:ChargeCategory>
                    <Abs:Code>ServiceFee</Abs:Code>
                </Abs:ChargeCategory>
                <Abs:ChargeAmt>0.10</Abs:ChargeAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ChargeInfo>
            <Abs:ChargeInfo>
                <Abs:Charge>
                    <Abs:Code>0000000022151</Abs:Code>
                    <Abs:Description>DeliveryFee</Abs:Description>
                </Abs:Charge>
                <Abs:ChargeCategory>
                    <Abs:Code>DeliveryFee</Abs:Code>
                </Abs:ChargeCategory>
                <Abs:ChargeAmt>5.95</Abs:ChargeAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ChargeInfo>
            <Abs:CustomerService>
                <Abs:PhoneFaxContact TypeCode="MOBILE">
                    <Abs:PhoneNbr>7843793105</Abs:PhoneNbr>
                    <Abs:PhonePurposes>
                        <Abs:PurposeDsc>CUST_SERV_PHONE</Abs:PurposeDsc>
                    </Abs:PhonePurposes>
                </Abs:PhoneFaxContact>
            </Abs:CustomerService>
        </Abs:GrocerySubOrder>
    </GroceryOrderData>
</GetGroceryOrder>
```

# ECHO
## Input
```
{
  "orderNumber": "17310876",
  "versionNumber": 1,
  "orderStatus": "COMPLETED",
  "companyId": "1",
  "banner": "Safeway",
  "storeNumber": "1490",
  "orderCreatedDate": "2020-07-15T19:33:21.043Z",
  "sourceInfo": {
    "source": "ECHO",
    "enteredBy": "CUSTOMER",
    "deviceType": "WEB",
    "affiliate": {
      "affiliateName": "IBOTTA",
      "affiliateOrderRef": "A123544334-123"
    }
  },
  "tender": [
    {
      "tenderType": "REFUND",
      "tenderSubType": "credit_card_refund",
      "chargeAmount": {
        "amount": 3.95,
        "currency": "USD"
      }
    },
    {
      "tenderType": "CANCELLATION",
      "tenderSubType": "credit_card_refund",
      "chargeAmount": {
        "amount": 3.95,
        "currency": "USD"
      }
    }
  ],
  "orderTotal": {
    "amount": 79.87,
    "currency": "USD"
  },
  "customer": {
    "customerId": "300-368-1000020461",
    "clubCardNumber": "41032675319",
    "isSubscription": false,
    "name": {
      "firstName": "THEODORE",
      "lastName": "CHIAO"
    },
    "address": [
      {
        "addressType": "SHIP-TO",
        "addressLine1": "75 Folsom St Apt 901",
        "addressLine2": " ",
        "city": "San Francisco",
        "state": "CA",
        "zipCode": "94105",
        "country": "USA"
      }
    ],
    "contact": [
      {
        "number": "4155336055",
        "type": "MOBILE"
      }
    ],
    "email": [
      {
        "id": "tedhchiao@gmail.com",
        "type": "PERSONAL"
      }
    ],
    "preference": {
      "optIn": [
        {
          "type": "TEXT",
          "id": "4155336055",
          "isOptin": true
        }
      ]
    }
  },
  "paymentDetails": [
    {
      "paymentType": "CREDITCARD",
      "paymentSubType": "American Express",
      "tokenNumber": "933418729074015",
      "cardExpiryMonth": "07",
      "cardExpiryYear": "20",
      "zipcode": "94105",
      "reqAuthorizationAmount": "79.87",
      "cardHolderName": "Theodore Chiao",
      "address": {
        "zipCode": "94105"
      },
      "paymentStatus": {
        "status": "AUTHORIZED",
        "authorizationCode": "150569",
        "authorizationDate": "2020-07-16T06:33:21.079Z"
      }
    },
    {
      "paymentType": "CREDITONACCOUNT",
      "paymentSubType": "COA",
      "reqAuthorizationAmount": "10",
      "paymentStatus": {
        "status": "AUTHORIZED",
        "authorizationDate": "2020-07-07T18:18:04.476Z"
      }
    },
    {
      "paymentType": "EBT",
      "paymentSubType": "EBT",
      "reqAuthorizationAmount": "20",
      "paymentStatus": {
        "status": "SUSPENDED"
      }
    }
  ],
  "subOrders": [
    {
      "subOrderNumber": 1,
      "fulfillmentType": "DELIVERY",
      "customerService": {
        "contact": [
          {
            "number": "8775054040",
            "type": "PHONE",
            "subType": "CUST_SERV_PHONE"
          }
        ]
      },
      "deliveryInfo": {
        "deliverySubType": "RESIDENTIAL",
        "slotInfo": {
          "slotPlan": "STANDARD",
          "slotType": "FOURHR",
          "slotId": "7e4232d0-dc62-4156-8844-3040468f788f",
          "timeZone": "America/Los_Angeles",
          "startTime": "2020-07-16T15:01:00.000Z",
          "endTime": "2020-07-16T19:00:00.000Z",
          "editingCutoffDate": "2020-07-16T08:00:00.000Z"
        },
        "deliveryServiceType": "ATTENDED",
        "instructions": "",
        "stageByDateTime": "2020-07-16T19:00:00.000Z"
      },
      "charges": [
        {
          "id": "0000000029103",
          "name": "BagFee",
          "category": "ServiceFee",
          "chargeAmount": {
            "amount": 0.1,
            "currency": "USD"
          }
        },
        {
          "id": "0000000022155",
          "name": "DeliveryFee",
          "category": "DeliveryFee",
          "chargeAmount": {
            "amount": 3.95,
            "currency": "USD"
          }
        }
      ],
      "promoCodes": [],
      "orderLines": [
        {
          "itemId": "05050000022",
          "itemDescription": "FJ LIVERWURST",
          "orderedQuantity": 2,
          "unitOfMeasure": "UNIT",
          "unitPrice": {
            "amount": 4.99,
            "currency": "USD"
          },
          "suppliedQuantity": 2,
          "suppliedQuantityType": "UNIT",
          "suppliedUnitPrice": {
            "amount": 4.99,
            "currency": "USD"
          },
          "itemTotalTax": "0.0",
          "discountsApplied": "-0.99",
          "isRegulatedItem": false
        }
      ],
      "refundedOrderLines": [
        {
          "itemDescription": "FJ LIVERWURST",
          "refundedQuantity": 2,
          "unitOfMeasure": "",
          "unitPrice": {
            "amount": 4.99,
            "currency": "USD"
          }
        }
      ],
      "subOrderStatus": "COMPLETED"
    }
  ],
  "storeInfo": [
    {
      "key": "isHybridStore",
      "value": "true"
    },
    {
      "key": "isMFC",
      "value": "false"
    },
    {
      "key": "isErumsEnabled",
      "value": "true"
    },
    {
      "key": "isPremiumStore",
      "value": "true"
    },
    {
      "key": "is3PLStore",
      "value": "true"
    }
  ]
}
```

## Output
```
<?xml version="1.0" encoding="UTF-8"?>
<GetGroceryOrder xmlns:Abs="https://collab.safeway.com/it/architecture/info/default.aspx">
    <DocumentData>
        <Document SystemEnvironmentCd="PROD">
            <Abs:DocumentID>GROCERY_ORDER</Abs:DocumentID>
            <Abs:AlternateDocumentID>OSMS-EMOM_C02_ORDER-20200921093053506638</Abs:AlternateDocumentID>
            <Abs:DocumentNm>GroceryOrder</Abs:DocumentNm>
            <Abs:CreationDt>2020-09-21T09:30:53.506638-07:00</Abs:CreationDt>
            <Abs:Description>GroceryOrderDetail</Abs:Description>
            <Abs:SourceApplicationCd>eRUMS</Abs:SourceApplicationCd>
            <Abs:TargetApplicationCd>EDIS</Abs:TargetApplicationCd>
            <Abs:InternalFileTransferInd>Y</Abs:InternalFileTransferInd>
            <Abs:DataClassification>
                <Abs:DataClassificationLevel>
                    <Abs:Code>Internal</Abs:Code>
                </Abs:DataClassificationLevel>
                <Abs:BusinessSensitivityLevel>
                    <Abs:Code>Low</Abs:Code>
                </Abs:BusinessSensitivityLevel>
                <Abs:PHIdataInd>N</Abs:PHIdataInd>
                <Abs:PCIdataInd>Y</Abs:PCIdataInd>
                <Abs:PIIdataInd>Y</Abs:PIIdataInd>
            </Abs:DataClassification>
        </Document>
        <DocumentAction>
            <Abs:ActionTypeCd>UPDATE</Abs:ActionTypeCd>
            <Abs:RecordTypeCd>CHANGE</Abs:RecordTypeCd>
        </DocumentAction>
    </DocumentData>
    <GroceryOrderData>
        <Abs:GroceryOrderHeader>
            <Abs:OrderId>17310876</Abs:OrderId>
            <Abs:OrderCreateTS>2020-07-15T19:33:21.043Z</Abs:OrderCreateTS>
            <Abs:CompanyId>1</Abs:CompanyId>
            <Abs:VersionNbr>1</Abs:VersionNbr>
            <Abs:OrderStatus>
                <Abs:StatusTypeCd>COMPLETED</Abs:StatusTypeCd>
            </Abs:OrderStatus>
            <Abs:OrderActionStatus/>
            <Abs:FulfillingFacility>
                <Abs:RetailStoreId>1490</Abs:RetailStoreId>
                <Abs:BannerCd>Safeway</Abs:BannerCd>
                <Abs:HybridStoreInd>Y</Abs:HybridStoreInd>
                <Abs:MFCInd>N</Abs:MFCInd>
                <Abs:ERUMSEnabledInd>Y</Abs:ERUMSEnabledInd>
                <Abs:PremiumStoreInd>Y</Abs:PremiumStoreInd>
                <Abs:ThirdPartyDeliveryOnlyInd>Y</Abs:ThirdPartyDeliveryOnlyInd>
            </Abs:FulfillingFacility>
            <Abs:RetailCustomer>
                <Abs:CustomerId>300-368-1000020461</Abs:CustomerId>
                <Abs:CustomerNm>
                    <Abs:GivenNm>THEODORE</Abs:GivenNm>
                    <Abs:FamilyNm>CHIAO</Abs:FamilyNm>
                </Abs:CustomerNm>
                <Abs:Contact>
                    <Abs:PhoneFaxContact TypeCode="MOBILE">
                        <Abs:PhoneNbr>4155336055</Abs:PhoneNbr>
                    </Abs:PhoneFaxContact>
                </Abs:Contact>
                <Abs:Contact>
                    <Abs:DigitalContact>
                        <Abs:DigitalAddress>tedhchiao@gmail.com</Abs:DigitalAddress>
                        <Abs:EmailStatuses Abs:typeCode="PERSONAL"/>
                    </Abs:DigitalContact>
                </Abs:Contact>
                <Abs:Contact>
                    <Abs:Address>
                        <Abs:AddressUsageTypeCd>SHIP-TO</Abs:AddressUsageTypeCd>
                        <Abs:AddressLine1txt>75 Folsom St Apt 901</Abs:AddressLine1txt>
                        <Abs:AddressLine2txt></Abs:AddressLine2txt>
                        <Abs:CityNm>San Francisco</Abs:CityNm>
                        <Abs:StateCd>CA</Abs:StateCd>
                        <Abs:CountryCd>USA</Abs:CountryCd>
                    </Abs:Address>
                </Abs:Contact>
                <Abs:ClubCardNbr>41032675319</Abs:ClubCardNbr>
                <Abs:CustomerPreference>
                    <Abs:PreferenceType>
                        <Abs:Code>TEXT</Abs:Code>
                    </Abs:PreferenceType>
                    <Abs:OptInContactNbr>4155336055</Abs:OptInContactNbr>
                    <Abs:OptInInd>true</Abs:OptInInd>
                </Abs:CustomerPreference>
                <Abs:CustomerSubscription>
                    <Abs:OptInInd>false</Abs:OptInInd>
                </Abs:CustomerSubscription>
            </Abs:RetailCustomer>
            <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            <Abs:TotalAmt>79.87</Abs:TotalAmt>
            <Abs:CustomerPayment>
                <Abs:Tender>
                    <Abs:TenderId>933418729074015</Abs:TenderId>
                    <Abs:TenderTypeCd>CREDITCARD</Abs:TenderTypeCd>
                    <Abs:TenderSubTypeCd>American Express</Abs:TenderSubTypeCd>
                    <Abs:HolderNm>Theodore Chiao</Abs:HolderNm>
                    <Abs:ExpireMonthYearTxt>07</Abs:ExpireMonthYearTxt>
                    <Abs:BillingAddress>
                        <Abs:PostalZoneCd>94105</Abs:PostalZoneCd>
                    </Abs:BillingAddress>
                    <Abs:PostalCd>94105</Abs:PostalCd>
                    <Abs:Token>
                        <Abs:TokenId>933418729074015</Abs:TokenId>
                        <Abs:TokenTypeCd>CREDITCARD</Abs:TokenTypeCd>
                    </Abs:Token>
                    <Abs:Status>
                        <Abs:StatusCd>AUTHORIZED</Abs:StatusCd>
                    </Abs:Status>
                    <Abs:RequiredAuthAmt>79.87</Abs:RequiredAuthAmt>
                    <Abs:AuthCd>150569</Abs:AuthCd>
                    <Abs:AuthDt>2020-07-16T06:33:21.079Z</Abs:AuthDt>
                </Abs:Tender>
                <Abs:Tender>
                    <Abs:TenderTypeCd>CREDITONACCOUNT</Abs:TenderTypeCd>
                    <Abs:TenderSubTypeCd>COA</Abs:TenderSubTypeCd>
                    <Abs:BillingAddress/>
                    <Abs:Token>
                        <Abs:TokenTypeCd>CREDITONACCOUNT</Abs:TokenTypeCd>
                    </Abs:Token>
                    <Abs:Status>
                        <Abs:StatusCd>AUTHORIZED</Abs:StatusCd>
                    </Abs:Status>
                    <Abs:RequiredAuthAmt>10.00</Abs:RequiredAuthAmt>
                    <Abs:AuthDt>2020-07-07T18:18:04.476Z</Abs:AuthDt>
                </Abs:Tender>
                <Abs:Tender>
                    <Abs:TenderTypeCd>EBT</Abs:TenderTypeCd>
                    <Abs:TenderSubTypeCd>EBT</Abs:TenderSubTypeCd>
                    <Abs:BillingAddress/>
                    <Abs:Token>
                        <Abs:TokenTypeCd>EBT</Abs:TokenTypeCd>
                    </Abs:Token>
                    <Abs:Status>
                        <Abs:StatusCd>SUSPENDED</Abs:StatusCd>
                    </Abs:Status>
                    <Abs:RequiredAuthAmt>20.00</Abs:RequiredAuthAmt>
                </Abs:Tender>
                <Abs:Tender>
                    <Abs:Refund>
                        <Abs:TenderTypeCd>credit_card_refund</Abs:TenderTypeCd>
                        <Abs:TotalRefundAmt>3.95</Abs:TotalRefundAmt>
                        <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                    </Abs:Refund>
                </Abs:Tender>
                <Abs:Tender>
                    <Abs:Cancellation>
                        <Abs:TenderTypeCd>credit_card_refund</Abs:TenderTypeCd>
                        <Abs:TotalAmt>3.95</Abs:TotalAmt>
                        <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                    </Abs:Cancellation>
                </Abs:Tender>
            </Abs:CustomerPayment>
            <Abs:CustomerSavings/>
            <Abs:OrderRecordDateInfo>
                <Abs:CreateUserId>CUSTOMER</Abs:CreateUserId>
            </Abs:OrderRecordDateInfo>
            <Abs:OrderCreatedDeviceType>
                <Abs:Code>WEB</Abs:Code>
            </Abs:OrderCreatedDeviceType>
            <Abs:AffiliatePartnerType>
                <Abs:AffiliatePartnerNm>IBOTTA</Abs:AffiliatePartnerNm>
                <Abs:OrderReferenceTxt>A123544334-123</Abs:OrderReferenceTxt>
            </Abs:AffiliatePartnerType>
            <Abs:OrderSourceSystemType>
                <Abs:Code>ECHO</Abs:Code>
            </Abs:OrderSourceSystemType>
        </Abs:GroceryOrderHeader>
        <Abs:GrocerySubOrder>
            <Abs:SubOrderNbr>1</Abs:SubOrderNbr>
            <Abs:SubOrderStatus>
                <Abs:StatusTypeCd>COMPLETED</Abs:StatusTypeCd>
            </Abs:SubOrderStatus>
            <Abs:SubOrderActionStatus/>
            <Abs:GroceryOrderDetail>
                <Abs:ItemId>
                    <Abs:SystemSpecificItemId>05050000022</Abs:SystemSpecificItemId>
                    <Abs:BaseProductNbr>5050000022</Abs:BaseProductNbr>
                    <Abs:ItemDescription>FJ LIVERWURST</Abs:ItemDescription>
                </Abs:ItemId>
                <Abs:UnitPriceAmt>4.9900</Abs:UnitPriceAmt>
                <Abs:Quantity>2.0000</Abs:Quantity>
                <Abs:UOM>
                    <Abs:UOMCd>UNIT</Abs:UOMCd>
                </Abs:UOM>
                <Abs:SubstitutionType/>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                <Abs:RegulatedItemInd>false</Abs:RegulatedItemInd>
                <Abs:Department/>
                <Abs:DeliveredItem>
                    <Abs:Quantity>2.0000</Abs:Quantity>
                    <Abs:UOM>
                        <Abs:UOMCd>UNIT</Abs:UOMCd>
                    </Abs:UOM>
                    <Abs:UnitPriceAmt>4.9900</Abs:UnitPriceAmt>
                    <Abs:ItemTaxAmt>0.0000</Abs:ItemTaxAmt>
                    <Abs:AppliedDiscountAmt>-0.9900</Abs:AppliedDiscountAmt>
                    <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                </Abs:DeliveredItem>
            </Abs:GroceryOrderDetail>
            <Abs:ReturnedItem>
                <Abs:Item>
                    <Abs:ItemDescription>FJ LIVERWURST</Abs:ItemDescription>
                </Abs:Item>
                <Abs:Quantity>2.0000</Abs:Quantity>
                <Abs:UOM>
                    <Abs:UOMCd/>
                </Abs:UOM>
                <Abs:UnitPriceAmt>4.9900</Abs:UnitPriceAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ReturnedItem>
            <Abs:FullFillmentType>
                <Abs:Code>DELIVERY</Abs:Code>
            </Abs:FullFillmentType>
            <Abs:PickupInfo>
                <Abs:PickupSlot/>
                <Abs:PickupSlotType/>
            </Abs:PickupInfo>
            <Abs:DeliveryInfo>
                <Abs:CustomerType>
                    <Abs:Code>RESIDENTIAL</Abs:Code>
                </Abs:CustomerType>
                <Abs:DeliverySlotId>7e4232d0-dc62-4156-8844-3040468f788f</Abs:DeliverySlotId>
                <Abs:DeliverySlotType>
                    <Abs:Code>FOURHR</Abs:Code>
                </Abs:DeliverySlotType>
                <Abs:DeliveryServiceType>
                    <Abs:Code>ATTENDED</Abs:Code>
                </Abs:DeliveryServiceType>
                <Abs:SlotPlan>
                    <Abs:Code>STANDARD</Abs:Code>
                </Abs:SlotPlan>
                <Abs:StartDttm>2020-07-16T15:01:00.000Z</Abs:StartDttm>
                <Abs:EndDttm>2020-07-16T19:00:00.000Z</Abs:EndDttm>
                <Abs:SlotExpiryDttm>2020-07-16T19:00:00.000Z</Abs:SlotExpiryDttm>
                <Abs:StageByDttm>2020-07-16T19:00:00.000Z</Abs:StageByDttm>
                <Abs:EditCutoffDttm>2020-07-16T08:00:00.000Z</Abs:EditCutoffDttm>
                <Abs:CustomerInstructionTxt/>
                <Abs:DeliveryTimeZoneCd>America/Los_Angeles</Abs:DeliveryTimeZoneCd>
            </Abs:DeliveryInfo>
            <Abs:ChargeInfo>
                <Abs:Charge>
                    <Abs:Code>0000000029103</Abs:Code>
                    <Abs:Description>BagFee</Abs:Description>
                </Abs:Charge>
                <Abs:ChargeCategory>
                    <Abs:Code>ServiceFee</Abs:Code>
                </Abs:ChargeCategory>
                <Abs:ChargeAmt>0.10</Abs:ChargeAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ChargeInfo>
            <Abs:ChargeInfo>
                <Abs:Charge>
                    <Abs:Code>0000000022155</Abs:Code>
                    <Abs:Description>DeliveryFee</Abs:Description>
                </Abs:Charge>
                <Abs:ChargeCategory>
                    <Abs:Code>DeliveryFee</Abs:Code>
                </Abs:ChargeCategory>
                <Abs:ChargeAmt>3.95</Abs:ChargeAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ChargeInfo>
            <Abs:CustomerService>
                <Abs:PhoneFaxContact TypeCode="PHONE">
                    <Abs:PhoneNbr>8775054040</Abs:PhoneNbr>
                    <Abs:PhonePurposes>
                        <Abs:PurposeDsc>CUST_SERV_PHONE</Abs:PurposeDsc>
                    </Abs:PhonePurposes>
                </Abs:PhoneFaxContact>
            </Abs:CustomerService>
        </Abs:GrocerySubOrder>
    </GroceryOrderData>
</GetGroceryOrder>
```

# WYSIWYG
## Input
```
{
  "orderNumber": "18182589",
  "versionNumber": 2,
  "orderStatus": "PAYMENT_REQUESTED",
  "orderStatusReasonCode": "CUS_RESCEDULE_CANCEL",
  "messageAction": "UPDATE",
  "messageActionReason": "",
  "companyId": "1",
  "banner": "JewelOsco",
  "isActive": true,
  "storeNumber": "0607",
  "orderCreatedDate": "2020-08-13T07:51:11.179Z",
  "fulfillmentSystem": "MANHATTAN",
  "sourceInfo": {
    "source": "ECOMMERCE",
    "enteredBy": "CUSTOMER",
    "deviceType": "MOBILE",
    "affiliate": {
      "affiliateName": "IBOTTA",
      "affiliateOrderRef": "A123544334-123"
    }
  },
  "orderTotal": {
    "amount": "263.56",
    "currency": "USD",
    "totalCardSavings": 15.51,
    "cardSavings": [
      {
        "savingsCategoryId": 123,
        "savingsCategoryName": "String",
        "savingsAmount": 12.23
      }
    ]
  },
  "customer": {
    "customerId": "556-020-1586122641346",
    "clubCardNumber": "49130429968",
    "isSubscription": false,
    "memberId": "1231321323",
    "name": {
      "firstName": "JASON",
      "lastName": "GRESS"
    },
    "address": [
      {
        "addressType": "SHIPTO",
        "addressLine1": "14448 Donna Ln",
        "addressLine2": " ",
        "city": "Saratoga",
        "state": "CA",
        "zipCode": "95070",
        "country": "USA"
      }
    ],
    "contact": [
      {
        "number": "4088577000",
        "type": "MOBILE"
      }
    ],
    "email": [
      {
        "id": "gressholdings@gmail.com",
        "type": "PERSONAL"
      }
    ],
    "preference": {
      "termsCheckedVersionId": "0",
      "optIn": [
        {
          "id": "4088577000",
          "type": "TEXT",
          "isOptin": true
        }
      ]
    }
  },
  "paymentDetails": [
    {
      "paymentType": "CREDITCARD",
      "paymentSubType": "AMEX",
      "tokenNumber": "846787175413009",
      "cardExpiryMonth": "10",
      "cardExpiryYear": "24",
      "zipcode": "95070",
      "reqAuthorizationAmount": "263.56",
      "cardHolderName": "Jason Gress",
      "address": {
        "zipCode": "95070"
      },
      "paymentStatus": {
        "status": "NOTAUTHORIZED",
        "authorizationCode": "201946",
        "authorizationDate": "2020-08-13T07:51:11.233Z"
      }
    }
  ],
  "subOrders": [
    {
      "subOrderNumber": 1,
      "subOrderStatusReasonCode": "RESCHEDULE_SLOT",
      "messageAction": "UPDATE",
      "messageActionReason": "RESCHEDULE_SLOT",
      "fulfillmentType": "DELIVERY",
      "customerService": {
        "contact": [
          {
            "number": "8775054040",
            "type": "PHONE",
            "subType": "CUST_SERV_PHONE"
          }
        ]
      },
      "deliveryInfo": {
        "deliverySubType": "RESIDENTIAL",
        "slotInfo": {
          "slotPlan": "STANDARD",
          "slotType": "FOURHR",
          "slotId": "95e9bd9c-bedd-41ac-b0ad-04bebd4d7eb9",
          "timeZone": "America/Los_Angeles",
          "startTime": "2020-08-13T15:01:00.000Z",
          "endTime": "2020-08-13T19:00:00.000Z",
          "shiftNumber": "7",
          "lastPickupTime": "2020-03-25T20:00:00.000Z",
          "editingCutoffDate": "2020-08-13T08:00:00.000Z"
        },
        "deliveryServiceType": "ATTENDED",
        "instructions": "",
        "stageByDateTime": "2020-08-13T14:21:00.000Z",
        "pickupInfo": {
          "locationType": "STORE",
          "locationId": "1211",
          "shortOrderNumber": 4
        }
      },
      "charges": [
        {
          "id": "0000000029103",
          "name": "BagFee",
          "category": "ServiceFee",
          "chargeAmount": {
            "amount": "0.1",
            "currency": "USD"
          }
        },
        {
          "id": "0000000022155",
          "name": "DeliveryFee",
          "category": "DeliveryFee",
          "chargeAmount": {
            "amount": "3.95",
            "currency": "USD"
          }
        }
      ],
      "promoCodes": [
        {
          "code": "SAVE20",
          "description": "$20 Off Orders Over $75",
          "pluCode": "00001234"
        }
      ],
      "routeInfo": {
        "vanNumber": "DDS",
        "stopNumber": "377"
      },
      "tote": {
        "toteEstimate": {
          "chilled": 6,
          "frozen": 2,
          "ambient": 7
        },
        "toteDetails": [
          {
            "toteId": "",
            "item": []
          },
          {
            "toteId": "99800400215899",
            "item": [
              {
                "itemId": "960027187",
                "fulfilledUpc": [
                  {
                    "upcId": "003320009471",
                    "upcQuantity": 1,
                    "scannedUpc": "033200094715",
                    "pickedBy": "SYSTEM",
                    "pickedDate": "2020-08-13T09:38:01.782Z",
                    "pickType": "REGULAR"
                  }
                ]
              }
            ]
          }
        ],
        "totalPickedToteCount": 4,
        "totalNumberOfBagsUsed": 5
      },
      "orderLines": [
        {
          "itemId": "196011495",
          "itemDescription": "San Luis Sourdough Bread Round - 24 Oz",
          "orderedQuantity": 1,
          "shortedQuantity": 0,
          "fulfilledQuantity": 1,
          "unitOfMeasure": "OZ",
          "unitPrice": {
            "amount": "4.99",
            "currency": "USD"
          },
          "substitutionCode": "2",
          "substitutionValue": "Same Brand Diff Size",
          "isRegulatedItem": false,
          "comments": "",
          "fulfilledUpc": [
            {
              "upcId": "001853724157",
              "entryId": 100,
              "upcQuantity": 1,
              "pickedBy": "SYSTEM",
              "pickedDate": "2020-08-13T09:38:01.782Z",
              "pickType": "REGULAR",
              "scanPrice": 2.33,
              "isSubstituted": false,
              "itemPrice": {
                "itemCode": "UPC",
                "entryId": 100,
                "department": 30,
                "unitPrice": 20.5,
                "extendedPrice": 5.5,
                "quantityType": "LB",
                "quantityValue": 4.45,
                "discountAllowed": true,
                "linkPluNumber": "promo PLU",
                "startDate": "2020-08-13T09:38:01.782Z",
                "endDate": "2020-08-14T09:38:01.782Z",
                "itemPluNumber": "embedded item PLU",
                "pointsApplyItem": true,
                "wic": false,
                "substituted": false,
                "netPromotionAmount": 205.49,
                "savings": [
                  {
                    "offerId": "463272",
                    "externalOfferId": "463274",
                    "category": 1,
                    "source": "CPE",
                    "linkpluNumber": "promo PLU",
                    "programCode": "SC",
                    "startDate": "2020-08-13T07:00:00.000+0000",
                    "endDate": "2020-08-14T07:00:00.000+0000",
                    "discountAmount": 14.94,
                    "discountType": "Free",
                    "description": "Default Description",
                    "discountMessage": "discount",
                    "discountLevel": "Item Level",
                    "promotionPrice": 2.59,
                    "netPromotionAmount": 8.43,
                    "points": [
                      {
                        "programName": "String",
                        "earn": 1,
                        "burn": 1
                      }
                    ]
                  }
                ]
              }
            },
            {
              "upcId": "001853724157",
              "entryId": 200,
              "upcQuantity": 1,
              "pickedBy": "SYSTEM",
              "pickedDate": "2020-08-13T09:38:01.782Z",
              "pickType": "REGULAR",
              "scanWeight": 3.98,
              "isSubstituted": true,
              "itemPrice": {
                "itemCode": "UPC",
                "entryId": 100,
                "department": 30,
                "unitPrice": 20.5,
                "extendedPrice": 5.5,
                "quantityType": "LB",
                "quantityValue": 4.45,
                "discountAllowed": true,
                "linkPluNumber": "promo PLU",
                "startDate": "2020-08-13T09:38:01.782Z",
                "endDate": "2020-08-14T09:38:01.782Z",
                "itemPluNumber": "embedded item PLU",
                "pointsApplyItem": true,
                "wic": false,
                "substituted": false,
                "netPromotionAmount": 205.49,
                "savings": [
                  {
                    "offerId": "463272",
                    "externalOfferId": "463274",
                    "category": 1,
                    "source": "CPE",
                    "linkpluNumber": "promo PLU",
                    "programCode": "SC",
                    "startDate": "2020-08-13T07:00:00.000+0000",
                    "endDate": "2020-08-14T07:00:00.000+0000",
                    "discountAmount": 14.94,
                    "discountType": "Free",
                    "description": "Default Description",
                    "discountMessage": "discount",
                    "discountLevel": "Item Level",
                    "promotionPrice": 2.59,
                    "netPromotionAmount": 8.43,
                    "points": [
                      {
                        "programName": "String",
                        "earn": 1,
                        "burn": 1
                      }
                    ]
                  }
                ]
              }
            }
          ],
          "itemPrice": {
            "itemCode": "UPC",
            "entryId": 100,
            "department": 30,
            "unitPrice": 20.5,
            "extendedPrice": 5.5,
            "quantityType": "LB",
            "quantityValue": 4.45,
            "discountAllowed": true,
            "linkPluNumber": "promo PLU",
            "startDate": "2020-08-13T09:38:01.782Z",
            "endDate": "2020-08-14T09:38:01.782Z",
            "itemPluNumber": "embedded item PLU",
            "pointsApplyItem": true,
            "wic": false,
            "substituted": false,
            "netPromotionAmount": 205.49,
            "savings": [
              {
                "offerId": "463272",
                "externalOfferId": "463274",
                "category": 1,
                "source": "CPE",
                "linkpluNumber": "promo PLU",
                "programCode": "SC",
                "startDate": "2020-08-13T07:00:00.000+0000",
                "endDate": "2020-08-14T07:00:00.000+0000",
                "discountAmount": 14.94,
                "discountType": "Free",
                "description": "Default Description",
                "discountMessage": "discount",
                "discountLevel": "Item Level",
                "promotionPrice": 2.59,
                "netPromotionAmount": 8.43,
                "points": [
                  {
                    "programName": "String",
                    "earn": 1,
                    "burn": 1
                  }
                ]
              }
            ]
          }
        }
      ]
    }
  ],
  "storeInfo": [
    {
      "key": "isHybridStore",
      "value": "true"
    },
    {
      "key": "isMFC",
      "value": "false"
    },
    {
      "key": "isErumsEnabled",
      "value": "true"
    },
    {
      "key": "isPremiumStore",
      "value": "true"
    },
    {
      "key": "is3PLStore",
      "value": "true"
    },
    {
      "key": "isDUGArrivalEnabled",
      "value": "false"
    },
    {
      "key": "storeTimeZone",
      "value": "PST"
    },
    {
      "key": "terminalNumber",
      "value": "99"
    },
    {
      "key": "isWYSIWYGEnabled",
      "value": "true"
    }
  ],
  "orderInfo": {
    "stageByDateTime": "2020-08-13T14:21:00.000Z",
    "data": [
      {
        "key": "key",
        "value": "value"
      }
    ]
  },
  "audit": {
    "createDate": "2020-08-13T12:27:32.039Z",
    "modifiedDate": "2020-08-13T12:27:32.039Z",
    "createdBy": "OSCO-SERVICES"
  }
}
```

## Output
```
<GetGroceryOrder xmlns:Abs="https://collab.safeway.com/it/architecture/info/default.aspx">
    <DocumentData>
        <Document VersionId="1.4.003" SystemEnvironmentCd="QA">
            <Abs:DocumentID>GROCERY_ORDER</Abs:DocumentID>
            <Abs:AlternateDocumentID>OSMS-EMOM_C02_ORDER-20200923180130362600</Abs:AlternateDocumentID>
            <Abs:DocumentNm>GroceryOrder</Abs:DocumentNm>
            <Abs:CreationDt>2020-09-23T18:01:30.362600</Abs:CreationDt>
            <Abs:Description>GroceryOrderDetail</Abs:Description>
            <Abs:SourceApplicationCd>eRUMS</Abs:SourceApplicationCd>
            <Abs:TargetApplicationCd>EDIS</Abs:TargetApplicationCd>
            <Abs:InternalFileTransferInd>Y</Abs:InternalFileTransferInd>
            <Abs:DataClassification>
                <Abs:DataClassificationLevel>
                    <Abs:Code>Internal</Abs:Code>
                </Abs:DataClassificationLevel>
                <Abs:BusinessSensitivityLevel>
                    <Abs:Code>Low</Abs:Code>
                </Abs:BusinessSensitivityLevel>
                <Abs:PHIdataInd>N</Abs:PHIdataInd>
                <Abs:PCIdataInd>Y</Abs:PCIdataInd>
                <Abs:PIIdataInd>Y</Abs:PIIdataInd>
            </Abs:DataClassification>
        </Document>
        <DocumentAction>
            <Abs:ActionTypeCd>UPDATE</Abs:ActionTypeCd>
            <Abs:RecordTypeCd>CHANGE</Abs:RecordTypeCd>
        </DocumentAction>
    </DocumentData>
    <GroceryOrderData>
        <Abs:GroceryOrderHeader>
            <Abs:OrderId>18182589</Abs:OrderId>
            <Abs:OrderCreateTS>2020-08-13T07:51:11.179Z</Abs:OrderCreateTS>
            <Abs:CompanyId>1</Abs:CompanyId>
            <Abs:VersionNbr>2</Abs:VersionNbr>
            <Abs:OrderStatus>
                <Abs:StatusTypeCd>PAYMENT_REQUESTED</Abs:StatusTypeCd>
            </Abs:OrderStatus>
            <Abs:OrderActionStatus>
                <Abs:StatusTypeCd>UPDATE</Abs:StatusTypeCd>
                <Abs:Description></Abs:Description>
            </Abs:OrderActionStatus>
            <Abs:FulfillingFacility>
                <Abs:RetailStoreId>0607</Abs:RetailStoreId>
                <Abs:BannerCd>JewelOsco</Abs:BannerCd>
                <Abs:HybridStoreInd>Y</Abs:HybridStoreInd>
                <Abs:MFCInd>N</Abs:MFCInd>
                <Abs:ERUMSEnabledInd>Y</Abs:ERUMSEnabledInd>
                <Abs:PremiumStoreInd>Y</Abs:PremiumStoreInd>
                <Abs:ThirdPartyDeliveryOnlyInd>Y</Abs:ThirdPartyDeliveryOnlyInd>
            </Abs:FulfillingFacility>
            <Abs:GroceryOrderProfileType>
                <Abs:ProfileType>
                    <Abs:Code>WYSIWYG</Abs:Code>
                </Abs:ProfileType>
                <Abs:ProfileValueInd>true</Abs:ProfileValueInd>
            </Abs:GroceryOrderProfileType>
            <Abs:RetailCustomer>
                <Abs:CustomerId>556-020-1586122641346</Abs:CustomerId>
                <Abs:CustomerNm>
                    <Abs:GivenNm>Q8xxD</Abs:GivenNm>
                    <Abs:FamilyNm>BDHT5</Abs:FamilyNm>
                </Abs:CustomerNm>
                <Abs:Contact>
                    <Abs:PhoneFaxContact TypeCode="MOBILE">
                        <Abs:PhoneNbr>4D4zGn6g9l</Abs:PhoneNbr>
                    </Abs:PhoneFaxContact>
                </Abs:Contact>
                <Abs:Contact>
                    <Abs:DigitalContact>
                        <Abs:DigitalAddress>pJDqsSHBOyNqW@Vydgj.8qp</Abs:DigitalAddress>
                        <Abs:EmailStatuses Abs:typeCode="PERSONAL"/>
                    </Abs:DigitalContact>
                </Abs:Contact>
                <Abs:Contact>
                    <Abs:Address>
                        <Abs:AddressUsageTypeCd>epgjqg</Abs:AddressUsageTypeCd>
                        <Abs:AddressLine1txt>lgG9T iBOZy P6</Abs:AddressLine1txt>
                        <Abs:AddressLine2txt></Abs:AddressLine2txt>
                        <Abs:CityNm>eNozeohY</Abs:CityNm>
                        <Abs:PostalZoneCd>t61WJ</Abs:PostalZoneCd>
                        <Abs:StateCd>Hpz</Abs:StateCd>
                        <Abs:CountryCd>USA</Abs:CountryCd>
                    </Abs:Address>
                </Abs:Contact>
                <Abs:ClubCardNbr>9LPMIqnFAJN</Abs:ClubCardNbr>
                <Abs:CustomerPreference>
                    <Abs:PreferenceType>
                        <Abs:Code>TEXT</Abs:Code>
                    </Abs:PreferenceType>
                    <Abs:OptInContactNbr>4088577000</Abs:OptInContactNbr>
                    <Abs:OptInInd>true</Abs:OptInInd>
                </Abs:CustomerPreference>
                <Abs:CustomerSubscription>
                    <Abs:OptInInd>false</Abs:OptInInd>
                </Abs:CustomerSubscription>
            </Abs:RetailCustomer>
            <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            <Abs:TotalAmt>263.56</Abs:TotalAmt>
            <Abs:CustomerPayment>
                <Abs:Tender>
                    <Abs:TenderId>YjQXWIpp4Uc9XX4</Abs:TenderId>
                    <Abs:TenderTypeCd>CREDITCARD</Abs:TenderTypeCd>
                    <Abs:TenderSubTypeCd>AMEX</Abs:TenderSubTypeCd>
                    <Abs:HolderNm>C9loT cjVtc</Abs:HolderNm>
                    <Abs:ExpireMonthYearTxt>xkdx</Abs:ExpireMonthYearTxt>
                    <Abs:BillingAddress>
                        <Abs:PostalZoneCd>t61WJ</Abs:PostalZoneCd>
                    </Abs:BillingAddress>
                    <Abs:PostalCd>sBhJC</Abs:PostalCd>
                    <Abs:Token>
                        <Abs:TokenId>YjQXWIpp4Uc9XX4</Abs:TokenId>
                        <Abs:TokenTypeCd>CREDITCARD</Abs:TokenTypeCd>
                    </Abs:Token>
                    <Abs:Status>
                        <Abs:StatusCd>NOTAUTHORIZED</Abs:StatusCd>
                    </Abs:Status>
                    <Abs:RequiredAuthAmt>263.56</Abs:RequiredAuthAmt>
                    <Abs:AuthCd>RzmCI2</Abs:AuthCd>
                    <Abs:AuthDt>2020-08-13T07:51:11.233Z</Abs:AuthDt>
                </Abs:Tender>
            </Abs:CustomerPayment>
            <Abs:CustomerSavings>
                <Abs:SavingsCategoryType>
                    <Abs:SavingsCategoryId>123</Abs:SavingsCategoryId>
                    <Abs:SavingsCategoryNm>String</Abs:SavingsCategoryNm>
                    <Abs:SavingsAmt>12.23</Abs:SavingsAmt>
                </Abs:SavingsCategoryType>
            </Abs:CustomerSavings>
            <Abs:OrderRecordDateInfo>
                <Abs:CreateUserId>CUSTOMER</Abs:CreateUserId>
            </Abs:OrderRecordDateInfo>
            <Abs:OrderCreatedDeviceType>
                <Abs:Code>MOBILE</Abs:Code>
            </Abs:OrderCreatedDeviceType>
            <Abs:AffiliatePartnerType>
                <Abs:AffiliatePartnerNm>IBOTTA</Abs:AffiliatePartnerNm>
                <Abs:OrderReferenceTxt>A123544334-123</Abs:OrderReferenceTxt>
            </Abs:AffiliatePartnerType>
            <Abs:OrderSourceSystemType>
                <Abs:Code>ECOMMERCE</Abs:Code>
            </Abs:OrderSourceSystemType>
        </Abs:GroceryOrderHeader>
        <Abs:GrocerySubOrder>
            <Abs:SubOrderNbr>1</Abs:SubOrderNbr>
            <Abs:SubOrderStatus/>
            <Abs:SubOrderActionStatus>
                <Abs:StatusTypeCd>UPDATE</Abs:StatusTypeCd>
                <Abs:Description>RESCHEDULE_SLOT</Abs:Description>
            </Abs:SubOrderActionStatus>
            <Abs:GroceryOrderDetail>
                <Abs:CartLineNbr>100</Abs:CartLineNbr>
                <Abs:ItemId>
                    <Abs:SystemSpecificItemId>196011495</Abs:SystemSpecificItemId>
                    <Abs:InternalItemId>UPC</Abs:InternalItemId>
                    <Abs:BaseProductNbr>196011495</Abs:BaseProductNbr>
                    <Abs:ItemDescription>San Luis Sourdough Bread Round - 24 Oz</Abs:ItemDescription>
                </Abs:ItemId>
                <Abs:UnitPriceAmt>4.9900</Abs:UnitPriceAmt>
                <Abs:ExtendedUnitPriceAmt>5.5000</Abs:ExtendedUnitPriceAmt>
                <Abs:Quantity>1.0000</Abs:Quantity>
                <Abs:UOM>
                    <Abs:UOMCd>OZ</Abs:UOMCd>
                </Abs:UOM>
                <Abs:DiscountAllowedInd>true</Abs:DiscountAllowedInd>
                <Abs:RewardAllowedInd>true</Abs:RewardAllowedInd>
                <Abs:SubstitutionType>
                    <Abs:Code>2</Abs:Code>
                    <Abs:Description>Same Brand Diff Size</Abs:Description>
                </Abs:SubstitutionType>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
                <Abs:RegulatedItemInd>false</Abs:RegulatedItemInd>
                <Abs:LinkPLUNbr>promo PLU</Abs:LinkPLUNbr>
                <Abs:CouponPLUNbr>embedded item PLU</Abs:CouponPLUNbr>
                <Abs:CustomerOffers>
                    <Abs:OfferId>463272</Abs:OfferId>
                    <Abs:OfferSourceType>
                        <Abs:Code>CPE</Abs:Code>
                    </Abs:OfferSourceType>
                    <Abs:CustomerRewards>
                        <Abs:RewardsProgramNm>String</Abs:RewardsProgramNm>
                        <Abs:EarnedQty>1</Abs:EarnedQty>
                        <Abs:UsedQty>1</Abs:UsedQty>
                    </Abs:CustomerRewards>
                </Abs:CustomerOffers>
                <Abs:Department>
                    <Abs:Code>30</Abs:Code>
                </Abs:Department>
                <Abs:WICItemInd>false</Abs:WICItemInd>
                <Abs:DeliveredItem>
                    <Abs:UOM/>
                </Abs:DeliveredItem>
            </Abs:GroceryOrderDetail>
            <Abs:GroceryOrderDetail>
                <Abs:PromotionType>
                    <Abs:Code>SAVE20</Abs:Code>
                    <Abs:Description>$20 Off Orders Over $75</Abs:Description>
                </Abs:PromotionType>
                <Abs:PromotionTriggerCd>00001234</Abs:PromotionTriggerCd>
            </Abs:GroceryOrderDetail>
            <Abs:FullFillmentType>
                <Abs:Code>DELIVERY</Abs:Code>
            </Abs:FullFillmentType>
            <Abs:PickupInfo>
                <Abs:PickupSlot>
                    <Abs:Code>1211</Abs:Code>
                </Abs:PickupSlot>
                <Abs:PickupSlotType>
                    <Abs:Code>STORE</Abs:Code>
                </Abs:PickupSlotType>
                <Abs:DisplayEndDttm>2020-03-25T20:00:00.000Z</Abs:DisplayEndDttm>
            </Abs:PickupInfo>
            <Abs:DeliveryInfo>
                <Abs:CustomerType>
                    <Abs:Code>RESIDENTIAL</Abs:Code>
                </Abs:CustomerType>
                <Abs:DeliverySlotId>95e9bd9c-bedd-41ac-b0ad-04bebd4d7eb9</Abs:DeliverySlotId>
                <Abs:DeliverySlotType>
                    <Abs:Code>FOURHR</Abs:Code>
                </Abs:DeliverySlotType>
                <Abs:DeliveryServiceType>
                    <Abs:Code>ATTENDED</Abs:Code>
                </Abs:DeliveryServiceType>
                <Abs:SlotPlan>
                    <Abs:Code>STANDARD</Abs:Code>
                </Abs:SlotPlan>
                <Abs:StartDttm>2020-08-13T15:01:00.000Z</Abs:StartDttm>
                <Abs:EndDttm>2020-08-13T19:00:00.000Z</Abs:EndDttm>
                <Abs:SlotExpiryDttm>2020-08-13T19:00:00.000Z</Abs:SlotExpiryDttm>
                <Abs:StageByDttm>2020-08-13T14:21:00.000Z</Abs:StageByDttm>
                <Abs:EditCutoffDttm>2020-08-13T08:00:00.000Z</Abs:EditCutoffDttm>
                <Abs:CustomerInstructionTxt></Abs:CustomerInstructionTxt>
                <Abs:DeliveryTimeZoneCd>America/Los_Angeles</Abs:DeliveryTimeZoneCd>
            </Abs:DeliveryInfo>
            <Abs:ChargeInfo>
                <Abs:Charge>
                    <Abs:Code>0000000029103</Abs:Code>
                    <Abs:Description>BagFee</Abs:Description>
                </Abs:Charge>
                <Abs:ChargeCategory>
                    <Abs:Code>ServiceFee</Abs:Code>
                </Abs:ChargeCategory>
                <Abs:ChargeAmt>0.10</Abs:ChargeAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ChargeInfo>
            <Abs:ChargeInfo>
                <Abs:Charge>
                    <Abs:Code>0000000022155</Abs:Code>
                    <Abs:Description>DeliveryFee</Abs:Description>
                </Abs:Charge>
                <Abs:ChargeCategory>
                    <Abs:Code>DeliveryFee</Abs:Code>
                </Abs:ChargeCategory>
                <Abs:ChargeAmt>3.95</Abs:ChargeAmt>
                <Abs:CurrencyCd>USD</Abs:CurrencyCd>
            </Abs:ChargeInfo>
            <Abs:CustomerService>
                <Abs:PhoneFaxContact TypeCode="PHONE">
                    <Abs:PhoneNbr>8775054040</Abs:PhoneNbr>
                    <Abs:PhonePurposes>
                        <Abs:PurposeDsc>CUST_SERV_PHONE</Abs:PurposeDsc>
                    </Abs:PhonePurposes>
                </Abs:PhoneFaxContact>
            </Abs:CustomerService>
        </Abs:GrocerySubOrder>
    </GroceryOrderData>
</GetGroceryOrder>
```