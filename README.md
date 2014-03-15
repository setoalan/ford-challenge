Ford Challenge
==============

revised (08/20/13)

Important Notes
---------------

* routeMileage in the OpenXC view displays current MPH. Will fix?
* Only in the main tab view do the social activites work. Once the app is near
  completion with the services and database, we will decide what messages to
  display/post once application is nearly finished.
* More styling, if needed, will be done when everything else is finished.

Assessment of Services
----------------------

* Feed & Sync services are not implemented

Debugging

Libraries
---------

These libraries should be imported automatically when importing android project
into Eclipse.

* facebook.jar
* google-play-services_lib.jar
* openxc.jar

External Libraries
-------------

Import these libs externally.

* android-support-v4,jar
* google-play-services.jar
* guava-10.0.1.jar
* jackson-core-2.1.1.jar
* simple-xml-2.7.jar

Contributors
------------

Alan Seto
Omar El-Haik
Demarius Chrite
Alan Yik

Installation
------------

1. Clone from git@code.logicdrop.com:riis/ford-challenge.git
2. Import project into Eclipse (only works in this IDE), while also importing
   Facebook, Google Play Services, and OpenXC Library projects.
3. Add external libs from ford-challenge/libs, make sure they are checked into
   the build path. Sometimes playing around with the order of import may solve
   some issues, if they arise.
4. Launch as Android Application with Android Virtual Device with min API 12
   and preferably a hdpi screen.

Usage (testing)
---------------

Creating a route (save or route)

1. Click on "create route" actionbar item to launch a view to input a start
   and end destination. Once inputted, user can save the route or routes the
   route. If a user saves the route, a dialog appears to name that route and 
   save. If a user routes the route, it should begin the launch the services
   and revert back to the "incident" view.

Reading data from OpenXC

1. Once app launches, import JSON file from ford-challenge/driving.json to
   readable/writable folder.
2. Install FileManager-2.0.2.apk using adb into emulator.
3. Go to drawer menu and go to "Enable OpenXC". Then go to Settings-->
   Data Sources-->Trace file playback file and select "driving.json" to be read
   by the enabler. Make sure it is receiving messages.
4. Go to "create route" action bar item to save a route. Give it name and it
   should revert back to the "routes" tab. Click on the created route to launch
   the history of the route, if used before, or blank, if just created.
5. Then click the "Open XC" actionbar item to launch OpenXC to record data.
   Make sure OpenXC is sending messages, then press start to begin the data.
   Click end to record that data at that instance and it should record it back
   to the created route.
   
Views
-------------

### Action Bar Items

* "Create Route" menu item launches a view to input a route with a start and
  end address. Once entered, user can save route or route the route. Saving
  the route will display it into the "My Routes" tab in the parent "Route"
  tab. Routing the route will start the process of calling services for
  directions from the Google Maps services, and services from Iteris and 
  TrafficLand. Services from Iteris will display incident data from the San 
  Jose Area and services from TrafficLand will display cams in four area of 
  the U.S. These has NO dependency on the inputed route, currently.
* "OpenXC" menu item will launch a view that the user can see input incoming
  from JSON file. This is only viewable when user has clicked on a route item.
  Data will start incoming once OpenXC is enabled from the drawer menu. User 
  can then press "Start" to begin recording data; once the user clicks "Stop",
  the data at that instance will be saved into the database and updated into
  the "my routes" and "recent routes" list view.
* "Directions List" menu item should display when user has routed a path on the
  map. It starts a listview with the driving directions, duration, and
  distance.
* "Save" menu item should appear when user has created a route from
  CreateRouteActivity and is able to save it to the database.

### Traffic Tab

* "Incidents" tab displays a map of the user's current location with a blank
  list view drawer on the bottom. Once the user and created a route, route the
  route, and after the services are done loading, the listview should refresh
  with incident data. The map should also refresh to the inputted route.
* User can click on the incident icon to see info on type of incident and
  relative location of it. The user can also click on the list of incidents to
  see the exact location of the incident on the map as the view will pan to it.
* User can click on the camera icon to see a live camera image of the roads.
  The user can click multiple times to refresh the image.
* NOTE: currently parsing expired images so the image is invisible.

### Route Tab

* "My Routes" tab displays saved routes from user. When a list item is clicked,
  it will launch an expandable list view with data from past recordings,
  displaying average MPG, average MPH, route mileage, total mileage, and total
  fuel used. This is also where the user can initate data recording from
  OpenXC screen or map the route on the map.
* "Recent Routes" tab displays an expandable list view of recent routes
  displaying total miles, average MPG, fuel used, elapsed time, and average
  MPH.

### Statistics Tab (not finished)

* The top view displays the data of average MPG, average MPH, route mileage,
  total mileage, and total fuel used of every route recorded in history.
* The bottom view display the "my routes" view from the previous tab.

### Feed Tab

* Does nothing at the moment

### Connect Tab

* Does nothing at the moment.

### Drawer Menu

* "Walkthough" is a page viewer with images about the app.
* "Enable OpenXC" launches the OpenXC Enabler app, where the user can 
  configure how data should be load into the app and other settings.

### Settings Menu

* Will open text-messaging app by default in emulator unless
  email/twitter/facebook are both installed and logged in
* "Email to a Friend" launches the user's default email intent with message.
* "Post to Facebook" launches the a Facebook view where the user can log-in
  and post the message that is on the screen (not customizable).
* "Tweet This" launches a Twitter view with message.