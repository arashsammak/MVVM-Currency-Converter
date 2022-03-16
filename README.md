# MVVM Currency Converter 💰

With this application, you can convert any currency into any other currency supported by [exchangeratesapi.io](https://exchangeratesapi.io)


## Project Stracture ⚙️

This Application is based on **MVVM** architecture and is written in **Kotlin** 

The project contains two main parts:


**Application**

The application contains these packages:

* `binding_adapter/` : contains binding adapter classes 
* `builder/` : contains builder class for conversion
* `data/` : contains all data and database related classes 
* `di/` : contains dependency injection class 
* `model/` : contains model classes
* `network/` : contains network API interface
* `repository/` : contains bussiness logic
* `ui/` : all UI related classes
* `util/` : contains Constants and PrefrencesHelper classes
* `viewmodel/` : hosts viewmodel class


**Tests**
* `data/db/` automated tests to check DAO funcnality










## Screenshots

Exchange Screen           |  Exchange Message
:-------------------------:|:-------------------------:
<img src="https://smrtmenu.ir/arash/1.png" width="280">  | <img src="https://smrtmenu.ir/arash/2.png" width="280">







## Constants 

* `ACCESS_KEY` : get the Access key from exchangeratesapi.io 
* `FREE_FEE_NUMBER` : free commission for "X" first transactions
* `GIVEAWAY_DEPOSIT` : Initial balance at the beginning

## Running Test 🧪

To run tests, run the following command

```bash
  gradlew app:connectedAndroidTest
```


## Libraries and tools 🛠

- ROOM Database
- Data Binding
- Retrofit
- Coroutine
- Okhttp interceptor
- LiveData
- Dagger Hilt


## Appendix

Contact address

* Email  : arashsammak@gmail.com
