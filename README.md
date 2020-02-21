# CreditCardEditText [![Build Status](https://app.bitrise.io/app/7001d38c52c3da45/status.svg?token=ecLCofa8SARRROcbUe8ddg&branch=master)](https://app.bitrise.io/app/7001d38c52c3da45) [ ![Download](https://api.bintray.com/packages/uphyca/maven/creditcardedittext/images/download.svg) ](https://bintray.com/uphyca/maven/creditcardedittext/)
クレジットカード番号入力用の EditText

## Set-up

### Download
grab via Gradle:
```groovy
repositories {
    jcenter()
}

dependencies {
    implementation "androidx.appcompat:appcompat:1.1.0"
    implementation "com.uphyca:creditcardedittext:1.3"
}
```


### Usage

```xml
<com.uphyca.creditcardedittext.CreditCardNumberEditText
    android:id="@+id/credit_card_number"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<com.uphyca.creditcardedittext.CreditCardDateEditText
    android:id="@+id/credit_card_date"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```


![CreditCardEditText](CreditCardEditText.gif)

# License

    Copyright 2016 uPhyca, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
