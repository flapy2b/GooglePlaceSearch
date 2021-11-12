# GooglePlaceSearch

GooglePlaceSearch is a Java code who allows to make requests to GoogleMaps and get interest points in an geographical area.

## Setup

To customize the code, there are 3 parameters to change in the GoogleMap.java file:

1. The **centroids** array of the points spaced by 10 kilometers from each others. This points can be generated from a GIS software, like QGis or ArcGIS. The value "10 kilometers" has been chosen from my own experience.

2. The **keywords** array: which activities or commercials you search

3. The **apikey**: your Google Apikey you can generate from your Google Cloud console page: https://console.cloud.google.com

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.
