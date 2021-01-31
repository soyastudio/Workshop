# Renderers
## JsonSourceMappings
```
{
  "aggregateId": [
    "GetCustomerPreferences/DocumentData/Document/DocumentID",
    "GetCustomerPreferences/CustomerPreferencesData/CustomerId"
  ],
  "state": {
    "preferences[*]": {
      "_class": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm",
      "preferenceId": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd",
      "value": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal",
      "categoryCode": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd",
      "optChoices[*]": {
        "choice": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc"
        ]
      },
      "optchoices[*]": {
        "reasonCode": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd"
        ],
        "reasonText": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc"
        ]
      },
      "bannerId": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd",
      "preferredInd": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd",
      "channel": {
        "type": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd"
      },
      "platform": {
        "type": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd"
      },
      "effectiveTimePeriods[*]": {
        "type": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode"
        ],
        "startDate": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt"
        ],
        "startTime": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm"
        ],
        "endDate": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt"
        ],
        "endTime": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm"
        ],
        "duration": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr"
        ],
        "inclusiveInd": [
          "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd",
          "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd"
        ]
      },
      "subCategoryCode": [
        "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd",
        "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd"
      ],
      "type": [
        "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd",
        "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd"
      ],
      "deliverySubscriptionOffer": {
        "serviceFeeWaived": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd",
        "deliveryFeeWaived": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd",
        "fuelSurchargeWaived": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd",
        "minBasketSize": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty"
      },
      "autoRenew": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd",
      "autoEnroll": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd"
    },
    "preferences": {
      "value": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId",
      "deliverySubscriptionOffer": {
        "fee": {
          "currency": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd",
          "amount": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt"
        },
        "initialOrderAmountOff": {
          "currency": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd"
        }
      }
    },
    "createTimestamp": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs",
    "lastUpdateTimestamp": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs",
    "createClientId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId",
    "createUserId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId",
    "lastUpdateClientId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId",
    "lastUpdateUserId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId",
    "createHostName": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm",
    "lastUpdateHostName": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm",
    "sequenceNumber": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr",
    "timestamp": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs",
    "aggregateRevision": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr",
    "version": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr",
    "eventId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId"
  },
  "aggregateType": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm"
}

```
## XmlSchema
```
TODO

```
## AvroSchema
```
{
  "type" : "record",
  "name" : "GetCustomerPreferences",
  "namespace" : "com.albertsons.esed.cmm",
  "fields" : [ {
    "name" : "DocumentData",
    "type" : {
      "type" : "array",
      "items" : {
        "type" : "record",
        "name" : "DocumentData",
        "fields" : [ {
          "name" : "Document",
          "type" : {
            "type" : "record",
            "name" : "DocumentType",
            "fields" : [ {
              "name" : "DocumentID",
              "type" : "string"
            }, {
              "name" : "AlternateDocumentID",
              "type" : "string"
            }, {
              "name" : "InboundOutboundInd",
              "type" : "string"
            }, {
              "name" : "DocumentNm",
              "type" : "string"
            }, {
              "name" : "CreationDt",
              "type" : "string"
            }, {
              "name" : "TargetApplicationCd",
              "type" : "string"
            }, {
              "name" : "Note",
              "type" : "string"
            }, {
              "name" : "GatewayNm",
              "type" : "string"
            }, {
              "name" : "SenderId",
              "type" : "string"
            }, {
              "name" : "ReceiverId",
              "type" : "string"
            }, {
              "name" : "RoutingSystemNm",
              "type" : "string"
            }, {
              "name" : "InternalFileTransferInd",
              "type" : "string"
            }, {
              "name" : "InterchangeDate",
              "type" : "string"
            }, {
              "name" : "InterchangeTime",
              "type" : "string"
            }, {
              "name" : "ExternalTargetInd",
              "type" : "string"
            }, {
              "name" : "MessageSequenceNbr",
              "type" : "long"
            }, {
              "name" : "ExpectedMessageCnt",
              "type" : "long"
            }, {
              "name" : "DataClassification",
              "type" : {
                "type" : "record",
                "name" : "DataClassificationType",
                "fields" : [ {
                  "name" : "DataClassificationLevel",
                  "type" : {
                    "type" : "record",
                    "name" : "CodeWithDescription",
                    "fields" : [ {
                      "name" : "Code",
                      "type" : "string"
                    }, {
                      "name" : "Description",
                      "type" : "string"
                    }, {
                      "name" : "ShortDescription",
                      "type" : "string"
                    } ]
                  }
                }, {
                  "name" : "BusinessSensitivityLevel",
                  "type" : "CodeWithDescription"
                }, {
                  "name" : "PHIdataInd",
                  "type" : "string"
                }, {
                  "name" : "PCIdataInd",
                  "type" : "string"
                }, {
                  "name" : "PIIdataInd",
                  "type" : "string"
                } ]
              }
            } ]
          }
        }, {
          "name" : "DocumentAction",
          "type" : {
            "type" : "record",
            "name" : "DocumentActionType",
            "fields" : [ {
              "name" : "ActionTypeCd",
              "type" : "string"
            }, {
              "name" : "RecordTypeCd",
              "type" : "string"
            } ]
          }
        } ]
      }
    }
  }, {
    "name" : "CustomerPreferencesData",
    "type" : {
      "type" : "array",
      "items" : {
        "type" : "record",
        "name" : "CustomerPreferencesType",
        "fields" : [ {
          "name" : "CustomerId",
          "type" : {
            "type" : "record",
            "name" : "IDType",
            "fields" : [ ]
          }
        }, {
          "name" : "CustomerAlternateId",
          "type" : {
            "type" : "array",
            "items" : {
              "type" : "record",
              "name" : "AlternateId",
              "fields" : [ {
                "name" : "AlternateIdTxt",
                "type" : "string"
              }, {
                "name" : "AlternateIdNbr",
                "type" : "double"
              } ]
            }
          }
        }, {
          "name" : "CustomerPreferences",
          "type" : {
            "type" : "array",
            "items" : {
              "type" : "record",
              "name" : "CustomerPreferences",
              "fields" : [ {
                "name" : "PreferenceClassNm",
                "type" : "string"
              }, {
                "name" : "PreferenceCd",
                "type" : "string"
              }, {
                "name" : "PreferenceTypeCd",
                "type" : "string"
              }, {
                "name" : "PreferenceTypeDsc",
                "type" : "string"
              }, {
                "name" : "PreferenceVal",
                "type" : "string"
              }, {
                "name" : "PreferenceCategoryCd",
                "type" : "string"
              }, {
                "name" : "PreferenceSubCategoryCd",
                "type" : "string"
              }, {
                "name" : "OptChoice",
                "type" : {
                  "type" : "array",
                  "items" : {
                    "type" : "record",
                    "name" : "OptChoice",
                    "fields" : [ {
                      "name" : "ChoiceDsc",
                      "type" : "string"
                    }, {
                      "name" : "OptChoiceInd",
                      "type" : "string"
                    }, {
                      "name" : "ReasonCd",
                      "type" : "string"
                    }, {
                      "name" : "ReasonDsc",
                      "type" : "string"
                    }, {
                      "name" : "SelectTs",
                      "type" : "string"
                    } ]
                  }
                }
              }, {
                "name" : "BannerCd",
                "type" : "string"
              }, {
                "name" : "PreferredInd",
                "type" : "long"
              }, {
                "name" : "ChannelTypCd",
                "type" : "string"
              }, {
                "name" : "PlatformTypCd",
                "type" : "string"
              }, {
                "name" : "PreferenceEffectivePeriod",
                "type" : {
                  "type" : "array",
                  "items" : {
                    "type" : "record",
                    "name" : "EffetiveDateTimeType",
                    "fields" : [ {
                      "name" : "FirstEffectiveDt",
                      "type" : "string"
                    }, {
                      "name" : "FirstEffectiveTm",
                      "type" : "string"
                    }, {
                      "name" : "LastEffectiveDt",
                      "type" : "string"
                    }, {
                      "name" : "LastEffectiveTm",
                      "type" : "string"
                    }, {
                      "name" : "DurationNbr",
                      "type" : "string"
                    }, {
                      "name" : "DurationUnitDsc",
                      "type" : "string"
                    }, {
                      "name" : "InclusiveInd",
                      "type" : "string"
                    } ]
                  }
                }
              } ]
            }
          }
        }, {
          "name" : "CustomerSubscriptions",
          "type" : {
            "type" : "array",
            "items" : {
              "type" : "record",
              "name" : "CustomerSubscriptions",
              "fields" : [ {
                "name" : "SubscriptionId",
                "type" : "string"
              }, {
                "name" : "SubscriptionCd",
                "type" : "string"
              }, {
                "name" : "SubscriptionTypeCd",
                "type" : "string"
              }, {
                "name" : "SubscriptionDsc",
                "type" : "string"
              }, {
                "name" : "OptChoice",
                "type" : "OptChoice"
              }, {
                "name" : "DeliverySubscription",
                "type" : {
                  "type" : "record",
                  "name" : "DeliverySubscription",
                  "fields" : [ {
                    "name" : "ServiceFeeWaivedInd",
                    "type" : "string"
                  }, {
                    "name" : "DeliveryFeeWaivedInd",
                    "type" : "string"
                  }, {
                    "name" : "FuelSurchargeWaivedInd",
                    "type" : "string"
                  }, {
                    "name" : "MinimumBasketSizeQty",
                    "type" : "int"
                  }, {
                    "name" : "AutoRenewInd",
                    "type" : "string"
                  }, {
                    "name" : "AutoEnrollInd",
                    "type" : "string"
                  }, {
                    "name" : "Fee",
                    "type" : {
                      "type" : "record",
                      "name" : "Fee",
                      "fields" : [ ]
                    }
                  }, {
                    "name" : "InitialOrderAmountOff",
                    "type" : {
                      "type" : "record",
                      "name" : "InitialOrderAmountOff",
                      "fields" : [ ]
                    }
                  }, {
                    "name" : "SignupFee",
                    "type" : {
                      "type" : "record",
                      "name" : "SignupFee",
                      "fields" : [ ]
                    }
                  } ]
                }
              }, {
                "name" : "SubscriptionEffectivePeriod",
                "type" : {
                  "type" : "array",
                  "items" : {
                    "type" : "record",
                    "name" : "SubscriptionEffectivePeriod",
                    "fields" : [ {
                      "name" : "FirstEffectiveDt",
                      "type" : "string"
                    }, {
                      "name" : "FirstEffectiveTm",
                      "type" : "string"
                    }, {
                      "name" : "LastEffectiveDt",
                      "type" : "string"
                    }, {
                      "name" : "LastEffectiveTm",
                      "type" : "string"
                    }, {
                      "name" : "DurationNbr",
                      "type" : "string"
                    }, {
                      "name" : "DurationUnitDsc",
                      "type" : "string"
                    }, {
                      "name" : "InclusiveInd",
                      "type" : "string"
                    } ]
                  }
                }
              } ]
            }
          }
        }, {
          "name" : "SourceAuditData",
          "type" : {
            "type" : "record",
            "name" : "SourceAuditType",
            "fields" : [ {
              "name" : "SourceNm",
              "type" : "string"
            }, {
              "name" : "CreateTs",
              "type" : "string"
            }, {
              "name" : "LastUpdateTs",
              "type" : "string"
            }, {
              "name" : "CreateClientId",
              "type" : "string"
            }, {
              "name" : "CreateUserId",
              "type" : "string"
            }, {
              "name" : "LastUpdateClientId",
              "type" : "string"
            }, {
              "name" : "LastUpdateUserId",
              "type" : "string"
            }, {
              "name" : "CreateHostNm",
              "type" : "string"
            }, {
              "name" : "LastUpdateHostNm",
              "type" : "string"
            }, {
              "name" : "SequenceNbr",
              "type" : "long"
            }, {
              "name" : "AggregateTs",
              "type" : "string"
            }, {
              "name" : "AggregateRevisionNbr",
              "type" : "long"
            }, {
              "name" : "PayloadVersionNbr",
              "type" : "long"
            }, {
              "name" : "EventId",
              "type" : "string"
            } ]
          }
        } ]
      }
    }
  } ]
}

```
## XmlSchemaTree
```
{
  "annotations": {
    "APPLICATION": {
      "application": {
        "name": "ESED_CustomerPreferences_UCA_IH_Publisher",
        "project": "CustomerPreferences",
        "brokerSchema": "com.abs.uca.cfms",
        "flow": "ESED_CustomerPreferences_UCA_IH_Publisher",
        "identifier": "customer_preferences_management",
        "source": "UCA",
        "version": "2.0.2.011"
      },
      "kafkaConsumer": {
        "topicName": "IAUC_C02_QA_CFMS_AGGREGATE"
      },
      "kafkaProducer": {
        "topicName": "ESED_C01_CustomerPreferencesManagement"
      },
      "transformer": {
        "name": "ESED_CFMS_CMM_Transformer"
      },
      "audit": {
        "inputMessageId": "InputRoot.JSON.Data.aggregateId"
      }
    },
    "SOURCE_PATHS": {
      "_id": "string",
      "_class": "string",
      "aggregateId": "string",
      "aggregateType": "string",
      "snapshot": "object",
      "snapshot/_class": "string",
      "snapshot/createTimestamp": "number",
      "snapshot/lastUpdateTimestamp": "number",
      "snapshot/createClientId": "string",
      "snapshot/lastUpdateClientId": "string",
      "snapshot/createHostName": "string",
      "snapshot/lastUpdateHostName": "string",
      "snapshot/aggregateId": "string",
      "snapshot/aggregateType": "string",
      "snapshot/sequenceNumber": "number",
      "snapshot/timestamp": "number",
      "snapshot/aggregateRevision": "number",
      "snapshot/version": "number",
      "snapshot/eventId": "string",
      "snapshot/guid": "string",
      "snapshot/preferences[*]": "array",
      "snapshot/preferences[*]/_class": "string",
      "snapshot/preferences[*]/preferenceId": "number",
      "snapshot/preferences[*]/value": "string",
      "snapshot/preferences[*]/type": "string",
      "snapshot/preferences[*]/categoryCode": "string",
      "snapshot/preferences[*]/subCategoryCode": "string",
      "snapshot/preferences[*]/optChoices[*]": "array",
      "snapshot/preferences[*]/optChoices[*]/_class": "string",
      "snapshot/preferences[*]/optChoices[*]/choice": "string",
      "snapshot/preferences[*]/effectiveTimePeriods[*]": "array",
      "snapshot/preferences[*]/effectiveTimePeriods[*]/_class": "string",
      "snapshot/preferences[*]/effectiveTimePeriods[*]/type": "string",
      "snapshot/preferences[*]/effectiveTimePeriods[*]/inclusiveInd": "number",
      "snapshot/preferences[*]/effectiveTimePeriods[*]/endDate": "string",
      "snapshot/preferences[*]/effectiveTimePeriods[*]/endTime": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer": "object",
      "snapshot/preferences[*]/deliverySubscriptionOffer/_class": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/serviceFeeWaived": "number",
      "snapshot/preferences[*]/deliverySubscriptionOffer/deliveryFeeWaived": "number",
      "snapshot/preferences[*]/deliverySubscriptionOffer/fuelSurchargeWaived": "number",
      "snapshot/preferences[*]/deliverySubscriptionOffer/minBasketSize": "number",
      "snapshot/preferences[*]/deliverySubscriptionOffer/fee": "object",
      "snapshot/preferences[*]/deliverySubscriptionOffer/fee/_class": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/fee/currency": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/fee/amount": "number",
      "snapshot/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff": "object",
      "snapshot/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff/_class": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff/currency": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff/amount": "number",
      "snapshot/preferences[*]/deliverySubscriptionOffer/signUpFee": "object",
      "snapshot/preferences[*]/deliverySubscriptionOffer/signUpFee/_class": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/signUpFee/currency": "string",
      "snapshot/preferences[*]/deliverySubscriptionOffer/signUpFee/amount": "number",
      "snapshot/preferences[*]/autoEnroll": "number",
      "snapshot/preferences[*]/autoRenew": "number",
      "snapshot/preferences[*]/serviceType": "string",
      "snapshot/preferences[*]/preferredInd": "number",
      "snapshot/preferences[*]/lastUpdatedTimestamp": "number",
      "snapshot/preferences[*]/bannerId": "string",
      "state": "object",
      "state/_class": "string",
      "state/createTimestamp": "number",
      "state/lastUpdateTimestamp": "number",
      "state/createClientId": "string",
      "state/lastUpdateClientId": "string",
      "state/createHostName": "string",
      "state/lastUpdateHostName": "string",
      "state/aggregateId": "string",
      "state/aggregateType": "string",
      "state/sequenceNumber": "number",
      "state/timestamp": "number",
      "state/aggregateRevision": "number",
      "state/version": "number",
      "state/eventId": "string",
      "state/guid": "string",
      "state/preferences[*]": "array",
      "state/preferences[*]/_class": "string",
      "state/preferences[*]/preferenceId": "number",
      "state/preferences[*]/value": "string",
      "state/preferences[*]/type": "string",
      "state/preferences[*]/categoryCode": "string",
      "state/preferences[*]/subCategoryCode": "string",
      "state/preferences[*]/optChoices[*]": "array",
      "state/preferences[*]/optChoices[*]/_class": "string",
      "state/preferences[*]/optChoices[*]/choice": "string",
      "state/preferences[*]/effectiveTimePeriods[*]": "array",
      "state/preferences[*]/effectiveTimePeriods[*]/_class": "string",
      "state/preferences[*]/effectiveTimePeriods[*]/type": "string",
      "state/preferences[*]/effectiveTimePeriods[*]/inclusiveInd": "number",
      "state/preferences[*]/effectiveTimePeriods[*]/endDate": "string",
      "state/preferences[*]/effectiveTimePeriods[*]/endTime": "string",
      "state/preferences[*]/effectiveTimePeriods[*]/duration": "string",
      "state/preferences[*]/deliverySubscriptionOffer": "object",
      "state/preferences[*]/deliverySubscriptionOffer/_class": "string",
      "state/preferences[*]/deliverySubscriptionOffer/serviceFeeWaived": "number",
      "state/preferences[*]/deliverySubscriptionOffer/deliveryFeeWaived": "number",
      "state/preferences[*]/deliverySubscriptionOffer/fuelSurchargeWaived": "number",
      "state/preferences[*]/deliverySubscriptionOffer/minBasketSize": "number",
      "state/preferences[*]/deliverySubscriptionOffer/fee": "object",
      "state/preferences[*]/deliverySubscriptionOffer/fee/_class": "string",
      "state/preferences[*]/deliverySubscriptionOffer/fee/currency": "string",
      "state/preferences[*]/deliverySubscriptionOffer/fee/amount": "number",
      "state/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff": "object",
      "state/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff/_class": "string",
      "state/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff/currency": "string",
      "state/preferences[*]/deliverySubscriptionOffer/initialOrderAmountOff/amount": "number",
      "state/preferences[*]/deliverySubscriptionOffer/signUpFee": "object",
      "state/preferences[*]/deliverySubscriptionOffer/signUpFee/_class": "string",
      "state/preferences[*]/deliverySubscriptionOffer/signUpFee/currency": "string",
      "state/preferences[*]/deliverySubscriptionOffer/signUpFee/amount": "number",
      "state/preferences[*]/autoEnroll": "number",
      "state/preferences[*]/autoRenew": "number",
      "state/preferences[*]/serviceType": "string",
      "state/preferences[*]/preferredInd": "number",
      "state/preferences[*]/lastUpdatedTimestamp": "number",
      "state/preferences[*]/bannerId": "string"
    },
    "UNKNOWN_MAPPINGS": [
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/optchoices[*]/choice"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/optchoices[*]/reasonCode"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/optchoices[*]/reasonText"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/channel/type"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/platform/type"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startDate"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startTime"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/optchoices[*]/choice"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/optchoices[*]/reasonCode"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/optchoices[*]/reasonText"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences/deliverySubscriptionOffer/fee/currency"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences/deliverySubscriptionOffer/fee/amount"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences/deliverySubscriptionOffer/initialOrderAmountOff/currency"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startDate"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startTime"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/createUserId"
      },
      {
        "unknownType": "UNKNOWN_SOURCE_PATH",
        "targetPath": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId",
        "mappingRule": "Direct Mapping",
        "sourcePath": "state/lastUpdateUserId"
      }
    ],
    "SOURCE_MAPPING": {
      "aggregateId": [
        "GetCustomerPreferences/DocumentData/Document/DocumentID",
        "GetCustomerPreferences/CustomerPreferencesData/CustomerId"
      ],
      "state": {
        "preferences[*]": {
          "_class": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm",
          "preferenceId": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd",
          "value": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal",
          "categoryCode": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd",
          "optChoices[*]": {
            "choice": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc"
            ]
          },
          "optchoices[*]": {
            "reasonCode": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd"
            ],
            "reasonText": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc"
            ]
          },
          "bannerId": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd",
          "preferredInd": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd",
          "channel": {
            "type": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd"
          },
          "platform": {
            "type": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd"
          },
          "effectiveTimePeriods[*]": {
            "type": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode"
            ],
            "startDate": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt"
            ],
            "startTime": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm"
            ],
            "endDate": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt"
            ],
            "endTime": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm"
            ],
            "duration": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr"
            ],
            "inclusiveInd": [
              "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd",
              "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd"
            ]
          },
          "subCategoryCode": [
            "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd",
            "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd"
          ],
          "type": [
            "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd",
            "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd"
          ],
          "deliverySubscriptionOffer": {
            "serviceFeeWaived": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd",
            "deliveryFeeWaived": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd",
            "fuelSurchargeWaived": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd",
            "minBasketSize": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty"
          },
          "autoRenew": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd",
          "autoEnroll": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd"
        },
        "preferences": {
          "value": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId",
          "deliverySubscriptionOffer": {
            "fee": {
              "currency": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd",
              "amount": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt"
            },
            "initialOrderAmountOff": {
              "currency": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd"
            }
          }
        },
        "createTimestamp": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs",
        "lastUpdateTimestamp": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs",
        "createClientId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId",
        "createUserId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId",
        "lastUpdateClientId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId",
        "lastUpdateUserId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId",
        "createHostName": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm",
        "lastUpdateHostName": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm",
        "sequenceNumber": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr",
        "timestamp": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs",
        "aggregateRevision": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr",
        "version": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr",
        "eventId": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId"
      },
      "aggregateType": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm"
    },
    "GLOBAL_VARIABLE": [
      {
        "name": "VERSION_ID",
        "type": "CHARACTER",
        "defaultValue": "\u00272.0.2.011\u0027"
      },
      {
        "name": "SYSTEM_ENVIRONMENT_CODE",
        "type": "CHARACTER",
        "defaultValue": "\u0027PROD\u0027"
      }
    ]
  },
  "mappings": {
    "GetCustomerPreferences": {
      "path": "GetCustomerPreferences",
      "name": "GetCustomerPreferences",
      "dataType": "complexType",
      "namespaceURI": "http://www.openapplications.org/oagis/9",
      "level": 1,
      "nodeType": "Folder",
      "alias": "xmlDocRoot",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData": {
      "path": "GetCustomerPreferences/DocumentData",
      "name": "DocumentData",
      "dataType": "complexType",
      "cardinality": "1-n",
      "namespaceURI": "http://www.openapplications.org/oagis/9",
      "level": 2,
      "nodeType": "Folder",
      "alias": "DocumentData_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData/Document": {
      "path": "GetCustomerPreferences/DocumentData/Document",
      "name": "Document",
      "dataType": "complexType",
      "cardinality": "1-1",
      "namespaceURI": "http://www.openapplications.org/oagis/9",
      "level": 3,
      "nodeType": "Folder",
      "alias": "Document_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData/Document/@ReleaseId": {
      "path": "GetCustomerPreferences/DocumentData/Document/@ReleaseId",
      "name": "ReleaseId",
      "namespaceURI": "",
      "level": 4,
      "nodeType": "Attribute",
      "defaultValue": "ReleaseId",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/@VersionId": {
      "path": "GetCustomerPreferences/DocumentData/Document/@VersionId",
      "name": "VersionId",
      "namespaceURI": "",
      "level": 4,
      "nodeType": "Attribute",
      "defaultValue": "VersionId",
      "annotations": {
        "mapping": {
          "assignment": "VERSION_ID"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd": {
      "path": "GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd",
      "name": "SystemEnvironmentCd",
      "namespaceURI": "",
      "level": 4,
      "nodeType": "Attribute",
      "defaultValue": "SystemEnvironmentCd",
      "annotations": {
        "mapping": {
          "mappingRule": "Depending upon Environment where code is deployed map it as DEV, QA, PROD",
          "assignment": "SYSTEM_ENVIRONMENT_CODE"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DocumentID": {
      "path": "GetCustomerPreferences/DocumentData/Document/DocumentID",
      "name": "DocumentID",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "DocumentID_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "It has to be populated from Kafka as of now same as Kafka Topic",
          "sourcePath": "aggregateid",
          "assignment": "\u0027CUSTOMER_PREFERENCES_MANAGEMENT\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/AlternateDocumentID": {
      "path": "GetCustomerPreferences/DocumentData/Document/AlternateDocumentID",
      "name": "AlternateDocumentID",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "AlternateDocumentID_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Map \u0027FileName_TimeStamp\u0027 where  Timestamp format - YYYYMMddHHmmssSSSSSS",
          "assignment": "\u0027IAUC_C02.cfms.aggregate-\u0027 || CAST(CURRENT_TIMESTAMP AS CHARACTER FORMAT \u0027YYYYMMddHHmmssSSSSSS\u0027)"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/InboundOutboundInd": {
      "path": "GetCustomerPreferences/DocumentData/Document/InboundOutboundInd",
      "name": "InboundOutboundInd",
      "dataType": "string",
      "restriction": {
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "InboundOutboundInd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "assignment": "\u0027Outbound from Albertsons\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DocumentNm": {
      "path": "GetCustomerPreferences/DocumentData/Document/DocumentNm",
      "name": "DocumentNm",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "DocumentNm_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027GetCustomerPreference\u0027",
          "assignment": "\u0027GetCustomerPreferences\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/CreationDt": {
      "path": "GetCustomerPreferences/DocumentData/Document/CreationDt",
      "name": "CreationDt",
      "dataType": "dateTime",
      "cardinality": "1-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "CreationDt_",
      "defaultValue": "2008-09-28T18:49:45",
      "annotations": {
        "mapping": {
          "mappingRule": "Get current date and time",
          "assignment": "CURRENT_TIMESTAMP"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/Description": {
      "path": "GetCustomerPreferences/DocumentData/Document/Description",
      "name": "Description",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-5",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "Description_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027Retail customer’s generic preferences and the subscriptions\u0027",
          "assignment": "\u0027Retail customer\u0027\u0027s generic preferences and the subscriptions\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/SourceApplicationCd": {
      "path": "GetCustomerPreferences/DocumentData/Document/SourceApplicationCd",
      "name": "SourceApplicationCd",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SourceApplicationCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027CFMS\u0027",
          "assignment": "\u0027CFMS\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/TargetApplicationCd": {
      "path": "GetCustomerPreferences/DocumentData/Document/TargetApplicationCd",
      "name": "TargetApplicationCd",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "TargetApplicationCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027EDIS\u0027",
          "assignment": "\u0027EDIS\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/Note": {
      "path": "GetCustomerPreferences/DocumentData/Document/Note",
      "name": "Note",
      "dataType": "string",
      "restriction": {
        "maxLength": "200",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "Note_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/GatewayNm": {
      "path": "GetCustomerPreferences/DocumentData/Document/GatewayNm",
      "name": "GatewayNm",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "GatewayNm_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/SenderId": {
      "path": "GetCustomerPreferences/DocumentData/Document/SenderId",
      "name": "SenderId",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SenderId_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/ReceiverId": {
      "path": "GetCustomerPreferences/DocumentData/Document/ReceiverId",
      "name": "ReceiverId",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "ReceiverId_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/RoutingSystemNm": {
      "path": "GetCustomerPreferences/DocumentData/Document/RoutingSystemNm",
      "name": "RoutingSystemNm",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "RoutingSystemNm_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd": {
      "path": "GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd",
      "name": "InternalFileTransferInd",
      "dataType": "string",
      "restriction": {
        "maxLength": "5",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "InternalFileTransferInd_",
      "defaultValue": "strin",
      "annotations": {
        "mapping": {
          "mappingRule": "Defaul to \u0027Y\u0027",
          "assignment": "\u0027Y\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/InterchangeDate": {
      "path": "GetCustomerPreferences/DocumentData/Document/InterchangeDate",
      "name": "InterchangeDate",
      "dataType": "date",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "InterchangeDate_",
      "defaultValue": "2006-08-19-07:00",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/InterchangeTime": {
      "path": "GetCustomerPreferences/DocumentData/Document/InterchangeTime",
      "name": "InterchangeTime",
      "dataType": "time",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "InterchangeTime_",
      "defaultValue": "17:18:37-07:00",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/ExternalTargetInd": {
      "path": "GetCustomerPreferences/DocumentData/Document/ExternalTargetInd",
      "name": "ExternalTargetInd",
      "dataType": "string",
      "restriction": {
        "maxLength": "1",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "ExternalTargetInd_",
      "defaultValue": "s",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/MessageSequenceNbr": {
      "path": "GetCustomerPreferences/DocumentData/Document/MessageSequenceNbr",
      "name": "MessageSequenceNbr",
      "dataType": "integer",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "MessageSequenceNbr_",
      "defaultValue": "100",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/ExpectedMessageCnt": {
      "path": "GetCustomerPreferences/DocumentData/Document/ExpectedMessageCnt",
      "name": "ExpectedMessageCnt",
      "dataType": "integer",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "ExpectedMessageCnt_",
      "defaultValue": "100",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification",
      "name": "DataClassification",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Folder",
      "alias": "DataClassification_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel",
      "name": "DataClassificationLevel",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Folder",
      "alias": "DataClassificationLevel_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code",
      "name": "Code",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 6,
      "nodeType": "Field",
      "alias": "Code_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027Internal\u0027",
          "assignment": "\u0027Internal\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Description": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Description",
      "name": "Description",
      "dataType": "string",
      "restriction": {
        "maxLength": "250",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 6,
      "nodeType": "Field",
      "alias": "Description_1",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/ShortDescription": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/ShortDescription",
      "name": "ShortDescription",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 6,
      "nodeType": "Field",
      "alias": "ShortDescription_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel",
      "name": "BusinessSensitivityLevel",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Folder",
      "alias": "BusinessSensitivityLevel_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code",
      "name": "Code",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 6,
      "nodeType": "Field",
      "alias": "Code_1",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027Low\u0027",
          "assignment": "\u0027Low\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Description": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Description",
      "name": "Description",
      "dataType": "string",
      "restriction": {
        "maxLength": "250",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 6,
      "nodeType": "Field",
      "alias": "Description_2",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/ShortDescription": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/ShortDescription",
      "name": "ShortDescription",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 6,
      "nodeType": "Field",
      "alias": "ShortDescription_1",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd",
      "name": "PHIdataInd",
      "dataType": "string",
      "restriction": {
        "minLength": "1",
        "maxLength": "5",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "PHIdataInd_",
      "defaultValue": "strin",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027N\u0027",
          "assignment": "\u0027N\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd",
      "name": "PCIdataInd",
      "dataType": "string",
      "restriction": {
        "minLength": "1",
        "maxLength": "5",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "PCIdataInd_",
      "defaultValue": "strin",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027N\u0027",
          "assignment": "\u0027N\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd": {
      "path": "GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd",
      "name": "PIIdataInd",
      "dataType": "string",
      "restriction": {
        "minLength": "1",
        "maxLength": "5",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "PIIdataInd_",
      "defaultValue": "strin",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027N\u0027",
          "assignment": "\u0027N\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/DocumentAction": {
      "path": "GetCustomerPreferences/DocumentData/DocumentAction",
      "name": "DocumentAction",
      "dataType": "complexType",
      "cardinality": "1-1",
      "namespaceURI": "http://www.openapplications.org/oagis/9",
      "level": 3,
      "nodeType": "Folder",
      "alias": "DocumentAction_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd": {
      "path": "GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd",
      "name": "ActionTypeCd",
      "dataType": "string",
      "cardinality": "1-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "ActionTypeCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027UPDATE\u0027",
          "assignment": "\u0027UPDATE\u0027"
        }
      }
    },
    "GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd": {
      "path": "GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd",
      "name": "RecordTypeCd",
      "dataType": "string",
      "cardinality": "1-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "RecordTypeCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Default to \u0027CHANGE\u0027",
          "assignment": "\u0027CHANGE\u0027"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData": {
      "path": "GetCustomerPreferences/CustomerPreferencesData",
      "name": "CustomerPreferencesData",
      "dataType": "complexType",
      "cardinality": "1-n",
      "namespaceURI": "http://www.openapplications.org/oagis/9",
      "level": 2,
      "nodeType": "Folder",
      "alias": "CustomerPreferencesData_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerId",
      "name": "CustomerId",
      "dataType": "complexType",
      "cardinality": "1-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 3,
      "nodeType": "Folder",
      "alias": "CustomerId_",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping\nNote: For all services except CHMS this will be mapped from the service aggregateId. For CHMS the aggregateId will be mapped to HHID",
          "sourcePath": "aggregateId",
          "assignment": "$.aggregateId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId",
      "name": "CustomerAlternateId",
      "dataType": "complexType",
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 3,
      "nodeType": "Folder",
      "alias": "CustomerAlternateId_",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@CodeType": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@CodeType",
      "name": "CodeType",
      "namespaceURI": "",
      "level": 4,
      "nodeType": "Attribute",
      "defaultValue": "CodeType",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@sequenceNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@sequenceNbr",
      "name": "sequenceNbr",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Attribute",
      "defaultValue": "sequenceNbr",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdTxt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdTxt",
      "name": "AlternateIdTxt",
      "dataType": "string",
      "restriction": {
        "maxLength": "30",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "AlternateIdTxt_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdNbr",
      "name": "AlternateIdNbr",
      "dataType": "decimal",
      "restriction": {
        "totalDigits": "24",
        "fractionDigits": "0",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "AlternateIdNbr_",
      "defaultValue": "1000",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences",
      "name": "CustomerPreferences",
      "dataType": "complexType",
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 3,
      "nodeType": "Folder",
      "alias": "CustomerPreferences_",
      "annotations": {
        "mapped": true,
        "construct": {
          "constructors": [],
          "loops": [
            {
              "name": "LOOP_STATE_PREFERENCES",
              "variable": "_state_preference",
              "sourcePath": "$.state.preferences[*]"
            }
          ]
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm",
      "name": "PreferenceClassNm",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceClassNm_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/_class",
          "assignment": "_state_preference._class"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd",
      "name": "PreferenceCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/preferenceId",
          "assignment": "_state_preference.preferenceId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd",
      "name": "PreferenceTypeCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceTypeCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/type",
          "assignment": "_state_preference.type"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeDsc",
      "name": "PreferenceTypeDsc",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceTypeDsc_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal",
      "name": "PreferenceVal",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceVal_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/value",
          "assignment": "_state_preference.value"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd",
      "name": "PreferenceCategoryCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceCategoryCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/categoryCode",
          "assignment": "_state_preference.categoryCode"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd",
      "name": "PreferenceSubCategoryCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferenceSubCategoryCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/subCategoryCode",
          "assignment": "_state_preference.subCategoryCode"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice",
      "name": "OptChoice",
      "dataType": "complexType",
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Folder",
      "alias": "OptChoice_",
      "annotations": {
        "mapped": true,
        "construct": {
          "constructors": [],
          "loops": [
            {
              "name": "LOOP_OPT_CHOICES",
              "variable": "_opt_choice",
              "sourcePath": "_state_preference.optChoices[*]"
            }
          ]
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc",
      "name": "ChoiceDsc",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ChoiceDsc_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/optchoices[*]/choice",
          "assignment": "_opt_choice.choice"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/OptChoiceInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/OptChoiceInd",
      "name": "OptChoiceInd",
      "dataType": "string",
      "restriction": {
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "OptChoiceInd_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd",
      "name": "ReasonCd",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ReasonCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/optchoices[*]/reasonCode",
          "assignment": "_opt_choice.reasonCode"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc",
      "name": "ReasonDsc",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ReasonDsc_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/optchoices[*]/reasonText",
          "assignment": "_opt_choice.reasonText"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/SelectTs": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/SelectTs",
      "name": "SelectTs",
      "dataType": "dateTime",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "SelectTs_",
      "defaultValue": "2009-10-14T00:16:36",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd",
      "name": "BannerCd",
      "dataType": "string",
      "restriction": {
        "maxLength": "30",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "BannerCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/bannerId",
          "assignment": "_state_preference.bannerId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd",
      "name": "PreferredInd",
      "dataType": "integer",
      "restriction": {
        "totalDigits": "1",
        "fractionDigits": "0",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PreferredInd_",
      "defaultValue": "100",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/preferredInd",
          "assignment": "_state_preference.preferredInd"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd",
      "name": "ChannelTypCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "ChannelTypCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/channel/type",
          "assignment": "_state_preference.channel.type"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd",
      "name": "PlatformTypCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PlatformTypCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/platform/type",
          "assignment": "_state_preference.platform.type"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod",
      "name": "PreferenceEffectivePeriod",
      "dataType": "complexType",
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Folder",
      "alias": "PreferenceEffectivePeriod_",
      "annotations": {
        "mapped": true,
        "construct": {
          "constructors": [],
          "loops": [
            {
              "name": "LOOP_PREF_EFF_TIME_PERIODS",
              "variable": "_pref_eff_time_period",
              "sourcePath": "_state_preference.effectiveTimePeriods[*]"
            }
          ]
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode",
      "name": "typeCode",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Attribute",
      "defaultValue": "typeCode",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/type"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt",
      "name": "FirstEffectiveDt",
      "dataType": "date",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "FirstEffectiveDt_",
      "defaultValue": "2018-12-28-08:00",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startDate",
          "assignment": "_pref_eff_time_period.startDate"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm",
      "name": "FirstEffectiveTm",
      "dataType": "time",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "FirstEffectiveTm_",
      "defaultValue": "11:33:25-07:00",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startTime",
          "assignment": "_pref_eff_time_period.startTime"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt",
      "name": "LastEffectiveDt",
      "dataType": "date",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "LastEffectiveDt_",
      "defaultValue": "2000-09-24",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/endDate",
          "assignment": "_pref_eff_time_period.endDate"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm",
      "name": "LastEffectiveTm",
      "dataType": "time",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "LastEffectiveTm_",
      "defaultValue": "18:04:37",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/endTime",
          "assignment": "_pref_eff_time_period.endTime"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr",
      "name": "DurationNbr",
      "dataType": "string",
      "restriction": {
        "maxLength": "10",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "DurationNbr_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Extract the number part from (state.preferences[*].effectiveTimePeriods[*].duration)",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/duration",
          "assignment": "_pref_eff_time_period.duration"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationUnitDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationUnitDsc",
      "name": "DurationUnitDsc",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "DurationUnitDsc_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "IF state.preferences[*].effectiveTimePeriods[*].duration ends with \u0027D\u0027 then \u0027Days\u0027\nIF  state.preferences[*].effectiveTimePeriods[*].duration ends with W then \u0027Weeks\u0027",
          "assignment": "_pref_eff_time_period.duration"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd",
      "name": "InclusiveInd",
      "dataType": "string",
      "restriction": {
        "maxLength": "2",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "InclusiveInd_",
      "defaultValue": "st",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/inclusiveInd",
          "assignment": "_pref_eff_time_period.inclusiveInd"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions",
      "name": "CustomerSubscriptions",
      "dataType": "complexType",
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 3,
      "nodeType": "Folder",
      "alias": "CustomerSubscriptions_",
      "annotations": {
        "mapped": true,
        "construct": {
          "constructors": [],
          "loops": [
            {
              "name": "LOOP_CUSTOMER_SUBSCRIPTION",
              "variable": "_preference",
              "sourcePath": "$.state.preferences[*]"
            }
          ]
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId",
      "name": "SubscriptionId",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SubscriptionId_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "preferences[].value\nWhen preferences[].categoryCode \u003d \"DELIVERY\" and preferences[].subCategoryCode \u003d  \"SUBSCRIPTION\" ",
          "sourcePath": "state/preferences/value",
          "assignment": "_preference.value"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd",
      "name": "SubscriptionCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SubscriptionCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/subCategoryCode",
          "assignment": "_preference.subCategoryCode"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd",
      "name": "SubscriptionTypeCd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SubscriptionTypeCd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/type",
          "assignment": "_preference.type"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionDsc",
      "name": "SubscriptionDsc",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SubscriptionDsc_",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice",
      "name": "OptChoice",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Folder",
      "alias": "OptChoice_1",
      "annotations": {
        "mapped": true,
        "construct": {
          "constructors": [],
          "loops": [
            {
              "name": "LOOP_SUBSCRIPTION_CHOICES",
              "variable": "_choice",
              "sourcePath": "_preference.optChoices[*]"
            }
          ]
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc",
      "name": "ChoiceDsc",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ChoiceDsc_1",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/optchoices[*]/choice",
          "assignment": "_choice.choice"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/OptChoiceInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/OptChoiceInd",
      "name": "OptChoiceInd",
      "dataType": "string",
      "restriction": {
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "OptChoiceInd_1",
      "defaultValue": "string",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd",
      "name": "ReasonCd",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ReasonCd_1",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/optchoices[*]/reasonCode",
          "assignment": "_choice.reasonCode"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc",
      "name": "ReasonDsc",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ReasonDsc_1",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/optchoices[*]/reasonText",
          "assignment": "_choice.reasonText"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/SelectTs": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/SelectTs",
      "name": "SelectTs",
      "dataType": "dateTime",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "SelectTs_1",
      "defaultValue": "2017-08-18T13:01:40",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription",
      "name": "DeliverySubscription",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Folder",
      "alias": "DeliverySubscription_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd",
      "name": "ServiceFeeWaivedInd",
      "dataType": "string",
      "restriction": {
        "length": "1",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "ServiceFeeWaivedInd_",
      "defaultValue": "s",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/deliverySubscriptionOffer/serviceFeeWaived",
          "assignment": "_preference.deliverySubscriptionOffer.serviceFeeWaived"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd",
      "name": "DeliveryFeeWaivedInd",
      "dataType": "string",
      "restriction": {
        "length": "1",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "DeliveryFeeWaivedInd_",
      "defaultValue": "s",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/deliverySubscriptionOffer/deliveryFeeWaived",
          "assignment": "_preference.deliverySubscriptionOffer.deliveryFeeWaived"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd",
      "name": "FuelSurchargeWaivedInd",
      "dataType": "string",
      "restriction": {
        "length": "1",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "FuelSurchargeWaivedInd_",
      "defaultValue": "s",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/deliverySubscriptionOffer/fuelSurchargeWaived",
          "assignment": "_preference.deliverySubscriptionOffer.fuelSurchargeWaived"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty",
      "name": "MinimumBasketSizeQty",
      "dataType": "int",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "MinimumBasketSizeQty_",
      "defaultValue": "3",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/deliverySubscriptionOffer/minBasketSize",
          "assignment": "_preference.deliverySubscriptionOffer.minBasketSize"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd",
      "name": "AutoRenewInd",
      "dataType": "string",
      "restriction": {
        "length": "1",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "AutoRenewInd_",
      "defaultValue": "s",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/autoRenew",
          "assignment": "_preference.autoRenew"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd",
      "name": "AutoEnrollInd",
      "dataType": "string",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "AutoEnrollInd_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/autoEnroll",
          "assignment": "_preference.autoEnroll"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee",
      "name": "Fee",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Folder",
      "alias": "Fee_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd",
      "name": "CurrencyCd",
      "namespaceURI": "",
      "level": 6,
      "nodeType": "Attribute",
      "defaultValue": "CurrencyCd",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences/deliverySubscriptionOffer/fee/currency",
          "assignment": "_preference.deliverySubscriptionOffer.fee.currency"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt",
      "name": "FeeAmt",
      "namespaceURI": "",
      "level": 6,
      "nodeType": "Attribute",
      "defaultValue": "FeeAmt",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences/deliverySubscriptionOffer/fee/amount",
          "assignment": "_preference.deliverySubscriptionOffer.fee.amount"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff",
      "name": "InitialOrderAmountOff",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Folder",
      "alias": "InitialOrderAmountOff_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd",
      "name": "CurrencyCd",
      "namespaceURI": "",
      "level": 6,
      "nodeType": "Attribute",
      "defaultValue": "CurrencyCd",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences/deliverySubscriptionOffer/initialOrderAmountOff/currency",
          "assignment": "_preference.deliverySubscriptionOffer.initialOrderAmountOff.currency"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@DiscountAmt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@DiscountAmt",
      "name": "DiscountAmt",
      "namespaceURI": "",
      "level": 6,
      "nodeType": "Attribute",
      "defaultValue": "DiscountAmt",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee",
      "name": "SignupFee",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Folder",
      "alias": "SignupFee_",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@CurrencyCd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@CurrencyCd",
      "name": "CurrencyCd",
      "namespaceURI": "",
      "level": 6,
      "nodeType": "Attribute",
      "defaultValue": "CurrencyCd",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@FeeAmt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@FeeAmt",
      "name": "FeeAmt",
      "namespaceURI": "",
      "level": 6,
      "nodeType": "Attribute",
      "defaultValue": "FeeAmt",
      "annotations": {}
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod",
      "name": "SubscriptionEffectivePeriod",
      "dataType": "complexType",
      "cardinality": "0-n",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Folder",
      "alias": "SubscriptionEffectivePeriod_",
      "annotations": {
        "mapped": true,
        "construct": {
          "constructors": [],
          "loops": [
            {
              "name": "LOOP_SUBSCRIPTION_EFF_TIME_PERIODS",
              "variable": "_period",
              "sourcePath": "_preference/effectiveTimePeriods[*]"
            }
          ]
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode",
      "name": "typeCode",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Attribute",
      "defaultValue": "typeCode",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/type",
          "assignment": "_period.type"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt",
      "name": "FirstEffectiveDt",
      "dataType": "date",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "FirstEffectiveDt_1",
      "defaultValue": "2005-01-21-08:00",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startDate",
          "assignment": "_period.startDate"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm",
      "name": "FirstEffectiveTm",
      "dataType": "time",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "FirstEffectiveTm_1",
      "defaultValue": "12:00:00",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startTime",
          "assignment": "_period.startTime"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt",
      "name": "LastEffectiveDt",
      "dataType": "date",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "LastEffectiveDt_1",
      "defaultValue": "2014-12-08",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/endDate",
          "assignment": "_period.endDate"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm",
      "name": "LastEffectiveTm",
      "dataType": "time",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "LastEffectiveTm_1",
      "defaultValue": "11:36:55",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/endTime",
          "assignment": "_period.endTime"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr",
      "name": "DurationNbr",
      "dataType": "string",
      "restriction": {
        "maxLength": "10",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "DurationNbr_1",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Extract the number part from (state.preferences[*].effectiveTimePeriods[*].duration)",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/duration",
          "assignment": "_period.duration"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationUnitDsc": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationUnitDsc",
      "name": "DurationUnitDsc",
      "dataType": "string",
      "restriction": {
        "maxLength": "20",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "DurationUnitDsc_1",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "IF state.preferences[*].effectiveTimePeriods[*].duration ends with \u0027D\u0027 then \u0027Days\u0027\nIF  state.preferences[*].effectiveTimePeriods[*].duration ends with W then \u0027Weeks\u0027",
          "assignment": "_period.duration"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd",
      "name": "InclusiveInd",
      "dataType": "string",
      "restriction": {
        "maxLength": "2",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 5,
      "nodeType": "Field",
      "alias": "InclusiveInd_1",
      "defaultValue": "st",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/inclusiveInd",
          "assignment": "_period.inclusiveInd"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData",
      "name": "SourceAuditData",
      "dataType": "complexType",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 3,
      "nodeType": "Folder",
      "alias": "SourceAuditData_",
      "annotations": {
        "mapped": true
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm",
      "name": "SourceNm",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SourceNm_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "aggregateType",
          "assignment": "$.aggregateType"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs",
      "name": "CreateTs",
      "dataType": "dateTime",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "CreateTs_",
      "defaultValue": "2017-10-14T05:43:21",
      "annotations": {
        "mapping": {
          "mappingRule": "Transform to YYYY-MM-DD-THH:MM:SSZ \nExample 2020-10-06T07:17:58.791Z",
          "sourcePath": "state/createTimestamp",
          "assignment": "$.state.createTimestamp"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs",
      "name": "LastUpdateTs",
      "dataType": "dateTime",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "LastUpdateTs_",
      "defaultValue": "2009-07-28T12:14:45",
      "annotations": {
        "mapping": {
          "mappingRule": "Transform to YYYY-MM-DD-THH:MM:SSZ \nExample 2020-10-06T07:17:58.791Z",
          "sourcePath": "state/lastUpdateTimestamp",
          "assignment": "$.state.lastUpdateTimestamp"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId",
      "name": "CreateClientId",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "CreateClientId_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/createClientId",
          "assignment": "$.state.createClientId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId",
      "name": "CreateUserId",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "CreateUserId_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/createUserId",
          "assignment": "$.state.createUserId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId",
      "name": "LastUpdateClientId",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "LastUpdateClientId_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/lastUpdateClientId",
          "assignment": "$.state.lastUpdateClientId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId",
      "name": "LastUpdateUserId",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "LastUpdateUserId_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/lastUpdateUserId",
          "assignment": "$.state.lastUpdateUserId"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm",
      "name": "CreateHostNm",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "CreateHostNm_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/createHostName",
          "assignment": "$.state.createHostName"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm",
      "name": "LastUpdateHostNm",
      "dataType": "string",
      "restriction": {
        "maxLength": "100",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "LastUpdateHostNm_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/lastUpdateHostName",
          "assignment": "$.state.lastUpdateHostName"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr",
      "name": "SequenceNbr",
      "dataType": "integer",
      "restriction": {
        "totalDigits": "5",
        "fractionDigits": "0",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "SequenceNbr_",
      "defaultValue": "100",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/sequenceNumber",
          "assignment": "$.state.sequenceNumber"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs",
      "name": "AggregateTs",
      "dataType": "dateTime",
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "AggregateTs_",
      "defaultValue": "2006-08-04T03:16:57",
      "annotations": {
        "mapping": {
          "mappingRule": "Transform to YYYY-MM-DD-THH:MM:SSZ \nExample 2020-10-06T07:17:58.791Z",
          "sourcePath": "state/timestamp",
          "assignment": "$.state.timestamp"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr",
      "name": "AggregateRevisionNbr",
      "dataType": "integer",
      "restriction": {
        "totalDigits": "5",
        "fractionDigits": "0",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "AggregateRevisionNbr_",
      "defaultValue": "100",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/aggregateRevision",
          "assignment": "$.state.aggregateRevision"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr",
      "name": "PayloadVersionNbr",
      "dataType": "integer",
      "restriction": {
        "totalDigits": "5",
        "fractionDigits": "0",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "PayloadVersionNbr_",
      "defaultValue": "100",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/version",
          "assignment": "$.state.version"
        }
      }
    },
    "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId": {
      "path": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId",
      "name": "EventId",
      "dataType": "string",
      "restriction": {
        "maxLength": "50",
        "whiteSpace": "preserve"
      },
      "cardinality": "0-1",
      "namespaceURI": "http://collab.safeway.com/it/architecture/info/default.aspx",
      "level": 4,
      "nodeType": "Field",
      "alias": "EventId_",
      "defaultValue": "string",
      "annotations": {
        "mapping": {
          "mappingRule": "Direct Mapping",
          "sourcePath": "state/eventId",
          "assignment": "$.state.eventId"
        }
      }
    }
  }
}

```
## UnknownTargets
```
[]

```
## UnknownSources
```
[
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/optchoices[*]/choice"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/optchoices[*]/reasonCode"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/optchoices[*]/reasonText"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/channel/type"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/platform/type"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startDate"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startTime"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/optchoices[*]/choice"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/optchoices[*]/reasonCode"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/optchoices[*]/reasonText"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences/deliverySubscriptionOffer/fee/currency"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences/deliverySubscriptionOffer/fee/amount"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences/deliverySubscriptionOffer/initialOrderAmountOff/currency"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startDate"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]/startTime"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/createUserId"
  },
  {
    "unknownType": "UNKNOWN_SOURCE_PATH",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId",
    "mappingRule": "Direct Mapping",
    "sourcePath": "state/lastUpdateUserId"
  }
]

```
## UnknownOthers
```
[]

```
## XPathMappings
```
GetCustomerPreferences=
GetCustomerPreferences/DocumentData=
GetCustomerPreferences/DocumentData/Document=
# GetCustomerPreferences/DocumentData/Document/@ReleaseId=
GetCustomerPreferences/DocumentData/Document/@VersionId=TODO(???)
GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd=TODO(???)
GetCustomerPreferences/DocumentData/Document/DocumentID=TODO(???)
GetCustomerPreferences/DocumentData/Document/AlternateDocumentID=TODO(???)
GetCustomerPreferences/DocumentData/Document/InboundOutboundInd=TODO(???)
GetCustomerPreferences/DocumentData/Document/DocumentNm=DEFAULT('GetCustomerPreference')
GetCustomerPreferences/DocumentData/Document/CreationDt=TODO(???)
GetCustomerPreferences/DocumentData/Document/Description=DEFAULT('Retail customer’s generic preferences and the subscriptions')
GetCustomerPreferences/DocumentData/Document/SourceApplicationCd=DEFAULT('CFMS')
GetCustomerPreferences/DocumentData/Document/TargetApplicationCd=DEFAULT('EDIS')
# GetCustomerPreferences/DocumentData/Document/Note=
# GetCustomerPreferences/DocumentData/Document/GatewayNm=
# GetCustomerPreferences/DocumentData/Document/SenderId=
# GetCustomerPreferences/DocumentData/Document/ReceiverId=
# GetCustomerPreferences/DocumentData/Document/RoutingSystemNm=
GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd=TODO(???)
# GetCustomerPreferences/DocumentData/Document/InterchangeDate=
# GetCustomerPreferences/DocumentData/Document/InterchangeTime=
# GetCustomerPreferences/DocumentData/Document/ExternalTargetInd=
# GetCustomerPreferences/DocumentData/Document/MessageSequenceNbr=
# GetCustomerPreferences/DocumentData/Document/ExpectedMessageCnt=
GetCustomerPreferences/DocumentData/Document/DataClassification=
GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel=
GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code=DEFAULT('Internal')
# GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Description=
# GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/ShortDescription=
GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel=
GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code=DEFAULT('Low')
# GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Description=
# GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/ShortDescription=
GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd=DEFAULT('N')
GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd=DEFAULT('N')
GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd=DEFAULT('N')
GetCustomerPreferences/DocumentData/DocumentAction=
GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd=DEFAULT('UPDATE')
GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd=DEFAULT('CHANGE')
GetCustomerPreferences/CustomerPreferencesData=
GetCustomerPreferences/CustomerPreferencesData/CustomerId=FROM(aggregateId)
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@CodeType=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@sequenceNbr=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdTxt=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdNbr=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm=FROM(state/preferences[*]/_class)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd=FROM(state/preferences[*]/preferenceId)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd=FROM(state/preferences[*]/type)
# GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeDsc=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal=FROM(state/preferences[*]/value)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd=FROM(state/preferences[*]/categoryCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd=FROM(state/preferences[*]/subCategoryCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc=FROM(state/preferences[*]/optchoices[*]/choice)
# GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/OptChoiceInd=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd=FROM(state/preferences[*]/optchoices[*]/reasonCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc=FROM(state/preferences[*]/optchoices[*]/reasonText)
# GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/SelectTs=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd=FROM(state/preferences[*]/bannerId)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd=FROM(state/preferences[*]/preferredInd)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd=FROM(state/preferences[*]/channel/type)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd=FROM(state/preferences[*]/platform/type)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode=FROM(state/preferences[*]/effectiveTimePeriods[*]/type)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt=FROM(state/preferences[*]/effectiveTimePeriods[*]/startDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm=FROM(state/preferences[*]/effectiveTimePeriods[*]/startTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt=FROM(state/preferences[*]/effectiveTimePeriods[*]/endDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm=FROM(state/preferences[*]/effectiveTimePeriods[*]/endTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationUnitDsc=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd=FROM(state/preferences[*]/effectiveTimePeriods[*]/inclusiveInd)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd=FROM(state/preferences[*]/subCategoryCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd=FROM(state/preferences[*]/type)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionDsc=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc=FROM(state/preferences[*]/optchoices[*]/choice)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/OptChoiceInd=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd=FROM(state/preferences[*]/optchoices[*]/reasonCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc=FROM(state/preferences[*]/optchoices[*]/reasonText)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/SelectTs=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd=FROM(state/preferences[*]/deliverySubscriptionOffer/serviceFeeWaived)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd=FROM(state/preferences[*]/deliverySubscriptionOffer/deliveryFeeWaived)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd=FROM(state/preferences[*]/deliverySubscriptionOffer/fuelSurchargeWaived)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty=FROM(state/preferences[*]/deliverySubscriptionOffer/minBasketSize)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd=FROM(state/preferences[*]/autoRenew)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd=FROM(state/preferences[*]/autoEnroll)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd=FROM(state/preferences/deliverySubscriptionOffer/fee/currency)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt=FROM(state/preferences/deliverySubscriptionOffer/fee/amount)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd=FROM(state/preferences/deliverySubscriptionOffer/initialOrderAmountOff/currency)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@DiscountAmt=
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee=
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@CurrencyCd=
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@FeeAmt=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode=FROM(state/preferences[*]/effectiveTimePeriods[*]/type)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt=FROM(state/preferences[*]/effectiveTimePeriods[*]/startDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm=FROM(state/preferences[*]/effectiveTimePeriods[*]/startTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt=FROM(state/preferences[*]/effectiveTimePeriods[*]/endDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm=FROM(state/preferences[*]/effectiveTimePeriods[*]/endTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationUnitDsc=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd=FROM(state/preferences[*]/effectiveTimePeriods[*]/inclusiveInd)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData=
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm=FROM(aggregateType)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId=FROM(state/createClientId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId=FROM(state/createUserId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId=FROM(state/lastUpdateClientId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId=FROM(state/lastUpdateUserId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm=FROM(state/createHostName)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm=FROM(state/lastUpdateHostName)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr=FROM(state/sequenceNumber)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs=TODO(???)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr=FROM(state/aggregateRevision)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr=FROM(state/version)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId=FROM(state/eventId)


```
## LoopAnalyzer
```
[
  {
    "sourcePath": "state/preferences[*]",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences"
  },
  {
    "sourcePath": "state/preferences[*]/effectiveTimePeriods[*]",
    "targetPath": "GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod"
  }
]

```
## XPathAutoAnnotate
```
GetCustomerPreferences=
GetCustomerPreferences/DocumentData=
GetCustomerPreferences/DocumentData/Document=
# GetCustomerPreferences/DocumentData/Document/@ReleaseId=
GetCustomerPreferences/DocumentData/Document/@VersionId=
GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd=???
GetCustomerPreferences/DocumentData/Document/DocumentID=???
GetCustomerPreferences/DocumentData/Document/AlternateDocumentID=???
GetCustomerPreferences/DocumentData/Document/InboundOutboundInd=
GetCustomerPreferences/DocumentData/Document/DocumentNm=ASSIGN('GetCustomerPreference')
GetCustomerPreferences/DocumentData/Document/CreationDt=???
GetCustomerPreferences/DocumentData/Document/Description=ASSIGN('Retail customer’s generic preferences and the subscriptions')
GetCustomerPreferences/DocumentData/Document/SourceApplicationCd=ASSIGN('CFMS')
GetCustomerPreferences/DocumentData/Document/TargetApplicationCd=ASSIGN('EDIS')
# GetCustomerPreferences/DocumentData/Document/Note=
# GetCustomerPreferences/DocumentData/Document/GatewayNm=
# GetCustomerPreferences/DocumentData/Document/SenderId=
# GetCustomerPreferences/DocumentData/Document/ReceiverId=
# GetCustomerPreferences/DocumentData/Document/RoutingSystemNm=
GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd=???
# GetCustomerPreferences/DocumentData/Document/InterchangeDate=
# GetCustomerPreferences/DocumentData/Document/InterchangeTime=
# GetCustomerPreferences/DocumentData/Document/ExternalTargetInd=
# GetCustomerPreferences/DocumentData/Document/MessageSequenceNbr=
# GetCustomerPreferences/DocumentData/Document/ExpectedMessageCnt=
GetCustomerPreferences/DocumentData/Document/DataClassification=
GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel=
GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code=ASSIGN('Internal')
# GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Description=
# GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/ShortDescription=
GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel=
GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code=ASSIGN('Low')
# GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Description=
# GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/ShortDescription=
GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd=ASSIGN('N')
GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd=ASSIGN('N')
GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd=ASSIGN('N')
GetCustomerPreferences/DocumentData/DocumentAction=
GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd=ASSIGN('UPDATE')
GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd=ASSIGN('CHANGE')
GetCustomerPreferences/CustomerPreferencesData=
GetCustomerPreferences/CustomerPreferencesData/CustomerId=ASSIGN($.aggregateId)
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@CodeType=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/@sequenceNbr=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdTxt=
# GetCustomerPreferences/CustomerPreferencesData/CustomerAlternateId/AlternateIdNbr=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences=construct().loop(LOOP_STATE_PREFERENCES).from(state/preferences[*]).var(_preferences).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences._class)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.preferenceId)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.type)
# GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeDsc=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.value)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.categoryCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.subCategoryCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice=construct().loop(LOOP_OPT_CHOICES).from(_state_preference.optChoices[*]).var(_opt_choice).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.optchoices[*].choice)
# GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/OptChoiceInd=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.optchoices[*].reasonCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.optchoices[*].reasonText)
# GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/SelectTs=
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.bannerId)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.preferredInd)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.channel.type)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.platform.type)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod=construct().loop(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).from(state/preferences[*]/effectiveTimePeriods[*]).var(_effectiveTimePeriods).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.type)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.startDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.startTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.endDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.endTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr=???
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationUnitDsc=???
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.inclusiveInd)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions=construct().loop(LOOP_CUSTOMER_SUBSCRIPTION).from($.state.preferences[*]).var(_preference).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId=???
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.subCategoryCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.type)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionDsc=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice=construct().loop(LOOP_SUBSCRIPTION_CHOICES).from(_preference.optChoices[*]).var(_choice).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.optchoices[*].choice)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/OptChoiceInd=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.optchoices[*].reasonCode)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.optchoices[*].reasonText)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/SelectTs=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.deliverySubscriptionOffer.serviceFeeWaived)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.deliverySubscriptionOffer.deliveryFeeWaived)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.deliverySubscriptionOffer.fuelSurchargeWaived)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.deliverySubscriptionOffer.minBasketSize)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.autoRenew)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd=FOR(LOOP_STATE_PREFERENCES).ASSIGN(_preferences.autoEnroll)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd=ASSIGN($.state.preferences.deliverySubscriptionOffer.fee.currency)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt=ASSIGN($.state.preferences.deliverySubscriptionOffer.fee.amount)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd=ASSIGN($.state.preferences.deliverySubscriptionOffer.initialOrderAmountOff.currency)
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@DiscountAmt=
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee=
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@CurrencyCd=
# GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/SignupFee/@FeeAmt=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod=construct().loop(LOOP_SUBSCRIPTION_EFF_TIME_PERIODS).from(_preference/effectiveTimePeriods[*]).var(_period).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.type)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.startDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.startTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.endDate)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.endTime)
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr=???
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationUnitDsc=???
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd=FOR(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).ASSIGN(_effectiveTimePeriods.inclusiveInd)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData=
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm=ASSIGN($.aggregateType)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs=???
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs=???
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId=ASSIGN($.state.createClientId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId=ASSIGN($.state.createUserId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId=ASSIGN($.state.lastUpdateClientId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId=ASSIGN($.state.lastUpdateUserId)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm=ASSIGN($.state.createHostName)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm=ASSIGN($.state.lastUpdateHostName)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr=ASSIGN($.state.sequenceNumber)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs=???
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr=ASSIGN($.state.aggregateRevision)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr=ASSIGN($.state.version)
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId=ASSIGN($.state.eventId)


```
## XPathAssignments
```
GetCustomerPreferences=
GetCustomerPreferences/DocumentData=
GetCustomerPreferences/DocumentData/Document=
GetCustomerPreferences/DocumentData/Document/@VersionId=VERSION_ID
GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd=SYSTEM_ENVIRONMENT_CODE
GetCustomerPreferences/DocumentData/Document/DocumentID='CUSTOMER_PREFERENCES_MANAGEMENT'
GetCustomerPreferences/DocumentData/Document/AlternateDocumentID='IAUC_C02.cfms.aggregate-' || CAST(CURRENT_TIMESTAMP AS CHARACTER FORMAT 'YYYYMMddHHmmssSSSSSS')
GetCustomerPreferences/DocumentData/Document/InboundOutboundInd='Outbound from Albertsons'
GetCustomerPreferences/DocumentData/Document/DocumentNm='GetCustomerPreferences'
GetCustomerPreferences/DocumentData/Document/CreationDt=CURRENT_TIMESTAMP
GetCustomerPreferences/DocumentData/Document/Description='Retail customer''s generic preferences and the subscriptions'
GetCustomerPreferences/DocumentData/Document/SourceApplicationCd='CFMS'
GetCustomerPreferences/DocumentData/Document/TargetApplicationCd='EDIS'
GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd='Y'
GetCustomerPreferences/DocumentData/Document/DataClassification=
GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel=
GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code='Internal'
GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel=
GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code='Low'
GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd='N'
GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd='N'
GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd='N'
GetCustomerPreferences/DocumentData/DocumentAction=
GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd='UPDATE'
GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd='CHANGE'
GetCustomerPreferences/CustomerPreferencesData=
GetCustomerPreferences/CustomerPreferencesData/CustomerId=$.aggregateId
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences=construct().loop(LOOP_STATE_PREFERENCES).from(state/preferences[*]).var(_preferences).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm=_state_preference._class
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd=_state_preference.preferenceId
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd=_state_preference.type
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal=_state_preference.value
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd=_state_preference.categoryCode
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd=_state_preference.subCategoryCode
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice=construct().loop(LOOP_OPT_CHOICES).from(_state_preference.optChoices[*]).var(_opt_choice).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc=_opt_choice.choice
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd=_opt_choice.reasonCode
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc=_opt_choice.reasonText
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd=_state_preference.bannerId
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd=_state_preference.preferredInd
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd=_state_preference.channel.type
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd=_state_preference.platform.type
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod=construct().loop(LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS).from(state/preferences[*]/effectiveTimePeriods[*]).var(_effectiveTimePeriods).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode=null
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt=_pref_eff_time_period.startDate
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm=_pref_eff_time_period.startTime
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt=_pref_eff_time_period.endDate
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm=_pref_eff_time_period.endTime
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr=_pref_eff_time_period.duration
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationUnitDsc=_pref_eff_time_period.duration
GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd=_pref_eff_time_period.inclusiveInd
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions=construct().loop(LOOP_CUSTOMER_SUBSCRIPTION).from($.state.preferences[*]).var(_preference).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId=_preference.value
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd=_preference.subCategoryCode
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd=_preference.type
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice=construct().loop(LOOP_SUBSCRIPTION_CHOICES).from(_preference.optChoices[*]).var(_choice).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc=_choice.choice
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd=_choice.reasonCode
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc=_choice.reasonText
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd=_preference.deliverySubscriptionOffer.serviceFeeWaived
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd=_preference.deliverySubscriptionOffer.deliveryFeeWaived
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd=_preference.deliverySubscriptionOffer.fuelSurchargeWaived
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty=_preference.deliverySubscriptionOffer.minBasketSize
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd=_preference.autoRenew
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd=_preference.autoEnroll
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd=_preference.deliverySubscriptionOffer.fee.currency
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt=_preference.deliverySubscriptionOffer.fee.amount
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff=
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd=_preference.deliverySubscriptionOffer.initialOrderAmountOff.currency
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod=construct().loop(LOOP_SUBSCRIPTION_EFF_TIME_PERIODS).from(_preference/effectiveTimePeriods[*]).var(_period).end()
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode=_period.type
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt=_period.startDate
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm=_period.startTime
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt=_period.endDate
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm=_period.endTime
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr=_period.duration
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationUnitDsc=_period.duration
GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd=_period.inclusiveInd
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData=
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm=$.aggregateType
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs=$.state.createTimestamp
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs=$.state.lastUpdateTimestamp
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId=$.state.createClientId
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId=$.state.createUserId
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId=$.state.lastUpdateClientId
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId=$.state.lastUpdateUserId
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm=$.state.createHostName
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm=$.state.lastUpdateHostName
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr=$.state.sequenceNumber
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs=$.state.timestamp
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr=$.state.aggregateRevision
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr=$.state.version
GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId=$.state.eventId


```
## SampleXml
```
<ns:GetCustomerPreferences xmlns:ns="http://www.openapplications.org/oagis/9" xmlns:def="http://collab.safeway.com/it/architecture/info/default.aspx">
  <ns:DocumentData>
    <ns:Document ReleaseId="string" VersionId="string" SystemEnvironmentCd="string">
      <!--Optional:-->
      <def:DocumentID>documentid</def:DocumentID>
      <!--Optional:-->
      <def:AlternateDocumentID>alternatedocumentid</def:AlternateDocumentID>
      <!--Optional:-->
      <def:InboundOutboundInd>inboundoutboundind</def:InboundOutboundInd>
      <!--Optional:-->
      <def:DocumentNm>documentnm</def:DocumentNm>
      <def:CreationDt>creationdt</def:CreationDt>
      <!--0 to 5 repetitions:-->
      <def:Description>description</def:Description>
      <!--Zero or more repetitions:-->
      <def:SourceApplicationCd>sourceapplicationcd</def:SourceApplicationCd>
      <!--Optional:-->
      <def:TargetApplicationCd>targetapplicationcd</def:TargetApplicationCd>
      <!--Optional:-->
      <def:Note>note</def:Note>
      <!--Optional:-->
      <def:GatewayNm>gatewaynm</def:GatewayNm>
      <!--Optional:-->
      <def:SenderId>senderid</def:SenderId>
      <!--Optional:-->
      <def:ReceiverId>receiverid</def:ReceiverId>
      <!--Optional:-->
      <def:RoutingSystemNm>routingsystemnm</def:RoutingSystemNm>
      <!--Optional:-->
      <def:InternalFileTransferInd>internalfiletransferind</def:InternalFileTransferInd>
      <!--Optional:-->
      <def:InterchangeDate>interchangedate</def:InterchangeDate>
      <!--Optional:-->
      <def:InterchangeTime>interchangetime</def:InterchangeTime>
      <!--Optional:-->
      <def:ExternalTargetInd>externaltargetind</def:ExternalTargetInd>
      <!--Optional:-->
      <def:MessageSequenceNbr>messagesequencenbr</def:MessageSequenceNbr>
      <!--Optional:-->
      <def:ExpectedMessageCnt>expectedmessagecnt</def:ExpectedMessageCnt>
      <!--Optional:-->
      <def:DataClassification>
        <!--Optional:-->
        <def:DataClassificationLevel>
          <!--Optional:-->
          <def:Code>code</def:Code>
          <!--Optional:-->
          <def:Description>description</def:Description>
          <!--Optional:-->
          <def:ShortDescription>shortdescription</def:ShortDescription>
        </def:DataClassificationLevel>
        <!--Optional:-->
        <def:BusinessSensitivityLevel>
          <!--Optional:-->
          <def:Code>code</def:Code>
          <!--Optional:-->
          <def:Description>description</def:Description>
          <!--Optional:-->
          <def:ShortDescription>shortdescription</def:ShortDescription>
        </def:BusinessSensitivityLevel>
        <!--Optional:-->
        <def:PHIdataInd>phidataind</def:PHIdataInd>
        <!--Optional:-->
        <def:PCIdataInd>pcidataind</def:PCIdataInd>
        <!--Optional:-->
        <def:PIIdataInd>piidataind</def:PIIdataInd>
      </def:DataClassification>
    </ns:Document>
    <ns:DocumentAction>
      <def:ActionTypeCd>actiontypecd</def:ActionTypeCd>
      <def:RecordTypeCd>recordtypecd</def:RecordTypeCd>
    </ns:DocumentAction>
  </ns:DocumentData>
  <ns:CustomerPreferencesData>
    <def:CustomerId>customerid</def:CustomerId>
    <!--Zero or more repetitions:-->
    <def:CustomerAlternateId CodeType="string" def:sequenceNbr="201">
      <!--Optional:-->
      <def:AlternateIdTxt>alternateidtxt</def:AlternateIdTxt>
      <!--Optional:-->
      <def:AlternateIdNbr>alternateidnbr</def:AlternateIdNbr>
    </def:CustomerAlternateId>
    <!--Zero or more repetitions:-->
    <def:CustomerPreferences>
      <!--Optional:-->
      <def:PreferenceClassNm>preferenceclassnm</def:PreferenceClassNm>
      <!--Optional:-->
      <def:PreferenceCd>preferencecd</def:PreferenceCd>
      <!--Optional:-->
      <def:PreferenceTypeCd>preferencetypecd</def:PreferenceTypeCd>
      <!--Optional:-->
      <def:PreferenceTypeDsc>preferencetypedsc</def:PreferenceTypeDsc>
      <!--Optional:-->
      <def:PreferenceVal>preferenceval</def:PreferenceVal>
      <!--Optional:-->
      <def:PreferenceCategoryCd>preferencecategorycd</def:PreferenceCategoryCd>
      <!--Optional:-->
      <def:PreferenceSubCategoryCd>preferencesubcategorycd</def:PreferenceSubCategoryCd>
      <!--Zero or more repetitions:-->
      <def:OptChoice>
        <!--Optional:-->
        <def:ChoiceDsc>choicedsc</def:ChoiceDsc>
        <!--Optional:-->
        <def:OptChoiceInd>optchoiceind</def:OptChoiceInd>
        <!--Optional:-->
        <def:ReasonCd>reasoncd</def:ReasonCd>
        <!--Optional:-->
        <def:ReasonDsc>reasondsc</def:ReasonDsc>
        <!--Optional:-->
        <def:SelectTs>selectts</def:SelectTs>
      </def:OptChoice>
      <!--Optional:-->
      <def:BannerCd>bannercd</def:BannerCd>
      <!--Optional:-->
      <def:PreferredInd>preferredind</def:PreferredInd>
      <!--Optional:-->
      <def:ChannelTypCd>channeltypcd</def:ChannelTypCd>
      <!--Optional:-->
      <def:PlatformTypCd>platformtypcd</def:PlatformTypCd>
      <!--Zero or more repetitions:-->
      <def:PreferenceEffectivePeriod def:typeCode="string">
        <!--Optional:-->
        <def:FirstEffectiveDt>firsteffectivedt</def:FirstEffectiveDt>
        <!--Optional:-->
        <def:FirstEffectiveTm>firsteffectivetm</def:FirstEffectiveTm>
        <!--Optional:-->
        <def:LastEffectiveDt>lasteffectivedt</def:LastEffectiveDt>
        <!--Optional:-->
        <def:LastEffectiveTm>lasteffectivetm</def:LastEffectiveTm>
        <!--Optional:-->
        <def:DurationNbr>durationnbr</def:DurationNbr>
        <!--Optional:-->
        <def:DurationUnitDsc>durationunitdsc</def:DurationUnitDsc>
        <!--Optional:-->
        <def:InclusiveInd>inclusiveind</def:InclusiveInd>
      </def:PreferenceEffectivePeriod>
    </def:CustomerPreferences>
    <!--Zero or more repetitions:-->
    <def:CustomerSubscriptions>
      <!--Optional:-->
      <def:SubscriptionId>subscriptionid</def:SubscriptionId>
      <!--Optional:-->
      <def:SubscriptionCd>subscriptioncd</def:SubscriptionCd>
      <!--Optional:-->
      <def:SubscriptionTypeCd>subscriptiontypecd</def:SubscriptionTypeCd>
      <!--Optional:-->
      <def:SubscriptionDsc>subscriptiondsc</def:SubscriptionDsc>
      <!--Optional:-->
      <def:OptChoice>
        <!--Optional:-->
        <def:ChoiceDsc>choicedsc</def:ChoiceDsc>
        <!--Optional:-->
        <def:OptChoiceInd>optchoiceind</def:OptChoiceInd>
        <!--Optional:-->
        <def:ReasonCd>reasoncd</def:ReasonCd>
        <!--Optional:-->
        <def:ReasonDsc>reasondsc</def:ReasonDsc>
        <!--Optional:-->
        <def:SelectTs>selectts</def:SelectTs>
      </def:OptChoice>
      <!--Optional:-->
      <def:DeliverySubscription>
        <!--Optional:-->
        <def:ServiceFeeWaivedInd>servicefeewaivedind</def:ServiceFeeWaivedInd>
        <!--Optional:-->
        <def:DeliveryFeeWaivedInd>deliveryfeewaivedind</def:DeliveryFeeWaivedInd>
        <!--Optional:-->
        <def:FuelSurchargeWaivedInd>fuelsurchargewaivedind</def:FuelSurchargeWaivedInd>
        <!--Optional:-->
        <def:MinimumBasketSizeQty>minimumbasketsizeqty</def:MinimumBasketSizeQty>
        <!--Optional:-->
        <def:AutoRenewInd>autorenewind</def:AutoRenewInd>
        <!--Optional:-->
        <def:AutoEnrollInd>autoenrollind</def:AutoEnrollInd>
        <!--Optional:-->
        <def:Fee CurrencyCd="string" FeeAmt="1000.00"/>
        <!--Optional:-->
        <def:InitialOrderAmountOff CurrencyCd="string" DiscountAmt="1000.00"/>
        <!--Optional:-->
        <def:SignupFee CurrencyCd="string" FeeAmt="1000.00"/>
      </def:DeliverySubscription>
      <!--Zero or more repetitions:-->
      <def:SubscriptionEffectivePeriod def:typeCode="string">
        <!--Optional:-->
        <def:FirstEffectiveDt>firsteffectivedt</def:FirstEffectiveDt>
        <!--Optional:-->
        <def:FirstEffectiveTm>firsteffectivetm</def:FirstEffectiveTm>
        <!--Optional:-->
        <def:LastEffectiveDt>lasteffectivedt</def:LastEffectiveDt>
        <!--Optional:-->
        <def:LastEffectiveTm>lasteffectivetm</def:LastEffectiveTm>
        <!--Optional:-->
        <def:DurationNbr>durationnbr</def:DurationNbr>
        <!--Optional:-->
        <def:DurationUnitDsc>durationunitdsc</def:DurationUnitDsc>
        <!--Optional:-->
        <def:InclusiveInd>inclusiveind</def:InclusiveInd>
      </def:SubscriptionEffectivePeriod>
    </def:CustomerSubscriptions>
    <!--Optional:-->
    <def:SourceAuditData>
      <!--Optional:-->
      <def:SourceNm>sourcenm</def:SourceNm>
      <!--Optional:-->
      <def:CreateTs>createts</def:CreateTs>
      <!--Optional:-->
      <def:LastUpdateTs>lastupdatets</def:LastUpdateTs>
      <!--Optional:-->
      <def:CreateClientId>createclientid</def:CreateClientId>
      <!--Optional:-->
      <def:CreateUserId>createuserid</def:CreateUserId>
      <!--Optional:-->
      <def:LastUpdateClientId>lastupdateclientid</def:LastUpdateClientId>
      <!--Optional:-->
      <def:LastUpdateUserId>lastupdateuserid</def:LastUpdateUserId>
      <!--Optional:-->
      <def:CreateHostNm>createhostnm</def:CreateHostNm>
      <!--Optional:-->
      <def:LastUpdateHostNm>lastupdatehostnm</def:LastUpdateHostNm>
      <!--Optional:-->
      <def:SequenceNbr>sequencenbr</def:SequenceNbr>
      <!--Optional:-->
      <def:AggregateTs>aggregatets</def:AggregateTs>
      <!--Optional:-->
      <def:AggregateRevisionNbr>aggregaterevisionnbr</def:AggregateRevisionNbr>
      <!--Optional:-->
      <def:PayloadVersionNbr>payloadversionnbr</def:PayloadVersionNbr>
      <!--Optional:-->
      <def:EventId>eventid</def:EventId>
    </def:SourceAuditData>
  </ns:CustomerPreferencesData>
</ns:GetCustomerPreferences>

```
## XmlConstructTree
```
	create://GetCustomerPreferences: # GetCustomerPreferences
		create://DocumentData: # GetCustomerPreferences/DocumentData
			create://Document: # GetCustomerPreferences/DocumentData/Document
				assign://VersionId: # GetCustomerPreferences/DocumentData/Document/@VersionId
					assignment: VERSION_ID

				assign://SystemEnvironmentCd: # GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd
					assignment: SYSTEM_ENVIRONMENT_CODE

				assign://DocumentID: # GetCustomerPreferences/DocumentData/Document/DocumentID
					assignment: "'CUSTOMER_PREFERENCES_MANAGEMENT'"

				assign://AlternateDocumentID: # GetCustomerPreferences/DocumentData/Document/AlternateDocumentID
					assignment: "'IAUC_C02.cfms.aggregate-' || CAST(CURRENT_TIMESTAMP AS CHARACTER FORMAT 'YYYYMMddHHmmssSSSSSS')"

				assign://InboundOutboundInd: # GetCustomerPreferences/DocumentData/Document/InboundOutboundInd
					assignment: "'Outbound from Albertsons'"

				assign://DocumentNm: # GetCustomerPreferences/DocumentData/Document/DocumentNm
					assignment: "'GetCustomerPreferences'"

				assign://CreationDt: # GetCustomerPreferences/DocumentData/Document/CreationDt
					assignment: CURRENT_TIMESTAMP

				assign://Description: # GetCustomerPreferences/DocumentData/Document/Description
					assignment: "'Retail customer''s generic preferences and the subscriptions'"

				assign://SourceApplicationCd: # GetCustomerPreferences/DocumentData/Document/SourceApplicationCd
					assignment: "'CFMS'"

				assign://TargetApplicationCd: # GetCustomerPreferences/DocumentData/Document/TargetApplicationCd
					assignment: "'EDIS'"

				assign://InternalFileTransferInd: # GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd
					assignment: "'Y'"

				create://DataClassification: # GetCustomerPreferences/DocumentData/Document/DataClassification
					create://DataClassificationLevel: # GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel
						assign://Code: # GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code
							assignment: "'Internal'"

					create://BusinessSensitivityLevel: # GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel
						assign://Code: # GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code
							assignment: "'Low'"

					assign://PHIdataInd: # GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd
						assignment: "'N'"

					assign://PCIdataInd: # GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd
						assignment: "'N'"

					assign://PIIdataInd: # GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd
						assignment: "'N'"

			create://DocumentAction: # GetCustomerPreferences/DocumentData/DocumentAction
				assign://ActionTypeCd: # GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd
					assignment: "'UPDATE'"

				assign://RecordTypeCd: # GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd
					assignment: "'CHANGE'"

		create://CustomerPreferencesData: # GetCustomerPreferences/CustomerPreferencesData
			create://CustomerId: # GetCustomerPreferences/CustomerPreferencesData/CustomerId
				assignment: $.aggregateId

			construct://CustomerPreferences: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences
				- loop://LOOP_STATE_PREFERENCES:
					assign://PreferenceClassNm: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm
						assignment: _state_preference._class

					assign://PreferenceCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd
						assignment: _state_preference.preferenceId

					assign://PreferenceTypeCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd
						assignment: _state_preference.type

					assign://PreferenceVal: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal
						assignment: _state_preference.value

					assign://PreferenceCategoryCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd
						assignment: _state_preference.categoryCode

					assign://PreferenceSubCategoryCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd
						assignment: _state_preference.subCategoryCode

					construct://OptChoice: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice
						- loop://LOOP_OPT_CHOICES:
					assign://BannerCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd
						assignment: _state_preference.bannerId

					assign://PreferredInd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd
						assignment: _state_preference.preferredInd

					assign://ChannelTypCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd
						assignment: _state_preference.channel.type

					assign://PlatformTypCd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd
						assignment: _state_preference.platform.type

					construct://PreferenceEffectivePeriod: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod
						- loop://LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS:
							assign://typeCode: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/@typeCode
								assignment: '???'

							assign://FirstEffectiveDt: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt
								assignment: _pref_eff_time_period.startDate

							assign://FirstEffectiveTm: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm
								assignment: _pref_eff_time_period.startTime

							assign://LastEffectiveDt: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt
								assignment: _pref_eff_time_period.endDate

							assign://LastEffectiveTm: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm
								assignment: _pref_eff_time_period.endTime

							assign://DurationNbr: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr
								assignment: _pref_eff_time_period.duration

							assign://InclusiveInd: # GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd
								assignment: _pref_eff_time_period.inclusiveInd

			construct://CustomerSubscriptions: # GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions
				- loop://LOOP_CUSTOMER_SUBSCRIPTION:
			create://SourceAuditData: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData
				assign://SourceNm: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm
					assignment: $.aggregateType

				assign://CreateTs: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs
					assignment: $.state.createTimestamp

				assign://LastUpdateTs: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs
					assignment: $.state.lastUpdateTimestamp

				assign://CreateClientId: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId
					assignment: $.state.createClientId

				assign://CreateUserId: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId
					assignment: $.state.createUserId

				assign://LastUpdateClientId: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId
					assignment: $.state.lastUpdateClientId

				assign://LastUpdateUserId: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId
					assignment: $.state.lastUpdateUserId

				assign://CreateHostNm: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm
					assignment: $.state.createHostName

				assign://LastUpdateHostNm: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm
					assignment: $.state.lastUpdateHostName

				assign://SequenceNbr: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr
					assignment: $.state.sequenceNumber

				assign://AggregateTs: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs
					assignment: $.state.timestamp

				assign://AggregateRevisionNbr: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr
					assignment: $.state.aggregateRevision

				assign://PayloadVersionNbr: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr
					assignment: $.state.version

				assign://EventId: # GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId
					assignment: $.state.eventId



```
## ESQL
```
BROKER SCHEMA com.abs.uca.cfms

CREATE COMPUTE MODULE ESED_CFMS_CMM_Transformer_Compute

	-- Declare UDPs
	DECLARE VERSION_ID EXTERNAL CHARACTER '2.0.2.011';
	DECLARE SYSTEM_ENVIRONMENT_CODE EXTERNAL CHARACTER 'PROD';

	-- Declare Namespace
	DECLARE Abs NAMESPACE 'https://collab.safeway.com/it/architecture/info/default.aspx';

	CREATE FUNCTION Main() RETURNS BOOLEAN
	BEGIN
		-- Declare Input Message Root
		DECLARE _inputRootNode REFERENCE TO InputRoot.JSON.Data;

		-- Declare Output Message Root
		CREATE LASTCHILD OF OutputRoot DOMAIN 'XMLNSC';

		DECLARE xmlDocRoot REFERENCE TO OutputRoot.XMLNSC.GetCustomerPreferences;
		CREATE LASTCHILD OF OutputRoot.XMLNSC AS xmlDocRoot TYPE XMLNSC.Folder NAME 'GetCustomerPreferences';
		SET OutputRoot.XMLNSC.GetCustomerPreferences.(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;

		-- GetCustomerPreferences/DocumentData
		DECLARE DocumentData_ REFERENCE TO xmlDocRoot;
		CREATE LASTCHILD OF xmlDocRoot AS DocumentData_ TYPE XMLNSC.Folder NAME 'DocumentData';

			-- GetCustomerPreferences/DocumentData/Document
			DECLARE Document_ REFERENCE TO DocumentData_;
			CREATE LASTCHILD OF DocumentData_ AS Document_ TYPE XMLNSC.Folder NAME 'Document';

				-- GetCustomerPreferences/DocumentData/Document/@VersionId
				SET Document_.(XMLNSC.Attribute)VersionId = VERSION_ID;

				-- GetCustomerPreferences/DocumentData/Document/@SystemEnvironmentCd
				SET Document_.(XMLNSC.Attribute)SystemEnvironmentCd = SYSTEM_ENVIRONMENT_CODE;

				-- GetCustomerPreferences/DocumentData/Document/DocumentID
				SET Document_.(XMLNSC.Field)Abs:DocumentID = 'CUSTOMER_PREFERENCES_MANAGEMENT';

				-- GetCustomerPreferences/DocumentData/Document/AlternateDocumentID
				SET Document_.(XMLNSC.Field)Abs:AlternateDocumentID = 'IAUC_C02.cfms.aggregate-' || CAST(CURRENT_TIMESTAMP AS CHARACTER FORMAT 'YYYYMMddHHmmssSSSSSS');

				-- GetCustomerPreferences/DocumentData/Document/InboundOutboundInd
				SET Document_.(XMLNSC.Field)Abs:InboundOutboundInd = 'Outbound from Albertsons';

				-- GetCustomerPreferences/DocumentData/Document/DocumentNm
				SET Document_.(XMLNSC.Field)Abs:DocumentNm = 'GetCustomerPreferences';

				-- GetCustomerPreferences/DocumentData/Document/CreationDt
				SET Document_.(XMLNSC.Field)Abs:CreationDt = CURRENT_TIMESTAMP;

				-- GetCustomerPreferences/DocumentData/Document/Description
				SET Document_.(XMLNSC.Field)Abs:Description = 'Retail customer''s generic preferences and the subscriptions';

				-- GetCustomerPreferences/DocumentData/Document/SourceApplicationCd
				SET Document_.(XMLNSC.Field)Abs:SourceApplicationCd = 'CFMS';

				-- GetCustomerPreferences/DocumentData/Document/TargetApplicationCd
				SET Document_.(XMLNSC.Field)Abs:TargetApplicationCd = 'EDIS';

				-- GetCustomerPreferences/DocumentData/Document/InternalFileTransferInd
				SET Document_.(XMLNSC.Field)Abs:InternalFileTransferInd = 'Y';

				-- GetCustomerPreferences/DocumentData/Document/DataClassification
				DECLARE DataClassification_ REFERENCE TO Document_;
				CREATE LASTCHILD OF Document_ AS DataClassification_ TYPE XMLNSC.Folder NAME 'Abs:DataClassification';

					-- GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel
					DECLARE DataClassificationLevel_ REFERENCE TO DataClassification_;
					CREATE LASTCHILD OF DataClassification_ AS DataClassificationLevel_ TYPE XMLNSC.Folder NAME 'Abs:DataClassificationLevel';

						-- GetCustomerPreferences/DocumentData/Document/DataClassification/DataClassificationLevel/Code
						SET DataClassificationLevel_.(XMLNSC.Field)Abs:Code = 'Internal';

					-- GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel
					DECLARE BusinessSensitivityLevel_ REFERENCE TO DataClassification_;
					CREATE LASTCHILD OF DataClassification_ AS BusinessSensitivityLevel_ TYPE XMLNSC.Folder NAME 'Abs:BusinessSensitivityLevel';

						-- GetCustomerPreferences/DocumentData/Document/DataClassification/BusinessSensitivityLevel/Code
						SET BusinessSensitivityLevel_.(XMLNSC.Field)Abs:Code = 'Low';

					-- GetCustomerPreferences/DocumentData/Document/DataClassification/PHIdataInd
					SET DataClassification_.(XMLNSC.Field)Abs:PHIdataInd = 'N';

					-- GetCustomerPreferences/DocumentData/Document/DataClassification/PCIdataInd
					SET DataClassification_.(XMLNSC.Field)Abs:PCIdataInd = 'N';

					-- GetCustomerPreferences/DocumentData/Document/DataClassification/PIIdataInd
					SET DataClassification_.(XMLNSC.Field)Abs:PIIdataInd = 'N';

			-- GetCustomerPreferences/DocumentData/DocumentAction
			DECLARE DocumentAction_ REFERENCE TO DocumentData_;
			CREATE LASTCHILD OF DocumentData_ AS DocumentAction_ TYPE XMLNSC.Folder NAME 'DocumentAction';

				-- GetCustomerPreferences/DocumentData/DocumentAction/ActionTypeCd
				SET DocumentAction_.(XMLNSC.Field)Abs:ActionTypeCd = 'UPDATE';

				-- GetCustomerPreferences/DocumentData/DocumentAction/RecordTypeCd
				SET DocumentAction_.(XMLNSC.Field)Abs:RecordTypeCd = 'CHANGE';

		-- GetCustomerPreferences/CustomerPreferencesData
		DECLARE CustomerPreferencesData_ REFERENCE TO xmlDocRoot;
		CREATE LASTCHILD OF xmlDocRoot AS CustomerPreferencesData_ TYPE XMLNSC.Folder NAME 'CustomerPreferencesData';

			-- GetCustomerPreferences/CustomerPreferencesData/CustomerId
			SET CustomerPreferencesData_.(XMLNSC.Field)Abs:CustomerId = _inputRootNode.aggregateId;

			-- LOOP FROM state/preferences[*] TO GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences:
			DECLARE _preferences REFERENCE TO state.preferences.Item;
			LOOP_STATE_PREFERENCES : WHILE LASTMOVE(_preferences) DO

				-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences
				DECLARE CustomerPreferences_ REFERENCE TO CustomerPreferencesData_;
				CREATE LASTCHILD OF CustomerPreferencesData_ AS CustomerPreferences_ TYPE XMLNSC.Folder NAME 'Abs:CustomerPreferences';

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceClassNm
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferenceClassNm = _state_preference._class;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferenceCd = _state_preference.preferenceId;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceTypeCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferenceTypeCd = _state_preference.type;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceVal
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferenceVal = _state_preference.value;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceCategoryCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferenceCategoryCd = _state_preference.categoryCode;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceSubCategoryCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferenceSubCategoryCd = _state_preference.subCategoryCode;

					-- LOOP FROM _state_preference.optChoices[*] TO GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice:
					DECLARE _opt_choice REFERENCE TO _state_preference.optChoices.Item;
					LOOP_OPT_CHOICES : WHILE LASTMOVE(_opt_choice) DO

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice
						DECLARE OptChoice_ REFERENCE TO CustomerPreferences_;
						CREATE LASTCHILD OF CustomerPreferences_ AS OptChoice_ TYPE XMLNSC.Folder NAME 'Abs:OptChoice';

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ChoiceDsc
							SET OptChoice_.(XMLNSC.Field)Abs:ChoiceDsc = _opt_choice.choice;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonCd
							SET OptChoice_.(XMLNSC.Field)Abs:ReasonCd = _opt_choice.reasonCode;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/OptChoice/ReasonDsc
							SET OptChoice_.(XMLNSC.Field)Abs:ReasonDsc = _opt_choice.reasonText;

					MOVE _opt_choice NEXTSIBLING;
					END WHILE LOOP_OPT_CHOICES;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/BannerCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:BannerCd = _state_preference.bannerId;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferredInd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PreferredInd = _state_preference.preferredInd;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/ChannelTypCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:ChannelTypCd = _state_preference.channel.type;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PlatformTypCd
					SET CustomerPreferences_.(XMLNSC.Field)Abs:PlatformTypCd = _state_preference.platform.type;

					-- LOOP FROM state/preferences[*]/effectiveTimePeriods[*] TO GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod:
					DECLARE _effectiveTimePeriods REFERENCE TO state.preferences[*].effectiveTimePeriods.Item;
					LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS : WHILE LASTMOVE(_effectiveTimePeriods) DO

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod
						DECLARE PreferenceEffectivePeriod_ REFERENCE TO CustomerPreferences_;
						CREATE LASTCHILD OF CustomerPreferences_ AS PreferenceEffectivePeriod_ TYPE XMLNSC.Folder NAME 'Abs:PreferenceEffectivePeriod';

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveDt
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:FirstEffectiveDt = _pref_eff_time_period.startDate;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/FirstEffectiveTm
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:FirstEffectiveTm = _pref_eff_time_period.startTime;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveDt
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:LastEffectiveDt = _pref_eff_time_period.endDate;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/LastEffectiveTm
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:LastEffectiveTm = _pref_eff_time_period.endTime;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationNbr
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:DurationNbr = _pref_eff_time_period.duration;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/DurationUnitDsc
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:DurationUnitDsc = _pref_eff_time_period.duration;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerPreferences/PreferenceEffectivePeriod/InclusiveInd
							SET PreferenceEffectivePeriod_.(XMLNSC.Field)Abs:InclusiveInd = _pref_eff_time_period.inclusiveInd;

					MOVE _effectiveTimePeriods NEXTSIBLING;
					END WHILE LOOP_STATE_PREFERENCES_EFFECTIVETIMEPERIODS;

			MOVE _preferences NEXTSIBLING;
			END WHILE LOOP_STATE_PREFERENCES;

			-- LOOP FROM $.state.preferences[*] TO GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions:
			DECLARE _preference REFERENCE TO _inputRootNode.state.preferences.Item;
			LOOP_CUSTOMER_SUBSCRIPTION : WHILE LASTMOVE(_preference) DO

				-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions
				DECLARE CustomerSubscriptions_ REFERENCE TO CustomerPreferencesData_;
				CREATE LASTCHILD OF CustomerPreferencesData_ AS CustomerSubscriptions_ TYPE XMLNSC.Folder NAME 'Abs:CustomerSubscriptions';

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionId
					SET CustomerSubscriptions_.(XMLNSC.Field)Abs:SubscriptionId = _preference.value;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionCd
					SET CustomerSubscriptions_.(XMLNSC.Field)Abs:SubscriptionCd = _preference.subCategoryCode;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionTypeCd
					SET CustomerSubscriptions_.(XMLNSC.Field)Abs:SubscriptionTypeCd = _preference.type;

					-- LOOP FROM _preference.optChoices[*] TO GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice:
					DECLARE _choice REFERENCE TO _preference.optChoices.Item;
					LOOP_SUBSCRIPTION_CHOICES : WHILE LASTMOVE(_choice) DO

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice
						DECLARE OptChoice_1 REFERENCE TO CustomerSubscriptions_;
						CREATE LASTCHILD OF CustomerSubscriptions_ AS OptChoice_1 TYPE XMLNSC.Folder NAME 'Abs:OptChoice';

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ChoiceDsc
							SET OptChoice_1.(XMLNSC.Field)Abs:ChoiceDsc = _choice.choice;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonCd
							SET OptChoice_1.(XMLNSC.Field)Abs:ReasonCd = _choice.reasonCode;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/OptChoice/ReasonDsc
							SET OptChoice_1.(XMLNSC.Field)Abs:ReasonDsc = _choice.reasonText;

					MOVE _choice NEXTSIBLING;
					END WHILE LOOP_SUBSCRIPTION_CHOICES;

					-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription
					DECLARE DeliverySubscription_ REFERENCE TO CustomerSubscriptions_;
					CREATE LASTCHILD OF CustomerSubscriptions_ AS DeliverySubscription_ TYPE XMLNSC.Folder NAME 'Abs:DeliverySubscription';

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/ServiceFeeWaivedInd
						SET DeliverySubscription_.(XMLNSC.Field)Abs:ServiceFeeWaivedInd = _preference.deliverySubscriptionOffer.serviceFeeWaived;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/DeliveryFeeWaivedInd
						SET DeliverySubscription_.(XMLNSC.Field)Abs:DeliveryFeeWaivedInd = _preference.deliverySubscriptionOffer.deliveryFeeWaived;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/FuelSurchargeWaivedInd
						SET DeliverySubscription_.(XMLNSC.Field)Abs:FuelSurchargeWaivedInd = _preference.deliverySubscriptionOffer.fuelSurchargeWaived;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/MinimumBasketSizeQty
						SET DeliverySubscription_.(XMLNSC.Field)Abs:MinimumBasketSizeQty = _preference.deliverySubscriptionOffer.minBasketSize;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoRenewInd
						SET DeliverySubscription_.(XMLNSC.Field)Abs:AutoRenewInd = _preference.autoRenew;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/AutoEnrollInd
						SET DeliverySubscription_.(XMLNSC.Field)Abs:AutoEnrollInd = _preference.autoEnroll;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee
						DECLARE Fee_ REFERENCE TO DeliverySubscription_;
						CREATE LASTCHILD OF DeliverySubscription_ AS Fee_ TYPE XMLNSC.Folder NAME 'Abs:Fee';

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@CurrencyCd
							SET Fee_.(XMLNSC.Attribute)CurrencyCd = _preference.deliverySubscriptionOffer.fee.currency;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/Fee/@FeeAmt
							SET Fee_.(XMLNSC.Attribute)FeeAmt = _preference.deliverySubscriptionOffer.fee.amount;

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff
						DECLARE InitialOrderAmountOff_ REFERENCE TO DeliverySubscription_;
						CREATE LASTCHILD OF DeliverySubscription_ AS InitialOrderAmountOff_ TYPE XMLNSC.Folder NAME 'Abs:InitialOrderAmountOff';

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/DeliverySubscription/InitialOrderAmountOff/@CurrencyCd
							SET InitialOrderAmountOff_.(XMLNSC.Attribute)CurrencyCd = _preference.deliverySubscriptionOffer.initialOrderAmountOff.currency;

					-- LOOP FROM _preference/effectiveTimePeriods[*] TO GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod:
					DECLARE _period REFERENCE TO _preference.effectiveTimePeriods.Item;
					LOOP_SUBSCRIPTION_EFF_TIME_PERIODS : WHILE LASTMOVE(_period) DO

						-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod
						DECLARE SubscriptionEffectivePeriod_ REFERENCE TO CustomerSubscriptions_;
						CREATE LASTCHILD OF CustomerSubscriptions_ AS SubscriptionEffectivePeriod_ TYPE XMLNSC.Folder NAME 'Abs:SubscriptionEffectivePeriod';

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/@typeCode
							SET SubscriptionEffectivePeriod_.(XMLNSC.Attribute)Abs:typeCode = _period.type;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveDt
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:FirstEffectiveDt = _period.startDate;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/FirstEffectiveTm
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:FirstEffectiveTm = _period.startTime;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveDt
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:LastEffectiveDt = _period.endDate;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/LastEffectiveTm
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:LastEffectiveTm = _period.endTime;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationNbr
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:DurationNbr = _period.duration;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/DurationUnitDsc
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:DurationUnitDsc = _period.duration;

							-- GetCustomerPreferences/CustomerPreferencesData/CustomerSubscriptions/SubscriptionEffectivePeriod/InclusiveInd
							SET SubscriptionEffectivePeriod_.(XMLNSC.Field)Abs:InclusiveInd = _period.inclusiveInd;

					MOVE _period NEXTSIBLING;
					END WHILE LOOP_SUBSCRIPTION_EFF_TIME_PERIODS;

			MOVE _preference NEXTSIBLING;
			END WHILE LOOP_CUSTOMER_SUBSCRIPTION;

			-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData
			DECLARE SourceAuditData_ REFERENCE TO CustomerPreferencesData_;
			CREATE LASTCHILD OF CustomerPreferencesData_ AS SourceAuditData_ TYPE XMLNSC.Folder NAME 'Abs:SourceAuditData';

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SourceNm
				SET SourceAuditData_.(XMLNSC.Field)Abs:SourceNm = _inputRootNode.aggregateType;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateTs
				SET SourceAuditData_.(XMLNSC.Field)Abs:CreateTs = _inputRootNode.state.createTimestamp;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateTs
				SET SourceAuditData_.(XMLNSC.Field)Abs:LastUpdateTs = _inputRootNode.state.lastUpdateTimestamp;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateClientId
				SET SourceAuditData_.(XMLNSC.Field)Abs:CreateClientId = _inputRootNode.state.createClientId;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateUserId
				SET SourceAuditData_.(XMLNSC.Field)Abs:CreateUserId = _inputRootNode.state.createUserId;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateClientId
				SET SourceAuditData_.(XMLNSC.Field)Abs:LastUpdateClientId = _inputRootNode.state.lastUpdateClientId;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateUserId
				SET SourceAuditData_.(XMLNSC.Field)Abs:LastUpdateUserId = _inputRootNode.state.lastUpdateUserId;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/CreateHostNm
				SET SourceAuditData_.(XMLNSC.Field)Abs:CreateHostNm = _inputRootNode.state.createHostName;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/LastUpdateHostNm
				SET SourceAuditData_.(XMLNSC.Field)Abs:LastUpdateHostNm = _inputRootNode.state.lastUpdateHostName;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/SequenceNbr
				SET SourceAuditData_.(XMLNSC.Field)Abs:SequenceNbr = _inputRootNode.state.sequenceNumber;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateTs
				SET SourceAuditData_.(XMLNSC.Field)Abs:AggregateTs = _inputRootNode.state.timestamp;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/AggregateRevisionNbr
				SET SourceAuditData_.(XMLNSC.Field)Abs:AggregateRevisionNbr = _inputRootNode.state.aggregateRevision;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/PayloadVersionNbr
				SET SourceAuditData_.(XMLNSC.Field)Abs:PayloadVersionNbr = _inputRootNode.state.version;

				-- GetCustomerPreferences/CustomerPreferencesData/SourceAuditData/EventId
				SET SourceAuditData_.(XMLNSC.Field)Abs:EventId = _inputRootNode.state.eventId;

		RETURN TRUE;
	END;

END MODULE;

```
## XmlMessageRenderer
```
null

```
## OverrideBaseline
```
com.abs.cmnflows.Audit_Validate_Input#APPLICATION_NAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.Audit_Validate_Input#APPLICATION_DESC = CustomerPreferences for UCA
com.abs.cmnflows.Audit_Validate_Input#BO_NAME = CustomerPreferences
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_DESC = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_TYPE = PF
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_INPUT_TYPE = KAFKA
com.abs.cmnflows.Audit_Validate_Input#SOURCE_SYSTEM_NAME = UCA
com.abs.cmnflows.Audit_Validate_Input#AUDIT_REQD = Y
com.abs.cmnflows.Audit_Validate_Input#AUDIT_SRC_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_SRC_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.cmnflows.Audit_Validate_Input#AUDIT_TRGT_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_TRGT_UNQ_ID = CURRENT_TIMESTAMP
com.abs.cmnflows.Audit_Validate_Input#VALIDATION_REQD_SOURCE = N
com.abs.cmnflows.Audit_Validate_Input#VALIDATION_REQD_TARGET = N
com.abs.cmnflows.Audit_Validate_Input#STORE_SRC_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#STORE_TRGT_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_INPUT_NAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.Audit_Validate_Input#queueName = ESEDPR.AUDIT.MSG
com.abs.cmnflows.Audit_Validate_Input#AUDIT_MDL_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_MDL_UNQ_ID = CURRENT_TIMESTAMP
com.abs.cmnflows.Audit_Validate_Input#STORE_MDL_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#Validate.validateMaster = contentAndValue
com.abs.cmnflows.Audit_Validate_Output#queueName = ESEDPR.AUDIT.MSG
com.abs.cmnflows.Audit_Validate_Output#Validate.validateMaster = contentAndValue
com.abs.cmnflows.ExceptionSubFlow#HOSTNAME = IIBPR
com.abs.cmnflows.ExceptionSubFlow#STOREMSG = Y
com.abs.cmnflows.ExceptionSubFlow#APPLICATIONNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.ExceptionSubFlow#BONAME = CustomerPreferences
com.abs.cmnflows.ExceptionSubFlow#INPUTTYPE = KAFKA
com.abs.cmnflows.ExceptionSubFlow#INPUTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.ExceptionSubFlow#REPLAYRULE = Y
com.abs.cmnflows.ExceptionSubFlow#MAXREPLAYCOUNT = 3
com.abs.cmnflows.ExceptionSubFlow#COMPONENTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.ExceptionSubFlow#queueName = ESEDPR.EXCEPTION.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.topicName = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.bootstrapServers = pgv013e8b.safeway.com:9093,pgv013e8d.safeway.com:9093,pgv013e8e.safeway.com:9093,pgv013e8f.safeway.com:9093,pgv013e90.safeway.com:9093,pgv013e91.safeway.com:9093
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.groupId = customer_preferences_management_consumer_group
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.initialOffset = latest
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.enableAutoCommit = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.clientId = customer_preferences_management_consumer
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.useClientIdSuffix = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.connectionTimeout = 15
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.sessionTimeout = 10
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.receiveBatchSize = 1
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.securityProtocol = SSL
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.sslProtocol = TLSv1.2
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.validateMaster = none
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.componentLevel = flow
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.additionalInstances = 0
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.topicName = ESED_C01_CustomerPreferencesManagement
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.bootstrapServers = pgv013e8b.safeway.com:9093,pgv013e8d.safeway.com:9093,pgv013e8e.safeway.com:9093,pgv013e8f.safeway.com:9093,pgv013e90.safeway.com:9093,pgv013e91.safeway.com:9093
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.clientId = customer_preferences_management_producer
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.useClientIdSuffix = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.acks = 1
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.timeout = 60
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.securityProtocol = SSL
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.sslProtocol = TLSv1.2
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.validateMaster = inherit
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ESED_CFMS_CMM_Transformer.SYSTEM_ENVIRONMENT_CODE = PR
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ESED_CFMS_CMM_Transformer.VERSION_ID = 2.0.2.011
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.HOSTNAME = IIBPR
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.STOREMSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.APPLICATIONNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.BONAME = CustomerPreferences
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.INPUTTYPE = KAFKA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.INPUTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.REPLAYRULE = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.MAXREPLAYCOUNT = 3
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.COMPONENTNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.queueName = ESEDPR.EXCEPTION.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.APPLICATION_NAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.APPLICATION_DESC = Transform Json to Canonical Message and Publish to Kafka
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.BO_NAME = CustomerPreferences
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_DESC = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_TYPE = PF
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_INPUT_TYPE = KAFKA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.SOURCE_SYSTEM_NAME = UCA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_REQD = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_SRC_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_SRC_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_TRGT_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_TRGT_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.VALIDATION_REQD_SOURCE = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.VALIDATION_REQD_TARGET = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_SRC_MSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_TRGT_MSG = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_INPUT_NAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.queueName = ESEDPR.AUDIT.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_MDL_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_MDL_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_MDL_MSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Output.queueName = ESEDPR.AUDIT.MSG
com.abs.uca.cfms.ESED_CFMS_CMM_Transformer#SYSTEM_ENVIRONMENT_CODE = PR
com.abs.uca.cfms.ESED_CFMS_CMM_Transformer#VERSION_ID = 2.0.2.011


```
## OverrideDev
```
com.abs.cmnflows.Audit_Validate_Input#APPLICATION_NAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.Audit_Validate_Input#APPLICATION_DESC = CustomerPreferences for UCA
com.abs.cmnflows.Audit_Validate_Input#BO_NAME = CustomerPreferences
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_DESC = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_TYPE = PF
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_INPUT_TYPE = KAFKA
com.abs.cmnflows.Audit_Validate_Input#SOURCE_SYSTEM_NAME = UCA
com.abs.cmnflows.Audit_Validate_Input#AUDIT_REQD = Y
com.abs.cmnflows.Audit_Validate_Input#AUDIT_SRC_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_SRC_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.cmnflows.Audit_Validate_Input#AUDIT_TRGT_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_TRGT_UNQ_ID = CURRENT_TIMESTAMP
com.abs.cmnflows.Audit_Validate_Input#VALIDATION_REQD_SOURCE = N
com.abs.cmnflows.Audit_Validate_Input#VALIDATION_REQD_TARGET = N
com.abs.cmnflows.Audit_Validate_Input#STORE_SRC_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#STORE_TRGT_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_INPUT_NAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.Audit_Validate_Input#queueName = ESEDDV.AUDIT.MSG
com.abs.cmnflows.Audit_Validate_Input#AUDIT_MDL_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_MDL_UNQ_ID = CURRENT_TIMESTAMP
com.abs.cmnflows.Audit_Validate_Input#STORE_MDL_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#Validate.validateMaster = contentAndValue
com.abs.cmnflows.Audit_Validate_Output#queueName = ESEDDV.AUDIT.MSG
com.abs.cmnflows.Audit_Validate_Output#Validate.validateMaster = contentAndValue
com.abs.cmnflows.ExceptionSubFlow#HOSTNAME = IIBDV
com.abs.cmnflows.ExceptionSubFlow#STOREMSG = Y
com.abs.cmnflows.ExceptionSubFlow#APPLICATIONNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.ExceptionSubFlow#BONAME = CustomerPreferences
com.abs.cmnflows.ExceptionSubFlow#INPUTTYPE = KAFKA
com.abs.cmnflows.ExceptionSubFlow#INPUTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.ExceptionSubFlow#REPLAYRULE = Y
com.abs.cmnflows.ExceptionSubFlow#MAXREPLAYCOUNT = 3
com.abs.cmnflows.ExceptionSubFlow#COMPONENTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.ExceptionSubFlow#queueName = ESEDDV.EXCEPTION.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.topicName = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.bootstrapServers = dgv0137c6.safeway.com:9093,dgv0137be.safeway.com:9093,dgv0137c0.safeway.com:9093
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.groupId = customer_preferences_management_consumer_group
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.initialOffset = latest
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.enableAutoCommit = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.clientId = customer_preferences_management_consumer
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.useClientIdSuffix = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.connectionTimeout = 15
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.sessionTimeout = 10
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.receiveBatchSize = 1
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.securityProtocol = SSL
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.sslProtocol = TLSv1.2
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.validateMaster = none
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.componentLevel = flow
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.additionalInstances = 0
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.topicName = ESED_C01_CustomerPreferencesManagement
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.bootstrapServers = dgv0137c6.safeway.com:9093,dgv0137be.safeway.com:9093,dgv0137c0.safeway.com:9093
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.clientId = customer_preferences_management_producer
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.useClientIdSuffix = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.acks = 1
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.timeout = 60
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.securityProtocol = SSL
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.sslProtocol = TLSv1.2
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.validateMaster = inherit
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ESED_CFMS_CMM_Transformer.SYSTEM_ENVIRONMENT_CODE = DV
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ESED_CFMS_CMM_Transformer.VERSION_ID = 2.0.2.011
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.HOSTNAME = IIBDV
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.STOREMSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.APPLICATIONNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.BONAME = CustomerPreferences
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.INPUTTYPE = KAFKA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.INPUTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.REPLAYRULE = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.MAXREPLAYCOUNT = 3
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.COMPONENTNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.queueName = ESEDDV.EXCEPTION.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.APPLICATION_NAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.APPLICATION_DESC = Transform Json to Canonical Message and Publish to Kafka
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.BO_NAME = CustomerPreferences
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_DESC = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_TYPE = PF
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_INPUT_TYPE = KAFKA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.SOURCE_SYSTEM_NAME = UCA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_REQD = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_SRC_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_SRC_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_TRGT_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_TRGT_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.VALIDATION_REQD_SOURCE = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.VALIDATION_REQD_TARGET = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_SRC_MSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_TRGT_MSG = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_INPUT_NAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.queueName = ESEDDV.AUDIT.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_MDL_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_MDL_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_MDL_MSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Output.queueName = ESEDDV.AUDIT.MSG
com.abs.uca.cfms.ESED_CFMS_CMM_Transformer#SYSTEM_ENVIRONMENT_CODE = DV
com.abs.uca.cfms.ESED_CFMS_CMM_Transformer#VERSION_ID = 2.0.2.011


```
## OverrideQA
```
com.abs.cmnflows.Audit_Validate_Input#APPLICATION_NAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.Audit_Validate_Input#APPLICATION_DESC = CustomerPreferences for UCA
com.abs.cmnflows.Audit_Validate_Input#BO_NAME = CustomerPreferences
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_DESC = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_TYPE = PF
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_INPUT_TYPE = KAFKA
com.abs.cmnflows.Audit_Validate_Input#SOURCE_SYSTEM_NAME = UCA
com.abs.cmnflows.Audit_Validate_Input#AUDIT_REQD = Y
com.abs.cmnflows.Audit_Validate_Input#AUDIT_SRC_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_SRC_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.cmnflows.Audit_Validate_Input#AUDIT_TRGT_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_TRGT_UNQ_ID = CURRENT_TIMESTAMP
com.abs.cmnflows.Audit_Validate_Input#VALIDATION_REQD_SOURCE = N
com.abs.cmnflows.Audit_Validate_Input#VALIDATION_REQD_TARGET = N
com.abs.cmnflows.Audit_Validate_Input#STORE_SRC_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#STORE_TRGT_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#COMPONENT_INPUT_NAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.Audit_Validate_Input#queueName = ESEDQA.AUDIT.MSG
com.abs.cmnflows.Audit_Validate_Input#AUDIT_MDL_UNQ_ID = Y
com.abs.cmnflows.Audit_Validate_Input#PATH_MDL_UNQ_ID = CURRENT_TIMESTAMP
com.abs.cmnflows.Audit_Validate_Input#STORE_MDL_MSG = Y
com.abs.cmnflows.Audit_Validate_Input#Validate.validateMaster = contentAndValue
com.abs.cmnflows.Audit_Validate_Output#queueName = ESEDQA.AUDIT.MSG
com.abs.cmnflows.Audit_Validate_Output#Validate.validateMaster = contentAndValue
com.abs.cmnflows.ExceptionSubFlow#HOSTNAME = IIBQA
com.abs.cmnflows.ExceptionSubFlow#STOREMSG = Y
com.abs.cmnflows.ExceptionSubFlow#APPLICATIONNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.cmnflows.ExceptionSubFlow#BONAME = CustomerPreferences
com.abs.cmnflows.ExceptionSubFlow#INPUTTYPE = KAFKA
com.abs.cmnflows.ExceptionSubFlow#INPUTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.ExceptionSubFlow#REPLAYRULE = Y
com.abs.cmnflows.ExceptionSubFlow#MAXREPLAYCOUNT = 3
com.abs.cmnflows.ExceptionSubFlow#COMPONENTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.cmnflows.ExceptionSubFlow#queueName = ESEDQA.EXCEPTION.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.topicName = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.bootstrapServers = qgv013bb3.safeway.com:9093,qgv013bb7.safeway.com:9093,qgv013bb8.safeway.com:9093
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.groupId = customer_preferences_management_consumer_group
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.initialOffset = latest
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.enableAutoCommit = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.clientId = customer_preferences_management_consumer
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.useClientIdSuffix = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.connectionTimeout = 15
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.sessionTimeout = 10
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.receiveBatchSize = 1
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.securityProtocol = SSL
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.sslProtocol = TLSv1.2
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.validateMaster = none
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.componentLevel = flow
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaConsumer.additionalInstances = 0
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.topicName = ESED_C01_CustomerPreferencesManagement
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.bootstrapServers = qgv013bb3.safeway.com:9093,qgv013bb7.safeway.com:9093,qgv013bb8.safeway.com:9093
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.clientId = customer_preferences_management_producer
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.useClientIdSuffix = true
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.acks = 1
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.timeout = 60
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.securityProtocol = SSL
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.sslProtocol = TLSv1.2
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#KafkaProducer.validateMaster = inherit
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ESED_CFMS_CMM_Transformer.SYSTEM_ENVIRONMENT_CODE = QA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ESED_CFMS_CMM_Transformer.VERSION_ID = 2.0.2.011
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.HOSTNAME = IIBQA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.STOREMSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.APPLICATIONNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.BONAME = CustomerPreferences
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.INPUTTYPE = KAFKA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.INPUTNAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.REPLAYRULE = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.MAXREPLAYCOUNT = 3
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.COMPONENTNAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#ExceptionSubFlow.queueName = ESEDQA.EXCEPTION.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.APPLICATION_NAME = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.APPLICATION_DESC = Transform Json to Canonical Message and Publish to Kafka
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.BO_NAME = CustomerPreferences
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_DESC = ESED_CustomerPreferences_UCA_IH_Publisher
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_TYPE = PF
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_INPUT_TYPE = KAFKA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.SOURCE_SYSTEM_NAME = UCA
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_REQD = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_SRC_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_SRC_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_TRGT_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_TRGT_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.VALIDATION_REQD_SOURCE = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.VALIDATION_REQD_TARGET = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_SRC_MSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_TRGT_MSG = N
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.COMPONENT_INPUT_NAME = IAUC_C02_QA_CFMS_AGGREGATE
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.queueName = ESEDQA.AUDIT.MSG
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.AUDIT_MDL_UNQ_ID = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.PATH_MDL_UNQ_ID = InputRoot.JSON.Data.aggregateId
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Input.STORE_MDL_MSG = Y
com.abs.uca.cfms.ESED_CustomerPreferences_UCA_IH_Publisher#Audit_Validate_Output.queueName = ESEDQA.AUDIT.MSG
com.abs.uca.cfms.ESED_CFMS_CMM_Transformer#SYSTEM_ENVIRONMENT_CODE = QA
com.abs.uca.cfms.ESED_CFMS_CMM_Transformer#VERSION_ID = 2.0.2.011


```
## IIBConsole
```
# Read Bar Properties

```
mqsireadbar -b ESED_CustomerPreferences_UCA_IH_Publisher.bar -r

```

# Override Bar Properties

```
mqsiapplybaroverride -k ESED_CustomerPreferences_UCA_IH_Publisher -b ESED_CustomerPreferences_UCA_IH_Publisher.bar -p ESED_CustomerPreferences_UCA_IH_Publisher.BASE.override.properties

```


```
