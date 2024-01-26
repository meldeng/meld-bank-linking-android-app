
# Connect Widget

This project shows how to integrate the Meld API to connect to link accounts.  
https://docs.meld.io/reference/request-connect-token

# Installation
Add it in your root build.gradle at the end of repositories:
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency

```
dependencies {
	        implementation 'com.github.meldeng:meld-bank-linking-android-app:0.0.1'
	}
```

# Setup Demo

You need to set up `BASE_URL` and `API_KEY` values in the appropriate file to run this demo.

`BASE_URL` -  com.example.connectwidgetexample.sdk.env.Environment In both environment PRODUCTION and SANDBOX  
`API_KEY` - MainActivity.kt Replace your api key with "YOUR_API_KEY"

## Usage

Get your `API_KEY` to initialize the MeldSDK in your Application file:

```kotlin  
// Kotlin
val meldConfiguration = MeldConfiguration(
    apiVersion = apiVersion,
    isDebugLogEnabled = true
)
MeldSDK.init(  
	apiKey = "API_KEY",  
	externalCustomerId = "externalCustomerId",  
	environment = Environment.SANDBOX  
    meldConfiguration = meldConfiguration
) 
```  
To Get your connect token to open connect widget:

```kotlin 
// Kotlin  
MeldSdk.getConnectToken( apiKey, listOf(Products.ACCOUNT_DETAILS), object : OnConnectListener { 
	override fun onConnect(response: ConnectResponse?, error: MeldError?) {
		 // your code with connect token 
	 } 
 })
```  

Once you received your connection token call `connectController` method to launch connect widget

```kotlin 
// Kotlin  
 MeldSdk.connectController( connectToken, this@MainActivity )```
```

# Institution connections
**Search institution connections**
Retrieve institution connections filtered by various parameters.
```kotlin
// Kotlin  
MeldSDK.searchInstitutionConnectionsMeldSDK.searchInstitutionConnections(
    customerId = MeldData.connect?.customerId,
    institutionId = MeldData.institutionId,
    statuses = null,
    serviceProviders = null,
    limit = 20,
    position = if (paginationKey == null) null else Position.BEFORE(paginationKey),
    listener = object : OnSearchInstitutionConnectionListener {
        override fun onSearchInstitutionConnection(
            response: SearchInstitutionConnectionResponse?,
            error: MeldError?
        ) {
            //Your code to handle institution connections	       
        }
    })
```

**Refresh accounts belonging to a connection**
Trigger a refresh of all financial accounts for a given institution connection with the latest information from the service provider.  
*Note: The first time a refresh is requested for a Finicity connection that has transactions as an available product will trigger loading historical transactions for the connection. Finicity treats historical transactions as a premium service, so we do not load them by default.*
```kotlin
// Kotlin  
MeldSDK.connectionRefresh(connectionId, products, object : OnRefreshAccountListener {
    override fun onRefreshAccount(response: RefreshAccountResponse?, error: MeldError?) {
        //code to handle connection refresh
    }
})
```
**Delete a connection**
Use this endpoint to delete a connection and all of its financial accounts. This will also delete the connection with the service provider used to make the connection, thus stopping any updates for the connection and its financial accounts.
```kotlin
// Kotlin  
MeldSDK.deleteConnection(connectionId, object : OnDeleteConnectionListener {  
    override fun onDeleteConnection(response: Any?, error: MeldError?) {  
	    // institution connection deleted      
    } 
})
```
**Repair a connection**
creates a new connection with the same data from the previous connection and generates a new connect token. This connect token can be used to invoke a new bank linking flow where the user can enter the service provider's UI to fix the connection
```kotlin
//Kotlin
MeldSDK.repairConnection(connectionId = "connectionId", customerId = "customerId", 
listener = object : OnRepairConnectionListener {
    override fun onRepairConnection(
                response: RepairConnectionResponse?,
                error: MeldError?
            ) {
                //handle connection repair response
            }
        })
```

# Financial accounts

**Search financial accounts**
Use this endpoint to retrieve account information of a specific financial account.
```kotlin
// Kotlin  
MeldSDK.searchFinancialAccount(
	customerId,
	institutionId,
	connectionId,
	limit,
	position,
	listener = object : OnSearchFinancialAccountListener {
	    override fun onSearchFinancialAccount(response: SearchFinancialAccountResponse?, error: MeldError?) {
	        // your code to handle financial accounts list
	    }
	})
```

**Get a financial account**
Use this endpoint to retrieve account information of a specific financial account.
```kotlin
// Kotlin  
MeldSDK.getFinancialAccount(accountId, object : OnFinancialAccountListener {  
    override fun onFinancialAccount(response: FinancialAccount?, error: MeldError?) {  
	    // your code to display financial account details      
    }
})
```
**Create a processor token**
The token is used by the processor to access the information on this account via a service provider. Depending on the service provider and/or processor, this token represents whatever token/code/key is needed to provide access.

```kotlin
// Kotlin  
MeldSDK.createProcessorToken(  
	financialAccountId,  
	processor,  
	serviceProvider,  
	object : OnCreateProcessorTokenListener {  
	    override fun onCreateProcessorToken(response: ProcessorTokenResponse?, error: MeldError?) {  
		// handle newly generated processor token	          
	}  
})
```
# Financial account transactions
**Search financial account transactions**
Use this endpoint to filter financial account transactions by various parameters.
```kotlin
// Kotlin  
MeldSDK.searchAccountTransactions(
	customerId,
	financialAccountId,
	startDate,
	endDate,
	limit,
	position,
	Position.BEFORE(paginationKey),
	listener = object : OnSearchAccountTransactionListener {  
        override fun onSearchAccountTransaction(response: SearchAccountTransactionResponse?, error: MeldError?){  
			// Your code to handle financial transactions
		}
})
```

**Get a financial account transaction**
Use this endpoint to retrieve transaction information of a specific financial account transaction.
```kotlin
// Kotlin  
MeldSDK.getTransaction(transactionId, object : OnAccountTransactionListener {  
    override fun onAccountTransaction(response: FinancialAccountTransaction?, error: MeldError?){  
			// Your code to handle financial account transaction        
    }
})
```

# Institutions
**Search institutions**
```kotlin
// Kotlin  
MeldSDK.searchInstitutions(limit, position, listener = object : OnSearchInstituteListener {  
	override fun onSearchInstitute(response: SearchInstitutionResponse?, error: MeldError?) {  
		//Handle institutions list     
	}
})
```

**Get institution**
```kotlin
// Kotlin  
MeldSDK.getInstitution(institutionId, object : OnInstitutionListener {
    override fun onInstitution(response: Institution?, error: MeldError?) {
        //handle institution response          
    }
})
```
