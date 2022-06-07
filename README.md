# Klassenplaner

![klassenplaner-ui](https://user-images.githubusercontent.com/37297474/172475098-4ec74af1-b09a-45fe-9848-d2b062367141.png)

An android app to ease the tedious task of managing homework for you and your class.

------

This project was part of my twelfth class seminar course where I tried playing with different things:

- MVVM using [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) and [Lifecycles](https://developer.android.com/topic/libraries/architecture/lifecycle)
- Navigation with [Androids Navigation Components](https://developer.android.com/topic/libraries/architecture/navigation.html)
- Database management with [Room](https://developer.android.com/topic/libraries/architecture/room)
- Dependency Injection with [Koin](https://github.com/InsertKoinIO/koin)
- [Android Material Design UI compontents](https://github.com/material-components/material-components-android)
- Asynchronous programming with [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
- Networking using [Retrofit](https://square.github.io/retrofit/)

Note that some features do not work properly or are straight up not implemented to begin with (authentication, notifications, …).

## Build instructions

To build the application you have to specify a `KLASSENPLANER_BASE_URL` property in your `gradle.properties` file. You can do this by either editing your local gradle properties (located under `~/.gradle/gradle.properties`, create if the file doesn't exist) or editing the projects properties (but remember not to push if you don't explicitly want to).

To make the deep link work, edit `app/res/navigation/setup_nav_graph.xml` and change `BASE.URL` to your desired URL. 

You can mock a simple back-end for testing using [json-server](https://github.com/typicode/json-server) and the file from [this gist](https://gist.github.com/jonas-haeusler/ceea897c25c1c117eb57c9e3c3402105). Authentication and status codes won't be available, but I was never provided with something better, so this will have to do. If you are running the application inside the android emulator and have json-server running on the same device, you can access the server using `http://10.0.2.2:3000/` as the address and setting the `android:usesCleartextTraffic` manifest attribute to `true` when you are using a device running Android 9 or above.
