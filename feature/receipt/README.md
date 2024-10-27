# Download & View Receipt Feature

This feature is responsible for download and view the transaction receipt. unfortunately,
this functionality of this feature is not working. due to configuration issue of `Savings Transaction Receipt` run report API.
Tested in Web, Postman and Swagger, but it's not working.

And this feature could be merge with `Accounting` feature, to download the transaction receipt.


## Receiving Template of the Receipt
``` json
{
    "id": 157,
    "reportName": "Savings Transaction Receipt",
    "reportType": "Pentaho",
    "coreReport": false,
    "useReport": true,
    "reportParameters": [
        {
            "id": 426,
            "parameterId": 1006,
            "parameterName": "transactionId",
            "reportParameterName": "transactionId"
        }
    ],
    "allowedReportTypes": [
        "Table",
        "Chart",
        "SMS"
    ],
    "allowedReportSubTypes": [
        "Bar",
        "Pie"
    ],
    "allowedParameters": [
        {
            "id": 1,
            "parameterName": "startDateSelect"
        },
        {
            "id": 2,
            "parameterName": "endDateSelect"
        },
        {
            "id": 3,
            "parameterName": "obligDateTypeSelect"
        },
        {
            "id": 5,
            "parameterName": "OfficeIdSelectOne"
        },
        {
            "id": 6,
            "parameterName": "loanOfficerIdSelectAll"
        },
        {
            "id": 10,
            "parameterName": "currencyIdSelectAll"
        },
        {
            "id": 20,
            "parameterName": "fundIdSelectAll"
        },
        {
            "id": 25,
            "parameterName": "loanProductIdSelectAll"
        },
        {
            "id": 26,
            "parameterName": "loanPurposeIdSelectAll"
        },
        {
            "id": 100,
            "parameterName": "parTypeSelect"
        },
        {
            "id": 1004,
            "parameterName": "selectAccount"
        },
        {
            "id": 1005,
            "parameterName": "savingsProductIdSelectAll"
        },
        {
            "id": 1006,
            "parameterName": "transactionId"
        },
        {
            "id": 1007,
            "parameterName": "selectCenterId"
        },
        {
            "id": 1008,
            "parameterName": "SelectGLAccountNO"
        },
        {
            "id": 1009,
            "parameterName": "asOnDate"
        },
        {
            "id": 1010,
            "parameterName": "SavingsAccountSubStatus"
        },
        {
            "id": 1011,
            "parameterName": "cycleXSelect"
        },
        {
            "id": 1012,
            "parameterName": "cycleYSelect"
        },
        {
            "id": 1013,
            "parameterName": "fromXSelect"
        },
        {
            "id": 1014,
            "parameterName": "toYSelect"
        },
        {
            "id": 1015,
            "parameterName": "overdueXSelect"
        },
        {
            "id": 1016,
            "parameterName": "overdueYSelect"
        },
        {
            "id": 1017,
            "parameterName": "DefaultLoan"
        },
        {
            "id": 1018,
            "parameterName": "DefaultClient"
        },
        {
            "id": 1019,
            "parameterName": "DefaultGroup"
        },
        {
            "id": 1020,
            "parameterName": "SelectLoanType"
        },
        {
            "id": 1021,
            "parameterName": "DefaultSavings"
        },
        {
            "id": 1022,
            "parameterName": "DefaultSavingsTransactionId"
        }
    ]
}
```

## Error Receiving From API

``` json
{
    "developerMessage": "The server is currently unable to handle the request , please try after some time.",
    "httpStatusCode": "503",
    "defaultUserMessage": "The server is currently unable to handle the request , please try after some time.",
    "userMessageGlobalisationCode": "error.msg.platform.service.unavailable",
    "errors": [
        {
            "developerMessage": "There is no ReportingProcessService registered in the ReportingProcessServiceProvider for this report type: Pentaho",
            "defaultUserMessage": "There is no ReportingProcessService registered in the ReportingProcessServiceProvider for this report type: Pentaho",
            "userMessageGlobalisationCode": "err.msg.report.service.implementation.missing",
            "parameterName": "id",
            "args": [
                {
                    "value": "Pentaho"
                }
            ]
        }
    ]
}
```
