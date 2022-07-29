# Traincut
An application which logs information about the trains you travel into.

From the basic travel data such as origin, destination... to specific things like the serial number
of the train you're travelling into.  
Just keep a record of all the details of your train trips that you may like to analyze in the future.

### This app is still under development
This is just a personal project that I develop in my spare time, so **it isn't functional** (yet).

## How Traincut exports log data

All the information that has been stored in the application user log
can be easily exported from the UI.

The exported information would then be stored in a JSON file which
would follow the structure shown below:

```json
{
    "origin":"",
    "destination":"",
    "departure":"0000-00-00 00:00:00",
    "arrival":"0000-00-00 00:00:00",
    "name":"",
    "trainNumber":0,
    "seat":"",
    "seatClass":"",
    "car":0,
    "cost":0.00,
    "currency":"",
    "realDepart":"0000-00-00 00:00:00",
    "realArrival":"0000-00-00 00:00:00",
    "series":"",
    "comments":""
}
```

### Objects:
- **origin**\*: Name of the station of origin.
- **destination**\*: Name of the station of destination.
- **departure**\*: Scheduled departure date and time *in UTC*.
- **arrival**\*: Scheduled arrival date and time *in UTC*.
- **name**: Train service type *(i.e.: International, Regional...)* or commercial name.
- **trainNumber**: A train number that identifies the itinerary followed by the train.
- **seat**
- **seatClass**
- **car** Car or wagon No.
- **cost**: Price of the train ticket
- **currency**: The currency in which the price is expressed.
- **realDepart**: Real departure date and time *in UTC*.
- **realArrival**: Real arrival date and time *in UTC*.
- **series**: Train unit serial number. Usually formatted in UIC.  
  *To learn more about UIC classification, take a look at the
  [UIC classification of railway coaches](https://en.wikipedia.org/wiki/UIC_classification_of_railway_coaches)*.
- **comments**: User comments about the trip.

The objects containing an asterisk (\*) will *always be present*, while the rest are optional and
depend on whether the user gave the specific information to fill them or not.

**For example:**  
If someone has travelled on a *TGV INOUI* train, with number *6603*, from Paris *(Paris Gare de Lyon)*
to Lyon *(Lyon Part Dieu)*, departing at 06:56h on Saturday, July 30th 2022 CEST (GMT+2), and arriving
at 08:58h although the train was expected to arrive at 08:56h; sitting in second class (in seat 37 which,
for explaining pruporses, we'll think it's on the first car) with a total cost of 96.60€,
and keeping in mind that the train is a TGV Duplex (serial number 716);
the resultant exportation data in JSON would be:

```json
{
    "origin":"Paris Gare de Lyon",
    "destination":"Lyon Part Dieu",
    "departure":"2022-07-30 04:56:00",
    "arrival":"2022-07-30 06:56:00",
    "name":"TGV INOUI",
    "trainNumber":6603,
    "seat":"37",
    "seatClass":"2de classe",
    "car":1,
    "cost":96.6,
    "currency":"Euro",
    "realArrival":"2022-07-30 06:58:00",
    "series":"93 87 0295 716-9"
}
```

## License
This project is licensed under the third version of the GNU General Public License (GPL-3.0).  
[![GNU GPL-3.0](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)
