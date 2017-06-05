# portfolio-chart-assignment

Short Descriptions
========
This is a simple project to make a simple android application that can store, read in and display data in a user-friendly manner.

Requirements
============
Read the provided JSON data at startup.

Your app should render portfolios on the same chart(line chart), with time as the x-axis and amount as y-axis. The chart needs to be properly scaled to fit the data. Each portfolio should be denoted in a different colour. There should also be a total portfolio amount that is the sum of all portfolios for each day.
 
Your app should have options to display data in daily/monthly/quarterly format. For monthly data, take the last available day of the month; for quarterly data, take the last available day for the following months: Mar, Jun, Sep, Dec.

UI with a nice looking and user-friendly design is a plus.

It is a plus if you can use unit test to verify tasks.

It is a plus if you can move data into Firebase.

Usage 
===================
Clone this repository and recommend to use Android Studio 2.3.1 to have full supports with Instant Run.
Open the CodingAssignment project by clicking the project's build.gradle file. Waiting gradle to sync up the dependencies as needed. 
Plug your devices to PC and Run the app or you can choose to build the apk with a key store file. 

Dependencies
===================
//====RxJava===//
compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
compile 'io.reactivex.rxjava2:rxjava:2.1.0'

//===Retrofit===//
compile "com.squareup.retrofit2:adapter-rxjava:2.0.2"
compile "com.squareup.retrofit2:converter-gson:2.0.2"
compile "com.squareup.retrofit2:retrofit:2.0.2"
compile "com.squareup.okhttp3:logging-interceptor:3.2.0"

//===MPAndroidChart===//
compile 'com.github.PhilJay:MPAndroidChart:v3.0.2'

//===HelloCharts===//
compile 'com.github.lecho:hellocharts-android:v1.5.8'

//===MaterialDateTimePicker===//
compile 'com.wdullaer:materialdatetimepicker:3.2.2'

//===MaterialDialog===//
compile 'com.afollestad.material-dialogs:commons:0.9.4.5'

//===Smooth Progress Bar ==/
compile 'com.github.castorflex.smoothprogressbar:library:1.1.0'

//===Scalable dp ===/
compile 'com.intuit.sdp:sdp-android:1.0.4'

//====Butterknife====//
compile "com.jakewharton:butterknife:8.5.1"
annotationProcessor "com.jakewharton:butterknife-compiler:8.5.1"
