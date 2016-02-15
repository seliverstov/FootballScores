## FootballScores
### Football scores app from Project 3 "Super Duo!" of [Udacity Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree--nd801)

FootballScores allows you to browse schedule of football matches and displays it in a widget. Application was taken from a functional state and put to a production-ready state by finding and handling error cases, adding accessibility features, localization, adding a widget and a library.

### Install
```
$ git clone https://github.com/seliverstov/FootballScores
$ cd FootballScores
```
Go to `app/src/main/res/values`, open `strings.xml`file in text editor and put your api key to. 

```
<string name="api_key" translatable="false">PUT_YOUR_API_KEY_HERE</string>
```
then return to project's root folder and run
```
$ gradle installDebug
```
###License

The contents of this repository are covered under the [MIT License](http://choosealicense.com/licenses/mit/).

