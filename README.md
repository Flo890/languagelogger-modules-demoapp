# LanguageLogger Modules Demo App

This application demonstrates how the LanguageLogger modules can be used in a separate application, instead of the keyboard app. In the following we explain step by step how we created this application from scratch.
If you didn't yet, we recommend to read our paper "LanguageLogger: A Mobile Keyboard Application for Studying Language Use in Everyday Text Communication in the Wild" first.


## Steps to Use LanguageLogger Modules in a Separate Application

### Prepare a Backend Instance

You have to setup a backend instance to use LanguageLogger. Please follow the steps in the [Backend Repository](https://gitlab.lrz.de/languagelogger/languagelogger-backend)

### Create an Android App and Include the Desired LanguageLogger Modules

1. Create an Android app via Android Studio and ensure that it is working

2. Copy the module folders of the desired LanguageLogger module folders into your project. Place them at the same hierarchy level as the app module of your application:
* [researchime_base](https://gitlab.lrz.de/languagelogger/languagelogger-app/-/tree/master/researchime_base)
* [researchime_contentextraction](https://gitlab.lrz.de/languagelogger/languagelogger-app/-/tree/master/researchime_contentextraction)
* [researchime_contentabstraction](https://gitlab.lrz.de/languagelogger/languagelogger-app/-/tree/master/researchime_contentabstraction)

2. Include them into your project's build by listing them in the settings.gradle file

3. These modules require the following properties to be set in the project's gradle file (not the app module's gradle file!):
Feel free to replace the values with those fitting to your project.
```
project.ext {
    compileSdkVersion = 28
    minSdkVersion = 21
    targetSdkVersion = compileSdkVersion
    supportLibVersion = '27.1.0'
    buildToolsVersion = "27.0.3"
    dbflow_version = "4.0.5"
}
```

4. Add the modules as dependency to your app module in its build.gradle file:
```
    api project(":researchime_contentabstraction")
    api project(':researchime_contentextraction')
```

5. Sync the Gradle project and hit rebuild. The app should work again now.



### Calling the Modules in Code
Please see the classes of this project for details.

1. Create an ApplicationController class, intializing the in-app databases. Mind to register it in the Manifest.

2. See example code for LanguageLogger calling (setup and processing) in class LanuageLoggerCaller. Its usage is demonstrated through buttons and text field in MainActivity.
 
3. Adjust the server url in: res -> values -> strings.xml

4. Optional: Sync. We provide some sample classes for sync. However they are not included in modules, as the used libs are often project dependent. Feel free to copy paste those from this demo project.


## common problems:
Build error: "dependency xyz (e.g. dflow ) does not exist" -> make sure the repository settings in project-level build.gradle fits those of this sampleproject. We added some repositories for dependencies here.



