# challenge-nubank

This project has the function of solving the issues of nubank's engineering challenge.


## Usage
There is a file called json.json, where all accout and transaction data are saved
I used the data.json library to read the data from the file.
The function that does this reading is in the name space called read_json.

I used the name space core to organize the logic that checks for valid and violated transactions.

My idea was to always check the last operation performed and validate them.
For this reason I created the init-system function which basically takes an empty input vector and an empty output vector.
Which receives a string from the json file.

The process-transaction function makes a reduce in the init-system updating the input and output data.
This function (process-transaction) receives another function called chek-transaction that basically does all the validation of the transaction and returns an output with this data.

I separated in another name space called validations the functions validating each use case. Which was later called in the check-transaction.

In the core namespace it is possible to run the pprint of the input and output. the Input brings the json data and the output brings the data with the validations.

