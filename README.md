# 一个自用的控件库
## 引用方法
* 1. build.gradle中添加maven url
```gradle
allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral(

        )
        maven {
            url "https://raw.githubusercontent.com/damonxiao/ll_widgetlibs/master/"
        }
    }
}
```
* 2. app build.gradle中添加引用
```gradle
implementation 'damon.ll.widgetlibs:llwidgets:1.0.1'
```
* 3. 参考
https://github.com/damonxiao/MyWidgetDemo/blob/master/README.md
