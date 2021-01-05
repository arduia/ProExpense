<h1 align="center">Pro Expense</h1>
<p align="center">
A simple free finance note to safely record daily expenses
</p>

<p align="center">
  <a href="https://www.gnu.org/licenses/gpl-3.0"><img alt="LICENSE" src="https://img.shields.io/badge/License-GPLv3-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen"/></a> 
  <a href="https://github.com/arduia/ProExpense/releases"><img alt="API" src="https://img.shields.io/github/v/release/arduia/ProExpense"/></a> 
</p> <br>

<table align="center">
       <tr>
          <td><img src="https://github.com/arduia/ProExpense/blob/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="220"></td>
          <td><img src="https://github.com/arduia/ProExpense/blob/master/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="220"></td>
          <td><img src="https://github.com/arduia/ProExpense/blob/master/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="220"></td>
       </tr>
 </table>
 
 **Quote**  
 *"Beware of little expenses; a small leak will sink a great ship"* - Benjamin Franklin

 
## Download
<a href="https://play.google.com/store/apps/details?id=com.arduia.expense">
<img src="https://cdn.jsdelivr.net/gh/steverichey/google-play-badge-svg/img/en_get.svg"  width="170">
</a> 
<a href="https://appgallery.huawei.com/#/app/C102892875">
<img src="https://huaweimobileservices.com/wp-content/uploads/2019/12/AppGallery_DownlaodBadge_ENG.png"  width="170">
</a> 
<a href="https://f-droid.org/en/packages/com.arduia.expense">
<img src="https://gitlab.com/fdroid/artwork/-/raw/master/badge/get-it-on.png"  width="160" >
</a>
<a href="https://www.amazon.com/dp/B08HZFQQ3L">
<img src="https://images-na.ssl-images-amazon.com/images/G/01/mobile-apps/devportal2/res/images/amazon-appstore-badge-english-black.png"  width="160" >
</a>

## Key priorities
**Behaviour**  
<ul>
  <li>Integrity</li> 
  <li>User Data Privacy</li>
  <li>Security</li>
  <li>Usefulness</li>
  <li>Performance</li>
  <li>Simplicity</li> 
  <li>UI/UX</li>
</ul> 

**Development**  
<ul>
  <li>Architecture</li>
  <li>Maintainability</li>
  <li>Scalability</li>
  <li>Code Quality</li> 
</ul>  

## Why Pro Expense?
<ul>
  <li>Free and Open-source</li>  
  <li>High priority on privacy</li>
  <li>Simple and Pretty UI Design</li> 
  <li>Good Performance</li>
  <li>Multi-language support</li> 
  <li>Statistics</li> 
  <li>No Advertisements</li>
</ul> 

## Limitations
<ul>
  <li>Multiple user accounts are not supported yet.</li>  
  <li>Currency exchange and different currency expense items are also no longer exist together.</li>  
  <li>Cloud backup will not be supported.</li>
  <li>Maximum expense enty amount is 999,999,999.99.</li>
</ul>

## Application Architecture
<img src="https://developer.android.com/topic/libraries/architecture/images/final-architecture.png" width="800">  

*Image source from d.android.com*

## Inspiration
Inspired from [Wallet](https://play.google.com/store/apps/details?id=com.droid4you.application.wallet), [My Expense](https://play.google.com/store/apps/details?id=com.nominalista.expenses) apps.

## Permission Use
> android.permission.INTERNET  
> *Feedback submittions, update version status*   

> android.permission.WAKE_LOCK  
> android.permission.READ_PHONE_STATE    
> *Backup, updating version status in app background*  

## Libraries
* [Material Component][material]
* [Android View Binding][view-binding]
* [Paging2][paging]
* [ProgressView][progress-view]
* [Kotlin Coroutines, Flow][coroutines-flow]
* [Navigation][navigation]
* [Dagger Hilt][dagger-hilt]
* [WorkManager][workmanager]
* [MVVM-Core][mvvm-core]
* [Retrofit][retrofit]
* [KTX Libraries][ktx]
* [Leak Canary][leak-canary]
* [Timber][timber]

[material]: https://github.com/material-components/material-components-android
[view-binding]: https://developer.android.com/topic/libraries/view-binding
[paging]: https://developer.android.com/topic/libraries/architecture/paging
[progress-view]: https://github.com/skydoves/ProgressView
[coroutines-flow]: https://kotlinlang.org/docs/reference/coroutines/flow.html
[navigation]: https://developer.android.com/guide/navigation
[dagger-hilt]: https://dagger.dev/
[workmanager]: https://developer.android.com/topic/libraries/architecture/workmanager
[mvvm-core]: https://github.com/arduia/mvvm-core
[ktx]: https://developer.android.com/kotlin/ktx
[leak-canary]: https://github.com/square/leakcanary
[retrofit]: http://square.github.io/retrofit 
[timber]: https://github.com/JakeWharton/timber

*Contribution Welcome*

# License
```xml
      Copyright (C) 2020  Aung Ye Htet
  
      This program is free software: you can redistribute it and/or modify
      it under the terms of the GNU General Public License as published by
      the Free Software Foundation, either version 3 of the License, or
      (at your option) any later version.
  
      This program is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU General Public License for more details.
  
      You should have received a copy of the GNU General Public License
      along with this program.  If not, see <https://www.gnu.org/licenses/>.
```


